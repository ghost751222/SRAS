package com.consiliuminc.sras.componets;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Random;


@Component
public class Pbkdf2Sha256 {

    private static final Logger logger = LoggerFactory.getLogger(Pbkdf2Sha256.class);


    public static final int SALT_BYTE_SIZE = 12;


    public static final int HASH_BIT_SIZE = 64 * 4;

    private static final Integer DEFAULT_ITERATIONS = 36000;


    private static final String algorithm = "pbkdf2_sha256";


    public static String getEncodedHash(String password, String salt, int iterations) {
        // Returns only the last part of whole encoded password
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Could NOT retrieve PBKDF2WithHmacSHA256 algorithm", e);
        }
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(Charset.forName("UTF-8")), iterations, HASH_BIT_SIZE);
        SecretKey secret = null;
        try {
            secret = keyFactory.generateSecret(keySpec);
        } catch (InvalidKeySpecException e) {
            logger.error("Could NOT generate secret key", e);
        }

        //使用Base64進行轉碼密文
//        byte[] rawHash = secret.getEncoded();
//        byte[] hashBase64 = Base64.getEncoder().encode(rawHash);
//        return new String(hashBase64);

        //使用十六進位制密文
        //return toHex(secret.getEncoded());
        return toBase64(secret.getEncoded());
    }


    private static byte[] fromHex(String hex) {
        byte[] binary = new byte[hex.length() / 2];
        for (int i = 0; i < binary.length; i++) {
            binary[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return binary;
    }


    private static String toHex(byte[] array) {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0)
            return String.format("%0" + paddingLength + "d", 0) + hex;
        else
            return hex;
    }

    private static String toBase64(byte[] data){
        byte[] encoded= Base64.getEncoder().encode(data);
        return new String(encoded, StandardCharsets.UTF_8);
    }


    public static String getsalt() {
        //鹽值使用ASCII表的數字加大小寫字母組成
        int length = SALT_BYTE_SIZE;
        Random rand = new Random();
        char[] rs = new char[length];
        for (int i = 0; i < length; i++) {
            int t = rand.nextInt(3);
            if (t == 0) {
                rs[i] = (char) (rand.nextInt(10) + 48);
            } else if (t == 1) {
                rs[i] = (char) (rand.nextInt(26) + 65);
            } else {
                rs[i] = (char) (rand.nextInt(26) + 97);
            }
        }
        return new String(rs);
    }


    public static String encode(String password) {
        return encode(password, getsalt());
    }


    public static String encode(String password, int iterations) {
        return encode(password, getsalt(), iterations);
    }


    public static String encode(String password, String salt) {
        return encode(password, salt, DEFAULT_ITERATIONS);
    }


    public static String encode(String password, String salt, int iterations) {
        // returns hashed password, along with algorithm, number of iterations and salt
        String hash = getEncodedHash(password, salt, iterations);
        return String.format("%s$%d$%s$%s", algorithm, iterations, salt, hash);
    }


    public static boolean verification(String password, String hashedPassword) {
        //hashedPassword = 演算法名稱+迭代次數+鹽值+密文;
        String[] parts = hashedPassword.split("\\$");
        if (parts.length != 4) {
            return false;
        }
        //解析得到迭代次數和鹽值進行鹽值
        Integer iterations = Integer.parseInt(parts[1]);
        String salt = parts[2];
        String hash = encode(password, salt, iterations);
        return hash.equals(hashedPassword);
    }
}
