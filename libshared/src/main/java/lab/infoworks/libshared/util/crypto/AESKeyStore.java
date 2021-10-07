package lab.infoworks.libshared.util.crypto;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.concurrent.locks.ReentrantLock;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class AESKeyStore extends AESCryptor{

    public static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    public static final String SECRET_ALIAS = "MySecretAlias";
    private static volatile AESKeyStore instance;
    private static final ReentrantLock REENTRANT_LOCK = new ReentrantLock();
    public static AESKeyStore getInstance(){
        if (instance == null){
            REENTRANT_LOCK.lock();
            try {
                if (instance == null){
                    instance = new AESKeyStore();
                }
            } catch (Exception e){ }
            finally {
                REENTRANT_LOCK.unlock();
            }
        }
        return instance;
    }

    private KeyStore keyStore;
    private SecretKey secretKey;

    private AESKeyStore() {
        super(ShaKey.Sha_256, AESMode.AES_ECB_PKCS5Padding, SecretKeyAlgo.AES);
    }

    private KeyStore getKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        if (keyStore == null){
            keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            keyStore.load(null);
        }
        return keyStore;
    }

    @Override
    public SecretKey getKeySpace(String mykey) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (secretKey == null){
            try {
                //Load from KeyStore:
                secretKey = (SecretKey) getKeyStore().getKey(SECRET_ALIAS, null);
                //If not imported then, create and import:
                if (secretKey == null){
                    secretKey = generateSecretKey(SECRET_ALIAS, mykey);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getKeyStore().setEntry(
                                SECRET_ALIAS,
                                new KeyStore.SecretKeyEntry(secretKey),
                                new KeyProtection.Builder(KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                                        .build());
                    }
                }
            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (UnrecoverableKeyException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }
        }
        return secretKey;
    }

    private SecretKey generateSecretKey(String alias, String myKey)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
        //
        return super.getKeySpace(myKey);
        /*KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            KeyGenParameterSpec.Builder builder =
                    new KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);
            builder.setKeySize(256);
            builder.setBlockModes(KeyProperties.BLOCK_MODE_CBC);
            builder.setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.init(builder.build());
        }
        // This key will work with a CipherObject ...
        SecretKey key = keyGenerator.generateKey();
        return key;*/
    }
}
