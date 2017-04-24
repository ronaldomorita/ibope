package br.com.move2.listeningdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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

        Button close = (Button) findViewById(R.id.buttonSendSample);
        close.setOnClickListener(saveSampleAndbackToHome);

    }

    private View.OnClickListener saveSampleAndbackToHome = new View.OnClickListener(){
        public void onClick(View v){
            //>>>>>>
            Log.i(getClass().getName(), "iniciando cópia do arquivo");
            //<<<<<<

            EditText sampleName = (EditText) findViewById(R.id.textSampleName);
            copySampleFile(sampleName.getText().toString());

            Intent intent = new Intent();
            intent.setClass(SendSampleActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private void copySampleFile(String sampleName) {
        String basePath = AudioRecorder.getSampleFilePath(getBaseContext());
        int li = basePath.lastIndexOf(File.separator);
        String newName = basePath.substring(0,li+1)+sampleName+".pcm";
        //>>>>>>
        Log.i(getClass().getName(), "iniciando cópia do arquivo "+sampleName+".pcm");
        Log.i(getClass().getName(), "input "+basePath);
        Log.i(getClass().getName(), "output "+newName);
        //<<<<<<
        FileInputStream inFile = null;
        FileOutputStream outFile = null;
        try{
            inFile = new FileInputStream(new File(basePath));
            outFile = new FileOutputStream(new File(newName));
            byte[] buf  =  new byte[1024];
            int bytesRead;
            while ((bytesRead = inFile.read(buf)) > 0) {
                outFile.write(buf, 0, bytesRead);
            }
            //>>>>>>
            Log.i(getClass().getName(), "Arquivo "+sampleName+" copiado com sucesso!");
            //<<<<<<
        }catch (IOException ex){
            //>>>>>>
            Log.e(getClass().getName(), "Erro ao coipar arquivo "+sampleName, ex);
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
