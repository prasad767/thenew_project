package com.visa.vts.certificationapp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.visa.cbp.external.common.EnrollPanResponse;
import com.visa.cbp.external.enp.EnrollPanRequest;
import com.visa.vts.certificationapp.R;
import com.visa.vts.certificationapp.core.Session;
import com.visa.vts.certificationapp.manager.NetworkManager;
import com.visa.vts.certificationapp.model.BillingAddress;
import com.visa.vts.certificationapp.model.ExpirationDate;
import com.visa.vts.certificationapp.model.PaymentInstrument;
import com.visa.vts.certificationapp.network.BackendEndPoints;
import com.visa.vts.certificationapp.network.RestController;
import com.visa.vts.certificationapp.storage.Preference;
import com.visa.vts.certificationapp.utils.AlertDialogSingleButton;
import com.visa.vts.certificationapp.utils.Constants;
import com.visa.vts.certificationapp.utils.JWTFactory;
import com.visa.vts.certificationapp.utils.Utils;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by prrathin on 11/18/15.
 */
public class AddCardActivity extends BaseActivity {
    private String TAG = AddCardActivity.class.getCanonicalName();
    private PaymentInstrument mPaymentInstrument;
    private BillingAddress mBillingAddress;
    private static final int SCAN_REQUEST_CODE = 23932;
    private EditText mCardEditText, mExpiryEditText, mCvvEditText, mFirstNameEditText, mLastNameEditText, mStateEditText,
            mCityEditText, mZipEditText, mAddressEditText1, mAddressEditText2;
    private Activity mContext;
    private int mResponseStatus, mKeyDeleteExpiry;
    private String mTermsAndConditionsID, mVPanEnrollmentID, mResponseBody, mExpiryDate;
    private com.visa.vts.certificationapp.utils.ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        classAndWidgetInitialize();

        processToolbar();

        mFirstNameEditText.setText("test");
        mLastNameEditText.setText("name");
        mCardEditText.setText("4622-9431-2700-1510");
        mCvvEditText.setText("742");
        mExpiryEditText.setText("12/17");
        mAddressEditText1.setText("900 Metro Center BLVD");
        mAddressEditText2.setText("M3-2WS");
        mStateEditText.setText("CA");
        mZipEditText.setText("94404");
        mCityEditText.setText("Foster City");

    }

    /**
     * Initialize class And Widget
     */
    private void classAndWidgetInitialize() {
        mContext = AddCardActivity.this;
        mProgressDialog = new com.visa.vts.certificationapp.utils.ProgressDialog(mContext);

        mFirstNameEditText = (EditText) findViewById(R.id.add_card_view_first_name);
        mLastNameEditText = (EditText) findViewById(R.id.add_card_view_last_name);
        mCardEditText = (EditText) findViewById(R.id.add_card_view_card_number);
        mExpiryEditText = (EditText) findViewById(R.id.add_card_view_expiry);
        mCvvEditText = (EditText) findViewById(R.id.add_card_view_cvv);
        mAddressEditText1 = (EditText) findViewById(R.id.add_card_view_address_line_1);
        mAddressEditText2 = (EditText) findViewById(R.id.add_card_view_address_line_2);
        mCityEditText = (EditText) findViewById(R.id.add_card_view_city);
        mStateEditText = (EditText) findViewById(R.id.add_card_view_state);
        mZipEditText = (EditText) findViewById(R.id.add_card_view_zip);

        setUpFocus();

        textChangedListener();


    }

    /**
     * text Changed Listener
     */
    private void textChangedListener() {

        mCardEditText.addTextChangedListener(new TextWatcher() {


            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 19; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = ' ';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // noop
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // chech that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }


            private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }
                Log.d("valuestring", "" + formatted.toString());
                return formatted.toString();

            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];

                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                Log.d("digits", "" + digits);
                return digits;


            }
        });

//        mCardEditText.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                boolean flag = true;
//                String eachBlock[] = getEditTextValue(mCardEditText).split("-");
//                for (int i = 0; i < eachBlock.length; i++) {
//                    if (eachBlock[i].length() > 4) {
//                        flag = false;
//                    }
//                }
//                if (flag) {
//
//                    mCardEditText.setOnKeyListener(new View.OnKeyListener() {
//
//                        @Override
//                        public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                            if (keyCode == KeyEvent.KEYCODE_DEL)
//                                keyDel = 1;
//                            return false;
//                        }
//                    });
//
//                    if (keyDel == 0) {
//
//                        if (((getEditTextLength(mCardEditText) + 1) % 5) == 0) {
//
//                            if (getEditTextValue(mCardEditText).split("-").length <= 3) {
//                                mCardEditText.setText(getEditTextValue(mCardEditText) + "-");
//                                mCardEditText.setSelection(getEditTextLength(mCardEditText));
//                            }
//                        }
//                        cardnumber = getEditTextValue(mCardEditText);
//                    } else {
//                        cardnumber = getEditTextValue(mCardEditText);
//                        keyDel = 0;
//                    }
//
//                } else {
//                    mCardEditText.setText(cardnumber);
//                }
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

        /* expiry date format*/
        mExpiryEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean flag = true;
                String eachBlock[] = getEditTextValue(mExpiryEditText).split("/");
                for (int i = 0; i < eachBlock.length; i++) {
                    if (eachBlock[i].length() > 2) {
                        flag = false;
                    }
                }
                if (flag) {

                    mExpiryEditText.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_DEL)
                                mKeyDeleteExpiry = 1;
                            return false;
                        }
                    });

                    if (mKeyDeleteExpiry == 0) {
                        if (((getEditTextLength(mExpiryEditText) + 1) % 3) == 0) {
                            if (getEditTextLength(mExpiryEditText) <= 3) {
                                mExpiryEditText.setText(getEditTextValue(mExpiryEditText) + "/");
                                mExpiryEditText.setSelection(getEditTextLength(mExpiryEditText));
                            }
                        }
                        mExpiryDate = getEditTextValue(mExpiryEditText);
                    } else {
                        mExpiryDate = getEditTextValue(mExpiryEditText);
                        mKeyDeleteExpiry = 0;
                    }
                } else {
                    mExpiryEditText.setText(mExpiryDate);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mZipEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    enrollPan();
                }
                return false;
            }
        });
    }


    public void onCameraClicked(View view) {
        Intent scanIntent = new Intent(this, CardIOActivity.class);
        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false

        // MY_SCAN_REQUEST_CODE is arbitrary and is only used within this activity.
        startActivityForResult(scanIntent, SCAN_REQUEST_CODE);
    }


    public void onEnrollClicked(View view) {

        enrollPan();

    }

    private void enrollPan() {

        if (!NetworkManager.checkInternet(mContext))
            return;

        //TODO why this? already we have Prefrence singletone class
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("cardnumber", getEditTextValue(mCardEditText).replace(" ", "").trim());
        editor.putString("cvv", getEditTextValue(mCvvEditText));
        editor.putString("expiry", getEditTextValue(mExpiryEditText));
        editor.commit();
        if (getEditTextLength(mFirstNameEditText) == 0) {
            mFirstNameEditText.requestFocus();
            mFirstNameEditText.setError("ENTER YOUR FIRST NAME");
            return;
        } else if (getEditTextLength(mLastNameEditText) == 0) {
            mLastNameEditText.requestFocus();
            mLastNameEditText.setError("ENTER YOUR LAST NAME");
            return;
        } else if (getEditTextLength(mCardEditText) == 0) {
            mCardEditText.requestFocus();
            mCardEditText.setError("CARD NUMBER FIELD CANNOT BE EMPTY");
            Log.d("AddcardActivity", "Card number  is empty ");
            return;
        } else if (!getEditTextValue(mCardEditText).substring(0, 1).toString().trim().equals("4") || Utils.isCardValid(getEditTextValue(mCardEditText))) {
            mCardEditText.requestFocus();
            mCardEditText.setError("ENTER YOUR VALID 16 DIGIT VISA CARD NUMBER TO PROCEED ");
            return;
        } else if (getEditTextLength(mExpiryEditText) == 0) {
            mExpiryEditText.requestFocus();
            mExpiryEditText.setError("EXPIRY DATE FIELD CANNOT BE EMPTY");
            return;
        } else if (!getEditTextValue(mExpiryEditText).matches("(?:0[1-9]|1[0-2])/[0-9]{2}")) {
            mExpiryEditText.requestFocus();
            mExpiryEditText.setError("ENTER VALID EXPIRY DATE IN MM/YY FORMAT ");
            return;
        } else if (getEditTextLength(mCvvEditText) == 0) {
            mCvvEditText.requestFocus();
            mCvvEditText.setError("CVV FIELD CANNOT BE EMPTY");
            return;
        } else if (getEditTextLength(mCvvEditText) < 3) {
            mCvvEditText.requestFocus();
            mCvvEditText.setError("ENTER YOUR 3 DIGIT VALID CVV NUMBER");
            return;
        } else if (getEditTextLength(mAddressEditText1) == 0 && getEditTextLength(mAddressEditText2) == 0) {
            mAddressEditText1.requestFocus();
            mAddressEditText1.setError("ENTER YOUR ADDRESS");
            return;
        } else if (getEditTextLength(mCityEditText) == 0) {
            mCityEditText.requestFocus();
            mCityEditText.setError("ENTER YOUR CITY");
            return;
        } else if (getEditTextLength(mStateEditText) == 0) {
            mStateEditText.requestFocus();
            mStateEditText.setError("ENTER YOUR STATE");
            return;
        } else if (getEditTextLength(mZipEditText) == 0) {
            mZipEditText.requestFocus();
            mZipEditText.setError("ENTER YOUR ZIP CODE");
            return;
        }


        executeEnrollPan();

    }

    /**
     * Execute enrollpan
     */
    private void executeEnrollPan() {
        mProgressDialog.show();

        mProgressDialog.setOnDismissListener(new com.visa.vts.certificationapp.utils.ProgressDialog.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                navigateToNextPage();
            }
        });
        final EnrollPanRequest enrollPanRequest = createEnrollPanRequest();
        BackendEndPoints service = RestController.getApi();
        Call<EnrollPanResponse> call = service.enrollPan(enrollPanRequest);
        call.enqueue(new Callback<EnrollPanResponse>() {

            @Override
            public void onResponse(Response<EnrollPanResponse> response, Retrofit retrofit) {
                try {
                    Utils.printLog("Response Code " + response.code());
                    mResponseStatus = response.code();

                    mResponseBody = Preference.getInstance().retrieveString(Constants.PREFERENCE_RESPONSEBODY);
                    String coRrelationID = response.headers().get("X-CORRELATION-ID");
                    Utils.printLog("EnrollPAN X-CORRELATION-ID " + coRrelationID);

                    // Gson gson = new Gson();
                    //EnrollPanResponse enrollPanResponse = gson.fromJson(mResponseBody,EnrollPanResponse.class);

                    //String tncId = enrollPanResponse.getCardMetaData().getTermsAndConditionsID();
                    if (response.body() != null) {
                        mTermsAndConditionsID = response.body().getCardMetaData().getTermsAndConditionsID();
                        Utils.printLog("EnrollPAN TermsAndConditionsID " + mTermsAndConditionsID);
                        mVPanEnrollmentID = response.body().getvPanEnrollmentID();
                        String guid = response.body().getCardMetaData().getCardData().get(0).getGuid();
                        Preference.getInstance().saveString(Constants.guid, guid);
                        Preference.getInstance().saveString(Constants.vPanEnrollmentID, mVPanEnrollmentID);

                        Utils.printLog("EnrollPAN mVPanEnrollmentID " + mVPanEnrollmentID + "guid" + guid);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onFailure(Throwable cause) {
                cause.getLocalizedMessage();
                cause.getCause();
                Log.v("TAG", " Cause for failure" + cause.toString());
                Log.v("TAG", "failure cause" + cause.getCause());
                mProgressDialog.dismiss();


            }
        });

    }

    /**
     * create Enroll PanRequest
     *
     * @return EnrollPanRequest
     */
    private EnrollPanRequest createEnrollPanRequest() {
        mPaymentInstrument = new PaymentInstrument();

        String NewAccountWithoutSpaces = getEditTextValue(mCardEditText).replaceAll("[\\D]", "");
        mPaymentInstrument.setAccountNumber(NewAccountWithoutSpaces);
        String cvv2 = getEditTextValue(mCvvEditText);
        if (cvv2.isEmpty())
            cvv2 = null;

        mPaymentInstrument.setCvv2(cvv2);

        ExpirationDate expirationDate = new ExpirationDate();
        String expiry = getEditTextValue(mExpiryEditText);
        String month = expiry.substring(0, 2);
        String year = "20" + expiry.substring(3, 5);
        expirationDate.setMonth(month);
        expirationDate.setYear(year);
        mPaymentInstrument.setExpirationDate(expirationDate);

        mBillingAddress = new BillingAddress();
        mBillingAddress.setLine1(getEditTextValue(mAddressEditText1));
        mBillingAddress.setLine2(getEditTextValue(mAddressEditText2));
        mBillingAddress.setState(getEditTextValue(mStateEditText));
        mBillingAddress.setPostalCode(getEditTextValue(mZipEditText));
        mBillingAddress.setCity(getEditTextValue(mCityEditText));

        mPaymentInstrument.setBillingAddress(mBillingAddress);

        //POJO to String
        String paymentInstrument_String = null;
        try {
            Gson gson = new Gson();
            paymentInstrument_String = gson.toJson(mPaymentInstrument, PaymentInstrument.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Plain paymentInstrument_String to EncryptedpaymentInstrument
        Utils.printLog("Before encrypt:" + paymentInstrument_String);
        String encPaymentInstrument = JWTFactory.createJwtFromEnvironment(getApplicationContext(), paymentInstrument_String);

        EnrollPanRequest enrollPanRequest = null;
        try {
//            int tag = 21;
//            byte[] panByteArray  = paymentInstrument_String.getBytes(StandardCharsets.UTF_8);
//
//            // Prepend tag ( 1 byte ) to the data that needs to be encrypted.
//            byte[] panTVByteArray = new byte[1 + panByteArray.length];
//            System.arraycopy(new byte[] {(byte)(tag & 0xff)},0,panTVByteArray,0,1);
//            System.arraycopy(panByteArray,0,panTVByteArray,1,panByteArray.length);
//
//            enrollPanRequest = visaPaymentSDK.constructEnrollRequest(panTVByteArray);

            enrollPanRequest = new EnrollPanRequest();
            enrollPanRequest.setEncPaymentInstrument(encPaymentInstrument);

        } catch (Exception e) {
            e.printStackTrace();
        }

        enrollPanRequest.setClientAppID(Constants.SANDBOX_EXTERNAL_APPID);
        enrollPanRequest.setPanSource("MANUALLYENTERED");
        enrollPanRequest.setConsumerEntryMode("KEYENTERED");
        enrollPanRequest.setLocale("en_US");

        Gson gson = new Gson();
        String enrollpanrequest = gson.toJson(enrollPanRequest, EnrollPanRequest.class);

        Utils.printLog("enrollPanRequest: " + enrollpanrequest);
        return enrollPanRequest;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SCAN_REQUEST_CODE) {
            String resultDisplayStr;
            if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                // Never log a raw card number. Avoid displaying it, but if necessary use getFormattedCardNumber()
                resultDisplayStr = "Card Number: " + scanResult.getRedactedCardNumber() + "\n";
                mCardEditText.setText(scanResult.getFormattedCardNumber());
                // Do something with the raw number, e.g.:
                // myService.setCardNumber( scanResult.cardNumber );
                if (scanResult.isExpiryValid()) {
                    mExpiryEditText.setText(scanResult.expiryMonth + "/" + scanResult.expiryYear);
                    resultDisplayStr += "Expiration Date: " + scanResult.expiryMonth + "/" + scanResult.expiryYear + "\n";
                }

                if (scanResult.cvv != null) {
                    mCvvEditText.setText(scanResult.cvv);
                    // Never log or display a CVV
                    resultDisplayStr += "CVV has " + scanResult.cvv.length() + " digits.\n";
                }

                if (scanResult.postalCode != null) {
                    resultDisplayStr += "Postal Code: " + scanResult.postalCode + "\n";
                }
            } else {
                resultDisplayStr = "Scan was canceled.";
            }

        }
    }


    private void setUpFocus() {
        mFirstNameEditText.setNextFocusDownId(R.id.add_card_view_last_name);
        mLastNameEditText.setNextFocusDownId(R.id.add_card_view_card_number);
        mCardEditText.setNextFocusDownId(R.id.add_card_view_expiry);
        mExpiryEditText.setNextFocusDownId(R.id.add_card_view_cvv);
        mCvvEditText.setNextFocusDownId(R.id.add_card_view_address_line_1);
        mAddressEditText1.setNextFocusDownId(R.id.add_card_view_address_line_2);
        mAddressEditText2.setNextFocusDownId(R.id.add_card_view_city);
        mCityEditText.setNextFocusDownId(R.id.add_card_view_state);
        mStateEditText.setNextFocusDownId(R.id.add_card_view_zip);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event " + event.getRawX() + "," + event.getRawY() + " " + x + "," + y + " rect " + w.getLeft() + "," + w.getTop() + "," + w.getRight() + "," + w.getBottom() + " coords " + scrcoords[0] + "," + scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    /**
     * navigate To NextPage
     * this function will decide whether log page should show or
     * take the user to next screen
     */
    private void navigateToNextPage() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TERMSCONDITIONID, mTermsAndConditionsID);
        bundle.putString(Constants.PANENROLLMENTID, mVPanEnrollmentID);
        bundle.putString(Constants.ACCOUNT_HOLDER_NAME, mPaymentInstrument.getName());
        //bundle.putSerializable(Constants.UNSUPPORTED_PRESENTATIONTYPES, response.body().getCardMetaData().getUnsupportedPresentationTypes());
        //bundle.putSerializable(Constants.UNSUPPORTED_PRESENTATIONTYPES, null);
        bundle.putString(Constants.CVV, mPaymentInstrument.getCvv2());
        bundle.putString(Constants.BILLING_ZIP, mBillingAddress.getPostalCode());
        bundle.putString(Constants.ADDRESS_LINE_1, mBillingAddress.getLine1());

        if (Preference.getInstance().retrieveBoolean(Constants.PREFERENCE_ENABLELOG)) {
            Session.getInstance().appFlow = Constants.FLOW_ADD_A_CARD;
            showLogScreen(bundle, mResponseStatus, Constants.REQUEST_ENROLL_PAN, mResponseBody);
        } else {
            if (mResponseStatus == Constants.STATUS_SUCCESS || mResponseStatus == Constants.STATUS_SUCCESS1) {
                Intent intent = new Intent(this, TermsAndConditionActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                AlertDialogSingleButton.showAlert(mContext, "Enroll Pan failed", new AlertDialogSingleButton.setOnNeutralButtonClick() {
                    @Override
                    public void onNeutralButton(DialogInterface dialog) {

                    }
                });
            }
        }

    }

    /**
     * get EditText Value
     *
     * @param EditText
     * @return string
     */
    private String getEditTextValue(EditText EditText) {
        return EditText.getText().toString().trim();
    }

    /**
     * get EditText Character Length
     *
     * @param EditText
     * @return int
     */
    private int getEditTextLength(EditText EditText) {
        return EditText.getText().toString().trim().length();
    }
}
