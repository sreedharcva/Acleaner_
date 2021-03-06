package com.test.acleaner;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.the.bestna.cleaner.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

import adapters.AdapterGridviewBatery;
import interfaces.IAdShower;

public class BateryActivity_Fragment extends Fragment implements AdapterView.OnItemClickListener {

    private int level = 0;
    private float fromDegrees;
    private float toDegrees;
    private Toast msg;
    private TextView textTemperature;
    private TextView textVoltage;
    private TextView textCapacity;
    private ImageView state;
    private ImageView imageView;
    private boolean sw = true;
    private GridView gridview;
    private AdapterGridviewBatery batery;

    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int temperature = intent.getIntExtra(
                    BatteryManager.EXTRA_TEMPERATURE, 0);
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,
                    -1);
            boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
            boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

            textVoltage.setText(activity.getResources().getString(R.string.voltage)+":" + ((float) voltage / 1000) + " V");
            textTemperature.setText(activity.getResources().getString(R.string.tempreture)+": "
                    + ((float) temperature / 10) + " ºC");
            textCapacity.setText(activity.getResources().getString(R.string.Capacity)+": " + (int) getBatteryCapacity()
                    + " mAh");

            state.setVisibility(View.INVISIBLE);

            if (usbCharge) {
                state.setImageDrawable(getResources().getDrawable(
                        R.drawable.usb));
                state.setVisibility(View.VISIBLE);
            } else {
                if (acCharge) {
                    state.setImageDrawable(getResources().getDrawable(
                            R.drawable.ac));
                    state.setVisibility(View.VISIBLE);
                }
            }

            if (sw) {
                sw = false;

                toDegrees = (level * 253f) / 100;

                Animation rotate = createAnimationRotate(1000, fromDegrees,
                        toDegrees);

                rotate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        sw = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                rotate.reset();

                imageView.startAnimation(rotate);

                fromDegrees = toDegrees;

            }

        }
    };
    private int valueBrightness = 0;
    private int valueVolumen = 0;
    private int valueTimeOut = 0;


    //private AdView mAdView; on Resume Method

    private View parentView;
    private Bundle saveInstance;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.activity_batery, container, false);
        this.saveInstance = savedInstanceState;
        activity = getActivity();
        init();
        return parentView;
    }


    private void init() {

        textTemperature = (TextView) parentView.findViewById(R.id.text_temperature);
        textVoltage = (TextView) parentView.findViewById(R.id.text_voltage);
        textCapacity = (TextView) parentView.findViewById(R.id.text_capacity);
        state = (ImageView) parentView.findViewById(R.id.state);
        imageView = (ImageView) parentView.findViewById(R.id.aguja);
        gridview = (GridView) parentView.findViewById(R.id.gridview);
        gridview.setOnItemClickListener(this);
        batery = new AdapterGridviewBatery(activity);
        gridview.setAdapter(batery);

        Manager.Style.roboto(activity, textTemperature,
                textVoltage, textCapacity);

        msg = Toast.makeText(activity, "", Toast.LENGTH_SHORT);

        loadOptions();
        fromDegrees = 0F;
        toDegrees = 0F;

        // barraTitulo();

    }

    @Override
    public void onResume() {
        activity.registerReceiver(this.batteryInfoReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));
        loadSettings();

//        mAdView = (AdView) parentView.findViewById(R.id.adView);
//        mAdView.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//                mAdView.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) {
//                super.onAdFailedToLoad(errorCode);
//                mAdView.setVisibility(View.GONE);
//            }
//
//        });
//
//        mAdView.loadAd(new AdRequest.Builder().build());
        super.onResume();
    }

    @Override
    public void onPause() {
        activity.unregisterReceiver(this.batteryInfoReceiver);
        super.onPause();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.history, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                startActivity(new Intent(this, MainActivity.class));
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//        // return true;
//    }

    private void loadOptions() {
        Typeface typeface = Typeface.createFromAsset(activity.getAssets(),
                "fonts/Roboto-Light.ttf");
        for (int i = 0; i < 6; i++) {
            LayoutInflater inflate = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            batery.arrayView[i] = inflate.inflate(R.layout.item_gridview_main,
                    null);
            TextView texto = (TextView) batery.arrayView[i]
                    .findViewById(R.id.texto);
            texto.setText(batery.title[i]);
            texto.setTypeface(typeface);
            texto.setCompoundDrawablesWithIntrinsicBounds(0, batery.icons[i],
                    0, 0);
        }
    }

    private void loadSettings() {
        stateGps();
        stateWifi();
        stateData();
        stateBrightness();
        stateTimeOut();
        stateVolumen();
    }

    private static Animation createAnimationRotate(long duration,
                                                   float fromDegrees, float toDegrees) {
        AnimationSet animationSet = new AnimationSet(false);
        animationSet.setFillAfter(true);
        Animation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setStartOffset(0);
        rotateAnimation.setDuration(duration);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setFillAfter(true);

        animationSet.addAnimation(rotateAnimation);

        return animationSet;
    }

    private void stateWifi() {

        WifiManager wifiManager = (WifiManager) activity
                .getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            batery.changedImage(R.drawable.wifi_on, 0);
        } else {
            batery.changedImage(R.drawable.wifi_off, 0);
        }
    }

    private void stateData() {

        boolean mobileDataEnabled = getStateDataDevice();

        if (mobileDataEnabled) {
            batery.changedImage(R.drawable.data_on, 1);
        } else {
            batery.changedImage(R.drawable.data_off, 1);
        }
    }

    private void stateBrightness() {

        try {
            if (Settings.System.getInt(activity.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
                batery.changedImage(R.drawable.bri_auto, 2);
                valueBrightness = 0;
            } else {
                int value = Settings.System.getInt(activity.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS);

                if (value > 0 && value <= 85) {
                    batery.changedImage(R.drawable.bri_low, 2);
                    valueBrightness = 1;
                }
                if (value > 85 && value <= 170) {
                    batery.changedImage(R.drawable.bri_medio, 2);
                    valueBrightness = 2;
                }
                if (value > 170) {
                    batery.changedImage(R.drawable.bri_all, 2);
                    valueBrightness = 3;
                }

            }
        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void stateVolumen() {
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

        if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_VIBRATE) {
            batery.changedImage(R.drawable.vibrate, 3);
            valueVolumen = 0;
        } else {
            int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
            int value = (audioManager.getStreamVolume(AudioManager.STREAM_RING) * 100)
                    / max;
            if (value >= 0 && value <= 30) {
                batery.changedImage(R.drawable.vol_low, 3);
                valueVolumen = 1;
            }
            if (value > 30 && value <= 60) {
                batery.changedImage(R.drawable.vol_medio, 3);
                valueVolumen = 2;
            }
            if (value > 60) {
                batery.changedImage(R.drawable.vol_all, 3);
                valueVolumen = 3;
            }
        }

    }

    private void stateGps() {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            batery.changedImage(R.drawable.gps_off, 4);
        } else {
            batery.changedImage(R.drawable.gps_on, 4);
        }
    }

    private void stateTimeOut() {
        try {
            int value = Settings.System.getInt(activity.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);

            batery.changedImage(R.drawable.time_out_30m, 5);
            valueTimeOut = 0;

            if (value == 15000) {
                batery.changedImage(R.drawable.time_out_15s, 5);
                valueTimeOut = 1;
            }
            if (value == 30000) {
                batery.changedImage(R.drawable.time_out_30s, 5);
                valueTimeOut = 2;
            }
            if (value == 60000) {
                batery.changedImage(R.drawable.time_out_1m, 5);
                valueTimeOut = 3;
            }
            if (value == 120000) {
                batery.changedImage(R.drawable.time_out_2m, 5);
                valueTimeOut = 4;
            }
            if (value == 600000) {
                batery.changedImage(R.drawable.time_out_10m, 5);
                valueTimeOut = 5;
            }

        } catch (SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private boolean getStateDataDevice() {
        Boolean mobileDataEnabled = false;
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
        }

        return mobileDataEnabled;
    }

//    private void barraTitulo() {
//
//        ActionBar actionbar = this.getSupportActionBar();
//        actionbar.setIcon(R.drawable.batery_bar);
//        SpannableString s = new SpannableString("Batery Analitic");
//        s.setSpan(new TypefaceSpan(this, "Roboto-Light.ttf"), 0, s.length(),
//                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        actionbar.setTitle(s);
//        actionbar.setDisplayUseLogoEnabled(true);
//        actionbar.setDisplayHomeAsUpEnabled(true);
//
//    }

    private double getBatteryCapacity() {
        Object mPowerProfile_ = null;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        double batteryCapacity = 0D;

        try {

            batteryCapacity = (Double) Class.forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", String.class)
                    .invoke(mPowerProfile_, "battery.capacity");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return batteryCapacity;
    }

    private void setMobileDataEnabled(Context context, boolean enabled)
            throws ClassNotFoundException, NoSuchFieldException,
            IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        final ConnectivityManager conman = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("rawtypes")
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass
                .getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField
                .get(conman);
        @SuppressWarnings("rawtypes")
        final Class iConnectivityManagerClass = Class
                .forName(iConnectivityManager.getClass().getName());
        @SuppressWarnings("unchecked")
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        ((IAdShower)BateryActivity_Fragment.this.getActivity()).ShowAd();

    }

    private void changeWifi() {
        WifiManager wifiManager = (WifiManager) activity
                .getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            batery.changedImage(R.drawable.wifi_off, 0);
            msg.cancel();
            msg.setText(activity.getResources().getString(R.string.wifi)+" : "+ activity.getResources().getString(R.string.off));
            msg.show();
            wifiManager.setWifiEnabled(false);
        } else {
            batery.changedImage(R.drawable.wifi_on, 0);
            msg.cancel();
            msg.setText("Wifi: ON");
            msg.show();
            wifiManager.setWifiEnabled(true);
        }
    }

    private void changeData() {

        boolean state = true;
        if (getStateDataDevice()) {
            state = false;
        }

        if (state) {
            batery.changedImage(R.drawable.data_on, 1);
            msg.cancel();
            msg.setText("Data: ON");
            msg.show();
        } else {
            batery.changedImage(R.drawable.data_off, 1);
            msg.cancel();
            msg.setText("Data: OFF");
            msg.show();
        }

        try {
            setMobileDataEnabled(activity, state);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private void changeTimeOut() {
        switch (valueTimeOut) {
            case 0: // 15s
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_OFF_TIMEOUT, 15000);
                batery.changedImage(R.drawable.time_out_15s, 5);
                msg.cancel();
                msg.setText("Time out: 15s");
                msg.show();
                valueTimeOut++;
                break;
            case 1:// 30s
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_OFF_TIMEOUT, 30000);
                batery.changedImage(R.drawable.time_out_30s, 5);
                msg.cancel();
                msg.setText("Time out: 30s");
                msg.show();
                valueTimeOut++;
                break;
            case 2:// 1M
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_OFF_TIMEOUT, 60000);
                batery.changedImage(R.drawable.time_out_1m, 5);
                msg.cancel();
                msg.setText("Time out: 1M");
                msg.show();
                valueTimeOut++;
                break;
            case 3:// 2M
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_OFF_TIMEOUT, 120000);
                batery.changedImage(R.drawable.time_out_2m, 5);
                msg.cancel();
                msg.setText("Time out: 2M");
                msg.show();
                valueTimeOut++;
                break;
            case 4:// 10M
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_OFF_TIMEOUT, 600000);
                batery.changedImage(R.drawable.time_out_10m, 5);
                msg.cancel();
                msg.setText("Time out: 10M");
                msg.show();
                valueTimeOut++;
                break;
            default:// 30M
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_OFF_TIMEOUT, 1800000);
                batery.changedImage(R.drawable.time_out_30m, 5);
                msg.cancel();
                msg.setText("Time out: 30M");
                msg.show();
                valueTimeOut = 0;
        }
    }

    private void changeGps() {
        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        ((IAdShower)BateryActivity_Fragment.this.getActivity()).ShowAd();
    }

    private void changeScreenBrightness() {
        switch (valueBrightness) {
            case 0: // 10%
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 25);
                batery.changedImage(R.drawable.bri_low, 2);
                msg.cancel();
                msg.setText("Brightness: 10%");
                msg.show();
                valueBrightness++;
                break;
            case 1:// 50%
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 125);

                batery.changedImage(R.drawable.bri_medio, 2);
                msg.cancel();
                msg.setText("Brightness: 50%");
                msg.show();
                valueBrightness++;
                break;
            case 2:// 100%
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS, 255);
                batery.changedImage(R.drawable.bri_all, 2);
                msg.cancel();
                msg.setText("Brightness: 100%");
                msg.show();
                valueBrightness++;
                break;
            default:// auto
                Settings.System.putInt(activity.getContentResolver(),
                        Settings.System.SCREEN_BRIGHTNESS_MODE,
                        Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
                batery.changedImage(R.drawable.bri_auto, 2);
                msg.cancel();
                msg.setText("Brightness: AUTO");
                msg.show();
                valueBrightness = 0;
        }
    }

    private void changeVolumen() {
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
        switch (valueVolumen) {
            case 0: // 10%
                audioManager.setStreamVolume(AudioManager.STREAM_RING,
                        (10 * max) / 100, AudioManager.FLAG_PLAY_SOUND);
                batery.changedImage(R.drawable.vol_low, 3);
                msg.cancel();
                msg.setText("Volumen: 10%");
                msg.show();
                valueVolumen++;
                break;
            case 1:// 50%
                audioManager.setStreamVolume(AudioManager.STREAM_RING,
                        (50 * max) / 100, AudioManager.FLAG_PLAY_SOUND);
                batery.changedImage(R.drawable.vol_medio, 3);
                msg.cancel();
                msg.setText("Volumen: 50%");
                msg.show();
                valueVolumen++;
                break;
            case 2:// 100%
                audioManager.setStreamVolume(AudioManager.STREAM_RING, max,
                        AudioManager.FLAG_PLAY_SOUND);
                batery.changedImage(R.drawable.vol_all, 3);
                msg.cancel();
                msg.setText("Volumen: 100%");
                msg.show();
                valueVolumen++;
                break;
            default:// vibrar
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0,
                        AudioManager.FLAG_VIBRATE);
                batery.changedImage(R.drawable.vibrate, 3);
                msg.cancel();
                msg.setText("Volumen: Vibrate");
                msg.show();
                valueVolumen = 0;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case 0:
                changeWifi();
                break;
            case 1:
                changeData();
                break;
            case 2:
                changeScreenBrightness();
                break;
            case 3:
                changeVolumen();
                break;
            case 4:
                changeGps();
                break;
            case 5:
                changeTimeOut();
                break;
        }
        if((new Random().nextInt()%2) == 0)
            ((IAdShower)BateryActivity_Fragment.this.getActivity()).ShowAd();
    }

}
