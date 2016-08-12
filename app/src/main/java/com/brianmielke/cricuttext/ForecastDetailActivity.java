package com.brianmielke.cricuttext;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ForecastDetailActivity extends AppCompatActivity
{
    protected ImageView back;
    protected ImageView weatherIcon;
    protected TextView day;
    protected TextView highLow;
    protected TextView forecast;
    protected String urlPrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        day = (TextView) findViewById(R.id.day);
        highLow = (TextView) findViewById(R.id.highLow);
        forecast = (TextView) findViewById(R.id.forecast);
        back = (ImageView) findViewById(R.id.back);
        weatherIcon = (ImageView) findViewById(R.id.weatherIcon);

        back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                finish();
                overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
            }
        });


        Intent intent = getIntent();
        String jsonString = intent.getStringExtra(YahooWeatherActivity.EXTRA_MESSAGE);
        urlPrefix = intent.getStringExtra(YahooWeatherActivity.URL_PREFIX);
        try
        {
            JSONObject forecastObject = new JSONObject(jsonString);
            day.setText(getString(R.string.string_string_format, forecastObject.getString("day"), forecastObject.getString("date")) );
            highLow.setText(  getResources().getString(R.string.high_label) + " " + forecastObject.getString("high") + "f / " + getResources().getString(R.string.low_label) + " " + forecastObject.getString("low") + "f");
            forecast.setText(forecastObject.getString("text"));

            Picasso.with(this).load(urlPrefix + "/" + forecastObject.getString("code") + ".gif")
                    .into(weatherIcon);

        }
        catch (JSONException je)
        {
            Log.d("cricut", "json exception = " + je.toString());
        }
    }
}
