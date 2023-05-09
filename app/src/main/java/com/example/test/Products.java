package com.example.test;

public class Products {
    String Image;
    String Name;
    String [] Size;
    String Qty;

    public Products(String image, String name, String [] size, String qty) {
        Image = image;
        Name = name;
        Size = size;
        Qty = qty;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String [] getSize() {
        return Size;
    }

    public void setSize(String [] size) {
        Size = size;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }
}
