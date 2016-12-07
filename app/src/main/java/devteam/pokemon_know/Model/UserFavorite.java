package devteam.pokemon_know.Model;

/**
 * Created by ekach on 4/12/2559.
 */

public class UserFavorite {
    //Database
    public static final String DATABASE_NAME = "PokemonDB_UserFavorite.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE = "UserFavorite";

    public class Column{
        public static final String id = "id";
        public static final String name = "name";
    }

    private String id;
    private String name;

    public UserFavorite(){

    }//Def Con

    public UserFavorite(String pokemonId, String pokemonName, String imgPath){
        id = pokemonId;
        name = pokemonName;
    }

    public String getId(){
        return id;
    }

    public String getName(){
        return name;
    }
}
