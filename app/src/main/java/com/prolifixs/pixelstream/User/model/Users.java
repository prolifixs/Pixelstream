package com.prolifixs.pixelstream.User.model;

/**
 * Created by Prolifixs on 1/31/2018.
 */
//------------------------------------------------------updated user account version-------------------------------
public class Users {
    private String description;
    private String display_name;
    private long followers;
    private long following;
    private long posts;
    private String profile_photo;
    private String website;
    private String user_id;
    private long phone_number;
    private String email;
    private String username;

    public Users(String description, String display_name, long followers, long following, long posts,
                 String profile_photo, String website, String user_id, long phone_number, String email, String username) {
        this.description = description;
        this.display_name = display_name;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
        this.profile_photo = profile_photo;
        this.website = website;
        this.user_id = user_id;
        this.phone_number = phone_number;
        this.email = email;
        this.username = username;
    }

    public Users(){

    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public long getFollowers() {
        return followers;
    }

    public void setFollowers(long followers) {
        this.followers = followers;
    }

    public long getFollowing() {
        return following;
    }

    public void setFollowing(long following) {
        this.following = following;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(long phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "Users{" +
                "description='" + description + '\'' +
                ", display_name='" + display_name + '\'' +
                ", followers=" + followers +
                ", following=" + following +
                ", posts=" + posts +
                ", profile_photo='" + profile_photo + '\'' +
                ", website='" + website + '\'' +
                ", user_id='" + user_id + '\'' +
                ", phone_number=" + phone_number +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
