package br.com.move2.listeningdemo;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.File;

public class InternetTestActivity extends NotifiableActivity {

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
        new CallPostURL(recFile, AudioRecorder.TYPE_RECORDED, web1, this).execute();
        //File sampFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"PetrobrasLivreEstou.pcm");
        //new CallPostURL(sampFile, AudioRecorder.TYPE_SAMPLE, web1, getBaseContext()).execute();

    }

    private View.OnClickListener backToHome = new View.OnClickListener(){
        public void onClick(View v){
            finish();
        }
    };


}
