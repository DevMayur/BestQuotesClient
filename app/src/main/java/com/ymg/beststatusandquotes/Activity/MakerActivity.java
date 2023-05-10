package com.ymg.beststatusandquotes.Activity;

import static com.ymg.beststatusandquotes.Utils.Constant.ADMOB;
import static com.ymg.beststatusandquotes.Utils.Constant.AD_STATUS_ON;
import static com.ymg.beststatusandquotes.Utils.Constant.BANNER_HOME;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.ymg.beststatusandquotes.Adapter.PhotosAdapter;
import com.ymg.beststatusandquotes.BuildConfig;
import com.ymg.beststatusandquotes.Config;
import com.ymg.beststatusandquotes.R;
import com.ymg.beststatusandquotes.Utils.AdNetwork;
import com.ymg.beststatusandquotes.Utils.AdsPref;
import com.ymg.beststatusandquotes.Utils.PrefManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class MakerActivity extends AppCompatActivity {

    RelativeLayout tvSize, tvColor, tvFont, tvAlign, tvPadding, tvStyle, tvShadow;
    RelativeLayout tvGradiant, tvBackgroundColor, tvBackgroundImage, tvOpecity, tvSaveImage, tvShare;
    private final String TAG = MakerActivity.class.getSimpleName();
    CardView tvPrime;
    EditText tvMaker;
    RelativeLayout rlBackground;
    ImageView backImage;
    CardView tvEsize, tvEfont, cvPadding, cvGradiant, cvOpacity;
    int shade = 0;
    int b0 = 20;
    int currentColor = 0xffffffff;
    int pLeft = 100;
    int pRight = 100;
    int pTop = 100;
    int pBottom = 100;
    int X;
    int Y;
    Uri uri;
    Boolean allreadyLoad = true;
    Boolean interLoad = true;
    View viewLayout;
    private int[] images;
    private int imagesIndex = 0;
    PrefManager prf;
    RewardedAd mRewardedAd;
    private AdNetwork adNetwork;
    private AdsPref adsPref;
    private RelativeLayout watermarkRelative;
    private ImageView watermarkIcon;

    public static final int REQUEST_IMAGE = 100;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maker);

        prf = new PrefManager(this);
        adsPref = new AdsPref(this);
        adNetwork = new AdNetwork(this);
        adNetwork.loadBannerAdNetwork(BANNER_HOME);
        loadRewardedAds();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        //extra
        tvEsize = findViewById(R.id.textSizeCardView);
        tvEfont = findViewById(R.id.textFontStyleCardView);
        cvPadding = findViewById(R.id.textPaddingCardView);
        cvGradiant = findViewById(R.id.gradientCardView);
        cvOpacity = findViewById(R.id.imageOpacityCardView);


        //main
        rlBackground = findViewById(R.id.idForSaving);
        backImage = findViewById(R.id.imageView);
        tvMaker = findViewById(R.id.editText);
        tvSize =  findViewById(R.id.textSizeClickCardView);
        tvColor = findViewById(R.id.textColorClickCardView);
        tvFont =  findViewById(R.id.textFontClickCardView);
        tvAlign =  findViewById(R.id.textAlignClickCardView);
        tvPadding =  findViewById(R.id.textPaddingClickCardView);
        tvStyle =  findViewById(R.id.textStyleClickCardView);
        tvShadow = findViewById(R.id.textShadowClickCardView);
        tvGradiant = findViewById(R.id.gradientClickCardView);
        tvBackgroundColor =  findViewById(R.id.bgColorClickCardView);
        tvBackgroundImage =  findViewById(R.id.imageClickCardView);
        tvOpecity =  findViewById(R.id.imageOpacityClickCirdView);
        tvSaveImage =findViewById(R.id.saveClickCardView);
        tvShare =  findViewById(R.id.shareClickCardView);
        tvPrime = findViewById(R.id.aboutClickCardView);
        watermarkRelative = findViewById(R.id.watermarkRelative);
        watermarkIcon = findViewById(R.id.watermarkIcon);


        String quote = getIntent().getExtras().getString("quote");
        imagesIndex = getIntent().getExtras().getInt("image");
        if(quote != null){
            tvMaker.setText(quote);
            if (imagesIndex == 0){
                backImage.setVisibility(View.GONE);
            }else {
                loadImages();
                viewLayout.setVisibility(View.VISIBLE);
                tvMaker.setTextColor(getResources().getColor(R.color.white));
            }
        }else {
            tvMaker.setText("");

        }

        //editText
        tvMaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvMaker.setCursorVisible(true);
                tvMaker.setHint("");
            }
        });

        //textView size change // app is created by ymg developers
        tvSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvEsize.setVisibility(View.VISIBLE);
                SeekBar seekBar = (SeekBar) findViewById(R.id.textSizeSeekbar);
                seekBar.setProgress(b0);
                tvMaker.setTextSize((float) b0);
                seekBar.setOnSeekBarChangeListener(new seekbaralert());
                ((Button) findViewById(R.id.textSizeDoneButton)).setOnClickListener(new n());

            }
        });

        //textView Color // app is created by ymg developers
        tvColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new AmbilWarnaDialog (MakerActivity.this, V, new u()).show();
                chooseTextColor(false);
            }
        });

        //textView fonts
        tvFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvEfont.setVisibility(View.VISIBLE);

                final Typeface a18 = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.ttf");
                final Typeface a2 = Typeface.createFromAsset(getAssets(), "fonts/1.ttf");
                final Typeface a3 = Typeface.createFromAsset(getAssets(), "fonts/montserrat_bold.ttf");
                final Typeface a4 = Typeface.createFromAsset(getAssets(), "fonts/2.ttf");
                final Typeface a5 = Typeface.createFromAsset(getAssets(), "fonts/3.ttf");
                final Typeface a6 = Typeface.createFromAsset(getAssets(), "fonts/4.ttf");
                final Typeface a7 = Typeface.createFromAsset(getAssets(), "fonts/5.ttf");
                final Typeface a8 = Typeface.createFromAsset(getAssets(), "fonts/6.ttf");
                final Typeface a9 = Typeface.createFromAsset(getAssets(), "fonts/7.ttf");
                final Typeface a10 = Typeface.createFromAsset(getAssets(), "fonts/8.ttf");
                final Typeface a11 = Typeface.createFromAsset(getAssets(), "fonts/9.ttf");
                final Typeface a12 = Typeface.createFromAsset(getAssets(), "fonts/10.ttf");
                final Typeface a13 = Typeface.createFromAsset(getAssets(), "fonts/11.ttf");
                final Typeface a14 = Typeface.createFromAsset(getAssets(), "fonts/12.ttf");
                final Typeface a15 = Typeface.createFromAsset(getAssets(), "fonts/13.ttf");
                final Typeface a16 = Typeface.createFromAsset(getAssets(), "fonts/14.ttf");
                final Typeface a17 = Typeface.createFromAsset(getAssets(), "fonts/15.ttf");

                TextView textView = (TextView) findViewById(R.id.fontButton1);
                TextView textView2 = (TextView) findViewById(R.id.fontButton2);
                TextView textView3 = (TextView) findViewById(R.id.fontButton3);
                TextView textView4 = (TextView) findViewById(R.id.fontButton4);
                TextView textView5 = (TextView) findViewById(R.id.fontButton5);
                TextView textView6 = (TextView) findViewById(R.id.fontButton6);
                TextView textView7 = (TextView) findViewById(R.id.fontButton7);
                TextView textView8 = (TextView) findViewById(R.id.fontButton8);
                TextView textView9 = (TextView) findViewById(R.id.fontButton9);
                TextView textView10 = (TextView) findViewById(R.id.fontButton10);
                TextView textView11 = (TextView) findViewById(R.id.fontButton11);
                TextView textView12 = (TextView) findViewById(R.id.fontButton12);
                TextView textView13 = (TextView) findViewById(R.id.fontButton13);
                TextView textView14 = (TextView) findViewById(R.id.fontButton14);
                TextView textView15 = (TextView) findViewById(R.id.fontButton15);
                TextView textView16 = (TextView) findViewById(R.id.fontButton16);
                Button button = (Button) findViewById(R.id.textFontStyleDoneButton);
                textView.setTypeface(a2);
                textView2.setTypeface(a3);
                textView3.setTypeface(a4);
                textView4.setTypeface(a5);
                textView5.setTypeface(a6);
                textView6.setTypeface(a7);
                textView7.setTypeface(a8);
                textView8.setTypeface(a9);
                textView9.setTypeface(a10);
                textView10.setTypeface(a11);
                textView11.setTypeface(a12);
                textView12.setTypeface(a13);
                textView13.setTypeface(a14);
                textView14.setTypeface(a15);
                textView15.setTypeface(a16);
                textView16.setTypeface(a17);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a2);
                    }
                });
                textView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a3);
                    }
                });
                textView3.setOnClickListener(new View.OnClickListener() {
                    @Override // app is created by ymg developers
                    public void onClick(View v) {
                        tvMaker.setTypeface(a4);
                    }
                });
                textView4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a5);
                    }
                });
                textView5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a6);
                    }
                });
                textView6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a7);
                    }
                });
                textView7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a8);
                    }
                });
                textView8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a9);
                    }
                });
                textView9.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a10);
                    }
                });
                textView10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a11);
                    }
                });
                textView11.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a12);
                    }
                });
                textView12.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a13);
                    }
                });
                textView13.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a14);
                    }
                });
                textView14.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a15);
                    }
                });
                textView15.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a16);
                    }
                });
                textView16.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvMaker.setTypeface(a17);
                    }
                });
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tvEfont.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });

        //textView alignmnet // app is created by ymg developers
        tvAlign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i2;
                ImageView imageView = (ImageView) findViewById(R.id.textAligmentImageView);
                if (String.valueOf(tvMaker.getGravity()).equals("17")) {
                    tvMaker.setGravity(16);
                    i2 = R.drawable.ic_menu_text_align;
                } else if (String.valueOf(tvMaker.getGravity()).equals("8388627")) {
                    tvMaker.setGravity(21);
                    i2 = R.drawable.ic_menu_text_align;
                } else if (String.valueOf(tvMaker.getGravity()).equals("21")) {
                    tvMaker.setGravity(17);
                    i2 = R.drawable.ic_menu_text_align;
                } else {
                    return;
                }
                imageView.setImageResource(i2);

            }
        });

        //textView padding
        tvPadding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvPadding.setVisibility(View.VISIBLE);
                SeekBar seekBar = (SeekBar) findViewById(R.id.leftPaddingSeekbar);
                SeekBar seekBar2 = (SeekBar) findViewById(R.id.rightPaddingSeekbar);
                SeekBar seekBar3 = (SeekBar) findViewById(R.id.topPaddingSeekbar);
                SeekBar seekBar4 = (SeekBar) findViewById(R.id.bottomPaddingSeekbar);
                seekBar.setProgress(pLeft / 2);
                seekBar2.setProgress(pRight / 2);
                seekBar3.setProgress(pTop / 2);
                seekBar4.setProgress(pBottom / 2);
                tvMaker.setPadding(pLeft, pRight, pTop, pBottom);
                seekBar.setOnSeekBarChangeListener(new paddingLeft());
                seekBar2.setOnSeekBarChangeListener(new paddingRight());
                seekBar3.setOnSeekBarChangeListener(new paddingTop());
                seekBar4.setOnSeekBarChangeListener(new paddingBottom());
                ((Button) findViewById(R.id.textPaddingDoneButton)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cvPadding.setVisibility(View.INVISIBLE);
                    }
                });

            }
            class paddingLeft implements SeekBar.OnSeekBarChangeListener {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    pLeft = progress * 2;
                    tvMaker.setPadding(pLeft, pRight, pTop, pBottom);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            }
            class paddingRight implements SeekBar.OnSeekBarChangeListener {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    pRight = progress * 2;
                    tvMaker.setPadding(pLeft, pRight, pTop, pBottom);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            }
            class paddingTop implements SeekBar.OnSeekBarChangeListener {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    pTop = progress * 2;
                    tvMaker.setPadding(pLeft, pRight, pTop, pBottom);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            }
            class paddingBottom implements SeekBar.OnSeekBarChangeListener {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    pBottom = progress * 2;
                    tvMaker.setPadding(pLeft, pRight, pTop, pBottom);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            }
        });

        //watermark

        //textView Style
        tvStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) findViewById(R.id.textStyleTextView);
                tvMaker.setTypeface(tvMaker.getTypeface(), Typeface.NORMAL);
                if (tvMaker.getTypeface() == null) {
                    textView.setTypeface(tvMaker.getTypeface(), Typeface.BOLD);
                    textView.setText("B");
                    tvMaker.setTypeface(tvMaker.getTypeface(), Typeface.BOLD);
                } else if (tvMaker.getTypeface().getStyle() == Typeface.NORMAL) {
                    textView.setTypeface(null, Typeface.BOLD);
                    textView.setText("B");
                    tvMaker.setTypeface(null, Typeface.BOLD);
                } else if (tvMaker.getTypeface().getStyle() == Typeface.BOLD) {
                    textView.setTypeface(null, Typeface.BOLD_ITALIC);
                    textView.setText("I");
                    tvMaker.setTypeface(null, Typeface.BOLD_ITALIC);
                } else if (tvMaker.getTypeface().getStyle() == Typeface.BOLD_ITALIC) {
                    textView.setTypeface(null, Typeface.NORMAL);
                    textView.setText("N");
                    tvMaker.setTypeface(null, Typeface.NORMAL);
                }

            }
        });

        //textView Shadow
        tvShadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shade++;
                int i = shade % 2;
                EditText editText = tvMaker;
                if (i == 0) {
                    editText.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
                } else {
                    editText.setShadowLayer(6.5f, -1.0f, -4.0f, -7829368);
                }
            }
        });

        //text background gradiant
        tvGradiant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvGradiant.setVisibility(View.VISIBLE);
                final Button button = (Button) findViewById(R.id.gradientFirstColorButton);
                final Button button2 = (Button) findViewById(R.id.gradientSecoundColorButton);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ColorPickerDialogBuilder
                                .with(MakerActivity.this)
                                .setTitle("Choose color")
                                .initialColor(currentColor)
                                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                .density(12)

                                .setOnColorSelectedListener(new OnColorSelectedListener() {
                                    @Override
                                    public void onColorSelected(int selectedColor) {

                                        X = selectedColor;
                                        button.setBackgroundColor(X);
                                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{X,Y});
                                        gradientDrawable.setCornerRadius(0.0f);
                                        rlBackground.setBackground(gradientDrawable);
                                    }
                                })
                                .setPositiveButton("ok", new ColorPickerClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                        X = selectedColor;
                                        button.setBackgroundColor(X);
                                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{X,Y});
                                        gradientDrawable.setCornerRadius(0.0f);
                                        rlBackground.setBackground(gradientDrawable);
                                    }
                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .build()
                                .show();
                    }
                });

                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ColorPickerDialogBuilder
                                .with(MakerActivity.this)
                                .setTitle("Choose color")
                                .initialColor(currentColor)
                                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                                .density(12)

                                .setOnColorSelectedListener(new OnColorSelectedListener() {
                                    @Override
                                    public void onColorSelected(int selectedColor) {

                                        Y = selectedColor;
                                        button2.setBackgroundColor(Y);
                                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{X,Y});
                                        gradientDrawable.setCornerRadius(0.0f);
                                        rlBackground.setBackground(gradientDrawable);
                                    }
                                })
                                .setPositiveButton("ok", new ColorPickerClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                        Y = selectedColor;
                                        button2.setBackgroundColor(Y);
                                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[]{X,Y});
                                        gradientDrawable.setCornerRadius(0.0f);
                                        rlBackground.setBackground(gradientDrawable);
                                    }
                                })
                                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .build()
                                .show();
                    }
                });
                ((Button) findViewById(R.id.gradientDoneButton)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cvGradiant.setVisibility(View.INVISIBLE);
                        backImage.setVisibility(View.INVISIBLE);

                    }
                });

            }
        });

        //textView Background Color
        tvBackgroundColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseBackgroundColor(false);
            }
        });

        //textView background image
        tvBackgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CropImage.startPickImageActivity(MakerActivity.this);
                showImagePickerOptions();
            }
        });

        //images opecity
        tvOpecity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cvOpacity.setVisibility(View.VISIBLE);
                ((SeekBar) findViewById(R.id.imageOpacitySeekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (backImage.getVisibility() != View.VISIBLE) {
                            seekBar.setClickable(false);
                            Toast.makeText(MakerActivity.this, "select image first", Toast.LENGTH_SHORT).show();
                        } else if (progress < 5) {
                            backImage.setAlpha(1.0f);
                            backImage.setColorFilter((ColorFilter) null);
                        } else if (progress <= 95) {
                            float floatValue = Float.valueOf("." + String.valueOf(progress)).floatValue();
                            backImage.setColorFilter(R.color.black);
                            backImage.setAlpha(floatValue);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                ((Button) findViewById(R.id.imageOpacityDoneButton)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cvOpacity.setVisibility(View.INVISIBLE);
                    }
                });

            }
        });

        //save image
        tvSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MakerActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    //finalHolder.tv_quotes_watermark.setVisibility(View.VISIBLE);
                    tvMaker.setCursorVisible(false);
                    Bitmap bitmap = Bitmap.createBitmap(rlBackground.getWidth(), rlBackground.getHeight(),
                            Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    rlBackground.draw(canvas);

                    OutputStream fos;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        ContentResolver resolver = getContentResolver();
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis() + ".jpg");
                        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
                        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                        Toast.makeText(MakerActivity.this, "File Saved", Toast.LENGTH_SHORT).show();
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
                    } else {

                        FileOutputStream outputStream = null;

                        File sdCard = Environment.getExternalStorageDirectory();

                        File directory = new File(sdCard.getAbsolutePath() + "/Latest Quotes");
                        directory.mkdir();

                        String filename = String.format("%d.jpg", System.currentTimeMillis());

                        File outFile = new File(directory, filename);

                        Toast.makeText(MakerActivity.this, "Saved", Toast.LENGTH_SHORT).show();
//                        finalHolder.tv_save_quote.setText("Saved");
//                        finalHolder.iv_save_quote.setImageResource(R.drawable.ic_menu_check);



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

                        tvMaker.setCursorVisible(true);

                    }

                }else{

                    //show permission popup
                    //requestStoragePermission();

                }
            }
        });


        //share images
        tvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvMaker.setCursorVisible(false);
                Bitmap bitmap = Bitmap.createBitmap(rlBackground.getWidth(), rlBackground.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                rlBackground.draw(canvas);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id="+getPackageName());
                startActivity(Intent.createChooser(intent, "Premium Quotes"));
                tvMaker.setCursorVisible(true);
                //finalHolder.tv_quotes_watermark.setVisibility(View.INVISIBLE);

            }
        });

        watermarkRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveDialog();
            }
        });


    }

    private void loadRewardedAds() {
        if (adsPref.getAdStatus().equals(AD_STATUS_ON)) {
            switch (adsPref.getAdType()) {
                case ADMOB:
                    AdRequest adRequest = new AdRequest.Builder().build();
                    RewardedAd.load(this, Config.REWARD_ADS_ID,
                            adRequest, new RewardedAdLoadCallback() {
                                @Override
                                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                    // Handle the error.
                                    Log.d("TAG", loadAdError.getMessage());
                                    mRewardedAd = null;
                                }

                                @Override
                                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                    mRewardedAd = rewardedAd;
                                    Log.d("TAG", "Ad was loaded.");
                                }
                            });
                    break;
            }
        }
    }

    private void showRemoveDialog() {
        final Dialog dialog = new Dialog(this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.dialog_watermark);

        LinearLayout mbtnWatch =dialog.findViewById(R.id.mbtnWatch);
        LinearLayout mbtnNo = dialog.findViewById(R.id.mbtnNo);
        mbtnWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedAd != null){
                    //Activity activityContext = mCtx;
                    mRewardedAd.show((Activity) MakerActivity.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d("TAG", "The user earned the reward.");
                            watermarkIcon.setVisibility(View.GONE);
                            watermarkRelative.setVisibility(View.GONE);
                            dialog.dismiss();

                        }
                    });
                } else {
                    mRewardedAd.show((Activity) MakerActivity.this, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d("TAG", "The user earned the reward.");
                            watermarkIcon.setVisibility(View.GONE);
                            watermarkRelative.setVisibility(View.GONE);

                            dialog.dismiss();
                        }
                    });
                }
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

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(MakerActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(MakerActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE){
            if (resultCode == Activity.RESULT_OK){
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    // loading profile image from local cache
                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadProfile(String url) {
        Log.d(TAG, "Image cache path: " + url);
        backImage.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(url)
                .into(backImage);
    }

    private void loadImages() {
        int numOfImages = 41;
        images = new int[ numOfImages ];
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

        backImage.setBackgroundResource(images[ imagesIndex ]);
    }



    //Share image tool
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


    //choose text colors
    private void chooseTextColor(boolean supportAlpha) {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(currentColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)

                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                        currentColor = selectedColor;
                        tvMaker.setTextColor(selectedColor);
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        currentColor = selectedColor;
                        tvMaker.setTextColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    //choose text colors - app is created by ymg developers
    private void chooseBackgroundColor(boolean supportAlpha) {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(currentColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)

                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {

                        currentColor = selectedColor;
                        backImage.setVisibility(View.INVISIBLE);
                        rlBackground.setBackgroundColor(selectedColor);
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        currentColor = selectedColor;
                        backImage.setVisibility(View.INVISIBLE);
                        rlBackground.setBackgroundColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    public class n implements View.OnClickListener {

        public void onClick(View view) {
            tvEsize.setVisibility(View.INVISIBLE);
        }
    }


    public class seekbaralert implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            if (i < 10) {
                tvMaker.setTextSize(15.0f);
                return;
            }
            tvMaker.setTextSize((float) i);
            b0 = i;
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    }

    public class c0 implements View.OnClickListener {

        final Typeface f1979c;
        final Button d;

        c0(Typeface typeface, Button button) {
            this.f1979c = typeface;
            this.d = button;
        }

        public void onClick(View view) {
            tvMaker.setTypeface(this.f1979c);
            this.d.setTypeface(this.f1979c);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.logo)
                .setTitle(getString(R.string.app_name))
                .setMessage("Are you sure you want to close Quotes Maker")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCheck();
    }

    private void initCheck() {
        if (prf.loadNightModeState()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }


}