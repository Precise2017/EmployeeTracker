package com.trackkarlo.employeetracker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class Emp_RegisterActivity extends Activity {

    EditText edtTextFName, edtTextLName, edtTextMobile, edtTextEmail, edtTextPassword, edtTextConfirmPassword, edtCompany;
    Button btnregister;
    private ProgressBar mRegistrationProgressBar;
    TextView txtlogin;

    final private int REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON = 3245;
    final private int PHONE_STATE_PERMISSION_REQUEST_CODE = 6234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emp_activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        edtTextFName = (EditText) findViewById(R.id.edtTextFName);
        edtTextLName = (EditText) findViewById(R.id.edtTextLName);
        edtTextMobile = (EditText) findViewById(R.id.edtTextMobile);
        edtTextEmail = (EditText) findViewById(R.id.edtTextEmail);
        edtTextPassword = (EditText) findViewById(R.id.edtTextPassword);
        edtTextConfirmPassword = (EditText) findViewById(R.id.edtTextConfirmPassword);
        edtCompany = (EditText) findViewById(R.id.edtCompany);
        btnregister = (Button) findViewById(R.id.btnregister);
        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationProgressBar.setVisibility(View.GONE);
        txtlogin = (TextView) findViewById(R.id.txtlogin);

        txtlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtTextFName.getWindowToken(), 0);

                if (!validateFname()) {
                    return;
                }
                if (!validateLname()) {
                    return;
                }

                if (!validateMobile()) {
                    return;
                }

                if (!validateEmail()) {
                    return;
                }

                if (!validatePassword()) {
                    return;
                }

                if (!matchPassword()) {
                    return;
                }

                if (!validateCompany()) {
                    return;
                }

                askForPermission(Manifest.permission.READ_PHONE_STATE, REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON);
            }
        });
    }

    private void askForPermission(String permission, Integer requestCode) {
        switch (requestCode) {
            case REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON:
                if (ContextCompat.checkSelfPermission(Emp_RegisterActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Emp_RegisterActivity.this, permission)) {
                        ActivityCompat.requestPermissions(Emp_RegisterActivity.this, new String[]{permission}, requestCode);
                    } else {
                        ActivityCompat.requestPermissions(Emp_RegisterActivity.this, new String[]{permission}, requestCode);
                    }
                } else {
                    registerNowAll();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON:
                    registerNowAll();
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON:

                    if (ActivityCompat.shouldShowRequestPermissionRationale(Emp_RegisterActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                        ActivityCompat.requestPermissions(Emp_RegisterActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, requestCode);
                        return;
                    }

                    showMessageOKCancelStorage("You need to allow access to phone state permission");

                    break;
            }
        }
    }


    private void showMessageOKCancelStorage(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Emp_RegisterActivity.this);
        alertDialogBuilder.setTitle("Permission Alert");
        alertDialogBuilder
                .setMessage(message)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, PHONE_STATE_PERMISSION_REQUEST_CODE);
                        Emp_RegisterActivity.this.overridePendingTransition(0, 0);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHONE_STATE_PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(Manifest.permission.READ_PHONE_STATE, REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON);
                } else {
                    registerNowAll();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(Manifest.permission.READ_PHONE_STATE, REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON);
                } else {
                    registerNowAll();
                }
            }
        }
    }


    private void registerNowAll() {

        String fname = edtTextFName.getText().toString().replaceAll(" ", "");
        String lname = edtTextLName.getText().toString().replaceAll(" ", "");
        String email = edtTextEmail.getText().toString().replaceAll(" ", "");
        String mobile = edtTextMobile.getText().toString().replaceAll(" ", "");
        String company = edtCompany.getText().toString().replaceAll(" ", "");

        TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tMgr.getDeviceId();

        doRegister(fname, lname, email, edtTextPassword.getText().toString(), mobile, deviceId, company);
    }

    private boolean validatePassword() {
        Pattern pSpecial = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

        if (edtTextPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
            requestFocus(edtTextPassword);
            return false;
        } else if (edtTextPassword.getText().toString().length() < 6) {
            Toast.makeText(getApplicationContext(), "Password length should be six digits", Toast.LENGTH_SHORT).show();
            requestFocus(edtTextPassword);
            return false;
        } else if (!pSpecial.matcher(edtTextPassword.getText().toString()).find()) {
            Toast.makeText(getApplicationContext(), "Password should have at least one special character", Toast.LENGTH_SHORT).show();
            requestFocus(edtTextPassword);
            return false;
        } else if (!edtTextPassword.getText().toString().matches(".*\\d+.*")) {
            Toast.makeText(getApplicationContext(), "Password should have at least one number", Toast.LENGTH_SHORT).show();
            requestFocus(edtTextPassword);
            return false;
        }
        return true;
    }

    private boolean matchPassword() {
        if (!edtTextPassword.getText().toString().equals(edtTextConfirmPassword.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Password not matched", Toast.LENGTH_SHORT).show();
            requestFocus(edtTextPassword);
            return false;
        }
        return true;
    }


    private boolean validateEmail() {
        if (edtTextEmail.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter email", Toast.LENGTH_SHORT).show();
            requestFocus(edtTextEmail);
            return false;
        } else if (!isValidEmail(edtTextEmail.getText().toString())) {
            Toast.makeText(getApplicationContext(), "Please enter valid email", Toast.LENGTH_SHORT).show();
            requestFocus(edtTextEmail);
            return false;
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateFname() {
        if (edtTextFName.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter first name", Toast.LENGTH_SHORT).show();
            requestFocus(edtTextFName);
            return false;
        }
        return true;
    }

    private boolean validateCompany() {
        if (edtCompany.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter company code", Toast.LENGTH_SHORT).show();
            requestFocus(edtCompany);
            return false;
        }
        return true;
    }

    private boolean validateMobile() {
        if (edtTextMobile.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter mobile number", Toast.LENGTH_SHORT).show();
            requestFocus(edtTextMobile);
            return false;
        } else if (edtTextMobile.getText().toString().length() < 10) {
            Toast.makeText(getApplicationContext(), "Please enter ten digits mobile number", Toast.LENGTH_SHORT).show();
            requestFocus(edtTextMobile);
            return false;
        }
        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateLname() {
        if (edtTextLName.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter last name", Toast.LENGTH_SHORT).show();
            requestFocus(edtTextLName);
            return false;
        }
        return true;
    }

    private void doRegister(final String firstname, final String lastname, final String email, final String password, final String phone, final String imei, final String companycode) {
        final Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String url = "http://46.4.94.41/mobiletracker/api/Registration/MobileRegistration";
                    HttpPost httpost = new HttpPost(url);

                    String urlParameters = "{IMEI:" + "\"" + imei + "\"" + ",FirstName:" + "\"" + firstname + "\"" + ",LastName:" + "\"" + lastname + "\"" +
                            ",Email:" + "\"" + email + "\"" + ",MobileNo:" + "\"" + phone + "\"" + ",Password :" + "\"" + password + "\"" + ",CompanyCode :" + "\"" + companycode + "\"" + "}";

                    System.out.println("urlParameters--" + urlParameters);

                    httpost.setEntity(new StringEntity(urlParameters));

                    httpost.setHeader("Accept", "application/json");
                    httpost.setHeader("Content-type", "application/json");

                    HttpResponse response;

                    response = httpClient.execute(httpost);
                    HttpEntity resEntity = response.getEntity();

                    if (resEntity != null) {
                        String responseStr = EntityUtils.toString(resEntity).trim();
                        System.out.println("responseStr---" + responseStr);
                        JSONObject jsonObject = new JSONObject(responseStr.toString());

                        if (jsonObject.get("msg") instanceof String) {
                            String strMessage = jsonObject.getString("msg");

                            if (strMessage.contains("Successfully") || strMessage.contains("successfully")) {
                                final Message msg = new Message();
                                final Bundle b = new Bundle();
                                b.putString("status", "true");
                                b.putString("strMessage", strMessage);
                                msg.setData(b);
                                handler.sendMessage(msg);
                            } else {
                                final Message msg = new Message();
                                final Bundle b = new Bundle();
                                b.putString("status", "false");
                                b.putString("strMessage", strMessage);
                                msg.setData(b);
                                handler.sendMessage(msg);
                            }

                        } else {
                            final Message msg = new Message();
                            final Bundle b = new Bundle();
                            b.putString("status", "false");
                            b.putString("strMessage", "Can't Register");
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    final Message msg = new Message();
                    final Bundle b = new Bundle();
                    b.putString("status", "false");
                    b.putString("strMessage", "Can't Register");
                    msg.setData(b);
                    handler.sendMessage(msg);
                }
            }
        };

        if (isNetworkAvailable()) {
            mRegistrationProgressBar.setVisibility(View.VISIBLE);
            thread.start();
        } else {
            Toast.makeText(Emp_RegisterActivity.this, "Please Check Your Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
    }


    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (mRegistrationProgressBar != null) {
                mRegistrationProgressBar.setVisibility(View.GONE);
            }

            Bundle b = msg.getData();
            if (b.getString("status") != null && b.getString("status").equalsIgnoreCase("true")) {
                String strMessage = b.getString("strMessage");
                Toast.makeText(Emp_RegisterActivity.this, strMessage,
                        Toast.LENGTH_SHORT).show();

                finish();
            } else {
                String strMessage = b.getString("strMessage");
                Toast.makeText(Emp_RegisterActivity.this, strMessage,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}