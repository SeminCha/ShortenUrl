package joseph.shortenurl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 데이터베이스 관련 기능 클래스
 */

public class DBHelper extends SQLiteOpenHelper {

    // DBHelper 생성자로 관리할 DB 이름과 버전 정보를 받음
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // DB를 새로 생성하는 함수
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE URL_LIST (id INTEGER PRIMARY KEY AUTOINCREMENT, originalUrl TEXT, shortUrl TEXT, count INTEGER);");
    }

    // DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // 데이터 삽입 함수
    public void insert(String originalUrl, String shortUrl, int count) {
        // 읽고 쓰기가 가능하게 DB 열기
        SQLiteDatabase db = getWritableDatabase();
        // DB에 입력한 값으로 행 추가
        db.execSQL("INSERT INTO URL_LIST VALUES(null, '" + originalUrl + "', '" + shortUrl + "', " + count + ");");
        db.close();
    }

    public void update(String shortUrl, int count, long id) {
        SQLiteDatabase db = getWritableDatabase();
        // 입력한 항목과 일치하는 행의 정보 수정
        db.execSQL("UPDATE URL_LIST SET shortUrl= '" + shortUrl + "' WHERE id= " + id + ";");
        db.execSQL("UPDATE URL_LIST SET count= " + count + " WHERE id= " + id + ";");
        db.close();
    }

    // 전체 데이터 출력 함수
    public String getResult() {
        // 읽기가 가능하게 DB 열기
        SQLiteDatabase db = getReadableDatabase();
        String result = "";

        // DB에 있는 데이터를 쉽게 처리하기 위해 Cursor를 사용하여 테이블에 있는 모든 데이터 출력
        Cursor cursor = db.rawQuery("SELECT * FROM URL_LIST", null);
        while (cursor.moveToNext()) {
            result += cursor.getInt(0)
                    + " , "
                    + cursor.getString(1)
                    + " , "
                    + cursor.getString(2)
                    + "\n";
        }
        return result;
    }

    public Long getId(String originalUrl){
        SQLiteDatabase db = getReadableDatabase();
        long id=0;
        String sql = "SELECT * FROM URL_LIST WHERE originalUrl = '"+originalUrl+"';";
        Cursor result = db.rawQuery(sql, null);
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(result.moveToFirst()){
            id = result.getLong(0);
        }
        result.close();
        return id;
    }

    // 원래 URL DB에서 가져와 반환하는 함수
    public String getOriginUrl(long index){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "select * from URL_LIST where id = "+index+";";
        Cursor result = db.rawQuery(sql, null);
        String originalUrl="";
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(result.moveToFirst()){
            originalUrl = result.getString(1);
        }
        result.close();
        return originalUrl;
    }

    // 원래 URL DB에서 가져와 반환하는 함수
    public int getCount(long id){
        int count =0;
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM URL_LIST WHERE id = "+id+";";
        Cursor result = db.rawQuery(sql, null);
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(result.moveToFirst()){
            count = result.getInt(3);
        }
        result.close();
        return count;
    }

    // 해당 url이 에 있는지 여부 반환하는 함수
    public boolean isIdExist(long id){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM URL_LIST WHERE id = "+id+";";
        Cursor result = db.rawQuery(sql, null);
        boolean isExist=false;
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(result.moveToFirst()){
            isExist = true;
        }
        result.close();
        return isExist;
    }

    // 변환된 URL이 DB에 있는지 여부 반환하는 함수
    public boolean isUrlExist(String originalUrl){
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM URL_LIST WHERE originalUrl = '"+originalUrl+"';";
        Cursor result = db.rawQuery(sql, null);
        boolean isExist=false;
        // result(Cursor 객체)가 비어 있으면 false 리턴
        if(result.moveToFirst()){
            isExist = true;
        }
        result.close();
        return isExist;
    }
}





