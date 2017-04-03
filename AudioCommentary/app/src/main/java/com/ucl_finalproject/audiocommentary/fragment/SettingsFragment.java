package com.ucl_finalproject.audiocommentary.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ucl_finalproject.audiocommentary.CommentatorSignUpActivitiy;
import com.ucl_finalproject.audiocommentary.R;
import com.ucl_finalproject.audiocommentary.helper.SQLiteHandler;

import java.util.Iterator;

/**
 * Created by Hoang on 28/12/2016.
 */

public class SettingsFragment extends Fragment {

    private SQLiteHandler sqLiteHandler;
    private TextView mTextView;
    private Button mButton;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mTextView = (TextView) view.findViewById(R.id.user_commentator_status);
        mButton = (Button) view.findViewById(R.id.commentator_signup);

        sqLiteHandler = new SQLiteHandler(getContext());
        Log.d("SettingFragment",sqLiteHandler.getUserDetails().toString());
        if(Integer.parseInt(sqLiteHandler.getUserDetails().get("commentator")) == 0) {
            mButton.setVisibility(View.VISIBLE);
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), CommentatorSignUpActivitiy.class));
                }
            });
        }
        else {
            mTextView.setVisibility(View.VISIBLE);
        }
        return view;
    }
}
