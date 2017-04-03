package com.ucl_finalproject.audiocommentary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ucl_finalproject.audiocommentary.Retrofit.Database;
import com.ucl_finalproject.audiocommentary.Retrofit.ServiceGenerator;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hoang on 01/04/2017.
 */

public class UserProfile extends Activity{

    private final String TAG = this.getClass().getSimpleName();
    private TextView mName, mTeam, mDescription;
    private RatingBar mRating;
    private Button mButton;
    private ProgressDialog pDialog;
    private final String API_BASE_URL = "http://audiocommentary.000webhostapp.com";
    private String mUserID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        mName = (TextView) findViewById(R.id.commentator_name);
        mTeam = (TextView) findViewById(R.id.commentator_team);
        mDescription = (TextView) findViewById(R.id.commentator_description);
        mRating = (RatingBar) findViewById(R.id.commentator_rating);
        mButton = (Button) findViewById(R.id.write_review);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        final Bundle extras = getIntent().getExtras();
        /*Log.d(TAG,extras.getString("name") + " " +
        extras.getString("team_support") + " " +
        extras.getString("description"));*/

        mName.setText(extras.getString("name"));
        mTeam.setText(extras.getString("team_support"));
        mDescription.setText(extras.getString("description"));
        mUserID = extras.getString("user_id");

        //mRating.setStepSize(0.01f);
        mRating.setRating(Float.valueOf(extras.get("rating").toString()));
        //mRating.invalidate();

        loadComments();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ReviewActivity.class);
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }

    private void loadComments() {
        pDialog.setMessage("Loading...");
        showDialog();

        Database databaseService = ServiceGenerator.getClient(API_BASE_URL).create(Database.class);
        Call<ResponseBody> response = databaseService.load_comments(mUserID);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideDialog();
                try {
                    if(response.isSuccessful()) {
                        JSONObject jObj = new JSONObject(response.body().string());
                        Log.d(TAG,jObj.toString());
                        boolean error = jObj.getBoolean("error");
                        if(!error) {
                            final JSONArray comments = jObj.getJSONArray("comments");
                            Log.d(TAG,comments.toString());
                            String values[] = new String[comments.length()];
                            for(int i = 0; i < comments.length(); i++) {
                                values[i] = comments.getJSONObject(i).getString("comment_content");
                                Log.d(TAG,values[i]);
                            }

                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                                    getApplicationContext(),
                                    R.layout.container_list_item_view,
                                    R.id.list_item,
                                    values
                            );

                            ListView lv = (ListView) findViewById(android.R.id.list);
                            lv.setAdapter(arrayAdapter);
                            lv.setEnabled(true);

                        } else {
                            // Wrong request or comments not found
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
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
