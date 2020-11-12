package com.pecake.paper.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Post {
    private String title;
    private String content;
    private String category;
    private String postPhotoUri;
    public int countViewer;
    private String date;
    private String uid;
    private String name;
    private String userLogoUri;
    private String time;
    public String categoryId;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
    public int userChoice;
    public List<String> viewers = new ArrayList<>();
    public List<String> bookMarks = new ArrayList<>();

    public Post() {
    }
    // category
    public Post(String categoryId,String title, String content, String category, String postPhotoUri, String date, String time, String uid, String name, String userLogoUri, int countViewer, List<String> viewers, List<String> bookMarks, int starCount, Map<String, Boolean> stars) {
        this.stars = stars;
        this.starCount = starCount;
        this.bookMarks = bookMarks;
        this.viewers = viewers;
        this.time = time;
        this.countViewer = countViewer;
        this.title = title;
        this.content = content;
        this.category = category;
        this.categoryId = categoryId;
        this.postPhotoUri = postPhotoUri;
        this.date = date;
        this.uid = uid;
        this.name = name;
        this.userLogoUri = userLogoUri;
    }

    public Post(String b,String a,String categoryId,String title, String content, String category, String date, String time, String uid, String name, String userLogoUri, int countViewer, List<String> viewers, List<String> bookMarks, int starCount, Map<String, Boolean> stars) {
        this.stars = stars;
        this.categoryId = categoryId;
        this.starCount = starCount;
        this.bookMarks = bookMarks;
        this.viewers = viewers;
        this.time = time;
        this.countViewer = countViewer;
        this.title = title;
        this.content = content;
        this.category = category;
        this.date = date;
        this.uid = uid;
        this.name = name;
        this.userLogoUri = userLogoUri;
    }

    @Exclude
    public Map<String, Object> toPhotoCategoryMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("content", content);
        result.put("time", time);
        result.put("category", category);
        result.put("categoryId",categoryId);
        result.put("postPhotoUri", postPhotoUri);
        result.put("date", date);
        result.put("uid", uid);
        result.put("name", name);
        result.put("userLogoUri", userLogoUri);
        result.put("countViewer", countViewer);
        result.put("viewers", viewers);
        result.put("bookMarks", bookMarks);
        result.put("stars", stars);
        result.put("starCount", starCount);
        return result;
    }
    @Exclude
    public Map<String, Object> toCategoryMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("content", content);
        result.put("category", category);
        result.put("time", time);
        result.put("date", date);
        result.put("uid", uid);
        result.put("categoryId",categoryId);
        result.put("name", name);
        result.put("bookMarks", bookMarks);
        result.put("userLogoUri", userLogoUri);
        result.put("countViewer", countViewer);
        result.put("viewers", viewers);
        result.put("stars", stars);
        result.put("starCount", starCount);
        return result;
    }







    public Post(String title, String content, String category, String postPhotoUri, String date, String time, String uid, String name, String userLogoUri, int countViewer, List<String> viewers, List<String> bookMarks, int starCount, Map<String, Boolean> stars) {
        this.stars = stars;
        this.starCount = starCount;
        this.bookMarks = bookMarks;
        this.viewers = viewers;
        this.time = time;
        this.countViewer = countViewer;
        this.title = title;
        this.content = content;
        this.category = category;
        this.postPhotoUri = postPhotoUri;
        this.date = date;
        this.uid = uid;
        this.name = name;
        this.userLogoUri = userLogoUri;
    }

    public Post(String title, String content, String category, String date, String time, String uid, String name, String userLogoUri, int countViewer, List<String> viewers, List<String> bookMarks, int starCount, Map<String, Boolean> stars) {
        this.stars = stars;
        this.starCount = starCount;
        this.bookMarks = bookMarks;
        this.viewers = viewers;
        this.time = time;
        this.countViewer = countViewer;
        this.title = title;
        this.content = content;
        this.category = category;
        this.date = date;
        this.uid = uid;
        this.name = name;
        this.userLogoUri = userLogoUri;
    }


    @Exclude
    public Map<String, Object> toPhotoMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("content", content);
        result.put("time", time);
        result.put("category", category);
        result.put("postPhotoUri", postPhotoUri);
        result.put("date", date);
        result.put("uid", uid);
        result.put("name", name);
        result.put("userLogoUri", userLogoUri);
        result.put("countViewer", countViewer);
        result.put("viewers", viewers);
        result.put("bookMarks", bookMarks);
        result.put("stars", stars);
        result.put("starCount", starCount);
        return result;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("content", content);
        result.put("category", category);
        result.put("time", time);
        result.put("date", date);
        result.put("uid", uid);
        result.put("name", name);
        result.put("bookMarks", bookMarks);
        result.put("userLogoUri", userLogoUri);
        result.put("countViewer", countViewer);
        result.put("viewers", viewers);
        result.put("stars", stars);
        result.put("starCount", starCount);
        return result;
    }

    public Post(int userChoice, String title, String content, String date,String time, String uid, String name, String userLogoUri, int countViewer, List<String> viewers, List<String> bookMarks, int starCount, Map<String, Boolean> stars) {
        this.stars = stars;
        this.userChoice = userChoice;
        this.title = title;
        this.starCount = starCount;
        this.bookMarks = bookMarks;
        this.viewers = viewers;
        this.countViewer = countViewer;
        this.content = content;
        this.time = time;
        this.date = date;
        this.uid = uid;
        this.name = name;
        this.userLogoUri = userLogoUri;
    }

    @Exclude
    public Map<String, Object> toNoTitleMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userChoice", userChoice);
        result.put("content", content);
//        result.put("category", category);
        result.put("date", date);
        result.put("title", title);
        result.put("uid", uid);
        result.put("time", time);
        result.put("name", name);
        result.put("bookMarks", bookMarks);
        result.put("userLogoUri", userLogoUri);
        result.put("countViewer", countViewer);
        result.put("viewers", viewers);
        result.put("stars", stars);
        result.put("starCount", starCount);
        return result;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPostPhotoUri() {
        return postPhotoUri;
    }

    public void setPostPhotoUri(String postPhotoUri) {
        this.postPhotoUri = postPhotoUri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public int getCountViewer() {
        return countViewer;
    }

    public void setCountViewer(int countViewer) {
        this.countViewer = countViewer;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserLogoUri() {
        return userLogoUri;
    }

    public void setUserLogoUri(String userLogoUri) {
        this.userLogoUri = userLogoUri;
    }
}
