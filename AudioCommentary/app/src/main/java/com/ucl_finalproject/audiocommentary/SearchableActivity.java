package com.ucl_finalproject.audiocommentary;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ucl_finalproject.audiocommentary.Retrofit.Database;
import com.ucl_finalproject.audiocommentary.Retrofit.ServiceGenerator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hoang on 10/01/2017.
 */

public class SearchableActivity extends ListActivity {

    private ProgressDialog pDialog;
    private final String API_BASE_URL = "http://audiocommentary.000webhostapp.com";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_list);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    private void doMySearch(String query) {
        pDialog.setMessage("Searching...");
        showDialog();

        Database databaseService = ServiceGenerator.getClient(API_BASE_URL).create(Database.class);
        Call<ResponseBody> response = databaseService.user_search(query);
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideDialog();

                try {
                    if(response.isSuccessful()) {
                        String result = response.body().string();
                        result = result.substring(result.indexOf('{'));
                        JSONObject jObj = new JSONObject(result);
                        boolean error = jObj.getBoolean("error");
                        // Check for error node in json
                        if (!error) {
                            final JSONArray names = jObj.getJSONArray("users");
                            String values[] = new String[names.length()];
                            for(int i = 0; i < names.length(); i++) {
                                values[i] = names.getJSONObject(i).getString("name");
                                Log.d("VS",values[i]);
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
                            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try {
                                        JSONObject mInfo = names.getJSONObject(position);
                                        Log.d("SearchableActivity",mInfo.toString());
                                        Intent intent = new Intent(getApplicationContext(),UserProfile.class);
                                        intent.putExtra("name",mInfo.getString("name"));
                                        intent.putExtra("team_support", mInfo.getString("team_support"));
                                        intent.putExtra("description", mInfo.getString("description"));
                                        intent.putExtra("user_id",mInfo.getString("user_id"));
                                        intent.putExtra("rating",mInfo.getString("rating"));
                                        startActivity(intent);
                                    } catch(Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        } else {
                            // Wrong request or users not found
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();

                            //TextView lv = (TextView) findViewById(android.R.id.empty);

                        }
                    }
                    else {
                        // Error in request. Get the error message
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
