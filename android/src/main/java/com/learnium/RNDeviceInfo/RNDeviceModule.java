package com.learnium.RNDeviceInfo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

public class RNDeviceModule extends ReactContextBaseJavaModule {

  ReactApplicationContext reactContext;

  public RNDeviceModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNDeviceInfo";
  }

  private String getCurrentLanguage() {
      Locale current = getReactApplicationContext().getResources().getConfiguration().locale;
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          return current.toLanguageTag();
      } else {
          StringBuilder builder = new StringBuilder();
          builder.append(current.getLanguage());
          if (current.getCountry() != null) {
              builder.append("-");
              builder.append(current.getCountry());
          }
          return builder.toString();
      }
  }

  private String getCurrentCountry() {
    Locale current = getReactApplicationContext().getResources().getConfiguration().locale;
    return current.getCountry();
  }

  @Override
  public @Nullable Map<String, Object> getConstants() {
    HashMap<String, Object> constants = new HashMap<String, Object>();

    PackageManager packageManager = this.reactContext.getPackageManager();
    String packageName = this.reactContext.getPackageName();

    constants.put("appVersion", "not available");
    constants.put("buildNumber", 0);

    try {
      PackageInfo info = packageManager.getPackageInfo(packageName, 0);
      constants.put("appVersion", info.versionName);
      constants.put("buildNumber", info.versionCode);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    String deviceName = "Unknown";

    // try {
      // BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
      // deviceName = myDevice.getName();
    // } catch(Exception e) {
      // e.printStackTrace();
    // }

    constants.put("deviceName", deviceName);
    constants.put("systemName", "Android");
    constants.put("systemVersion", Build.VERSION.RELEASE);
    constants.put("model", Build.MODEL);
    constants.put("deviceId", Build.BOARD);
    constants.put("deviceLocale", this.getCurrentLanguage());
    constants.put("deviceCountry", this.getCurrentCountry());
    constants.put("uniqueId", Secure.getString(this.reactContext.getContentResolver(), Secure.ANDROID_ID));
    constants.put("systemManufacturer", Build.MANUFACTURER);
    constants.put("bundleId", packageName);
    constants.put("userAgent", System.getProperty("http.agent"));


      TelephonyManager telephony = (TelephonyManager) reactContext.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

      constants.put("networkCountryIso", telephony.getNetworkCountryIso());
      constants.put("networkOperator", telephony.getNetworkOperator());
      constants.put("networkOperatorName", telephony.getNetworkOperatorName());

      try {
          constants.put("simOperator", telephony.getSimOperator());
          constants.put("simOperatorName", telephony.getSimOperatorName());
          constants.put("simSerialNumber", telephony.getSimSerialNumber());
      }
      catch(Exception e){
          e.printStackTrace();
      }
    return constants;
  }
}
