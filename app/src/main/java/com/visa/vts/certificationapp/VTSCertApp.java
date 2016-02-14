package com.visa.vts.certificationapp;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.visa.cbp.external.common.EnrollDeviceCerts;
import com.visa.cbp.external.common.IssuerCerts;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
//import com.visa.cbp.sdk.facade.callbacks.ResponseCallback;
import com.visa.cbp.sdk.facade.data.IdStore;
import com.visa.cbp.sdk.facade.data.KeyType;
//import com.visa.cbp.sdk.facade.environment.Environment;
import com.visa.cbp.sdk.facade.exception.DeviceIDValidationException;

import com.visa.dmpd.token.JWTUtility;
import com.visa.vts.certificationapp.activity.BaseActivity;
import com.visa.vts.certificationapp.storage.Preference;
import com.visa.vts.certificationapp.utils.Constants;
import com.visa.vts.certificationapp.utils.ContextHelper;
import com.visa.vts.certificationapp.utils.EncryptionUtility;
import com.visa.vts.certificationapp.utils.JWTLocalUtility;
import com.visa.vts.certificationapp.utils.Utils;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.util.encoders.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Chandrakanta_Sahoo on 11/25/2015.
 */
public class VTSCertApp extends Application {
    private static final String TAG = VTSCertApp.class.getSimpleName();
    private String sEnvironment;
    private Preference preference;
    private VisaPaymentSDK visaPaymentSDK;
    private String deviceId;
    private String DSK = "4615dc2542aa001427c6057c46a06d16";



    public Preference getPreference() {
        return Preference.getInstance(this);
    }

    public String getsEnvironment() {
        return sEnvironment;
    }

    public void setsEnvironment(String sEnvironment) {
        this.sEnvironment = sEnvironment;
        if(sEnvironment!=null){
            getPreference().saveString(Constants.Preference_Environment, sEnvironment);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Setting context for ContextHelper
        ContextHelper.getInstance(getApplicationContext());

        //initialize SDK - TODO Check the appropiate parameter for environment instead of null
        //VisaPaymentSDKImpl.initialize(getApplicationContext(), Environment.valueOf("SANDBOX"));
        //VisaPaymentSDKImpl.initialize(getApplicationContext(),Environment.SANDBOX);
        VisaPaymentSDKImpl.initialize(getApplicationContext());
        visaPaymentSDK = VisaPaymentSDKImpl.getInstance();

        final String jweDSK = JWTLocalUtility.createJwe(getApplicationContext(), DSK.getBytes(), Build.SERIAL);
        //try {
        try {
            deviceId = visaPaymentSDK.getDeviceId();
        } catch (DeviceIDValidationException e) {
            //DeviceIDValidationException, deviceID wasn't set
            deviceId = null;
        }
        if (deviceId == null) {
            deviceId = createDeviceId();
            Log.d(TAG, "Device ID >>:" + deviceId);
            //               visaPaymentSDK.setDeviceId(deviceId);


        }
        //BaseActivity.deviceID = deviceId;
//            Preference.getInstance().saveString(Constants.Preference_DeviceID,deviceId);
        Utils.deviceID = deviceId;
        // } catch (DeviceIDValidationException e) {
        //   e.printStackTrace();
        //}
        visaPaymentSDK.onBoardDSK(jweDSK.getBytes(StandardCharsets.UTF_8));
        visaPaymentSDK.onBoardDeviceKeys(prepareIssuerCerts());

//        EnrollDeviceCerts enrollDeviceCerts = visaPaymentSDK.getCerts();
//        Log.d(TAG, "EnrollDeviceCerts " + enrollDeviceCerts.toString());


        //       setsEnvironment(getPreference().retrieveString(Constants.Preference_Environment));
    }
    public VisaPaymentSDK getVisaPaymentSDK() {
        return visaPaymentSDK;

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    @Override
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        super.registerComponentCallbacks(callback);
    }

    @Override
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        super.unregisterComponentCallbacks(callback);
    }
    //IssuerCert Creation
    private IssuerCerts prepareIssuerCerts() {
        IssuerCerts issuerCerts = null;

        PrivateKey issuerEncPrivateKey = getPrivateKey(R.raw.issuer_enc_priv_key);
        PrivateKey issuerSignPrivateKey = getPrivateKey(R.raw.issuer_sign_priv_key);
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            RSAPrivateKeySpec pkSpec = keyFac.getKeySpec(issuerEncPrivateKey, RSAPrivateKeySpec.class);
            BigInteger issuerEncPrivateKeyExponent = pkSpec.getPrivateExponent();
            String exponentString = issuerEncPrivateKeyExponent.toString(16);

            pkSpec = keyFac.getKeySpec(issuerSignPrivateKey, RSAPrivateKeySpec.class);
            BigInteger issuerSignPrivateKeyExponent = pkSpec.getPrivateExponent();
            String signExponentString = issuerSignPrivateKeyExponent.toString(16);
            String issuer_enc_exponent_jwe  = JWTUtility.createSharedSecretJwe(exponentString, Build.SERIAL, DSK);
            Log.d(TAG, ":ENC JWE " + issuer_enc_exponent_jwe);

            Log.d(TAG, "decrypted enc jwe" + JWTUtility.decryptJwe(issuer_enc_exponent_jwe, DSK));
            String issuer_sign_exponent_jwe = JWTUtility.createSharedSecretJwe(signExponentString, Build.SERIAL, DSK);
            Log.d(TAG+":SIGN JWE",issuer_sign_exponent_jwe);

            Log.d(TAG, "decrypted enc jwe" +JWTUtility.decryptJwe(issuer_sign_exponent_jwe, DSK));

            byte [] signPriKey = issuer_sign_exponent_jwe.getBytes(StandardCharsets.UTF_8);
            byte [] encPriKey = issuer_enc_exponent_jwe.getBytes(StandardCharsets.UTF_8);
            byte[] signCert = getCert(R.raw.issuer_sign_signed_new);
            byte[] encCert = getCert(R.raw.issuer_enc_signed_new);

            issuerCerts = new IssuerCerts();
            issuerCerts.setDeviceID(createDeviceId().getBytes(StandardCharsets.UTF_8));
            issuerCerts.setEncCert(encCert);
            issuerCerts.setEncExponent(encPriKey);
            issuerCerts.setSignCert(signCert);
            issuerCerts.setSignExponent(signPriKey);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return issuerCerts;
    }

    //Private key creation for given cert
    public PrivateKey getPrivateKey(int keyPEMFile) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream is = getResources().openRawResource(keyPEMFile);
        EncryptionUtility.readStream(is, byteArrayOutputStream);
        String certinpem = new String(byteArrayOutputStream.toByteArray());
        String privKeyPEM = certinpem.replace(
                "-----BEGIN RSA PRIVATE KEY-----\n", "")
                .replace("-----END RSA PRIVATE KEY-----", "");

        byte[] encodedPrivateKey = Base64.decode(privKeyPEM);
        try {

            ASN1Sequence primitive = (ASN1Sequence) ASN1Sequence
                    .fromByteArray(encodedPrivateKey);
            Enumeration<?> e = primitive.getObjects();
            BigInteger v = ((DERInteger) e.nextElement()).getValue();

            int version = v.intValue();
            if (version != 0 && version != 1) {
                throw new IllegalArgumentException("wrong version for RSA private key");
            }
            /**
             * In fact only modulus and private exponent are in use.
             */
            BigInteger modulus = ((DERInteger) e.nextElement()).getValue();
            BigInteger publicExponent = ((DERInteger) e.nextElement()).getValue();
            BigInteger privateExponent = ((DERInteger) e.nextElement()).getValue();
            BigInteger prime1 = ((DERInteger) e.nextElement()).getValue();
            BigInteger prime2 = ((DERInteger) e.nextElement()).getValue();
            BigInteger exponent1 = ((DERInteger) e.nextElement()).getValue();
            BigInteger exponent2 = ((DERInteger) e.nextElement()).getValue();
            BigInteger coefficient = ((DERInteger) e.nextElement()).getValue();

            RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, privateExponent);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey pk = kf.generatePrivate(spec);
            return pk;
        } catch (IOException e2) {
            throw new IllegalStateException();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException(e);
        }
    }

    //get byte[] of given certid
    public byte[] getCert(int certId) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream is = getResources().openRawResource(certId);
        EncryptionUtility.readStream(is, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    //creating deviceID
    public String createDeviceId() {
        String uniqueId = "";

        TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        uniqueId = uniqueId.concat(TelephonyMgr.getDeviceId());

        String buildParams = "99" +
                Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                Build.USER.length()%10 ;

        uniqueId = uniqueId.concat(buildParams);

        uniqueId = uniqueId.concat(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        uniqueId = uniqueId.concat(wm.getConnectionInfo().getMacAddress());

        BluetoothAdapter mBluetoothAdapter	= BluetoothAdapter.getDefaultAdapter();
        uniqueId = uniqueId.concat(mBluetoothAdapter.getAddress());

        // Initiate digest with MD5
        MessageDigest mDigest = null;
        try {
            mDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        mDigest.update(uniqueId.getBytes(), 0, uniqueId.length());

        byte[] digestedBytes = mDigest.digest();
        String deviceId = "";

        for (int i = 0; i < digestedBytes.length; i++) {

            int b = (0xFF & digestedBytes[i]);

            // Additional Padding
            if (b <= 0xF) {
                deviceId += "0";
            }

            // concat at the end
            deviceId = deviceId.concat(Integer.toHexString(b));
        }

        return deviceId.substring(0, 23);
    }

}
