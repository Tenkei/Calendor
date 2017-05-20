package com.esbati.keivan.persiancalendar.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.esbati.keivan.persiancalendar.Fragments.HomeFragment;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities;
import com.esbati.keivan.persiancalendar.Utils.CalendarHelper;
import com.esbati.keivan.persiancalendar.Utils.SoundManager;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private final static int MY_PERMISSIONS_REQUEST_READ_CALENDAR = 666;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.AppTheme_NoActionBar);
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Load Audio and Events
        SoundManager.getInstance();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CALENDAR)) {

                //Permission Request Explanation
                AlertDialog dialog = new AlertDialog.Builder(this)
                        //.setView(mDialogView)
                        .setTitle(getResources().getString(R.string.dialog_calendar_rationale_title))
                        .setMessage(getResources().getString(R.string.dialog_calendar_rationale_body))
                        .setNegativeButton(getResources().getString(R.string.dialog_button_return), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Start Fragment Without Calendar Access
                                setupFragment();
                            }
                        })
                        .setPositiveButton(getResources().getString(R.string.dialog_button_confirm), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                //Request the permission
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.READ_CALENDAR},
                                        MY_PERMISSIONS_REQUEST_READ_CALENDAR);
                            }
                        }).create();
                AndroidUtilities.showRTLDialog(dialog);
            } else {
                //No explanation needed, We can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CALENDAR},
                        MY_PERMISSIONS_REQUEST_READ_CALENDAR);
            }
        } else {
            CalendarHelper.initCalendars();
            setupFragment();
        }
    }

    public void setupFragment(){
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        if(fragment == null)
            fragment = new HomeFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if(currentFragment instanceof HomeFragment && ((HomeFragment) currentFragment).onBackPressed()){
            return;
        } else {
            //Exit Confirmation Dialog
            AlertDialog dialog = new AlertDialog.Builder(this)
                    //.setView(mDialogView)
                    .setTitle(getResources().getString(R.string.dialog_exit_title))
                    .setMessage(getResources().getString(R.string.dialog_exit_body))
                    .setNegativeButton(getResources().getString(R.string.dialog_button_return), null)
                    .setPositiveButton(getResources().getString(R.string.dialog_button_exit), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            MainActivity.super.onBackPressed();
                        }
                    }).create();
            AndroidUtilities.showRTLDialog(dialog);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_READ_CALENDAR:

                //FIXME Refresh Fragment Instead of Recreating
                CalendarHelper.initCalendars();
                setupFragment();
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }
}
