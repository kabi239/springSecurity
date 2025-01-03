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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import org.slf4j.Logger;

@Component //Indicates that this class is a Spring-managed component, allowing it to be automatically detected and instantiated during component scanning.
public abstract class AuthTokenFilter extends OncePerRequestFilter { //This class extends the OncePerRequestFilter, ensuring that the filter logic is applied
    // only once per HTTP request. and  Declares the class as an abstract class, meaning it cannot be instantiated directly
    @Autowired //Injects the JwtUtils dependency automatically
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService; //Injects the UserDetailsService, a Spring Security interface used to load user-specific data (e.g., fetching user details based on the username).

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class.getName());
    // declaring a logger to log messages for debugging or tracking

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       //FilterChain filterChain: Provides a mechanism to pass the request/response to the next entity in the filter chain.

        logger.debug("AuthTokenFilter called for URI: {} " , request.getRequestURI());//Logs the URI of the incoming request for debugging purposes.
        try {
           String jwt =parseJwt(request); // Extracting the JWT from the request header
           if (jwt != null && jwtUtils.validateJwtToken(jwt)) { //Validates the token to check its integrity and authenticity.
               String username = jwtUtils.getUserNameFromJwtToken(jwt); // Extracting username from the JWT
               UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Load user details based on the extracted username

               UsernamePasswordAuthenticationToken authentication =
                       new UsernamePasswordAuthenticationToken(userDetails,
                               null,userDetails.getAuthorities());
               // creating an authentication object with the user's details, setting the credentials to null since the JWT is already validated above.
               logger.debug("Roles from JWT: {} " , userDetails.getAuthorities()); //Logs the roles or authorities extracted from the user details.
               authentication.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
               // Attaches additional details about the current request (e.g., remote IP) to the authentication object.
               SecurityContextHolder.getContext().setAuthentication(authentication);
               //Sets the authentication object into the SecurityContext, establishing the user's identity for the current request.
           }
       }catch (Exception e) {
           logger.error("Cannot set user AuthTokenFilter:{}", e);
       }
       filterChain.doFilter(request, response);
        //Passes the request and response objects to the next entity in the filter chain.
    }
    private  String parseJwt(HttpServletRequest request) {
        String jwt = jwtUtils.gwtJwtFromHeader(request);
        //Extracts the JWT from the request header using the jwtUtils.getJwtFromHeader method.
        logger.debug("AuthTokenFilter.java: {}" , jwt);
        return jwt;
        //Returns the token.
    }

}

//This class ensures secure handling of incoming HTTP requests by validating JWTs
// and establishing user identity in the application's security context.