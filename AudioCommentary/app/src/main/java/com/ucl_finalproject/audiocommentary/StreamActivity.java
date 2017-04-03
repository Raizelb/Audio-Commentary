package com.ucl_finalproject.audiocommentary;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wowza.gocoder.sdk.api.WowzaGoCoder;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcast;
import com.wowza.gocoder.sdk.api.broadcast.WZBroadcastConfig;
import com.wowza.gocoder.sdk.api.configuration.WZMediaConfig;
import com.wowza.gocoder.sdk.api.configuration.WowzaConfig;
import com.wowza.gocoder.sdk.api.devices.WZAudioDevice;
import com.wowza.gocoder.sdk.api.devices.WZCameraView;
import com.wowza.gocoder.sdk.api.errors.WZError;
import com.wowza.gocoder.sdk.api.errors.WZStreamingError;
import com.wowza.gocoder.sdk.api.logging.WZLog;
import com.wowza.gocoder.sdk.api.status.WZState;
import com.wowza.gocoder.sdk.api.status.WZStatus;
import com.wowza.gocoder.sdk.api.status.WZStatusCallback;

/**
 * Created by Hoang on 23/01/2017.
 */

public class StreamActivity extends Activity implements WZStatusCallback, View.OnClickListener{

    // The top level GoCoder API interface
    private WowzaGoCoder goCoder;

    // The GoCoder SDK camera view
    private WZCameraView goCoderCameraView;

    // The GoCoder SDK broadcaster
    WZBroadcast goCoderBroadcaster;

    // The broadcast configuration settings
    WZBroadcastConfig broadcastConfig;

    protected WZAudioDevice wzAudioDevice = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stream);
        // Get the camera view
        goCoderCameraView = (WZCameraView) findViewById(R.id.camera_preview);
        final Button broadcastButton = (Button) findViewById(R.id.broadcast_button);
        broadcastButton.setOnClickListener(this);
        initialise();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (goCoderCameraView != null) {
            if (goCoderCameraView.isPreviewPaused())
                goCoderCameraView.onResume();
            else
                goCoderCameraView.startPreview();
        }
    }

    @Override
    public void onClick(View view) {
        // Ensure the minimum set of configuration settings have been specified necessary to
        // initiate a broadcast streaming session
        WZStreamingError configValidationError = broadcastConfig.validateForBroadcast();

        if (configValidationError != null) {
            Log.d("Wowza config",configValidationError.getErrorDescription());
        } else if (goCoderBroadcaster.getStatus().isRunning()) {
            // Stop the broadcast that is currently running
            goCoderBroadcaster.endBroadcast();
        }
         else {
        // Start streaming
            goCoderBroadcaster.startBroadcast(broadcastConfig);
        }
    }

    @Override
    public void onWZStatus(final WZStatus goCoderStatus) {
        // A successful status transition has been reported by the GoCoder SDK
        final StringBuffer statusMessage = new StringBuffer("Broadcast status: ");

        switch (goCoderStatus.getState()) {
            case WZState.STARTING:
                statusMessage.append("Broadcast initialization");
                break;

            case WZState.READY:
                statusMessage.append("Ready to begin streaming");
                break;

            case WZState.RUNNING:
                statusMessage.append("Streaming is active");
                break;

            case WZState.STOPPING:
                statusMessage.append("Broadcast shutting down");
                break;

            case WZState.IDLE:
                statusMessage.append("The broadcast is stopped");
                break;

            default:
                return;
        }

        // Display the status message using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(StreamActivity.this, statusMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onWZError(final WZStatus goCoderStatus) {
        // If an error is reported by the GoCoder SDK, display a message
        // containing the error details using the U/I thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(StreamActivity.this,
                        "Streaming error: " + goCoderStatus.getLastError().getErrorDescription(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void initialise() {
        goCoder = WowzaGoCoder.init(getApplicationContext(),"GOSK-6643-0103-F6AA-8675-9F53");

        if (goCoder == null) {
            // If initialization failed, retrieve the last error and display it
            WZError goCoderInitError = WowzaGoCoder.getLastError();
            Log.d("Wowza GoCoder", goCoderInitError.getErrorDescription());
        } else {
            // Create a broadcaster instance
            goCoderBroadcaster = new WZBroadcast();

            //// Initialize the audio input device interface
            wzAudioDevice = new WZAudioDevice();

            // Create a configuration instance for the broadcaster
            broadcastConfig = new WZBroadcastConfig();
            broadcastConfig.setLogLevel(WZLog.LOG_LEVEL_DEBUG);
            broadcastConfig.setAudioBroadcaster(wzAudioDevice);
            broadcastConfig.setVideoEnabled(false);
            broadcastConfig.setAudioEnabled(true);

            // Set the address for the Wowza Streaming Engine server or Wowza Cloud
            //broadcastConfig.setHostAddress("f2ca87.entrypoint.cloud.wowza.com");
            broadcastConfig.setHostAddress("34.253.13.70");
            //broadcastConfig.setHostAddress("192.168.1.10");
            broadcastConfig.setPortNumber(1935);
            broadcastConfig.setApplicationName("app-4dae");
            // Set the name of the stream
            broadcastConfig.setStreamName("cd175e53");
        }
    }
}
