package joseph.shortenurl;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static TextView shortenUrlResultTxt;
    private static AutoCompleteTextView originUrlEditTxt;
    private static AutoCompleteTextView openBrowserEditTxt;
    private static Button translateBtn;
    private static Button copyBtn;
    private static Button shareBtn;
    private static Button goBtn;
    private UrlShorten urlShorten;
    private static WebView webView;
    private ClipboardManager clipboardManager;
    private static TextView totalTxt;
    private static DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shortenUrlResultTxt = (TextView) findViewById(R.id.resultTxt);
        originUrlEditTxt = (AutoCompleteTextView) findViewById(R.id.originUrlEditTxt);
        openBrowserEditTxt = (AutoCompleteTextView) findViewById(R.id.openBrowserEditTxt);
        translateBtn = (Button) findViewById(R.id.translationBtn);
        copyBtn = (Button) findViewById(R.id.copyBtn);
        shareBtn = (Button) findViewById(R.id.shareBtn);
        goBtn = (Button) findViewById(R.id.goBtn);
        webView = (WebView) findViewById(R.id.webView);
        urlShorten = new UrlShorten();
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        totalTxt = (TextView) findViewById(R.id.totalTxt);
        dbHelper = new DBHelper(getApplicationContext(), "URL.db", null, 1);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            String result;

            @Override
            public void onClick(View v) {
                if (originUrlEditTxt.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "URL을 입력하여 주세요.", Toast.LENGTH_SHORT).show();
                } else if (!isUrlMatch(originUrlEditTxt.getText().toString())) {
                    Toast.makeText(MainActivity.this, "올바른 URL 형식을 입력하여 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    result = urlShorten.getShortUrl(originUrlEditTxt.getText().toString());
                    shortenUrlResultTxt.setText("http://localhost/" + result);
                    long id = urlShorten.getId();
                    String originalUrl = originUrlEditTxt.getText().toString();
                    String shortUrl = "http://localhost/" + result;
                    //데이터 삽입
                    if (!dbHelper.isIdExist(id)) {
                        dbHelper.insert(id, originalUrl, shortUrl);
                    }
                }
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ClipData clipData;
                if (!shortenUrlResultTxt.getText().toString().equals("")) {
                    clipData = ClipData.newPlainText("label", shortenUrlResultTxt.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(MainActivity.this, "URL이 복사되었습니다.", Toast.LENGTH_SHORT).show();
                    totalTxt.setText(dbHelper.getResult());
                }
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!shortenUrlResultTxt.getText().toString().equals("")) {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, shortenUrlResultTxt.getText().toString());
                    Intent chooser = Intent.createChooser(intent, "친구에게 공유하기");
                    startActivity(chooser);
                }
            }
        });

        goBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ClipData clipData;
                String result;
                if (!openBrowserEditTxt.getText().toString().equals("")) {
                    String url = openBrowserEditTxt.getText().toString();
                    if (isMyHost(url)) {
                        result = getOriginalUrl(url);
                        if (!result.equals("NONE")) {
                            url = result;
                        }
                    }
                    Intent intent = new Intent(MainActivity.this, WebViewClient.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            }
        });
    }

    public boolean isUrlMatch(String s) {
        try {
            Pattern patt = Patterns.WEB_URL;
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isMyHost(String url) {
        // Base62로 인코딩된 문자를 다시 디코딩
        if (url.startsWith("http://localhost/")) {
            return true;
        } else {
            return false;
        }
    }

    public String getOriginalUrl(String url) {
        Long id;
        String originalUrl;
        id = urlShorten.fromBase62(url.substring(17));
        //해당 shortenUrl이 있는 경우
        if (dbHelper.isIdExist(id)) {
            originalUrl = dbHelper.getOriginUrl(id);
            return originalUrl;
        } else {
            return "NONE";
        }
    }
}





