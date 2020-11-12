package com.pecake.paper.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by USER on 2/13/2018.
 */

public class User {
    private String provider;
    private String name;
    private String phone;
    private String address;
    private String photoUri;
    private String email;
    private String bio;
    private String hobby;
    private String degree;
    private boolean moderator;
    private String gender;

    public User() {
    }

//    public User(String provider, String name, String phone, String photoUri, String address, boolean moderator) {
//        this.provider = provider;
//        this.photoUri = photoUri;
//        this.name = name;
//        this.phone = phone;
//        this.address = address;
//        this.moderator = moderator;
//    }

    public User(String provider, String name, String phone, String photoUri, String email,String address,String bio,String degree,String hobby,String gender, boolean moderator) {
        this.provider = provider;
        this.email = email;
        this.bio = bio;
        this.degree = degree;
        this.hobby = hobby;
        this.gender = gender;
        this.photoUri = photoUri;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.moderator = moderator;
    }
    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String provider, String name, String phone, String photoUri, String email, String address, boolean moderatior) {
        this.provider = provider;
        this.address = address;
        this.name = name;
        this.phone = phone;
        this.photoUri = photoUri;
        this.email = email;
        this.moderator = moderatior;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isModerator() {
        return moderator;
    }

    public void setModerator(boolean moderator) {
        this.moderator = moderator;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
