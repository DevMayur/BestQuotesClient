package com.ymg.beststatusandquotes.Activity;

import static com.ymg.beststatusandquotes.Config.ADMOB_PUB_ID;
import static com.ymg.beststatusandquotes.Config.AD_STATUS;
import static com.ymg.beststatusandquotes.Config.AD_TYPE;
import static com.ymg.beststatusandquotes.Config.APPLOVIN_BANNER_ID;
import static com.ymg.beststatusandquotes.Config.APPLOVIN_INTER_ID;
import static com.ymg.beststatusandquotes.Config.BANNER_ID;
import static com.ymg.beststatusandquotes.Config.DEVELOPER_NAME;
import static com.ymg.beststatusandquotes.Config.FACEBOOK_BANNER_ID;
import static com.ymg.beststatusandquotes.Config.FACEBOOK_INTER_ID;
import static com.ymg.beststatusandquotes.Config.FACEBOOK_NATIVE_ID;
import static com.ymg.beststatusandquotes.Config.INTERSTITIAL_ADS_INTERVAL;
import static com.ymg.beststatusandquotes.Config.INTER_ID;
import static com.ymg.beststatusandquotes.Config.NATIVE_ADS_INTERVAL;
import static com.ymg.beststatusandquotes.Config.NATIVE_ADS_POSITION;
import static com.ymg.beststatusandquotes.Config.NATIVE_ID;
import static com.ymg.beststatusandquotes.Config.STARTAPP_ID;
import static com.ymg.beststatusandquotes.Config.UNITY_BANNER_ID;
import static com.ymg.beststatusandquotes.Config.UNITY_GAME_ID;
import static com.ymg.beststatusandquotes.Config.UNITY_INTER_ID;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ymg.beststatusandquotes.R;
import com.ymg.beststatusandquotes.Utils.AdsPref;
import com.ymg.beststatusandquotes.Utils.Anims;
import com.ymg.beststatusandquotes.Utils.Constant;
import com.ymg.beststatusandquotes.Utils.PrefManager;
import com.ymg.beststatusandquotes.Utils.Sync;
import com.ymg.beststatusandquotes.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends AppCompatActivity {

    private TextView developers;
    private PrefManager prf;
    private AdsPref adsPref;
    private RelativeLayout parentLayout;
    private AppCompatImageView logo;
    public static final String TAG = "ActivitySplash";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prf = new PrefManager(this);
        adsPref = new AdsPref(this);

        developers = findViewById(R.id.developers);
        parentLayout = findViewById(R.id.parentLayout);

        logo = findViewById(R.id.logo);
        Anims aVar = new Anims(this.getResources().getDrawable(R.drawable.logo));
        aVar.m14932a(true);
        logo.setImageDrawable(aVar);
        logo.setVisibility(View.VISIBLE);

        if (Tools.isConnect(this)){
            getData();
        }else {
            onSplashFinished();
        }
        getDate();

    }

    private void onSplashFinished() {
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // If you want to modify a view in your Activity
                SplashActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int lastSync = 0; // Replace with the actual timestamp of the last sync, if available
                        final ProgressDialog progressDialog = new ProgressDialog(SplashActivity.this);
                        progressDialog.setMessage("Syncing data...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        new Sync().syncData(lastSync, SplashActivity.this, new Sync.SyncCallback() {
                            @Override
                            public void onSyncFinished() {
                                progressDialog.dismiss();
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                    }
                });
            }
        }, 3000);
    }


    private void getDate() {

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        int min = 8;
        int max = 5000;

        Random r = new Random();
        int i1 = r.nextInt(max - min + 1) + min;

        if (date.equals(prf.getString("TodayDate"))){

        }else {
            prf.setInt("quoteToday",i1);
            prf.setString("TodayDate", date);
        }
    }

    private void getConfig() {
        adsPref.saveAds(
                AD_STATUS,
                AD_TYPE,
                ADMOB_PUB_ID,
                ADMOB_PUB_ID,
                BANNER_ID,
                INTER_ID,
                NATIVE_ID,
                DEVELOPER_NAME,
                FACEBOOK_BANNER_ID,
                FACEBOOK_INTER_ID,
                FACEBOOK_NATIVE_ID,
                STARTAPP_ID,
                UNITY_GAME_ID,
                UNITY_BANNER_ID,
                UNITY_INTER_ID,
                APPLOVIN_BANNER_ID,
                APPLOVIN_INTER_ID,
                INTERSTITIAL_ADS_INTERVAL,
                NATIVE_ADS_INTERVAL,
                NATIVE_ADS_POSITION,
                prf.getString("VDN"),
                ""
        );
    }

    private void getData() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET, Constant.URL_DATA, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    final String VDN = response.getString("DN");
                    prf.setString("VDN",VDN);
                    getConfig();
                    onSplashFinished();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MyApplication.getInstance().addToRequestQueue(jsonObjReq);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCheck();
    }

    private void initCheck() {
        if (prf.loadNightModeState()==true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
}
