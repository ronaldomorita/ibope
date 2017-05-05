package br.com.move2.listeningdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.File;

public class ListeningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listening);

        Button stop = (Button) findViewById(R.id.buttonStopListening);
        stop.setOnClickListener(stopListening);

        Button close = (Button) findViewById(R.id.buttonCloseCompare);
        close.setOnClickListener(backToHome);

        String content = "<html><body><p>Aguardando final da gravação.</p></body></html>";
        WebView web1 = (WebView) findViewById(R.id.webCompare);
        web1.setWebViewClient(new WebViewClient());
        web1.loadDataWithBaseURL(null, content, "text/html", CallPostURL.ENCODING, null);

    }

    private View.OnClickListener stopListening = new View.OnClickListener(){
        public void onClick(View v){
            Intent service = new Intent();
            service.setClass(ListeningActivity.this, AudioRecorderService.class);
            stopService(service);

            findViewById(R.id.subtitleListening).setVisibility(View.GONE);
            findViewById(R.id.buttonStopListening).setVisibility(View.GONE);

            String content = "<html><body><h3>Processando... Aguardando resposta do servidor</h3></body></html>";
            WebView web1 = (WebView) findViewById(R.id.webCompare);
            web1.loadDataWithBaseURL(null, content, "text/html", CallPostURL.ENCODING, null);

            findViewById(R.id.buttonCloseCompare).setVisibility(View.VISIBLE);

            File recFile = new File(AudioRecorder.getRecordedFilePath(getBaseContext()));
            new CallPostURL(recFile, AudioRecorder.TYPE_RECORDED, web1, getBaseContext()).execute();
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
