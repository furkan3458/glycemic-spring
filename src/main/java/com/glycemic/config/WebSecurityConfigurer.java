package com.glycemic.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.glycemic.filter.AuthTokenFilter;
import com.glycemic.jwt.AuthenticationEntryPointHandler;
import com.glycemic.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		securedEnabled = true,
		jsr250Enabled = true,
		prePostEnabled = true)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter{

	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private AuthenticationEntryPointHandler authenticationEntryPointImpl;
	
	private static final String[] AUTH_URLS = {
			"/user/**", 
		    "/auth/logout",
		    "/food/insert",
		    "/food/delete",
		    "/food/update",
		    "/food/list/user",
		    "/search/search/user",
		    "/category/insert",
		    "/category/update",
		    "/category/delete",
	};
	
	@Bean
	AuthTokenFilter authenticationJwtTokenFilter() throws Exception {
	    return new AuthTokenFilter(AUTH_URLS);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}
	
	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
	
	@Override
    public void configure(WebSecurity web) {
  
    }
	
	@Override
	 protected void configure(HttpSecurity http) throws Exception {
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPointImpl);
		http.authorizeHttpRequests().antMatchers(AUTH_URLS).authenticated().anyRequest().permitAll();
		http.authorizeRequests().regexMatchers("\\/food\\/get\\?name=[\\s\\S]\\w{1,}\\w[&]{1}status=[\\s\\S]\\w{1,}").hasAnyRole("USER","ADMIN");
		http.addFilterBefore(authenticationJwtTokenFilter(),UsernamePasswordAuthenticationFilter.class);

		http.cors();
		http.formLogin().disable();
		http.logout().disable();
		http.httpBasic().disable();
		http.csrf().disable();
	 }
}
