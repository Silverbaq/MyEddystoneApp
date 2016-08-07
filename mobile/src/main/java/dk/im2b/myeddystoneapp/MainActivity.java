package dk.im2b.myeddystoneapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.connection.internal.protocols.Operation;
import com.estimote.sdk.eddystone.Eddystone;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BeaconManager beaconManager;
    private BeaconManager beaconManager2;
    private Region region;

    private String scanId;

    private String TAG = "MAIN";

    ListView lvBeacon, lvEddystone;
    BeaconDetailsAdapter beaconAdapter;
    EddystoneAdapter eddystoneAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvBeacon = (ListView) findViewById(R.id.activityMain_ListviewBeacon);
        lvEddystone = (ListView)findViewById(R.id.activityMain_ListviewEddystone);

        beaconManager = new BeaconManager(MainActivity.this);
        beaconManager2 = new BeaconManager(MainActivity.this);

        region = new Region("ranged region", null, null, null);


        // iBeacon
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    // Check if beacon is in list (have been registered)
                    for (Beacon b : list) {
                        Log.d(TAG, "beacon Nearby!!!! : " + b);
                    }
                    beaconAdapter = new BeaconDetailsAdapter(MainActivity.this, list);
                    lvBeacon.setAdapter(beaconAdapter);

                }
            }
        });


        beaconManager.startRanging(region);


        // Eddystone
        beaconManager2.setEddystoneListener(new BeaconManager.EddystoneListener() {
            @Override
            public void onEddystonesFound(List<Eddystone> eddystones) {
                //Log.d(TAG, "Nearby Eddystone beacons: " + eddystones);
                for (Eddystone eddystone : eddystones) {
                    if (eddystone.isUrl()) Log.d(TAG, "" + eddystone.url);
                    else if (eddystone.isUid()) Log.d(TAG, "isUid: " + eddystone.calibratedTxPower);
                }
                eddystoneAdapter = new EddystoneAdapter(MainActivity.this, eddystones);
                lvEddystone.setAdapter(eddystoneAdapter);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });

        beaconManager2.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                scanId = beaconManager2.startEddystoneScanning();
            }
        });


    }


    @Override
    protected void onStop() {
        super.onStop();
        beaconManager.stopRanging(region);
        beaconManager2.stopEddystoneScanning(scanId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.disconnect();
        beaconManager2.disconnect();
    }
}
