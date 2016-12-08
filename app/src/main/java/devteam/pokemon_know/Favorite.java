package devteam.pokemon_know;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.support.v7.widget.SearchView;

import devteam.pokemon_know.Model.DBHelper;

/**
 * Created by ekach on 4/12/2559.
 */

public class Favorite extends DrawerActivity {

    private Toolbar toolbar;
    private ListView fav;
    private SearchView searchView;
    private ImageView pinmap;
    int[] favResId;
    String[] favList;
    private List<Map.Entry<String,String>> listResources;
    private LinearLayout r;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        initDrawer();
        init();
    }

    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.rgb(1,117,164));

        fav = (ListView) findViewById(R.id.favlist);
        fav.setBackgroundColor(Color.rgb(255,255,255));

        pinmap = (ImageView) findViewById(R.id.favToSearch);
        pinmap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Bundle b=new Bundle();
                b.putStringArray("favoriteList", favList);

                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                main.putExtras(b);
                startActivity(main);
            }
        });

        r = (LinearLayout) findViewById(R.id.refresh);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });
        load();
    }

    private void load(){
        try {
            loadList();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

//    private List<Map.Entry<String,String>> loadFavList() throws IOException{
//        resources = getApplicationContext().getResources();
//        InputStream inputStream = resources.openRawResource(R.raw.favorite);
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//
//        List<Map.Entry<String,String>> list = new ArrayList<>();
//
//        try {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                String[] strings = TextUtils.split(line, "-");
//                if (strings.length < 2) continue;
//                Log.d("Loading", strings[0].trim() + " : " + strings[1].trim());
//                list.add(new AbstractMap.SimpleEntry<>(strings[0].trim(), strings[1].trim()));
//            }
//        } catch (Exception e){
//            Log.e("Error", "error while loading favorite pokemon");
//            e.printStackTrace();
//        } finally {
//            reader.close();
//        }
//        Log.d("Finish", "DONE loading favorite.");
//        return list;
//    }

    private void loadList() throws IOException{
//        final List<Map.Entry<String,String>> listResources;
//        listResources = loadFavList();

        listResources = new DBHelper(getApplicationContext()).getFavoriteList();


//        Log.d("Size of favorite", String.valueOf(listResources.size()));

        favList = new String[listResources.size()];
//        favList = s.toArray(favList);

        favResId = new int[listResources.size()];
        for(int i=0; i<listResources.size(); i++ ){
            Map.Entry<String, String> temp = listResources.get(i);

            int resourceId = getApplicationContext().getResources().getIdentifier("pokemon"+temp.getKey(), "drawable",
                    getApplicationContext().getPackageName());

            favResId[i] = resourceId;
            Log.d("res ID", String.valueOf(resourceId));
            favList[i] = temp.getValue();
        }

        CustomAdapter adapter = new CustomAdapter(Favorite.this, favList, favResId, "fav", listResources);
        fav.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu) {
        getMenuInflater().inflate( R.menu.menu, menu);

        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();

//        searchView.setSuggestionsAdapter(searchFav());
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length() >= 2) {
                    searchFav(query.substring(0, 1).toUpperCase() + query.substring(1).toLowerCase());
                }

                if( !searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });

//        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
//            @Override
//            public boolean onSuggestionSelect(int position) {
//                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
//                String feedName = cursor.getString(4);
//                searchView.setQuery(feedName, false);
//                searchView.clearFocus();
//                return true;
//            }
//
//            @Override
//            public boolean onSuggestionClick(int position) {
//                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
//                String feedName = cursor.getString(4);
//                searchView.setQuery(feedName, false);
//                searchView.clearFocus();
//                return true;
//            }
//        });
        return true;
    }

    private void searchFav(String query){
        final DBHelper db = new DBHelper(getApplicationContext());
        String pokemon = db.getPokemonID(query);
        if(pokemon.length() == 0){
            Toast.makeText(Favorite.this, "Pokemon name : " + query + " not found", Toast.LENGTH_SHORT).show();
        }
        else {
            int resourceId = getApplicationContext().getResources().getIdentifier(db.getPokemonID(query.trim()), "drawable",
                    getApplicationContext().getPackageName());
            Log.d("Favorite", "pokemonId; " + db.getPokemonID(query) + "name: " + query);
            String statemet;
            if (findPokemonInFavList(query)) {
                statemet = "fav";
            } else {
                statemet = "notfav";
            }
            int[] pokemonId = { resourceId };
            CustomAdapter adapter = new CustomAdapter(Favorite.this, new String[]{query}, pokemonId, statemet, listResources);
            fav.setAdapter(adapter);
        }
    }

    private boolean findPokemonInFavList(String name){
        for (String s : favList) {
            int i = s.indexOf(name);
            if (i >= 0) {
                return true;
            }
        }
        return false;
    }
}
