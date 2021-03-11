package com.cgp.common.utils;

import org.apache.commons.io.FileUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加密工具
 *
 * @author Manaphy
 * @date 2020-08-31
 */
@SuppressWarnings("unused")
public class SecureUtil {

    /**
     * DES模式
     */
    public static final String DES_MODE = "DES/ECB/PKCS5Padding";

    /**
     * AES模式
     */
    public static final String AES_MODE = "AES/ECB/PKCS5Padding";

    /**
     * RSA模式
     */
    public static final String RSA_MODE = "RSA/ECB/PKCS1Padding";

    /**
     * rsa算法
     */
    public static final String ALGORITHM_RSA = "RSA";

    /**
     * aes算法
     */
    public static final String ALGORITHM_AES = "AES";

    /**
     * des算法
     */
    public static final String ALGORITHM_DES = "DES";

    //=========================>Base64<=========================

    /**
     * Base64加密
     *
     * @param input 输入
     * @return {@link String}
     */
    public static String base64encode(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    /**
     * Base64解密
     *
     * @param input 输入
     * @return {@link byte[]}
     */
    public static byte[] base64decode(String input) {
        return Base64.getDecoder().decode(input);
    }

    //=========================>数字摘要<=========================

    /**
     * 消息数字摘要
     *
     * @param input     输入
     * @param algorithm 算法
     * @return {@link String}* @throws Exception 异常
     */
    private static String getDigest(String input, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            // 消息数字摘要
            byte[] digest = messageDigest.digest(input.getBytes(StandardCharsets.UTF_8));
            return toHex(digest);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * md5加密
     *
     * @param input 输入
     * @return {@link String}
     */
    public static String md5(String input) {
        return getDigest(input, "MD5");
    }

    /**
     * sha1加密
     *
     * @param input 输入
     * @return {@link String}
     */
    public static String sha1(String input) {
        return getDigest(input, "SHA-1");
    }

    /**
     * sha256加密
     *
     * @param input 输入
     * @return {@link String}
     */
    public static String sha256(String input) {
        return getDigest(input, "SHA-256");
    }

    /**
     * sha512加密
     *
     * @param input 输入
     * @return {@link String}
     */
    public static String sha512(String input) {
        return getDigest(input, "SHA-512");
    }

    /**
     * 获取文件的数字摘要
     *
     * @param filePath  文件路径
     * @param algorithm 算法
     * @return {@link String}* @throws Exception 异常
     */
    public static String getDigestFile(String filePath, String algorithm) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        int len;
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((len = fis.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        // 获取消息摘要对象
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        // 获取消息摘要
        byte[] digest = messageDigest.digest(baos.toByteArray());
        return toHex(digest);
    }

    //=========================>对称加密<=========================

    /**
     * DES AES加密数据
     *
     * @param input          : 原文
     * @param key            : 密钥(DES,密钥的长度必须是8个字节;AES是16个字节)
     * @param transformation : 获取Cipher对象的算法
     * @param algorithm      : 获取密钥的算法
     * @return {@link String}
     */
    private static String encryptDesAes(String input, String key, String transformation, String algorithm) {
        try {
            // 获取加密对象
            Cipher cipher = Cipher.getInstance(transformation);
            // 创建加密规则
            // 第一个参数key的字节
            // 第二个参数表示加密算法
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(), algorithm);
            // ENCRYPT_MODE：加密模式
            // DECRYPT_MODE: 解密模式
            // 初始化加密模式和算法
            cipher.init(Cipher.ENCRYPT_MODE, sks);
            // 加密
            byte[] bytes = cipher.doFinal(input.getBytes());
            // 返回加密后的数据
            return base64encode(bytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * DES AES解密
     *
     * @param input          : 密文
     * @param key            : 密钥
     * @param transformation : 获取Cipher对象的算法
     * @param algorithm      : 获取密钥的算法
     * @return 原文
     */
    private static String decryptDesAes(String input, String key, String transformation, String algorithm) {
        try {
            // 1,获取Cipher对象
            Cipher cipher = Cipher.getInstance(transformation);
            // 指定密钥规则
            SecretKeySpec sks = new SecretKeySpec(key.getBytes(), algorithm);
            cipher.init(Cipher.DECRYPT_MODE, sks);
            // 3. 解密，上面使用的base64编码，下面直接用密文
            byte[] bytes = cipher.doFinal(base64decode(input));
            //  因为是明文，所以直接返回
            return new String(bytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String desEncrypt(String input, String key) {
        return encryptDesAes(input, key, DES_MODE, ALGORITHM_DES);
    }

    public static String desDecrypt(String input, String key) {
        return decryptDesAes(input, key, DES_MODE, ALGORITHM_DES);
    }

    public static String aesEncrypt(String input, String key) {
        return encryptDesAes(input, key, AES_MODE, ALGORITHM_AES);
    }

    public static String aesDecrypt(String input, String key) {
        return decryptDesAes(input, key, AES_MODE, ALGORITHM_AES);
    }

    //=========================>非对称加密<=========================

    /**
     * 读取公钥
     *
     * @param publicPath 公钥路径
     * @return {@link PublicKey}* @throws Exception 异常
     */
    public static PublicKey getPublicKey(String publicPath) throws IOException, InvalidKeySpecException {
        // 将文件内容转为字符串
        String publicKeyString = FileUtils.readFileToString(new File(publicPath), Charset.defaultCharset());
        // 获取密钥工厂
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException ignore) {
        }
        // 构建密钥规范 进行Base64解码
        X509EncodedKeySpec spec = new X509EncodedKeySpec(base64decode(publicKeyString));
        // 生成公钥
        return keyFactory.generatePublic(spec);
    }

    /**
     * 读取私钥
     *
     * @param priPath 私钥路径
     * @return {@link PrivateKey}* @throws Exception 异常
     */
    public static PrivateKey getPrivateKey(String priPath) throws IOException, InvalidKeySpecException {
        // 将文件内容转为字符串
        String privateKeyString = FileUtils.readFileToString(new File(priPath), Charset.defaultCharset());
        // 获取密钥工厂
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException ignore) {
        }
        // 构建密钥规范 进行Base64解码
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(base64decode(privateKeyString));
        // 生成私钥
        return keyFactory.generatePrivate(spec);
    }

    /**
     * 生成密钥对并保存在本地文件中
     *
     * @param pubPath : 公钥保存路径
     * @param priPath : 私钥保存路径
     * @throws Exception 异常
     */
    public static void generateKeyToFile(String pubPath, String priPath) throws IOException {
        // 获取密钥对生成器
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException e) {
        }
        // 获取密钥对
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        // 获取公钥
        PublicKey publicKey = keyPair.getPublic();
        // 获取私钥
        PrivateKey privateKey = keyPair.getPrivate();
        // 获取byte数组
        byte[] publicKeyEncoded = publicKey.getEncoded();
        byte[] privateKeyEncoded = privateKey.getEncoded();
        // 进行Base64编码
        String publicKeyString = base64encode(publicKeyEncoded);
        String privateKeyString = base64encode(privateKeyEncoded);
        // 保存文件
        FileUtils.writeStringToFile(new File(pubPath), publicKeyString, StandardCharsets.UTF_8);
        FileUtils.writeStringToFile(new File(priPath), privateKeyString, StandardCharsets.UTF_8);

    }

    /**
     * 使用密钥加密数据
     *
     * @param input : 原文
     * @param key   : 密钥
     * @return {@link String}
     * @throws Exception 异常
     */
    public static String rsaEncrypt(Key key, String input) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        // 创建加密对象
        // 参数表示加密算法
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ignore) {
        }
        // 初始化加密
        // 第一个参数:加密的模式
        // 第二个参数：使用私钥进行加密
        cipher.init(Cipher.ENCRYPT_MODE, key);
        // 私钥加密
        byte[] bytes = cipher.doFinal(input.getBytes());
        // 对密文进行Base64编码
        return base64encode(bytes);
    }

    /**
     * 解密数据
     *
     * @param encrypted : 密文
     * @param key       : 密钥
     * @return {@link String}
     * @throws Exception 异常
     */
    public static String rsaDecrypt(Key key, String encrypted) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        // 创建加密对象
        // 参数表示加密算法
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
        }
        // 使用密钥进行解密
        cipher.init(Cipher.DECRYPT_MODE, key);
        // 由于密文进行了Base64编码, 在这里需要进行解码
        byte[] decode = base64decode(encrypted);
        // 对密文进行解密，不需要使用base64，因为原文不会乱码
        byte[] bytes1 = cipher.doFinal(decode);
        return new String(bytes1);

    }

    /**
     * 生成签名
     *
     * @param input      : 原文
     * @param privateKey : 私钥
     * @return {@link String}
     * @throws Exception 异常
     */
    public static String getSignature(String input, PrivateKey privateKey) throws InvalidKeyException, SignatureException {
        // 获取签名对象
        Signature signature = null;
        try {
            signature = Signature.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException ignore) {
        }
        // 初始化签名
        signature.initSign(privateKey);
        // 传入原文
        signature.update(input.getBytes());
        // 开始签名
        byte[] sign = signature.sign();
        // 对签名数据进行Base64编码
        return base64encode(sign);
    }

    /**
     * 校验签名
     *
     * @param input         : 原文
     * @param publicKey     : 公钥
     * @param signatureData : 签名
     * @return boolean
     * @throws Exception 异常
     */
    public static boolean verifySignature(String input, PublicKey publicKey, String signatureData) throws InvalidKeyException, SignatureException {
        // 获取签名对象
        Signature signature = null;
        try {
            signature = Signature.getInstance(ALGORITHM_RSA);
        } catch (NoSuchAlgorithmException ignore) {
        }
        // 初始化签名
        signature.initVerify(publicKey);
        // 传入原文
        signature.update(input.getBytes());
        // 校验数据
        return signature.verify(base64decode(signatureData));
    }


    /**
     * 十六进制化字节数组
     *
     * @param digest 字节数组
     * @return {@link String}
     */
    private static String toHex(byte[] digest) {
        // 创建对象用来拼接
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            // 转成 16进制
            String s = Integer.toHexString(b & 0xff);
            if (s.length() == 1) {
                // 如果生成的字符只有一个，前面补0
                s = "0" + s;
            }
            sb.append(s);
        }
        return sb.toString();
    }


    /**
     * unicode编码转中文
     *
     * @param theString 的字符串
     * @return {@link String}
     */
    public static String decodeUnicode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuilder outBuffer = new StringBuilder(len);
        for (int x = 0; x < len; ) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    int length = 4;
                    for (int i = 0; i < length; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException("不合法的unicode编码");
                        }

                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't') {
                        aChar = '\t';
                    } else if (aChar == 'r') {
                        aChar = '\r';
                    } else if (aChar == 'n') {
                        aChar = '\n';
                    } else if (aChar == 'f') {
                        aChar = '\f';
                    }
                    outBuffer.append(aChar);
                }
            } else {
                outBuffer.append(aChar);
            }
        }
        return outBuffer.toString();
    }

}
