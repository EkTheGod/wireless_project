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
    private String imgPath;

    public Pokemon(){

    }//Def Con

    public Pokemon(String pokemonId, String pokemonName, int generation, String imgPath){
        id = pokemonId;
        name = pokemonName;
        gen = generation;
        this.imgPath = imgPath;
    }
}//end Pokemon
