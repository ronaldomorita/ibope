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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CompareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        Button close = (Button) findViewById(R.id.buttonCloseCompare);
        close.setOnClickListener(backToHome);

        File recFile = new File(AudioRecorder.getRecordedFilePath(getBaseContext()));
        String content = "<html><body><p>No Content</p></body></html>";
        try{
            content = new CallPostURL().postAudio(recFile, AudioRecorder.TYPE_RECORDED);
        }catch (IOException e){
            //>>>>>>
            Log.e(getClass().getName(), "erro ao chamar upload de arquivo", e);
            //<<<<<<
        }

        WebView web1 = (WebView) findViewById(R.id.webCompare);
        web1.setWebViewClient(new WebViewClient());
        web1.loadDataWithBaseURL(null, content, "text/html", CallPostURL.ENCODING, null);
    }

    private View.OnClickListener backToHome = new View.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(CompareActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

}
