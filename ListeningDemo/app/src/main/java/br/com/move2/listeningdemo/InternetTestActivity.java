package br.com.move2.listeningdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.File;

public class InternetTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_test);

        Button close = (Button) findViewById(R.id.buttonCloseInternetTest);
        close.setOnClickListener(backToHome);

        String content = "<html><body><h3>Processando... Aguardando resposta do servidor</h3></body></html>";
        WebView web1 = (WebView) findViewById(R.id.webTest);
        web1.setWebViewClient(new WebViewClient());
        web1.loadDataWithBaseURL(null, content, "text/html", CallPostURL.ENCODING, null);

        File recFile = new File(AudioRecorder.getRecordedFilePath(getBaseContext()));
        new CallPostURL(recFile, AudioRecorder.TYPE_RECORDED, web1, getBaseContext()).execute();
        //File sampFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"PetrobrasLivreEstou.pcm");
        //new CallPostURL(sampFile, AudioRecorder.TYPE_SAMPLE, web1, getBaseContext()).execute();


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
