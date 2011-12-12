package org.pvoid.timeandweatherwidget.weather;

import android.net.Uri;
import android.util.Log;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.pvoid.timeandweatherwidget.R;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class WeatherRequest
{
  private final static String GOOGLE_WEATHER_API = "http://www.google.com/ig/api";

  private static final SAXParserFactory _sSAXFactory = SAXParserFactory.newInstance();

  public static boolean getWeather(WeatherInfo weather)
  {
    final HttpClient client = new DefaultHttpClient();
    try
    {
      Uri uri = Uri.parse(GOOGLE_WEATHER_API);
      uri = uri.buildUpon().appendQueryParameter("weather",weather.city)
                     .appendQueryParameter("oe","utf8")
                     .appendQueryParameter("hl","ru").build();
      final HttpGet get = new HttpGet(uri.toString());
      /////
      final ResponseHandler<String> handler = new BasicResponseHandler();
      try
      {
        final String body = client.execute(get,handler);
        ////
        Log.d("WEATHER", body);
        ////
        final ResponseParser parser = new ResponseParser(weather);
        try
        {
          SAXParser saxParser = _sSAXFactory.newSAXParser();
          InputSource source = new InputSource();
          ByteArrayInputStream stream;
          try
          {
            stream = new ByteArrayInputStream(body.getBytes("UTF-8"));
          }
          catch (UnsupportedEncodingException e)
          {
            e.printStackTrace();
            return false;
          }
          source.setByteStream(stream);
          source.setEncoding("UTF-8");
          saxParser.parse(source,parser);
          ////////
          return true;
        }
        catch(ParserConfigurationException e)
        {
          e.printStackTrace();
        }
        catch(SAXException e)
        {
          e.printStackTrace();
        }
        catch(IOException e)
        {
          e.printStackTrace();
        }
      }
      catch(IOException e)
      {
        e.printStackTrace();
        return false;
      }

      return true;
    }
    finally
    {
      client.getConnectionManager().shutdown();
    }
  }

  private static class ResponseParser extends DefaultHandler
  {
    private final WeatherInfo _mWeatherInfo;
    private int _mForecastIndex;

    public ResponseParser(WeatherInfo info)
    {
      super();
      _mWeatherInfo = info;
    }

    @Override
    public void startDocument() throws SAXException
    {
      _mForecastIndex = -1;
    }

    @Override
    public void endDocument() throws SAXException
    {
      _mWeatherInfo.date = System.currentTimeMillis();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException
    {
      if("forecast_conditions".equals(localName))
      {
        ++_mForecastIndex;
        if(_mForecastIndex<_mWeatherInfo.forecast.length)
          _mWeatherInfo.forecast[_mForecastIndex] = new WeatherInfo.WeatherForecast();
        return;
      }
      ////
      if("temp_c".equals(localName))
      {
        if(_mForecastIndex==-1)
          _mWeatherInfo.temp = Integer.parseInt(attributes.getValue("data"));
        return;
      }
      ////
      if("low".equals(localName) && _mForecastIndex<_mWeatherInfo.forecast.length)
        _mWeatherInfo.forecast[_mForecastIndex].temp_low = Integer.parseInt(attributes.getValue("data"));
      ////
      if("high".equals(localName) && _mForecastIndex<_mWeatherInfo.forecast.length)
        _mWeatherInfo.forecast[_mForecastIndex].temp_high = Integer.parseInt(attributes.getValue("data"));
      ////
      if("humidity".equals(localName))
      {
        if(_mForecastIndex==-1)
        {
          _mWeatherInfo.humidity = attributes.getValue("data");
          return;
        }
      }
      ////
      if("wind_condition".equals(localName))
      {
        if(_mForecastIndex==-1)
        {
          _mWeatherInfo.wind = attributes.getValue("data");
          return;
        }
      }
      ////
      if("condition".equals(localName))
      {
        if(_mForecastIndex==-1)
          _mWeatherInfo.condition = attributes.getValue("data");
        else if(_mForecastIndex<_mWeatherInfo.forecast.length)
          _mWeatherInfo.forecast[_mForecastIndex].condition = attributes.getValue("data");
        return;
      }
      ////
      if("icon".equals(localName))
      {
        if(_mForecastIndex==-1)
          _mWeatherInfo.icon = getIcon(attributes.getValue("data"));
        else if(_mForecastIndex<_mWeatherInfo.forecast.length)
          _mWeatherInfo.forecast[_mForecastIndex].icon = getIcon(attributes.getValue("data"));
        //noinspection UnnecessaryReturnStatement
        return;
      }
    }

    private static int getIcon(String url)
    {
      if(url.length()<23)
        return R.drawable.ic_unknown;
      String fileName = url.substring(19,url.length()-4);

      if("chance_of_rain".equals(fileName))
        return R.drawable.ic_chance_of_rain;
      if("chance_of_snow".equals(fileName))
        return R.drawable.ic_chance_of_snow;
      if("chance_of_storm".equals(fileName))
        return R.drawable.ic_chance_of_storm;
      if("mist".equals(fileName))
        return R.drawable.ic_mist;
      if("mostly_cloudy".equals(fileName))
        return R.drawable.ic_mostly_cloudy;
      if("mostly_sunny".equals(fileName))
        return R.drawable.ic_mostly_sunny;
      if("rain".equals(fileName))
        return R.drawable.ic_rain;
      if("sleet".equals(fileName))
        return R.drawable.ic_sleet;
      if("snow".equals(fileName))
        return R.drawable.ic_snow;
      if("sunny".equals(fileName))
        return R.drawable.ic_sunny;
      if("thunderstorm".equals(fileName))
        return R.drawable.ic_thunderstorm;
      if("flurries".equals(fileName))
        return R.drawable.ic_chance_of_snow;
      if("cloudy".equals(fileName))
        return R.drawable.ic_cloudy;

      Log.d("WEATHER","Image file name: " + fileName);

      return R.drawable.ic_unknown;
    }
  }
}
