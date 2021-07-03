package com.rawtalent.schoolsystemuser;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.rawtalent.UserProfile;

import java.io.ByteArrayOutputStream;

import static android.content.Context.MODE_PRIVATE;

public class UserSharedPreference {

    public static final String PREFERENCE_NAME = "profile";

    public static final String NAME = "name";
    public static final String IMAGE = "image";
    public static final String STATUS = "status";


    public static void storeData(Context context, String name,String image,String status) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(NAME, name);
            editor.putString(IMAGE, image);
            editor.putString(STATUS, status);
            editor.apply();
        }
    }
    public static void storeImage(Context context,String image) {
        if (context != null) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(IMAGE, image);
            editor.apply();
        }
    }

    public static UserProfile retrieveData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        String name=sharedPreferences.getString(NAME,"");
        String image=sharedPreferences.getString(IMAGE,"");
        String status=sharedPreferences.getString(STATUS,"");

        UserProfile profile=new UserProfile();
        profile.setName(name);
        profile.setImage(image);
        profile.setStatus(status);
        return profile;
    }


    /**
     * This function encode the image bitmap to store into shared preferences.
     *
     * @param image
     * @return
     */
    public static String encodeTobase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imagestring = Base64.encodeToString(b, Base64.DEFAULT);
        Log.d("TAG", "encodeTobase64: imagestring = "+imagestring);
        return imagestring;
    }

    /**
     * This function decode the image into bitmap to show onto screen.
     *
     * @param input
     * @return
     */
    public static Bitmap decodeBase64(String input) {
        if (input == null)
            return null;

        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}

