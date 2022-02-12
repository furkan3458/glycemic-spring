package com.glycemic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.glycemic.filter.AuthTokenFilter;
import com.glycemic.jwt.AuthenticationEntryPointHandler;
import com.glycemic.jwt.NoRedirectStrategy;
import com.glycemic.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		// securedEnabled = true,
		// jsr250Enabled = true,
		prePostEnabled = true)
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter{

	@Autowired
	UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private AuthenticationEntryPointHandler authenticationEntryPointImpl;
	
	private static final RequestMatcher PUBLIC_URLS = new OrRequestMatcher(
			//new AntPathRequestMatcher("auth/login"), 
		    new AntPathRequestMatcher("auth/signup")
	);
	
	private static final RequestMatcher AUTH_URLS = new OrRequestMatcher(
			new AntPathRequestMatcher("user/**"), 
		    new AntPathRequestMatcher("auth/logout"),
		    new AntPathRequestMatcher("auth/login")
	);
	
	@Bean
	AuthTokenFilter authenticationJwtTokenFilter() throws Exception {
		final AuthTokenFilter filter = new AuthTokenFilter(AUTH_URLS);
	    filter.setAuthenticationManager(authenticationManager());
	    filter.setAuthenticationSuccessHandler(successHandler());
	    return filter; 
	}
	
	@Bean
	public SimpleUrlAuthenticationSuccessHandler successHandler() {
		final SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
	    successHandler.setRedirectStrategy(new NoRedirectStrategy());
	    return successHandler;
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
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
	   //http.requiresChannel();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.exceptionHandling().authenticationEntryPoint(authenticationEntryPointImpl);
		http.authorizeHttpRequests().requestMatchers(AUTH_URLS).authenticated().anyRequest().permitAll();
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		
		http.formLogin().disable();
		http.logout().disable();
		http.httpBasic().disable();
		http.csrf().disable();
	 }
}
