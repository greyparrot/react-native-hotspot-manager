
package com.hotspotmanager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.lang.reflect.Method;
import java.lang.Exception;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class RNHotspotManagerModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNHotspotManagerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNHotspotManager";
  }

  @ReactMethod
  public void createHotspot(
    String ssid,
    Callback errorCallback,
    Callback successCallback
  ){
    try {
			Boolean result = setWifiApEnabled(true, ssid);
				if( result== true){
					String status = "Created Network!";
					successCallback.invoke(true, status); 
				}else{
					 errorCallback.invoke(false, "Failed!");
				}
			} catch (Exception e) {  
        errorCallback.invoke(false, e.getMessage()); 
			}
  }

	// Use the class below to change/check the Wifi hotspot setting:

	// check whether wifi hotspot on or off
	public static boolean isApOn(Context context) {
		WifiManager wifimanager = (WifiManager) context
				.getSystemService(context.WIFI_SERVICE);
		try {
			Method method = wifimanager.getClass().getDeclaredMethod(
					"isWifiApEnabled");
			method.setAccessible(true);
			return (Boolean) method.invoke(wifimanager);
		} catch (Throwable ignored) {
		}
		return false;
	}

	public boolean setWifiApEnabled(boolean enabled, String ssid) {
		WifiManager mWifiManager = (WifiManager) getReactApplicationContext()
				.getSystemService(getReactApplicationContext().WIFI_SERVICE);

		WifiConfiguration wificonfiguration = new WifiConfiguration();
		// WifiConfiguration wificonfiguration =mwifi
		wificonfiguration.SSID = ssid;
//		wificonfiguration.preSharedKey=

		if (enabled) { // disable WiFi in any case
			mWifiManager.setWifiEnabled(false);
		}

		try {

			// TODO comment from here
			/*
			 * Method getWifiApConfigurationMethod =
			 * mWifiManager.getClass().getMethod("getWifiApConfiguration");
			 * Object config =
			 * getWifiApConfigurationMethod.invoke(mWifiManager);
			 */

			// configuration = null works for many devices
			Method setWifiApEnabledMethod = mWifiManager.getClass().getMethod(
					"setWifiApEnabled", WifiConfiguration.class, boolean.class);
			return (Boolean) setWifiApEnabledMethod.invoke(mWifiManager,
					wificonfiguration, enabled);
		} catch (Exception e) {
			// System.out.println(e);
			return false;
		}
	}

}