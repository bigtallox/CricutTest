package com.brianmielke.cricuttext;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ForecastDetailActivity extends AppCompatActivity
{

    ImageView back;
    ImageView weatherIcon;
    TextView day;
    TextView highLow;
    TextView forcast;
    String urlPrefix;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        day = (TextView) findViewById(R.id.day);
        highLow = (TextView) findViewById(R.id.highLow);
        forcast = (TextView) findViewById(R.id.forecast);
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
        String urlPrefix = intent.getStringExtra(YahooWeatherActivity.URL_PREFIX);
        try
        {
            JSONObject forecastObject = new JSONObject(jsonString);

            day.setText(forecastObject.getString("day") + " " + forecastObject.getString("date"));
            highLow.setText("High " + forecastObject.getString("high") + "f / Low " + forecastObject.getString("low") + "f");
            forcast.setText(forecastObject.getString("text"));


            Picasso.with(this).load(urlPrefix + "/" + forecastObject.getString("code") + ".gif")
                    .into(weatherIcon);

        }
        catch (JSONException je)
        {
        }
    }
}
