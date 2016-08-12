package com.brianmielke.cricuttext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class ForecastAdapter extends BaseAdapter
{
    final private JSONArray items;
    final private LayoutInflater mInflater;
    final private Context context;
    final private String urlImagePrefix;

    public ForecastAdapter(Context context, JSONArray items, String urlImagePrefix)
    {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.items = items;
        this.urlImagePrefix = urlImagePrefix;
    }

    @Override
    public int getCount()
    {
        return items.length();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        final ViewHolder holder;

        if(convertView ==null)
        {
            convertView = mInflater.inflate(R.layout.forecast_row_item,parent,false);
            holder = new ViewHolder();
            holder.day = (TextView) convertView.findViewById(R.id.day);
            holder.highLow = (TextView) convertView.findViewById(R.id.highLow);
            holder.forecast = (TextView) convertView.findViewById(R.id.forecast);
            //holder.container = (LinearLayout) convertView.findViewById(R.id.container);
            convertView.setTag(holder);
            holder.button = (ImageView) convertView.findViewById(R.id.upButton);
            holder.weatherIcon = (ImageView) convertView.findViewById(R.id.weatherIcon);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        try
        {
            final JSONObject obj = items.getJSONObject(position);

            holder.day.setText(context.getString(R.string.string_string_format,
                    obj.getString("day"), obj.getString("date")) );

            holder.highLow.setText(obj.getString("high") + "f / " + obj.getString("low") + "f");
            holder.forecast.setText(obj.getString("text"));

            //String imageUrl = urlImagePrefix + "/" + obj.getString("code") + ".gif";
            Picasso.with(YahooWeatherActivity.instance)
                    .load(urlImagePrefix + "/" + obj.getString("code") + ".gif")
                    .resize(100,100)
                    .into(holder.weatherIcon);

            final Context contextFinal = context;
            holder.button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(contextFinal, ForecastDetailActivity.class);
                    intent.putExtra(YahooWeatherActivity.EXTRA_MESSAGE, obj.toString());
                    intent.putExtra(YahooWeatherActivity.URL_PREFIX,urlImagePrefix);
                    contextFinal.startActivity(intent);
                    ((Activity)contextFinal).overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                }
            });
        }
        catch(Exception e)
        {
            Log.d("cricut", "exception " + e);
        }

        return convertView;
    }


    static class ViewHolder
    {
        TextView highLow;
        TextView day;
        TextView forecast;
        //LinearLayout container;
        ImageView button;
        ImageView weatherIcon;
    }
}