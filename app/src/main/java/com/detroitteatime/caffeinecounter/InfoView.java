package com.detroitteatime.caffeinecounter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InfoView extends Activity {

    private WebView webView;
    private String type, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_info);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                super.onReceivedError(view, errorCode, description, failingUrl);

            }

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {


                super.onPageStarted(view, url, favicon);
            }


        });

        Intent intent = getIntent();
        type = intent.getExtras().getString("drink_type");

        // /if Starbucks type, direct to Starbucks site
        if (type.contains("arbucks")) {

            url = "http://www.starbucks.com/menu/drinks";
            webView.loadUrl(url);

        } else {
            // else, direct to energyfiend
            type = type.toLowerCase();
            type = type.replace(" ", "-");
            type = type.replace("(", "");
            type = type.replace(")", "");
            type = type.replace(",", "-");
            type = type.replace("'", "");

            url = "http://www.energyfiend.com/caffeine-content/" + type;
            webView.loadUrl(url);


        }

    }

}
