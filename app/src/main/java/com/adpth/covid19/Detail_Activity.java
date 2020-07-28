package com.adpth.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Detail_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Symptoms");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
