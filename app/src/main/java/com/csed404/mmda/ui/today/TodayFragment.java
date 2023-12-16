package com.csed404.mmda.ui.today;

import android.widget.Toast;
import com.csed404.mmda.ui.home.GptClient;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.csed404.mmda.databinding.FragmentTodayBinding;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TodayFragment extends Fragment {
    private EditText logText, llmText;

    private FragmentTodayBinding binding;

    private Date date;
    private SimpleDateFormat sdf;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TodayViewModel todayViewModel =
                new ViewModelProvider(this).get(TodayViewModel.class);

        binding = FragmentTodayBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        date = new Date(System.currentTimeMillis());
        sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);

        logText = binding.dailyRawText;
        llmText = binding.generatedText;

        binding.button.setOnClickListener(view -> generateJournal(String.valueOf(logText.getText())));
        binding.saveButton.setOnClickListener(view -> saveJournal());

        setLogText();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void setLogText(){
        File classDir = new File(getActivity().getFilesDir(), sdf.format(date));
        File recordFile = new File(classDir, "record.txt");
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(recordFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Process each line as needed
                sb.append(line);
            }
        } catch (IOException e) {
            sb.append("No record.");
        }
        logText.setText(sb.toString());
    }

    public void generateJournal(String log){
        GptClient httpClient = new GptClient();
        llmText.setText(httpClient.generateText(log));

    }

    public void saveJournal(){
        File classDir = new File(getActivity().getFilesDir(), sdf.format(date));
        if (!classDir.exists()) classDir.mkdirs();
        try (FileWriter fw = new FileWriter(new File(classDir, "journal.txt"), false)) {
            fw.write(String.valueOf(llmText.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getActivity().getApplicationContext(), "Journal Saved.", Toast.LENGTH_SHORT).show();
    }
}