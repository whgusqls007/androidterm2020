package com.example.androidterm2020.OPEN_API_Task;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class AirTask extends AsyncTask<String, Void, String[]> {
    String clientKey = "#########################";
    private String str, receiveMsg;
    private final String ID = "########";
    String[] arr = new String[2];
    @Override
    protected String[] doInBackground(String... params)
    {
        URL url = null;
        try {
            url = new URL("http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/" +
                    "getMsrstnAcctoRltmMesureDnsty?stationName=%EB%B6%80%EA%B3%A1%EB%8F%99&dataTerm=month" +
                    "&pageNo=1&numOfRows=10&ServiceKey=7sWWeYO4MfEJPwnU33%2BgPEO8587yFl8rJgID8BsgbSij73COH0j"
                    +"TvoafHsF5Eo6Os3bdFvX5qba%2BtEjWJVpgYQ%3D%3D&ver=1.3&_returnType=json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(500);
            conn.setReadTimeout(500);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            String clientKey = "#########################";
            conn.setRequestProperty("x-waple-authorization", clientKey);
            if (conn.getResponseCode() == conn.HTTP_OK) { // 터지는 라인.
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
                Log.i("receiveMsg : ", receiveMsg);
                reader.close();
            } else {
                Log.i("통신 결과", conn.getResponseCode() + "에러");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            arr[0] = Integer.toString(-1);
            arr[1] = Integer.toString(-1);
            return arr;
            //e.printStackTrace();
        }
        String pm10Value24 = " ";
        String pm25Value24 = " ";
        try {
            JSONObject jObject = new JSONObject(receiveMsg);
            JSONArray jar = jObject.getJSONArray("list");
            //JSONObject jObject2 = jObject.getJSONObject("list");

            //JSONArray jar = new JSONObject(receiveMsg).getJSONArray("list");
            for (int i = 0; i < jar.length(); i++) {
                JSONObject jObject3 = jar.getJSONObject(i);
                pm10Value24 = jObject3.optString("pm10Value24");
                pm25Value24 = jObject3.optString("pm25Value24");
                if (!pm10Value24.equals(" ") || !pm25Value24.equals(" ")) {
                    arr[0] = pm10Value24;
                    arr[1] = pm25Value24;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr;
    }
}
