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


public class Settings extends Fragment implements OnItemSelectedListener{


    private static final String ARG_PARAM1 = "longitude";
    private static final String ARG_PARAM2 = "latitude";
    private static final String ARG_PARAM3 = "refresh";
    private Spinner refreshSpinner;
    private EditText longitudeInput;
    private EditText latitudeInput;
    private ArrayAdapter<CharSequence> adapter;
    private String longitude;
    private String latitude;
    private int refresh;
    private String refreshString;
    private View rootView;


    public Settings() {
        // Required empty public constructor
    }

    public static Settings newInstance(String param1, String param2, int param3) {
        Settings fragment = new Settings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    public void onStart() {

        super.onStart();
        setValues();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            longitude = getArguments().getString(ARG_PARAM1);
            latitude = getArguments().getString(ARG_PARAM2);
            refresh = getArguments().getInt(ARG_PARAM3);
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
        refreshSpinner.setOnItemSelectedListener(this);
        adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.refresh_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        refreshSpinner.setAdapter(adapter);
        refreshSpinner.getSelectedItem();
        Button button = rootView.findViewById(R.id.saveButton);
        button.setOnClickListener(v -> {
            saveChanges();
        });
        setValues();
        return rootView;
    }

    public void setValues() {
        refreshSpinner.setSelection(adapter.getPosition(refreshString));
        latitudeInput.setText(latitude);
        longitudeInput.setText(longitude);

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


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        refreshString = (String) parent.getItemAtPosition(position);
        System.out.println(refreshString);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}