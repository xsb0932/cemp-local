package com.landleaf.comm.util.encrypt;

import cn.hutool.core.codec.Base64;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * 用于rsa加解密
 */
public class RsaUtil {
    public static final String CHARSET = "utf-8";
    public static final String ENCRYPTION_ALGORITHM = "RSA";

    /**
     * 生成密钥对
     *
     * @return 密钥对
     * @throws Exception
     */
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ENCRYPTION_ALGORITHM);
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

    public static void main(String[] args) throws Exception {
        KeyPair keyPair = getKeyPair();
        System.out.println(getPublicKey(keyPair));
        System.out.println(getPrivateKey(keyPair));
    }

    /**
     * 获取公钥(Base64编码)
     *
     * @param keyPair 密钥对
     * @return 公钥的字符串形式
     */
    public static String getPublicKey(KeyPair keyPair) {
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return byte2Base64(bytes);
    }

    /**
     * 获取私钥(Base64编码)
     *
     * @param keyPair 密钥对
     * @return 私钥的字符串形式
     */
    public static String getPrivateKey(KeyPair keyPair) {
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return byte2Base64(bytes);
    }

    /**
     * 将Base64编码后的公钥转换成PublicKey对象
     *
     * @param pubStr 公钥的字符串
     * @return 公钥
     * @throws Exception
     */
    public static PublicKey string2PublicKey(String pubStr) throws Exception {
        byte[] keyBytes = base642Byte(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 将Base64编码后的私钥转换成PrivateKey对象
     *
     * @param priStr 私钥的字符串
     * @throws Exception
     * @return私钥
     */
    public static PrivateKey string2PrivateKey(String priStr) throws Exception {
        byte[] keyBytes = base642Byte(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ENCRYPTION_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 使用公钥加密
     *
     * @param content   加密内容
     * @param publicKey 公钥
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String publicEncrypt(String content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(content.getBytes(CHARSET));
        return byte2Base64(bytes);
    }

    /**
     * 使用公钥解密
     *
     * @param content   解密内容
     * @param publicKey 公钥
     * @return 解密后的字符串
     * @throws Exception
     */
    public static String publicDecrypt(String content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] bytes = cipher.doFinal(base642Byte(content));
        return new String(bytes, CHARSET);
    }

    /**
     * 使用私钥加密
     *
     * @param content    解密内容
     * @param privateKey 私钥
     * @return 加密后的字符串
     * @throws Exception
     */
    public static String privateEncrypt(String content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] bytes = cipher.doFinal(content.getBytes(CHARSET));
        return byte2Base64(bytes);
    }

    /**
     * 使用私钥解密
     *
     * @param content    解密内容
     * @param privateKey 私钥
     * @return 解密后的字符串
     * @throws Exception
     */
    public static String privateDecrypt(String content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bytes = cipher.doFinal(base642Byte(content));
        return new String(bytes, CHARSET);
    }

    /**
     * 将字节数组base64成字符串
     *
     * @param bytes 字节数组
     * @return 对应的字符串
     */
    public static String byte2Base64(byte[] bytes) {
        return Base64.encode(bytes);
    }

    /**
     * 将base64字符串转为对应的字节数组
     *
     * @param base64Key base64字符串
     * @return 对应的byte
     * @throws IOException
     */
    public static byte[] base642Byte(String base64Key) throws IOException {
        return Base64.decode(base64Key);
    }

    /**
     * 将rsa的key保存至文件
     *
     * @param key      rsa的key
     * @param fileName 文件名（路径+名称）
     */
    public static void persistentKey2File(String key, String fileName) throws IOException {
        File rsaFile = new File(fileName);
        String path = rsaFile.getParent();
        rsaFile.getParentFile().mkdirs();
        BufferedWriter writer = new BufferedWriter(new FileWriter(rsaFile));
        writer.write(key);
        writer.close();
    }

    /**
     * 从file读取key
     *
     * @param fileName
     * @return
     */
    public static String loadKeyFromFile(String fileName) throws IOException {
        File rsaFile = new File(fileName);
        BufferedReader reader = new BufferedReader(new FileReader(rsaFile));
        StringBuilder builder = new StringBuilder();
        reader.lines().forEach(i -> builder.append(i));
        reader.close();
        return builder.toString();
    }
}
