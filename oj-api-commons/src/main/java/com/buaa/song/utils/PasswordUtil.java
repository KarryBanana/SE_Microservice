package com.buaa.song.utils;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

import java.util.Random;

/**
 * @FileName: PasswordUtil
 * @author: ProgrammerZhao
 * @Date: 2020/10/27
 * @Description:
 */

public class PasswordUtil {

    public static String generate(String rawPassword) {
        String algorithm = "sha1";
        String salt = generateSalt(8);
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, salt);
        if (rawPassword == null) {
            return null;
        }
        int iterations = 1;
        String hash = rawPassword;
        try {
            byte[] byteResult = hmacUtils.hmac(hash);
            hash = toHex(byteResult);
            return (algorithm + '$' + salt + '$' + iterations + '$' + hash).toLowerCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Boolean verify(String password, String passwordInSQL) {
        if(password==null||passwordInSQL==null){
            return false;
        }
        String[] split = passwordInSQL.split("\\$");
//        说明在数据库里没找到相应用户的密码
        if (split.length < 4) {
            return false;
        }
        return verify(password, split[1], split[3]);
    }

    private static String generateSalt(int len) {
        try {
            StringBuffer result = new StringBuffer();
            for (int i = 0; i < len; i++) {
                result.append(Integer.toHexString(new Random().nextInt(16)));
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    private static Boolean verify(String password, String salt, String expected) {
        if(password==null||expected==null){
            return false;
        }
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, salt);
        byte[] byteResult = hmacUtils.hmac(password);
        String result = toHex(byteResult);
        return expected.equalsIgnoreCase(result);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (Byte b : bytes) {
            hex.append(String.format("%02X", b.intValue() & 0xFF));
        }
        return hex.toString();
    }

    public static void main(String[] args) {
        try {
            System.out.println(generate("000000"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}