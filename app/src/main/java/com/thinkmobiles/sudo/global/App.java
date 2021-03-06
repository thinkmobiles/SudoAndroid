package com.thinkmobiles.sudo.global;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import com.thinkmobiles.sudo.models.addressbook.UserModel;

import java.util.List;

/**
 * Created by Sasha on 12.02.2015.
 */
public class App extends Application {

    private static List<UserModel> contactsList;

    private static UserModel currentUser;

    public static String currentCredits;
    public static String currentContacts;
    public static String currentChats;
    private static String currentMobile;
    private static String uId;
    private static String currentISO;
    private static String currentLocationISO;
    private static NotificationManager mNotificationManager;

    private static String [] curreentChat;

    public static String[] getCurrentChat() {
        return curreentChat;
    }

    public static void setCurrentChat(String[] curreentChat) {
        App.curreentChat = curreentChat;
    }

    public static String getCurrentLocationISO() {
        return currentLocationISO;
    }

    public static void setCurrentLocationISO(String currentLocationISO) {
        App.currentLocationISO = currentLocationISO;
    }

    public static NotificationManager getmNotificationManager() {
        return mNotificationManager;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        mNotificationManager.cancelAll();
    }

    public static String getCurrentChats() {
        return currentChats;
    }

    public static void setCurrentChats(String currentChats) {
        App.currentChats = currentChats;
    }


    public static UserModel getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(UserModel currentUser) {
        App.currentUser = currentUser;
    }


    public static String getuId() {
        return uId;
    }

    public static void setuId(String uId) {
        App.uId = uId;
    }

    public static String getCurrentMobile() {
        if (currentMobile != null && !currentMobile.equalsIgnoreCase("")) return currentMobile;
        else return "Buy a new number";
    }

    public static List<UserModel> getContactsList() {
        return contactsList;
    }

    public static void setContactsList(List<UserModel> contactsList) {
        App.contactsList = contactsList;
    }

    public static void setCurrentMobile(String currentMobile) {
        App.currentMobile = currentMobile;
    }


    public static String getCurrentCredits() {
        return currentCredits;
    }

    public static void setCurrentCredits(String currentCredits) {
        App.currentCredits = currentCredits;
    }

    public static String getCurrentContacts() {
        return currentContacts;
    }

    public static void setCurrentContacts(String currentContacts) {
        App.currentContacts = currentContacts;
    }

    public static String getCurrentISO() {
        return currentISO;
    }

    public static void setCurrentISO(String currentISO) {
        App.currentISO = currentISO;
    }


}
