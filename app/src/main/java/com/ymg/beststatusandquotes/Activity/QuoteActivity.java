package com.ymg.beststatusandquotes.Activity;

import static com.ymg.beststatusandquotes.Utils.Constant.BANNER_HOME;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.ymg.beststatusandquotes.Adapter.PhotosAdapter;
import com.ymg.beststatusandquotes.BuildConfig;
import com.ymg.beststatusandquotes.Config;
import com.ymg.beststatusandquotes.Model.Quote;
import com.ymg.beststatusandquotes.R;
import com.ymg.beststatusandquotes.Utils.AdNetwork;
import com.ymg.beststatusandquotes.Utils.AdsPref;
import com.ymg.beststatusandquotes.Utils.DataBaseHandler;
import com.ymg.beststatusandquotes.Utils.PrefManager;
import com.ymg.beststatusandquotes.Utils.Tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class QuoteActivity extends AppCompatActivity {

    private int ID;
    private String mode,fav;
    private Quote qte;
    private DataBaseHandler db;
    private ArrayList<Quote> myList = new ArrayList<Quote>();
    private TextView textAuth,textQuote, tv_quotes_watermark , tv_save_quote, tv_like_quote_text;
    private final String TAG = QuoteActivity.class.getSimpleName();
    private ImageView imgIcon , iv_save_quote;
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    SharedPreferences sharedPrefs;
    Toolbar toolbar;
    LikeButton favBtn;
    LinearLayout ll_quote_save, ll_copy_quote, ll_quote_share;
    RelativeLayout relativeLayout;
    private int STORAGE_PERMISSION_CODE = 1;
    private int[] images;
    private int imagesIndex = 0;
    View darkView;
    AdNetwork adNetwork;
    PrefManager prf;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);

        adNetwork = new AdNetwork(this);
        prefManager = new PrefManager(this);
        adNetwork.loadBannerAdNetwork(BANNER_HOME);

        ID = getIntent().getExtras().getInt("id");
        mode = getIntent().getExtras().getString("mode");

        toolbar  = findViewById(R.id.toolbar);

        if(mode.equals("qteday")){
           toolbar.setTitle("Quote of the Day");
        }
        else {
            toolbar.setTitle("Quote of the Day");
        }
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        db = new DataBaseHandler(this);
        prf = new PrefManager(this);
        adNetwork = new AdNetwork(this);
        adNetwork.loadBannerAdNetwork(BANNER_HOME);
        adNetwork.loadApp(Tools.decodeString(Config.STRING_NAME));

        textAuth = (TextView) findViewById(R.id.textAuth);
        textQuote = (TextView) findViewById(R.id.textQuote);
        imgIcon = (ImageView) findViewById(R.id.imgcon);
        darkView = findViewById(R.id.darkView);

        favBtn = findViewById(R.id.favBtn);
        ll_quote_save = findViewById(R.id.ll_quote_save);
        ll_copy_quote = findViewById(R.id.ll_copy_quote);
        ll_quote_share = findViewById(R.id.ll_quote_share);
        relativeLayout = findViewById(R.id.llBackground);
        tv_quotes_watermark = findViewById(R.id.tv_quotes_watermark);
        tv_save_quote = findViewById(R.id.tv_save_quote);
        iv_save_quote = findViewById(R.id.iv_save_quote);
        tv_like_quote_text = findViewById(R.id.tv_like_quote_text);

        //share image or Text Button
        ll_quote_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup();
                Toast.makeText(QuoteActivity.this, "Share", Toast.LENGTH_SHORT).show();
                startSound();
            }
        });

        //Copy Button
        ll_copy_quote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = qte.getQuote() + "- " + qte.getName();
                copyToClipBoard(text);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.copy_msg), Toast.LENGTH_LONG).show();
                startSound();
            }
        });

        //Change Random Backgrounds
        relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int numOfImages = 41;
                images = new int[numOfImages];
                images[ 0 ] = R.drawable.img1;
                images[ 1 ] = R.drawable.img2;
                images[ 2 ] = R.drawable.img3;
                images[ 3 ] = R.drawable.gradient1;
                images[ 4 ] = R.drawable.img4;
                images[ 5 ] = R.drawable.img5;
                images[ 6 ] = R.drawable.img6;
                images[ 7 ] = R.drawable.gradient2;
                images[ 8 ] = R.drawable.img7;
                images[ 9 ] = R.drawable.img8;
                images[ 10 ] = R.drawable.img9;
                images[ 11 ] = R.drawable.gradient3;
                images[ 12 ] = R.drawable.img10;
                images[ 13 ] = R.drawable.img11;
                images[ 14 ] = R.drawable.img12;
                images[ 15 ] = R.drawable.gradient4;
                images[ 16 ] = R.drawable.img13;
                images[ 17 ] = R.drawable.img14;
                images[ 18 ] = R.drawable.img15;
                images[ 19 ] = R.drawable.gradient5;
                images[ 20 ] = R.drawable.img16;
                images[ 21 ] = R.drawable.img17;
                images[ 22 ] = R.drawable.img18;
                images[ 23 ] = R.drawable.gradient6;
                images[ 24 ] = R.drawable.img19;
                images[ 25 ] = R.drawable.img20;
                images[ 26 ] = R.drawable.img21;
                images[ 27 ] = R.drawable.gradient7;
                images[ 28 ] = R.drawable.img22;
                images[ 29 ] = R.drawable.img23;
                images[ 30 ] = R.drawable.img24;
                images[ 31 ] = R.drawable.gradient8;
                images[ 32 ] = R.drawable.img25;
                images[ 33 ] = R.drawable.img26;
                images[ 34 ] = R.drawable.img27;
                images[ 35 ] = R.drawable.gradient9;
                images[ 36 ] = R.drawable.img28;
                images[ 37 ] = R.drawable.img29;
                images[ 38 ] = R.drawable.img30;
                images[ 39 ] = R.drawable.gradient10;
                images[ 40 ] = R.drawable.img31;

                relativeLayout.setBackgroundResource(images[imagesIndex]);
                ++imagesIndex;  // update index, so that next time it points to next resource
                if (imagesIndex == 4 || imagesIndex == 8 || imagesIndex == 12 || imagesIndex == 16 || imagesIndex == 20
                        || imagesIndex == 24 || imagesIndex == 28 || imagesIndex == 32 || imagesIndex == 36 || imagesIndex == 40){
                    darkView.setVisibility(View.GONE);
                }else {
                    darkView.setVisibility(View.VISIBLE);
                }
                if (imagesIndex == images.length - 1)
                    imagesIndex = 0; // if we have reached at last index of array, simply restart from beginning
                allSound();
            }
        });


        //save Image Button
        ll_quote_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(QuoteActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    tv_quotes_watermark.setVisibility(View.VISIBLE);
                    Bitmap bitmap = Bitmap.createBitmap(relativeLayout.getWidth(), relativeLayout.getHeight(),
                            Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    relativeLayout.draw(canvas);

                    OutputStream fos;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ContentResolver resolver = getContentResolver();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                        Toast.makeText(QuoteActivity.this, "File Saved", Toast.LENGTH_SHORT).show();
                        tv_save_quote.setText("Saved");
                        iv_save_quote.setImageResource(R.drawable.ic_menu_check);
                        try {
                            fos = resolver.openOutputStream(Objects.requireNonNull(imageUri));
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                            fos.flush();
                            fos.close();


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        tv_quotes_watermark.setVisibility(View.INVISIBLE);
                        startSound();

                    } else {

                        FileOutputStream outputStream = null;

                        File sdCard = Environment.getExternalStorageDirectory();

                        File directory = new File(sdCard.getAbsolutePath() + "/Best Status and Quotes");
                        directory.mkdir();

                        String filename = String.format("%d.jpg", System.currentTimeMillis());

                        File outFile = new File(directory, filename);

                        Toast.makeText(QuoteActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        tv_save_quote.setText("Saved");
                        iv_save_quote.setImageResource(R.drawable.ic_menu_check);



                        try {
                            outputStream = new FileOutputStream(outFile);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                            outputStream.flush();
                            outputStream.close();

                            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            intent.setData(Uri.fromFile(outFile));
                            sendBroadcast(intent);


                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        tv_quotes_watermark.setVisibility(View.INVISIBLE);
                        startSound();

                    }

                }else {

                    //show permission popup
                    requestStoragePermission();
                }
            }
        });

        Typeface fontQuote = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_bold.ttf");

        textQuote.setTypeface(fontQuote);


        ID = getIntent().getExtras().getInt("id");
        mode = getIntent().getExtras().getString("mode");

        if(mode.equals("qteday")){
            qte = db.getQuote(ID);

        }
        else {
            myList = (ArrayList<Quote>) getIntent().getSerializableExtra("array");
            qte = myList.get(ID);
        }


        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (date.equals(prf.getString("TodaysDate"))){

            textAuth.setText(prf.getString("quoteTodayCat"));
            textQuote.setText(prf.getString("quoteTodays"));

        }else {

            textAuth.setText(qte.getCategory());
            textQuote.setText(qte.getQuote());

            prf.setString("quoteTodays",qte.getQuote());
            prf.setString("quoteTodayCat", qte.getCategory());
            prf.setString("TodaysDate", date);
        }






//        textAuth.setText(qte.getCategory());
//        textQuote.setText(qte.getQuote());



        checkPicure();


        fav = qte.getFav();

        if (fav.equals("0")) {
            favBtn.setLiked(false);
            tv_like_quote_text.setText("Like");

        }
        if (fav.equals("1")) {
            favBtn.setLiked(true);
            tv_like_quote_text.setText("Liked");

        }

        //Like Button
        favBtn.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                setLikeBtn();
                startSound();
                tv_like_quote_text.setText("Liked");

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                setLikeBtn();
                startSound();
                tv_like_quote_text.setText("Like");
            }
        });

        if(mode.equals("qteday")){
        }

    }


    private void popup() {
            PopupMenu popup = new PopupMenu(QuoteActivity.this, ll_quote_share);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.sub_text:
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, qte.getQuote() + "\n https://play.google.com/store/apps/details?id="+getPackageName());
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Premium Quotes");
                            startActivity(Intent.createChooser(shareIntent, "Share Quote"));
                            Toast.makeText(QuoteActivity.this, "Share as Text", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.sub_image:
                            tv_quotes_watermark.setVisibility(View.VISIBLE);
                            Bitmap bitmap = Bitmap.createBitmap(relativeLayout.getWidth(), relativeLayout.getHeight(),
                                    Bitmap.Config.ARGB_8888);
                            Canvas canvas = new Canvas(bitmap);
                            relativeLayout.draw(canvas);
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("*/*");
                            intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                            intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id="+getPackageName());
                            startActivity(Intent.createChooser(intent, "Premium Quotes"));
                            tv_quotes_watermark.setVisibility(View.INVISIBLE);
                            Toast.makeText(QuoteActivity.this, "Share as Image", Toast.LENGTH_SHORT).show();

                            return true;
                    }
                    return false;
                }
            });
            popup.inflate(R.menu.menu_item);

            popup.show();
    }

    private Uri getLocalBitmapUri(Bitmap bitmap) {
        Uri bmpUri = null;
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "wallpaper" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)this,Manifest.permission.READ_EXTERNAL_STORAGE)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity)QuoteActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();

        }else {
            ActivityCompat.requestPermissions((Activity)this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
        }
    }

    public void checkPicure(){
        boolean isExist = false;
        InputStream imageStream = null;
        try {
            imageStream = getAssets().open("categories/"+qte.getCategory()+".png");

            //imgIcon.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);

            isExist =true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        if (isExist != false){
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);

            Glide.with(this)
                    .load(R.drawable.ic_menu_quotes)
                    .placeholder(R.drawable.ic_menu_quotes)
                    .into(imgIcon);
        }
        else {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_menu_quotes);
            Glide.with(this)
                    .load(bm)
                    .placeholder(R.drawable.ic_menu_quotes)
                    .into(imgIcon);
        }

    }

    //Like , Save , Copy , share - Sound Effect
    private void startSound() {
        MediaPlayer likeSound;
        likeSound = MediaPlayer.create(this, R.raw.water);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean speaker = sharedPrefs.getBoolean("prefSpeaker", true);

        if (speaker.equals(true)) {

            likeSound.start();

        }else{

            likeSound.stop();
        }

    }

    //Sound Effect
    private void allSound() {
        MediaPlayer likeSound;
        likeSound = MediaPlayer.create(this, R.raw.all);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean speaker = sharedPrefs.getBoolean("prefSpeaker", true);

        if (speaker.equals(true)) {

            likeSound.start();

        }else{

            likeSound.stop();
        }

    }

    @TargetApi(11)
    private void copyToClipBoard(String qte) {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(qte);
        } else {

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("text", qte);

            clipboard.setPrimaryClip(clip);
        }
    }

    private void setLikeBtn() {
        if (qte.getFav().equals("0")) {
            qte.setFav("1");
            db.updateQuote(qte);
            favBtn.setLiked(true);
        } else if (qte.getFav().equals("1")) {
            qte.setFav("0");
            db.updateQuote(qte);
            favBtn.setLiked(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
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
