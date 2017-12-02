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

    private TextView shortenUrlResultTxt;
    private TextView countOfTransTxt;
    private TextView countOfUsedTxt;
    private TextView splitTxt;
    private AutoCompleteTextView originUrlEditTxt;
    private AutoCompleteTextView openBrowserEditTxt;
    private Button translateBtn;
    private Button copyBtn;
    private Button shareBtn;
    private Button goBtn;
    private UrlShorten urlShorten;
    private ClipboardManager clipboardManager;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shortenUrlResultTxt = (TextView) findViewById(R.id.resultTxt);
        countOfTransTxt  = (TextView) findViewById(R.id.countOfTransTxt);
        countOfUsedTxt  = (TextView) findViewById(R.id.countOfUsedTxt);
        splitTxt  = (TextView) findViewById(R.id.splitTxt);
        originUrlEditTxt = (AutoCompleteTextView) findViewById(R.id.originUrlEditTxt);
        openBrowserEditTxt = (AutoCompleteTextView) findViewById(R.id.openBrowserEditTxt);
        translateBtn = (Button) findViewById(R.id.translationBtn);
        copyBtn = (Button) findViewById(R.id.copyBtn);
        shareBtn = (Button) findViewById(R.id.shareBtn);
        goBtn = (Button) findViewById(R.id.goBtn);
        clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        urlShorten = new UrlShorten();
        dbHelper = new DBHelper(getApplicationContext(), "URL.db", null, 1);

        // URL 변환 버튼에 대한 기능
        translateBtn.setOnClickListener(new View.OnClickListener() {
            String result;
            long id;
            int countOfTrans, countOfUsed;
            @Override
            public void onClick(View v) {
                // 아무런 정보도 입력하지 않은 상태에서 버튼을 눌렀을 때에 대한 예외처리
                if (originUrlEditTxt.getText().toString().equals("")) {
                    shortenUrlResultTxt.setText("");
                    countOfTransTxt.setText("");
                    splitTxt.setText("");
                    countOfUsedTxt.setText("");
                    Toast.makeText(MainActivity.this, "URL을 입력하여 주세요.", Toast.LENGTH_SHORT).show();
                }
                // 올바르지 않은 URL형식을 입력했을 때의 예외처리
                else if (!isUrlMatch(originUrlEditTxt.getText().toString())) {
                    shortenUrlResultTxt.setText("");
                    countOfTransTxt.setText("");
                    splitTxt.setText("");
                    countOfUsedTxt.setText("");
                    Toast.makeText(MainActivity.this, "올바른 URL 형식을 입력하여 주세요.", Toast.LENGTH_SHORT).show();
                }
                // 올바른 URL 입력 시 변환
                else {
                    countOfTrans=0;
                    countOfUsed=0;
                    String originalUrl = originUrlEditTxt.getText().toString();
                    // 데이터베이스에 해당 url이 없는 경우 삽입(중복방지)
                    if (!dbHelper.isUrlExist(originalUrl)) {
                        dbHelper.insert(originalUrl, "-",0,0);
                        id = dbHelper.getId(originalUrl);
                    } else {
                        id = dbHelper.getId(originalUrl);
                        countOfTrans = dbHelper.getCountOfTrans(id);
                        countOfUsed = dbHelper.getCountOfUsed(id);
                    }
                    // 62진법을 통해 줄여진 url 반환
                    result = urlShorten.toBase62(id);
                    countOfTrans++;
                    dbHelper.update(result,countOfTrans, id);
                    shortenUrlResultTxt.setText("http://localhost/" + result);
                    countOfTransTxt.setText("변환된 횟수 : "+countOfTrans+" 번");
                    splitTxt.setText("/");
                    countOfUsedTxt.setText("사용된 횟수 : "+countOfUsed+" 번");
                }
            }
        });

        // URL 복사버튼 클릭기능
        copyBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ClipData clipData;
                if (!shortenUrlResultTxt.getText().toString().equals("")) {
                    clipData = ClipData.newPlainText("label", shortenUrlResultTxt.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(MainActivity.this, "URL이 복사되었습니다.", Toast.LENGTH_SHORT).show();
                    //totalTxt.setText(dbHelper.getResult());
                }
            }
        });

        // 공유하기 버튼 클릭기능
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

        // 웹뷰를 통한 URL 열기
        goBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int countOfUsed=0;
                long id=0;
                String result;
                if (!openBrowserEditTxt.getText().toString().equals("")) {
                    String url = openBrowserEditTxt.getText().toString();
                    // short url을 입력하였을 경우
                    if (isMyHost(url)) {
                        result = getOriginalUrl(url);
                        if (!result.equals("NONE")) {
                            // 데이터베이스에서 가져온 원래 URL 저장
                            url = result;
                            id = dbHelper.getId(url);
                            countOfUsed = dbHelper.getCountOfUsed(id);
                            dbHelper.update(countOfUsed+1,id);
                        }
                    }
                    // URL 열기
                    Intent intent = new Intent(MainActivity.this, WebViewClient.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            }
        });
    }

    // 올바른 URL인지 확인하는 함수
    public boolean isUrlMatch(String s) {
        try {
            Pattern patt = Patterns.WEB_URL;
            Matcher matcher = patt.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    // 변환된 URL인지 확인하는 함수
    public boolean isMyHost(String url) {
        // Base62로 인코딩된 문자를 다시 디코딩
        if (url.startsWith("http://localhost/")) {
            return true;
        } else {
            return false;
        }
    }

    // 원래 URL을 가져오기 위한 함수
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





