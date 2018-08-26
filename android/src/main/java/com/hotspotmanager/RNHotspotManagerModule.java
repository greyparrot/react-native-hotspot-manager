
package com.hotspotmanager;

// import com.facebook.react.bridge.ReactApplicationContext;
// import com.facebook.react.bridge.ReactContextBaseJavaModule;
// import com.facebook.react.bridge.ActivityEventListener;
// import com.facebook.react.bridge.ReactMethod;
// import com.facebook.react.bridge.Callback;

import com.facebook.react.bridge.*;

import android.util.Log;

import android.app.Activity;
import android.content.ComponentName;
import android.net.Uri;

import android.os.Bundle;
import android.provider.Settings;
import java.lang.reflect.Method;
import java.lang.Exception;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class RNHotspotManagerModule extends ReactContextBaseJavaModule implements ActivityEventListener {

	private static final int REQUEST_CODE = 78;
	private static Method getWifiApConfiguration;
	private static Method getWifiApState;
	private static Method setWifiApConfiguration;

	static {
		for (Method method : WifiManager.class.getDeclaredMethods()) {
			switch (method.getName()) {
			case "getWifiApConfiguration":
				getWifiApConfiguration = method;
				break;
			case "getWifiApState":
				getWifiApState = method;
				break;
			case "setWifiApConfiguration":
				setWifiApConfiguration = method;
				break;
			}
		}
	}

	Promise promise;
	String ssid;
	private final ReactApplicationContext reactContext;
	private WifiConfiguration m_original_config_backup;

	public RNHotspotManagerModule(ReactApplicationContext reactContext) {
		super(reactContext);
		this.reactContext = reactContext;
		reactContext.addActivityEventListener(this);
	}

	@Override
	public String getName() {
		return "RNHotspotManager";
	}

	public void writePermission() {
		// if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
		// if (!Settings.System.canWrite(getReactApplicationContext())) {
		Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
				Uri.parse("package:" + getReactApplicationContext().getPackageName()));
		getReactApplicationContext().startActivityForResult(intent, REQUEST_CODE, null);
		// }
		// }
	}

	public void _fireHotSpot() {
		Boolean result = setWifiApEnabled(true, this.ssid);
		WritableMap params = Arguments.createMap();
		if (result == true) {
			String ipAddress = this.getIPAddress();
			params.putString("status", "Created Network!");
			params.putString("ipAddress", ipAddress);
			// this.successCallback.invoke(true, params);
		} else {
			params.putString("status", "Failed");
			// this.errorCallback.invoke(false, params);
		}
		this.promise.resolve(params);
	}

	@ReactMethod
	public void createHotspot(String ssid, final Promise promise) {
		this.promise = promise;
		this.ssid = ssid;
		try {
			if (!Settings.System.canWrite(getReactApplicationContext())) {
				this.writePermission();
			} else {
				this._fireHotSpot();
			}
		} catch (Exception e) {
			this.promise.reject("Error");
			// errorCallback.invoke(false, e.getMessage());
		}
	}

	@ReactMethod
	public void disableHotspot() {
		// restore original hotspot config if available
		if (null != m_original_config_backup)
			this.setHotspotConfig(m_original_config_backup);
		this.setHotspotEnabled(m_original_config_backup, false);
	}

	public WifiConfiguration getConfiguration() {
		WifiManager wifimanager = (WifiManager) getReactApplicationContext()
				.getSystemService(getReactApplicationContext().WIFI_SERVICE);
		Object result = invokeSilently(getWifiApConfiguration, wifimanager);
		if (result == null) {
			return null;
		}
		return (WifiConfiguration) result;
	}

	private boolean setHotspotEnabled(WifiConfiguration config, boolean enabled) {
		try {
			WifiManager wifimanager = (WifiManager) getReactApplicationContext()
					.getSystemService(getReactApplicationContext().WIFI_SERVICE);
			Method setWifiApEnabledMethod = wifimanager.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, boolean.class);
			Object result = invokeSilently(setWifiApEnabledMethod, wifimanager, config, enabled);
			if (result == null) {
				return false;
			}
			return (Boolean) result;
		} catch (Exception e) {
			Log.e("RN Hotspot Manager", "error: " + e.getMessage());
			return false;
		}
	}

	private boolean setHotspotConfig(WifiConfiguration config) {
		WifiManager wifimanager = (WifiManager) getReactApplicationContext()
				.getSystemService(getReactApplicationContext().WIFI_SERVICE);
		Object result = invokeSilently(setWifiApConfiguration, wifimanager, config);
		if (result == null) {
			return false;
		}
		return (Boolean) result;
	}
	// Use the class below to change/check the Wifi hotspot setting:

	// check whether wifi hotspot on or off
	public boolean isApOn(Context context) {
		WifiManager wifimanager = (WifiManager) getReactApplicationContext()
				.getSystemService(getReactApplicationContext().WIFI_SERVICE);
		try {
			Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
			method.setAccessible(true);
			return (Boolean) method.invoke(wifimanager);
		} catch (Throwable ignored) {
		}
		return false;
	}

	private static Object invokeSilently(Method method, Object receiver, Object... args) {
		try {
			return method.invoke(receiver, args);
		} catch (Exception e) {
			Log.e("RN Hotspot Manager", "error invoking methods: " + e.getMessage());
		}
		return null;
	}

	    public String getIPAddress() {
        String ipAddress = "0.0.0.0";

        for (InterfaceAddress address : getInetAddresses()) {
            if (!address.getAddress().isLoopbackAddress() && address.getAddress() instanceof Inet4Address) {
                ipAddress = address.getAddress().getHostAddress().toString();
            }
}
		return ipAddress;
    }


	private List<InterfaceAddress> getInetAddresses() {
        List<InterfaceAddress> addresses = new ArrayList<>();
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();

                for (InterfaceAddress interface_address : intf.getInterfaceAddresses()) {
                    addresses.add(interface_address);
                }
            }
        } catch (Exception ex) {
            Log.e("RN Hotspot Manager", ex.toString());
        }
        return addresses;
}

	public boolean setWifiApEnabled(boolean enabled, String ssid) {
		WifiManager mWifiManager = (WifiManager) getReactApplicationContext()
				.getSystemService(getReactApplicationContext().WIFI_SERVICE);
		m_original_config_backup = this.getConfiguration();
		WifiConfiguration wificonfiguration = new WifiConfiguration();
		// WifiConfiguration wificonfiguration =mwifi
		wificonfiguration.SSID = ssid;
		// wificonfiguration.preSharedKey=

		if (enabled) { // disable WiFi in any case
			mWifiManager.setWifiEnabled(false);
		}

		try {

			// configuration = null works for many devices
			Method setWifiApEnabledMethod = mWifiManager.getClass().getMethod("setWifiApEnabled",
					WifiConfiguration.class, boolean.class);
			return (Boolean) setWifiApEnabledMethod.invoke(mWifiManager, wificonfiguration, enabled);
		} catch (Exception e) {
			// System.out.println(e);
			return false;
		}
	}

	@Override
	public void onNewIntent(Intent intent) {
	}

	@Override
	public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent intent) {
		if (requestCode == REQUEST_CODE) {
			this._fireHotSpot();
		}
	}

}
