package devteam.pokemon_know;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

/**
 * Created by ekach on 4/12/2559.
 */

public class Favorite extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView fav;
    private Resources resources;
    int[] favResId;// = { R.drawable.pokemon5 };
    String[] favList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        init();
    }

    private void init(){
        toolbar = (Toolbar) findViewById(R.id.toolbarInFavorite);
        toolbar.setBackgroundColor(Color.rgb(1,117,164));

        fav = (ListView) findViewById(R.id.favlist);

        try {
            initList();
        }
        catch (IOException e){

        }
    }

    private List<Map.Entry<String,String>> loadFavList() throws IOException{
        resources = getApplicationContext().getResources();
        InputStream inputStream = resources.openRawResource(R.raw.favorite);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        List<Map.Entry<String,String>> list = new ArrayList<>();

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] strings = TextUtils.split(line, "-");
                if (strings.length < 2) continue;
                Log.d("Loading", strings[0].trim() + " : " + strings[1].trim());
                list.add(new AbstractMap.SimpleEntry<>(strings[0].trim(), strings[1].trim()));
            }
        } catch (Exception e){
            Log.e("Error", "error while loading favorite pokemon");
            e.printStackTrace();
        } finally {
            reader.close();
        }
        Log.d("Finish", "DONE loading favorite.");
        return list;
    }

    private void initList() throws IOException{
        final List<Map.Entry<String,String>> listResources;
        listResources = loadFavList();

//        Log.d("Size of favorite", String.valueOf(listResources.size()));

        favList = new String[listResources.size()];
        favResId = new int[listResources.size()];
        for(int i=0; i<listResources.size(); i++ ){
            Map.Entry<String, String> temp = listResources.get(i);

            int resourceId = resources.getIdentifier("pokemon"+temp.getKey(), "drawable",
                    getApplicationContext().getPackageName());

            favResId[i] = resourceId;
            Log.d("res ID", String.valueOf(resourceId));
            favList[i] = temp.getValue();
        }

        CustomAdapter adapter = new CustomAdapter(Favorite.this, favList, favResId);

        fav.setAdapter(adapter);
        fav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(Favorite.this, "You Clicked at " +favList[+ position], Toast.LENGTH_SHORT).show();
            }
        });
    }

    static int[] addElement(int[] a, int e) {
        a  = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }
}
