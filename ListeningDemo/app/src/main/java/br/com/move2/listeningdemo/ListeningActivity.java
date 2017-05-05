package br.com.move2.listeningdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.File;

public class ListeningActivity extends AppCompatActivity {

    private boolean recording = false;
    private Intent recordService = new Intent();

    Runnable startServiceRunnable = new Runnable() {
        @Override
        public void run() {
            startService(recordService);
            final Handler handler = new Handler();
            handler.postDelayed(stopServiceRunnable, 6000);
            //>>>>>>
            Log.i(getClass().getName(), "início");
            //<<<<<<
        }
    };

    Runnable stopServiceRunnable = new Runnable() {
        @Override
        public void run() {
            stopService(recordService);
            //>>>>>>
            Log.i(getClass().getName(), "fim");
            //<<<<<<

            File recFile = new File(AudioRecorder.getRecordedFilePath(getBaseContext()));
            new CallPostURL(recFile, AudioRecorder.TYPE_RECORDED, (WebView) findViewById(R.id.webCompare), getBaseContext())
                    .appendRestartScript((recording)?startServiceRunnable:null).execute();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);

        Button start = (Button) findViewById(R.id.buttonStartListening);
        start.setOnClickListener(startListening);

        Button stop = (Button) findViewById(R.id.buttonStopListening);
        stop.setOnClickListener(stopListening);

        Button close = (Button) findViewById(R.id.buttonCloseCompare);
        close.setOnClickListener(backToHome);

        findViewById(R.id.subtitleListening).setVisibility(View.GONE);
        findViewById(R.id.buttonStopListening).setVisibility(View.INVISIBLE);
        findViewById(R.id.subtitleCompare).setVisibility(View.GONE);
        findViewById(R.id.webCompare).setVisibility(View.GONE);

        recordService.setClass(ListeningActivity.this, AudioRecorderService.class);
        recordService.putExtra("AudioType", AudioRecorder.TYPE_RECORDED);

    }

    private View.OnClickListener startListening = new View.OnClickListener(){
        public void onClick(View v){
            //startService(recordService);

            findViewById(R.id.buttonStartListening).setVisibility(View.GONE);
            findViewById(R.id.buttonCloseCompare).setVisibility(View.GONE);

            findViewById(R.id.subtitleListening).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonStopListening).setVisibility(View.VISIBLE);
            findViewById(R.id.subtitleCompare).setVisibility(View.VISIBLE);

            String content = "<html><body><p>Aguardando final da gravação.</p></body></html>";
            WebView web1 = (WebView) findViewById(R.id.webCompare);
            web1.setVisibility(View.VISIBLE);
            web1.setWebViewClient(new WebViewClient());
            web1.loadDataWithBaseURL(null, content, "text/html", CallPostURL.ENCODING, null);

            recording = true;

            startServiceRunnable.run();
            //>>>>>>
            Log.i(getClass().getName(), "aguardando log");
            //<<<<<<

        }
    };

    private View.OnClickListener stopListening = new View.OnClickListener(){
        public void onClick(View v){

            String content = "<html><body><h3>Aguardando final da última gravação</h3></body></html>";
            WebView web1 = (WebView) findViewById(R.id.webCompare);
            web1.loadDataWithBaseURL(null, content, "text/html", CallPostURL.ENCODING, null);

            recording = false;

            //Intent recordService = new Intent();
            //recordService.setClass(ListeningActivity.this, AudioRecorderService.class);
            //stopService(recordService);

            findViewById(R.id.subtitleListening).setVisibility(View.GONE);
            findViewById(R.id.buttonStopListening).setVisibility(View.GONE);

            findViewById(R.id.buttonCloseCompare).setVisibility(View.VISIBLE);

        }
    };

    private View.OnClickListener backToHome = new View.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(ListeningActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

}
