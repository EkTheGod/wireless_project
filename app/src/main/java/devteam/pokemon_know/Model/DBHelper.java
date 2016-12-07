package devteam.pokemon_know.Model;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import devteam.pokemon_know.R;


/**
 * Created by wachirapong on 11/9/2016 AD.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static final String TAG  = "DBHelper";
    private static final String DBName = "pokemonKnow.db";

    private SQLiteDatabase sqLiteDatabase;
    private Context context;

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POKEMON_TABLE = String.format("CREATE TABLE %s " +
                        "(%s INTEGER PRIMARY KEY , %s TEXT, %s INTEGER, %s TEXT)",
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


        String CREATE_USERFAVORITE_TABLE = String.format("CREATE TABLE %s " +
                        "(%s VARCHAR(8) PRIMARY KEY , %s TEXT)",
                UserFavorite.TABLE,
                Pokemon.Column.id,
                Pokemon.Column.name
        );
        Log.i(TAG, CREATE_USERFAVORITE_TABLE);

        // create friend table
        db.execSQL(CREATE_POKEMON_TABLE);
        db.execSQL(CREATE_POSTPOKEMON_TABLE);
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_USERFAVORITE_TABLE);
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
        this.context = context;
    }

    public void loadPokemonResource() throws IOException {
        sqLiteDatabase = this.getWritableDatabase();
        final Resources resources = context.getResources();
        InputStream inputStream = resources.openRawResource(R.raw.pokemon_list);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String[] strings = new String[2];
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                strings = TextUtils.split(line, "-");
                if (strings.length < 2) continue;
                Log.d("Loading", strings[0].trim() + " : " + strings[1].trim());
                long add = addPokemon2(new Pokemon(
                        strings[0].trim(), //ID
                        strings[1].trim(),  //Name
                        1, //Gen
                        "pokemon"+strings[1].trim() //Image Path
                ));

                if(add < 0)
                    Log.e("Error", "Unable to add pokemon " + strings[1].trim());
            }
        } catch (Exception e){
            Log.e("Error", "error while add pokemon");
            e.printStackTrace();
        } finally {

            addToFav(new Pokemon(
                    "5", //ID
                    "Charmeleon",  //Name
                    1, //Gen
                    "pokemon5" //Image Path
            ));

            sqLiteDatabase.close();
            reader.close();
        }
        Log.d("Finish", "DONE loading resource.");
    }



    public long addPokemon2(Pokemon pokemon) {
        ContentValues values = new ContentValues();

        values.put(Pokemon.Column.id, pokemon.getId());
        values.put(Pokemon.Column.name, pokemon.getName());
        values.put(Pokemon.Column.gen, pokemon.getGeneration());
        values.put(Pokemon.Column.imgPath, pokemon.getImgPath());

        return sqLiteDatabase.insert(Pokemon.TABLE, null, values);
    }

    public void addToFav(Pokemon pokemon){
        ContentValues values = new ContentValues();
        values.put(Pokemon.Column.name, pokemon.getName());
        sqLiteDatabase.insert(UserFavorite.TABLE, null, values);
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
        Log.d("DBHelper","Add Pokemon Name : "+pokemon.getName()+" Complete!");
    }

    public Pokemon getPokemon(String id) {

        sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query( Pokemon.TABLE,
                null,
                Pokemon.Column.id + " = ? ",
                new String[] { id },
                null,
                null,
                null,
                null);


        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        Pokemon pokemon = new Pokemon(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getString(3)
        );

        return pokemon;
    }

    public Pokemon getPokemonByName(String name) {

        sqLiteDatabase = this.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query( Pokemon.TABLE,
                null,
                Pokemon.Column.name + " = ? ",
                new String[] { name },
                null,
                null,
                null,
                null);


        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
        }else{
            return null;
        }

        Pokemon pokemon = new Pokemon(
                cursor.getString(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getString(3)
        );

        return pokemon;
    }

    public List<String> getFavoriteList(){
        sqLiteDatabase = this.getWritableDatabase();

        List<String> list = new ArrayList<>();
        String[] columnArgs = new String[]{
                Pokemon.Column.name
        };

        Cursor cursor = sqLiteDatabase.query(UserFavorite.TABLE, columnArgs, null, null, null, null, null); //(table, column, where, where arg, groupby, having, orderby)

        Log.d("Size of Favorite", String.valueOf(cursor.getCount()));
        if (cursor != null) {
            cursor.moveToFirst();
        }

        while(!cursor.isAfterLast()) {
            //Log.d("Auto complete load", cursor.getString(0));
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public List<String> getPokemonList(){
        sqLiteDatabase = this.getWritableDatabase();

        List<String> list = new ArrayList<>();
        String[] columnArgs = new String[]{
                Pokemon.Column.name
        };

        Cursor cursor = sqLiteDatabase.query(Pokemon.TABLE, columnArgs, null, null, null, null, null); //(table, column, where, where arg, groupby, having, orderby)

        Log.d("Size", String.valueOf(cursor.getCount()));
        if (cursor != null) {
            cursor.moveToFirst();
        }

        while(!cursor.isAfterLast()) {
            //Log.d("Auto complete load", cursor.getString(0));
            list.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public String getPokemonID(String pokemonName){
        sqLiteDatabase = this.getWritableDatabase();

        String ID = new String();
        String[] columnArgs = new String[]{
                Pokemon.Column.id
        };
        String whereClause = Pokemon.Column.name + "=?";
        String[] whereArgs = new String[] {
                pokemonName
        };

        Cursor cursor = sqLiteDatabase.query(Pokemon.TABLE, columnArgs, whereClause, whereArgs, null, null, null); //(table, column, where, where arg, groupby, having, orderby)

        Log.d("Size", String.valueOf(cursor.getCount()));
        if (cursor != null) {
            cursor.moveToFirst();
        }

        while(!cursor.isAfterLast()) {
            //Log.d("Auto complete load", cursor.getString(0));
            ID = "pokemon"+cursor.getString(0);
            cursor.moveToNext();
        }
        cursor.close();

        return ID;
    }
}//end class
