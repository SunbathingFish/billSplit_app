package com.example.billsplit_app;

import java.util.ArrayList;

public class Dish {
    private String name;
    private String price = "0.0";
    private String liquorTax = "0.0";
    private boolean collapsed = false;
    private boolean alcoholic = false;
    private ArrayList<User> sharedUsers = new ArrayList<>();

    public static ArrayList<String> names = new ArrayList<>();
    public static int num;


    public Dish (String name, String price) {
        this.name = name;
        this.price = price;
    }
    public ArrayList<String> stuff1(){
        return names;
    }
    public int stuff2(){
        return num;
    }
    public ArrayList<User> stuff3() {
        return sharedUsers;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isCollapsed() {
        return collapsed;
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    public boolean isAlcoholic() {
        return alcoholic;
    }

    public void setAlcoholic(boolean alcoholic) {
        this.alcoholic = alcoholic;
    }

    public void addUser(User u) {
        if (!this.sharedUsers.contains(u)) {
            names.add(u.getUsername());
            this.sharedUsers.add(u);
        }
    }

    public void removeUser(User u) {
        System.out.println("yayayfusufbsua");
        names.remove(u.getUsername());
        this.sharedUsers.remove(u);
    }

    public int getNOfSharedUsers() {
        return this.sharedUsers.size();
    }

    public void clearSharedUsers() {
        this.sharedUsers.clear();
    }

    public double getLiquorTax() {
        return Double.parseDouble(this.liquorTax);
    }

    public void setLiquorTax(Double tax) {
        this.liquorTax = String.valueOf(tax);
    }
}
