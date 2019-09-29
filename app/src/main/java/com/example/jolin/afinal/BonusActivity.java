package com.example.jolin.afinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class BonusActivity extends AppCompatActivity {
    private Button buster;
    private SoundEffectPlayer soundEffectPlayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bonus);

        /*----音效模組-----*/
        soundEffectPlayer = new SoundEffectPlayer( this ) ;

        /*----將變數綁向layout中物件----*/
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageButton buster = (ImageButton)findViewById(R.id.buster);
        buster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundEffectPlayer.play(R.raw.entersong);
                Intent intent = new Intent();
                Intent i = new Intent(getApplicationContext(),BusterActivity.class);
                startActivity(i);
            }
        });

        ImageButton quick = (ImageButton)findViewById(R.id.quick);
        quick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundEffectPlayer.play(R.raw.entersong);
                Intent intent = new Intent();
                Intent i = new Intent(getApplicationContext(),QuickActivity.class);
                startActivity(i);
            }
        });
    }

}

