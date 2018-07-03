package com.bots.lvivcroissantbot.security;

import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static com.bots.lvivcroissantbot.constantenum.security.SecurityConstants.HEADER_STRING;
import static com.bots.lvivcroissantbot.constantenum.security.SecurityConstants.SECRET;
import static com.bots.lvivcroissantbot.constantenum.security.SecurityConstants.TOKEN_PREFIX;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING.getValue());

        if (header == null || !header.startsWith(TOKEN_PREFIX.getValue())) {
            chain.doFilter(req, res);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING.getValue());
        if (token != null) {
            String user = parseJwtToken(token);
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user,null,new ArrayList<>());
            }
            return null;
        }
        return null;
    }

    private String parseJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET.getValue().getBytes())
                .parseClaimsJws(token.replace(TOKEN_PREFIX.getValue(), ""))
                .getBody()
                .getSubject();
    }
}
