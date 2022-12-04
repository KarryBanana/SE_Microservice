package com.buaa.song.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @FileName: JwtUtils
 * @author: ProgrammerZhao
 * @Date: 2020/10/24
 * @Description:
 */

public class JwtUtil {

    private static final String defaultSecretKey = "buaasonglab";
    private static String defaultbase64EncodedSecretKey;
    private static final SignatureAlgorithm defaultsignatureAlgorithm = SignatureAlgorithm.HS256;
    private static final long ttlMillis = 180 * 24 * 60 * 60 * 1000;

    static {
        defaultbase64EncodedSecretKey = Base64.encodeBase64String(defaultSecretKey.getBytes());
    }

    public static String encode(String iss, Map<String, Object> claims) {
        //iss签发人，ttlMillis生存时间，claims是指还想要在jwt中存储的一些非隐私信息
        if (claims == null) {
            claims = new HashMap<>();
        }
        long nowMillis = System.currentTimeMillis();

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setId(UUID.randomUUID().toString())//2. 这个是JWT的唯一标识，一般设置成唯一的，这个方法可以生成唯一标识
                .setIssuedAt(new Date(nowMillis))//1. 这个地方就是以毫秒为单位，换算当前系统时间生成的iat
                .setSubject(iss)//3. 签发人，也就是JWT是给谁的（逻辑上一般都是username或者userId）
                .signWith(defaultsignatureAlgorithm, defaultbase64EncodedSecretKey);//这个地方是生成jwt使用的算法和秘钥
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);//4. 过期时间，这个也是使用毫秒生成的，使用当前时间+前面传入的持续时间生成
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    public static Claims decode(String jwtToken) {
        // 得到 DefaultJwtParser
        return Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(defaultbase64EncodedSecretKey)
                // 设置需要解析的 jwt
                .parseClaimsJws(jwtToken)
                .getBody();
    }

    public static boolean isVerify(String jwtToken) {
        Algorithm algorithm = Algorithm.HMAC256(Base64.decodeBase64(defaultbase64EncodedSecretKey));

        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(jwtToken);  // 校验不通过会抛出异常
        //判断合法的标准：1. 头部和荷载部分没有篡改过。2. 没有过期
        return true;
    }

    public static void main(String[] args) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("id",3);
        String encode = encode("test2@test.com", claims);
        System.out.println(encode);

    }
}
