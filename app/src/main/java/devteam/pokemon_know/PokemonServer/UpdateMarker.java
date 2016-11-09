package devteam.pokemon_know.PokemonServer;

import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

import devteam.pokemon_know.Model.PostPokemon;

/**
 * Created by wachirapong on 11/10/2016 AD.
 */

public class UpdateMarker extends Thread{
    private ArrayList<Marker> markerArrayList;
    private ArrayList<PostPokemon> postPokemonArrayList;
    public UpdateMarker(ArrayList<Marker> markerArrayList,ArrayList<PostPokemon> postPokemonArrayList){
        this.markerArrayList = markerArrayList;
        this.postPokemonArrayList = postPokemonArrayList;
    }

    public void run(){
        for(int i=0;i<markerArrayList.size();i++){
            markerArrayList.get(i).setSnippet(postPokemonArrayList.get(i).getEndTime());
        }
    }
}//end
