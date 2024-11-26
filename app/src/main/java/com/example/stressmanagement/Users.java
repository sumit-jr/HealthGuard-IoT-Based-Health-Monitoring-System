package com.example.stressmanagement;

public class Users {
    String profilepic;
    String mail;
    String userName;
    String password;
    String userId;
    String lastMessage;
    String addresh;
    Long stress_level;

    public Users() {

    }

    public Users(String userId, String userName, String maill, String password, String profilepic, String addresh, Long stress_level) {
        this.userId = userId;
        this.userName = userName;
        this.mail = maill;
        this.password = password;
        this.profilepic = profilepic;
        this.addresh = addresh;
        this.stress_level = stress_level;
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getaddresh() {
        return addresh;
    }

    public void setaddresh(String addresh) {
        this.addresh = addresh;
    }

    public Long getstress_level() {
        return stress_level;
    }

    public void setstress_level(Long stress_level) {
        this.stress_level = stress_level;
    }
}
