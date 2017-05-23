package br.com.move2.listeningdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button listen = (Button) findViewById(R.id.buttonRecord);
        listen.setOnClickListener(startListening);

        Button sample = (Button) findViewById(R.id.buttonSample);
        sample.setOnClickListener(startSample);

        Button speech = (Button) findViewById(R.id.buttonSpeech);
        speech.setOnClickListener(startSpeechRecognize);

        Button logs = (Button) findViewById(R.id.buttonLogs);
        logs.setOnClickListener(goToLogs);

        Button internet = (Button) findViewById(R.id.buttonInternet);
        internet.setOnClickListener(goToInternetTest);

        Button exit = (Button) findViewById(R.id.buttonExit);
        exit.setOnClickListener(exitApp);
    }

    private View.OnClickListener startListening = new View.OnClickListener(){
        public void onClick(View v){
            Intent activity = new Intent();
            activity.setClass(MainActivity.this, ListeningActivity.class);
            startActivity(activity);
        }
    };

    private View.OnClickListener startSample = new View.OnClickListener(){
        public void onClick(View v){
            Intent service = new Intent();
            service.setClass(MainActivity.this, AudioRecorderService.class);
            service.putExtra("AudioType", AudioRecorder.TYPE_SAMPLE);
            startService(service);

            Intent activity = new Intent();
            activity.setClass(MainActivity.this, SampleActivity.class);
            startActivity(activity);
        }
    };

    private View.OnClickListener startSpeechRecognize = new View.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, SpeechRecognizeActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener goToLogs = new View.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LogsActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener goToInternetTest = new View.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, InternetTestActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener exitApp = new View.OnClickListener(){
        public void onClick(View v){
            finish();
            System.exit(0);
        }
    };
}
