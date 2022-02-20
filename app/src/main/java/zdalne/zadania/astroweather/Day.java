package zdalne.zadania.astroweather;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Day#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Day extends Fragment {


    private static final String ARG_PARAM1 = "longitude";
    private static final String ARG_PARAM2 = "latitude";
    private static final String ARG_PARAM3 = "refresh";

    private AstroCalculator.SunInfo sunInfo;
    private AstroDateTime astroDateTime;
    private AstroCalculator.Location astroLocation;
    private LocalDateTime localDateTime;

    private TextView sunsetTime;
    private TextView sunsetAzimuth;
    private TextView sunriseTime;
    private TextView sunriseAzimuth;
    private TextView civilDawnTime;
    private TextView civilDuskTime;


    private TextView currentTimeDay;
    private TextView longitudeDay;
    private TextView latitudeDay;
    private Thread sunUpdater;
    private Thread phoneTimeUpdater;
    private boolean exit = false;


    private View rootView;
    private String longitude;
    private String latitude;
    private int refresh;

    public Day() {
        // Required empty public constructor
    }


    public static Day newInstance(String longi, String lati, int refresh) {
        Day fragment = new Day();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, longi);
        args.putString(ARG_PARAM2, lati);
        args.putInt(ARG_PARAM3, refresh);
        fragment.setArguments(args);
        return fragment;
    }

    private void setupSunUpdater() {
        sunUpdater = new Thread(() -> {
            while (!sunUpdater.isInterrupted()) {
                getActivity().runOnUiThread(this::setSun);
                try {
                    Thread.sleep(refresh);
                } catch (InterruptedException e) {
                    exit = true;
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupPhoneTimeUpdater() {
        phoneTimeUpdater = new Thread(() -> {
            while(true){
                getActivity().runOnUiThread(this::setPhoneTime);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });

    }


    public void onStart() {
        super.onStart();
        setSun();
        setPhoneTime();
        setupSunUpdater();
        setupPhoneTimeUpdater();
        sunUpdater.start();
        phoneTimeUpdater.start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            longitude = getArguments().getString(ARG_PARAM1);
            latitude = getArguments().getString(ARG_PARAM2);
            refresh = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_day, container, false);
        sunsetTime = rootView.findViewById(R.id.sunsetTime);
        sunsetAzimuth = rootView.findViewById(R.id.sunsetAzimuth);
        sunriseTime = rootView.findViewById(R.id.sunriseTime);
        sunriseAzimuth = rootView.findViewById(R.id.sunriseAzimuth);
        civilDawnTime = rootView.findViewById(R.id.civilDawnTime);
        civilDuskTime = rootView.findViewById(R.id.civilDuskTime);

        currentTimeDay = rootView.findViewById(R.id.currentTimeDay);
        longitudeDay = rootView.findViewById(R.id.longitudeDay);
        latitudeDay = rootView.findViewById(R.id.latitudeDay);
        return rootView;
    }

    public void setSunInfo() {
        localDateTime = LocalDateTime.now();
        astroDateTime = new AstroDateTime(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond(), 1, TimeZone.getDefault().inDaylightTime(new Date()));
        astroLocation = new AstroCalculator.Location(Double.parseDouble(longitude), Double.parseDouble(latitude));
        AstroCalculator astroCalculator = new AstroCalculator(astroDateTime, astroLocation);
        sunInfo = astroCalculator.getSunInfo();
    }

    @SuppressLint("SetTextI18n")
    public void setPhoneTime() {
        localDateTime = LocalDateTime.now();
        if(localDateTime.getHour() < 10 && localDateTime.getMinute()<10){
            currentTimeDay.setText("0" + localDateTime.getHour() + ":0" + localDateTime.getMinute());
        }
        else if(localDateTime.getHour() >= 10 && localDateTime.getMinute() < 10){
            currentTimeDay.setText(localDateTime.getHour() + ":0" + localDateTime.getMinute());
        }
        else if(localDateTime.getHour() < 10 && localDateTime.getMinute() >= 10){
            currentTimeDay.setText("0" + localDateTime.getHour() + ":" + localDateTime.getMinute());
        }
        else{
            currentTimeDay.setText(localDateTime.getHour() + ":" + localDateTime.getMinute());
        }
    }

    @SuppressLint("SetTextI18n")
    public void setSun() {
        setSunInfo();
        if(sunInfo.getSunset().getHour() < 10 && sunInfo.getSunset().getMinute() < 10){
            sunsetTime.setText("0" + sunInfo.getSunset().getHour() + ":0" + sunInfo.getSunset().getMinute());
        }
        else if(sunInfo.getSunset().getHour() >= 10 && sunInfo.getSunset().getMinute() < 10){
            sunsetTime.setText(sunInfo.getSunset().getHour() + ":0" + sunInfo.getSunset().getMinute());
        }
        else if(sunInfo.getSunset().getHour() < 10 && sunInfo.getSunset().getMinute() >= 10){
            sunsetTime.setText("0" + sunInfo.getSunset().getHour() + ":" + sunInfo.getSunset().getMinute());
        }
        else{
            sunsetTime.setText(sunInfo.getSunset().getHour() + ":" + sunInfo.getSunset().getMinute());

        }


        if(sunInfo.getSunrise().getHour() < 10 && sunInfo.getSunrise().getMinute() < 10){
            sunriseTime.setText("0" + sunInfo.getSunrise().getHour() + ":0" + sunInfo.getSunrise().getMinute());
        }
        else if(sunInfo.getSunrise().getHour() >= 10 && sunInfo.getSunrise().getMinute() < 10){
            sunriseTime.setText(sunInfo.getSunrise().getHour() + ":0" + sunInfo.getSunrise().getMinute());
        }
        else if(sunInfo.getSunrise().getHour() < 10 && sunInfo.getSunrise().getMinute() >= 10){
            sunriseTime.setText("0" + sunInfo.getSunrise().getHour() + ":" + sunInfo.getSunrise().getMinute());
        }
        else{
            sunriseTime.setText(sunInfo.getSunrise().getHour() + ":" + sunInfo.getSunrise().getMinute());
        }


        sunriseAzimuth.setText(String.valueOf(sunInfo.getAzimuthRise()));
        sunsetAzimuth.setText(String.valueOf(sunInfo.getAzimuthSet()));

        if(sunInfo.getTwilightMorning().getHour() < 10 && sunInfo.getTwilightMorning().getMinute() < 10){
            civilDawnTime.setText("0" + sunInfo.getTwilightMorning().getHour() + ":0" + sunInfo.getTwilightMorning().getMinute());
        }
        else if(sunInfo.getTwilightMorning().getHour() >= 10 && sunInfo.getTwilightMorning().getMinute() < 10){
            civilDawnTime.setText(sunInfo.getTwilightMorning().getHour() + ":0" + sunInfo.getTwilightMorning().getMinute());
        }
        else if(sunInfo.getTwilightMorning().getHour() < 10 && sunInfo.getTwilightMorning().getMinute() >= 10){
            civilDawnTime.setText("0" + sunInfo.getTwilightMorning().getHour() + ":" + sunInfo.getTwilightMorning().getMinute());
        }
        else{
            civilDawnTime.setText(sunInfo.getTwilightMorning().getHour() + ":" + sunInfo.getTwilightMorning().getMinute());
        }

        if(sunInfo.getTwilightEvening().getHour() < 10 && sunInfo.getTwilightEvening().getMinute() < 10){
            civilDuskTime.setText("0" + sunInfo.getTwilightEvening().getHour() + ":0" + sunInfo.getTwilightEvening().getMinute());
        }
        else if(sunInfo.getTwilightEvening().getHour() >= 10 && sunInfo.getTwilightEvening().getMinute() < 10){
            civilDuskTime.setText(sunInfo.getTwilightEvening().getHour() + ":0" + sunInfo.getTwilightEvening().getMinute());
        }
        else if(sunInfo.getTwilightEvening().getHour() < 10 && sunInfo.getTwilightEvening().getMinute() >= 10){
            civilDuskTime.setText("0" + sunInfo.getTwilightEvening().getHour() + ":" + sunInfo.getTwilightEvening().getMinute());
        }
        else{
            civilDuskTime.setText(sunInfo.getTwilightEvening().getHour() + ":" + sunInfo.getTwilightEvening().getMinute());
        }

        latitudeDay.setText(latitude);
        longitudeDay.setText(longitude);
        Toast.makeText(getActivity(),"Sun Data updated!", Toast.LENGTH_SHORT).show();
    }

    public void forceUpdate(int refresh, String lon, String lat) {
        sunUpdater.interrupt();
        setupSunUpdater();
        this.refresh = refresh;
        this.longitude = lon;
        this.latitude = lat;
        exit = false;
        sunUpdater.start();


    }


}