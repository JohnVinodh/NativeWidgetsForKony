package com.kony.nativewidgets;

import android.app.Activity;
import android.content.Intent;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.util.Map;

/**
 * Created by KH2195 on 10/12/2016.
 */
public class WebViewActivity extends BaseAppCompatActivity {

    WebView mWebView = null;
    String url = null;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_webview);

        View decorView = getWindow().getDecorView();
// Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        mWebView = (WebView) findViewById(R.id.webview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mWebView.getSettings().setSafeBrowsingEnabled(false);
        }

        WebSettings webViewSettings =  mWebView.getSettings();
        webViewSettings.setJavaScriptEnabled(true);
        webViewSettings.setDomStorageEnabled(true);
        webViewSettings.setAllowFileAccess(true);
        webViewSettings.setAllowUniversalAccessFromFileURLs(true);
        webViewSettings.setUseWideViewPort(true);
        webViewSettings.setLoadWithOverviewMode(true);
        Log.i("JohnVinodh","Just a test message");
       final  Activity activity = this;
        mWebView.setWebChromeClient(new CustomWebChromeClient(activity));
        mWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(tel);
                    return true;
                }
                else if (url.contains("mailto:")) {
                    /*view.getContext().startActivity(
                            new Intent(Intent.ACTION_VIEW, Uri.parse(url)));*/
                    MailTo mailTo = MailTo.parse(url);

                    Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse(url));//new Intent(android.content.Intent.ACTION_SENDTO);
                    intent.setType("text/plain");

                    String to = mailTo.getTo();
                    if(to != null)
                    {
                        intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{to});
                    }
                    String subject = mailTo.getSubject();
                    if(subject != null)
                    {
                        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
                    }

                    String cc = mailTo.getCc();
                    if(cc != null)
                    {
                        intent.putExtra(android.content.Intent.EXTRA_CC, new String[]{cc});
                    }

                    String body = mailTo.getBody();
                    if(body != null)
                    {
                        intent.putExtra(android.content.Intent.EXTRA_TEXT, body);
                    }

                    Map<String,String> headers = mailTo.getHeaders();
                    String bcc = headers.get("bcc");
                    if(bcc != null)
                    {
                        intent.putExtra(android.content.Intent.EXTRA_BCC, new String[]{bcc});
                    }
                    view.getContext().startActivity(intent);
                    return true;

                }else {
                    view.loadUrl(url);
                    return true;
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("ContentType");
            url = extras.getString("URL");
            loadWebView(value);
        }
        //loadWebView("https://www.missionfed.com/rates?mobileview=1");
    }

    private void loadWebView(String value) {
        String base64 = getString(R.string.base64pdf);
       String browserString="<html>\n<body>\n<div id=\"pdf\" >\n<object data=\"data:application/pdf;base64,"+base64+"\""+"width=\"100%\" height=\"1000%\">\n</div>\n</body>\n</html>";
        if(value.equalsIgnoreCase("StaticContent")) {
            mWebView.loadUrl("file:///android_asset/testpdf.html");
            //mWebView.loadData(browserString,"text/html", "UTF-8");
            //mWebView.loadDataWithBaseURL(null,browserString,"text/html","UTF-8",null);
            //Toast.makeText(getApplicationContext(),"Static Content it is",Toast.LENGTH_LONG).show();
        } else if(value.equalsIgnoreCase("DynamicContent")) {
            Toast.makeText(getApplicationContext(),"Dynamic Content it is",Toast.LENGTH_LONG).show();
            //mWebView.loadUrl("https://www.missionfed.com/rates?mobileview=1");//http://10.10.3.98:8080/neo/ModifyCustomerDSD-StartPage.htm");
            //https://uattmbbank.tau2904.com/dev/page/view/webview-tmb-business-touch.html?mib_view
            mWebView.loadUrl(url);
        }
    }
}
