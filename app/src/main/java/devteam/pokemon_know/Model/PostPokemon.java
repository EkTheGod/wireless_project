package devteam.pokemon_know.Model;

/**
 * Created by wachirapong on 11/9/2016 AD.
 */

public class PostPokemon {
    //Database
    public static final String DATABASE_NAME = "PokemonDB_PostPokemon.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE = "PostPokemon";

    public class Column{
        public static final String postId = "postId";
        public static final String pokemonName = "pokemonName";
        public static final String Lat = "Lat";
        public static final String Long = "Long";
        public static final String startTime = "startTime";
        public static final String endTime = "endTime";
        public static final String user = "User";
    }

    private String postId;
    private String pokemonName; //FK
    private String Lat;
    private String Long;
    private String startTime;
    private String endTime;
    private String user;

    public PostPokemon() {

    }// def con

    public PostPokemon(String postId, String pokemonName, String Lat, String Long,String startTime, String endTime, String user) {
        this.postId = postId;
        this.pokemonName = pokemonName;
        this.Lat = Lat;
        this.Long = Long;
        this.user = user;
    }// def con

    public String getEndTime(){
        return endTime;
    }

}//end PostPokemon
