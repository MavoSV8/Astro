package zdalne.zadania.astroweather;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroCalculator.*;
import com.astrocalculator.AstroDateTime;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.TimeZone;

public class Night extends Fragment {

    private MoonInfo moonInfo;
    private AstroDateTime astroDateTime;
    private Location astroLocation;
    private LocalDateTime localDateTime;

    private static final String ARG_PARAM1 = "longitude";
    private static final String ARG_PARAM2 = "latitude";
    private static final String ARG_PARAM3 = "refresh";
    private TextView moonsetTime;
    private TextView moonriseTime;
    private TextView newMoonDate;
    private TextView fullMoonDate;
    private TextView moonPhase;
    private TextView dayOfLunarMonth;
    private TextView currentTimeNight;
    private TextView longitudeNight;
    private TextView latitudeNight;
    private Thread moonUpdater;
    private Thread phoneTimeUpdater;
    private boolean exit = false;
    private View rootView;
    private String longitude;
    private String latitude;
    private int refresh;
    private String synodicDay;

    public Night() {
        // Required empty public constructor
    }


    public static Night newInstance(String longi, String lati, int refresh) {
        Night fragment = new Night();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, longi);
        args.putString(ARG_PARAM2, lati);
        args.putInt(ARG_PARAM3, refresh);
        fragment.setArguments(args);
        return fragment;
    }

    private void setupMoonUpdater(){
        moonUpdater = new Thread(() -> {
            while(!moonUpdater.isInterrupted()){
                getActivity().runOnUiThread(this::setMoon);

                try {
                    Thread.sleep(refresh);
                } catch (InterruptedException e) {
                    exit = true;
                    e.printStackTrace();
                }
            }
        });
    }

    private void setupPhoneTimeUpdater(){
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
        setMoon();
        setPhoneTime();
        setupMoonUpdater();
        setupPhoneTimeUpdater();
        moonUpdater.start();
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
        rootView = inflater.inflate(R.layout.fragment_night, container, false);
        moonsetTime = rootView.findViewById(R.id.moonsetTime);
        moonriseTime = rootView.findViewById(R.id.moonriseTime);
        newMoonDate = rootView.findViewById(R.id.newMoonDate);
        fullMoonDate = rootView.findViewById(R.id.fullMoonDate);
        moonPhase = rootView.findViewById(R.id.moonPhase);
        dayOfLunarMonth = rootView.findViewById(R.id.dayOfLunarMonth);
        currentTimeNight = rootView.findViewById(R.id.currentTimeNight);
        longitudeNight = rootView.findViewById(R.id.longitudeNight);
        latitudeNight = rootView.findViewById(R.id.latitudeNight);

        return rootView;
    }

    public void setMoonInfo(){
        localDateTime = LocalDateTime.now();
        astroDateTime = new AstroDateTime(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth(), localDateTime.getHour(), localDateTime.getMinute(), localDateTime.getSecond(), 1, TimeZone.getDefault().inDaylightTime( new Date()));
        astroLocation = new Location(Double.parseDouble(longitude),Double.parseDouble(latitude));
        AstroCalculator astroCalculator = new AstroCalculator(astroDateTime,astroLocation);
        moonInfo = astroCalculator.getMoonInfo();
        synodicDay = String.valueOf(Math.abs(localDateTime.until(LocalDateTime.of(moonInfo.getNextNewMoon().getYear(),moonInfo.getNextNewMoon().getMonth(),moonInfo.getNextNewMoon().getDay(),moonInfo.getNextNewMoon().getHour(),moonInfo.getNextNewMoon().getMinute()), ChronoUnit.DAYS)));

    }

    @SuppressLint("SetTextI18n")
    public void setPhoneTime(){
        localDateTime = LocalDateTime.now();
        if(localDateTime.getHour() < 10 && localDateTime.getMinute()<10){
            currentTimeNight.setText("0" + localDateTime.getHour() + ":0" + localDateTime.getMinute());
        }
        else if(localDateTime.getHour() >= 10 && localDateTime.getMinute() < 10){
            currentTimeNight.setText(localDateTime.getHour() + ":0" + localDateTime.getMinute());
        }
        else if(localDateTime.getHour() < 10 && localDateTime.getMinute() >= 10){
            currentTimeNight.setText("0" + localDateTime.getHour() + ":" + localDateTime.getMinute());
        }
        else{
            currentTimeNight.setText(localDateTime.getHour() + ":" + localDateTime.getMinute());
        }

    }

    @SuppressLint("SetTextI18n")
    public void setMoon(){
        setMoonInfo();
        if(moonInfo.getMoonset().getHour() < 10 && moonInfo.getMoonset().getMinute()<10){
            moonsetTime.setText("0" + moonInfo.getMoonset().getHour() + ":0" + moonInfo.getMoonset().getMinute());
        }
        else if(moonInfo.getMoonset().getHour() >= 10 && moonInfo.getMoonset().getMinute() < 10){
            moonsetTime.setText(moonInfo.getMoonset().getHour() + ":0" + moonInfo.getMoonset().getMinute());
        }
        else if(moonInfo.getMoonset().getHour() < 10 && moonInfo.getMoonset().getMinute() >= 10){
            moonsetTime.setText("0" + moonInfo.getMoonset().getHour() + ":" + moonInfo.getMoonset().getMinute());
        }
        else{
            moonsetTime.setText(moonInfo.getMoonset().getHour() + ":" + moonInfo.getMoonset().getMinute());
        }

        if(moonInfo.getMoonrise().getHour() < 10 && moonInfo.getMoonrise().getMinute() < 10){
            moonriseTime.setText("0" + moonInfo.getMoonrise().getHour() + ":0" + moonInfo.getMoonrise().getMinute());
        }
        else if(moonInfo.getMoonrise().getHour() >= 10 && moonInfo.getMoonrise().getMinute() < 10){
            moonriseTime.setText(moonInfo.getMoonrise().getHour() + ":0" + moonInfo.getMoonrise().getMinute());
        }
        else if(moonInfo.getMoonrise().getHour() < 10 && moonInfo.getMoonrise().getMinute() >= 10){
            moonriseTime.setText("0" + moonInfo.getMoonrise().getHour() + ":" + moonInfo.getMoonrise().getMinute());
        }
        else{
            moonriseTime.setText(moonInfo.getMoonrise().getHour() + ":" + moonInfo.getMoonrise().getMinute());
        }



        newMoonDate.setText(moonInfo.getNextNewMoon().toString());
        fullMoonDate.setText(moonInfo.getNextFullMoon().toString());
        moonPhase.setText((moonInfo.getIllumination()*100) + "%");
        dayOfLunarMonth.setText(synodicDay);
        latitudeNight.setText(latitude);
        longitudeNight.setText(longitude);
        Toast.makeText(getActivity(),"Moon Data updated!", Toast.LENGTH_SHORT).show();

    }

    public void forceUpdate(int refresh, String lon, String lat){
        moonUpdater.interrupt();
        this.refresh = refresh;
        this.longitude = lon;
        this.latitude = lat;
        setupMoonUpdater();
        exit = false;
        moonUpdater.start();

    }


}