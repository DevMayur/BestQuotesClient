package com.ymg.beststatusandquotes.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ymg.beststatusandquotes.Model.Author;
import com.ymg.beststatusandquotes.Model.Category;
import com.ymg.beststatusandquotes.Model.Quote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Sync {

    private static final String TAG = "syncWithLocalhost";
    private static final String SYNC_URL = "https://codewithmayur.in/quotes/sync.php?last_sync=";

    public void syncData(int lastSync, Context context, SyncCallback callback) {
        new FetchDataAsyncTask(lastSync, context, callback).execute();
    }

    private class FetchDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private int lastSync;
        private Context context;
        private int currentPage = 1;

        private SyncCallback callback;

        FetchDataAsyncTask(int lastSync, Context context, SyncCallback callback) {
            this.lastSync = lastSync;
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            fetchData();
            return null;
        }

        private void fetchData() {
            String url = SYNC_URL + lastSync + "&page=" + currentPage;

            StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray authors = jsonResponse.getJSONArray("author");
                        JSONArray categories = jsonResponse.getJSONArray("category");
                        JSONArray quotes = jsonResponse.getJSONArray("quote");

                        syncAuthors(authors, context);
                        syncCategories(categories, context);
                        syncQuotes(quotes, context);

                        if (authors.length() > 0 || categories.length() > 0 || quotes.length() > 0) {
                            currentPage++;
                            fetchData();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(request);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (callback != null) {
                callback.onSyncFinished();
            }
        }
    }

    private void syncAuthors(JSONArray authors, Context context) {
        DataBaseHandler db = new DataBaseHandler(context);
        for (int i = 0; i < authors.length(); i++) {
            try {
                JSONObject authorJson = authors.getJSONObject(i);
                Author author = new Author(
                        authorJson.getInt("_id_author"),
                        authorJson.getString("name")
                );
                db.addAuthor(author);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void syncCategories(JSONArray categories, Context context) {
        DataBaseHandler db = new DataBaseHandler(context);
        for (int i = 0; i < categories.length(); i++) {
            try {
                JSONObject categoryJson = categories.getJSONObject(i);
                Category category = new Category(
                        categoryJson.getString("name"),
                        categoryJson.getString("file_name"),
                        "0"
                );
                db.addCategory(category);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void syncQuotes(JSONArray quotes, Context context) {
        DataBaseHandler db = new DataBaseHandler(context);
        for (int i = 0; i < quotes.length(); i++) {
            try {
                JSONObject quoteJson = quotes.getJSONObject(i);
                Quote quote = new Quote(
                        quoteJson.getInt("_id"),
                        quoteJson.getString("author_name"),
                        quoteJson.getString("qte"),
                        quoteJson.getString("category_name"),
                        quoteJson.getString("author_name"),
                        quoteJson.getString("fav")
                );
                db.addQuote(quote);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public interface SyncCallback {
        void onSyncFinished();
    }

}
