package org.pvoid.timeandweatherwidget.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.pvoid.timeandweatherwidget.R;
import org.pvoid.timeandweatherwidget.Storage;
import org.pvoid.timeandweatherwidget.weather.GoogleWeatherRequest;
import org.pvoid.timeandweatherwidget.weather.WeatherInfo;

public class WidgetConfigureActivity extends Activity implements View.OnClickListener
{
  private int _mWidgetId;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
////////
    Intent intent = getIntent();
    _mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
    if(_mWidgetId==AppWidgetManager.INVALID_APPWIDGET_ID)
    {
      finish();
      return;
    }
////////
    setContentView(R.layout.widget_configure);
////////
    Spinner spinner = (Spinner)findViewById(R.id.weather_source);
    if(spinner!=null)
      spinner.setAdapter(new WeatherSourceAdapter(this));
////////
    View button = findViewById(R.id.add_city);
    if(button!=null)
      button.setOnClickListener(this);
  }

  public void onClick(View view)
  {
    TextView cityField = (TextView)findViewById(R.id.city_field);
    if(cityField==null)
      return;
////////
    final CharSequence city = cityField.getText();
    if(TextUtils.isEmpty(city))
    {
      Toast.makeText(this,R.string.need_city,Toast.LENGTH_SHORT).show();
      cityField.requestFocus();
      return;
    }
////////
    WeatherInfo weather = new WeatherInfo(city.toString());
    if(GoogleWeatherRequest.getWeather(weather))
    {
      Storage.storeWeatherInfo(this, weather, _mWidgetId);
      final RemoteViews views = WidgetProvider.getWeatherView(this, _mWidgetId, weather);
      final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
      appWidgetManager.updateAppWidget(_mWidgetId,views);
      ///// Tell WidgetManager that we are ready
      Intent result = new Intent();
      result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,_mWidgetId);
      setResult(RESULT_OK,result);
    }
    finish();
  }

  private static class WeatherSourceAdapter extends BaseAdapter
  {
    private static final String[] SOURCE = new String[] {"Google"};
    private final ContextWrapper _mContext;

    public WeatherSourceAdapter(Context context)
    {
      super();
      _mContext = new ContextWrapper(context);
    }

    public int getCount()
    {
      return SOURCE.length;
    }

    public Object getItem(int index)
    {
      return SOURCE[index];
    }

    public long getItemId(int index)
    {
      return index;
    }

    public View getView(int index, View view, ViewGroup viewGroup)
    {
      LayoutInflater inflater = (LayoutInflater) _mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      if(inflater==null)
        return null;
/////////
      if(view==null)
        view = inflater.inflate(android.R.layout.simple_spinner_item,viewGroup,false);
/////////
      TextView text = (TextView) view.findViewById(android.R.id.text1);
      if(text!=null)
        text.setText(SOURCE[index]);
/////////
      return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
      LayoutInflater inflater = (LayoutInflater) _mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      if(inflater==null)
        return null;
/////////
      if(convertView==null)
        convertView = inflater.inflate(android.R.layout.simple_spinner_dropdown_item,parent,false);
/////////
      TextView text = (TextView) convertView.findViewById(android.R.id.text1);
      if(text!=null)
        text.setText(SOURCE[position]);
/////////
      return convertView;
    }
  }
}
