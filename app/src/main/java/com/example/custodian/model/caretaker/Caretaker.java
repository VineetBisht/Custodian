package com.example.custodian.model.caretaker;

public class Caretaker {
    private int exp;
    private String fullname;
    private String mail;
    private String address;
    private String birthday;
    private String username;
    private String description;
    private Integer image;

    public Caretaker() {
    }

    public Caretaker(int exp, String fullname, String mail, String address, String birthday, String username, Integer image, String description) {
        this.exp = exp;
        this.fullname = fullname;
        this.mail = mail;
        this.address = address;
        this.birthday = birthday;
        this.username = username;
        this.image = image;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + exp + '\'' +
                ", lastName='" + fullname + '\'' +
                ", mail='" + mail + '\'' +
                ", address='" + address + '\'' +
                ", birthday='" + birthday + '\'' +
                '}';
    }
}
