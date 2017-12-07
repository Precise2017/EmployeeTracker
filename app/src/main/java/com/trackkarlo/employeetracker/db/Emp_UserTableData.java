package com.trackkarlo.employeetracker.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by precise on 09-May-17.
 */

public class Emp_UserTableData {

    public Emp_UserTableData() {
    }
    // A content URI is a URI that identifies data in a provider. Content URIs
    // include the symbolic name of the entire provider (its authority)

    public static final String AUTHORITY_USER_INFO = "com.trackkarlo.employeetracker.db.Emp_UserTableData";
    public static final Uri CONTENT_URI_USER_INFO = Uri.parse("content://" + AUTHORITY_USER_INFO
    + "/user_info");
    public static final String DATABASE_NAME = "employee_tracker.db";
    public static final int DATABASE_VERSION = 1;
    public static final String CONTENT_TYPE_USER_INFO = "vnd.android.cursor.dir/vnd.tag.user_info";


    public class UserTable implements BaseColumns {
        private UserTable() {
        }
        public static final String TABLE_NAME_USER_INFO = "user_info";
        public static final String _ID = "_id";
        public static final String USER_INFO_USER_ID = "user_id";
        public static final String USER_INFO_NAME = "name";
        public static final String USER_INFO_IMEI = "imei";
        public static final String USER_INFO_EMAIL = "email";
        public static final String USER_INFO_IMAGE = "image";
        public static final String USER_INFO_TYPE = "type";
        public static final String USER_INFO_LOGGED_IN = "logged_in";
    }


}
