package com.kacu.planerplus;


public class CalendarUser {


    public String hour;
    public String userId;
    public String userName;
    public String task;
    public String companyName;



    public CalendarUser() {
    }


    public CalendarUser(String hour, String userId, String userName, String task, String CompanyName) {
        this.hour = hour;
        this.userId = userId;
        this.userName = userName;
        this.task = task;
        this.companyName = CompanyName;
    }

    public CalendarUser(String hour, String userId, String task, String companyName) {
        this.hour = hour;
        this.userId = userId;
        this.task = task;
        this.companyName = companyName;
    }
}
