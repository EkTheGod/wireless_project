package devteam.pokemon_know.PokemonServer;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import devteam.pokemon_know.Model.Pokemon;
import devteam.pokemon_know.Model.PostPokemon;

/**
 * Created by wachirapong on 11/29/2016 AD.
 */

public class PokemonWebService {
    private static String server = "http://192.168.0.24:7777/";
    private static HashMap<String,Boolean> pokemonCheck;
    private static ArrayList<PostPokemon> pokemonArrayList = new ArrayList<PostPokemon>();

    public static String getPokemonFromServer(){
        return server+"pokemon";
    }

    public static String getResource(){
        return server+"first_pokemon";
    }

    public static String addPokemon(){
        return server+"addpokemon";
    }

    public static String getServer(){
        return server;
    }

    public static ArrayList<PostPokemon> getPokemon(){
        return pokemonArrayList;
    }

    public static void addPokemonToArray(Object jsonArrayObject){
        JSONObject Jobject = null;
        try {
            JSONArray Jarray =  (JSONArray) jsonArrayObject;
            for (int i = 0; i < Jarray.length(); i++) {
                JSONObject object = Jarray.getJSONObject(i);
                Log.d("Pokemon",object.toString());
                pokemonArrayList.add(new PostPokemon(
                        object.getString("postId").toString(),
                        object.getString("pokemonName").toString(),
                        object.getString("lat").toString(),
                        object.getString("long").toString(),
                        object.getString("startTime").toString(),
                        object.getString("endTime").toString(),
                        object.getString("user").toString()
                ));
//                        if( filterPokemonName!=null && filterPokemonName.length() > 0 && filterPokemonName.equals(object.getString("pokemonName").toString()))
//                            continue;
                if( pokemonCheck.containsKey(object.getString("_id").toString()) ) {
                    continue;
                }
                else{
                    pokemonCheck.put(object.getString("_id").toString(),true);
                }
//                Pokemon pokemon = dbHelper.getPokemonByName(object.getString("pokemonName").toString());
//                if( pokemon != null ){
//                    createMarker(pokemon.getId(),pokemon.getName(),object.getDouble("lat"),object.getDouble("long"));
//                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
