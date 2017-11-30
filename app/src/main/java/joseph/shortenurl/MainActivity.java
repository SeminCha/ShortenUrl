package joseph.shortenurl;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static TextView shortenUrlResultTxt;
    private static AutoCompleteTextView originUrlEditTxt;
    private static Button translateBtn;
    private static Button copyBtn;
    private static Button shareBtn;
    private UrlShorten urlShorten;
    ClipboardManager clipboardManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shortenUrlResultTxt = (TextView) findViewById(R.id.resultTxt);
        originUrlEditTxt = (AutoCompleteTextView) findViewById(R.id.originUrlEditTxt);
        translateBtn = (Button) findViewById(R.id.translationBtn);
        copyBtn = (Button) findViewById(R.id.copyBtn);
        shareBtn = (Button) findViewById(R.id.shareBtn);
        urlShorten = new UrlShorten();
        clipboardManager  =  (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            String result;

            @Override
            public void onClick(View v) {
                if (originUrlEditTxt.getText() == null) {
                    Toast.makeText(MainActivity.this, "URL을 입력하여 주세요.", Toast.LENGTH_SHORT).show();
                } else if (!isUrlMatch(originUrlEditTxt.getText().toString())) {
                    Toast.makeText(MainActivity.this, "올바른 URL 형식을 입력하여 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    result = urlShorten.getShortUrl(originUrlEditTxt.getText().toString());
                    shortenUrlResultTxt.setText("http://localhost/" + result);
                }
            }
        });

        copyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ClipData clipData;
                if (shortenUrlResultTxt.getText() != null) {
                    clipData = ClipData.newPlainText("label",shortenUrlResultTxt.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(MainActivity.this, "URL이 복사되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (shortenUrlResultTxt.getText() != null) {
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, shortenUrlResultTxt.getText().toString());
                    Intent chooser = Intent.createChooser(intent, "친구에게 공유하기");
                    startActivity(chooser);
                }
            }
        });
    }


    public boolean isUrlMatch(String s) {
        try {
            Pattern patt =  Patterns.WEB_URL;
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

}
