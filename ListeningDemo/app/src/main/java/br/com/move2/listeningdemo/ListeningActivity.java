package br.com.move2.listeningdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class ListeningActivity extends NotifiableActivity {

    private static final int RECORD_LENGTH_IN_MILLIS = 30000;

    private boolean recording = false;
    private Intent recordService = new Intent();
    private WebViewListeningHandler webHandler;

    private Runnable startServiceRunnable = new Runnable() {
        @Override
        public void run() {
            startService(recordService);
            final Handler handler = new Handler();
            handler.postDelayed(stopServiceRunnable, RECORD_LENGTH_IN_MILLIS);
            //>>>>>>
            Log.i(getClass().getName(), "início");
            //<<<<<<
        }
    };

    private Runnable stopServiceRunnable = new Runnable() {
        @Override
        public void run() {
            stopService(recordService);
            //>>>>>>
            Log.i(getClass().getName(), "fim");
            //<<<<<<

            File recFile = new File(AudioRecorder.getRecordedFilePath(getBaseContext()));
            new CallPostURL(recFile, AudioRecorder.TYPE_RECORDED, webHandler, ListeningActivity.this)
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

        webHandler = new WebViewListeningHandler((WebView) findViewById(R.id.webCompare));

        findViewById(R.id.subtitleListening).setVisibility(View.GONE);
        findViewById(R.id.buttonStopListening).setVisibility(View.INVISIBLE);
        findViewById(R.id.subtitleCompare).setVisibility(View.GONE);
        findViewById(R.id.webCompare).setVisibility(View.GONE);

        recordService.setClass(this, AudioRecorderService.class);
        recordService.putExtra("AudioType", AudioRecorder.TYPE_RECORDED);

    }

    private View.OnClickListener startListening = new View.OnClickListener(){
        public void onClick(View v){
            findViewById(R.id.buttonStartListening).setVisibility(View.GONE);
            findViewById(R.id.buttonCloseCompare).setVisibility(View.GONE);

            findViewById(R.id.subtitleListening).setVisibility(View.VISIBLE);
            findViewById(R.id.buttonStopListening).setVisibility(View.VISIBLE);
            findViewById(R.id.subtitleCompare).setVisibility(View.VISIBLE);
            findViewById(R.id.webCompare).setVisibility(View.VISIBLE);

            webHandler.loadData("<html><body><p>Aguardando final da gravação.</p></body></html>");

            recording = true;

            startServiceRunnable.run();
            //>>>>>>
            Log.i(getClass().getName(), "aguardando log");
            //<<<<<<
        }
    };

    private View.OnClickListener stopListening = new View.OnClickListener(){
        public void onClick(View v){

            recording = false;
            Toast.makeText(ListeningActivity.this,"Aguardando final da última gravação",Toast.LENGTH_SHORT);

            findViewById(R.id.subtitleListening).setVisibility(View.GONE);
            findViewById(R.id.buttonStopListening).setVisibility(View.GONE);

            findViewById(R.id.buttonCloseCompare).setVisibility(View.VISIBLE);

        }
    };

    private View.OnClickListener backToHome = new View.OnClickListener(){
        public void onClick(View v){
            finish();
        }
    };

}
