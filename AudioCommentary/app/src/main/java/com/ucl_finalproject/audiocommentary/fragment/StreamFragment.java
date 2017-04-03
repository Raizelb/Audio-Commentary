package com.ucl_finalproject.audiocommentary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ucl_finalproject.audiocommentary.MatchMakingActivity;
import com.ucl_finalproject.audiocommentary.R;
import com.ucl_finalproject.audiocommentary.StreamActivity;

/**
 * Created by Hoang on 23/01/2017.
 */

public class StreamFragment extends Fragment {

    private Button streamBtn;
    private View view;

    public StreamFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_stream, container, false);
        streamBtn = (Button) view.findViewById(R.id.stream_btn);

        streamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), StreamActivity.class));
            }
        });

        return view;
    }


}
