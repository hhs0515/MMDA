package com.csed404.mmda.ui.slideshow;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.csed404.mmda.R;
import com.csed404.mmda.databinding.FragmentSlideshowBinding;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.dailyJournal;
        slideshowViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        Bundle args = getArguments();
        if (args != null) {
            int cYear = args.getInt("year");
            int cMonth = args.getInt("month");
            int cDay = args.getInt("day");
            StringBuilder sb = getJournalOn(cYear, cMonth, cDay);
            Bitmap bitmap = getImageOn(cYear, cMonth, cDay);

            binding.dailyJournal.setText(sb.toString());
            if(bitmap != null){
                binding.imageView.setImageBitmap(bitmap);
            }
        }

        binding.returnButton.setOnClickListener(view -> {
            NavHostFragment navHostFragment = (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
            assert navHostFragment != null;
            NavController navController = navHostFragment.getNavController();

            navController.navigate(R.id.action_nav_slideshow_to_nav_home);
        });

        return root;
    }

    @NotNull
    private StringBuilder getJournalOn(int cYear, int cMonth, int cDay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        Date date = new Date(cYear-1900, cMonth, cDay);

        File classDir = new File(getActivity().getFilesDir(), sdf.format(date));
        File recordFile = new File(classDir, "journal.txt");
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(recordFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            sb.append("No record.");
        }
        return sb;
    }

    private Bitmap getImageOn(int cYear, int cMonth, int cDay) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        Date date = new Date(cYear-1900, cMonth, cDay);

        File classDir = new File(getActivity().getFilesDir(), sdf.format(date));
        File recordFile = new File(classDir, "image.png");
        if(!recordFile.exists()) return null;
        return BitmapFactory.decodeFile(recordFile.getAbsolutePath());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}