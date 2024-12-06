package com.example.contentproviderexample;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.contentproviderexample.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    private static final String TAG = "MainActivity";
    private ListView contactNames;

    private static final int REQUEST_CODE_READ_CONTACTS = 1;
    //    private static boolean READ_CONTACTS_GRANTED = false;
    FloatingActionButton fab = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        contactNames = (ListView) findViewById(R.id.contact_names);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {

            int hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        Log.d(TAG,"onCreate: checkSelfPermission = "+hasReadContactPermission);

//        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
//            Log.d(TAG, "onCreate: permission granted");
////            READ_CONTACTS_GRANTED = true;
//        } else {
//            Log.d(TAG, "onCreate: requesting permission");
//            ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
//        }
        if(hasReadContactPermission !=PackageManager.PERMISSION_GRANTED)

            {
                Log.d(TAG, "onCreate: requesting permissions");
                ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
            }

            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
                Log.d(TAG, "fab onClick: starts");
//                if (READ_CONTACTS_GRANTED) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    String[] projection = {ContactsContract.Contacts.DISPLAY_NAME_PRIMARY};
                    ContentResolver contentResolver = getContentResolver();
                    Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,
                            projection,
                            null,
                            null,
                            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY);

                    if (cursor != null) {
                        List<String> contacts = new ArrayList<String>();
                        while (cursor.moveToNext()) {
                            contacts.add(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
                        }
                        cursor.close();
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.contact_detail, R.id.name, contacts);
                        contactNames.setAdapter(adapter);
                    }

                } else {
                    Snackbar.make(view, "This app can't display your Contacts records unless you...", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Grant Access", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d(TAG, "Snackbar onClick: starts");
                                            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, READ_CONTACTS)) {
                                                Log.d(TAG, "Snackbar onClick: calling requestPermissions");
                                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
                                            } else {
                                                // The user has permanently denied the permission, so take them to the settings
                                                Log.d(TAG, "Snackbar onClick: launching settings");
                                                Intent intent = new Intent();
                                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", MainActivity.this.getPackageName(), null);
                                                Log.d(TAG, "Snackbar onClick: Intent Uri is " + uri.toString());
                                                intent.setData(uri);
                                                MainActivity.this.startActivity(intent);

                                            }
                                            Log.d(TAG, "Snackbar onClick: ends");
                                        }
                                    }

                            ).show();
                }

                Log.d(TAG, "fab onClick: ends");
            }
        });
        Log.d(TAG, "onCreate: ends");
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        Log.d(TAG, "onRequestPermissionsResult: starts");
//        switch (requestCode) {
//            case REQUEST_CODE_READ_CONTACTS: {
//                // If request is cancelled, the rest arrays are empty.
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.
//                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
////                    READ_CONTACTS_GRANTED = true;
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                    Log.d(TAG, "onRequestPermissionsResult: permission refused");
//                }
////                fab.setEnabled(READ_CONTACTS_GRANTED);
//            }
//        }
//
//        Log.d(TAG, "onRequestPermissionsResult: ends");
//}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}