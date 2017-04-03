package com.ucl_finalproject.audiocommentary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.ucl_finalproject.audiocommentary.Retrofit.Database;
import com.ucl_finalproject.audiocommentary.Retrofit.ServiceGenerator;
import com.ucl_finalproject.audiocommentary.helper.SQLiteHandler;
import com.ucl_finalproject.audiocommentary.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hoang on 27/02/2017.
 */

public class CommentatorSignUpActivitiy extends Activity {

    private final String API_BASE_URL = "http://audiocommentary.000webhostapp.com";

    private ProgressDialog pDialog;
    private Button mButton;
    private EditText mEditText;
    private Spinner mSpinner;
    private SQLiteHandler db;
    private SessionManager session;

    private String team, email, description;

    @Override
    public void onCreate(Bundle onSavedInstanceState) {
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.activity_commentator_signup);

        db = new SQLiteHandler(getApplicationContext());
        email = db.getUserDetails().get("email");

        session = new SessionManager(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        mEditText = (EditText) findViewById(R.id.commentator_description);
        mButton = (Button) findViewById(R.id.commentator_submit);
        mSpinner = (Spinner) findViewById(R.id.supported_team);

        // Create an adapter from the string array resource and use
        // android's inbuilt layout file simple_spinner_item
        // that represents the default spinner in the UI
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.premier_league, android.R.layout.simple_spinner_item);
        // Set the layout to use for each dropdown item
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] teams = getResources().getStringArray(R.array.premier_league);
                team = teams[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                description = mEditText.getText().toString().trim();
                if(!description.isEmpty()) {
                    registerCommentator(email,team,description);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please Enter a short description of yourself",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void registerCommentator(String email, String team, String description) {
        pDialog.setMessage("Logging in ...");
        showDialog();

        Database databaseService = ServiceGenerator.getClient(API_BASE_URL).create(Database.class);
        Call<ResponseBody> response = databaseService.register_commentator(email, team, description);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideDialog();
                try {
                    if(response.isSuccessful()) {
                        JSONObject jObj = new JSONObject(response.body().string());
                        boolean error = jObj.getBoolean("error");
                        // Check for error node in json
                        if (!error) {
                            session.setLogin(false);

                            db.deleteUsers();

                            //Log user out if successful
                            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                            finish();
                        }
                        else {
                            // Error in login. Get the error message
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "IOException: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

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
