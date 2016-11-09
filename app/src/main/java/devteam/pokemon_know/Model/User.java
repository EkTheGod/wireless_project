package devteam.pokemon_know.Model;

/**
 * Created by wachirapong on 11/9/2016 AD.
 */

public class User {
    //Database
    public static final String DATABASE_NAME = "PokemonDB_User.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE = "User";

    public class Column{
        public static final String UserId = "UserId";
        public static final String UserType = "UserType";
        public static final String UserName = "UserName";
        public static final String UserLastName = "UserLastName";

    }

    private String UserId;
    private String UserType; //FK
    private String UserName;
    private String UserLastName;

    public User() {

    }// def con

    public User(String userId, String userType, String userName, String userLastName) {
        this.UserId = userId;
        this.UserType = userType;
        this.UserName = userName;
        this.UserLastName = userLastName;
    }// def con
}//end User
