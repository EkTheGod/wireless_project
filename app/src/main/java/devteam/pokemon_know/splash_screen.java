package devteam.pokemon_know;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import devteam.pokemon_know.Model.DBHelper;
import devteam.pokemon_know.Model.Pokemon;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ekach on 8/11/2559.
 */

public class splash_screen extends Activity {

    private Handler myHandler;
    private DBHelper mHelper;

    private final String urlDownload = "http://192.168.0.188:7777/first_pokemon";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        myHandler = new Handler();
//        myHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//            }
//        }, 3000);
        initDatabase();
    }

    private void initDatabase(){
        mHelper = new DBHelper(this);
        callASyncGet();
    }

    private void callASyncGet() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlDownload)
                .build();
        Response responses = null;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Test", e.toString());
                callASyncGet();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                JSONObject Jobject = null;
                try {
                    JSONArray Jarray = new JSONArray(jsonData);
                    Log.d("Pokemon Size",Jarray.length()+"");
                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject object = Jarray.getJSONObject(i);
                        Log.d("Pokemon",object.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });




    }

}//end splash screen
