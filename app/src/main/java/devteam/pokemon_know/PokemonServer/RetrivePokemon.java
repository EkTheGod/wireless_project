package devteam.pokemon_know.PokemonServer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import devteam.pokemon_know.MainActivity;
import devteam.pokemon_know.Model.DBHelper;
import devteam.pokemon_know.Model.Pokemon;
import devteam.pokemon_know.Model.PostPokemon;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wachirapong on 11/9/2016 AD.
 */

public class RetrivePokemon extends Thread {

    private String urlGetPostPokemon = "http://192.168.0.188:7777/pokemon";
    private GoogleMap mMap;
    private ArrayList<PostPokemon> postPokemonArrayList;
    private Context context;
    private DBHelper dbHelper;
    private HashMap<String,Boolean> pokemonCheck;
    private ArrayList<Marker> markerArrayList;

    public RetrivePokemon(Context context, GoogleMap mMap,ArrayList<Marker> markerArrayList){
        this.mMap = mMap;
        postPokemonArrayList = new ArrayList<PostPokemon>();
        this.context =context;
        dbHelper = new DBHelper(context);
        pokemonCheck = new HashMap<String,Boolean>();
        this.markerArrayList = markerArrayList;
    }

    private void getPostPokemon() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlGetPostPokemon)
                .build();
        Response responses = null;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Test", e.toString());
                getPostPokemon();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonData = response.body().string();
                JSONObject Jobject = null;
                try {
                    JSONArray Jarray = new JSONArray(jsonData);
                    for (int i = 0; i < Jarray.length(); i++) {
                        JSONObject object = Jarray.getJSONObject(i);
//                        postPokemonArrayList.add(new PostPokemon(
//                                object.getString("postId").toString(),
//                                object.getString("pokemonName").toString(),
//                                object.getString("Lat").toString(),
//                                object.getString("Long").toString(),
//                                object.getString("startTime").toString(),
//                                object.getString("endTime").toString(),
//                                object.getString("user").toString()
//                        ));
                        if( pokemonCheck.containsKey(object.getString("postId").toString()) ) {
                            continue;
                        }
                        else{
                            pokemonCheck.put(object.getString("postId").toString(),true);
                        }
                        Pokemon pokemon = dbHelper.getPokemonByName(object.getString("pokemonName").toString());
                        if( pokemon != null ){
                            createMarker(pokemon.getId(),pokemon.getName(),object.getDouble("lat"),object.getDouble("long"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        });
    }//

    @Override
    public void run() {
        while (true) {
            getPostPokemon();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void createMarker(final String pokemonId,final String pokemonName,final Double Lat,final Double Long){
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Resources resources = context.getResources();
                final int resourceId = resources.getIdentifier("pokemon"+pokemonId, "drawable",
                        context.getPackageName());
                LatLng latLng = new LatLng(Lat, Long);
                Marker pokeMark = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(pokemonName)
                        .snippet("I Love You.")
                        .icon(BitmapDescriptorFactory.fromResource(resourceId)));
                markerArrayList.add(pokeMark);
            } // This is your code
        };
        mainHandler.post(myRunnable);
    }

    public Bitmap resizeMapIcons(String iconName,int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(),context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }
}//end class