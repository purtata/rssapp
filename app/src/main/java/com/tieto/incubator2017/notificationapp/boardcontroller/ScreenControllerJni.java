package com.tieto.incubator2017.notificationapp.boardcontroller;

public class ScreenControllerJni {

    public native int sendMessage(String message);
    public native int sendListOfMessages(String[] messages);
    public native int clearScreen();

    static {
        System.loadLibrary("screencontroller");
    }
}
