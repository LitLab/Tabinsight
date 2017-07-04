package com.lumen.model;

import com.google.gson.annotations.SerializedName;

/**
 * Application model
 */

public class App {

    @SerializedName("name")
    public String name;

    @SerializedName("package")
    public String packageName;

    @SerializedName("imageUrl")
    public String imageUrl;

    public App(String name, String packageName, String imageUrl) {
        this.name = name;
        this.packageName = packageName;
        this.imageUrl = imageUrl;
    }
}
