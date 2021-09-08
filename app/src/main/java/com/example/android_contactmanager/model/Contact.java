package com.example.android_contactmanager.model;

public class Contact {
    private String name;
    private String number;
    private String id;
    private String uriImage;

    public Contact(String name, String number, String id, String uriImage) {
        this.name = name;
        this.number = number;
        this.id = id;
        this.uriImage = uriImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUriImage() {
        return uriImage;
    }

    public void setUriImage(String uriImage) {
        this.uriImage = uriImage;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "name='" + name + '\'' +
                ", number='" + number + '\'' +
                ", id='" + id + '\'' +
                ", uriImage='" + uriImage + '\'' +
                '}';
    }
}
