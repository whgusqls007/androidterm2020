package com.example.androidterm2020.OPEN_API_Task;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ReceiveWeatherTask extends AsyncTask<String, Void, String[]> {
    double lan1, lon1;
    String[] arr = new String[5];
    public ReceiveWeatherTask(double lan, double lon){
        lan1 = lan;
        lon1 = lon;
    }
    private String str, receiveMsg;
    @Override
    protected String[] doInBackground(String... params) {
        URL url = null;
        try {
            url = new URL("https://api.openweathermap.org/data/2.5/weather?lat="+lan1+"&lon="+lon1+"&units=metric&appid=9a3df66611952a5d90a404ef25a32a15");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            String clientKey = "#########################";
            conn.setRequestProperty("x-waple-authorization", clientKey);
            if (conn.getResponseCode() == conn.HTTP_OK) {
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
            e.printStackTrace();
        }
        String main = "";
        String description = "";
        String temp = "";
        String min = "";
        String max = "";
        try {
            JSONObject jObject = new JSONObject(receiveMsg);
            JSONObject jObject2 = jObject.getJSONObject("main");
            JSONArray jar = new JSONObject(receiveMsg).getJSONArray("weather");
            temp = jObject2.getString("temp");
            min = jObject2.getString("temp_min");
            max = jObject2.getString("temp_max");
            for(int i=0;i<jar.length();i++){
                JSONObject jObject3 = jar.getJSONObject(i);
                main = jObject3.optString("main");
                description = jObject3.optString("description");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        arr[0] = main;
        arr[1] = description;
        arr[2] = temp;
        arr[3] = min;
        arr[4] = max;
        return arr;
    }
}
