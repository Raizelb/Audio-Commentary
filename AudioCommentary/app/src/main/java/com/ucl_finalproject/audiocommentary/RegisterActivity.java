package com.ucl_finalproject.audiocommentary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.Toast;

import com.ucl_finalproject.audiocommentary.Retrofit.Database;
import com.ucl_finalproject.audiocommentary.Retrofit.ServiceGenerator;
import com.ucl_finalproject.audiocommentary.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hoang on 12/12/2016.
 */

public class RegisterActivity extends Activity{
    private final String API_BASE_URL = "http://audiocommentary.000webhostapp.com";

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private AppCompatButton btnRegister, btnLinkToLogin;
    private TextInputEditText inputFullName, inputEmail, inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (TextInputEditText) findViewById(R.id.register_name);
        inputEmail = (TextInputEditText) findViewById(R.id.register_email);
        inputPassword = (TextInputEditText) findViewById(R.id.register_password);
        btnRegister = (AppCompatButton) findViewById(R.id.btn_signup);
        btnLinkToLogin = (AppCompatButton) findViewById(R.id.link_login);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                    registerUser(name, email, password);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        findViewById(R.id.link_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(),LoginActivity.class));
            }
        });
    }

    private void registerUser(final String name, final String email,
                              final String password) {
        pDialog.setMessage("Registering ...");
        showDialog();
        Database databaseService = ServiceGenerator.getClient(API_BASE_URL).create(Database.class);
        Call<ResponseBody> response = databaseService.register(email,password,name);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideDialog();
                try {
                    String result = response.body().string();
                    result = result.substring(result.indexOf('{'));
                    JSONObject jObj = new JSONObject(result);
                    boolean error = jObj.getBoolean("error");
                    if(!error) {
                        // Launch login activity
                        Intent intent = new Intent(RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                    // Check for error node in json
                } catch(JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch(IOException e) {
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
