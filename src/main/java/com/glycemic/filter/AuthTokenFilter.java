
package com.glycemic.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.glycemic.handler.JwtExceptionHandler;
import com.glycemic.jwt.JwtUtils;
import com.glycemic.model.JwtSession;
import com.glycemic.model.Users;
import com.glycemic.repository.JwtSessionRepository;
import com.glycemic.security.UserDetailsServiceImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthTokenFilter extends OncePerRequestFilter {
	
	@Autowired
	private UserDetailsServiceImpl userDetailsService;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private JwtExceptionHandler jwtException;
	
	@Autowired
	private JwtSessionRepository jwtRepo;
	
	private List<String> AUTH_URLS;
	
	private static final String[] REGEX_URLS = {
			"\\/food\\/get\\?name=[\\s\\S]\\w{1,}\\w[&]{1}status=[\\s\\S]\\w{1,}"
	};
	
	public AuthTokenFilter(String... AUTH_URLS) {
		this.AUTH_URLS = Arrays.asList(AUTH_URLS);
	}
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getServletPath();
		String fullpath = path+"?"+request.getQueryString();
		boolean upper = false;
		for(String s : AUTH_URLS) {
			if(s.contains(path) || s.equals("/**") || s.equals(path))
				upper = true;
		}
		
		for(String s : REGEX_URLS) {
			if(fullpath.matches(s))
				upper = true;
		}
		
		return !upper;
	}
	

	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String jwt = parseJwt(request);
			if (jwt != null && !jwt.isEmpty()) {
				jwtUtils.validateToken(jwt);
				Optional<JwtSession> jwtOpt = jwtRepo.findByJwttoken(jwt);
				
				if(jwtOpt.isPresent()) {
					JwtSession session = jwtOpt.get();
					String fingerprint = request.getHeader("Fingerprint");
					Long id = jwtUtils.getIdFromJwtToken(jwt);
					String email = jwtUtils.getEmailFromJwtToken(jwt);
					Users user = session.getUsers();
					
					if(!session.getFingerPrint().equals(fingerprint)) {
						log.info("Request is coming from different client except that logged client.{from: "+fingerprint+", logged: "+session.getFingerPrint()+"}");
					}
					
					if(user.getId() == id && user.getEmail().equals(email)) {
						UserDetails userDetails = userDetailsService.loadUserByUsername(email);
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

						SecurityContextHolder.getContext().setAuthentication(authentication);
					}		
				}
			}
		} catch (SignatureException | MalformedJwtException | UnsupportedJwtException | ExpiredJwtException e) {
			jwtException.jwtException(request, response, e);
			return;
		}	
		
		filterChain.doFilter(request, response);
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}

		return null;
	}
}