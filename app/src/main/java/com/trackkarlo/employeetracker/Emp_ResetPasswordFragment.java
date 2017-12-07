package com.trackkarlo.employeetracker;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.trackkarlo.employeetracker.db.Emp_UserTableData;
import com.trackkarlo.employeetracker.utils.Emp_PrefManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by precise on 04-Sep-17.
 */

public class Emp_ResetPasswordFragment extends Fragment {

    public Emp_ResetPasswordFragment() {
        // Required empty public constructor
    }

    EditText edtTextoldPassword, edtTextNewPassword, edtTextConfirmNewPassword;
    TextView etvalidateedtTextoldPassword, etvalidateedtTextNewPassword, etvalidateedtTextConfirmNewPassword;
    Button btn;
    private Emp_PrefManager prefManager;
    private ProgressBar mRegistrationProgressBar;
    String userid = "";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.emp_fragment_reset, container,
                false);
        prefManager = new Emp_PrefManager(getActivity());
        edtTextoldPassword = (EditText) rootView.findViewById(R.id.edtTextoldPassword);
        edtTextNewPassword = (EditText) rootView.findViewById(R.id.edtTextNewPassword);
        edtTextConfirmNewPassword = (EditText) rootView.findViewById(R.id.edtTextConfirmNewPassword);
        btn = (Button) rootView.findViewById(R.id.btn);

        etvalidateedtTextoldPassword = (TextView) rootView.findViewById(R.id.etvalidateedtTextoldPassword);
        etvalidateedtTextNewPassword = (TextView) rootView.findViewById(R.id.etvalidateedtTextNewPassword);
        etvalidateedtTextConfirmNewPassword = (TextView) rootView.findViewById(R.id.etvalidateedtTextConfirmNewPassword);

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
                etvalidateedtTextoldPassword.setVisibility(View.GONE);
                etvalidateedtTextNewPassword.setVisibility(View.GONE);
                etvalidateedtTextConfirmNewPassword.setVisibility(View.GONE);

                int check = 0;

                if (edtTextoldPassword.getText().toString().equals("")) {
                    check = 1;
                    etvalidateedtTextoldPassword.setVisibility(View.VISIBLE);
                    etvalidateedtTextoldPassword.setText("Please enter old password");
                }

                if (edtTextNewPassword.getText().toString().equals("")) {
                    check = 1;
                    etvalidateedtTextNewPassword.setVisibility(View.VISIBLE);
                    etvalidateedtTextNewPassword.setText("Please enter new password");
                }

                if (!edtTextConfirmNewPassword.getText().toString().equals(edtTextNewPassword.getText().toString())) {
                    check = 1;
                    etvalidateedtTextConfirmNewPassword.setVisibility(View.VISIBLE);
                    etvalidateedtTextConfirmNewPassword.setText("Password not matched");
                }

                if (check == 0) {
                    resetPassword(edtTextoldPassword.getText().toString(), edtTextNewPassword.getText().toString(), edtTextConfirmNewPassword.getText().toString(), userid);
                }
            }
        });




        return rootView;
    }

    private void resetPassword(final String oldPassword, final String newPassword, final String confirmPassword, final String userid) {
        final Thread thread = new Thread() {
            @Override
            public void run() {

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    String url = "http://46.4.94.41/mobiletracker/api/Registration/ResetPassword?DeviceId=" + userid +
                            "&OldPassword=" + oldPassword + "&NewPassword=" + newPassword + "&ConfirmPassword=" + confirmPassword;
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

                            if (strMessage.equalsIgnoreCase("Status")) {
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
                            b.putString("strMessage", "Password not updated");
                            msg.setData(b);
                            handler.sendMessage(msg);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    final Message msg = new Message();
                    final Bundle b = new Bundle();
                    b.putString("status", "false");
                    b.putString("strMessage", "Password not updated");
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

            } else {
                String strMessage = b.getString("strMessage");
                Toast.makeText(getActivity(), strMessage,
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

}