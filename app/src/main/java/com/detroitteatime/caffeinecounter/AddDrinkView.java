package com.detroitteatime.caffeinecounter;

/**
 * Created by marksargent on 5/30/13.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Scanner;


public class AddDrinkView extends Activity {

    private WebView webView;
    private String url;
    private Button add;
    private TextView prompt;
    private final Context myApp = this;
    private String returned;
    private ProgressBar progress;
    private int kind = 0;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.webview);

        Intent intent = getIntent();
        kind = intent.getIntExtra(DataBaseHelper.QUERY_TYPE, 0);


        setProgressBarIndeterminateVisibility(true);

        progress = (ProgressBar) findViewById(R.id.progressBar);

        add = (Button) findViewById(R.id.getCaffInfo);
        add.getBackground().setColorFilter(Color.parseColor(HelperMethodHolder.BUTTON_COLOR_BLUE), PorterDuff.Mode.MULTIPLY);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new GetDataTask().execute();
            }
        });

        prompt = (TextView) findViewById(R.id.getPrompt);
        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {

                super.onReceivedError(view, errorCode, description, failingUrl);

            }

            @Override
            public void onPageFinished(WebView view, String url) {

                setProgressBarIndeterminateVisibility(false);
                /*
                 * This call inject JavaScript into the page which just finished
				 * loading.
				 */
                AddDrinkView.this.url = url;
                super.onPageFinished(view, url);

                if (url.contains("caffeine-content")) {
                    add.setVisibility(View.VISIBLE);

                } else {
                    add.setVisibility(View.GONE);

                }
                webView.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);

                if (url.contains("caffeine-content")) {

                    prompt.setVisibility(View.GONE);

                } else {

                    prompt.setVisibility(View.VISIBLE);
                }
            }

        });

        url = "http://www.energyfiend.com/caffeine-content/";
        webView.loadUrl(url);

    }





    /* An instance of this class will be registered as a JavaScript interface */
    class MyJavaScriptInterface {
        @JavascriptInterface
        public void showHTML(String html) {
            new AlertDialog.Builder(myApp).setTitle("HTML").setMessage(html)
                    .setPositiveButton(android.R.string.ok, null)
                    .setCancelable(false).create().show();
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
        }


        @Override
        protected void onPostExecute(Void result) {
            //get the html and parse it

            progress.setVisibility(View.VISIBLE);

            Document doc = Jsoup.parse(returned);


            Elements perOz = doc.getElementsContainingOwnText(" mgs of caffeine per ");

/// To Do: Handle case where there is no double in the element
            String text = perOz.text();
            Scanner st = new Scanner(text);
            while (!st.hasNextDouble() && st.hasNext()) //adding st.hasNext() to fix NoSuchElementException
            {
                st.next();//crash report here
            }

            //if there is no double don't do anything with caffPer
            Intent intent = new Intent(AddDrinkView.this, NewTypeDialog.class);
            intent.putExtra(DataBaseHelper.QUERY_TYPE, kind);

            if (st.hasNextDouble()) {
                double caffPer = st.nextDouble();

                String drinkName;

                try {
                    String[] splitted = text.split(" contains");
                    drinkName = splitted[0];

                    intent.putExtra("CAFF_PER", caffPer);
                    intent.putExtra("DRINK_NAME", drinkName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            startActivity(intent);
        }

        @Override
        protected Void doInBackground(Void... params) {

            GetMethod data = new GetMethod(url);
            try {
                returned = data.getCaffeineData();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;

        }
    }
}
