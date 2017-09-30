package org.nashorn.prototype;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        new LoadUserList().execute("http://172.16.1.253:52273/user");
    }

    public void addUser(View view){

    }

    class LoadUserList extends AsyncTask<String,String,String> {
        ProgressDialog dialog = new ProgressDialog(UserActivity.this);

        /* 선택 메소드 : Ctrl+O */
        @Override
        protected void onPreExecute() {
            dialog.setMessage("사용자 목록 로딩 중...");
            dialog.show();
        }

        /* 선택 메소드 : Ctrl+O */
        @Override
        protected void onPostExecute(String s) { //s-->서버에서 받은 JSON문자열
            dialog.dismiss();
            //JSON파싱 --> ListView에 출력
            try {
                JSONArray array = new JSONArray(s);
                ArrayList<String> strings = new ArrayList<String>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    strings.add(obj.getString("name"));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        UserActivity.this, android.R.layout.simple_list_item_1,strings);
                ListView listView = (ListView)findViewById(R.id.listview);;
                listView.setAdapter(adapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /* 필수-추상메소드 재정의해서 사용할때 Alt+Enter  (통신처리) */
        @Override
        protected String doInBackground(String... params) {
            //스레드 처리
            //스레드 구간안에서는 URL접근해서는 안된다. --> 파일 입출력, NETWORK통신만,,,
            //메인 스레드 -> 워크 스레드가 있을대 워크 스레드가 메인 스레드를 접근해서는 안된다.
            StringBuilder output = new StringBuilder();
            try{
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                if (conn != null){
                    conn.setConnectTimeout(10000);
                    conn.setRequestMethod("GET");
                    //conn.setDoInput(true);
                    //conn.setDoOutput(true);
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    while(true){
                        line = reader.readLine();
                        if (line == null) break;
                        output.append(line);
                    }
                    reader.close();
                    conn.disconnect();
                }
            } catch (Exception e){e.printStackTrace(); }
            return output.toString();
        }
    }
}
