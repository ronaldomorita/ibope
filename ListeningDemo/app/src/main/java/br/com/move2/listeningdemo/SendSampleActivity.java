package br.com.move2.listeningdemo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SendSampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sample);

        Button send = (Button) findViewById(R.id.buttonSendSample);
        send.setOnClickListener(sendSample);

        Button close = (Button) findViewById(R.id.buttonCloseSendSample);
        close.setOnClickListener(backToHome);

    }

    private View.OnClickListener sendSample = new View.OnClickListener(){
        public void onClick(View v){
            findViewById(R.id.textLabelSampleName).setVisibility(View.GONE);

            EditText textSampleName = (EditText) findViewById(R.id.textSampleName);
            textSampleName.setVisibility(View.GONE);

            findViewById(R.id.buttonSendSample).setVisibility(View.GONE);

            WebView web = (WebView) findViewById(R.id.webSendSample);
            web.setVisibility(View.VISIBLE);

            findViewById(R.id.buttonCloseSendSample).setVisibility(View.VISIBLE);

            String sampleName = textSampleName.getText().toString();
            String basePath = AudioRecorder.getSampleFilePath(getBaseContext());
            int li = basePath.lastIndexOf(File.separator);
            String newPath = basePath.substring(0,li+1)+sampleName+".pcm";
            copySampleFile(basePath,newPath);

            File sampFile = new File(newPath);
            String content = "<html><body><p>No Content</p></body></html>";
            try{
                content = new CallPostURL().postAudio(sampFile, AudioRecorder.TYPE_SAMPLE);
            }catch (IOException e){
                //>>>>>>
                Log.e(getClass().getName(), "erro ao chamar upload de arquivo", e);
                //<<<<<<
            }

            web.setWebViewClient(new WebViewClient());
            web.loadDataWithBaseURL(null, content, "text/html", CallPostURL.ENCODING, null);
       }
    };

    private View.OnClickListener backToHome = new View.OnClickListener(){
        public void onClick(View v){
            Intent intent = new Intent();
            intent.setClass(SendSampleActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private void copySampleFile(String basePath, String newPath) {
        //>>>>>>
        Log.i(getClass().getName(), "iniciando cópia do arquivo");
        Log.i(getClass().getName(), "input "+basePath);
        Log.i(getClass().getName(), "output "+newPath);
        //<<<<<<
        FileInputStream inFile = null;
        FileOutputStream outFile = null;
        try{
            inFile = new FileInputStream(new File(basePath));
            outFile = new FileOutputStream(new File(newPath));
            byte[] buf  =  new byte[1024];
            int bytesRead;
            while ((bytesRead = inFile.read(buf)) > 0) {
                outFile.write(buf, 0, bytesRead);
            }
            //>>>>>>
            Log.i(getClass().getName(), "Arquivo "+newPath+" copiado com sucesso!");
            //<<<<<<
        }catch (IOException ex){
            //>>>>>>
            Log.e(getClass().getName(), "Erro ao coipar arquivo "+newPath, ex);
            //<<<<<<
            ex.printStackTrace();
        }finally {
            if(inFile!=null){
                try{
                    inFile.close();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
            if(outFile!=null){
                try{
                    outFile.close();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        }
    }

}
