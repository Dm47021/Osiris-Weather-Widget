package org.pvoid.timeandweatherwidget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.pvoid.timeandweatherwidget.weather.WeatherInfo;
import org.pvoid.timeandweatherwidget.widget.TimeAndWeatherWidgetProvider;

import java.util.Calendar;

public class WeatherActivity extends Activity
{
  public static final String WIDGET_ID = "widget_id";
  
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
////// Вытащим данные по погоде
    int widgetId = getIntent().getIntExtra(WIDGET_ID,-1);
    if(widgetId==-1)
    {

      return;
    }
//////
    final WeatherInfo info = Storage.restoreWeatherInfo(this,widgetId);
    if(info==null)
    {

      return;
    }
///////
    setContentView(R.layout.weather_activity);
/////// Текущая погода
    final StringBuilder builder = new StringBuilder();
    ImageView icon = (ImageView)findViewById(R.id.current_weather_icon);
    if(icon!=null)
      icon.setImageResource(info.icon);
    TextView text = (TextView)findViewById(R.id.current_temp);
    if(text!=null)
    {
      builder.append(info.temp).append("°");
      text.setText(builder.toString());
    }
    text = (TextView)findViewById(R.id.current_condition);
    if(text!=null)
      text.setText(info.condition);
    text = (TextView)findViewById(R.id.current_wind);
    if(text!=null)
      text.setText(info.wind);
    text = (TextView)findViewById(R.id.current_humidity);
    if(text!=null)
      text.setText(info.humidity);
///////
    Calendar calendar = Calendar.getInstance();
    for(int index=0,count=info.forecast.length;index<count;++index)
    {
      calendar.add(Calendar.DAY_OF_WEEK,1);
      setForecastInfo(calendar,info.forecast[index]);
    }
  }

  private void setForecastInfo(Calendar calendar, WeatherInfo.WeatherForecast forecast)
  {
    final LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    final ViewGroup container = (ViewGroup)findViewById(R.id.container);
    final StringBuilder builder = new StringBuilder();
    ViewGroup root = (ViewGroup)inflater.inflate(R.layout.weather_forecast_control, container, false);
///////
    TextView text = (TextView)root.findViewById(R.id.forecast_day);
    if(text!=null)
      text.setText(TimeAndWeatherWidgetProvider.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
    builder.append(", ").append(calendar.get(Calendar.DAY_OF_MONTH)).append(' ').append(getString(TimeAndWeatherWidgetProvider.getMonthName(calendar.get(Calendar.MONTH))));
    text = (TextView)root.findViewById(R.id.forecast_date);
    if(text!=null)
      text.setText(builder.toString());
    builder.setLength(0);
///////
    ImageView icon = (ImageView)root.findViewById(R.id.forecast_weather_icon);
    if(icon!=null)
      icon.setImageResource(forecast.icon);
    text = (TextView)root.findViewById(R.id.forecast_temp);
    if(text!=null)
    {
      builder.append(forecast.temp_low).append("°").append("/").append(forecast.temp_high).append("°");
      text.setText(builder.toString());
    }
    text = (TextView)root.findViewById(R.id.forecast_condition);
    if(text!=null)
      text.setText(forecast.condition);
///////
    container.addView(root);
  }
}