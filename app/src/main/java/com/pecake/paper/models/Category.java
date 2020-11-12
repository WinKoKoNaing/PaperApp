package com.pecake.paper.models;

public class Category {
    public String categoryId;
    public String title;
    public String photoUri;
    public String userId;

    public Category() {
    }

    public Category(String categoryId,String userId, String title, String photoUri) {
        this.categoryId = categoryId;
        this.userId = userId;
        this.title = title;
        this.photoUri = photoUri;
    }
}
