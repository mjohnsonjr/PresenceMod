package com.michael.lyncpresence;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage {
	
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		
		if (!lpparam.packageName.equals("com.android.systemui")){//check if the package being loaded is systemUI
		
            return;
		}
		//All code here is only called if it is indeed LooperThread for Lync
		else {

			findAndHookMethod("com.android.systemui.statusbar.policy.Clock", lpparam.classLoader, "updateClock", new XC_MethodHook() {
	            @Override
	            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

	            	Log.i("LYNC_XPOSED", "Reached the FindAndHook on Clock Update." );
	            	
	            	//Simulate user input
	            	Class<?> Tab = XposedHelpers.findClass("com.microsoft.office.lync.proxy.UserActivityMonitor", null);
	            	//XposedHelpers.callMethod(Tab.newInstance(), "reportUserInteraction");
	            	XposedHelpers.callStaticMethod(Tab, "reportUserInteraction");//Method(Tab.newInstance(), "reportUserInteraction");
	            	
	            }
			});
			
		}
	}

}
