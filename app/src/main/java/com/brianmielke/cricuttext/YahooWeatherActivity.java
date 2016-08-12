package com.brianmielke.cricuttext;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YahooWeatherActivity extends AppCompatActivity
{
    static YahooWeatherActivity instance;
    private ListView listView;
    private TextView description;
    private TextView conditions;
    private TextView conditions2;
    private ImageView background;

    public final static String EXTRA_MESSAGE = "com.brianmielke.message";
    public final static String URL_PREFIX = "com.brianmielke.urlprefix";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        listView = (ListView) findViewById(R.id.listView);
        description = (TextView) findViewById(R.id.description);
        conditions = (TextView) findViewById(R.id.conditions);
        conditions2 = (TextView) findViewById(R.id.conditions2);
        background = (ImageView) findViewById(R.id.background);

        final ProgressDialog pd= new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        HttpRequest task = new HttpRequest("https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20where%20text%3D%22nome%2C%20ak%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys ", null, "GET", new HttpRequest.HttpTaskHandler()
        {
            @Override
            public void requestSuccessful(String jsonString)
            {
                pd.dismiss();
                try
                {
                    JSONObject channel = new JSONObject(jsonString).getJSONObject("query").getJSONObject("results").getJSONObject("channel");
                    JSONArray forecast = channel.getJSONObject("item").getJSONArray("forecast");
                    JSONObject condition = channel.getJSONObject("item").getJSONObject("condition");
                    JSONObject atmosphere = channel.getJSONObject("atmosphere");

                    // get the url to the images for the detail view...
                    String imageLink = channel.getJSONObject("item").getString("description");
                    String pattern =  "<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>";
                    Pattern r = Pattern.compile(pattern);
                    Matcher m = r.matcher(imageLink);
                    String urlImagePrefix = "";
                    if (m.find( ))
                    {
                        String found = m.group(0);
                        urlImagePrefix =  found.substring(found.indexOf("\""), found.lastIndexOf("/"));
                        urlImagePrefix =  urlImagePrefix.substring(1, urlImagePrefix.lastIndexOf("/"));
                    }
                    else
                    {
                        urlImagePrefix = "http://l.yimg.com/a/i/us/we/52";
                    }


                    description.setText(channel.getString("description"));
                    conditions.setText(condition.getString("temp") + "f " + condition.getString("text"));
                    conditions2.setText(condition.getString("date"));
                    ForecastAdapter adapter = new ForecastAdapter(YahooWeatherActivity.this,forecast,urlImagePrefix);
                    listView.setAdapter(adapter);
                }
                catch (JSONException je)
                {

                }
            }

            @Override
            public void requestFailed(Exception e)
            {
                pd.dismiss();
            }
        });

        task.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
