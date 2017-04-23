package br.com.move2.listeningdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AudioRecorderService extends Service {

    private static AudioRecorder recorder = null;
    public AudioRecorderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int audioType = intent.getIntExtra("AudioType",AudioRecorder.NO_TYPE_DEFINED);

        //>>>>>>
        Log.d(getClass().getName(), "construindo o serviço. audioType="+audioType);
        //<<<<<<
        recorder = new AudioRecorder(getBaseContext(), audioType);
        recorder.start();
        //>>>>>>
        Log.d(getClass().getName(), "serviço iniciado");
        //<<<<<<

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        //>>>>>>
        Log.d(getClass().getName(), "parando serviço");
        //<<<<<<
        recorder.stop();
        //>>>>>>
        Log.d(getClass().getName(), "serviço parado");
        //<<<<<<

        super.onDestroy();
    }
}
