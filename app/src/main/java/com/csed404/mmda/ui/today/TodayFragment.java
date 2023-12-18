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

        binding.button.setOnClickListener(view -> {
            String log = sdf.format(date) + "\n" + logText.getText();
            generateJournal(log);
        });
        binding.saveButton.setOnClickListener(view -> saveJournal());

        setLogText();
        setJournalText();

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

        readFile(recordFile, sb, logText);
    }

    public void setJournalText(){
        File classDir = new File(getActivity().getFilesDir(), sdf.format(date));
        File recordFile = new File(classDir, "journal.txt");
        StringBuilder sb = new StringBuilder();

        if(recordFile.exists()){
            readFile(recordFile, sb, llmText);
        }
    }

    private void readFile(File recordFile, StringBuilder sb, EditText editText) {
        try (BufferedReader br = new BufferedReader(new FileReader(recordFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            sb.append("No record.");
        }
        editText.setText(sb.toString());
    }

    public void generateJournal(String log){
        final String[] generatedText = new String[1];
        Toast.makeText(getActivity().getApplicationContext(), "On request...", Toast.LENGTH_LONG).show();
        new Thread(() -> {
            GptClient httpClient = new GptClient();
            generatedText[0] = httpClient.generateTxt(log, "");

            // Update UI on the main (UI) thread
            getActivity().runOnUiThread(() -> {
                llmText.setText(generatedText[0]);
                Toast.makeText(getActivity().getApplicationContext(), "MMDA!", Toast.LENGTH_LONG).show();
            });
        }).start();
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