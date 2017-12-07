package com.trackkarlo.employeetracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by precise on 4/26/2017.
 */

public class DeviceControlAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<DeviceControlObject> objects;
    private LayoutInflater mInflater;

    public DeviceControlAdapter(Context context, ArrayList<DeviceControlObject> objects) {
        super();
        this.mContext = context;
        this.objects = objects;
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateArray(ArrayList<DeviceControlObject> objects)
    {
        this.objects = objects;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.device_control_row, null);
            holder = new ViewHolder();
            holder.textViewReportName = (TextView)convertView.findViewById(R.id.textViewReportName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.textViewReportName.setText(String.format("%s",objects.get(position).getReportName()));
        return convertView;
    }

    private class ViewHolder {
        private TextView textViewReportName;
    }
}