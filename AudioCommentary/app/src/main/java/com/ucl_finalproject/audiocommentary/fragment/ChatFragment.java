package com.ucl_finalproject.audiocommentary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ucl_finalproject.audiocommentary.ChatActivity;
import com.ucl_finalproject.audiocommentary.R;

/**
 * Created by Hoang on 15/03/2017.
 */

public class ChatFragment extends Fragment {

    private Button chatBtn;
    private View view;

    public ChatFragment() {
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
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        chatBtn = (Button) view.findViewById(R.id.chat_btn);

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity().getApplicationContext(), ChatActivity.class));
            }
        });

        return view;
    }
}
