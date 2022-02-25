package zdalne.zadania.astroweather;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;


public class Forecast extends Fragment {


    private static final String ARG_PARAM1 = "city";
    private static final String ARG_PARAM2 = "unit";
    private static final String ARG_PARAM3 = "JSONstring";


    private Thread forecastUpdater;
    private TextView forecast;
    private String forecastString = "";
    private String city;
    private String unit;
    private String temperatureUnit;
    private String JSONstring;
    private View rootView;
    private JSONObject data;


    public Forecast() {
        // Required empty public constructor
    }

    public void toJSONObject() throws JSONException {
        data = new JSONObject(JSONstring);
    }

    public void onStart() {
        super.onStart();
        setupForecastFragmentUpdater();

        forecastUpdater.start();

    }

    public static Forecast newInstance(String param1, String param2, String param3) {
        Forecast fragment = new Forecast();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    private void setUnitString(){
        switch (unit){
            case "standard":
                temperatureUnit = "K";
                break;
            case "metric":
                temperatureUnit = "C";
                break;
            case "imperial":
                temperatureUnit = "F";
                break;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            city = getArguments().getString(ARG_PARAM1);
            unit = getArguments().getString(ARG_PARAM2);
            JSONstring = getArguments().getString(ARG_PARAM3);
        }
        setUnitString();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_forecast, container, false);
        forecast = rootView.findViewById(R.id.forecast);
        return rootView;
    }

    public void setData() {

        try {
            JSONArray jsonArray;
            toJSONObject();
            jsonArray = data.getJSONArray("list");
            for(int i = 0; i<jsonArray.length();i++){
                Date dateOfday;
                dateOfday = new Date(jsonArray.getJSONObject(i).getLong("dt"));
                if(dateOfday.getHours() == 12){
                    forecastString = forecastString + "Date: " + dateOfday.getDate() + "\n"
                            + "Temperature: " + jsonArray.getJSONObject(i).getDouble("temp")
                            + " " + temperatureUnit + "\n"
                            + "Weather: " + jsonArray.getJSONObject(i).getJSONArray("weather")
                            .getJSONObject(0).getString("description") + "\n\n";
                }
            }
            forecast.setText(forecastString);
        } catch (JSONException e) {
            e.printStackTrace();
        }



    }

    private void updateWeather(){
        try {
            if(!this.JSONstring.equals("no data")){
                setUnitString();
                toJSONObject();
            }
            setData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void forceUpdateWeatherFragment(String city, String unit){
        forecastUpdater.interrupt();
        this.city = city;
        this.unit = unit;

        setupForecastFragmentUpdater();
        forecastUpdater.start();
    }

    public void setupForecastFragmentUpdater(){
        forecastUpdater = new Thread(() -> {
            Looper.prepare();
            while(!forecastUpdater.isInterrupted()){
                getActivity().runOnUiThread(()->{
                    try {
                        Toast toast;
                        if(((MainActivity) getActivity()).checkConnection()) {
                            JSONstring = ((MainActivity) getActivity()).getWeather(city,unit);

                            if(JSONstring != null){
                                ((MainActivity) getActivity()).writeToFile(JSONstring, "w" + city + unit + ".json");

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
                            JSONstring = ((MainActivity) getActivity()).readFromFile("w" + city + unit + ".json");
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