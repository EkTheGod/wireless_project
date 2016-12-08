package devteam.pokemon_know;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.facebook.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Map;

import devteam.pokemon_know.HistoryList.HistoryAdapter;
import devteam.pokemon_know.Model.Pokemon;
import devteam.pokemon_know.Model.PostPokemon;
import devteam.pokemon_know.PokemonServer.PokemonWebService;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class HistoryActivity extends DrawerActivity {

    private ListView listView;
    private ArrayAdapter<Map<String, String>> arrayAdapter;
    private Socket mSocket;
    private LinearLayout progressHistory;
    {
        try {
            mSocket = IO.socket(PokemonWebService.getServer());
        } catch (URISyntaxException e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initDrawer();
        init();
    }

    private void init(){
        progressHistory = (LinearLayout) findViewById(R.id.historyProgress);
        mSocket.on("getHistory", onGetHistory);
        mSocket.connect();
        sendRequestHistory(null,Profile.getCurrentProfile().getId());
    }//end init

    private Emitter.Listener onGetHistory = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d("History",args[0].toString());

            final JSONArray jsonArray = (JSONArray) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressHistory.setVisibility(View.GONE);
                    arrayAdapter = new HistoryAdapter(getApplicationContext(),R.layout.listview_history, jsonArray);
                    listView = (ListView) findViewById(R.id.listView);
                    listView.setAdapter(arrayAdapter);
                }
            });
        }
    };

    private void sendRequestHistory(String socialType,String userId) {
        JSONObject historyRequest = new JSONObject();
        try {
            historyRequest.put("userId", userId);
            mSocket.emit("getHistory", historyRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}//end Activity
