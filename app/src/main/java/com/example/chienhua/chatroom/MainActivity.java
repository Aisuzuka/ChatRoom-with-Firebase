package com.example.chienhua.chatroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {
    private TextView textView;
    private EditText editText2;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button button, button1, button2;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void doUpdate(DataStruct data) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", data.name);
        map.put("message", data.message);
        mRef.push().setValue(map);
    }

    @Override
    protected void onStart() {
        super.onStart();
        textView = (TextView) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        final ListView list = (ListView) findViewById(R.id.listView);
        button = (Button) findViewById(R.id.button2);
        button1 = (Button) findViewById(R.id.button3);
        button2 = (Button) findViewById(R.id.button4);
        final ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1);

        list.setAdapter(myAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance(); // Initial Firebase service
        mRef = database.getReference("chat");                       // Read data from Table 'chat'
        mRef.addValueEventListener(new ValueEventListener() {       // Listener datas' state in following case
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myAdapter.clear();
                for (DataSnapshot message : dataSnapshot.getChildren()) {
                    DataStruct data = message.getValue(DataStruct.class);
                    myAdapter.add(data.name + " : " + data.message);
                }
                list.setSelection(myAdapter.getCount() - 1);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


        mAuth = FirebaseAuth.getInstance();                         // Initial FirbaseAuth service
        mAuthListener = new FirebaseAuth.AuthStateListener(){       // Setting Auth's Listener_mode

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){                                   // If the state isn't login, user will be null
                    final String name = user.getDisplayName();
                    textView.setText("Welcome User : " + name);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DataStruct data = new DataStruct();
                            data.name = String.valueOf(name);
                            data.message = String.valueOf(editText2.getText());
                            editText2.setText("");
                            doUpdate(data);
                        }
                    });
                    button1.setText("LOGOUT");
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseAuth.getInstance().signOut();
                        }
                    });
                    button2.setClickable(false);
                } else {
                    textView.setText("Press Buttom to Login");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent();
                            i.setClass( MainActivity.this, LoginActivity.class);
                            MainActivity.this.startActivityForResult(i, 0);
                        }
                    });
                    button1.setText("LOGIN");
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent();
                            i.setClass( MainActivity.this, LoginActivity.class);
                            MainActivity.this.startActivity(i);
                        }
                    });
                    button2.setClickable(true);
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent();
                            i.setClass( MainActivity.this, RegistActivity.class);
                            MainActivity.this.startActivity(i);
                        }
                    });

                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);              // When Activity onRun, must be add to listener
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);           // When Activity OnStop, must be removed. Or won't logout
    }
}
