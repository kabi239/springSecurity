package com.example.securitydemo.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.slf4j.Logger;

@Component
public abstract class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class.getName());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       logger.debug("AuthTokenFilter called for URI: {} " , request.getRequestURI());
       try {
           String jwt =parseJwt(request);
           if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
               String username = jwtUtils.getUserNameFromJwtToken(jwt);
               UserDetails userDetails = userDetailsService.loadUserByUsername(username);

               UsernamePasswordAuthenticationToken authentication =
                       new UsernamePasswordAuthenticationToken(userDetails,
                               null,userDetails.getAuthorities());
               logger.debug("Roles from JWT: {} " , userDetails.getAuthorities());
               authentication.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
               SecurityContextHolder.getContext().setAuthentication(authentication);
           }
       }catch (Exception e) {
           logger.error("Cannot set user AuthTokenFilter:{}", e);
       }
       filterChain.doFilter(request, response);
    }
    private  String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtils.gwtJwtFromHeader(request);
        logger.debug("AuthTokenFilter.java: {}" , jwt);
        return jwt;
    }

}
