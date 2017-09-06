package com.kidappolis.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Application model
 */

public class App extends RealmObject {


    @SerializedName("name")
    public String name;

    @SerializedName("package")
    @PrimaryKey
    public String packageName;

    @SerializedName("imageUrl")
    public String imageUrl;

    @SerializedName("category")
    public String category;

    public App() { /* need default constructor for realm */ }

    public App(String name, String packageName, String imageUrl, String category) {
        this.name = name;
        this.packageName = packageName;
        this.imageUrl = imageUrl;
        this.category = category;
    }
}
