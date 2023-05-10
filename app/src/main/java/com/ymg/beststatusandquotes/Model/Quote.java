package com.ymg.beststatusandquotes.Model;

import java.io.Serializable;


public class Quote implements Serializable {

    // private variables
    int _id;
    String _name;
    String _quote;
    String _category;
    String _fileName;
    String _fav;
    String _count;

    //This app is Created by YMG-developers
    public Quote() {

    }

    // Quote constructor
    public Quote(int id, String name, String quote, String category,
                 String fileName, String fav) {
        this._id = id;
        this._name = name;
        this._quote = quote;
        this._category = category;
        this._fileName = fileName;
        this._fav = fav;

    }

    // Author constructor //This app is Created by YMG-developers
    public Quote(String name, String fileName, String count) {

        this._name = name;

        this._fileName = fileName;

        this._count = count;
    }

    // getting ID
    public int getID() {
        return this._id;
    }

    // setting id
    public void setID(int keyId) {
        this._id = keyId;
    }

    // getting name
    public String getName() {
        return this._name;
    }

    // setting name
    public void setName(String name) {
        this._name = name;
    }

    // getting quote
    public String getQuote() {
        return this._quote;
    }

    // setting quote
    public void setQuote(String quote) {
        this._quote = quote;
    }

    // getting categoty
    public String getCategory() {
        return this._category;
    }

    // setting categoty
    public void setCategory(String category) {
        this._category = category;
    }

    // getting fileName
    public String getFileName() {
        return this._fileName;
    }

    // setting fileName
    public void setFileName(String fileName) {
        this._fileName = fileName;
    }

    // getting favorite
    public String getFav() {
        return this._fav;
    }

    // setting favorite
    public void setFav(String fav) {
        this._fav = fav;
    }

    // getting counter
    public String getCount() {
        return this._count;
    }

    // setting counter //This app is Created by YMG-developers
    public String setCount(String count) {
        return this._count = count;
    }

}
