package br.com.move2.listeningdemo;

import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class LogsActivity extends AppCompatActivity {

    private String recordedFilePath;
    private String sampleFilePath;
    private String actualFilePath;
    private static final String TEXT_TO_SEARCH = "havaianas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        Button playRecord = (Button) findViewById(R.id.buttonPlayRecord);
        playRecord.setOnClickListener(playRecordedAudio);

        Button playSample = (Button) findViewById(R.id.buttonPlaySample);
        playSample.setOnClickListener(playSampleAudio);

        Button close = (Button) findViewById(R.id.buttonCloseLogs);
        close.setOnClickListener(backToHome);

        recordedFilePath = AudioRecorder.getRecordedFilePath(getBaseContext());
        File recordedFile = new File(recordedFilePath);
        String recordedContent;
        if(recordedFile.exists()){
            recordedFile.setReadable(true,false);
            recordedContent = "Arquivo de gravação "+recordedFile.getName() + " encontrado. Clique em \"Play Gravação\" para escutar";
        }else{
            recordedContent = "Nenhum arquivo gravado ainda. clique em \"Fechar\" e então em \"Gravar\"";
        }

        sampleFilePath = AudioRecorder.getSampleFilePath(getBaseContext());
        File sampleFile = new File(sampleFilePath);
        String sampleContent;
        if(sampleFile.exists()){
            recordedFile.setReadable(true,false);
            sampleContent = "Arquivo de amostra "+sampleFile.getName() + " encontrado. Clique em \"Play Amostra\" para escutar";
        }else{
            sampleContent = "Nenhum arquivo de amostra gravado ainda. clique em \"Fechar\" e então em \"Gravar Amostra\"";
        }

        TextView text1 = (TextView) findViewById(R.id.textLogs);
        text1.setText(text1.getText()+"\n"+recordedContent+"\n"+sampleContent);
    }

    private View.OnClickListener backToHome = new View.OnClickListener(){
        public void onClick(View v){
            finish();
        }
    };

    private View.OnClickListener playRecordedAudio = new View.OnClickListener(){
        public void onClick(View v){
            try{
                playShortAudioFileViaAudioTrack(recordedFilePath);
            }catch (IOException e){
                //>>>>>>
                Log.e(LogsActivity.class.getName(), "erro ao abrir arquivo para reprodução", e);
                //<<<<<<
            }
        }
    };

    private View.OnClickListener playSampleAudio = new View.OnClickListener(){
        public void onClick(View v){
            try{
                playShortAudioFileViaAudioTrack(sampleFilePath);
            }catch (IOException e){
                //>>>>>>
                Log.e(LogsActivity.class.getName(), "erro ao abrir arquivo para reprodução", e);
                //<<<<<<
            }
        }
    };
    private void playShortAudioFileViaAudioTrack(String filePath) throws IOException{
        if (filePath == null){
            return;
        }
        actualFilePath = filePath;

        SpeechRecognizer sr = SpeechRecognizer.createSpeechRecognizer(this.getBaseContext());
        sr.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                // process results here
                ArrayList<String> strList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(strList!=null && strList.size()>0){
                    TextView text1 = (TextView) findViewById(R.id.textLogs);
                    int countSearch = 0;
                    for (int i=0; i<strList.size(); i++){
                        String text = strList.get(i).toLowerCase();
                        int count=0;
                        while(text.contains(TEXT_TO_SEARCH)){
                            count ++;
                            text = text.substring(0,text.lastIndexOf(TEXT_TO_SEARCH));
                        }
                        countSearch = Math.max(countSearch,count);
                        //>>>>>>
                        Log.i(LogsActivity.class.getName(), strList.get(i));
                        //<<<<<<
                    }
                    String text = "Transcrição mais provável:\n" + strList.get(0);
                    text1.setText(text);
                    if(countSearch>0){
                        text1.setText("\n"+text1.getText()+"\nTotal máximo de "+TEXT_TO_SEARCH+": "+countSearch);
                    }
                }

            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                ArrayList<String> strList = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(strList!=null) {
                    for (int i = 0; i < strList.size(); i++) {
                        String text = strList.get(i).toLowerCase();
                        //>>>>>>
                        Log.i(LogsActivity.class.getName(), text);
                        //<<<<<<
                    }
                }
            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }

            @Override
            public void onReadyForSpeech(Bundle params) {
                //Reading the file..
                File file = new File(actualFilePath); // for ex. path= "/sdcard/samplesound.pcm" or "/sdcard/samplesound.wav"
                byte[] byteData = new byte[(int) file.length()];
                //>>>>>>
                Log.d(getClass().getName(), "file length: "+file.length());
                //<<<<<<

                FileInputStream in = null;
                try {
                    in = new FileInputStream( file );
                    in.read( byteData );
                    in.close();
                } catch (Exception e) {
                    //>>>>>>
                    Log.e(getClass().getName(), "erro ao abrir arquivo", e);
                    //<<<<<<
                }

                AudioTrack at = new AudioTrack(
                        AudioManager.STREAM_MUSIC, AudioRecorder.SAMPLE_RATE_IN_HZ,
                        AudioRecorder.CHANNEL_OUT_CONFIG, AudioRecorder.AUDIO_FORMAT,
                        AudioRecorder.BUFFER_SIZE, AudioTrack.MODE_STREAM);
                at.play();

                // Write the byte array to the track
                at.write(byteData, 0, byteData.length);
                at.stop();
                at.release();

            }

            @Override
            public void onBeginningOfSpeech() {
                //>>>>>>
                Log.d(getClass().getName(), "começou a falar");
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
                Log.d(getClass().getName(), "terminou de falar");
                //<<<<<<
            }

            @Override
            public void onError(int error) {

            }

        });
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // the following appears to be a requirement, but can be a "dummy" value
        //intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.move2");
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true);
        // define any other intent extras you want

        // this will start the speech recognizer service in the background
        // without starting a separate activity
        sr.startListening(intent);
    }
}
