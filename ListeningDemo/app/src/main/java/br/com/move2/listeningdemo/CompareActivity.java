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

public class CompareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        Button close = (Button) findViewById(R.id.buttonCloseCompare);
        close.setOnClickListener(backToHome);

        TextView text = (TextView) findViewById(R.id.textCompare);
        text.setText(text.getText()+"aqui vai o resultado da comparação");
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
