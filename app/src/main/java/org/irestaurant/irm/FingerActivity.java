package org.irestaurant.irm;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import org.irestaurant.irm.Database.DatabaseHelper;
import org.irestaurant.irm.Database.FingerprintHandler;
import org.irestaurant.irm.Database.SessionManager;
import org.irestaurant.irm.Database.User;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class FingerActivity extends Activity {
    //    Finger
    private KeyStore keyStore;
    private static final String KEY_NAME = "EDMTDev";
    private Cipher cipher;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_finger);
        setResult(Activity.RESULT_CANCELED);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            KeyguardManager keyguardManager = (KeyguardManager)getSystemService(KEYGUARD_SERVICE);
            FingerprintManager fingerprintManager = null;
            fingerprintManager = (FingerprintManager)getSystemService(FINGERPRINT_SERVICE);
            if(!fingerprintManager.isHardwareDetected()) {
                Toast.makeText(this, "Thiết bị không có vân tay", Toast.LENGTH_SHORT).show();
                finish();
            }else {
                if (!keyguardManager.isKeyguardSecure()) {
                        Toast.makeText(this, "Chưa cài đặt vân tay", Toast.LENGTH_SHORT).show();
                    }else
                        genKey();
                    if (cipherInit()){
                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
                        FingerprintHandler helper = new FingerprintHandler(this);
                        helper.startAuthentication(fingerprintManager, cryptoObject);
                    }
//                if (fingerprintManager.hasEnrolledFingerprints()) {
//                    Toast.makeText(this, "Đăng ký ít nhất 1 vân tay", Toast.LENGTH_SHORT).show();
//                }else {
//                    if (!keyguardManager.isKeyguardSecure()) {
//                        Toast.makeText(this, "Chưa cài đặt vân tay", Toast.LENGTH_SHORT).show();
//                    }else
//                        genKey();
//                    if (cipherInit()){
//                        FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
//                        FingerprintHandler helper = new FingerprintHandler(this);
//                        helper.startAuthentication(fingerprintManager, cryptoObject);
//                    }
//                }
            }
        }
    }


    private boolean cipherInit() {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES+"/"+KeyProperties.BLOCK_MODE_CBC+"/"+KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (CertificateException e1) {
            e1.printStackTrace();
            return false;
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
            return false;
        } catch (UnrecoverableKeyException e1) {
            e1.printStackTrace();
            return false;
        } catch (KeyStoreException e1) {
            e1.printStackTrace();
            return false;
        } catch (InvalidKeyException e1) {
            e1.printStackTrace();
            return false;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void genKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        KeyGenerator keyGenerator = null;
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        try {
            keyStore.load(null);
            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7).build()
            );
            keyGenerator.generateKey();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        catch (InvalidAlgorithmParameterException e){
            e.printStackTrace();
        }
    }

    public void login(){
//        DatabaseHelper db = new DatabaseHelper(this);
//        User user = db.fingerLogin(phone);
//        sessionManager.createSession(user.getId(),user.getName(),user.getPhone(), user.getPassword(),user.getResname(),user.getResphone(),user.getResaddress());
//        Toast.makeText(this, "Xin chào "+user.getName(), Toast.LENGTH_SHORT).show();
//        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        finish();
    }
}
