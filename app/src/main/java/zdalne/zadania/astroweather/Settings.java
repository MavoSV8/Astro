package zdalne.zadania.astroweather;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class Settings extends Fragment{


    private static final String ARG_PARAM1 = "longitude";
    private static final String ARG_PARAM2 = "latitude";
    private static final String ARG_PARAM3 = "refresh";
    private static final String ARG_PARAM4 = "settings";
    private Spinner refreshSpinner;
    private Spinner citiesSpinner;
    private ArrayList<String> cities = new ArrayList<>();
    private Spinner unitsSpinner;
    private EditText longitudeInput;
    private EditText latitudeInput;
    private EditText cityInput;
    private ArrayAdapter<CharSequence> refreshSpinnerAdapter;
    private ArrayAdapter<String> citiesSpinnerAdapter;
    private ArrayAdapter<CharSequence> unitsSpinnerAdapter;
    private String longitude;
    private String latitude;
    private int refresh;
    private String refreshString;
    private View rootView;
    private String settings;
    private JSONObject settingsJSON;
    private String selectedCity;
    private String selectedUnit;


    public Settings() {
        // Required empty public constructor
    }

    public static Settings newInstance(String param1, String param2, int param3, String param4) {
        Settings fragment = new Settings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4,param4);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            longitude = getArguments().getString(ARG_PARAM1);
            latitude = getArguments().getString(ARG_PARAM2);
            refresh = getArguments().getInt(ARG_PARAM3);
            settings = getArguments().getString(ARG_PARAM4);
        }
        try {
            settingsJSON = new JSONObject(settings);
            JSONArray jsonArray = settingsJSON.getJSONArray("favourites");
            for (int i = 0;i<jsonArray.length(); i++){
                cities.add(jsonArray.getString(i));
            }
            selectedCity = settingsJSON.getString("selected");
            selectedUnit = settingsJSON.getString("unit");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        refreshString = String.valueOf(refresh);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        latitudeInput = rootView.findViewById(R.id.latitudeInput);
        longitudeInput = rootView.findViewById(R.id.longitudeInput);
        refreshSpinner = rootView.findViewById(R.id.refreshSpinner);
        refreshSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshString = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        refreshSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.refresh_values, android.R.layout.simple_spinner_item);
        refreshSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        refreshSpinner.setAdapter(refreshSpinnerAdapter);
        refreshSpinner.getSelectedItem();
        citiesSpinner = rootView.findViewById(R.id.citiesSpinner);
        citiesSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCity = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        citiesSpinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, cities);
        citiesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citiesSpinner.setAdapter(citiesSpinnerAdapter);

        unitsSpinner = rootView.findViewById(R.id.unitsSpinner);
        unitsSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedUnit = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        unitsSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.units, android.R.layout.simple_spinner_item);
        unitsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        unitsSpinner.setAdapter(unitsSpinnerAdapter);


        Button saveAstro = rootView.findViewById(R.id.saveButton);
        saveAstro.setOnClickListener(v -> saveChanges());
        Button saveWeather = rootView.findViewById(R.id.saveButton2);
        saveWeather.setOnClickListener(v -> saveWeatherChanges());
        Button addCity = rootView.findViewById(R.id.addCityButton);
        addCity.setOnClickListener(v -> addCity());
        Button delCity = rootView.findViewById(R.id.delCityButton);
        delCity.setOnClickListener(v -> deleteCity());
        Button refreshWeather = rootView.findViewById(R.id.refreshButton);
        refreshWeather.setOnClickListener(v -> refreshWeatherData());

        cityInput = rootView.findViewById(R.id.cityInput);

        setValues();
        setWeatherValues();
        return rootView;
    }

    public void setValues() {
        refreshSpinner.setSelection(refreshSpinnerAdapter.getPosition(refreshString));
        latitudeInput.setText(latitude);
        longitudeInput.setText(longitude);

    }

    public void setWeatherValues(){
        citiesSpinner.setSelection(citiesSpinnerAdapter.getPosition(selectedCity));
        unitsSpinner.setSelection(unitsSpinnerAdapter.getPosition(selectedUnit));

    }

    public void saveChanges() {

        switch (refreshString) {
            case "15s":
                refresh = 15000;
                break;
            case "30s":
                refresh = 30000;
                break;
            case "1m":
                refresh = 60000;
                break;
            case "10m":
                refresh = 600000;
                break;
            case "30m":
                refresh = 1800000;
                break;
        }

        longitude = longitudeInput.getText().toString();
        latitude = latitudeInput.getText().toString();
        ((MainActivity) getActivity()).forceUpdateActivities(refresh, longitude, latitude);
        setValues();
    }

    public void refreshWeatherData(){
        ((MainActivity) getActivity()).forceUpdateWeather(selectedCity,selectedUnit);
    }
    public void addCity(){
        cities.add(cityInput.getText().toString().trim());
        cityInput.setText("");
        citiesSpinnerAdapter.notifyDataSetChanged();
    }
    public void deleteCity(){
        cities.remove(cityInput.getText().toString().trim());
        cityInput.setText("");
        citiesSpinnerAdapter.notifyDataSetChanged();
    }
    public void saveWeatherChanges(){
        setWeatherValues();
        settings = ((MainActivity)getActivity()).createSettingsJSON(selectedCity,selectedUnit,cities);
        ((MainActivity) getActivity()).writeToFile(settings,"settings.json");
    }


}