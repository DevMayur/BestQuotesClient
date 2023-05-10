package com.ymg.beststatusandquotes.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import com.ymg.beststatusandquotes.Config;
import com.ymg.beststatusandquotes.R;
import com.ymg.beststatusandquotes.Utils.PrefManager;

public class PrimeActivity extends AppCompatActivity{

    Toolbar toolbar;
    TextView tvBuy,tvOne , tvThree, tvSix, tvYear;
    RelativeLayout llOne , llThree, llSix, llYear;
    ImageView oneImg , threeImg, sixImg, yearImg;
    PrefManager prf;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        prf = new PrefManager(this);



        tvBuy = findViewById(R.id.tvContinue);


        llOne = findViewById(R.id.one);
        llThree = findViewById(R.id.three);
        llSix = findViewById(R.id.six);
        llYear = findViewById(R.id.year);

        oneImg = findViewById(R.id.oneImg);
        threeImg = findViewById(R.id.threeImg);
        sixImg = findViewById(R.id.sixImg);
        yearImg = findViewById(R.id.yearImg);


        tvBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        llOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                oneImg.setVisibility(View.VISIBLE);
                threeImg.setVisibility(View.GONE);
                sixImg.setVisibility(View.GONE);
                yearImg.setVisibility(View.GONE);

                prf.setString("PrimePrice",Config.PRIME_ONE_MONTH);
                prf.setString("Subs_id",Config.PRIME_ONE_MONTH_SUB);


            }
        });

        llThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                oneImg.setVisibility(View.GONE);
                threeImg.setVisibility(View.VISIBLE);
                sixImg.setVisibility(View.GONE);
                yearImg.setVisibility(View.GONE);

                prf.setString("PrimePrice", Config.PRIME_THREE_MONTH);
                prf.setString("Subs_id",Config.PRIME_THREE_MONTH_SUB);

            }
        });

        llSix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                oneImg.setVisibility(View.GONE);
                threeImg.setVisibility(View.GONE);
                sixImg.setVisibility(View.VISIBLE);
                yearImg.setVisibility(View.GONE);

                prf.setString("PrimePrice",Config.PRIME_SIX_MONTH);
                prf.setString("Subs_id", Config.PRIME_SIX_MONTH_SUB);


            }
        });

        llYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                oneImg.setVisibility(View.GONE);
                threeImg.setVisibility(View.GONE);
                sixImg.setVisibility(View.GONE);
                yearImg.setVisibility(View.VISIBLE);
                prf.setString("PrimePrice",Config.PRIME_ONE_YEAR);
                prf.setString("Subs_id",Config.PRIME_ONE_YEAR_SUB);


            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    public void onResume() {
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