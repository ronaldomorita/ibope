package br.com.move2.listeningdemo;

import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewListeningHandler {

    private WebView webView;
    private String currContent;

    private static final String CONTENT_START = "<!-- content-start -->";
    private static final String CONTENT_END = "<!-- content-end -->";
    private static final String DIV_BACKGROUND = "<div style=\"background-color:";
    private static final String COLOR_ODD = "#EEEEAA";
    private static final String COLOR_EVEN = "#FFFFFF";


    public WebViewListeningHandler(WebView givenWebView){
        webView = givenWebView;
        webView.setWebViewClient(new WebViewClient());
        currContent = "";
    }

    void loadData(String newContent){

        int indCurrStart = currContent.indexOf(CONTENT_START);
        int indCurrEnd   = currContent.indexOf(CONTENT_END  );
        int indNewStart  =  newContent.indexOf(CONTENT_START);
        int indNewEnd    =  newContent.indexOf(CONTENT_END  );

        String contentToLoad = "";
        if(indCurrStart>=0 && indCurrEnd>indCurrStart &&
                indNewStart>=0 && indNewEnd>indNewStart ){

            String subNewContent = newContent.substring(indNewStart+CONTENT_START.length(),indNewEnd);

            int indBackground = currContent.indexOf(DIV_BACKGROUND);
            if(indBackground>0) {
                int indColor = indBackground + DIV_BACKGROUND.length();
                String backgroundColor = currContent.substring(indColor, indColor+7);
                if (COLOR_ODD.equals(backgroundColor)) {
                    subNewContent = subNewContent.replace(COLOR_ODD, COLOR_EVEN);
                }
            }

            contentToLoad = currContent.substring(0,indCurrStart+CONTENT_START.length())+subNewContent+currContent.substring(indCurrStart+CONTENT_START.length());
            //>>>>>>
            Log.i(getClass().getName(), "newContent:\n"+newContent);
            Log.i(getClass().getName(), "subNewContent:\n"+subNewContent);
            Log.i(getClass().getName(), "contentToLoad:\n"+contentToLoad);
            //<<<<<<

        }else{
            contentToLoad = newContent;
        }

        webView.loadDataWithBaseURL(null, contentToLoad, "text/html", CallPostURL.ENCODING, null);
        currContent = contentToLoad;
    }
}
