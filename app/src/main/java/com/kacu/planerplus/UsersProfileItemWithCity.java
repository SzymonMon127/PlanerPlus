package com.kacu.planerplus;

public class UsersProfileItemWithCity {
    private String imageResource;
    private String text1;
    private String text2;



    public UsersProfileItemWithCity(String imageResource, String text1, String text2) {
        this.imageResource = imageResource;
        this.text1 = text1;
        this.text2 = text2;
    }

    public String getImageResource() {
        return imageResource;
    }

    public String getText1() {
        return text1;
    }

    public String getText2() {
        return text2;
    }
}
