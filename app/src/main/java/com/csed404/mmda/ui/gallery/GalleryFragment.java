package com.csed404.mmda.ui.gallery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.csed404.mmda.R;
import com.csed404.mmda.databinding.FragmentGalleryBinding;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private boolean recording;
    private Button recordButton, saveButton;
    private TextView recordText;
    private SpeechRecognizer speechRecognizer;
    private Intent intent;

    public GalleryFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recordButton = binding.recordButton;
        recordText = binding.contentsTextView;
        saveButton = binding.saveButton;
        recordButton.setOnClickListener(view -> {
            if (!recording) {
                StartRecord();
            }
            else {
                StopRecord();
            }
        });

        saveButton.setOnClickListener(view -> {
            saveRecord();
        });

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recording = false;

        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getActivity().getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");   //한국어
    }

    protected void StartRecord() {
        recording = true;
        CheckPermission();

        Toast.makeText(getActivity().getApplicationContext(), "Recording in progress", Toast.LENGTH_SHORT).show();
        recordButton.setText("STOP");

        speechRecognizer= SpeechRecognizer.createSpeechRecognizer(getActivity().getApplicationContext());
        speechRecognizer.setRecognitionListener(listener);
        speechRecognizer.startListening(intent);
    }

    //녹음 중지
    void StopRecord() {
        recording = false;

        recordButton.setText("RECORD");
        speechRecognizer.stopListening();   //녹음 중지
        Toast.makeText(getActivity().getApplicationContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();
    }

    protected void CheckPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED ) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.INTERNET,
                            Manifest.permission.RECORD_AUDIO}, 123 );
        }
    }

    protected void saveRecord(){
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        SimpleDateFormat dateLog = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);

        File classDir = new File(getActivity().getFilesDir(), sdf.format(date));
        if (!classDir.exists()) classDir.mkdirs();
        try (FileWriter fw = new FileWriter(new File(classDir, "record.txt"), true)) {
            fw.write("\n" + dateLog.format(date) + " " + recordText.getText());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(getActivity().getApplicationContext(), "Saved for " + dateLog.format(date), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Tasks to perform when the fragment becomes visible
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tasks to perform when the fragment is in the foreground
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        speechRecognizer.stopListening();
        binding = null;
    }


    RecognitionListener listener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) { }

        @Override
        public void onBeginningOfSpeech() { }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {

        }

        @Override
        public void onEndOfSpeech() { }

        @Override
        public void onError(int error) {    //토스트 메세지로 에러 출력
            String message;
            switch (error) {
                case SpeechRecognizer.ERROR_AUDIO:
                    message = "오디오 에러";
                    break;
                case SpeechRecognizer.ERROR_CLIENT:
                    return;
                case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                    message = "퍼미션 없음";
                    break;
                case SpeechRecognizer.ERROR_NETWORK:
                    message = "네트워크 에러";
                    break;
                case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                    message = "네트웍 타임아웃";
                    break;
                case SpeechRecognizer.ERROR_NO_MATCH:
                    if (recording)
                        StartRecord();
                    return;
                case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                    message = "RECOGNIZER가 바쁨";
                    break;
                case SpeechRecognizer.ERROR_SERVER:
                    message = "서버가 이상함";
                    break;
                case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                    message = "말하는 시간초과";
                    break;
                default:
                    message = "알 수 없는 오류임";
                    break;
            }
            Toast.makeText(getActivity().getApplicationContext(), "에러가 발생하였습니다. : " + message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String originText = recordText.getText().toString();

            //인식 결과
            StringBuilder newText= new StringBuilder();
            for (String match : matches) {
                newText.append(match);
            }

            recordText.setText(originText + newText + " ");
            speechRecognizer.startListening(intent);
        }

        @Override
        public void onPartialResults(Bundle bundle) {

        }

        @Override
        public void onEvent(int i, Bundle bundle) {

        }
    };

}