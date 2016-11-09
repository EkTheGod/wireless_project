package devteam.pokemon_know;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;

import devteam.pokemon_know.Model.DBHelper;

/**
 * Created by ekach on 8/11/2559.
 */

public class splash_screen extends Activity {

    private Handler myHandler;
    private DBHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        }, 3000);
        initDatabase();
    }

    private void initDatabase(){
        mHelper = new DBHelper(this);
    }
}//end splash screen
