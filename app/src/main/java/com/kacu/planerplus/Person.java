package com.kacu.planerplus;



import java.util.ArrayList;

public class Person {


    public String Id;
    public String name;
    public String email;
    public String userType;


    public String companyAdress;
    public String companyPhone;
    public String companyCattegory;



    public Person() {
    }


    public Person(String id, String name, String email, String userType, String phoneNumber) {
        Id = id;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.companyPhone = phoneNumber;
    }

    public Person(String id, String name, String email, String userType, String companyAdress, String companyPhone, String companyCattegory) {
        Id = id;
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.companyAdress = companyAdress;
        this.companyPhone = companyPhone;
        this.companyCattegory = companyCattegory;
    }

    public Person(String id, String name)
    {
        Id= id;
        this.name = name;
    }
}
