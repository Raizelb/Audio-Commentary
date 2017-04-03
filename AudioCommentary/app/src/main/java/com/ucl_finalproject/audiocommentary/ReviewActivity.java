package com.ucl_finalproject.audiocommentary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.ucl_finalproject.audiocommentary.Retrofit.Database;
import com.ucl_finalproject.audiocommentary.Retrofit.ServiceGenerator;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hoang on 01/04/2017.
 */

public class ReviewActivity extends Activity {

    private final String API_BASE_URL = "http://audiocommentary.000webhostapp.com";
    private final String TAG = this.getClass().getSimpleName();
    private RatingBar mRatingBar;
    private TextInputEditText mReview;
    private Button mButton;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        final Bundle extras = getIntent().getExtras();
        Log.d(TAG,extras.getString("name"));
        final String mUserID = extras.getString("user_id");

        mRatingBar = (RatingBar) findViewById(R.id.review_rating);

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Log.d(TAG,Float.toString(rating));
            }
        });

        mReview = (TextInputEditText) findViewById(R.id.comment_content);

        mButton = (Button) findViewById(R.id.submit_review);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mContent = mReview.getText().toString();
                String mRating = Float.toString(mRatingBar.getRating());
                submitReview(mUserID, mContent, mRating, extras);
            }
        });

    }

    private void submitReview(String mUserID, String mContent, String mRating,final Bundle extras) {
        pDialog.setMessage("Submitting...");
        showDialog();

        Database databaseService = ServiceGenerator.getClient(API_BASE_URL).create(Database.class);
        final Call<ResponseBody> response = databaseService.submit_comment(mUserID, mContent, mRating);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideDialog();
                try {
                    if(response.isSuccessful()) {
                        JSONObject jObj = new JSONObject(response.body().string());
                        boolean error = jObj.getBoolean("error");
                        if(!error) {
                            finish();
                        } else {
                            // Error in submitting comment. Get the error message
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        String errorMsg = response.message();
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideDialog();
            }
        });

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
