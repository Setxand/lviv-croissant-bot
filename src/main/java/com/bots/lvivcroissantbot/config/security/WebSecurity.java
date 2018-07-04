package com.bots.lvivcroissantbot.config.security;

import com.bots.lvivcroissantbot.security.JWTAuthorizationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests().anyRequest().permitAll()
                .and()
                .antMatcher("/reqDispatcher/sendMail")
                .authorizeRequests()
                .and().addFilter(new JWTAuthorizationFilter(authenticationManager()));
    }


}
