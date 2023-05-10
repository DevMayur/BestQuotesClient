package com.ymg.beststatusandquotes.Utils;

/**
 * Created by YMG Developers on 90/09/2021.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ymg.beststatusandquotes.Model.Author;
import com.ymg.beststatusandquotes.Model.Category;
import com.ymg.beststatusandquotes.Model.Quote;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DataBaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 5;

    // Database Name
    private static final String DATABASE_NAME = "quotes.db";
    private static final String DB_PATH_SUFFIX = "/databases/";
    // Quotes table name
    private static final String TABLE_QUOTES = "quote";

    // Quotes Table Columns names
    private static final String KEY_ID = "_id";
    private static final String KEY_AUTHOR_ID = "_id_author";
    private static final String KEY_CATEGORY_ID = "_id_cat";

    static Context myContext;



//    private static final int DATABASE_VERSION = 1;
//    private static final String DATABASE_NAME = "best_status_quotes";

    private static final String TABLE_QUOTE = "quote";
    private static final String TABLE_CATEGORY = "category";
//    private static final String TABLE_AUTHOR = "author";

//    private static final String KEY_ID = "_id";
    private static final String KEY_AUTHOR_NAME = "author_name";
    private static final String KEY_CATEGORY_NAME = "category_name";
    private static final String KEY_QUOTE = "qte";
    private static final String KEY_FAVORITE = "fav";

//    private static final String KEY_ID_CATEGORY = "_id_cat";
    private static final String KEY_NAME_CATEGORY = "name";
    private static final String KEY_FILE_NAME_CATEGORY = "file_name";

//    private static final String KEY_ID_AUTHOR = "_id_author";
//    private static final String KEY_NAME_AUTHOR = "name";
//    private static final String KEY_FILE_NAME_AUTHOR = "file_name";
//

    // Table names
    private static final String TABLE_AUTHOR = "author";

    // Author table column names
    private static final String KEY_NAME = "name";
    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        myContext = context;
    }

    public void CopyDataBaseFromAsset() throws IOException {

        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);

        // Path to the just created empty db
        String outFileName = getDatabasePath();

        // if the path doesn't exist first, create it
        File f = new File(myContext.getApplicationInfo().dataDir
                + DB_PATH_SUFFIX);
        if (!f.exists())
            f.mkdir();

        // Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        // transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    private static String getDatabasePath() {
        return myContext.getApplicationInfo().dataDir + DB_PATH_SUFFIX
                + DATABASE_NAME;
    }

    public void openDataBase() throws SQLException {
        File dbFile = myContext.getDatabasePath(DATABASE_NAME);

        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
                System.out.println("Copying sucess from Assets folder");
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }

    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DataBaseHandler.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        try {
            CopyDataBaseFromAsset();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.w(DataBaseHandler.class.getName(), "Data base is upgraded  ");

    }

    public void addQuote(Quote quote) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AUTHOR_NAME, quote.getName());
        values.put(KEY_CATEGORY_NAME, quote.getCategory());
        values.put(KEY_QUOTE, quote.getQuote());
        values.put(KEY_FAVORITE, quote.getFav());

        db.insert(TABLE_QUOTE, null, values);
        db.close();
    }

    public void addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(KEY_CATEGORY_ID, category.get());
        values.put(KEY_NAME_CATEGORY, category.getName());
        values.put(KEY_FILE_NAME_CATEGORY, category.getFileName());

        db.insert(TABLE_CATEGORY, null, values);
        db.close();
    }

    public void addAuthor(Author author) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
//        values.put(KEY_AUTHOR_ID, author.getId());
        values.put(KEY_NAME, author.getName());
        values.put(KEY_FILE_NAME_CATEGORY, author.getName());

        // Inserting Row
        db.insert(TABLE_AUTHOR, null, values);
        // Closing database connection
        db.close();
    }


    // Getting single Quote
    public Quote getQuote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db
                .rawQuery(
                        "SELECT quote._id, quote.author_name, quote.qte, quote.category_name, author.file_name,fav FROM quote,author WHERE author.name = quote.author_name ORDER BY RANDOM()"
                                , null);
        try {
            if (cursor != null && cursor.moveToFirst()) {

                Quote quote = new Quote(Integer.parseInt(cursor.getString(0)),
                        cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4),cursor.getString(5));
                return quote;
            }
        } finally {
            cursor.close();
            db.close();
        }
        // return Quote
        return null;

    }

    // Getting All Authors
    public List<Quote> getAllAuthors(CharSequence value) {
        List<Quote> quoteList = new ArrayList<Quote>();
        // Select All Query
        String selectQuery = "SELECT name,file_name, COUNT(author_name ) AS count FROM author LEFT JOIN quote ON name = author_name WHERE name LIKE '%"
                + value + "%'  GROUP BY name ORDER BY  name ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        try {
            // looping through all rows and adding to list
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Quote quote = new Quote();

                    quote.setName(cursor.getString(0));

                    quote.setFileName(cursor.getString(1));
                    quote.setCount(cursor.getString(2));
                    quoteList.add(quote);

                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
            db.close();
        }

        // return quote list
        return quoteList;

    }

    // Getting All Categories
    public List<Category> getAllCategories(CharSequence value) {
        List<Category> categoryList = new ArrayList<Category>();
        // Select All Query
        String selectQuery = "SELECT name, file_name, COUNT(author_name ) AS count FROM category LEFT JOIN quote ON name = category_name WHERE name LIKE '%"
                + value + "%'  GROUP BY name ORDER BY  name ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        try {
            if (cursor.moveToFirst()) {
                do {
                    Category category = new Category();
                    category.setName(cursor.getString(0));
                    category.setFileName(cursor.getString(1));
                    category.setCount(cursor.getString(2));
                    categoryList.add(category);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
            db.close();
        }

        return categoryList;

    }

    // Getting All Quotes
    public List<Quote> getAllQuotes(String limit) {
        List<Quote> quoteList = new ArrayList<Quote>();
        // Select All Query
        String selectQuery = "SELECT quote._id, quote.author_name, quote.qte, quote.category_name,fav, author.file_name  FROM quote,author where author.name = quote.author_name ORDER BY quote.qte "+limit;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        try {
            if (cursor.moveToFirst()) {
                do {
                    Quote quote = new Quote();

                    quote.setID(Integer.parseInt(cursor.getString(0)));
                    quote.setName(cursor.getString(1));
                    quote.setQuote(cursor.getString(2));
                    quote.setCategory(cursor.getString(3));
                    // quote.setImage(cursor.getBlob(4));
                    quote.setFav(cursor.getString(4));
                    quote.setFileName(cursor.getString(5));
                    quoteList.add(quote);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
            db.close();
        }

        // return quote list
        return quoteList;

    }



    // Getting Favorites
    public List<Quote> getFavorites() {
        List<Quote> quoteList = new ArrayList<Quote>();
        // Select All Query
        String selectQuery = "SELECT quote._id, quote.author_name, quote.qte, quote.category_name,fav, author.file_name  FROM quote,author where author.name = quote.author_name AND fav ='1'  ORDER BY name";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        try {
            if (cursor.moveToFirst()) {
                do {
                    Quote quote = new Quote();
                    quote.setID(Integer.parseInt(cursor.getString(0)));
                    quote.setName(cursor.getString(1));
                    quote.setQuote(cursor.getString(2));
                    // quote.setImage(cursor.getBlob(4));
                    quote.setCategory(cursor.getString(3));
                    quote.setFav(cursor.getString(4));
                    quote.setFileName(cursor.getString(5));
                    quoteList.add(quote);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
            db.close();
        }

        // close inserting data from database

        return quoteList;

    }

    // Getting Quotes By Category
    public List<Quote> getQuotesByCategory(String value) {
        List<Quote> quoteList = new ArrayList<Quote>();
        // Select All Query
        String selectQuery = "SELECT quote._id, quote.author_name, quote.qte, quote.category_name,fav, author.file_name  FROM quote,author where author.name = quote.author_name AND category_name = '"
                + value + "' ORDER BY quote._id DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        try {
            if (cursor.moveToFirst()) {
                do {
                    Quote quote = new Quote();
                    quote.setID(Integer.parseInt(cursor.getString(0)));
                    quote.setName(cursor.getString(1));
                    quote.setQuote(cursor.getString(2));
                    // quote.setImage(cursor.getBlob(4));
                    quote.setCategory(cursor.getString(3));
                    quote.setFav(cursor.getString(4));
                    quote.setFileName(cursor.getString(5));
                    quoteList.add(quote);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
            db.close();
        }

        // close inserting data from database

        return quoteList;

    }

    // Getting Quotes By Author
    public List<Quote> getQuotesByAuthor(String value) {
        List<Quote> quoteList = new ArrayList<Quote>();
        // Select All Query
        String selectQuery = "SELECT quote._id, quote.author_name, quote.qte, quote.category_name,fav, author.file_name  FROM quote,author where author.name = quote.author_name AND name= '"
                + value + "' ORDER BY author_name";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        try {
            if (cursor.moveToFirst()) {
                do {
                    Quote quote = new Quote();
                    quote.setID(Integer.parseInt(cursor.getString(0)));
                    quote.setName(cursor.getString(1));
                    quote.setQuote(cursor.getString(2));
                    // quote.setImage(cursor.getBlob(4));
                    quote.setCategory(cursor.getString(3));
                    quote.setFav(cursor.getString(4));
                    quote.setFileName(cursor.getString(5));
                    quoteList.add(quote);
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
            db.close();
        }

        // close inserting data from database

        return quoteList;

    }

    // Updating single quote

    public int updateQuote(Quote quote) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_id", quote.getID());
        values.put("author_name", quote.getName());
        values.put("qte", quote.getQuote());
        values.put("category_name", quote.getCategory());
        values.put("fav", quote.getFav());
        // updating row
        return db.update(TABLE_QUOTES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(quote.getID()) });

    }



}
