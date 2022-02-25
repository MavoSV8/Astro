package zdalne.zadania.astroweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private ScreenSlideAdapter screenSlideAdapter;
    private ViewPager2 viewPager;

    private String longitude;
    private String latitude;
    private int refresh;
    private Day day;
    private Night night;
    private Weather weather;
    private Forecast forecast;
    private final String APIkey = "7bb076112e94bd89b1342fbd3cdbfbb2";

    private String settingsString;
    private String city;
    private String unit;
    private String weatherJsonString;
    private String forecastJsonString;
    private JSONObject settingsJSON;

    private void setDefaultLocation(){

        longitude = "51.74";
        latitude = "19.45";

    }

    private void setDefaultRefresh(){
        refresh = 15000;

    }

    public boolean checkConnection(){

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()){
            return true;
        }
        else{
            return false;
        }
    }

    public void getDataFromString(String JSONString) throws JSONException {
        settingsJSON = new JSONObject(JSONString);
        city = settingsJSON.getString("selected");
        unit = settingsJSON.getString("unit");
    }


    public void forceUpdateWeather(String city, String unit){

        this.city = city;
        this.unit = unit;
        weather.forceUpdateWeatherFragment(city,unit);

    }

    public String createSettingsJSON(String selectedCity, String unit, ArrayList<String> favourites){
            JSONObject json = new JSONObject();
        try {
            json.put("selected",selectedCity);
            json.put("unit",unit);
            JSONArray jsonArray = new JSONArray(favourites);
            json.put("favourites",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }


    public String readFromFile(String fileName){

        try {
            String strToReadTo;
            InputStream fileInput = openFileInput(fileName);
            strToReadTo = new BufferedReader(
                    new InputStreamReader(fileInput, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            fileInput.close();
            System.out.println("file read");
            return strToReadTo;
        } catch (Exception e) {
            System.out.println("Couldn't read file.");
            return null;
        }

    }

    public void writeToFile(String stringToWrite, String fileName){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(stringToWrite);
            outputStreamWriter.close();
            System.out.println("file written");
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e);
        }
    }

    public String getWeather(String city, String unit) throws IOException {
        this.city = city;
        this.unit = unit;
        try {
            if (checkConnection()) {
                URL request = new URL("https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=" + unit + "&lang=en&appid=" + APIkey);
                HttpURLConnection apiConnection = (HttpURLConnection) request.openConnection();
                apiConnection.setRequestMethod("POST");
                apiConnection.connect();

                if (apiConnection.getResponseCode() == 200) {

                    weatherJsonString = new BufferedReader(
                            new InputStreamReader(request.openStream(), StandardCharsets.UTF_8))
                            .lines()
                            .collect(Collectors.joining("\n"));

                    System.out.println(weatherJsonString);

                } else {
                    Toast.makeText(this, "Problem with Weather API", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "No connection!", Toast.LENGTH_SHORT).show();

            }
            }catch(MalformedURLException murle){
                System.out.println("Wrong URL");
            }
            catch (IOException ioe){
                System.out.println("IO problem");
            }
        return weatherJsonString;
    }

    public void getForecast(String city, String unit){
        this.city = city;
        this.unit = unit;
        try {
            if (checkConnection()) {
                URL request = new URL("https://api.openweathermap.org/data/2.5/forecast?q=" + city + "&units=" + unit + "&lang=pl&appid=" + APIkey);
                HttpURLConnection apiConnection = (HttpURLConnection) request.openConnection();
                apiConnection.setRequestMethod("POST");
                apiConnection.connect();

                if (apiConnection.getResponseCode() == 200) {

                    forecastJsonString = new BufferedReader(
                            new InputStreamReader(request.openStream(), StandardCharsets.UTF_8))
                            .lines()
                            .collect(Collectors.joining("\n"));

                    System.out.println(forecastJsonString);

                } else {
                    Toast.makeText(this, "Problem with Weather API", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "No connection!", Toast.LENGTH_SHORT).show();

            }
        }catch(MalformedURLException murle){
            System.out.println("Wrong URL");
        }
        catch (IOException ioe){
            System.out.println("IO problem");
        }
    }


    public void forceUpdateActivities(int refresh, String longitude, String latitude){

        day.forceUpdate(refresh,longitude,latitude);
        night.forceUpdate(refresh,longitude,latitude);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        settingsString = readFromFile("settings.json");
        if(settingsString == null){
            writeToFile(createSettingsJSON("Łódź","standard",new ArrayList<>(Arrays.asList("Łódź"))), "settings.json");
            settingsString = readFromFile("settings.json");
            System.out.println("THERE WAS NO SUCH FILE!");
        }
        try {
            getDataFromString(settingsString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            if(checkConnection()) {
                getWeather(city,unit);
                if(weatherJsonString != null){
                    writeToFile(weatherJsonString, "w" + city + unit + ".json");
                }
                else{
                    weatherJsonString = "no data";
                    System.out.println("COULDN'T WRITE TO FILE");
                }
            }
            else{
                weatherJsonString = readFromFile("w" + city + unit + ".json");
                if (weatherJsonString == null){
                    weatherJsonString = "no data";
                    Toast.makeText(this, "Couldn't load data from file", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(this, "Loaded from file, data can be outdated", Toast.LENGTH_LONG).show();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(checkConnection()) {
            getForecast(city,unit);
            if(forecastJsonString != null){
                writeToFile(forecastJsonString, "w" + city + unit + ".json");
            }
            else{
                forecastJsonString = "no data";
                System.out.println("COULDN'T WRITE TO FILE");
            }
        }
        else{
            forecastJsonString = readFromFile("w" + city + unit + ".json");
            if (forecastJsonString == null){
                forecastJsonString = "no data";
                Toast.makeText(this, "Couldn't load data from file", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, "Loaded from file, data can be outdated", Toast.LENGTH_LONG).show();
            }
        }

        setContentView(R.layout.activity_main);
        setDefaultLocation();
        setDefaultRefresh();
        screenSlideAdapter = new ScreenSlideAdapter(this);
        viewPager = findViewById(R.id.container);
        screenSlideAdapter.addFragment(Day.newInstance(longitude,latitude,refresh),"Day");
        screenSlideAdapter.addFragment(Night.newInstance(longitude,latitude,refresh),"Night");
        screenSlideAdapter.addFragment(Weather.newInstance(city,unit, weatherJsonString),"Weather");
        screenSlideAdapter.addFragment(Forecast.newInstance(city,unit, forecastJsonString),"Forecast");
        screenSlideAdapter.addFragment(Settings.newInstance(longitude,latitude,refresh,settingsString),"Settings");
        viewPager.setAdapter(screenSlideAdapter);
        day = (Day) screenSlideAdapter.getFragment(0);
        night = (Night) screenSlideAdapter.getFragment(1);
        weather = (Weather) screenSlideAdapter.getFragment(2);
        forecast = (Forecast) screenSlideAdapter.getFragment(3);


    }



    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
        }


    }
}