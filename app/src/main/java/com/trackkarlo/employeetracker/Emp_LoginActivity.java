package com.trackkarlo.employeetracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.Log;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.trackkarlo.employeetracker.db.Emp_UserTableData;
import com.trackkarlo.employeetracker.utils.Emp_GPSTracker;
import com.trackkarlo.employeetracker.utils.Emp_PrefManager;

public class Emp_LoginActivity extends AppCompatActivity {

   String str="test_updated";
    EditText edtTextEmail;
    EditText edtTextPassword;

    Button btnLogin;

    private Emp_PrefManager prefManager;
    private Typeface custom_fontRegular;
    private Typeface custom_fontBold;

    private ProgressBar mRegistrationProgressBar;

    final private int REQUEST_STORAGE_PERMISSIONS_ON_BUTTON = 3245;
    final private int STORAGE_PERMISSION_REQUEST_CODE = 6234;

    final private int REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON = 4453;
    final private int PHONE_STATE_PERMISSION_REQUEST_CODE = 4999;

    TextView txtregister;

    String strUSER_INFO_LOGGED_IN = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emp_activity_login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        prefManager = new Emp_PrefManager(this);

        Cursor cursor = getContentResolver().query(Emp_UserTableData.CONTENT_URI_USER_INFO, null, null, null, null);
        if (cursor != null) {
            System.out.println("USER_cursor --" + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    strUSER_INFO_LOGGED_IN = cursor.getString(cursor.getColumnIndex(Emp_UserTableData.UserTable.USER_INFO_LOGGED_IN));

                    System.out.println("strUSER_INFO_LOGGED_IN --" + strUSER_INFO_LOGGED_IN);
                }
                while (cursor.moveToNext());
            }
        }

        if (strUSER_INFO_LOGGED_IN != null && strUSER_INFO_LOGGED_IN.equalsIgnoreCase("1")) {
            startActivity(new Intent(Emp_LoginActivity.this, Emp_HomeScreen.class));
            Emp_LoginActivity.this.overridePendingTransition(0, 0);
            finish();
        }

        custom_fontRegular = Typeface.createFromAsset(getAssets(), "font/" + prefManager.getRegularFontFamilySelected());
        custom_fontBold = Typeface.createFromAsset(getAssets(), "font/" + prefManager.getBoldFamilySelected());

        edtTextEmail = (EditText) findViewById(R.id.edtTextEmail);
        edtTextPassword = (EditText) findViewById(R.id.edtTextPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationProgressBar.setVisibility(View.GONE);

        edtTextEmail.setTypeface(custom_fontRegular);
        edtTextPassword.setTypeface(custom_fontRegular);
        btnLogin.setTypeface(custom_fontRegular);

        txtregister = (TextView) findViewById(R.id.txtregister);
        txtregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Emp_LoginActivity.this, Emp_RegisterActivity.class);
                startActivity(i);
                Emp_LoginActivity.this.overridePendingTransition(0, 0);

              /*  *//*startActivity(new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
                        Uri.parse("package:" + getPackageName())));*//*

                *//*Intent myIntent = new Intent();
                myIntent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                startActivity(myIntent);*//*

               *//* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Intent intent = new Intent();
                    String packageName = getPackageName();
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                    if (pm.isIgnoringBatteryOptimizations(packageName))
                        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    else {
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + packageName));
                    }
                    startActivity(intent);
                }*//*

                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                GsmCellLocation cellLocation = (GsmCellLocation)telephonyManager.getCellLocation();

                String networkOperator = telephonyManager.getNetworkOperator();
                String mcc = networkOperator.substring(0, 3);
                String mnc = networkOperator.substring(3);
                String operatorname = telephonyManager.getNetworkOperatorName();
                String manufacturer = Build.MANUFACTURER;
                String model = Build.MODEL;
               // textMCC.setText("mcc: " + mcc);
               // textMNC.setText("mnc: " + mnc);

                int cid = cellLocation.getCid();
                int lac = cellLocation.getLac();
                Log.i("sdsa",cellLocation.toString());
             //   textCID.setText("gsm cell id: " + String.valueOf(cid));
              //  textLAC.setText("gsm location area code: " + String.valueOf(lac));

               // getLocationfromGCM();

               *//* String manufacturer = "xiaomi";
                if (manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
                    //this will open auto start screen where user can enable permission for your app
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                    startActivity(intent);
                }*/
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtTextEmail.getWindowToken(), 0);
                if (!validateEmail()) {
                    return;
                }

                if (!validatePassword()) {
                    return;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSIONS_ON_BUTTON);
                } else {
                    loginNowAll();
                }
            }
        });
    }


    private void askForPermission(String permission, Integer requestCode) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSIONS_ON_BUTTON:
                if (ContextCompat.checkSelfPermission(Emp_LoginActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Emp_LoginActivity.this, permission)) {
                        //This is called if user has denied the permission before
                        //In this case I am just asking the permission again
                        ActivityCompat.requestPermissions(Emp_LoginActivity.this, new String[]{permission}, requestCode);

                    } else {
                        ActivityCompat.requestPermissions(Emp_LoginActivity.this, new String[]{permission}, requestCode);
                    }
                } else {
                    checkForPhoneStatePermission();
                }
                break;

            case REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON:
                if (ContextCompat.checkSelfPermission(Emp_LoginActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Emp_LoginActivity.this, permission)) {
                        //This is called if user has denied the permission before
                        //In this case I am just asking the permission again
                        ActivityCompat.requestPermissions(Emp_LoginActivity.this, new String[]{permission}, requestCode);

                    } else {
                        ActivityCompat.requestPermissions(Emp_LoginActivity.this, new String[]{permission}, requestCode);
                    }
                } else {
                    loginNowAll();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_STORAGE_PERMISSIONS_ON_BUTTON:
                    checkForPhoneStatePermission();
                    break;

                case REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON:
                    loginNowAll();
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_STORAGE_PERMISSIONS_ON_BUTTON:

                    if (ActivityCompat.shouldShowRequestPermissionRationale(Emp_LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(Emp_LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                        return;
                    }

                    showMessageOKCancelStorage("You need to allow access to storage permission");

                    break;

                case REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON:

                    if (ActivityCompat.shouldShowRequestPermissionRationale(Emp_LoginActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                        ActivityCompat.requestPermissions(Emp_LoginActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, requestCode);
                        return;
                    }

                    showMessageOKCancelPhoneState("You need to allow access to phone state permission");

                    break;
            }
        }
    }


    private void showMessageOKCancelStorage(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Emp_LoginActivity.this);
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
                        startActivityForResult(intent, STORAGE_PERMISSION_REQUEST_CODE);
                        Emp_LoginActivity.this.overridePendingTransition(0, 0);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showMessageOKCancelPhoneState(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Emp_LoginActivity.this);
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
                        Emp_LoginActivity.this.overridePendingTransition(0, 0);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
//    - worked on employee tracker application
//    - created new app for employee tracking
//    - made some design changes
//    - tested all api in application


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,REQUEST_STORAGE_PERMISSIONS_ON_BUTTON);
//                }
//                else {
//                    checkForPhoneStatePermission();
//                }
//            }
//            if (resultCode == RESULT_CANCELED) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,REQUEST_STORAGE_PERMISSIONS_ON_BUTTON);
//                }
//                else {
//                    checkForPhoneStatePermission();
//                }
//            }
//        }
//        else if (requestCode == PHONE_STATE_PERMISSION_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    askForPermission(Manifest.permission.READ_PHONE_STATE,REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON);
//                }
//                else {
//                    loginNowAll();
//                }
//            }
//            if (resultCode == RESULT_CANCELED) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    askForPermission(Manifest.permission.READ_PHONE_STATE,REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON);
//                }
//                else {
//                    loginNowAll();
//                }
//            }
//        }
//    }


    private void checkForPhoneStatePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForPermission(Manifest.permission.READ_PHONE_STATE, REQUEST_PHONE_STATE_PERMISSIONS_ON_BUTTON);
        } else {
            loginNowAll();
        }
    }


    private void loginNowAll() {
        Emp_GPSTracker gps = new Emp_GPSTracker(this);
        if (gps.canGetLocation()) {
            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String deviceId = tMgr.getDeviceId();

            doLogin(edtTextEmail.getText().toString(), edtTextPassword.getText().toString(), deviceId);
        } else {
            gps.showSettingsAlert();
        }
    }

    private void doLogin(final String email, final String password, final String deviceId) {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String url = "http://46.4.94.41/mobiletracker/api/Login/getMobileLogin/" + "?EmailId=" +
                            email + "&Password=" + password + "&IMEI=" + deviceId;
                    System.out.println("urlParameters--" + url);
                    HttpGet httpost = new HttpGet(url);

                    HttpResponse response;

                    response = httpClient.execute(httpost);
                    HttpEntity resEntity = response.getEntity();

                    if (resEntity != null) {
                        String responseStr = EntityUtils.toString(resEntity).trim();

                        System.out.println("responseStr---" + responseStr);

                        JSONObject jsonObject = new JSONObject(responseStr.toString());

                        Integer responseToCheck = jsonObject.getInt("response");
                        if (responseToCheck == 0) {
                            String strMessage = "Login failed";

                            final Message msg = new Message();
                            final Bundle b = new Bundle();
                            b.putString("status", "false");
                            b.putString("strMessage", strMessage);
                            msg.setData(b);
                            handler.sendMessage(msg);
                        } else if (responseToCheck == 1) {
                            String strMessage = "Login failed";

                            final Message msg = new Message();
                            final Bundle b = new Bundle();
                            b.putString("status", "false");
                            b.putString("strMessage", strMessage);
                            msg.setData(b);
                            handler.sendMessage(msg);
                        } else if (responseToCheck == 2) {
                            String UserName = jsonObject.getString("UserName");
                            String UserId = jsonObject.getString("UserId");
                            String IMEI = jsonObject.getString("IMEI");
                            String Email = jsonObject.getString("Email");
                            String UserImage = "";
                            String UserType = "";
                            String strMessage = "Successfully login";

                            final Message msg = new Message();
                            final Bundle b = new Bundle();
                            b.putString("status", "true");
                            b.putString("UserName", UserName);
                            b.putString("UserId", UserId);
                            b.putString("IMEI", IMEI);
                            b.putString("Email", Email);
                            b.putString("UserImage", UserImage);
                            b.putString("UserType", UserType);
                            b.putString("strMessage", strMessage);
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    final Message msg = new Message();
                    final Bundle b = new Bundle();
                    b.putString("status", "false");
                    b.putString("strMessage", "Can't Login");
                    msg.setData(b);
                    handler.sendMessage(msg);
                }
            }
        };

        if (isNetworkAvailable()) {
            mRegistrationProgressBar.setVisibility(View.VISIBLE);
            thread.start();
        } else {
            Toast.makeText(Emp_LoginActivity.this, "Please Check Your Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
    }


    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle b = msg.getData();
            if (b.getString("status") != null && b.getString("status").equalsIgnoreCase("true")) {
                String UserId = b.getString("UserId");
                String UserName = b.getString("UserName");
                String IMEI = b.getString("IMEI");
                String Email = b.getString("Email");
                String UserImage = b.getString("UserImage");
                String UserType = b.getString("UserType");

//800896227D4 1800896227D4 1800896227D4
                ContentValues values = new ContentValues();
                values.put(Emp_UserTableData.UserTable.USER_INFO_USER_ID, UserId);
                values.put(Emp_UserTableData.UserTable.USER_INFO_NAME, UserName);
                values.put(Emp_UserTableData.UserTable.USER_INFO_IMEI, IMEI);
                values.put(Emp_UserTableData.UserTable.USER_INFO_EMAIL, Email);
                values.put(Emp_UserTableData.UserTable.USER_INFO_IMAGE, UserImage);
                values.put(Emp_UserTableData.UserTable.USER_INFO_TYPE, UserType);
                values.put(Emp_UserTableData.UserTable.USER_INFO_LOGGED_IN, "1");
                getContentResolver().insert(Emp_UserTableData.CONTENT_URI_USER_INFO, values);

                String strMessage = b.getString("strMessage");
                Toast.makeText(Emp_LoginActivity.this, strMessage,
                        Toast.LENGTH_LONG).show();

                startActivity(new Intent(Emp_LoginActivity.this, Emp_HomeScreen.class));
                Emp_LoginActivity.this.overridePendingTransition(0, 0);
                finish();
            } else {
                String strMessage = b.getString("strMessage");
                Toast.makeText(Emp_LoginActivity.this, strMessage,
                        Toast.LENGTH_LONG).show();
            }

            if (mRegistrationProgressBar != null) {
                mRegistrationProgressBar.setVisibility(View.GONE);
            }
        }
    };

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private boolean validatePassword() {
        if (edtTextPassword.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
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

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void getLocationfromGCM() {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String url = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyBeGMIL8hOpvn-dVTpCOP2GPYPWtDfwZXY";
                    System.out.println("urlParameters--" + url);
                    HttpGet httpost = new HttpGet(url);

                    HttpResponse response;

                    response = httpClient.execute(httpost);
                    HttpEntity resEntity = response.getEntity();

                    if (resEntity != null) {
                        String responseStr = EntityUtils.toString(resEntity).trim();
                        System.out.println("responseStr---" + responseStr);
                        JSONObject jsonObject = new JSONObject(responseStr.toString());
                        Integer responseToCheck = jsonObject.getInt("response");
                        if (responseToCheck == 0) {
                            String strMessage = "Login failed";
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    final Message msg = new Message();
                    final Bundle b = new Bundle();
                    b.putString("status", "false");
                    b.putString("strMessage", "Can't Login");
                    msg.setData(b);
                    handler11.sendMessage(msg);
                }
            }
        };

        if (isNetworkAvailable()) {
            thread.start();
        } else {
            Toast.makeText(Emp_LoginActivity.this, "Please Check Your Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
    }


    protected Handler handler11 = new Handler() {
        @Override
        public void handleMessage(Message msg) {


        }
    };

}
