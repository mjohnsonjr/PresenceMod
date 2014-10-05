package com.michael.lyncpresence;

public class UserActivityMonitor
{
    private static native void raiseUserActivityEvent();
    
    public static void reportUserInteraction() {
        raiseUserActivityEvent();
    }
}