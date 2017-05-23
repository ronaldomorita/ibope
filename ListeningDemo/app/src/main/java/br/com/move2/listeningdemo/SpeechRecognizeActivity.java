package br.com.move2.listeningdemo;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpeechRecognizeActivity extends NotifiableActivity {

    private SpeechRecognizer sr = null;

    private TextView textContainer;
    private Button startButton;
    private Button stopButton;

    private static boolean recording = false;

    private static final Map<List<List<String>>,String> TEXT_TO_SEARCH = new HashMap<>();
    static{
        List<String> synonima;
        List<List<String>> temp;

        temp = new ArrayList<>();
        synonima = new ArrayList<>();
        synonima.add("final de semana");
        synonima.add("fim de semana");
        //---
        temp.add(synonima);
        synonima = new ArrayList<>();
        synonima.add("viagem");
        temp.add(synonima);
        //---
        TEXT_TO_SEARCH.put(temp,"Vai viajar no final de semana, consulte aqui os melhores hoteis");

        temp = new ArrayList<>();
        synonima = new ArrayList<>();
        synonima.add("churrasco");
        synonima.add("churras");
        //---
        temp.add(synonima);
        synonima = new ArrayList<>();
        synonima.add("futebol");
        synonima.add("bola");
        temp.add(synonima);
        //---
        TEXT_TO_SEARCH.put(temp,"Tem promoção de Brahma no churrasco com a galera para assistir futebol, confira aqui");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        textContainer = (TextView) findViewById(R.id.textSpeech);
        startButton = (Button) findViewById(R.id.buttonStartSpeech);
        stopButton = (Button) findViewById(R.id.buttonStopSpeech);

        textContainer.setText("Aguardando início do reconhecimento da fala");

        startButton.setOnClickListener(startSpeech);

        stopButton.setOnClickListener(stopSpeech);

        ((recording)? startButton : stopButton).setVisibility(View.GONE);

        findViewById(R.id.buttonCloseSpeech).setOnClickListener(backToHome);
    }

    private View.OnClickListener startSpeech = new View.OnClickListener(){
        public void onClick(View v){
            startButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.VISIBLE);

            textContainer.setText("Reconhecimento iniciado");

            recording = true;
            startRecognizer();
        }
    };

    private View.OnClickListener stopSpeech = new View.OnClickListener(){
        public void onClick(View v){
            startButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.GONE);

            textContainer.setText(textContainer.getText()+"\nReconhecimento finalizado");

            recording = false;
            sr.stopListening();
        }
    };

    private View.OnClickListener backToHome = new View.OnClickListener(){
        public void onClick(View v){
            if(sr != null){
                sr.stopListening();
                sr.destroy();
            }

            finish();
        }
    };


    private void startRecognizer() {

        if(sr!=null){
            sr.stopListening();
            sr.destroy();
        }
        if (!recording){
            return;
        }

        sr = SpeechRecognizer.createSpeechRecognizer(this.getBaseContext());
        sr.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onEvent(int eventType, Bundle params) {

            }

            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                //>>>>>>
                Log.w(getClass().getName(), "começou a falar");
                //<<<<<<
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {
                //>>>>>>
                Log.w(getClass().getName(), "terminou de falar");
                //<<<<<<
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                List<String> recognizedList = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(recognizedList!=null) {
                    for (String recognized : recognizedList) {
                        //>>>>>>
                        Log.w(SpeechRecognizeActivity.class.getName(), "partial"+recognized);
                        //<<<<<<
                    }
                }
            }

            @Override
            public void onResults(Bundle results) {
                List<String> recognizedList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(recognizedList==null) {
                    recognizedList = new ArrayList<>();
                }

                List<String> offers = new ArrayList<>();

                for (String recognized : recognizedList) {
                    //>>>>>>
                    Log.w(SpeechRecognizeActivity.class.getName(), recognized);
                    //<<<<<<
                    recognized = recognized.toLowerCase();

                    Set<List<List<String>>> setToSearch = TEXT_TO_SEARCH.keySet();
                    for (List<List<String>> listToSearch : setToSearch) {

                        boolean found = true;
                        for (List<String> synonimaToSearch : listToSearch) {
                            boolean synFound = false;
                            for (String textToSearch : synonimaToSearch) {
                                synFound |= recognized.contains(textToSearch);
                            }
                            found &= synFound;
                        }
                        if (found && !offers.contains(TEXT_TO_SEARCH.get(listToSearch))) {
                            offers.add(TEXT_TO_SEARCH.get(listToSearch));
                        }
                    }
                }

                for (String offer : offers) {
                    generateNotification(RECOGNIZER_OFFER_NOTIFICATION_ID,offer);
                }
                if(recognizedList.size()>0){
                    textContainer.setText("Transcrição mais provável:\n"+recognizedList.get(0));
                }
                startRecognizer();

            }

            @Override
            public void onError(int error) {
                //>>>>>>
                Log.w(getClass().getName(), "onError: "+ error);
                //<<<<<<
                startRecognizer();
            }

        });

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,false);
        sr.startListening(intent);

    }

}
