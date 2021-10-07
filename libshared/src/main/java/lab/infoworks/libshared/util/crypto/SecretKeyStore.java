package lab.infoworks.libshared.util.crypto;

import android.app.Application;
import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.google.android.gms.common.util.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.concurrent.locks.ReentrantLock;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.security.auth.x500.X500Principal;

import lab.infoworks.libshared.domain.shared.AppStorage;

public class SecretKeyStore implements iSecretKeyStore{

    public static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    public static final String TAG = SecretKeyStore.class.getSimpleName();
    private static volatile SecretKeyStore instance;
    private static final ReentrantLock REENTRANT_LOCK = new ReentrantLock();

    public static SecretKeyStore init(Application context){
        if (instance == null){
            REENTRANT_LOCK.lock();
            try {
                if (instance == null){
                    instance = new SecretKeyStore(context);
                }
            } catch (Exception e){ }
            finally {
                REENTRANT_LOCK.unlock();
            }
        }
        return instance;
    }

    public static SecretKeyStore getInstance() throws RuntimeException{
        if (instance == null) throw new RuntimeException("Not instantiated!");
        return instance;
    }

    private KeyStore keyStore;
    private SecretKey secretKey;
    private WeakReference<Context> weakContext;
    private AppStorage appStorage;
    private Cryptor cryptor;

    private SecretKeyStore(Context context) {
        this.cryptor = Cryptor.create();
        this.weakContext = new WeakReference<>(context);
        this.appStorage = new AppStorage(context);
    }

    private KeyStore getKeyStore() throws RuntimeException {
        if (keyStore == null){
            try {
                keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
                keyStore.load(null);
            } catch (KeyStoreException | NoSuchAlgorithmException
                    | IOException | CertificateException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return keyStore;
    }

    private AppStorage getAppStorage() {
        return appStorage;
    }

    private Context getContext() {
        return weakContext.get();
    }

    @Override
    public Cryptor getCryptor() {
        return cryptor;
    }

    public String encrypt(String alias, String text) throws RuntimeException{
        String secret = getStoredSecret(alias);
        return getCryptor().encrypt(secret, text);
    }

    public String decrypt(String alias, String text) throws RuntimeException{
        String secret = getStoredSecret(alias);
        return getCryptor().decrypt(secret, text);
    }

    public void storeSecret(String alias, String secret, boolean replace) throws RuntimeException{
        try {
            if (replace == false){
                String encryptedSecret = getAppStorage().stringValue(alias);
                if (encryptedSecret != null && !encryptedSecret.isEmpty()) return;
            }
            //Now not exist:
            Key pbKey = createSecretKey(alias, getContext());
            if (pbKey == null) {
                Log.d(TAG, "storeSecret: " + "Already exist.");
                //
                KeyStore.Entry entry = getKeyStore().getEntry(alias, null);
                if (entry instanceof KeyStore.PrivateKeyEntry){
                    pbKey = ((KeyStore.PrivateKeyEntry) entry).getCertificate().getPublicKey();
                } else if(entry instanceof KeyStore.SecretKeyEntry) {
                    pbKey = ((KeyStore.SecretKeyEntry) entry).getSecretKey();
                }
            }
            //
            if (pbKey instanceof RSAPublicKey){
                String encrypted = encryptUsingRsaPublicKey((RSAPublicKey)pbKey, secret);
                getAppStorage().put(alias, encrypted);
            }else if (pbKey instanceof SecretKey){
                String encrypted = encryptUsingAesSecretKey((SecretKey)pbKey, secret);
                getAppStorage().put(alias, encrypted);
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String encryptUsingAesSecretKey(SecretKey pbKey, String secret) throws RuntimeException{
        try {
            Cipher cipher = Cipher.getInstance(AESMode.AES_CBC_PKCS7Padding.value());
            cipher.init(Cipher.ENCRYPT_MODE, pbKey);
            //
            /*ByteArrayOutputStream bos = new ByteArrayOutputStream();
            CipherOutputStream cos = new CipherOutputStream(bos, cipher);
            cos.write(secret.getBytes(StandardCharsets.UTF_8));
            cos.close();
            byte[] encryptedBytes = bos.toByteArray();*/
            //
            byte[] encryptedBytes = cipher.doFinal(secret.getBytes(StandardCharsets.UTF_8));
            String encrypted = Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
            Log.d("StarterApp", "encryptUsingAesSecretKey: " + encrypted);
            return encrypted;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String encryptUsingRsaPublicKey(RSAPublicKey pbKey, String secret) throws RuntimeException{
        //TODO:
        throw new RuntimeException("NOT IMPLEMENTED YET!");
    }

    public String getStoredSecret(String alias) throws RuntimeException {
        try {
            if (!getKeyStore().containsAlias(alias)) {
                throw new RuntimeException(alias + " Not Exist!");
            }
            String encrypted = getAppStorage().stringValue(alias);
            KeyStore.Entry entry = getKeyStore().getEntry(alias, null);
            if (entry instanceof KeyStore.PrivateKeyEntry){
                RSAPrivateKey key = (RSAPrivateKey) ((KeyStore.PrivateKeyEntry) entry).getPrivateKey();
                return decryptUsingRsaPrivateKey(key, encrypted);
            } else if(entry instanceof KeyStore.SecretKeyEntry) {
                SecretKey key = ((KeyStore.SecretKeyEntry) entry).getSecretKey();
                return decryptUsingAesSecretKey(key, encrypted);
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableEntryException e) {
            throw new RuntimeException(e.getMessage());
        }
        return "";
    }

    private String decryptUsingAesSecretKey(SecretKey key, String encrypted) throws RuntimeException{
        try {
            Cipher decipher = Cipher.getInstance(AESMode.AES_CBC_PKCS7Padding.value());
            decipher.init(Cipher.ENCRYPT_MODE, key);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(decipher.getIV());
            //
            Cipher cipher = Cipher.getInstance(AESMode.AES_CBC_PKCS7Padding.value());
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            //
            Log.d("StarterApp", "decryptUsingAesSecretKey: " + encrypted);
            byte[] encryptedBytes = Base64.decode(encrypted.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
            /*ByteArrayInputStream bis = new ByteArrayInputStream(encryptedBytes);
            CipherInputStream cis = new CipherInputStream(bis, cipher);
            //byte[] readBytes = new byte[encryptedBytes.length];
            //cis.read(readBytes);
            byte[] readBytes = IOUtils.readInputStreamFully(cis, false);
            cis.close();*/
            //
            byte[] readBytes = cipher.doFinal(encryptedBytes);
            String decryptedText = new String(readBytes, StandardCharsets.UTF_8);
            return decryptedText;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | BadPaddingException
                | InvalidAlgorithmParameterException | IllegalBlockSizeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String decryptUsingRsaPrivateKey(RSAPrivateKey key, String encryptedRandDeviceKey) throws RuntimeException{
        //TODO:
        throw new RuntimeException("NOT IMPLEMENTED YET!");
    }

    protected Key createSecretKey(String alias, Context context)
            throws RuntimeException {
        //Pre-Check if already created:
        try {
            if (getKeyStore().containsAlias(alias)) return null;
            //
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
                KeyGenParameterSpec.Builder builder =
                        new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);
                builder.setKeySize(128);
                builder.setBlockModes(KeyProperties.BLOCK_MODE_CBC);
                builder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
                keyGenerator.init(builder.build());
                // This key will work with a CipherObject ...
                SecretKey key = keyGenerator.generateKey();
                return key;
            } else{
                KeyPairGenerator generator = KeyPairGenerator.getInstance(
                        KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);
                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(new X500Principal("CN=Sample Name, O=Android Authority"))
                        .setSerialNumber(BigInteger.ONE)
                        .build();
                generator.initialize(spec);
                // This key will work with a CipherObject ...
                KeyPair pair = generator.generateKeyPair();
                return pair.getPublic();
            }
        } catch (NoSuchAlgorithmException | NoSuchProviderException
                | InvalidAlgorithmParameterException | KeyStoreException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
