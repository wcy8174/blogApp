package com.example.chatapp.FunctionDisplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.chatapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class webViewFunction extends AppCompatActivity {
    private WebView webView;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_function);

//        navView=findViewById(R.id.nav_view);
//        webView=findViewById(R.id.webView);
//        WebSettings settings= webView.getSettings();
//        settings.setJavaScriptEnabled(true);
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webView.loadUrl("http://114.55.66.160/about.html");
//        webView.addJavascriptInterface(new MyService(this),"myService");
//
//
//        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                switch (menuItem.getItemId()){
//                    case R.id.navigation_home:
//                        webView.evaluateJavascript("javascript:fun1('JohnYu')",
//                                value-> Toast.makeText(webViewFunction.this,value,Toast.LENGTH_LONG).show());
//                        break;
//
//                }
//                return true;
//            }
//        });
    }
}
