package com.ymg.beststatusandquotes.Activity;

import static com.ymg.beststatusandquotes.Config.NOTIFICATION_TIME;
import static com.ymg.beststatusandquotes.Utils.Constant.ADMOB;
import static com.ymg.beststatusandquotes.Utils.Constant.AD_STATUS_ON;
import static com.ymg.beststatusandquotes.Utils.Constant.APPLOVIN;
import static com.ymg.beststatusandquotes.Utils.Constant.BANNER_HOME;
import static com.ymg.beststatusandquotes.Utils.Constant.STARTAPP;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.ferfalk.simplesearchview.SimpleSearchView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.material.navigation.NavigationView;

import com.onesignal.OneSignal;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.ymg.beststatusandquotes.Adapter.CategoryAdapter;
import com.ymg.beststatusandquotes.Model.Category;
import com.ymg.beststatusandquotes.R;
import com.ymg.beststatusandquotes.Utils.AdNetwork;
import com.ymg.beststatusandquotes.Utils.AdsPref;
import com.ymg.beststatusandquotes.Utils.AlarmReceiver;
import com.ymg.beststatusandquotes.Utils.DataBaseHandler;
import com.ymg.beststatusandquotes.Utils.GDPR;
import com.ymg.beststatusandquotes.Utils.PrefManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{


    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private PrefManager prefManager;
    private static final String TAG = "MainActivity";
    private SimpleSearchView simpleSearchView;
    private CategoryAdapter categoryAdapter;
    private final ArrayList<Category> imageArry = new ArrayList<Category>();
    private GridView dataList;
    private DataBaseHandler dataBaseHandler;
    private AdNetwork adNetwork;
    private AdsPref adsPref;
    private static final String ONESIGNAL_APP_ID = "f8bf3c7f-fb54-4b9e-9be9-xxxxxxxxxxxx";
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefManager = new PrefManager(this);
        dataBaseHandler = new DataBaseHandler(this);
        dataBaseHandler.openDataBase();

        adsPref = new AdsPref(this);

        if (adsPref.getAdStatus().equals(AD_STATUS_ON)) {
            switch (adsPref.getAdType()) {
                case STARTAPP:
                    StartAppSDK.setUserConsent(this, "pas", System.currentTimeMillis(), true);
                    StartAppAd.disableSplash();
                    break;
                case ADMOB:
                    MobileAds.initialize(this, initializationStatus -> {
                        Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                        for (String adapterClass : statusMap.keySet()) {
                            AdapterStatus status = statusMap.get(adapterClass);
                            assert status != null;
                            Log.d("MyApp", String.format("Adapter name: %s, Description: %s, Latency: %d", adapterClass, status.getDescription(), status.getLatency()));
                            Log.d("Open Bidding", "FAN open bidding with AdMob as mediation partner selected");
                        }
                    });
                    GDPR.updateConsentStatus(this);
                    break;
                case APPLOVIN:
                    AppLovinSdk.getInstance(this).setMediationProvider(AppLovinMediationProvider.MAX);
                    AppLovinSdk.getInstance(this).initializeSdk(config -> {
                    });
                    final String sdkKey = AppLovinSdk.getInstance(getApplicationContext()).getSdkKey();
                    if (!sdkKey.equals(getString(R.string.applovin_sdk_key))) {
                        Log.e(TAG, "AppLovin ERROR : Please update your sdk key in the manifest file.");
                    }
                    Log.d(TAG, "AppLovin SDK Key : " + sdkKey);
                    break;
            }
        }

        adNetwork = new AdNetwork(this);
        adNetwork.loadBannerAdNetwork(BANNER_HOME);
        adNetwork.loadApp(prefManager.getString("VDN"));

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        toolbar.setNavigationIcon(R.drawable.ic_menu_action);

        final List<Category> categories = dataBaseHandler.getAllCategories("");
        for (Category cat : categories) {
            imageArry.add(cat);
        }
        //Adapter Code
        categoryAdapter = new CategoryAdapter(this, R.layout.item_category,imageArry);
        dataList = findViewById(R.id.categoryList);
        dataList.setAdapter(categoryAdapter);
        dataList.setTextFilterEnabled(true);
        dataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked,
                                    int position, long idInDB) {

                Category srr = imageArry.get(position);
                Intent i = new Intent(getApplicationContext(),
                        QuotesActivity.class);
                i.putExtra("category", srr.getName());
                i.putExtra("mode", "isCategory");
                startActivity(i);


            }
        });

        //search
        simpleSearchView = findViewById(R.id.search_view);
        simpleSearchView.setOnQueryTextListener(new SimpleSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d("SimpleSearchView", "Submit:" + query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d("SimpleSearchView", "Text changed:" + newText);
                imageArry.clear();
                final List<Category> categories = dataBaseHandler.getAllCategories(newText);
                for (Category cat : categories) {
                    imageArry.add(cat);
                }
                dataList.setAdapter(categoryAdapter);
                return false;
            }
            @Override
            public boolean onQueryTextCleared() {
                Log.d("SimpleSearchView", "Text cleared");
                return false;
            }
        });




        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {

            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, NOTIFICATION_TIME);
            calendar.set(Calendar.MINUTE, 1);
            calendar.set(Calendar.SECOND, 1);

            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        simpleSearchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }//This app is Created by YMG-developers
        if (item.getItemId() == R.id.action_prime) {
            startActivity(new Intent(this,PrimeActivity.class));
        }//This app is Created by YMG-developers
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            showExitDialog();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        if (menuItem.getItemId() == R.id.nav_maker){
            Intent favorites = new Intent(this,
                    MakerActivity.class);
            favorites.putExtra("quote", "");
            startActivity(favorites);
        }
        if (menuItem.getItemId() == R.id.nav_today){
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Intent qteDay = new Intent(MainActivity.this,
                    QuoteActivity.class);
            qteDay.putExtra("id", preferences.getInt("id", prefManager.getInt("quoteToday")));
            qteDay.putExtra("mode", "qteday");
            startActivity(qteDay);
        }
        if (menuItem.getItemId() == R.id.nav_favotite){
            Intent favorites = new Intent(MainActivity.this, QuotesActivity.class);
            favorites.putExtra("mode", "isFavorite");
            startActivity(favorites);
        }
        if (menuItem.getItemId() == R.id.nav_rate){
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
            }catch (ActivityNotFoundException ex){
                startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
            }
        }
        if (menuItem.getItemId() == R.id.nav_share){
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            String shareBodyText = "https://play.google.com/store/apps/details?id="+getPackageName();
            intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT,shareBodyText);
            startActivity(Intent.createChooser(intent,"share via"));
        }
        if (menuItem.getItemId() == R.id.nav_contact){
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{getResources().getString(R.string.your_email)});
            i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            i.putExtra(Intent.EXTRA_TEXT   , getResources().getString(R.string.email_text));
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(MainActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
        if (menuItem.getItemId() == R.id.nav_about){
            showAboutDialog();
        }
        if (menuItem.getItemId() == R.id.nav_settings){
            startActivity(new Intent(MainActivity.this,SettingsActivity.class));
        }
        if (menuItem.getItemId() == R.id.nav_insta){

            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.instagram.com/"));
            startActivity(browserIntent);
        }
        if (menuItem.getItemId() == R.id.nav_night){
            return false;
        }
        return false;
    }

    private void showExitDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_exit);

        LinearLayout dialog_btn=dialog.findViewById(R.id.mbtnYes);
        LinearLayout mbtnNo=dialog.findViewById(R.id.mbtnNo);
        dialog_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mbtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showAboutDialog() {
        final Dialog dialog = new Dialog(MainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_about);

        AppCompatButton btn_done=dialog.findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (!prefManager.getString("VDN").equals(Config.DEVELOPER_NAME)){
//            finish();
//        }
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