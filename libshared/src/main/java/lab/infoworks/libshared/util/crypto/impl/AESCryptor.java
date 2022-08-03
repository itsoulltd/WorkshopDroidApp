package lab.infoworks.libshared.util.crypto.impl;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import lab.infoworks.libshared.util.crypto.definition.Cryptor;
import lab.infoworks.libshared.util.crypto.models.AESMode;
import lab.infoworks.libshared.util.crypto.models.SecretKeyAlgo;
import lab.infoworks.libshared.util.crypto.models.ShaKey;

public class AESCryptor implements Cryptor {

    private Cipher cipher;
    private Cipher decipher;
    private MessageDigest sha;

    private final ShaKey shaKey;
    private final AESMode aesMode;
    private final SecretKeyAlgo secretKeyAlgo;

    public AESCryptor() {
        this(ShaKey.Sha_256, AESMode.AES_ECB_PKCS5Padding, SecretKeyAlgo.AES);
    }

    public AESCryptor(ShaKey shaKey, AESMode aesMode, SecretKeyAlgo secretKeyAlgo) {
        this.shaKey = shaKey;
        this.aesMode = aesMode;
        this.secretKeyAlgo = secretKeyAlgo;
    }

    private Cipher getCipher(String secret) throws Exception{
        if (cipher == null){
            SecretKey secretKey = getKeySpace(secret);
            cipher = Cipher.getInstance(aesMode.value());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        }
        return cipher;
    }

    private Cipher getDecipher(String secret) throws Exception{
        if (decipher == null){
            SecretKey secretKey = getKeySpace(secret);
            decipher = Cipher.getInstance(aesMode.value());
            decipher.init(Cipher.DECRYPT_MODE, secretKey);
        }
        return decipher;
    }

    @Override
    public SecretKey getKeySpace(String mykey)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //
        if (mykey == null || mykey.isEmpty())
            throw new UnsupportedEncodingException("SecretKey is null or empty!");
        //
        if (aesMode == AESMode.AES_ECB_PKCS5Padding){
            byte[] key = mykey.getBytes("UTF-8");
            key = getSha(shaKey).digest(key);
            key = Arrays.copyOf(key, 16);
            SecretKeySpec secretKey = new SecretKeySpec(key, secretKeyAlgo.name());
            return secretKey;
        }
        else if (aesMode == AESMode.AES_CBC_PKCS7Padding){
            throw new NoSuchAlgorithmException(aesMode.value() + " not supported yet");
        }
        else if (aesMode == AESMode.AES_GCM_NoPadding){
            throw new NoSuchAlgorithmException(aesMode.value() + " not supported yet");
        }
        else {
            throw new NoSuchAlgorithmException(aesMode.value() + " not supported yet");
        }
    }

    private MessageDigest getSha(ShaKey shaKey) throws NoSuchAlgorithmException {
        if (sha == null){
            sha = MessageDigest.getInstance(shaKey.value());
        }
        return sha;
    }

    @Override
    public String encrypt(String secret, String strToEncrypt) {
        try {
            Cipher cipher = getCipher(secret);
            return Base64.encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")), Base64.DEFAULT);
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    @Override
    public String decrypt(String secret, String strToDecrypt) {
        try {
            Cipher cipher = getDecipher(secret);
            return new String(cipher.doFinal(Base64.decode(strToDecrypt.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }
}