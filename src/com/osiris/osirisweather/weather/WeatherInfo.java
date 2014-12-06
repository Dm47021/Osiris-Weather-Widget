package com.osiris.osirisweather.weather;

import android.os.Parcel;
import android.os.Parcelable;

public class WeatherInfo implements Parcelable
{
  private static final byte VERSION = 1;
  public int temp;
  public String humidity;
  public String wind;
  public String condition;
  public int icon;
  public long date;
  public String city;
  public WeatherForecast forecast[] = new WeatherForecast[4];

  public WeatherInfo()
  {
    super();
  }

  public WeatherInfo(String cityName)
  {
    super();
    city = cityName;
  }

  public int describeContents()
  {
    return 0;
  }

  public void writeToParcel(Parcel parcel, int i)
  {
    parcel.writeByte(VERSION);
    parcel.writeInt(temp);
    parcel.writeString(humidity);
    parcel.writeString(wind);
    parcel.writeString(condition);
    parcel.writeInt(icon);
    parcel.writeLong(date);
    parcel.writeString(city);
///////
    for(WeatherForecast forecast_info : forecast)
    {
      parcel.writeInt(forecast_info.temp_low);
      parcel.writeInt(forecast_info.temp_high);
      parcel.writeString(forecast_info.condition);
      parcel.writeLong(forecast_info.date);
      parcel.writeInt(forecast_info.icon);
    }
  }

  @SuppressWarnings("unused")
  public static final Parcelable.Creator<WeatherInfo> CREATOR = new Parcelable.Creator<WeatherInfo>()
  {
    public WeatherInfo createFromParcel(Parcel parcel)
    {
      if(parcel.readByte()!=VERSION)
        return null;
      ////
      WeatherInfo info = new WeatherInfo();
      info.temp = parcel.readInt();
      info.humidity = parcel.readString();
      info.wind = parcel.readString();
      info.condition = parcel.readString();
      info.icon = parcel.readInt();
      info.date = parcel.readLong();
      info.city = parcel.readString();
///////
      for(int index=0;index<info.forecast.length;++index)
      {
        info.forecast[index] = new WeatherForecast();
        info.forecast[index].temp_low = parcel.readInt();
        info.forecast[index].temp_high = parcel.readInt();
        info.forecast[index].condition = parcel.readString();
        info.forecast[index].date = parcel.readLong();
        info.forecast[index].icon = parcel.readInt();
      }
      return info;
    }

    public WeatherInfo[] newArray(int count)
    {
      return new WeatherInfo[count];
    }
  };

  public static class WeatherForecast
  {
    public int temp_low;
    public int temp_high;
    public String condition;
    public int icon;
    public long date;
  }
}
