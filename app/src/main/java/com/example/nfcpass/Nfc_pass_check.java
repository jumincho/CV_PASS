package com.example.nfcpass;

import static android.net.wifi.p2p.WifiP2pManager.ERROR;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Nfc_pass_check extends Activity {

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    private TextToSpeech tts;
    String check;
    LinearLayout shop_mode,user_mode;
    long timenow,todaynow;
    Date mtime,mtoday;
    SimpleDateFormat mtimeformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    SimpleDateFormat todaynowformat = new SimpleDateFormat("yyyy-MM-dd");
    String time,todaytime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_pass_check);
        context = this;

        shop_mode = findViewById(R.id.shop_mode);
        user_mode = findViewById(R.id.user_mode);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != ERROR) {
                    // 언어를 선택한다.
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });

        Intent intent = getIntent();
        if(intent.getStringExtra("값") != null) { //사업자 모드인지 / 유저 모드인지 구별 해줌
            check = intent.getStringExtra("값");
        }else{
            check = "3";
        }

        if(check.equals("1")){ // 상인 모드
            shop_mode.setVisibility(View.VISIBLE);
            user_mode.setVisibility(View.GONE);
        } else if (check.equals("2")) { // 사용자 실행 모드
            shop_mode.setVisibility(View.GONE);
            user_mode.setVisibility(View.VISIBLE);
        }
        else{ // 빠른 실행모드
            shop_mode.setVisibility(View.GONE);
            user_mode.setVisibility(View.VISIBLE);
        }


        if(check.equals("1")) {
            writeTag wt = new writeTag("감자탕"); // 이곳에 사업자 정보 입력 "사업자 번호" + "사장님 이름"
            wt.start();
            shop_mode.setVisibility(View.VISIBLE);
        }else{}



        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            // Stop here, we definitely need NFC
            Toast.makeText(this, "NFC를 지원하지 않는 기기 입니다. QR체크인을 진행해 주세요.", Toast.LENGTH_LONG).show();
            finish();
        }

        timenow = System.currentTimeMillis();
        mtime = new Date(timenow);
        time = mtimeformat.format(mtime);


        todaynow = System.currentTimeMillis();
        mtoday = new Date(todaynow);
        todaytime = todaynowformat.format(mtoday);


        readFromIntent(getIntent());


        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] { tagDetected };
    }


    private void readFromIntent(Intent intent) {
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs);
        }
    }
    private void buildTagViews(NdefMessage[] msgs) {

        //FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (msgs == null || msgs.length == 0) return;

        String text = "";
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
        int languageCodeLength = payload[0] & 0063;

        try {
            // 받아온 Tag 값
            text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();


        if(check.equals("2")||check.equals("3")) {
            tts.setPitch(0.3f);
            tts.setSpeechRate(2.5f);
            tts.speak("입장이 완료 되었습니다.", TextToSpeech.QUEUE_FLUSH, null);
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] a = {300,100,300,100};
            vibrator.vibrate(a,-1); // 0.5초간 진동
            Toast.makeText(this,"입장이 완료 되었습니다.",Toast.LENGTH_LONG).show();

            String name = "조주민";
            Map<String,Object> userdate = new HashMap<>();
            userdate.put("전화번호","010-5602-3622");
            userdate.put("시간",time);


           // db.collection("사업장").document("후생관").collection(todaytime).document(name).set(userdate);
            //UUID를 사용하여 이름에 더하여 동명이인 방지 및 여러번 입장 체크



        }else{
            tts.setPitch(0.3f);
            tts.setSpeechRate(2.5f);
            tts.speak("기록이 완료 되었습니다.", TextToSpeech.QUEUE_FLUSH, null);
            Toast.makeText(this,"기록이 완료 되었습니다",Toast.LENGTH_LONG).show();

         //   db.collection("사업장").document("이마트");
        }
    }


    /******************************************************************************
     **********************************Write to NFC Tag****************************
     ******************************************************************************/
    private void write(String text, Tag tag) throws IOException, FormatException {
        NdefRecord[] records = { createRecord(text) };
        NdefMessage message = new NdefMessage(records);
        Ndef ndef = Ndef.get(tag);
        ndef.connect();
        ndef.writeNdefMessage(message);
        ndef.close();
    }
    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "en";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];


        payload[0] = (byte) langLength;


        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
    }



    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        readFromIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())){
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        WriteModeOff();
    }

    @Override
    public void onResume(){
        super.onResume();
        WriteModeOn();
    }

    private void WriteModeOn(){
        writeMode = true;
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null);
    }
    private void WriteModeOff(){
        writeMode = false;
        nfcAdapter.disableForegroundDispatch(this);
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


    class writeTag extends Thread {

        String name;


        public writeTag(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            long[] a = {1000,100,1000,100};
            vibrator.vibrate(a,0);
            while (true) {
                try {
                    if(myTag != null) {
                        write(name, myTag);
                        vibrator.cancel();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
