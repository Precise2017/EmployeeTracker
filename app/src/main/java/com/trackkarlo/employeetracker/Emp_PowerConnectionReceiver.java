package com.trackkarlo.employeetracker;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.trackkarlo.employeetracker.utils.Emp_PrefManager;

/**
 * Created by precise on 26-May-17.
 */

public class Emp_PowerConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        try
        {
            Emp_PrefManager prefManager  = new Emp_PrefManager(context);
            String SECOND_APP_PACKAGE_NAME = prefManager.getSECOND_APP_PACKAGE_NAME();
            String SECOND_APP_SERVICE_NAME = prefManager.getSECOND_APP_SERVICE_NAME();

            Boolean isAppInstalled = false;
            PackageManager pm = context.getPackageManager();
            try {
                pm.getPackageInfo(SECOND_APP_PACKAGE_NAME, PackageManager.GET_ACTIVITIES);
                isAppInstalled = true;
            } catch (PackageManager.NameNotFoundException e) {
            }

            if(isAppInstalled)
            {
                Boolean isServiceRunning = false;

                ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                    if (SECOND_APP_SERVICE_NAME.equals(service.service.getClassName()))
                    {
                        isServiceRunning = true;
                        break;
                    }
                }

                if(!isServiceRunning)
                {
                    Intent i = new Intent();
                    i.setComponent(new ComponentName(SECOND_APP_PACKAGE_NAME, SECOND_APP_SERVICE_NAME));
                    ComponentName c = context.startService(i);
                }
            }
        }
        catch (Exception e)
        {}
    }
}
