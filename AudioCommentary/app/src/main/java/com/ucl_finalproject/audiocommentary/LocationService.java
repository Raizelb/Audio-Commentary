package com.ucl_finalproject.audiocommentary;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Hoang on 07/02/2017.
 */

public class LocationService implements android.location.LocationListener {

    private static LocationService instance;
    private LocationManager mLocationManager;
    private final Context mContext;
    private Location currentLocation = null;

    // flag for GPS status
    public boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    @Override
    public void onLocationChanged(Location location) {
        if(currentLocation == null)
            currentLocation = location;
        //currentLocation.setLongitude(location.getLongitude());
        //currentLocation.setLatitude(location.getLatitude());
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("LocationService",provider + " Disabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("LocationService",provider + " Enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    /**
     * Singleton implementation
     * @return
     */
    public static LocationService getLocationManager(Context context) {
        if(instance == null) {
            instance = new LocationService(context);
        }
        return instance;
    }

    /**
     * Local constructor
     */
    private LocationService( Context context )     {
        mContext = context;
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        currentLocation = getLastBestLocation();
    }

    public Location getCurrentLocation() {
        if(getLastBestLocation() != null)
            return getLastBestLocation();
        return currentLocation;
    }


    public boolean isGPSEnabled() {
        isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isNetworkEnabled && !isGPSEnabled) {
            return false;
        }
        return true;
    }

    /**
     * @return the last know best location
     */
    private Location getLastBestLocation() {
        try {
            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            //Log.d("LocationService","" + (!isGPSEnabled && !isNetworkEnabled));

            /*if(!isNetworkEnabled && !isGPSEnabled) {
                showSettingsAlert();
            }*/

            Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            long GPSLocationTime = 0;
            if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

            long NetLocationTime = 0;

            if (null != locationNet) {
                NetLocationTime = locationNet.getTime();
            }

            if ( 0 < GPSLocationTime - NetLocationTime ) {
                return locationGPS;
            }
            else {
                return locationNet;
            }
        } catch(SecurityException e){
            //Toast.makeText(mContext, "Location Service Error: " + e.toString(),Toast.LENGTH_LONG);
            Log.d("LocationService", e.toString());
            return null;
        }
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     * */
    public void stopUsingGPS() {
        if (mLocationManager != null) {
            try {
                mLocationManager.removeUpdates(LocationService.this);
            } catch(SecurityException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     * */
    public void showSettingsAlert() {
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
