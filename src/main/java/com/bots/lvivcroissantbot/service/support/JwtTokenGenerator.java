package com.bots.lvivcroissantbot.service.support;

import com.bots.lvivcroissantbot.security.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtTokenGenerator {

    public static String generate(String sub) {
        return Jwts.builder()
                .setSubject(sub)
                .signWith(SignatureAlgorithm.HS512, SecurityConstants.SECRET.getBytes())
                .compact();
    }
}
