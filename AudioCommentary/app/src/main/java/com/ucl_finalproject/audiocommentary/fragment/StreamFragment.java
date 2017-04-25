package com.ucl_finalproject.audiocommentary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ucl_finalproject.audiocommentary.MatchMakingActivity;
import com.ucl_finalproject.audiocommentary.R;
import com.ucl_finalproject.audiocommentary.StreamActivity;
import com.ucl_finalproject.audiocommentary.helper.SQLiteHandler;

/**
 * Created by Hoang on 23/01/2017.
 */

public class StreamFragment extends Fragment {

    private Button streamBtn;
    private View view;
    private SQLiteHandler sqLiteHandler;

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

        sqLiteHandler = new SQLiteHandler(getContext());
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_stream, container, false);
        streamBtn = (Button) view.findViewById(R.id.stream_btn);

        streamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.parseInt(sqLiteHandler.getUserDetails().get("commentator")) == 1) {
                    startActivity(new Intent(getActivity().getApplicationContext(), MatchMakingActivity.class));
                }
                else {
                    Toast.makeText(getContext(), "You are not a commentator", Toast.LENGTH_LONG).show();
                }

            }
        });

        return view;
    }


}