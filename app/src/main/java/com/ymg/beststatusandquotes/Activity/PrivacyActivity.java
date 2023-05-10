package com.ymg.beststatusandquotes.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.ymg.beststatusandquotes.Config;
import com.ymg.beststatusandquotes.R;
import com.ymg.beststatusandquotes.Utils.PrefManager;

public class PrivacyActivity extends AppCompatActivity {

    PrefManager prefManager;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        prefManager = new PrefManager(this);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getResources().getString(R.string.policy_privacy));
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        WebView mWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.loadUrl(Config.PRIVACY_POLICY_URL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCheck();
    }

    private void initCheck() {
        if (prefManager.loadNightModeState()==true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}