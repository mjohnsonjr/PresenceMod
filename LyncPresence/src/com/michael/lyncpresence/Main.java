package com.michael.lyncpresence;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage {
	
	public static Class<?> Tab1;
	public static Class<?> lyncClass;
	public static Class<?> contactClass;
	public static Class<?> activityMonitorClass;
	public static Class<?> lyncReceiver;
	public static Class<?> wakeLockerClass;
	
	public static boolean closedOnce = false;
	public static Context context;
	public static PendingIntent alarmIntent;
	public static AlarmManager alarm;
	
	
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		Log.d("Lync", "We have made it to the start: " + lpparam.packageName );
		
		if( lpparam.packageName.equals( "com.microsoft.office.lync" ) ){
			Log.d("Lync", "Loaded (Lync) static classloader.");
			
			Main.Tab1 = XposedHelpers.findClass("com.microsoft.office.lync.proxy.UserActivityMonitor", lpparam.classLoader );
			Main.lyncClass = XposedHelpers.findClass("com.microsoft.office.lync.ui.BaseTabActivity", lpparam.classLoader );
			Main.contactClass = XposedHelpers.findClass("com.microsoft.office.lync.proxy.Contact", lpparam.classLoader );
			Main.activityMonitorClass = XposedHelpers.findClass("com.microsoft.office.lync.ui.ActivityMonitor", lpparam.classLoader );
			Main.lyncReceiver = XposedHelpers.findClass("com.microsoft.office.lync.ui.MyReceiver", lpparam.classLoader );
			Main.wakeLockerClass = XposedHelpers.findClass("com.microsoft.office.lync.ui.WakeLocker", lpparam.classLoader );

//			XposedHelpers.findAndHookMethod(lyncClass, "onStop", new XC_MethodHook() {
//			
//				@Override
//				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//					if(Main.closedOnce == false){
//						lyncClass.getSuperclass().getMethod("onStop").invoke(param.thisObject, (Object[]) null);
//						param.setResult( null ); //Only run superclass onStop
//						Main.closedOnce = true;
//					}
//				}
//			
//			});
			
			XposedHelpers.findAndHookMethod( lyncClass, "onStart", new XC_MethodHook() {
    			
        		@Override //I'm in the com.microsoft.lync view here.
        		protected void afterHookedMethod(final MethodHookParam param) throws Throwable {	
				
        			   if( alarmIntent != null ){
        					alarm.cancel( alarmIntent );
        			   }
        			   
        				context = ( (TabActivity) param.thisObject ).getApplication();
        				
	        			Log.d( "Lync", "Starting alarm service..." );
						//grab context from an activity.  use it to start service, alarm manager, etc.
						alarm = ( AlarmManager )( context.getSystemService(Context.ALARM_SERVICE));
						
						Intent i = new Intent( context , lyncReceiver );
//						i.putExtra("activityMonitor", Main.activityMonitorClass  );
						
						alarmIntent = PendingIntent.getBroadcast( context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT );
						alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 2000L, 20000L, alarmIntent);
						Log.d( "Lync", "Started...Done." );
    			}
        	});
			
			//Below is UI Messing around, not useful.
/*
			XposedHelpers.findAndHookMethod( "com.microsoft.office.lync.proxy.Contact", lpparam.classLoader, "getActivityToken", new XC_MethodHook() {
			
				@Override
				protected void afterHookedMethod(MethodHookParam param)	throws Throwable {
					if( (boolean) contactClass.getMethod("isSelfContact").invoke(param.thisObject, (Object[]) null) ){
						Log.d("Lync", "We have hooked and invoked isSelfContact()" );
						param.setResult( "" );  
												//Explore state vs publishable state, and SelfContact's setPublishableState().

												//Looks like these are all local only (doesn't affect what server shows).
												//getActivityToken returns the 'special' presence states, such as inactive, meeting, etc.
												//Oddly enough, it includes off-work, also a user publishable state.
												//return empty string to maintain status.
												//(see MyStatusActivity.getPublishableStateFromContact)
												
												//getStatusImageResourceId contains all the color-coded images, including ones with
												//the out of office star (determined by parameter 3, or the second boolean b2.
						
												//Return "off-work" to get OOF star?.
					}
				}					
			});
*/
		}
	}
}