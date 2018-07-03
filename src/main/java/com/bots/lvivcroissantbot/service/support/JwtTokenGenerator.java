package com.bots.lvivcroissantbot.service.support;

import com.bots.lvivcroissantbot.constantenum.security.SecurityConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

import static com.bots.lvivcroissantbot.constantenum.security.SecurityConstants.SECRET;

public class JwtTokenGenerator {

    public static String generate(String sub){
        return SecurityConstants.TOKEN_PREFIX.getValue()+Jwts.builder()
                .setSubject(sub)
                .signWith(SignatureAlgorithm.HS512, SECRET.getValue().getBytes())
                .compact();
    }
}
