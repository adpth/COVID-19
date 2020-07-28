package com.adpth.covid19;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.adpth.covid19.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    AlertDialog dialog;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //The banner content
        binding.imageSlider.setIndicatorAnimation(IndicatorAnimations.FILL);
        binding.imageSlider.setScrollTimeInSec(3);
        setSliderViews();

        //shift to the new activity on Corona Symptoms
        binding.btnSeeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Detail_Activity.class);
                startActivity(intent);
            }
        });

        //Searching Task begins
        binding.searchCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(binding.yourCountry.getText().toString())) {
                    ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    assert ConnectionManager != null;
                    NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        new searchData().execute();
                    }
                    else
                    {
                        Snackbar snackbar = Snackbar.make(binding.constraintLayout,"check your Internet connection", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }
                else {
                    Snackbar snackbar = Snackbar.make(binding.constraintLayout,"Please check out the input or input is empty", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });
    }

    private class searchData extends AsyncTask<String,Void,String> {

        String COUNTRY = binding.yourCountry.getText().toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.CustomAlertDialog);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.loading_progress, viewGroup, false);
            builder.setView(dialogView);
            dialog = builder.create();
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://covid-19-data.p.rapidapi.com/country?format=json&name=" + COUNTRY)
                        .get()
                        .addHeader("x-rapidapi-host", "covid-19-data.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "a65ed4164bmshecc6a41b1453609p12d370jsn36dc92fffc6d")
                        .build();

                Response response = client.newCall(request).execute();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    return Objects.requireNonNull(response.body()).string();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONArray jsonarray = new JSONArray(s);
                if (jsonarray.length() > 0) {
                    JSONObject main = jsonarray.getJSONObject(Integer.parseInt("0"));
                    String country = main.getString("country");
                    String confirmed = main.getString("confirmed");
                    String recovered = main.getString("recovered");
                    String deaths = main.getString("deaths");

                    dialog.dismiss();

                    String value = getResources().getString(R.string.case_study)+country;
                    binding.country.setText(value);

                    binding.confirmedNo.setVisibility(View.VISIBLE);
                    binding.confirmedNo.setText(confirmed);

                    binding.revoveredNo.setVisibility(View.VISIBLE);
                    binding.revoveredNo.setText(recovered);

                    binding.deathsNo.setVisibility(View.VISIBLE);
                    binding.deathsNo.setText(deaths);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setSliderViews() {

        for (int i = 0; i < 3; i++) {

            DefaultSliderView sliderView = new DefaultSliderView(this);

            switch (i) {
                case 0:
                    sliderView.setImageDrawable(R.drawable.picture_01);
                    break;
                case 1:
                    sliderView.setImageDrawable(R.drawable.picture_02);
                    break;
                case 2:
                    sliderView.setImageDrawable(R.drawable.picture_03);
                    break;
            }
            sliderView.setImageScaleType(ImageView.ScaleType.FIT_CENTER);

            //at last add this view in your layout :
            binding.imageSlider.addSliderView(sliderView);
        }
    }
}
