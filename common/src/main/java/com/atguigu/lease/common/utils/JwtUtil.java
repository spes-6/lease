package com.atguigu.lease.common.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static SecretKey secretKey = Keys.hmacShaKeyFor("shudhcsjidnvdujdhxhftvnajxignwut".getBytes());
    public static String creatToken(Long userId,String username){

        String jwt = Jwts.builder().setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)).
                setSubject("login_User").
                claim("username", username).
                claim("userId", userId).
                signWith(secretKey, SignatureAlgorithm.HS256).
                compact();


        return jwt;
    }
}
