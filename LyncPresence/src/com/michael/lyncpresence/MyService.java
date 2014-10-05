package com.michael.lyncpresence;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyService extends Service {

    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return new  Binder() ;
    }
    
    @Override
    public void onCreate() {
        Toast.makeText(this, "Presence service has started.", Toast.LENGTH_SHORT).show();
       }

    @Override
    public void onStart(Intent intent, int startId) {
    	
    	WakeLocker.acquire( getApplicationContext() );
        
        Log.d( "Lync", "Called Service routine." );
        
       	UserActivityMonitor.reportUserInteraction();
       
       	WakeLocker.release();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Presence service destroyed.", Toast.LENGTH_SHORT).show();
    }
}