package com.esbati.keivan.persiancalendar.features.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.esbati.keivan.persiancalendar.R
import com.esbati.keivan.persiancalendar.utils.showThemedDialog

private const val PERMISSIONS_REQUEST_READ_CALENDAR = 76

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme_NoActionBar)
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
            window.decorView.layoutDirection = View.LAYOUT_DIRECTION_RTL

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupFragment()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CALENDAR)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALENDAR)) {

                // Permission Request Explanation
                AlertDialog.Builder(this)
                        .setTitle(resources.getString(R.string.dialog_calendar_rationale_title))
                        .setMessage(resources.getString(R.string.dialog_calendar_rationale_body))
                        .setNegativeButton(resources.getString(R.string.dialog_button_return), null)
                        .setPositiveButton(resources.getString(R.string.dialog_button_confirm)) { _, _ ->
                            //Request the permission
                            ActivityCompat.requestPermissions(this@MainActivity,
                                    arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                                    PERMISSIONS_REQUEST_READ_CALENDAR)
                        }
                        .showThemedDialog()
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                        PERMISSIONS_REQUEST_READ_CALENDAR)
            }
        }
    }

    private fun setupFragment(refresh: Boolean = false) {
        var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (fragment == null || refresh)
            fragment = HomeFragment()

        supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commitAllowingStateLoss()
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment is HomeFragment && currentFragment.onBackPressed()) {
            return
        } else {
            //Exit Confirmation Dialog
            AlertDialog.Builder(this)
                    .setTitle(resources.getString(R.string.dialog_exit_title))
                    .setMessage(resources.getString(R.string.dialog_exit_body))
                    .setNegativeButton(resources.getString(R.string.dialog_button_return), null)
                    .setPositiveButton(resources.getString(R.string.dialog_button_exit)) {
                        _, _ -> super@MainActivity.onBackPressed()
                    }
                    .showThemedDialog()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSIONS_REQUEST_READ_CALENDAR ->
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    // permission was granted, reload fragment
                    setupFragment(true)

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}