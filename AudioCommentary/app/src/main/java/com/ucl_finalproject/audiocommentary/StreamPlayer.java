package com.ucl_finalproject.audiocommentary;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.VideoView;

import com.longtailvideo.jwplayer.JWPlayerFragment;
import com.longtailvideo.jwplayer.JWPlayerView;
import com.longtailvideo.jwplayer.media.playlists.PlaylistItem;

import org.apache.http.conn.ssl.X509HostnameVerifier;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;


/**
 * Created by Hoang on 06/02/2017.
 */

public class StreamPlayer extends Activity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streamplayer);

        // Get a handle to the JWPlayerFragment
        JWPlayerFragment fragment = (JWPlayerFragment) getFragmentManager().findFragmentById(R.id.playerFragment);

        // Get a handle to the JWPlayerView
        JWPlayerView playerView = fragment.getPlayer();

        // Create a PlaylistItem
        PlaylistItem video = new PlaylistItem("http://af5261.entrypoint.cloud.wowza.com/app-4dae/ngrp:ea21a548_all/playlist.m3u8");
        //PlaylistItem video = new PlaylistItem("https://wowza.jwplayer.com/live/jelly.stream/playlist.m3u8");
        // Load a stream into the player

        playerView.load(video);

    }

}
