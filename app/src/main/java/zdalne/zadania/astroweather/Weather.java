package zdalne.zadania.astroweather;

import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


public class Weather extends Fragment {


    private static final String ARG_PARAM1 = "city";
    private static final String ARG_PARAM2 = "unit";
    private static final String ARG_PARAM3 = "JSONstring";

    private Thread weatherUpdater;
    private TextView city;
    private TextView longitudeWeather;
    private TextView latitudeWeather;
    private TextView pressure;
    private TextView windStrength;
    private TextView windDirection;
    private TextView humidity;
    private TextView visibility;
    private TextView weather;
    private TextView temperature;
    private ImageView weatherImg;
    private JSONObject data;

    private String cityString;
    private String unit;
    private String JSONstring;

    private View rootView;

    public Weather() {
        // Required empty public constructor
    }


    public static Weather newInstance(String city, String unit, String jsonString) {
        Weather fragment = new Weather();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, city);
        args.putString(ARG_PARAM2, unit);
        args.putString(ARG_PARAM3, jsonString);
        fragment.setArguments(args);
        return fragment;
    }

    public void onStart() {
        super.onStart();
        setupWeatherFragmentsUpdater();
        try {
            if(!JSONstring.equals("no data")){
                toJSONObject();
            }
            setData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        weatherUpdater.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cityString = getArguments().getString(ARG_PARAM1);
            unit = getArguments().getString(ARG_PARAM2);
            JSONstring = getArguments().getString(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        city = rootView.findViewById(R.id.city);
        longitudeWeather = rootView.findViewById(R.id.longitudeWeather);
        latitudeWeather = rootView.findViewById(R.id.latitudeWeather);
        pressure = rootView.findViewById(R.id.pressure);
        windStrength = rootView.findViewById(R.id.strength);
        windDirection = rootView.findViewById(R.id.direction);
        humidity = rootView.findViewById(R.id.humidity);
        visibility = rootView.findViewById(R.id.visibility);
        weather = rootView.findViewById(R.id.weather);
        weatherImg = rootView.findViewById(R.id.weatherImg);
        temperature = rootView.findViewById(R.id.temperature);


        return rootView;
    }


    public void toJSONObject() throws JSONException {
        data = new JSONObject(JSONstring);

    }
    @SuppressLint("SetTextI18n")
    public void setData() throws JSONException {
        if(!JSONstring.equals("no data")) {
            city.setText(cityString);
            longitudeWeather.setText(String.valueOf(data.getJSONObject("coord").getDouble("lon")));
            latitudeWeather.setText(String.valueOf(data.getJSONObject("coord").getDouble("lat")));
            switch (unit){
                case "standard":
                    temperature.setText(data.getJSONObject("main").getDouble("temp") + " K");
                    break;
                case "metric":
                    temperature.setText(data.getJSONObject("main").getDouble("temp") + " C");
                    break;
                case "imperial":
                    temperature.setText(data.getJSONObject("main").getDouble("temp") + " F");
                    break;
            }

            pressure.setText(data.getJSONObject("main").getInt("pressure") + " hPa");
            humidity.setText(data.getJSONObject("main").getInt("humidity") + "%");
            visibility.setText(data.getInt("visibility") + " metres");
            weather.setText(data.getJSONArray("weather").getJSONObject(0).getString("description"));
            switch (unit){
                case "standard":
                case "metric":
                    windStrength.setText(data.getJSONObject("wind").getDouble("speed") + " m/s");
                    break;
                case "imperial":
                    windStrength.setText(data.getJSONObject("wind").getDouble("speed") + " miles/h");
            }

            windDirection.setText(data.getJSONObject("wind").getInt("deg") + " deg");
            if (((MainActivity) getActivity()).checkConnection()) {
                Glide.with(getActivity()).load("http://openweathermap.org/img/wn/" + data.getJSONArray("weather").getJSONObject(0).getString("icon") + "@2x.png").into(weatherImg);
            }
        }
        else{
            city.setText("no data");
            longitudeWeather.setText("no data");
            latitudeWeather.setText("no data");
            temperature.setText("no data");
            pressure.setText("no data");
            humidity.setText("no data");
            visibility.setText("no data");
            weather.setText("no data");
            windStrength.setText("no data");
            windDirection.setText("no data");

        }
    }

    private void updateWeather(){
        try {
            if(!this.JSONstring.equals("no data")){
                toJSONObject();
            }
            setData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void forceUpdateWeatherFragment(String city, String unit){
        weatherUpdater.interrupt();
        cityString = city;
        this.unit = unit;

        setupWeatherFragmentsUpdater();
        weatherUpdater.start();
    }

    public void setupWeatherFragmentsUpdater(){
        weatherUpdater = new Thread(() -> {
            Looper.prepare();
            while(!weatherUpdater.isInterrupted()){
                getActivity().runOnUiThread(()->{
                    try {
                        Toast toast;
                        if(((MainActivity) getActivity()).checkConnection()) {
                            JSONstring = ((MainActivity) getActivity()).getWeather(cityString,unit);

                            if(JSONstring != null){
                                ((MainActivity) getActivity()).writeToFile(JSONstring, "w" + cityString + unit + ".json");

                                toast = Toast.makeText(getActivity(), "Weather refreshed", Toast.LENGTH_SHORT);
                            }
                            else{
                                JSONstring = "no data";
                                toast = Toast.makeText(getActivity(), "Couldn't save data to file", Toast.LENGTH_SHORT);
                                System.out.println("COULDN'T WRITE TO FILE");
                            }

                            //Thread.sleep(3600000);

                        }
                        else{
                            JSONstring = ((MainActivity) getActivity()).readFromFile("w" + cityString + unit + ".json");
                            if (JSONstring == null){
                                JSONstring = "no data";
                                toast = Toast.makeText(getActivity(), "Couldn't load data from file", Toast.LENGTH_LONG);
                            }
                            else{
                                toast = Toast.makeText(getActivity(), "Loaded from file, data can be outdated", Toast.LENGTH_LONG);
                            }

                        }

                        toast.show();
                        updateWeather();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}