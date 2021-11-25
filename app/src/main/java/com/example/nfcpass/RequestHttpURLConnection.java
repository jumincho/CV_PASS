package com.example.nfcpass;

import android.content.ContentValues;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class RequestHttpURLConnection {

    public String request(String _url, String jsonData){
        try{
            URL url = new URL(_url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("accept", "application/json");
            con.setDoOutput(true);
            String jsonString = jsonData;
            Log.i("호출","jsonString: "+jsonString);

            OutputStream os = con.getOutputStream();
            Log.i("호출", "os.toString()");
            byte[] input = jsonString.getBytes("utf-8");
            Log.i("호출", "os.toString()");
            os.write(input, 0, input.length);
            Log.i("호출", "os.toString()");

            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();

            String responseLine = null;
            while ((responseLine = reader.readLine()) != null) {
                response.append(responseLine.trim());
                Log.i("호출", "response" + response.toString());
            }

            return response.toString();

        } catch (ProtocolException e) {
            e.printStackTrace();
            Log.i("호출", "1");
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.i("호출", "2");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("호출", e.toString());
        }
        //JSON 보내는 Output stream
        return null;

    }
}