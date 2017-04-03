package com.ucl_finalproject.audiocommentary;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by Hoang on 07/02/2017.
 */

public class MatchMakingActivity extends Activity {

    private Spinner homeTeam, awayTeam;
    //private Location currentLocation;
    private Location stadium;
    private String firstTeam, secondTeam;
    private LocationService locationService;
    private Button submit;
    private final int mThreshold = 200;
    private Context mContext;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_making);

        homeTeam = (Spinner) findViewById(R.id.spinner_home_team);
        awayTeam = (Spinner) findViewById(R.id.spinner_away_team);

        mContext = this;

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Create an adapter from the string array resource and use
        // android's inbuilt layout file simple_spinner_item
        // that represents the default spinner in the UI
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.premier_league, android.R.layout.simple_spinner_item);
        // Set the layout to use for each dropdown item
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        homeTeam.setAdapter(adapter);
        awayTeam.setAdapter(adapter);

        homeTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                firstTeam = parent.getItemAtPosition(position).toString();
                Log.d("MatchMakingActivity",firstTeam);
                try {
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    List<Address> addresses;
                    //addresses = geocoder.getFromLocationName("King's Park Dr, Bournemouth BH7 7AF", 1);
                    String[] stadiums = getResources().getStringArray(R.array.premier_league_stadiums);
                    addresses = geocoder.getFromLocationName(stadiums[position], 1);
                    if (addresses.size() > 0) {
                        double latitude = addresses.get(0).getLatitude();
                        double longitude = addresses.get(0).getLongitude();
                        Log.d("MatchMakingActivity",latitude + " " + longitude);
                        stadium = new Location("stadium");
                        stadium.setLatitude(latitude);
                        stadium.setLongitude(longitude);
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        awayTeam.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                secondTeam = parent.getItemAtPosition(position).toString();
                Log.d("MatchMakingActivity",secondTeam);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit = (Button) findViewById(R.id.match_making_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationService = LocationService.getLocationManager(MatchMakingActivity.this);
                if(!locationService.isGPSEnabled()) {
                    showSettingsAlert(mContext);
                }
                else {
                    locationService = LocationService.getLocationManager(MatchMakingActivity.this);
                    if(locationService.getCurrentLocation() == null) {
                        if(!pDialog.isShowing()) {
                            pDialog.setMessage("Getting Location...");
                            pDialog.show();
                        }
                        //Log.d("MatchMakingActivity",locationService.getCurrentLocation() + "");
                    }
                    hideDialog();
                    Log.d("MatchMakingActivity", "" + locationService.getCurrentLocation());
                    Log.d("MatchMakingActivity", stadium.distanceTo(locationService.getCurrentLocation()) + "");
                    double mDistance = stadium.distanceTo(locationService.getCurrentLocation());
                    if(firstTeam != secondTeam) {
                        if (mDistance <= mThreshold) {
                            startActivity(new Intent(getApplicationContext(), StreamActivity.class));
                        } else {
                            Toast.makeText(getApplicationContext(), "Distance from stadium: " + mDistance, Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(),"Please choose a different Away Team",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void hideDialog() {
        if(pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     * */
    public void showSettingsAlert(final Context mContext) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog
                .setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mContext.startActivity(intent);
                    }
                });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Showing Alert Message
        alertDialog.show();
    }


}
