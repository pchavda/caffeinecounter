package com.detroitteatime.caffeinecounter.model;


public class Drink {

    private double size;
    private String type;
    private int mgCaffeine;
    private String date;
    private String time;
    private long id;


    public Drink(double size, String type, String date, String time, int mgCaff) {
        this.size = size;
        this.type = type;
        this.date = date;
        this.time = time;
        this.mgCaffeine = mgCaff;
    }



    public Drink() {
    }


    public void setSize(double size) {
        this.size = size;
    }

    public double getSize() {
        return size;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setMgCaffeine(int mgCaffeine) {
        this.mgCaffeine = mgCaffeine;
    }

    public int getMgCaffeine() {
        return mgCaffeine;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }


    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }


}
