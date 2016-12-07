package devteam.pokemon_know;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.model.ContainerDrawerItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import devteam.pokemon_know.Model.DBHelper;
import devteam.pokemon_know.Model.Pokemon;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ekach on 4/12/2559.
 */

public class CustomAdapter extends ArrayAdapter<String> {

    private Activity mContext;
    private String[] strName;
    private int[] resId;
    private View row;
    private String isFav;
    private List<Map.Entry<String,String>> favList;
//    private ImageView check;

    public CustomAdapter(Activity mContext, String[] strName, int[] resId, String isFav, @Nullable List<Map.Entry<String,String>> f) {
        super(mContext,R.layout.favorite_list,strName);
        this.mContext = mContext;
        this.strName = strName;
        this.resId = resId;
        this.isFav = isFav;
        this.favList = f;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater mInflater = mContext.getLayoutInflater();

        row = mInflater.inflate(R.layout.favorite_list,null,true);

        final TextView textView = (TextView)row.findViewById(R.id.list_row_text);
        textView.setText(strName[position]);

        ImageView imageView = (ImageView)row.findViewById(R.id.list_row_image);
        imageView.setImageResource(resId[position]);

        final String name = strName[position];
        final ImageView check = (ImageView)row.findViewById(R.id.check);

        if(isFav.equalsIgnoreCase("fav")){
            check.setImageResource(R.drawable.fave_check);
            check.setTag("check");
        } else {
            check.setImageResource(R.drawable.fave_uncheck);
            check.setTag("uncheck");
        }


        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Log.d("Click add fave", (String)check.getTag());
                String tag = (String)check.getTag();
                if(tag.equalsIgnoreCase("check")) {
//                    Toast.makeText(mContext, "UNCHECK!!!!!!!", Toast.LENGTH_SHORT).show();
                    check.setImageResource(R.drawable.fave_uncheck);
                    check.setTag("uncheck");
                    removeFromFav(name);
                }
                else {
//                    Toast.makeText(mContext, "CHECK!!!!!!!", Toast.LENGTH_SHORT).show();
                    check.setImageResource(R.drawable.fave_check);
                    check.setTag("check");
                    addToFav(name);
                }
            }
        });

        return row;
    }

    private void addToFav(String name){
        DBHelper db = new DBHelper(getContext());
        db.addToFav(new DBHelper(getContext()).getPokemonID(name).substring(7), name);
    }

    private void removeFromFav(String name){
        DBHelper db = new DBHelper(getContext());
        db.removeFromFav(new DBHelper(getContext()).getPokemonID(name).substring(7), name);
    }
}
