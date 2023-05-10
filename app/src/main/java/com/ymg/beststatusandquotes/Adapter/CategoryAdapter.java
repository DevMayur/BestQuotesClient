package com.ymg.beststatusandquotes.Adapter;


import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.ymg.beststatusandquotes.Model.Category;
import com.ymg.beststatusandquotes.R;
import com.ymg.beststatusandquotes.Utils.PrefManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;


public class CategoryAdapter extends ArrayAdapter<Category> {
    Context context;
    int layoutResourceId;
    private int lastPosition = -1;
    //private RoundImage roundedImage;
    ArrayList<Category> data = new ArrayList<Category>();
    private PrefManager prefManager;

    public CategoryAdapter(Context context, int layoutResourceId,
                           ArrayList<Category> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        prefManager = new PrefManager(context);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;

        ImageHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ImageHolder();
            holder.txtName = (TextView) row.findViewById(R.id.CatName);
            holder.imgCat = (ImageView) row.findViewById(R.id.imgCat);
            holder.txtCounter = (TextView) row.findViewById(R.id.txtCounter);
            holder.relativeLayout = row.findViewById(R.id.llBackground);

            row.setTag(holder);
        } else {
            holder = (ImageHolder) row.getTag();
        }

        Category picture = data.get(position);
        holder.txtName.setText(picture.getName());
        holder.txtCounter.setText(picture.getCount());

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

        String[] mColors = {
                "#00C7F9",
                "#923EFF",
                "#FF3E8B",
                "#FFAB3E",
                "#007DF9",
                "#43D4CF",
                "#F27DBD",
                "#7EAB1D",
                "#FF4863",
                "#D46EFF",
                "#34D48E",
                "#8D99C1",
                "#00C7F9",
                "#923EFF",
                "#FF3E8B",
                "#FFAB3E",
                "#007DF9",
                "#43D4CF",
                "#F27DBD",
                "#7EAB1D",
                "#FF4863",
                "#D46EFF",
                "#34D48E",
                "#8D99C1",
                "#34D48E"
        };

        if (prefManager.loadNightModeState()){
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#505775"));
        }else {
            holder.relativeLayout.setBackgroundColor(Color.parseColor(mColors[position % 24]));
        }


        //AssetManager assetManager = context.getAssets();
        boolean isExist = false;
        AssetManager assetManager = context.getAssets();
        InputStream imageStream = null;
        try {
            imageStream = assetManager.open("categories/"+picture.getFileName()+".png");

            isExist =true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (isExist != false){
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);

            Glide.with(context)
                    .load(theImage)
                    .placeholder(R.drawable.ic_menu_quotes)
                    .into(holder.imgCat);
        }
        else {
            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_menu_quotes);
            Glide.with(context)
                    .load(bm)
                    .placeholder(R.drawable.ic_menu_quotes)
                    .into(holder.imgCat);
        }

        return row;
    }

    static class ImageHolder {
        TextView txtCounter;
        ImageView imgCat;
        TextView txtName;
        RelativeLayout relativeLayout;

    }
}
