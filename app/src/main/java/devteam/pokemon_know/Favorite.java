package devteam.pokemon_know;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import devteam.pokemon_know.Model.DBHelper;

/**
 * Created by ekach on 4/12/2559.
 */

public class Favorite extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView fav;
    int[] resId = { R.drawable.pokemon5 };
    String[] list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        init();
        initList();
    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbarInFavorite);
        toolbar.setBackgroundColor(Color.rgb(1,117,164));

        fav = (ListView) findViewById(R.id.favlist);
    }

    private void initList() {
        final DBHelper db = new DBHelper(this);
        final List<String> ls = db.getFavoriteList();

        Log.d("Size of favorite", String.valueOf(ls.size()));
        list = new String[ls.size()];
//        resId = new int[ls.size()];
        list = ls.toArray(list);

        Resources resources = getApplicationContext().getResources();
        Log.d("Show ID", db.getPokemonID(list[0]));

//        int resourceId = resources.getIdentifier(db.getPokemonID(list[0]), "drawable",
//                getApplicationContext().getPackageName());
//        //Log.d("Show ID", StrresourceId);
//
//        resId = addElement(resId, R.drawable.pokemon5);

        CustomAdapter adapter = new CustomAdapter(Favorite.this, list, resId);

        fav.setAdapter(adapter);
        fav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(Favorite.this, "You Clicked at " +list[+ position], Toast.LENGTH_SHORT).show();
            }
        });
    }

    static int[] addElement(int[] a, int e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }
}
