package alm.motiv.AlmendeMotivator;

/**
 * Created by AsterLaptop on 4/13/14.
 */

import java.util.ArrayList;
import java.util.List;

import alm.motiv.AlmendeMotivator.adapters.EntryAdapter;
import alm.motiv.AlmendeMotivator.facebook.FacebookMainActivity;
import alm.motiv.AlmendeMotivator.facebook.FacebookManager;
import alm.motiv.AlmendeMotivator.models.ChallengeHeader;
import alm.motiv.AlmendeMotivator.adapters.Item;
import alm.motiv.AlmendeMotivator.models.Challenge;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mongodb.*;

public class ChallengeOverviewActivity extends Activity implements OnItemClickListener {
    Intent k;
    private String[] mMenuOptions;
    private ListView mDrawerList;
    ArrayList<Item> items = new ArrayList<Item>();
    ListView listview = null;
    DatabaseThread DT = new DatabaseThread();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challengeoverview);

        listview = (ListView) findViewById(R.id.listView_main);
        DT.execute();

        mMenuOptions = getResources().getStringArray(R.array.profile_array);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item_menu, mMenuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
    }

    public void initListview() {
        List<DBObject> send = DT.sendChallenges;
        List<DBObject> received = DT.receivedChallenges;

        items.add(new ChallengeHeader("Challenges you send"));
        if (send != null) {
            for (DBObject aChallenge : send) {
                items.add((Item) aChallenge);
            }
        } else {
            items.add(new ChallengeHeader("No challenges send"));
        }

        items.add(new ChallengeHeader("Challenges you received"));
        if (received != null) {
            for (DBObject aChallenge : received) {
                items.add((Item) aChallenge);
            }
        } else {
            items.add(new ChallengeHeader("No challenges received"));
        }

        EntryAdapter adapter = new EntryAdapter(this, items);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        showPopUp();
    }

    private void showPopUp() {
        AlertDialog.Builder helpBuilder = new AlertDialog.Builder(this);
        helpBuilder.setTitle("Exit");
        helpBuilder.setMessage("Are you sure you want to exit Sportopia?");
        helpBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
        );

        helpBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        AlertDialog helpDialog = helpBuilder.create();
        helpDialog.show();
    }


    public void onCreatePressed(View v) {
        startActivity(new Intent(this, ChallengeCreateActivity.class));
    }


    @Override
    public void onItemClick(AdapterView arg0, View arg1, int position, long arg3) {

        Challenge item = (Challenge) items.get(position);
        //Toast.makeText(this, "You clicked " + item.getTitle() , Toast.LENGTH_SHORT).show();

        //Open the challengeViewActivity and give the current selected Challenge to the activity
        Intent intent = new Intent(this, ChallengeViewActivity.class);
        //TODO This works as a cheap workaround because I can't send a Serializable object. Fix
        /*intent.putExtra("title", item.getTitle());
        intent.putExtra("challenger", item.getChallenger());
        intent.putExtra("challengee", item.getChallengee());
        intent.putExtra("content", item.getContent());
        intent.putExtra("evidenceAmount", item.getEvidenceAmount());
        intent.putExtra("evidenceType", item.getEvidenceType());
        intent.putExtra("reward", item.getReward());
        intent.putExtra("status", item.getStatus());*/
        intent.putExtra("id", item.getID().toString());
        /*intent.putExtra("comments", item.getComments());*/
        this.startActivity(intent);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    public void selectItem(int pos) {
        switch (pos) {
            case 0:
                k = new Intent(ChallengeOverviewActivity.this, ProfileActivity.class);
                break;
            case 1:
                k = new Intent(ChallengeOverviewActivity.this, MessageActivity.class);
                break;
            case 2:
                k = new Intent(ChallengeOverviewActivity.this, ChallengeOverviewActivity.class);
                break;
            case 3:
                k = new Intent(ChallengeOverviewActivity.this, FriendActivity.class);
                break;
            case 4:
                FacebookManager.logout();
                k = new Intent(ChallengeOverviewActivity.this, FacebookMainActivity.class);
                break;
        }
        finish();
        startActivity(k);
    }

    private class DatabaseThread extends AsyncTask<String, String, String> {
        public List<DBObject> sendChallenges = null;
        public List<DBObject> receivedChallenges = null;

        ProgressDialog simpleWaitDialog;

        @Override
        protected void onPreExecute() {
            simpleWaitDialog = ProgressDialog.show(ChallengeOverviewActivity.this,
                    "Please wait", "Processing");
        }

        @Override
        protected void onPostExecute(String string) {
            simpleWaitDialog.setMessage("Process completed.");
            simpleWaitDialog.dismiss();
            initListview();
        }

        @Override
        protected String doInBackground(String... args) {
            MongoClient client = Database.getInstance();
            DB db = client.getDB(Database.uri.getDatabase());
            DBCollection challengeCollection = db.getCollection("challenge");
            challengeCollection.setObjectClass(Challenge.class);

            //find al the challenges the user send
            Challenge query1 = new Challenge();
            query1.put("challenger", Cookie.getInstance().userEntryId);
            sendChallenges = challengeCollection.find(query1).toArray();

            //find al the challenges the user received
            Challenge query2 = new Challenge();
            query2.put("challengee", Cookie.getInstance().userEntryId);
            receivedChallenges = challengeCollection.find(query2).toArray();

            return "succes";
        }

    }
}
