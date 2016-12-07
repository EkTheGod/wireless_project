package devteam.pokemon_know;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

import devteam.pokemon_know.HistoryList.HistoryAdapter;
import devteam.pokemon_know.Model.Pokemon;
import devteam.pokemon_know.Model.PostPokemon;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class HistoryActivity extends DrawerActivity {

    private ListView listView;
    private Socket mSocket;
    private ArrayAdapter<Map<String, String>> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initDrawer();
        init();

    }

    private void init(){
        mSocket.on("getHistory", onGetHistory);
        mSocket.connect();
        sendRequestHistory(null,Profile.getCurrentProfile().getId());
    }//end init

    private Emitter.Listener onGetHistory = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            JSONArray asd = new JSONArray();
            arrayAdapter = new HistoryAdapter(getApplicationContext(),R.layout.listview_history, asd);

            listView = (ListView) findViewById(R.id.listView);
            listView.setAdapter(arrayAdapter);
//            final JSONObject object = (JSONObject) args[0];
//            try {
//                Log.d("Get Pokemon", object.getString("pokemonName"));
//                if (pokemonHashMap.containsKey(object.getString("_id").toString()))
//                    return;
//                PostPokemon postPokemon = new PostPokemon(
//                        object.getString("postId").toString(),
//                        object.getString("pokemonName").toString(),
//                        object.getString("lat").toString(),
//                        object.getString("long").toString(),
//                        object.getString("startTime").toString(),
//                        object.getString("endTime").toString(),
//                        object.getString("user").toString()
//                );
//                pokemonHashMap.put(object.getString("_id").toString(), postPokemon);
//                final Pokemon pokemon = db.getPokemonByName(object.getString("pokemonName").toString());
//                if (pokemon != null) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                createMarker(pokemon.getId(), pokemon.getName(), object.getDouble("lat"), object.getDouble("long"));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }

        }
    };

    private void sendRequestHistory(String socialType,String userId) {
        JSONObject historyRequest = new JSONObject();
        try {
            historyRequest.put("user", userId);
            mSocket.emit("addPokemon", historyRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}//end Activity
