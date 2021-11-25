package com.example.nfcpass;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Check_shop_doc extends AppCompatActivity {
    //정환이가 쓸 변수
    String resultName;
    String resultNumber;


    EditText et_BusinessNumber_1;
    EditText et_BusinessNumber_2;
    EditText et_BusinessNumber_3;
    EditText et_BusinessName;
    EditText et_BusinessDate;
    Button btn_BusinessData;
    TextView tv_TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("호출", "1");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_shop_doc);

        et_BusinessNumber_1 = (EditText) findViewById(R.id.et_BusinessNumber_1);
        et_BusinessNumber_2 = (EditText) findViewById(R.id.et_BusinessNumber_2);
        et_BusinessNumber_3 = (EditText) findViewById(R.id.et_BusinessNumber_3);
        et_BusinessName = (EditText) findViewById(R.id.et_BusinessName);
        et_BusinessDate = (EditText) findViewById(R.id.et_BusinessDate);
        btn_BusinessData = (Button) findViewById(R.id.btn_BusinessData);
        tv_TextView = (TextView) findViewById(R.id.textView);

        btn_BusinessData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String numberData = (et_BusinessNumber_1.getText().toString() + et_BusinessNumber_2.getText().toString() + et_BusinessNumber_3.getText().toString());
                String nameData = et_BusinessName.getText().toString();
                String dateData = et_BusinessDate.getText().toString();


                String url = "https://api.odcloud.kr/api/nts-businessman/v1/validate?serviceKey=EEA0ZjjFdim30KlXr7%2FroJJf6LBMusuAISvO9ET5leSjtUIivRhW%2F4g%2FOJlqTXSodVTOQKY8BN%2B05S9qRzpRjg%3D%3D";
                String jsonData = "{  \"businesses\": [    {      \"b_no\": \""
                        + numberData + "\",      \"start_dt\": \""
                        + dateData + "\",      \"p_nm\": \""
                        + nameData +"\",      \"p_nm2\": \"\",      \"b_nm\": \"\",      \"corp_no\": \"\",      \"b_sector\": \"\",      \"b_type\": \"\"    }  ]}";


                // AsyncTask를 통해 HttpURLConnection 수행.
                NetworkTask networkTask = new NetworkTask(url, jsonData);
                networkTask.execute();

            }
        });
    }


    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private String jsonData;

        public NetworkTask(String url, String jsonData) {

            this.url = url;
            this.jsonData = jsonData;
            Log.i("호출", "jsonData: " + this.jsonData);
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.i("호출", "doInBackground");
            String result; // 요청 결과를 저장할 변수.

            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, jsonData);

            Log.i("호출", "result: " + result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("호출", "onPostExecute");
            super.onPostExecute(s);

            DataParser dataParser = new DataParser(s);
            tv_TextView.setText(dataParser.getNo());
            resultName = dataParser.getName();
            resultNumber = dataParser.getNo();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);

    }

}