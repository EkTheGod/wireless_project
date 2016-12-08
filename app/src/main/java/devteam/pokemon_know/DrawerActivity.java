package devteam.pokemon_know;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

public class DrawerActivity extends AppCompatActivity {
    private Intent intent;
    private Drawer draw;

    private String className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_drawer);
    }

    protected void initDrawer(){
        className = this.getClass().getSimpleName().toString();
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                Picasso.with(imageView.getContext()).cancelRequest(imageView);
            }
        });
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName(getResources().getString(R.string.drawerHome)).withIcon(R.drawable.ic_home);


        PrimaryDrawerItem item2 = new PrimaryDrawerItem().withName(getResources().getString(R.string.drawerFavorite)).withIcon(R.drawable.ic_favorite);


        PrimaryDrawerItem item3 = new PrimaryDrawerItem().withName(getResources().getString(R.string.drawerHistory)).withIcon(R.drawable.ic_history);

        SecondaryDrawerItem secItem1 = new SecondaryDrawerItem().withName(getResources().getString(R.string.drawerLogout)).withIcon(R.drawable.ic_exit);


        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(Profile.getCurrentProfile().getName())
                                .withIcon(Profile.getCurrentProfile().getProfilePictureUri(460,460))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();


        draw = new DrawerBuilder()
                .withActivity(this)
//                .withRootView(R.id.frame_container)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(item1,item2,item3,new DividerDrawerItem(),secItem1)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 1: // Home
                                if( !className.equals("MainActivity") )
                                    finish();
                                    intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                break;
                            case 2: // Favorite
                                finish();
                                intent = new Intent(getApplicationContext(), Favorite.class);
                                startActivity(intent);
                                break;
                            case 3: //History
                                if( className.equals("MainActivity") ) {
                                    finish();
                                    Intent history = new Intent(getApplicationContext(), HistoryActivity.class);
                                    startActivity(history);
                                }else if( className.equals("Favorite") ){
                                    finish();
                                    Intent history = new Intent(getApplicationContext(), HistoryActivity.class);
                                    startActivity(history);
                                }
                                break;
                            case 5: //Logout
                                LoginManager.getInstance().logOut();
                                intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                break;
                            default:
                        }
                        return false;
                    }

                })
                .build();
        draw.resetDrawerContent();
    }//end InitDrawer

    @Override
    public void onBackPressed() {
        if (this.draw.isDrawerOpen()) {
            this.draw.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }
}
