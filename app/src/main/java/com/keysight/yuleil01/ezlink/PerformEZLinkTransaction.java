package com.keysight.yuleil01.ezlink;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.script.model.ExecutionRequest;
import com.google.api.services.script.model.Operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PerformEZLinkTransaction extends AppCompatActivity {

    private View mrtStationFrom, mrtStationTo, busNumber, busStopFrom, busStopTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perform_ezlink_transaction);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();

        mrtStationFrom = findViewById(R.id.selectedSmrtStation1);
        mrtStationTo = findViewById(R.id.selectedSmrtStation2);
        busNumber = findViewById(R.id.selectedBusNumber);
        busStopFrom = findViewById(R.id.selectedBusStop1);
        busStopTo = findViewById(R.id.selectedBusStop2);

        // Capture the layout's TextView and set the string as its text
        ((TextView) findViewById(R.id.selectedCardNumber)).setText("EZLink Card Number: " + intent.getStringExtra(MainActivity.EZLINK_CARD_NUMBER));
        String transportationType = intent.getStringExtra(MainActivity.TRANSPORTATION_TYPE);
        ((TextView) findViewById(R.id.selectedTransportationType)).setText("Transportation Type: " + transportationType);
        if (transportationType.equals("MRT")) {
            busNumber.setVisibility(View.GONE);
            busStopFrom.setVisibility(View.GONE);
            busStopTo.setVisibility(View.GONE);
            ((TextView) mrtStationFrom).setText("SMRT Station (From): " + intent.getStringExtra(MainActivity.MRT_STATION_FROM));
            ((TextView) mrtStationTo).setText("SMRT Station (To): " + intent.getStringExtra(MainActivity.MRT_STATION_TO));
        } else if (transportationType.equals("BUS")) {
            mrtStationFrom.setVisibility(View.GONE);
            mrtStationTo.setVisibility(View.GONE);
            ((TextView) busNumber).setText("Bus Number: " + intent.getStringExtra(MainActivity.BUS_NUMBER));
            ((TextView) busStopFrom).setText("Bus Stop (From): " + intent.getStringExtra(MainActivity.BUS_STOP_FROM));
            ((TextView) busStopTo).setText("Bus Stop (To): " + intent.getStringExtra(MainActivity.BUS_STOP_TO));
        } else if (transportationType.equals("RETAIL")) {
            mrtStationFrom.setVisibility(View.GONE);
            mrtStationTo.setVisibility(View.GONE);
            busNumber.setVisibility(View.GONE);
            busStopFrom.setVisibility(View.GONE);
            busStopTo.setVisibility(View.GONE);
        } else if (transportationType.equals("TOP UP")) {
            mrtStationFrom.setVisibility(View.GONE);
            mrtStationTo.setVisibility(View.GONE);
            busNumber.setVisibility(View.GONE);
            busStopFrom.setVisibility(View.GONE);
            busStopTo.setVisibility(View.GONE);
        } else {
            //Do nothing here.
        }
        ((TextView) findViewById(R.id.distanceTraveled)).setText("Distance (km): " + intent.getStringExtra(MainActivity.EZLINK_RESULT1));
        //findViewById(R.id.distanceTraveled).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.fareIncurred)).setText("Fare (SGD): " + intent.getStringExtra(MainActivity.EZLINK_RESULT2));
        ((TextView) findViewById(R.id.balanceBefore)).setText("Balance before (SGD): " + intent.getStringExtra(MainActivity.EZLINK_RESULT3));
        ((TextView) findViewById(R.id.balanceAfter)).setText("Balance after (SGD): " + intent.getStringExtra(MainActivity.EZLINK_RESULT4));
    }
}
