package com.ymg.beststatusandquotes.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.ymg.beststatusandquotes.BuildConfig;
import com.ymg.beststatusandquotes.R;
import com.ymg.beststatusandquotes.Utils.PrefManager;


public class SettingsActivity extends AppCompatActivity {

    TextView tvCurrentVersion;
    TextView tvSaveLocation;
    TextView tvCacheValue;
    TextView tvNotificationTag;
    TextView tvColumns;
    LinearLayout linearLayoutPolicyPrivacy;
    LinearLayout linearLayoutClearCache;
    LinearLayout linearLayoutColumes;
    Switch switchButtonNotification;
    Switch switchButtonSound;
    Switch switchDarkMode;
    AlertDialog alertDialog1;
    PrefManager prf;
    SharedPreferences sharedPrefs;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //This app is Created by YMG-developers
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getResources().getString(R.string.action_settings));
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        prf = new PrefManager(this);

        tvCurrentVersion = findViewById(R.id.tvCurrentVersion);
        tvSaveLocation = findViewById(R.id.tvSaveLocation);
        tvNotificationTag = findViewById(R.id.tvNotificationTag);
        linearLayoutPolicyPrivacy = findViewById(R.id.linearLayoutPolicyPrivacy);
        switchButtonNotification = findViewById(R.id.switchButtonNotification);
        switchButtonSound = findViewById(R.id.switchButtonSound);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        tvCurrentVersion.setText(BuildConfig.VERSION_NAME);
        tvSaveLocation.setText(getResources().getString(R.string.storagelocation)+getResources().getString(R.string.app_name));
        tvNotificationTag.setText(getResources().getString(R.string.label_notification)+getResources().getString(R.string.app_name));


        if (prf.getBoolean("SOUND")) {
            switchButtonSound.setChecked(true);
        } else {
            switchButtonSound.setChecked(false);
        }

        //Night Mode
        switchDarkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public final void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    prf.setNightModeState(true);
                    onResume();
                }else {
                    prf.setNightModeState(false);
                    onResume();
                }
            }
        });

        //notification switch //This app is Created by YMG-developers
        switchButtonSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public final void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){

                    prf.setBoolean("SOUND",true);
                }else {


                    prf.setBoolean("SOUND",false);
                }
            }
        });

        linearLayoutPolicyPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View v) {
                startActivity(new Intent(SettingsActivity.this, PrivacyActivity.class));
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        initCheck();
    }

    private void initCheck() {
        if (prf.loadNightModeState()==true){
            switchDarkMode.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            switchDarkMode.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }
}