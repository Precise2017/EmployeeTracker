package com.trackkarlo.employeetracker;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.trackkarlo.employeetracker.db.Emp_UserTableData;
import com.trackkarlo.employeetracker.utils.Emp_GPSTracker;
import com.trackkarlo.employeetracker.utils.Emp_PrefManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by precise on 04-Sep-17.
 */

public class Emp_UserFragment extends Fragment {

    public Emp_UserFragment() {
        // Required empty public constructor
    }

    private Emp_PrefManager prefManager;
    private Typeface custom_fontRegular;
    private Typeface custom_fontBold;
    Button btnok;
    final private int REQUEST_INSTALLATION_EMP_TRACKER_CODE = 5554;
    final private int REQUEST_STORAGE_PERMISSIONS_ON_BUTTON = 3245;
    final private int STORAGE_PERMISSION_REQUEST_CODE = 6234;
    String strUSER_NAME = "";
    String strUSER_INFO_IMEI = "";
    TextView txtbottom;
    Dialog alertDialog = null;
    ProgressDialog progressDialog = null;
    int flag = 0;
    TextView txtimei;
    TextView txtuser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewFragment = inflater.inflate(R.layout.emp_fragment_user, container, false);

        prefManager = new Emp_PrefManager(getActivity());
        custom_fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "font/" + prefManager.getRegularFontFamilySelected());
        custom_fontBold = Typeface.createFromAsset(getActivity().getAssets(), "font/" + prefManager.getBoldFamilySelected());

        txtimei = (TextView) viewFragment.findViewById(R.id.txtimei);
        txtuser = (TextView) viewFragment.findViewById(R.id.txtuser);
        txtuser.setTypeface(custom_fontBold);
        txtimei.setTypeface(custom_fontRegular);

        txtbottom = (TextView) viewFragment.findViewById(R.id.txtbottom);
        txtbottom.setVisibility(View.GONE);
        txtbottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    showAlert();
                } catch (Exception e) {

                }
            }
        });

        btnok = (Button) viewFragment.findViewById(R.id.btnok);
        btnok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isAppInstalled = appInstalledOrNot(prefManager.getSECOND_APP_PACKAGE_NAME());
                if (isAppInstalled) {
                    Emp_GPSTracker gps = new Emp_GPSTracker(getActivity());
                    if (gps.canGetLocation()) {
                        try {
                            System.out.println("isServiceRunning" + isServiceRunning(getActivity()));
                            if (!isServiceRunning(getActivity())) {
                                Intent i = new Intent();
                                i.setComponent(new ComponentName(prefManager.getSECOND_APP_PACKAGE_NAME(), prefManager.getSECOND_APP_SERVICE_NAME()));
                                ComponentName c = getActivity().startService(i);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        getActivity().finish();
                    } else {
                        gps.showSettingsAlert();
                    }
                } else {
                    installApkFromAssets();
                }
            }
        });

        Cursor cursor = getActivity().getContentResolver().query(Emp_UserTableData.CONTENT_URI_USER_INFO, null, null, null, null);
        if (cursor != null) {
            System.out.println("USER_cursor --" + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    strUSER_NAME = cursor.getString(cursor.getColumnIndex(Emp_UserTableData.UserTable.USER_INFO_NAME));
                    strUSER_INFO_IMEI = cursor.getString(cursor.getColumnIndex(Emp_UserTableData.UserTable.USER_INFO_IMEI));
                    System.out.println("strUSER_NAME --" + strUSER_NAME);
                    System.out.println("strUSER_INFO_IMEI --" + strUSER_INFO_IMEI);
                }
                while (cursor.moveToNext());
            }
        }


        if (strUSER_NAME != null) {
            txtuser.setText("HELLO " + strUSER_NAME);
        }

        boolean isAppInstalled = appInstalledOrNot(prefManager.getSECOND_APP_PACKAGE_NAME());
        if (isAppInstalled) {
            try {
                PackageInfo pinfo = null;
                pinfo = getActivity().getPackageManager().getPackageInfo(prefManager.getSECOND_APP_PACKAGE_NAME(), 0);
                int verCode = pinfo.versionCode;
                String verName = pinfo.versionName;

                if (verCode < prefManager.getSECOND_APP_APK_VERSION()) {
                    showUpdateVersion();
                }


            } catch (Exception ex) {
                Log.e("Error", ex.getMessage());
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSIONS_ON_BUTTON);
            }
        }

        scheduleAlarm();

        return viewFragment;
    }


    public void scheduleAlarm() {
        Intent intent = new Intent(getActivity().getApplicationContext(), Emp_MyAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(getActivity(), Emp_MyAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 30000, pIntent);
    }

    public void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.emp_alert_uninstall,
                null);

        final TextView txttitle = (TextView) dialoglayout
                .findViewById(R.id.txttitle);
        final TextView txtmessage = (TextView) dialoglayout
                .findViewById(R.id.txtmessage);
        final EditText etcode = (EditText) dialoglayout
                .findViewById(R.id.etcode);

        final Button btnok = (Button) dialoglayout.findViewById(R.id.btnok);
        final Button btncancel = (Button) dialoglayout.findViewById(R.id.btncancel);

        txttitle.setText("Uninstall Application");
        txtmessage.setText("Enter code to verify");

        btnok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                http_SendResponse(etcode.getText().toString());
                alertDialog.cancel();
            }
        });

        btncancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                alertDialog.cancel();
            }
        });

        builder.setView(dialoglayout);

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void http_SendResponse(final String code) {
        progressDialog = ProgressDialog.show(getActivity(), "",
                "Sending request..Please wait");
        flag = 0;
        final Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String url = "http://46.4.94.41/mobiletracker/api/VerifyUnlock/verifyUnlock/";
                    HttpPost httpost = new HttpPost(url);
                    String urlParameters = "{IMEI:" + "\"" + strUSER_INFO_IMEI + "\"" + ",unlockCode:" + "\"" + code + "\"" + "}";


                    System.out.println("urlParameters--" + urlParameters);
                    httpost.setEntity(new StringEntity(urlParameters));

                    httpost.setHeader("Accept", "application/json");
                    httpost.setHeader("Content-type", "application/json");
                    HttpResponse response;

                    response = httpClient.execute(httpost);
                    HttpEntity resEntity = response.getEntity();

                    if (resEntity != null) {

                        String responseStr = "";

                        responseStr = EntityUtils.toString(resEntity)
                                .trim();
                        System.out.println("responseStr--" + responseStr);

                        JSONObject jsonObject = new JSONObject(responseStr.toString());
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
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    final Message msg = new Message();
                    final Bundle b = new Bundle();
                    b.putString("status", "false");
                    b.putString("strMessage", "Request Failed");
                    msg.setData(b);
                    handler.sendMessage(msg);
                }
            }
        };
        thread.start();
    }


    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            Bundle b = msg.getData();
            if (b.getString("status") != null && b.getString("status").equalsIgnoreCase("true")) {
                String strMessage = b.getString("strMessage");
                Toast.makeText(getActivity(), strMessage,
                        Toast.LENGTH_SHORT).show();

                uninstallAPP();
            } else {
                String strMessage = b.getString("strMessage");
                Toast.makeText(getActivity(), strMessage,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void uninstallAPP() {
        boolean isAppInstalled = appInstalledOrNot(prefManager.getSECOND_APP_PACKAGE_NAME());
        if (isAppInstalled) {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + prefManager.getSECOND_APP_PACKAGE_NAME()));
            startActivity(intent);
            getActivity().overridePendingTransition(0, 0);


            Intent broadcastIntent = new Intent(prefManager.getSECOND_APP_PACKAGE_NAME() + ".UnRegisterAdminReciever");
            getActivity().sendBroadcast(broadcastIntent);

        } else {
            Toast.makeText(getActivity().getApplicationContext(), "App not found", Toast.LENGTH_SHORT).show();
        }
    }


    private void askForPermission(String permission, Integer requestCode) {
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSIONS_ON_BUTTON:
                if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
                    }
                } else {
                    installApkFromAssets();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(getActivity(), permissions[0]) == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_STORAGE_PERMISSIONS_ON_BUTTON:
                    installApkFromAssets();
                    break;
            }
        } else {
            switch (requestCode) {
                case REQUEST_STORAGE_PERMISSIONS_ON_BUTTON:

                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                        return;
                    }

                    showMessageOKCancelStorage("You need to allow access to storage permission");

                    break;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        boolean isAppInstalled = appInstalledOrNot(prefManager.getSECOND_APP_PACKAGE_NAME());
        if (isAppInstalled) {
            try {
                if (!isServiceRunning(getActivity())) {
                    Intent i = new Intent();
                    i.setComponent(new ComponentName(prefManager.getSECOND_APP_PACKAGE_NAME(), prefManager.getSECOND_APP_SERVICE_NAME()));
                    ComponentName c = getActivity().startService(i);
                }
            } catch (Exception e) {
            }

            txtbottom.setVisibility(View.VISIBLE);
        } else {
            txtbottom.setVisibility(View.GONE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            } else {

                installApkFromAssets();
            }
        }
    }

    private void showMessageOKCancelStorage(String message) {
        AlertDialog.Builder alertDialogBuilderForStoragePermission = new AlertDialog.Builder(getActivity());
        alertDialogBuilderForStoragePermission.setTitle("Permission Alert");
        alertDialogBuilderForStoragePermission
                .setMessage(message)
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, STORAGE_PERMISSION_REQUEST_CODE);
                        getActivity().overridePendingTransition(0, 0);
                    }
                });
        AlertDialog alertDialogForStoragePermission = alertDialogBuilderForStoragePermission.create();
        alertDialogForStoragePermission.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSIONS_ON_BUTTON);
                } else {
                    installApkFromAssets();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_STORAGE_PERMISSIONS_ON_BUTTON);
                } else {
                    installApkFromAssets();
                }
            }
        } else if (requestCode == REQUEST_INSTALLATION_EMP_TRACKER_CODE) {
            boolean isAppInstalled = appInstalledOrNot(prefManager.getSECOND_APP_PACKAGE_NAME());
            if (isAppInstalled) {

            } else {
                installApkFromAssets();
            }
        }
    }

    public static boolean isServiceRunning(Context queryingContext) {
        try {
            ActivityManager manager = (ActivityManager) queryingContext.getSystemService(Context.ACTIVITY_SERVICE);
            Emp_PrefManager prefManager = new Emp_PrefManager(queryingContext);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (prefManager.getSECOND_APP_PACKAGE_NAME().equals(service.service.getClassName()))
                    return true;
            }
        } catch (Exception ex) {

        }
        return false;
    }

    private void installApkFromAssets() {

        try {
            File file = new File(Environment.getExternalStorageDirectory(), prefManager.getSECOND_APP_APK_NAME_IN_ASSEST());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                file = new File(getActivity().getFilesDir(), prefManager.getSECOND_APP_APK_NAME_IN_ASSEST());
            }

            // File file = new File(Environment.getExternalStorageDirectory(), prefManager.getSECOND_APP_APK_NAME_IN_ASSEST());
            //  File file = new File(Environment.getExternalStorageState(), prefManager.getSECOND_APP_APK_NAME_IN_ASSEST());
            // File file = new File(getActivity().getFilesDir(), prefManager.getSECOND_APP_APK_NAME_IN_ASSEST());

            if (file.exists()) {
                file.delete();
            }

            AssetManager assetManager = getActivity().getAssets();

            InputStream in = null;
            OutputStream out = null;

            try {
                in = assetManager.open(prefManager.getSECOND_APP_APK_NAME_IN_ASSEST());
                out = new FileOutputStream(file);

                byte[] buffer = new byte[1024];

                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }

                in.close();
                in = null;

                out.flush();
                out.close();
                out = null;

                Uri uri = null;
                Intent intent = new Intent(Intent.ACTION_VIEW);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                   /* uri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);

                    List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }*/

                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    uri = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".fileprovider", file);

                    // Uri uri_new  = Uri.parse("content://com.trackkarlo.employeetracker.fileprovider/root/storage/emulated/0/emptracker.apk");
                    /* File file =
                            Utils.getFileForUri(
                                    Uri.parse("content://com.nononsenseapps.filepicker.sample.provider/root/storage/emulated/0/000000_nonsense-tests/A-dir/file-1.txt"));
*/

                    intent.setDataAndType(uri, "application/vnd.android.package-archive");
                } else {
                    intent.setDataAndType(Uri.fromFile(file),
                            "application/vnd.android.package-archive");
                }

                startActivityForResult(intent, REQUEST_INSTALLATION_EMP_TRACKER_CODE);
                getActivity().overridePendingTransition(0, 0);

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
        }
    }


    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }


    private void showUpdateVersion() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Alert")
                .setMessage("A New Version of SDK Library application is available. Do you want to update with new version")
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {

                                try {
                                    uninstallAPP();
                                } catch (Exception e) {
                                }

                            }
                        }).setNegativeButton("No", null).show();
    }

    public class Utils {
        @NonNull
        public File getFileForUri(@NonNull Uri uri) {
            String path = uri.getEncodedPath();
            final int splitIndex = path.indexOf('/', 1);
            final String tag = Uri.decode(path.substring(1, splitIndex));
            path = Uri.decode(path.substring(splitIndex + 1));

            if (!"root".equalsIgnoreCase(tag)) {
                throw new IllegalArgumentException(
                        String.format("Can't decode paths to '%s', only for 'root' paths.",
                                tag));
            }

            final File root = new File("/");

            File file = new File(root, path);
            try {
                file = file.getCanonicalFile();
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to resolve canonical path for " + file);
            }

            if (!file.getPath().startsWith(root.getPath())) {
                throw new SecurityException("Resolved path jumped beyond configured root");
            }

            return file;
        }
    }

}