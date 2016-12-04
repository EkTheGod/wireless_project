package devteam.pokemon_know;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

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
    private SharedPreferences sp;

    private final String urlDownload = "http://192.168.0.188:7777/first_pokemon";
    private String imgUrl;
    private final String directory = Environment.getExternalStorageDirectory().toString()+"/Pokemon/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        myHandler = new Handler();
        mHelper = new DBHelper(this);
        initDatabase();
    }

    private void initDatabase(){
//        sp = getSharedPreferences("PREF_Check_First_Load", Context.MODE_PRIVATE);
//        if(sp.getBoolean("LOADED", false))
//        getPokemonData();
        loadPokemonData();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent intent;
                if( AccessToken.getCurrentAccessToken() ==null || AccessToken.getCurrentAccessToken().isExpired() )
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                else
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }, 3000);
    }

    private void loadPokemonData() {
        try {
            mHelper.loadPokemonResource();
        }catch (IOException e){
            Log.e("Error in splash screen", "below");
            e.printStackTrace();
        }
    }

    private void getPokemonData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlDownload)
                .build();
        Response responses = null;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getPokemonData();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                JSONObject Jobject = null;
                try {
                    JSONArray Jarray = new JSONArray(jsonData);
                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject object = Jarray.getJSONObject(i);
                        imgUrl = object.getString("img");
                        mHelper.addPokemon(new Pokemon(
                                object.getString("id").toString(),
                                object.getString("name").toString(),
                                1,
                                "pokemon"+object.getString("id").toString()
                        ));
                    }

//                    SharedPreferences.Editor editor = sp.edit();
//                    editor.putBoolean("LOADED", true);
//                    editor.commit();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }//



}//end splash screen
