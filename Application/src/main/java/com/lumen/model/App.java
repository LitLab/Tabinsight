package com.lumen.model;

/**
 * Application model
 */

public class App {

    public String name;
    public String packageName;
    public String imageUrl;

    public App(String name, String packageName, String imageUrl) {
        this.name = name;
        this.packageName = packageName;
        this.imageUrl = imageUrl;
    }
}
