package com.visa.vts.certificationapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.visa.cbp.external.common.EnrollDeviceCerts;
import com.visa.cbp.external.enp.EnrollDeviceResponse;
import com.visa.cbp.sdk.facade.VisaPaymentSDK;
import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.VTSCertApp;
import com.visa.vts.certificationapp.core.Session;
import com.visa.vts.certificationapp.manager.NetworkManager;
import com.visa.vts.certificationapp.model.request.ChannelInfo;
import com.visa.vts.certificationapp.model.request.ChannelSecurityContext;
import com.visa.vts.certificationapp.model.request.DeviceCert;
import com.visa.vts.certificationapp.model.request.DeviceInfo;
import com.visa.vts.certificationapp.model.request.EnrollDeviceParams;
import com.visa.vts.certificationapp.model.request.VtsCert;
import com.visa.vts.certificationapp.network.BackendEndPoints;
import com.visa.vts.certificationapp.network.RestController;
import com.visa.vts.certificationapp.storage.Preference;
import com.visa.vts.certificationapp.utils.Constants;
import com.visa.vts.certificationapp.utils.Utils;

import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.UrlBase64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class EnvironmentActivity extends BaseActivity {
    private TextView text_select_env;
    private VTSCertApp mainApp;
    private VisaPaymentSDK visaPaymentSDK;
    private String deviceID;
    private Activity mContext;
    private int responseStatus;
    private String responseBody;
    private com.visa.vts.certificationapp.utils.ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment);
        mainApp = (VTSCertApp) getApplicationContext();
        visaPaymentSDK = mainApp.getVisaPaymentSDK();
        //progress dialog
        mContext = EnvironmentActivity.this;
        mProgressDialog = new com.visa.vts.certificationapp.utils.ProgressDialog(mContext);


        Button btnEnroll = (Button) findViewById(R.id.selectEnv);
        btnEnroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enrollDevice();
            }
        });

        text_select_env = (TextView) findViewById(R.id.select_env_text);
        Spinner spinner = (Spinner) findViewById(R.id.env_spinner);
        String[] envList = getResources().getStringArray(R.array.vts_environments);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this,
                        android.R.layout.simple_spinner_dropdown_item, envList);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedItem = (TextView) view;
                text_select_env.setText("SELECTED ENVIRONMENT: " + "   " + selectedItem.getText());
                String envSelected = selectedItem.getText().toString();
                mainApp.setsEnvironment(envSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mainApp.setsEnvironment("SANDBOX");
            }
        });
    }


    private void enrollDevice() {

        if (!NetworkManager.checkInternet(mContext))
            return;

        mProgressDialog.show();

        mProgressDialog.setOnDismissListener(new com.visa.vts.certificationapp.utils.ProgressDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                navigateToNextPage();
            }
        });

        EnrollDeviceParams enrollDeviceParams = createEnrollDeviceparams();
        String payload = null;

        try {
            Gson gson = new Gson();
            payload = gson.toJson(enrollDeviceParams, EnrollDeviceParams.class);
            Utils.printLog("enrollDevice PayloadData :" + payload);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String x_pay_token = genXpayToken_HMAC(Constants.SANDBOX_SHARED_SECRET, Constants.ApiNameEnrollDevice, Constants.SANDBOX_API_KEY, payload);

        BackendEndPoints service = RestController.getApi();
        Call<EnrollDeviceResponse> call = service.enrollDevice(x_pay_token, enrollDeviceParams, deviceID);

        call.enqueue(new Callback<EnrollDeviceResponse>() {
            @Override
            public void onResponse(Response<EnrollDeviceResponse> response, Retrofit retrofit) {

                Utils.printLog("Response Code " + response.code());
                responseStatus = response.code();
                responseBody = Preference.getInstance().retrieveString(Constants.PREFERENCE_RESPONSEBODY);

                //String serverNonce = "99218e2656f042d1b4f2553e6c69e95f0389";
                //EnrollDeviceResponse enrollDeviceResponse = response.body();
                //enrollDeviceResponse.setVServerNonce(serverNonce);
                //visaPaymentSDK.processEnrollDeviceresponse(enrollDeviceResponse);

                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Throwable throwable) {
                mProgressDialog.dismiss();
                Utils.printLog("Error: " + throwable.getMessage());
            }
        });
    }


    private EnrollDeviceParams createEnrollDeviceparams() {

        try {
            //List<File> files = getListFiles(new File("/data/data/com.vts.oct/files/VisaCBPSDK/"));

            EnrollDeviceCerts enrollDeviceCerts = visaPaymentSDK.getCerts();
            //deviceID = enrollDeviceCerts.getDeviceId();
            deviceID = Utils.deviceID;
            //temp fix.
//            deviceID = Constants.clientDeviceID;
            Utils.printLog("Device ID: " + deviceID);

            //1. DeviceInfo
            DeviceInfo deviceInfo = new DeviceInfo();
            //Set all deviceinfo values here- confirm with HARI for pre existence value in SDK?
            deviceInfo.init(deviceID);


            //2. ChannelInfo
            ChannelInfo channelInfo = new ChannelInfo();
            channelInfo.setEncryptionScheme(Constants.ENCRYPTION_SCHEME);


            //3.Device CERT
            List<DeviceCert> deviceCertList = new ArrayList<DeviceCert>();


            DeviceCert deviceCert_confidentially = new DeviceCert();
            deviceCert_confidentially.setCertFormat(Constants.CERT_FORMAT_VALUE);
            deviceCert_confidentially.setCertUsage(Constants.CERT_CONFIDENTIALITY);


            Utils.printLog("enrollDeviceCerts.getConfidentialityKey(): " + getCertString(enrollDeviceCerts.getConfidentialityKey()));

            deviceCert_confidentially.setCertValue(getCertString(enrollDeviceCerts.getConfidentialityKey()));


            DeviceCert deviceCert_integrity = new DeviceCert();
            deviceCert_integrity.setCertFormat(Constants.CERT_FORMAT_VALUE);
            deviceCert_integrity.setCertUsage(Constants.CERT_INTEGRITY);
            deviceCert_integrity.setCertValue(getCertString(enrollDeviceCerts.getIntegrityKey()));

            DeviceCert deviceCert_deviceroot = new DeviceCert();
            deviceCert_deviceroot.setCertFormat(Constants.CERT_FORMAT_VALUE);
            deviceCert_deviceroot.setCertUsage(Constants.CERT_DEVICE_ROOT);
            deviceCert_deviceroot.setCertValue(getCertString(enrollDeviceCerts.getDeviceRootKey()));


            deviceCertList.add(deviceCert_confidentially);
            deviceCertList.add(deviceCert_integrity);
            if (enrollDeviceCerts.getDeviceRootKey() != null)
                deviceCertList.add(deviceCert_deviceroot);

            //4.VTS
            List<VtsCert> vtsCertList = new ArrayList<VtsCert>();


            VtsCert vtsCert_confi = new VtsCert();
            vtsCert_confi.setCertUsage(Constants.CERT_CONFIDENTIALITY);
            vtsCert_confi.setVCertificateID(enrollDeviceCerts.getVisaConfidentialityCertId());
//        vtsCert_confi.setVCertificateID("215452c7");

            VtsCert vtsCert_integrity = new VtsCert();
            vtsCert_integrity.setCertUsage(Constants.CERT_INTEGRITY);
            vtsCert_integrity.setVCertificateID(enrollDeviceCerts.getVisaIntegrityCertId());
//        vtsCert_integrity.setVCertificateID("8302bc7f");

            vtsCertList.add(vtsCert_confi);
            vtsCertList.add(vtsCert_integrity);

            //5.ChannelSecurity
            ChannelSecurityContext channelSecurityContext = new ChannelSecurityContext();
            channelSecurityContext.setChannelInfo(channelInfo);
            channelSecurityContext.setDeviceCerts(deviceCertList);
            channelSecurityContext.setVtsCerts(vtsCertList);

            //Enroll
            EnrollDeviceParams enrollDeviceParams = new EnrollDeviceParams();
            enrollDeviceParams.setDeviceInfo(deviceInfo);
            enrollDeviceParams.setChannelSecurityContext(channelSecurityContext);
            return enrollDeviceParams;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCertString(byte[] cert) {
        if (cert == null) return null;
        String certString = new String(Base64.encode(cert));
        String certPem = "-----BEGIN CERTIFICATE-----\n" + certString + "\n-----END CERTIFICATE-----";

        return new String(UrlBase64.encode(certPem.getBytes(StandardCharsets.UTF_8)));
    }


    private void navigateToDashboards() {
        Intent intent = new Intent(EnvironmentActivity.this, DashboardActivity.class);
        startActivity(intent);

    }

    private void navigateToNextPage() {

        showLogScreen(responseStatus, Constants.REQUEST_ENROLL_DEVICE, responseBody);


    }

    public static final String genXpayToken_HMAC(String seckey, String apiname, String apikey, String payload) {
        String prefix = "xv2:";
        String xpayToken;
        String data;
        String querystring = "apiKey=";
        String timestamp = getCurrentTimeStamp();
        data = timestamp + apiname + querystring + apikey + payload;
        String xpayTokenData = generatev2(seckey, data);
        xpayToken = prefix + timestamp + ":" + xpayTokenData;
        System.out.println("xpayTokenData :::" + xpayToken + ":::payload:::" + payload);
        return xpayToken;
    }

    public static final String generatev2(String key, String data) {
        return hmacSha256(key, data);
    }

    public static String hmacSha256(String key, String toHash) {

        SecretKeySpec signingKey = null;
        try {
            signingKey = new SecretKeySpec(key.getBytes("UTF-8"), "HMACSHA256");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Mac mac = null;
        try {
            mac = Mac.getInstance("HMACSHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            mac.init(signingKey);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        byte[] rawHmac = new byte[0];
        try {
            rawHmac = mac.doFinal(toHash.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        byte[] hexArray = {
                (byte) '0', (byte) '1', (byte) '2', (byte) '3',
                (byte) '4', (byte) '5', (byte) '6', (byte) '7',
                (byte) '8', (byte) '9', (byte) 'a', (byte) 'b',
                (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f'
        };
        byte[] hexChars = new byte[rawHmac.length * 2];
        for (int j = 0; j < rawHmac.length; j++) {
            int v = rawHmac[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static final String getCurrentTimeStamp() {
        long timestamplong = System.currentTimeMillis() / 1000;
        //43980 -temp fix
        //timestamplong = (timestamplong - 43980);
        String timestamp = Long.toString(timestamplong);
        Utils.printLog("Time Stamp:" + timestamp);
        return timestamp;
    }
}


