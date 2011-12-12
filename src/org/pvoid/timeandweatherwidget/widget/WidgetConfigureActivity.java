package org.pvoid.timeandweatherwidget.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;
import org.pvoid.timeandweatherwidget.Storage;
import org.pvoid.timeandweatherwidget.weather.WeatherInfo;
import org.pvoid.timeandweatherwidget.weather.WeatherRequest;

public class WidgetConfigureActivity extends Activity
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
    WeatherInfo weather = new WeatherInfo("kazan");
    if(WeatherRequest.getWeather(weather))
    {
      Storage.storeWeatherInfo(this, weather,_mWidgetId);
      final RemoteViews views = TimeAndWeatherWidgetProvider.getWeatherView(this,weather);
      final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
      appWidgetManager.updateAppWidget(_mWidgetId,views);
      ///// Tell WidgetManager that we are ready
      Intent result = new Intent();
      result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,_mWidgetId);
      setResult(RESULT_OK,result);
    }
    finish();
  }
}
