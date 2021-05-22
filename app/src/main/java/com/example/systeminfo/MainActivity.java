package com.example.systeminfo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    TelephonyManager telephonyManager;
    TextView deviceName;
    TextView imeiNumber;
    TextView manufacturer;
    TextView model;
    TextView ram;
    TextView storageDetails;
    TextView androidVersion;
    TextView buildNumber;
    TextView cpu;
    TextView shareApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);


        deviceName = findViewById(R.id.device_name);
        imeiNumber = findViewById(R.id.imei_number);
        manufacturer = findViewById(R.id.manufacturer);
        model = findViewById(R.id.model);
        ram = findViewById(R.id.ram);
        storageDetails = findViewById(R.id.storage);
        androidVersion = findViewById(R.id.android_version);
        buildNumber = findViewById(R.id.build_number);
        cpu = findViewById(R.id.cpu);
        shareApp = findViewById(R.id.share_app);

        shareApp.setOnClickListener(v -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                String shareMessage= "";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
                System.out.println("Inside catch");
                //e.toString();
            }
        });

        deviceName.setText(Settings.Secure.getString(getContentResolver(), "bluetooth_name"));

        manufacturer.setText(Build.MANUFACTURER);

        model.setText(Build.MODEL);



        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        double availableGb = (mi.availMem / 0x100000L)/1024;
        double totalGb = (mi.totalMem/ 0x100000L)/1024;

        //Percentage can be calculated for API 16+
        int percentAvail =100- (int) (mi.availMem / (double)mi.totalMem * 100.0);

        ram.setText("Used RAM: "+percentAvail+"%\nAvailable RAM: "+availableGb+"GB\nTotal RAM: "+ totalGb+"GB\n(remaining is used by system)");

        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable, totalBytes;
        if (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.getBlockSizeLong() * stat.getAvailableBlocksLong();
            totalBytes = stat.getBlockSizeLong() * stat.getBlockCountLong();
        }
        else {
            bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
            totalBytes = (long)stat.getBlockSize() * (long)stat.getBlockCount();
        }
        long gbAvailable = bytesAvailable / (1024 * 1024 * 1024);
        long gbTotal = totalBytes / (1024 * 1024 * 1024);

        storageDetails.setText("Available Storage: "+gbAvailable+"GB\nTotal Storage: "+ gbTotal+"GB\n(remaining is used by system)");

        androidVersion.setText(Build.VERSION.RELEASE);

        cpu.setText(Build.HARDWARE);

        buildNumber.setText(Build.DISPLAY);

        if (ActivityCompat.checkSelfPermission(this, READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE},
                    PERMISSION_REQUEST_CODE);
        }else {
            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);

            String imeiSIM1 = telephonyInfo.getImsiSIM1();
            String imeiSIM2 = telephonyInfo.getImsiSIM2();
            System.out.println("abcdef "+telephonyInfo.getImsiSIM1());

            boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
            boolean isSIM2Ready = telephonyInfo.isSIM2Ready();

            boolean isDualSIM = telephonyInfo.isDualSIM();

            if (isDualSIM){
                imeiNumber.setText(imeiSIM1+"\n"+imeiSIM2);
            } else {
                imeiNumber.setText(imeiSIM1);
            }

            System.out.println("xyzzz isDualSim "+ isDualSIM+" Sim1Imei "+imeiSIM1+" Sim2Imei "+imeiSIM2+
                    " isSimReady1 "+isSIM1Ready+ " isSimReady2 "+ isSIM2Ready);

            System.out.println("sdf "+telephonyManager);
        }
    }
}