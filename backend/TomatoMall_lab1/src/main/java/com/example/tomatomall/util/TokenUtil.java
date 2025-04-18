package com.example.tomatomall.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.example.tomatomall.po.Account;
import com.example.tomatomall.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.NoSuchElementException;


@Component
public class TokenUtil {
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000;
    private static final String SALT = "someRandomSalt";  // 盐值固定
    private static final int ITERATION_COUNT = 10000;  // 迭代次数
    private static final int KEY_LENGTH = 256;  // 密钥长度

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 生成JWT Token
    public String getToken(Account user) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);

        // 使用PBKDF2算法生成密钥
        byte[] salt = SALT.getBytes();  // 盐值
        KeySpec spec = new PBEKeySpec(user.getPassword().toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
        SecretKeyFactory factory = null;
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] secretKey = null;
        try {
            secretKey = factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

        // 生成JWT
        return JWT.create()
                .withAudience(String.valueOf(user.getId()))
                .withExpiresAt(date)
                .sign(Algorithm.HMAC256(secretKey));
    }

    // 验证JWT Token
    public boolean verifyToken(String token) {
        try {
            // 解码Token获取用户ID
            Integer userId = Integer.parseInt(JWT.decode(token).getAudience().get(0));

            // 查询用户信息
            Account user = accountRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 使用相同的密码生成密钥
            byte[] salt = SALT.getBytes();  // 盐值
            KeySpec spec = new PBEKeySpec(user.getPassword().toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] secretKey = factory.generateSecret(spec).getEncoded();

            // 使用密钥验证Token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secretKey)).build();
            jwtVerifier.verify(token);

            return true;
        } catch (JWTDecodeException e) {
            System.err.println("Token解码失败: " + e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            System.err.println("用户ID格式错误: " + e.getMessage());
            return false;
        } catch (NoSuchElementException e) {
            System.err.println("用户不存在: " + e.getMessage());
            return false;
        } catch (JWTVerificationException e) {
            System.err.println("Token验证失败: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("其他错误: " + e.getClass().getName() + " - " + e.getMessage());
            return false;
        }
    }

    // 获取用户信息
    public Account getUser(String token){
        Integer userId = Integer.parseInt(JWT.decode(token).getAudience().get(0));
        return accountRepository.findById(userId).orElse(null);
    }
}
