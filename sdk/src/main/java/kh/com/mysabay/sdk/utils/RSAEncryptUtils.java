package kh.com.mysabay.sdk.utils;

import android.content.Context;
import android.util.Base64;

import com.yakivmospan.scytale.Crypto;
import com.yakivmospan.scytale.KeyProps;
import com.yakivmospan.scytale.Store;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

import javax.inject.Inject;
import javax.security.auth.x500.X500Principal;

import kh.com.mysabay.sdk.R;

public class RSAEncryptUtils {

    private static final String TAG = RSAEncryptUtils.class.getSimpleName();

    private static final String CRYPTO_METHOD = "RSA/ECB/PKCS1Padding";
    static RSAEncryptUtils RSAEncryptUtilsInstance;
    private Store keyStore;
    public KeyPair keyPair;
    private Crypto crypto;
    private static final int KEY_SIZE = 2048;

    public static RSAEncryptUtils getInstance(Context context) {
        synchronized (RSAEncryptUtils.class) {
            if (null == RSAEncryptUtilsInstance) {
                RSAEncryptUtilsInstance = new RSAEncryptUtils(context);
            }
        }
        return RSAEncryptUtilsInstance;
    }

    @Inject
    public RSAEncryptUtils(Context context) {
        LogUtil.debug(TAG, "init");
        initKeyStore(context);
        createNewKeys(context, "ALIAS");
        initCipher();
    }

    private void initCipher() {
        final int encryptionBlockSize = KEY_SIZE / 8 - 11; // as specified for RSA/ECB/PKCS1Padding keys
        final int decryptionBlockSize = KEY_SIZE / 8; // as specified for RSA/ECB/PKCS1Padding keys

        crypto = new Crypto(CRYPTO_METHOD, encryptionBlockSize, decryptionBlockSize);
    }

    private void initKeyStore(Context context) {
        // Create store with specific name and password
        keyStore = new Store(context, context.getString(R.string.store_name), context.getString(R.string.store_password).toCharArray());
    }

    private void createNewKeys(@NotNull Context context, String alias) {
        final char[] password = context.getString(R.string.key_store_password).toCharArray();

        final Calendar start = Calendar.getInstance();
        final Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 1);

        // Create a key store params, some of them are specific per platform
        // Check KeyProps doc for more info
        KeyProps keyProps = new KeyProps.Builder()
                .setAlias(alias)
                .setPassword(password)
                .setKeySize(KEY_SIZE)
                .setKeyType("RSA")
                .setSerialNumber(BigInteger.ONE)
                .setSubject(new X500Principal("CN=" + alias + " CA Certificate"))
                .setStartDate(start.getTime())
                .setEndDate(end.getTime())
                .setBlockModes("ECB")
                .setEncryptionPaddings("PKCS1Padding")
                .setSignatureAlgorithm("SHA256WithRSAEncryption")
                .build();

        // Generate KeyPair depending on KeyProps
        keyPair = keyStore.generateAsymmetricKey(keyProps);

    }

    public String encrypt(String text) {
        return crypto.encrypt(text, keyPair.getPublic(), false);
    }

    public String decrypt(String encryptedData) {
        return crypto.decrypt(encryptedData, keyPair.getPrivate(), false);
    }

    private static PrivateKey stringToPrivateKey(String privateKeyString)
            throws InvalidKeySpecException,
            NoSuchAlgorithmException {

        byte[] pkcs8EncodedBytes = Base64.decode(privateKeyString, Base64.DEFAULT);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        KeyFactory kf = KeyFactory.getInstance(CRYPTO_METHOD);
        return kf.generatePrivate(keySpec);
    }

    public static enum PublicKeyType {
        PKCS1_PEM, PKCS8_PEM, BASE64
    }

    public String getPublicKey(@NotNull PublicKeyType option) {

        switch (option) {

            case PKCS1_PEM:
                String pkcs1pem = "-----BEGIN RSA PUBLIC KEY-----";
                pkcs1pem += Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.NO_WRAP);
                pkcs1pem += "-----END RSA PUBLIC KEY-----";

                return pkcs1pem;

            case PKCS8_PEM:
                String pkcs8pem = "-----BEGIN PUBLIC KEY-----";
                pkcs8pem += Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.NO_WRAP);
                pkcs8pem += "-----END PUBLIC KEY-----";

                return pkcs8pem;

            case BASE64:
                return Base64.encodeToString(keyPair.getPublic().getEncoded(), Base64.DEFAULT);
            default:
                return null;

        }

    }

    @Nullable
    public static PublicKey stringToPublicKey(@NotNull String publicKeyString) {

        try {
            if (publicKeyString.contains("-----BEGIN PUBLIC KEY-----") || publicKeyString.contains("-----END PUBLIC KEY-----"))
                publicKeyString = publicKeyString.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "");
            byte[] keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(spec);

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();

            return null;
        }
    }
}
