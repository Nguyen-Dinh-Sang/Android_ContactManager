package com.example.android_contactmanager.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android_contactmanager.R;
import com.example.android_contactmanager.adapter.ContactAdapter;
import com.example.android_contactmanager.model.Contact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Contact> contactList = new ArrayList<Contact>();
    ContactAdapter contactAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        initData();
        checkPermissionGranted();
    }

    private void initData() {
    }

    private void initEvent() {

    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void checkPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            getContactList();
        }
    }

    private void getContactList() {
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);

        if ((cursor != null ? cursor.getCount() : 0) > 0) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";

                Cursor cursorPhone = getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);

                if (cursorPhone.moveToNext()) {
                    String number = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String uriImage = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    Contact contact = new Contact(name, number, id, uriImage);
                    contactList.add(contact);
                    cursorPhone.close();
                }
            }
            cursor.close();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactAdapter = new ContactAdapter(this, contactList);
        recyclerView.setAdapter(contactAdapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getContactList();
        } else {
            checkPermissionGranted();
        }

        if (requestCode == 200 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            export();
        } else {
            isWriteStoragePermissionGranted();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_export:
                if (isWriteStoragePermissionGranted()) {
                    export();
                }
                return true;
            case R.id.item_import:
                Log.d("TAGXXX", "onOptionsItemSelected: import");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void export() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + "ContactManagerExport");

        boolean isFolderCreated = false;
        if (!folder.exists()) {
            isFolderCreated = folder.mkdir();
        }

        String fileName = "ContactManagerExport.csv";
        String filePath = folder.toString() + "/" + fileName;

        try {
            Writer fileWriter = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8);
            fileWriter.append("ID");
            fileWriter.append(",");

            fileWriter.append("Name");
            fileWriter.append(",");

            fileWriter.append("Number");
            fileWriter.append(",");

            fileWriter.append("UriImage");
            fileWriter.append("\n");

            for (Contact contact : contactList) {
                fileWriter.append(""+contact.getId());
                fileWriter.append(",");

                fileWriter.append(""+contact.getName());
                fileWriter.append(",");

                fileWriter.append(""+contact.getNumber());
                fileWriter.append(",");

                fileWriter.append(""+contact.getUriImage());
                fileWriter.append("\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {

        }
    }

    private boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 200);
                return false;
            }
        } else {
            // Below api 23 was requested in Manifest
            return true;
        }
    }
}