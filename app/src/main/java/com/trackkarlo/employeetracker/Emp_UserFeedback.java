package com.trackkarlo.employeetracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.trackkarlo.employeetracker.db.Emp_UserTableData;
import com.trackkarlo.employeetracker.utils.Emp_GPSTracker;
import com.trackkarlo.employeetracker.utils.Emp_PrefManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by precise on 04-Sep-17.
 */

public class Emp_UserFeedback extends Fragment {

    public Emp_UserFeedback() {
        // Required empty public constructor
    }

  String str="test_updated";
    EditText edttaskname, edtDescription, edtContactPerson;
    Button btn;
    Spinner spinnerStatus;
    private Emp_PrefManager prefManager;
    private ProgressBar mRegistrationProgressBar;
    String userid = "", status = "";
    ArrayList<DeviceControlObject> arrayListForDeviceControlObject = new ArrayList<DeviceControlObject>();
    DeviceControlAdapter deviceControlAdapter;
    double latitude = 0.0, longitude = 0.0;
    Emp_GPSTracker gps;

    LocationManager locationManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.emp_fragment_user_feedback, container,
                false);
        prefManager = new Emp_PrefManager(getActivity());
        edttaskname = (EditText) rootView.findViewById(R.id.edttaskname);
        edtDescription = (EditText) rootView.findViewById(R.id.edtDescription);
        edtContactPerson = (EditText) rootView.findViewById(R.id.edtContactPerson);
        btn = (Button) rootView.findViewById(R.id.btn);

        spinnerStatus = (Spinner) rootView.findViewById(R.id.spinnerStatus);

        mRegistrationProgressBar = (ProgressBar) rootView.findViewById(R.id.registrationProgressBar);
        mRegistrationProgressBar.setVisibility(View.GONE);


        Cursor cursor = getActivity().getContentResolver().query(Emp_UserTableData.CONTENT_URI_USER_INFO, null, null, null, null);
        if (cursor != null) {
            System.out.println("USER_cursor --" + cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    userid = cursor.getString(cursor.getColumnIndex(Emp_UserTableData.UserTable.USER_INFO_USER_ID));
                    System.out.println("UserID --" + userid);
                }
                while (cursor.moveToNext());
            }
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gps = new Emp_GPSTracker(getActivity());
                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    if (gps.canGetLocation()) {
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        if (!edttaskname.getText().toString().equals("")) {
                            if (!status.equalsIgnoreCase("Select Status"))
                                submitFeedback(edttaskname.getText().toString(), edtDescription.getText().toString(), edtContactPerson.getText().toString(), "", status, userid);
                            else
                                Toast.makeText(getActivity(), "Please Select Status", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getActivity(), "Please Enter Taskname", Toast.LENGTH_SHORT).show();
                    } else {
                        gps.showSettingsAlert();
                    }
                } else {
                    gps.showSettingsAlert();
                }
            }
        });


        arrayListForDeviceControlObject = new ArrayList<DeviceControlObject>();
        DeviceControlObject deviceListObject1 = new DeviceControlObject();
        deviceListObject1.setReportName("Select Status");
        arrayListForDeviceControlObject.add(deviceListObject1);
        DeviceControlObject deviceListObject2 = new DeviceControlObject();
        deviceListObject2.setReportName("Complete");
        arrayListForDeviceControlObject.add(deviceListObject2);
        DeviceControlObject deviceListObject3 = new DeviceControlObject();
        deviceListObject3.setReportName("Pending");
        arrayListForDeviceControlObject.add(deviceListObject3);
        deviceControlAdapter = new DeviceControlAdapter(getActivity(), arrayListForDeviceControlObject);
        spinnerStatus.setAdapter(deviceControlAdapter);

        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                if (spinnerStatus.getSelectedItemPosition() > 0) {
                    status = arrayListForDeviceControlObject.get(position).getReportName();
                } else {
                    status = "Select Status";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        return rootView;
    }

    private void submitFeedback(final String _taskname, final String _desc, final String _contactperson,
                                final String _contactmobile, final String _status, final String _userid) {
        final Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String url = "";

                    url = "http://46.4.94.41/mobiletracker/api/Login/feedback?TaskName="
                            + _taskname + " &Detail=" + _desc + "&ContactPerson=" + _contactperson +
                            "&ContactNumber=1234567890" + _contactmobile + "&Latitude=" + latitude + "&Longitude=" + longitude + "&Status=" + _status + "&FeedbackBy=" + _userid;
                    url = url.replaceAll(" ", "%20");
                    HttpGet httpost = new HttpGet(url);

                    HttpResponse response;

                    response = httpClient.execute(httpost);
                    HttpEntity resEntity = response.getEntity();

                    if (resEntity != null) {
                        String responseStr = EntityUtils.toString(resEntity).trim();
                        System.out.println("responseStr---" + responseStr);
                        JSONObject jsonObject = new JSONObject(responseStr.toString());

                        if (jsonObject.get("msg") instanceof String) {
                            String strMessage = jsonObject.getString("msg");
                            String Status = jsonObject.getString("Status");

                            if (Status.equalsIgnoreCase("True")) {
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
                            b.putString("strMessage", "Feedback not sent");
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    final Message msg = new Message();
                    final Bundle b = new Bundle();
                    b.putString("status", "false");
                    b.putString("strMessage", "Feedback not sent");
                    msg.setData(b);
                    handler.sendMessage(msg);
                }
            }
        };

        if (isNetworkAvailable()) {
            mRegistrationProgressBar.setVisibility(View.VISIBLE);
            thread.start();
        } else {
            Toast.makeText(getActivity(), "Please Check Your Internet Connection",
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                Toast.makeText(getActivity(), strMessage,
                        Toast.LENGTH_SHORT).show();

                edttaskname.setText("");
                edtDescription.setText("");
                edtContactPerson.setText("");
                spinnerStatus.setSelection(0);

            } else {
                String strMessage = b.getString("strMessage");
                Toast.makeText(getActivity(), strMessage,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };


    private void showEditAlert() {
        LinearLayout v2 = new LinearLayout(getActivity());

        v2.setOrientation(LinearLayout.HORIZONTAL);
        int newHeight = 200; // New height in pixels
        int newWidth = 200; // New width in pixels


        final ImageView img1 = new ImageView(getActivity());
        img1.setBackgroundResource(R.drawable.passenger);

        final ImageView img2 = new ImageView(getActivity());
        img2.setBackgroundResource(R.drawable.passenger);


        final ImageView img3 = new ImageView(getActivity());
        img3.setBackgroundResource(R.drawable.passenger);


        final ImageView img4 = new ImageView(getActivity());
        img4.setBackgroundResource(R.drawable.passenger);

        v2.addView(img1);
        v2.addView(img2);
        v2.addView(img3);
        v2.addView(img4);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Start a Trip");
        builder.setView(v2);
        builder.setMessage("Select number of Passengers");
        builder.setPositiveButton("Start", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                try {

                } catch (Exception ex) {
                    Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        final Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(false);

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img1.setBackgroundResource(R.drawable.passenger_fill);
            }
        });

        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img1.setBackgroundResource(R.drawable.passenger_fill);
                img2.setBackgroundResource(R.drawable.passenger_fill);
            }
        });

        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img1.setBackgroundResource(R.drawable.passenger_fill);
                img2.setBackgroundResource(R.drawable.passenger_fill);
                img3.setBackgroundResource(R.drawable.passenger_fill);
            }
        });

        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img1.setBackgroundResource(R.drawable.passenger_fill);
                img2.setBackgroundResource(R.drawable.passenger_fill);
                img3.setBackgroundResource(R.drawable.passenger_fill);
                img4.setBackgroundResource(R.drawable.passenger_fill);
            }
        });
    }

}
