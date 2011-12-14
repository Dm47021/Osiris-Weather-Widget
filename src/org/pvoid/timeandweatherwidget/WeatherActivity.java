package org.pvoid.timeandweatherwidget;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.pvoid.timeandweatherwidget.weather.WeatherInfo;
import org.pvoid.timeandweatherwidget.widget.WidgetProvider;

import java.util.Calendar;

public class WeatherActivity extends Activity
{
  public static final String WIDGET_ID = "widget_id";
  private long _mUpdateDate;

  private final Runnable _mUpdateTimeSpan = new Runnable()
  {
    public void run()
    {
      TextView text = (TextView) findViewById(R.id.refresh_time);
      if(text!=null)
      {
        StringBuilder builder = new StringBuilder(", ");
        builder.append(getString(R.string.updated)).append(": ")
            .append(DateUtils.getRelativeTimeSpanString(_mUpdateDate, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
        text.setText(builder.toString());
      }
      _mHandler.postDelayed(_mUpdateTimeSpan,1000);
    }
  };
  private final Handler _mHandler = new Handler();

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
    final StringBuilder builder = new StringBuilder();
/////// Город и время обновления
    TextView text = (TextView)findViewById(R.id.city);
    if(text!=null)
      text.setText(capitalize(info.city));
///////
    _mUpdateDate = info.date;
    text = (TextView) findViewById(R.id.refresh_time);
    if(text!=null)
    {
      builder.append(", ").append(getString(R.string.updated)).append(": ")
             .append(DateUtils.getRelativeTimeSpanString(info.date, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS));
      text.setText(builder.toString());
      builder.setLength(0);
    }
/////// Установим таймер обновления
    _mHandler.postDelayed(_mUpdateTimeSpan,1000);
/////// Текущая погода
    ImageView icon = (ImageView)findViewById(R.id.current_weather_icon);
    if(icon!=null)
      icon.setImageResource(WidgetProvider.getWeatherIcon(info.icon));
    text = (TextView)findViewById(R.id.current_temp);
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

  @Override
  protected void onDestroy()
  {
    _mHandler.removeCallbacks(_mUpdateTimeSpan);
    super.onDestroy();
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
      text.setText(WidgetProvider.getDayOfWeek(calendar.get(Calendar.DAY_OF_WEEK)));
    builder.append(", ").append(calendar.get(Calendar.DAY_OF_MONTH)).append(' ').append(getString(WidgetProvider.getMonthName(calendar.get(Calendar.MONTH))));
    text = (TextView)root.findViewById(R.id.forecast_date);
    if(text!=null)
      text.setText(builder.toString());
    builder.setLength(0);
///////
    ImageView icon = (ImageView)root.findViewById(R.id.forecast_weather_icon);
    if(icon!=null)
      icon.setImageResource(forecast.icon);
    text = (TextView)root.findViewById(R.id.forecast_temp_min);
    if(text!=null)
    {
      builder.append(forecast.temp_low).append("°");
      text.setText(builder.toString());
      builder.setLength(0);
    }
    text = (TextView)root.findViewById(R.id.forecast_temp_max);
    if(text!=null)
    {
      builder.append(forecast.temp_high).append("°");
      text.setText(builder.toString());
      builder.setLength(0);
    }
    text = (TextView)root.findViewById(R.id.forecast_condition);
    if(text!=null)
      text.setText(forecast.condition);
///////
    container.addView(root);
  }
  
  private String capitalize(String text)
  {
    StringBuilder str = new StringBuilder();
    char chars[] = text.toCharArray();
    boolean space = true;
///////
    for(int index=0, count = chars.length; index<count; ++index)
    {
      if(chars[index]==' ' || chars[index]=='-')
      {
        space = true;
        str.append(chars[index]);
      }
      else if(space)
      {
        str.append(Character.toUpperCase(chars[index]));
        space = false;
      }
      else
        str.append(chars[index]);
    }
///////
    return str.toString();
  }
}