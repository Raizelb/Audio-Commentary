package com.ucl_finalproject.audiocommentary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
 * Created by Hoang on 12/12/2016.
 */

public class LoginActivity extends Activity {

    private final String API_BASE_URL = "http://audiocommentary.000webhostapp.com";

    //private int dataBaseVersion = 1;
    private TextInputEditText inputEmail, inputPassword;
    private static final String TAG = LoginActivity.class.getSimpleName();
    private Button btnLogin, btnRegister;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (TextInputEditText) findViewById(R.id.login_email);
        inputPassword = (TextInputEditText) findViewById(R.id.login_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnRegister = (Button) findViewById(R.id.link_signup);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());
        //db.onUpgrade(db.getWritableDatabase(),1,1);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!email.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        btnRegister.setOnClickListener(new TextView.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                finish();
            }
        });
    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String email, final String password) {
        pDialog.setMessage("Logging in ...");
        showDialog();

        Database databaseService = ServiceGenerator.getClient(API_BASE_URL).create(Database.class);
        Call<ResponseBody> response = databaseService.login(email,password);
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

                            // user successfully logged in
                            // Create login session
                            session.setLogin(true);

                            // Now store the user in SQLite
                            String uid = jObj.getString("uid");

                            JSONObject user = jObj.getJSONObject("user");
                            String name = user.getString("name");
                            String email = user.getString("email");
                            int commentator = user.getInt("commentator");
                            String created_at = user
                                    .getString("created_at");

                            // Inserting row in users table
                            db.addUser(name, email, commentator, uid, created_at);

                            // Launch main activity
                            Intent intent = new Intent(LoginActivity.this,
                                    MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Error in login. Get the error message
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        String errorMsg = response.message();
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
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
                Log.e(TAG, "Login Error: " + t.getMessage());
                Toast.makeText(getApplicationContext(),
                        t.getMessage(), Toast.LENGTH_LONG).show();
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
