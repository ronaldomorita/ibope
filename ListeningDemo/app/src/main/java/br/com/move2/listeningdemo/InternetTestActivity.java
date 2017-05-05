package br.com.move2.listeningdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class InternetTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_test);

        File recFile = new File(AudioRecorder.getRecordedFilePath(getBaseContext()));
        //File sampFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"PetrobrasLivreEstou.pcm");
        String content = "<html><body><p>No Content</p></body></html>";
        try{
            content = new CallPostURL().postAudio(recFile, AudioRecorder.TYPE_RECORDED);
            //content = new CallPostURL().postAudio(sampFile, AudioRecorder.TYPE_SAMPLE);
        }catch (IOException e){
            //>>>>>>
            Log.e(getClass().getName(), "erro ao chamar upload de arquivo", e);
            //<<<<<<
        }

        WebView web1 = (WebView) findViewById(R.id.webTest);
        web1.setWebViewClient(new WebViewClient());
        web1.loadDataWithBaseURL(null, content, "text/html", CallPostURL.ENCODING, null);

        Button close = (Button) findViewById(R.id.buttonCloseInternetTest);
        close.setOnClickListener(backToHome);
    }

    private View.OnClickListener stopListening = new View.OnClickListener(){
        public void onClick(View v){
            Intent service = new Intent();
            service.setClass(InternetTestActivity.this, AudioRecorderService.class);
            stopService(service);

            Intent intent = new Intent();
            intent.setClass(InternetTestActivity.this, CompareActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener backToHome = new View.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(InternetTestActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

}
