package com.ymg.beststatusandquotes.Model;

public class Author {
    int _id;
    String _name;

    public Author() {
    }

    public Author(int id, String name) {
        this._id = id;
        this._name = name;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getName() {
        return _name;
    }

    public void setName(String name) {
        this._name = name;
    }
}
