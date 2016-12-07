package devteam.pokemon_know.HistoryList;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import devteam.pokemon_know.Model.DBHelper;
import devteam.pokemon_know.R;

/**
 * Created by wachirapong on 12/7/2016 AD.
 */

public class HistoryAdapter extends ArrayAdapter<Map<String, String>> {
    private int layoutResourceId;
    private List<Map<String, String>> data;
    private Context mContext;
    private DBHelper db;

    public HistoryAdapter(Context context, int resource, JSONArray objects) {
        super(context, resource, getListFromJsonArray(objects));
        layoutResourceId = resource;
        data = getListFromJsonArray(objects);
        mContext = context;
        db = new DBHelper(context);
    }

    // method converts JSONArray to List of Maps
    protected static List<Map<String, String>> getListFromJsonArray(JSONArray jsonArray) {
        ArrayList<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map;
        // fill the list
        for (int i = 0; i < jsonArray.length(); i++) {
            map = new HashMap<String, String>();
            try {
                JSONObject jo = (JSONObject) jsonArray.get(i);
                // fill map
                Iterator iter = jo.keys();
                while(iter.hasNext()) {
                    String currentKey = (String) iter.next();
                    map.put(currentKey, jo.getString(currentKey));
                }
                // add map to list
                list.add(map);
            } catch (JSONException e) {
                Log.e("JSON", e.getLocalizedMessage());
            }


        }
        return list;
    }


    @NonNull
    @Override
    public View getView(int position, View row, ViewGroup parent) {
        if (row == null)
        {
            LayoutInflater inflater = LayoutInflater.from(mContext);  // we get a reference to the activity
            row = inflater.inflate (R.layout.listview_history, parent, false);
        }
        ImageView ivPokemon = (ImageView) row.findViewById(R.id.lvHistoryPokemon);
        String pokemonResPath = db.getPokemonByName(data.get(position).get("pokemonName")).getImgPath();
        int resId = mContext.getResources().getIdentifier(pokemonResPath,"drawable",mContext.getPackageName());
        ivPokemon.setImageResource(resId);

        TextView tvHistoryPokemonName = (TextView) row.findViewById(R.id.lvHistoryPokemonName);
        tvHistoryPokemonName.setText( data.get(position).get("pokemonName") );

        TextView tvHistoryDate = (TextView) row.findViewById(R.id.lvHistoryDate);
        tvHistoryDate.setText( data.get(position).get("startTime") );

        TextView tvHistoryStatus = (TextView) row.findViewById(R.id.lvHistoryStatus);
        String active = ( data.get(position).get("active").equals("1") )? "Active": "Expired";
        tvHistoryStatus.setText( active );
        return row;
    }


}//end ArrayAdapter
