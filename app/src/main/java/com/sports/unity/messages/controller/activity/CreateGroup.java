package com.sports.unity.messages.controller.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.sports.unity.Database.SportsUnityDBHelper;
import com.sports.unity.R;
import com.sports.unity.XMPPManager.XMPPClient;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.util.ArrayList;

public class CreateGroup extends AppCompatActivity {
    ListView contacts;
    EditText groupName;
    ArrayAdapter<String> adapter;
    ArrayList<SportsUnityDBHelper.GroupParticipants> list = new ArrayList<>();
    ArrayList<String> users = new ArrayList<>();
    ArrayList<String> usernames = new ArrayList<>();
    Button create;
    private MultiUserChat muc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        contacts = (ListView) findViewById(R.id.contactsGroupAdd);
        contacts.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        groupName = (EditText) findViewById(R.id.groupNameEnter);

        list = SportsUnityDBHelper.getInstance(this).readContactNames();

        for (int i = 0; i < list.size(); i++) {
            usernames.add(list.get(i).name);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, usernames);
        contacts.setAdapter(adapter);

        create = (Button) findViewById(R.id.createButton);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = contacts.getCheckedItemPositions();
                if (checked != null) {
                    for (int i = 0; i < checked.size(); i++) {
                        // Item position in adapter
                        int position = checked.keyAt(i);
                        // Add users if it is checked i.e.) == TRUE!
                        if (checked.valueAt(i)) {
                            users.add(list.get(position).name);
                        }
                    }
                }
                if (!groupName.getText().toString().trim().isEmpty())
                    createGroup();
                else
                    Toast.makeText(CreateGroup.this, "Please enter a Name", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void createGroup() {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(XMPPClient.getConnection());
        muc = manager.getMultiUserChat(groupName.getText().toString() + "@conference.mm.io");
        try {
            muc.create(groupName.getText().toString());
            muc.sendConfigurationForm(new Form(DataForm.Type.submit));
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        }

        inviteUsers();
    }


    private void inviteUsers() {
        for (int i = 0; i < users.size(); i++) {
            try {
                muc.invite(list.get(i).number + "@mm.io", "Meet me in this excellent room");
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_group, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
