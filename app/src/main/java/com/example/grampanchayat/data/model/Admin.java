// This class is used to check whether the user is admin or not

// Importing the required packages
package com.example.grampanchayat.data.model;

// Creating the Admin class
public class Admin {

    // Creating the required variables and assigning the admin UID and email
    String adminUid = "mvKPFsWpUTOAR80lkPen6H0rQow2";
    String adminMail = "djeroba4@gmail.com";

    // Creating the isAdminUsingUID method to check
    // whether the user is admin or not using the UID
    public boolean isAdminUsingUID(String uid){
        if (adminUid.equals(uid)){
            return true;
        }
        return false;
    }

    // Creating the isAdminUsingMail method to check
    // whether the user is admin or not using the email
    public boolean isAdminUsingMail(String mail){
        if (adminMail.equals(mail)){
            return true;
        }
        return false;
    }



}
