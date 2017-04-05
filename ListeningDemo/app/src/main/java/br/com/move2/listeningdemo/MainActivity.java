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

        Button logs = (Button) findViewById(R.id.buttonLogs);
        logs.setOnClickListener(goToLogs);
    }

    private View.OnClickListener startListening = new View.OnClickListener(){
        public void onClick(View v){
            Intent service = new Intent();
            service.setClass(MainActivity.this, AudioRecorderService.class);
            startService(service);

            Intent activity = new Intent();
            activity.setClass(MainActivity.this, ListeningActivity.class);
            startActivity(activity);

            finish();
        }
    };

    private View.OnClickListener goToLogs = new View.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, LogsActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
