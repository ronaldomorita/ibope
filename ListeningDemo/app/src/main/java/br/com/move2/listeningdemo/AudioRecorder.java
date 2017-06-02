package br.com.move2.listeningdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

class AudioRecorder {

    private enum State {INITIALIZING, READY, RECORDING, ERROR, STOPPED};

    static final int SOURCE = MediaRecorder.AudioSource.MIC;
    static final int SAMPLE_RATE_IN_HZ = 44100;
    static final int CHANNEL_IN_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    static final int CHANNEL_OUT_CONFIG = AudioFormat.CHANNEL_OUT_MONO;
    static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, CHANNEL_IN_CONFIG, AUDIO_FORMAT);

    static final int TYPE_RECORDED = 1;
    static final int TYPE_SAMPLE = 2;
    static final int NO_TYPE_DEFINED = 99;

    private static final String RECORDED_FILE_NAME = "recorded.pcm";
    private static final String SAMPLE_FILE_NAME = "sample.pcm";

    private State state;
    private int type;
    private AudioRecord audioRecorder = null;
    FileOutputStream os = null;
    private volatile Thread t = null;

    AudioRecorder(Context context, int typeToRecord)
    {
        //>>>>>>
        Log.d(getClass().getName(), "criando recorder");
        //<<<<<<
        state = State.INITIALIZING;
        type = typeToRecord;

        try {
            audioRecorder = new AudioRecord(SOURCE, SAMPLE_RATE_IN_HZ, CHANNEL_IN_CONFIG, AUDIO_FORMAT, BUFFER_SIZE);
            //>>>>>>
            Log.i(getClass().getName(), "audio recorder criado. state: "+audioRecorder.getState()+" BUFFER_SIZE: "+ BUFFER_SIZE);
            //<<<<<<

            String filePath;
            switch (type){
                case TYPE_SAMPLE:
                    filePath = getSampleFilePath(context);
                    break;
                case TYPE_RECORDED:
                default:
                    filePath = getRecordedFilePath(context);
            }
            //>>>>>>
            Log.d(getClass().getName(), "filePath: "+filePath);
            //<<<<<<
            os = new FileOutputStream(filePath);
            state = State.READY;

        } catch(Exception ex) {
            //>>>>>>
            Log.e(getClass().getName(), "erro ao criar recorder", ex);
            //<<<<<<
            state = State.ERROR;
        }
    }

    void start() {
        //>>>>>>
        Log.d(getClass().getName(), "iniciando recorder " + state);
        //<<<<<<

        if (state == State.READY || state == State.STOPPED) {
            try {
                //>>>>>>
                Log.i(getClass().getName(),
                        "audioRecorder: getAudioSource()="+audioRecorder.getAudioSource()
                        +"\ngetRecordingState()"+audioRecorder.getRecordingState()
                        +"\ngetState()"+audioRecorder.getState());
                //<<<<<<
                audioRecorder.startRecording();
                state = State.RECORDING;
                //>>>>>>
                Log.d(getClass().getName(), "audioRecorder iniciado");
                //<<<<<<
                t = new Thread() {
                    public void run()
                    {
                        //>>>>>>
                        Log.d(getClass().getName(), "iniciando thread");
                        //<<<<<<
                        writeAudioDataToFile();
                    }
                };

                t.setPriority(Thread.MAX_PRIORITY);

                t.start();
                //>>>>>>
                Log.d(getClass().getName(), "recorder iniciado");
                //<<<<<<
            }catch (Exception ex){
                //>>>>>>
                Log.e(getClass().getName(), "iniciando recorder exceção:",ex);
                //<<<<<<
            }

        } else {
            //>>>>>>
            Log.e(getClass().getName(), "iniciando recorder estado inválido: "+state);
            //<<<<<<
            state = State.ERROR;
        }

    }

    private void writeAudioDataToFile() {
        //>>>>>>
        Log.d(getClass().getName(), "iniciando gravação do arquivo");
        //<<<<<<
        // Write the output audio in byte
        byte[] bData = new byte[BUFFER_SIZE];
        while (getState()==State.RECORDING) {
            // gets the voice output from microphone to byte format

            audioRecorder.read(bData, 0, BUFFER_SIZE);
            try {
                os.write(bData, 0, BUFFER_SIZE);
            } catch (IOException e) {
                //>>>>>>
                Log.e(getClass().getName(), "erro ao abrir arquivo", e);
                //<<<<<<
            }
        }

        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            //>>>>>>
            Log.i(getClass().getName(), "text");
            //<<<<<<
        }
    }

    void stop() {
        //>>>>>>
        Log.d(getClass().getName(), "parando recorder");
        //<<<<<<

        if (state==State.RECORDING)  {
            audioRecorder.stop();
            audioRecorder.release();
            Thread t1 = t;
            t=null;
            t1.interrupt();
            state = State.STOPPED;
            //>>>>>>
            Log.i(getClass().getName(), "recorder parado");
            //<<<<<<

        } else {
            //>>>>>>
            Log.e(getClass().getName(), "parando recorder estado inválido " + state);
            //<<<<<<
            state = State.ERROR;
        }
    }

    private State getState()  {
        return state;
    }

    static String getRecordedFilePath(Context context){
        return
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),RECORDED_FILE_NAME).getPath();

                //Environment.getExternalStorageDirectory().getPath() +
                //context.getFilesDir().getPath() +
                //File.separator + RECORDED_FILE_NAME;
    }

    static String getSampleFilePath(Context context){
        return
                new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),SAMPLE_FILE_NAME).getPath();

                //Environment.getExternalStorageDirectory().getPath() +
                //context.getFilesDir().getPath() +
                //File.separator + SAMPLE_FILE_NAME;
    }



}
