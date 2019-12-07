package com.ts.server.ods.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES加密工具类
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
public class AESUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(AESUtils.class);

    private static final String KEY_ALGORITHM = "AES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";//默认的加密算法

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @param key 加密密钥
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String key) {
        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);// 创建密码器

            byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);

            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));// 初始化为加密模式的密码器

            byte[] result = cipher.doFinal(byteContent);// 加密

            return new String(Base64Utils.encode(result));//通过Base64转码返回

        } catch (Exception ex) {
            LOGGER.error("Encrypt fail content={}, throw={}", content, ex.getMessage());
        }

        return null;
    }

    /**
     * AES 解密操作
     *
     * @param content 加密内容
     * @param key 加密key
     * @return 加密后字符串
     */
    public static String decrypt(String content, String key) {

        try {
           // Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
            //实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

            //使用密钥初始化，设置为解密模式
            byte[] raw = key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            //执行操作
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            byte[] bytes = Base64.getDecoder().decode(contentBytes);
            byte[] result = cipher.doFinal(bytes);

            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            LOGGER.error("Decrypt fail content={}, throw={}", content, ex.getMessage());
        }

        return null;
    }

    /**
     * 生成加密秘钥
     *
     * @return 解密字符串
     */
    private static SecretKeySpec getSecretKey(final String key) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;

        try {
            kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            byte[] fullBytes =new byte[16];
            Arrays.fill(fullBytes, (byte)0);
            byte[] keyBytes = key.getBytes();
            System.arraycopy(keyBytes, 0, fullBytes, 0, keyBytes.length);

            //AES 要求密钥长度为 128
            kg.init(128, new SecureRandom(fullBytes));

            //生成一个密钥
            SecretKey secretKey = kg.generateKey();

            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error("SecretKeySpec fail key={}, throw={}", key, ex.getMessage());
        }

        return null;
    }
}
