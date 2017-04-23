package br.com.move2.listeningdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class InternetTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_test);

        WebView web1 = (WebView) findViewById(R.id.webTest);
        web1.setWebViewClient(new WebViewClient());
        web1.loadUrl("http://ec2-54-224-63-179.compute-1.amazonaws.com:8000/");

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
