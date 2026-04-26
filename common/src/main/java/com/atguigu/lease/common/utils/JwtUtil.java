package com.atguigu.lease.common.utils;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {
    private static SecretKey secretKey = Keys.hmacShaKeyFor("shudhcsjidnvdujdhxhftvnajxignwut".getBytes());
    public static String creatToken(Long userId,String username){

        String jwt = Jwts.builder().setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24*365L)).
                setSubject("login_User").
                claim("username", username).
                claim("userId", userId).
                signWith(secretKey, SignatureAlgorithm.HS256).
                compact();


        return jwt;
    }
    //解析token,
    public static Claims parseToken(String token){
        if(token==null){
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }
        try{
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return claimsJws.getBody();
        }
        catch(ExpiredJwtException e){
            throw new LeaseException(ResultCodeEnum.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new LeaseException(ResultCodeEnum.TOKEN_INVALID);
        }
    }
    public static void main(String[] args){
        System.out.println(creatToken(2L,"user"));
    }
}
