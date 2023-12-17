package com.csed404.mmda.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavHostController;
import androidx.navigation.fragment.NavHostFragment;
import com.csed404.mmda.R;
import com.csed404.mmda.databinding.FragmentHomeBinding;
import com.csed404.mmda.ui.today.TodayFragment;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private CalendarView mainCalendar;
    private Button mainButton;
    private int cYear, cMonth, cDay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mainCalendar = binding.calendarMain;
        mainButton = binding.buttonAction;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Calendar calendar = Calendar.getInstance();

        cYear = calendar.get(Calendar.YEAR);
        cMonth = calendar.get(Calendar.MONTH);
        cDay = calendar.get(Calendar.DAY_OF_MONTH);

        mainCalendar.setOnDateChangeListener((calendarView, i, i1, i2) -> {
                cYear = i;
                cMonth = i1;
                cDay = i2;
        });

        mainButton.setOnClickListener( view -> {
            NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();

            Bundle bundle = new Bundle();
            bundle.putInt("year", cYear);
            bundle.putInt("month", cMonth);
            bundle.putInt("day", cDay);

            if(cYear == calendar.get(Calendar.YEAR) && cMonth == calendar.get(Calendar.MONTH) && cDay == calendar.get(Calendar.DAY_OF_MONTH)){
                navController.navigate(R.id.action_nav_home_to_nav_today);
            }
            else{
                navController.navigate(R.id.action_nav_home_to_nav_slideshow, bundle);
            }
        });

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

