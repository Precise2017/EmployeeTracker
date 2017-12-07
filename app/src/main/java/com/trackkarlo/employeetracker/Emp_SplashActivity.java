package com.trackkarlo.employeetracker;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.trackkarlo.employeetracker.db.Emp_UserTableData;
import com.trackkarlo.employeetracker.utils.Emp_Constants;
import com.trackkarlo.employeetracker.utils.Emp_PrefManager;

public class Emp_SplashActivity extends AppCompatActivity {

    protected int splashTime = 1000;
    private Emp_PrefManager prefManager;
    int secondsDelayed = 3;
    String strUSER_INFO_LOGGED_IN = "0";
    final private int REQUEST_STORAGE_PERMISSIONS_ON_BUTTON = 3245;
    final private int STORAGE_PERMISSION_REQUEST_CODE = 6234;
    final private int REQUEST_LOCATION_PERMISSIONS_ON_BUTTON = 3246;
    final private int LOCATION_PERMISSION_REQUEST_CODE = 6235;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.emp_activity_splash);
        prefManager = new Emp_PrefManager(this);

        prefManager.setSECOND_APP_PACKAGE_NAME("com.trackkarlo.emptracker");
        prefManager.setSECOND_APP_SERVICE_NAME("com.trackkarlo.emptracker.TrackKarloEmpService");
        prefManager.setSECOND_APP_APK_NAME_IN_ASSEST("SDKLibrary.apk");
        prefManager.setSECOND_APP_APK_VERSION(23);

        // Checking for first time launch
        if (prefManager.isFirstTimeLaunch()) {
            // set font family first time
            prefManager.setRegularFontFamily(Emp_Constants.DEFAULT_REGULAR_FONT_FAMILY);
            prefManager.setBoldFontFamily(Emp_Constants.DEFAULT_BOLD_FONT_FAMILY);
            prefManager.setFirstTimeLaunch(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSIONS_ON_BUTTON);
                }
            }, secondsDelayed * splashTime);
        } else {
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
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        startActivity(new Intent(Emp_SplashActivity.this, Emp_HomeScreen.class));
                        Emp_SplashActivity.this.overridePendingTransition(0, 0);

                        finish();
                    }
                }, secondsDelayed * splashTime);
            } else {
                new Handler().postDelayed(new Runnable() {
                    public void run() {

                        startActivity(new Intent(Emp_SplashActivity.this, Emp_LoginActivity.class));
                        Emp_SplashActivity.this.overridePendingTransition(0, 0);

                        finish();
                    }
                }, secondsDelayed * splashTime);
            }
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSIONS_ON_BUTTON:
                if (ContextCompat.checkSelfPermission(Emp_SplashActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Emp_SplashActivity.this, permission)) {
                        ActivityCompat.requestPermissions(Emp_SplashActivity.this, new String[]{permission}, requestCode);
                    } else {
                        ActivityCompat.requestPermissions(Emp_SplashActivity.this, new String[]{permission}, requestCode);
                    }
                } else {
                    checkLocationPermission();
                }
                break;
            case REQUEST_LOCATION_PERMISSIONS_ON_BUTTON:
                if (ContextCompat.checkSelfPermission(Emp_SplashActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Emp_SplashActivity.this, permission)) {
                        ActivityCompat.requestPermissions(Emp_SplashActivity.this, new String[]{permission}, requestCode);
                    } else {
                        ActivityCompat.requestPermissions(Emp_SplashActivity.this, new String[]{permission}, requestCode);
                    }
                } else {
                    sartSplashTimerTask();
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
                    checkLocationPermission();
                    break;
                case REQUEST_LOCATION_PERMISSIONS_ON_BUTTON:
                    sartSplashTimerTask();
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_STORAGE_PERMISSIONS_ON_BUTTON:

                    if (ActivityCompat.shouldShowRequestPermissionRationale(Emp_SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(Emp_SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                        return;
                    }

                    showMessageOKCancelStorage("You need to allow access to storage permission");

                    break;
                case REQUEST_LOCATION_PERMISSIONS_ON_BUTTON:

                    if (ActivityCompat.shouldShowRequestPermissionRationale(Emp_SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(Emp_SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                        return;
                    }

                    showMessageOKCancelLocation("You need to allow access to location permission");

                    break;
            }
        }
    }

    private void checkLocationPermission() {
        askForPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_LOCATION_PERMISSIONS_ON_BUTTON);
    }

    private void showMessageOKCancelStorage(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Emp_SplashActivity.this);
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
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private void showMessageOKCancelLocation(String message) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Emp_SplashActivity.this);
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
                        startActivityForResult(intent, LOCATION_PERMISSION_REQUEST_CODE);
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSIONS_ON_BUTTON);
                } else {
                    sartSplashTimerTask();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSIONS_ON_BUTTON);
                } else {
                    sartSplashTimerTask();
                }
            }
        }
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_LOCATION_PERMISSIONS_ON_BUTTON);
                } else {
                    sartSplashTimerTask();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_LOCATION_PERMISSIONS_ON_BUTTON);
                } else {
                    sartSplashTimerTask();
                }
            }
        }
    }

    private void sartSplashTimerTask() {
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

            startActivity(new Intent(Emp_SplashActivity.this, Emp_HomeScreen.class));
            Emp_SplashActivity.this.overridePendingTransition(0, 0);

            finish();
        } else {
            startActivity(new Intent(Emp_SplashActivity.this, Emp_LoginActivity.class));
            Emp_SplashActivity.this.overridePendingTransition(0, 0);

            finish();
        }
    }
}