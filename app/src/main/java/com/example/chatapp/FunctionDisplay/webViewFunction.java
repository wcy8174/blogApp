package com.example.chatapp.FunctionDisplay;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.example.chatapp.R;

public class webViewFunction extends AppCompatActivity {
    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_function);
        webView=findViewById(R.id.webView);
        WebSettings settings= webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        //settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadUrl("http://114.55.66.160/about.html");
       // webView.addJavascriptInterface(new MyService(this),"myService");

    }
}