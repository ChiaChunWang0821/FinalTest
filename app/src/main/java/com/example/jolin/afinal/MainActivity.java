package com.example.jolin.afinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Client client;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private SoundEffectPlayer soundEffectPlayer ;
    private ViewPager mSliderViewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*----音效模組-----*/
        soundEffectPlayer = new SoundEffectPlayer( this ) ;

        /*----將變數綁向layout中物件----*/
        Toolbar toolbar = findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        /*----左側選單----*/
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*----左側選單頁面----*/
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*----滑動頁面----*/
        mSliderViewPager = (ViewPager) findViewById(R.id.sliderViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        sliderAdapter = new SliderAdapter(this);
        mSliderViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSliderViewPager.addOnPageChangeListener(viewListener);

    }

    public void addDotsIndicator(int position){
        mDots = new TextView[2];
        mDotLayout.removeAllViews();
        for(int i=0; i<mDots.length; i++){
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            mDotLayout.addView(mDots[i]);
        }
        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /*----創建選單----*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*----選單點擊事件----*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch(item.getItemId()){
            case R.id.bluetooth:
                Intent bluetooth = new Intent(this, BluetoothActivity.class);
                startActivity(bluetooth);
                break;
            case R.id.help:
                Intent help = new Intent(this, HelpActivity.class);
                startActivity(help);
                break;
            case R.id.update:
                Toast.makeText(getApplicationContext(), "Latest version installed!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    /*----設定左側清單點擊事件----*/
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //左側清單
        int id = item.getItemId();
        if(id == R.id.start_game){
            soundEffectPlayer.play(R.raw.entersong);
            Intent start_game = new Intent(this, GameActivity.class);
            startActivity(start_game);
        }
        else if(id == R.id.challenge_mode){
            soundEffectPlayer.play(R.raw.entersong);
            Intent challenge_mode = new Intent(this, BonusActivity.class);
            startActivity(challenge_mode);
        }
        return false;
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
