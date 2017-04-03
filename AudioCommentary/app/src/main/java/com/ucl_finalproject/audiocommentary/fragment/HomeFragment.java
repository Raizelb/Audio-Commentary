package com.ucl_finalproject.audiocommentary.fragment;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.ucl_finalproject.audiocommentary.R;

import java.nio.ByteBuffer;

/**
 * Created by Hoang on 28/12/2016.
 */

public class HomeFragment extends Fragment {

    private boolean streamingFlag = false;

    private AppCompatButton stream;
    private MediaCodec encoder;
    private AudioRecord audioRecord;
    private MediaCodec.BufferInfo bufferInfo;

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void stream() {
        final int bufferSize = AudioRecord.getMinBufferSize(44100,AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,44100, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT,bufferSize);
        if (getEncoder() & audioRecord.getState() != AudioRecord.STATE_UNINITIALIZED) {
            Thread streamThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        encoder.start();
                        byte[] inputBufferSize = new byte[bufferSize];
                        ByteBuffer inputBuffer = encoder.getInputBuffer(encoder.dequeueInputBuffer(-1));
                        ByteBuffer outputBuffer = encoder.getOutputBuffer(encoder.dequeueOutputBuffer(bufferInfo,-1));
                        audioRecord.startRecording();
                        audioRecord.read(inputBufferSize,0,inputBufferSize.length);
                        inputBuffer.put(inputBufferSize);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

    }

    private boolean getEncoder() {
        try {
            encoder = MediaCodec.createEncoderByType("audio/mp4a-latm");
            MediaFormat mediaFormat = new MediaFormat();
            mediaFormat.setString(MediaFormat.KEY_MIME,"audio/mp4a-latm");
            mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT,AudioFormat.CHANNEL_IN_MONO);
            mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE,44100);
            mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectHE);
            encoder.configure(mediaFormat,null,null,MediaCodec.CONFIGURE_FLAG_ENCODE);
            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
