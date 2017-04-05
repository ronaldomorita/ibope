package br.com.move2.listeningdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class ListeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);

        Button stop = (Button) findViewById(R.id.buttonStop);
        stop.setOnClickListener(stopListening);
    }

    private View.OnClickListener stopListening = new View.OnClickListener(){
        public void onClick(View v){
            Intent service = new Intent();
            service.setClass(ListeningActivity.this, AudioRecorderService.class);
            stopService(service);

            Intent intent = new Intent();
            intent.setClass(ListeningActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

}
