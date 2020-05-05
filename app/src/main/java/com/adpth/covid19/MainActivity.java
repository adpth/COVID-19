package com.adpth.covid19;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.google.android.material.snackbar.Snackbar;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderLayout;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    TextView confir,recov,deat,countr;

    SliderLayout sliderLayout;

    ImageButton search_data;
    EditText country_search;

    TextView btn_see_details;

    ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //The banner content
        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.FILL);
        sliderLayout.setScrollTimeInSec(3);
        setSliderViews();

        //data which we have to get from REST API
        confir = findViewById(R.id.confirmed_no);
        countr = findViewById(R.id.country);
        recov = findViewById(R.id.revovered_no);
        deat = findViewById(R.id.deaths_no);
        search_data = findViewById(R.id.search_country);
        country_search = findViewById(R.id.your_country);

        btn_see_details = findViewById(R.id.btn_see_details);

        constraintLayout = findViewById(R.id.constraintLayout);

        //shift to the new activity
        btn_see_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Detail_Activity.class);
                startActivity(intent);
            }
        });

        //search activity begins
        search_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager ConnectionManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                assert ConnectionManager != null;
                NetworkInfo networkInfo = ConnectionManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected()) {
                    new searchData().execute();
                }
                else
                {
                    Snackbar snackbar = Snackbar.make(constraintLayout,"check your Internet connection", Snackbar.LENGTH_LONG);
                    snackbar.show();

                }
            }
        });
    }

    class searchData extends AsyncTask<String,Void,String> {

        String COUNTRY = country_search.getText().toString();

        @Override
        protected String doInBackground(String... strings) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("https://covid-19-data.p.rapidapi.com/country?format=json&name=" + COUNTRY)
                    .get()
                    .addHeader("x-rapidapi-host", "covid-19-data.p.rapidapi.com")
                    .addHeader("x-rapidapi-key", "a65ed4164bmshecc6a41b1453609p12d370jsn36dc92fffc6d")
                    .build();

            try {

                Response response = client.newCall(request).execute();
                return response.body().string();

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

                    countr.setText(country);

                    confir.setVisibility(View.VISIBLE);
                    confir.setText(confirmed);

                    recov.setVisibility(View.VISIBLE);
                    recov.setText(recovered);

                    deat.setVisibility(View.VISIBLE);
                    deat.setText(deaths);

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
            sliderLayout.addSliderView(sliderView);
        }
    }
}
