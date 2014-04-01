package alm.motiv.AlmendeMotivator;

import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.misc.CustomCallback;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.facebook.model.GraphUser;

import java.util.List;

public class MainMenuActivity extends Activity {
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainmenu);

        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int pos){
        switch (pos){
            case 0:
                k = new Intent(MainMenuActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(MainMenuActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(MainMenuActivity.this, ChallengeActivity.class);
                break;
            case 3:
                k = new Intent(MainMenuActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(MainMenuActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }

    public void switchMessages(View v) {
        switch (v.getId()) {
            case R.id.profileBut:
                k = new Intent(MainMenuActivity.this, ProfileActivity.class);
                break;
            case R.id.messagesBut:
                k = new Intent(MainMenuActivity.this, MessageActivity.class);
                break;
            //case R.id.challengesBut : k = new Intent(MainMenuActivity.this, ChallengesMenuActivity.class); break;
            case R.id.challengesBut:
                k = new Intent(MainMenuActivity.this, ChallengesMenuActivity.class);
                break;
            case R.id.friendsBut:
                k = new Intent(MainMenuActivity.this, FriendActivity.class);
                break;

            //This switch case enables the JSON Parser test button
            //case R.id.testBut : k = new Intent(MainMenuActivity.this, Test.class); break;
        }
        finish();
        startActivity(k);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logoutFacebook:
                FacebookManager.logout();
                startActivity(new Intent(this, FacebookMainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
