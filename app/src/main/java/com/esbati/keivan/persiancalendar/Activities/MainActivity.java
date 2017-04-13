package com.esbati.keivan.persiancalendar.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.esbati.keivan.persiancalendar.Fragments.HomeFragment;
import com.esbati.keivan.persiancalendar.R;
import com.esbati.keivan.persiancalendar.Utils.AndroidUtilities;
import com.esbati.keivan.persiancalendar.Utils.SoundManager;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFragment();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
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


}
