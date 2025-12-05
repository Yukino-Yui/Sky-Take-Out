package com.sky.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt有效存货时间(毫秒)
     * @param claims    自定义设置的信息
     * @return
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        // 设置jwt
        JwtBuilder builder = Jwts.builder()
                // 设置Payload，（设置加密信息，设置JWT的过期时间）
                .setClaims(claims)
                .setExpiration(exp)
                // 设置签名使用的签名算法和签名使用的秘钥（身份认证，防篡改）
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8));

        //最后将生成的jwt字符串返回并传给token
        return builder.compact();
    }

    /**
     * Token解密
     *
     * @param secretKey 密钥
     * @param token     加密后的token
     * @return
     */
    public static Claims parseJWT(String secretKey, String token) {
        //先验签名、再检过期，全部通过才会返回 Jwt<Claims> 对象
        Claims claims = Jwts.parser()
                // 把密钥转成字节数组，告诉解析器“正确签名长这样”
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                // 把整串JWT拆成头.载荷.签名三段，并验签 + 过期时间等标准校验，通过就得到一个 Jwt<Claims>
                .parseClaimsJws(token).getBody();
        return claims;
    }

}
