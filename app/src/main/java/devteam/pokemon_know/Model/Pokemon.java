package devteam.pokemon_know.Model;

/**
 * Created by wachirapong on 11/9/2016 AD.
 */

public  class Pokemon {
    //Database
    public static final String DATABASE_NAME = "PokemonDB_Pokemon.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE = "Pokemon";

    public class Column{
        public static final String id = "id";
        public static final String name = "name";
        public static final String gen = "gen";
        public static final String imgPath = "imgPath";
    }

    private String id;
    private String name;
    private int gen;
    private int imgPath;

    public Pokemon(){

    }//Def Con

    public Pokemon(String pokemonId, String pokemonName, int generation, int imgPath){
        id = pokemonId;
        name = pokemonName;
        gen = generation;
        this.imgPath = imgPath;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public int getGeneration(){
        return  gen;
    }

    public int getImgPath(){
        return  imgPath;
    }
}//end Pokemon
