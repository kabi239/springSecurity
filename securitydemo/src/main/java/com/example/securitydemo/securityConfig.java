package com.example.securitydemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
//Tells spring that this provides configuration to tha application context
@EnableWebSecurity
//-This annotation tells spring boot to enable web security features in the application.
//-Also gives us the liberty, where using this particular class we are able
// to customize the security configs.
public class securityConfig {
    @Bean
    //this make sure that the below method is available as bean provider
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> {
            ((AuthorizeHttpRequestsConfigurer.AuthorizedUrl)requests.anyRequest()).authenticated();
        });
        //the above line tells that any coming request gets authenticated by default.
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //Configures session management policy to be stateless,
        // meaning the server will not store session information.


        //http.formLogin(Customizer.withDefaults()); -> this is for form based login
        http.httpBasic(Customizer.withDefaults());
        // implementing http basic auth

        return (SecurityFilterChain)http.build();
        //returning the object of type security filter chain
    }
}
