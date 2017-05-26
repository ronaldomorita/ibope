package br.com.move2.listeningdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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

    class Keyword{
        List<String> synonyms = new ArrayList<>();

        public void addSynonym(String synonym){
            synonyms.add(synonym);
        }

        public List<String> getSynonyms(){
            return synonyms;
        }
    }
    class Offer{
        int offerId;
        String offerContent;

        Offer(int offerId, String offerContent){
            this.offerId = offerId;
            this.offerContent = offerContent;
        }

        public int getOfferId(){
            return offerId;
        }

        public String getOfferContent() {
            return offerContent;
        }
    }
    private static final Map<List<Keyword>,Offer> TEXT_TO_SEARCH = new HashMap<>();
    private void loadOffers(String offerJson) throws JSONException {

        JSONArray json = new JSONArray(offerJson);
        for (int i=0; i<json.length(); i++){
            int offerId = ((JSONObject) json.get(i)).getInt("offer_id");
            String offerContent = ((JSONObject) json.get(i)).getString("content");
            Offer offer = new Offer(offerId,offerContent);

            List<Keyword> keywords = new ArrayList<>();
            JSONArray keywordsJson = ((JSONObject) json.get(i)).getJSONArray("keywords");
            for (int j=0; j<keywordsJson.length(); j++){
                Keyword kw = new Keyword();
                JSONArray synonymsJson = ((JSONArray) keywordsJson.get(j));
                for (int k=0; k<synonymsJson.length(); k++){
                    kw.addSynonym(synonymsJson.getString(k));
                }
                keywords.add(kw);
            }
            TEXT_TO_SEARCH.put(keywords,offer);
        }

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
        if(!recording) {
            try {
                startButton.setVisibility(View.GONE);
                stopButton.setVisibility(View.GONE);
                String offerJson = getOffersJson();
                loadOffers(offerJson);
                startButton.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                //>>>>>>
                Log.e(getClass().getName(), "Erro ao carregar ofertas de keyword", e);
                //<<<<<<
            }
        }else{
            startButton.setVisibility(View.GONE);
        }

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

                List<Offer> offers = new ArrayList<>();

                for (String recognized : recognizedList) {
                    //>>>>>>
                    Log.w(SpeechRecognizeActivity.class.getName(), recognized);
                    //<<<<<<
                    recognized = recognized.toLowerCase();

                    Set<List<Keyword>> setToSearch = TEXT_TO_SEARCH.keySet();
                    for (List<Keyword> listToSearch : setToSearch) {

                        boolean found = true;
                        for (Keyword synonymsToSearch : listToSearch) {
                            boolean synFound = false;
                            for (String textToSearch : synonymsToSearch.getSynonyms()) {
                                synFound |= recognized.contains(textToSearch);
                            }
                            found &= synFound;
                        }
                        if (found && !offers.contains(TEXT_TO_SEARCH.get(listToSearch))) {
                            offers.add(TEXT_TO_SEARCH.get(listToSearch));
                        }
                    }
                }

                for (Offer offer : offers) {
                    generateNotification(offer.getOfferId(),offer.getOfferContent());
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


    public String getOffersJson() throws IOException {
        String url = CallPostURL.OFFERS_URL;

        HttpURLConnection conn = null;
        InputStream inResponse = null;

        StringBuffer bufferResponse = new StringBuffer();

        try{
            // permit connections in main thread
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            // start connection and define multipart
            conn = (HttpURLConnection) ( new URL(url)).openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            //conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.connect();
            //>>>>>>
            Log.i(getClass().getName(), "enviado para "+url);
            //<<<<<<

            // reads response
            inResponse = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inResponse,CallPostURL.ENCODING));
            String line;
            while ((line = reader.readLine()) != null) {
                bufferResponse.append(line);
            }
            //>>>>>>
            Log.i(getClass().getName(), "recebido");
            //<<<<<<

        }catch (Exception e){
            //>>>>>>
            Log.e(getClass().getName(), "Erro ao carregar ofertas de keyword", e);
            //<<<<<<
            return "[{\"error\": \"Erro ao carregar ofertas de keyword: " + e.getMessage() + "\"";
        }finally {
            if (conn != null) conn.disconnect();
            if (inResponse !=null) inResponse.close();
        }
        //>>>>>>
        Log.w(getClass().getName(), "conteúdo JSON recebido: "+bufferResponse.toString());
        //<<<<<<
        return bufferResponse.toString();
    }

}
