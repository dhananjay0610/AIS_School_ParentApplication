package com.rawtalent.schoolsystemuser.NavigationActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;



public class ChangeNavigationActivities {

    public void startHomeActivity(Context context) {
        Intent intent = new Intent(context, DashBoard.class);
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }

    public void startProfileActivity(Context context) {
        Intent intent = new Intent(context, ProfileSettings.class);
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }
    public void startContactsActivity(Context context) {
        Intent intent = new Intent(context, Contacts.class);
        if ((Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
    }


}
