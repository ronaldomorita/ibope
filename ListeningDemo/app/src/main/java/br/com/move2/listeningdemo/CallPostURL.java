package br.com.move2.listeningdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPOutputStream;


public class CallPostURL extends AsyncTask<String, String, String> {


    private static final String BOUNDARY_APPEND = "pac";
    private static final String CRLF = "\r\n"; // Line separator required by multipart/form-data.
    private static final String DELIMITER = "--";

    public static final String RECORDED_URL = "http://ec2-54-224-63-179.compute-1.amazonaws.com/";
    public static final String SAMPLE_URL = RECORDED_URL + "loadsample";
    public static final String OFFERS_URL = RECORDED_URL + "loadoffers";
    private static final String RECORDED_PARAMNAME = "recorded";
    private static final String SAMPLE_PARAMNAME = "sample";
    private static final int GZIP_BUFFER_SIZE = 1024;

    public static final String ENCODING = "utf-8";
    public static final String NOTIFIC_BEGIN = "<!-- notific: ";
    public static final String NOTIFIC_END = " notific-end -->";

    private File fileToUpload;
    int audioType;
    private Context context;

    private WebView webForResult;
    private WebViewListeningHandler webHandlerForResult;
    private Runnable restartScript;

    private CallPostURL(File fileToUpload, int audioType, Context context) {
        this.fileToUpload = fileToUpload;
        this.audioType = audioType;
        this.context = context;
        this.webForResult = null;
        this.webHandlerForResult = null;
        this.restartScript = null;
    }

    public CallPostURL(File fileToUpload, int audioType, WebView webForResult, Context context) {
        this(fileToUpload, audioType, context);
        this.webForResult = webForResult;
    }

    public CallPostURL(File fileToUpload, int audioType, WebViewListeningHandler webHandlerForResult, Context context) {
        this(fileToUpload, audioType, context);
        this.webHandlerForResult = webHandlerForResult;
    }


    public CallPostURL appendRestartScript(Runnable restartScript){
        this.restartScript = restartScript;
        return this;
    }

    @Override
    protected void onPreExecute() {
        //>>>>>>
        Log.i(getClass().getName(), "onPreExecute()");
        //<<<<<<

        // zip the file to upload
        String gzipPath = compressFileToGzip();
        fileToUpload = new File(gzipPath);

        //  restart recording if available
        if(restartScript!=null){
            restartScript.run();
            //>>>>>>
            Log.i(getClass().getName(), "reinício");
            //<<<<<<
        }
    }

    @Override
    protected String doInBackground(String... params) {
        //>>>>>>
        Log.i(getClass().getName(), "doInBackground()");
        //<<<<<<

        try {
            return postAudio();
        } catch (Exception e) {
            //>>>>>>
            Log.e(getClass().getName(), "erro ao chamar upload de arquivo", e);
            //<<<<<<
            return "<html><body><h3><font color=\"red\">Erro ao fazer upload do arquivo: " + e.getMessage() + "</font></h3></body></html>";
        }
    }


    @Override
    protected void onPostExecute(String result) {
        //>>>>>>
        Log.i(getClass().getName(), "onPostExecute()");
        //<<<<<<
        if(webForResult != null){
            webForResult.loadDataWithBaseURL(null, result, "text/html", CallPostURL.ENCODING, null);
        }else if (webHandlerForResult != null) {
            webHandlerForResult.loadData(result);
        }else{
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
        }
        //>>>>>>
        Log.i(getClass().getName(), "atualizado");
        //<<<<<<

        if(context instanceof NotifiableActivity){
            String contentToNotify = result;
            int indNotif;
            int indEndNotif;
            while ((indNotif = contentToNotify.indexOf(NOTIFIC_BEGIN))>=0){
                indEndNotif = contentToNotify.indexOf(NOTIFIC_END,indNotif);
                String notificContent = contentToNotify.substring(indNotif+NOTIFIC_BEGIN.length(), indEndNotif);
                String[] notificArr = notificContent.split("\\|");
                ((NotifiableActivity) context).generateNotification(Integer.parseInt(notificArr[0]),notificArr[1]);
                contentToNotify = contentToNotify.substring(indEndNotif+NOTIFIC_END.length());
            }
        }
    }

    public String postAudio() throws IOException {
        // prepare head data
        final long timestamp = System.currentTimeMillis();
        final byte[] timestampByt = (Long.toString(timestamp)).getBytes();
        final String boundary = BOUNDARY_APPEND + Long.toHexString(timestamp) + BOUNDARY_APPEND;
        //>>>>>>
        Log.i(getClass().getName(), "timestamp: "+timestamp+", timestampByt: "+timestampByt+", boundary: "+boundary);
        //<<<<<<

        String postUrl;
        String fileParamName;
        switch (audioType){
            case AudioRecorder.TYPE_RECORDED:
                postUrl = RECORDED_URL;
                fileParamName = RECORDED_PARAMNAME;
                break;
            case AudioRecorder.TYPE_SAMPLE:
                postUrl = SAMPLE_URL;
                fileParamName = SAMPLE_PARAMNAME;
                break;
            default:
                throw new IOException("Audio Type Invalid");
        }

        HttpURLConnection conn = null;
        OutputStream outRequest = null;
        InputStream inFileData = null;
        InputStream inResponse = null;

        StringBuffer bufferResponse = new StringBuffer();

        try{
            // start connection and define multipart
            conn = (HttpURLConnection) ( new URL(postUrl)).openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            conn.connect();

            byte[] content = {};
            outRequest = conn.getOutputStream();

            // populate regular params part
            if(audioType == AudioRecorder.TYPE_RECORDED) {
                outRequest.write((DELIMITER + boundary + CRLF).getBytes());
                outRequest.write(("Content-Disposition: form-data; name=\"ts\"" + CRLF).getBytes());
                outRequest.write(("Content-Type: text/plain; charset=" + ENCODING + CRLF).getBytes());
                outRequest.write(("Content-Length: " + timestampByt.length + CRLF).getBytes());
                outRequest.write(CRLF.getBytes());
                outRequest.write(timestampByt);
                outRequest.write(CRLF.getBytes());
            }

            // read binary file content
            content = new byte[(int) fileToUpload.length()];
            inFileData = new BufferedInputStream(new FileInputStream(fileToUpload));
            int totalBytesRead = 0;
            while (totalBytesRead < content.length) {
                int bytesRemaining = content.length - totalBytesRead;
                int bytesRead = inFileData.read(content, totalBytesRead, bytesRemaining);
                if (bytesRead > 0){
                    totalBytesRead = totalBytesRead + bytesRead;
                }
            }

            // populate binary file part
            outRequest.write( (DELIMITER + boundary + CRLF).getBytes());
            outRequest.write( ("Content-Disposition: form-data; name=\"" + fileParamName + "\"; filename=\"" + fileToUpload.getName() + "\"" + CRLF ).getBytes());
            outRequest.write( ("Content-Type: application/octet-stream" + CRLF ).getBytes());
            outRequest.write( ("Content-Length: " + content.length + CRLF ).getBytes());
            outRequest.write( ("Content-Transfer-Encoding: binary" + CRLF ).getBytes());
            outRequest.write(CRLF.getBytes());
            outRequest.write(content);
            outRequest.write(CRLF.getBytes());

            // end of multipart/form-data.
            outRequest.write((DELIMITER + boundary + DELIMITER + CRLF).getBytes());
            //>>>>>>
            Log.i(getClass().getName(), "enviado");
            //<<<<<<

            // reads response
            inResponse = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inResponse,ENCODING));
            String line;
            while ((line = reader.readLine()) != null) {
                bufferResponse.append(line);
            }
            //>>>>>>
            Log.i(getClass().getName(), "recebido");
            //<<<<<<

        }catch (Exception e){
            //>>>>>>
            Log.e(getClass().getName(), "erro ao fazer upload de arquivo", e);
            //<<<<<<
            return "<html><body><h3><font color=\"red\">Erro ao fazer upload do arquivo: " + e.getMessage() + "</font></h3></body></html>";
        }finally {
            if (conn != null) conn.disconnect();
            if (outRequest != null) outRequest.close();
            if (inFileData !=null) inFileData.close();
            if (inResponse !=null) inResponse.close();
        }
        //>>>>>>
        Log.i(getClass().getName(), "conteúdo HTML recebido: "+bufferResponse.toString());
        //<<<<<<
        return bufferResponse.toString();
    }

    private String compressFileToGzip() {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        GZIPOutputStream gzipos = null;
        String gzipPath = "";
        try {
            String originalPath = fileToUpload.getPath();
            gzipPath = originalPath+".gz";
            fis = new FileInputStream(originalPath);
            fos = new FileOutputStream(gzipPath);
            gzipos = new GZIPOutputStream(fos);
            byte[] buffer = new byte[GZIP_BUFFER_SIZE];
            int len;
            while((len=fis.read(buffer)) != -1){
                gzipos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            //>>>>>>
            Log.e(getClass().getName(), "erro ao zipar arquivo", e);
            //<<<<<<
        } finally {
            try {
                if (gzipos != null) {
                    gzipos.close();
                }
                if (gzipos != null) {
                    fos.close();
                }
                if (gzipos != null) {
                    fis.close();
                }
            } catch (IOException e) {
                //>>>>>>
                Log.e(getClass().getName(), "erro ao fechar arquivos ao zipar", e);
                //<<<<<<
            }
        }
        return gzipPath;
    }

}
