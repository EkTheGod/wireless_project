package devteam.pokemon_know.Model;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by wachirapong on 11/9/2016 AD.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG  = "DBHelper";
    private static final String DBName = "pokemonKnow.db";

    private SQLiteDatabase sqLiteDatabase;

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POKEMON_TABLE = String.format("CREATE TABLE %s " +
                        "(%s VARCHAR(8) PRIMARY KEY , %s TEXT, %s INTEGER, %s TEXT)",
                Pokemon.TABLE,
                Pokemon.Column.id,
                Pokemon.Column.name,
                Pokemon.Column.gen,
                Pokemon.Column.imgPath);
        Log.i(TAG, CREATE_POKEMON_TABLE);



        String CREATE_POSTPOKEMON_TABLE = String.format("CREATE TABLE %s " +
                        "(%s VARCHAR(8) PRIMARY KEY , %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT)",
                PostPokemon.TABLE,
                PostPokemon.Column.postId,
                PostPokemon.Column.pokemonName,
                PostPokemon.Column.Lat,
                PostPokemon.Column.Long,
                PostPokemon.Column.startTime,
                PostPokemon.Column.endTime,
                PostPokemon.Column.user);
        Log.i(TAG, CREATE_POSTPOKEMON_TABLE);

        String CREATE_USER_TABLE = String.format("CREATE TABLE %s " +
                        "(%s VARCHAR(8) PRIMARY KEY , %s TEXT, %s TEXT, %s TEXT)",
                User.TABLE,
                User.Column.UserId,
                User.Column.UserType,
                User.Column.UserName,
                User.Column.UserLastName
                );
        Log.i(TAG, CREATE_USER_TABLE);

        // create friend table
        db.execSQL(CREATE_POKEMON_TABLE);
        db.execSQL(CREATE_POSTPOKEMON_TABLE);
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_POKEMON_TABLE = "DROP TABLE IF EXISTS " + Pokemon.TABLE;

        db.execSQL(DROP_POKEMON_TABLE);

        String DROP_POSTPOKEMON_TABLE = "DROP TABLE IF EXISTS " + PostPokemon.TABLE;

        db.execSQL(DROP_POSTPOKEMON_TABLE);

        String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + User.TABLE;

        db.execSQL(DROP_POSTPOKEMON_TABLE);

        Log.i(TAG, "Upgrade Database from " + oldVersion + " to " + newVersion);

        onCreate(db);
    }

    public DBHelper(Context context){
        super(context,DBName,null,1);
    }

    public void addPokemon(Pokemon pokemon){
        sqLiteDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        //values.put(Friend.Column.ID, friend.getId());
        values.put(Pokemon.Column.id, pokemon.getId());
        values.put(Pokemon.Column.name, pokemon.getName());
        values.put(Pokemon.Column.gen, pokemon.getGeneration());
        values.put(Pokemon.Column.imgPath, pokemon.getImgPath());

        sqLiteDatabase.insert(Pokemon.TABLE, null, values);

        sqLiteDatabase.close();
    }
}//end class
