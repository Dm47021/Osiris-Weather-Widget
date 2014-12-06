package com.osiris.osirisweather.weather;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;
import com.osiris.osirisweather.Storage;

import java.util.ArrayList;

public class WeatherRequestThread extends Thread
{
  private final Context            _mContext;
  private final ArrayList<Integer> _mWidgets = new ArrayList<Integer>();
  private PowerManager.WakeLock    _mWakeLock;

  public WeatherRequestThread(Context context)
  {
    super();
    _mContext = context;
  }

  public void add(int widget)
  {
    _mWidgets.add(widget);
  }

  @Override
  public void start()
  {
    PowerManager pm = (PowerManager)_mContext.getSystemService(Context.POWER_SERVICE);
    _mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Weather request wake lock");
    _mWakeLock.acquire();
    super.start();
  }

  @Override
  public void run()
  {
    Log.d("WEATHER","Request started");
    try
    {
      for(Integer id : _mWidgets)
      {
        WeatherInfo info = Storage.restoreWeatherInfo(_mContext,id);
        if(info==null)
          continue;
        if(GoogleWeatherRequest.getWeather(info))
          Storage.storeWeatherInfo(_mContext,info,id);
      }
      _mWidgets.clear();
    }
    finally
    {
      _mWakeLock.release();
    }
  }
}
