package joseph.shortenurl;

import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;

/**
 * Created by Semin on 2017-11-30.
 */

public class WebViewClient extends AppCompatActivity {

    WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new SSLTolerentWebViewClient());
        if (!url.startsWith("http"))
            url = "http://" + url;
        webView.loadUrl(url);
    }

    // SSL Error Tolerant Web View Client
    private class SSLTolerentWebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors } }
        }
    }
}
