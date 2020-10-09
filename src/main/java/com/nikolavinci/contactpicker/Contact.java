package com.nikolavinci.contactpicker;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.makeText;

public class Contact extends AppCompatActivity {

    // Declare
    public static final int PICK_CONTACT = 1;
    //    private static int PICK_CONTACT = 1;
    private static final int GET_PHONE_NUMBER = 3007;
    // Identifier for the permission request
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    public static String CONTACT_NUMBER = "cNumber";
    public static String CONTACT_NUMBER_ONE = "cN1";
    public static String CONTACT_NUMBER_TWO = "cN2";
    public static String CONTACT_NUMBER_THREE = "cN3";
    public static String CONTACT_NAME = "cName";
    public static String CONTACT_NAME_ONE = "cName1";
    public static String CONTACT_NAME_TWO = "cName2";
    public static String CONTACT_NAME_THREE = "cName3";
    private static int BUTTON_CLICKED = 0;
    SharedPreferences preferences;
    //    EditText contact1 , contact2 , contact3;
    TextView tvcontact1, tvcontact2, tvcontact3;
    Button addContact1, addContact2, addContact3;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        preferences = getSharedPreferences("contactInfo", 0);

        tvcontact1 = findViewById(R.id.contact1);
        tvcontact2 = findViewById(R.id.contact2);
        tvcontact3 = findViewById(R.id.contact3);

        addContact1 = findViewById(R.id.btnContact1);
        addContact2 = findViewById(R.id.btnContact2);
        addContact3 = findViewById(R.id.btnContact3);

        SharedPreferences preferences = getSharedPreferences("contactInfo", Context.MODE_PRIVATE);
        CONTACT_NUMBER_ONE = preferences.getString("CONTACT_NUMBER_ONE", "");
        CONTACT_NUMBER_TWO = preferences.getString("CONTACT_NUMBER_TWO", "");
        CONTACT_NUMBER_THREE = preferences.getString("CONTACT_NUMBER_THREE", "");

        tvcontact1.setText(CONTACT_NAME_ONE);
        tvcontact2.setText(CONTACT_NAME_TWO);
        tvcontact3.setText(CONTACT_NAME_THREE);

//        tvcontact1.setText(CONTACT_NUMBER_ONE);
//        tvcontact2.setText(CONTACT_NUMBER_TWO);
//        tvcontact3.setText(CONTACT_NUMBER_THREE);
    }

    public void addContact(View v) {
        getPermissionToReadUserContacts();
        if (v.getId() == R.id.btnContact1) BUTTON_CLICKED = 11;
        if (v.getId() == R.id.btnContact2) BUTTON_CLICKED = 22;
        if (v.getId() == R.id.btnContact3) BUTTON_CLICKED = 33;
        Intent it = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(it, PICK_CONTACT);
    }

    //code
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        if (reqCode == PICK_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                ContentResolver cr = getContentResolver();


                Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);
                assert cursor != null;
                while (cursor.moveToNext()) {
                    String contactId = cursor.getString(cursor
                            .getColumnIndex(ContactsContract.Contacts._ID));
                    String Name = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                    CONTACT_NAME = Name;
                    String hasPhone = cursor
                            .getString(cursor
                                    .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                    Cursor emailCur = cr.query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{contactId}, null);

                    if (emailCur != null) {
                        emailCur.close();
                    }

                    if (hasPhone.equalsIgnoreCase("1"))
                        hasPhone = "true";
                    else
                        hasPhone = "false";

                    String phoneNo = null;
                    if (Boolean.parseBoolean(hasPhone)) {
                        Cursor phones = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                        + " = " + contactId, null, null);
                        while (phones.moveToNext()) {
                            phoneNo = phones
                                    .getString(phones
                                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            CONTACT_NUMBER = phoneNo;
                        }
                        phones.close();
                    } else if (!Boolean.parseBoolean(hasPhone)) {
                        CONTACT_NUMBER = getString(R.string.PhonenoEmpty);
                        makeText(this, "Phone Number is Empty. Please Select Another Number.", Toast.LENGTH_LONG).show();
                    }

                    makeText(this, Name + "   " + CONTACT_NUMBER, Toast.LENGTH_SHORT).show();
                }
                if (BUTTON_CLICKED == 11) {
                    tvcontact1.setText(CONTACT_NAME);
                    CONTACT_NUMBER_ONE = CONTACT_NUMBER;
                    CONTACT_NAME_ONE = CONTACT_NAME;
                }
                if (BUTTON_CLICKED == 22) {
                    tvcontact2.setText(CONTACT_NAME);
                    CONTACT_NUMBER_TWO = CONTACT_NUMBER;
                    CONTACT_NAME_TWO = CONTACT_NAME;
                }
                if (BUTTON_CLICKED == 33) {
                    tvcontact3.setText(CONTACT_NAME);
                    CONTACT_NUMBER_THREE = CONTACT_NUMBER;
                    CONTACT_NAME_THREE = CONTACT_NAME;
                }
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("CONTACT_NUMBER_ONE", CONTACT_NUMBER_ONE);
                editor.putString("CONTACT_NUMBER_TWO", CONTACT_NUMBER_TWO);
                editor.putString("CONTACT_NUMBER_THREE", CONTACT_NUMBER_THREE);
                editor.putString("CONTACT_NAME_ONE", CONTACT_NAME_ONE);
                editor.putString("CONTACT_NAME_TWO", CONTACT_NAME_TWO);
                editor.putString("CONTACT_NAME_THREE", CONTACT_NAME_THREE);
                editor.apply();
                BUTTON_CLICKED = 0;

                cursor.close();
            }
        }
    }


    private String correctNumber(String cNumber) {

        makeText(this, "The length is " + cNumber.length(), Toast.LENGTH_SHORT).show();
        if (!cNumber.contains("+977")) {
            cNumber = "+977" + cNumber;
        }
        return cNumber;
    }

    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (PackageManager.PERMISSION_GRANTED != ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                shouldShowRequestPermissionRationale(
                        Manifest.permission.READ_CONTACTS);// Show our own UI to explain to the user why we need to read the contacts
// before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        READ_CONTACTS_PERMISSIONS_REQUEST);
            }
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
