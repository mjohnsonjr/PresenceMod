package com.michael.lyncpresence;

import java.lang.reflect.InvocationTargetException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
	
		Log.d("Lync", "Running on receive." );
		
		Class<?> activityMonitor = (Class<?>) intent.getExtras().get("activityMonitor");
		Object activityMonitorObject = null;
		
		try {
			activityMonitorObject = activityMonitor.getMethod("getInstance", (Class[]) null ).invoke( null, (Object[]) null );
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | NoSuchMethodException e) {
			Log.d("Lync", "Exception: " + e.getMessage() + " thrown on attempt to set in foreground." );
		}
		
		if( activityMonitorObject != null ){
			try {
				activityMonitor.getMethod("setInBackground", boolean.class ).invoke( activityMonitorObject, false );
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		
		UserActivityMonitor.reportUserInteraction();
		UserActivityMonitor.reportUserInteraction();
		UserActivityMonitor.reportUserInteraction();
		UserActivityMonitor.reportUserInteraction();

		if( activityMonitorObject != null ){
			try {
				activityMonitor.getMethod("setInBackground", boolean.class ).invoke( activityMonitorObject, true );
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException e) {
				Log.d("Lync", "Exception: " + e.getMessage() + " thrown on attempt to set in background." );
			}
		}	
	}
}