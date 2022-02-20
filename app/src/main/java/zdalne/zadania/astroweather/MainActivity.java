package zdalne.zadania.astroweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private ScreenSlideAdapter screenSlideAdapter;
    private ViewPager2 viewPager;

    private String longitude;
    private String latitude;
    private int refresh;
    private Day day;
    private Night night;



    private void setDefaultLocation(){

        longitude = "51.74";
        latitude = "19.45";

    }

    private void setDefaultRefresh(){
        refresh = 15000;

    }
    public void forceUpdateActivities(int refresh, String longitude, String latitude){

        day.forceUpdate(refresh,longitude,latitude);
        night.forceUpdate(refresh,longitude,latitude);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setDefaultLocation();
        setDefaultRefresh();
        screenSlideAdapter = new ScreenSlideAdapter(this);
        viewPager = findViewById(R.id.container);
        screenSlideAdapter.addFragment(Day.newInstance(longitude,latitude,refresh),"Day");
        screenSlideAdapter.addFragment(Night.newInstance(longitude,latitude,refresh),"Night");
        screenSlideAdapter.addFragment(Settings.newInstance(longitude,latitude,refresh),"Settings");
        viewPager.setAdapter(screenSlideAdapter);
        day = (Day) screenSlideAdapter.getFragment(0);
        night = (Night) screenSlideAdapter.getFragment(1);


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