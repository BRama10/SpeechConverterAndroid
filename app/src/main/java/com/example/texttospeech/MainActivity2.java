package com.example.texttospeech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity2 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private EditText editText;
    private EditText translatedText;
    private ImageView micButton;
    private Button toggleButton;
    private Button translateButton;
    private Translator englishGermanTranslator;
    private TextToSpeech textToSpeech;
    private Button speakText;
    private Spinner spinner;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }

        editText = findViewById(R.id.text);
        micButton = findViewById(R.id.button);
        toggleButton = findViewById(R.id.toggleButton);
        translatedText = findViewById(R.id.translatedText);
        translateButton = findViewById(R.id.translateBtn);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speakText = findViewById(R.id.speakBtn);
        spinner = findViewById(R.id.spinner);

        List<String> langs = new ArrayList<String>();
        langs.add("GERMAN");
        langs.add("SPANISH");
        langs.add("HINDI");
        langs.add("MANDARIN");
        langs.add("FRENCH");
        langs.add("URDU");

        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, langs);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(new Locale("fr"));
                }
            }
        });

        TranslatorOptions options =
                new TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.GERMAN)
                        .build();
        englishGermanTranslator = Translation.getClient(options);

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                editText.setText("");
                editText.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setImageResource(R.drawable.ic_mic_black_off);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                editText.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int status = translateButton.getVisibility();

                //Toast.makeText(MainActivity.this, "Phase v1 Passed", Toast.LENGTH_SHORT).show();

                if(status == View.VISIBLE) {
                    editText.setVisibility(View.INVISIBLE);
                    translatedText.setVisibility(View.INVISIBLE);
                    micButton.setVisibility(View.INVISIBLE);
                    translateButton.setVisibility(View.INVISIBLE);
                    speakText.setVisibility(View.INVISIBLE);
                    spinner.setVisibility(View.INVISIBLE);
                }
                else {
                    editText.setVisibility(View.VISIBLE);
                    translatedText.setVisibility(View.VISIBLE);
                    micButton.setVisibility(View.VISIBLE);
                    translateButton.setVisibility(View.VISIBLE);
                    speakText.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.VISIBLE);
                }


                //Toast.makeText(MainActivity.this, "Phase v2 Passed", Toast.LENGTH_SHORT).show();


            }
        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string = editText.getText().toString();
                downloadModal(string);
            }
        });

        speakText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textToSpeech.speak(translatedText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });


        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    speechRecognizer.stopListening();
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    micButton.setImageResource(R.drawable.ic_mic_black_24dp);
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });


    }

    private void downloadModal(final String input) {
        DownloadConditions conditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();
        englishGermanTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener(
                        new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                Toast.makeText(MainActivity2.this, "Please wait language modal is being downloaded.", Toast.LENGTH_SHORT).show();
                                Toast.makeText(MainActivity2.this, "Object override {not passing through Void}", Toast.LENGTH_SHORT).show();

                                // calling method to translate our entered text.
                                translateLanguage(input);
                            }

                            public void onSuccess(Void v) {
                                // Model downloaded successfully. Okay to start translating.
                                // (Set a flag, unhide the translation UI, etc.)
                                Toast.makeText(MainActivity2.this, "Please wait language modal is being downloaded.", Toast.LENGTH_SHORT).show();

                                // calling method to translate our entered text.
                                translateLanguage(input);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be downloaded or other internal error.
                                // ...
                                Toast.makeText(MainActivity2.this, "Fail to download modal", Toast.LENGTH_SHORT).show();
                            }
                        });
    }

    private void translateLanguage(String text) {
        englishGermanTranslator.translate(text)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@NonNull String translatedText1) {
                                // Translation successful.
                                translatedText.setText(translatedText1);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Error.
                                // ...
                                translatedText.setText("Error");
                            }
                        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(String.valueOf(spinner.getSelectedItem()) == "SPANISH") {
            Intent intent= new Intent(MainActivity2.this,MainActivity.class);
            startActivity(intent);
        }
        if(String.valueOf(spinner.getSelectedItem()) == "FRENCH") {
            Intent intent= new Intent(MainActivity2.this,MainActivity3.class);
            startActivity(intent);
        }
        else if(String.valueOf(spinner.getSelectedItem()) == "MANDARIN") {
            Intent intent= new Intent(MainActivity2.this,MainActivity4.class);
            startActivity(intent);
        }
        else if(String.valueOf(spinner.getSelectedItem()) == "URDU") {
            Intent intent= new Intent(MainActivity2.this,MainActivity5.class);
            startActivity(intent);
        }
        else if(String.valueOf(spinner.getSelectedItem()) == "HINDI") {
            Intent intent= new Intent(MainActivity2.this,MainActivity6.class);
            startActivity(intent);
        }


    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
