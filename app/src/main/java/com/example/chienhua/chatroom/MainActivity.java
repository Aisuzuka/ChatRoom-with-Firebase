package com.example.chienhua.chatroom;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends Activity {
    private TextView textView;
    private EditText editText2;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button button, button1, button2, button3;
    private DatabaseReference mRef;
    private StorageReference storageRef;
    private String userID;
    private StorageReference myStorageRef;
    private ImageView imageView;
    private String photoLink = "";
    private ChatRoomAdapter chatRoomAdapter;
    private ArrayList<DataStruct> dataStruct = new ArrayList<DataStruct>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void doUpdate(DataStruct data) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", data.name);
        map.put("message", data.message);
        map.put("messagePhoto", data.messagePhoto);
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
        button3 = (Button) findViewById(R.id.button6);
        imageView = (ImageView) findViewById(R.id.imageView);
        chatRoomAdapter = new ChatRoomAdapter(MainActivity.this, dataStruct);
        list.setAdapter(chatRoomAdapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance(); // Initial Firebase service
        mRef = database.getReference("chat");                       // Read data from Table 'chat'
        mRef.addValueEventListener(new ValueEventListener() {       // Listener datas' state in following case
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataStruct.clear();
                for (DataSnapshot message : dataSnapshot.getChildren()) {
                    DataStruct data = message.getValue(DataStruct.class);
                    dataStruct.add(data);
                }
                list.setSelection(chatRoomAdapter.getCount() - 1);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        mAuth = FirebaseAuth.getInstance();                         // Initial FirbaseAuth service
        mAuthListener = new FirebaseAuth.AuthStateListener() {       // Setting Auth's Listener_mode

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {                                   // If the state isn't login, user will be null
                    final String name = user.getDisplayName();
                    userID = user.getUid();
                    textView.setText("Welcome User : " + name);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DataStruct data = new DataStruct();
                            data.name = String.valueOf(name);
                            data.message = String.valueOf(editText2.getText());
                            data.messagePhoto = photoLink;
                            editText2.setText("");
                            doUpdate(data);
                            photoLink = "";
                            imageView.setImageBitmap(null);
                            imageView.setVisibility(View.GONE);
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
                            i.setClass(MainActivity.this, LoginActivity.class);
                            MainActivity.this.startActivityForResult(i, 0);
                        }
                    });
                    button1.setText("LOGIN");
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent();
                            i.setClass(MainActivity.this, LoginActivity.class);
                            MainActivity.this.startActivity(i);
                        }
                    });
                    button2.setClickable(true);
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent();
                            i.setClass(MainActivity.this, RegistActivity.class);
                            MainActivity.this.startActivity(i);
                        }
                    });

                }
            }
        };

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getImage = new Intent(Intent.ACTION_GET_CONTENT);
                getImage.addCategory(Intent.CATEGORY_OPENABLE);
                getImage.setType("image/jpeg");
                startActivityForResult(getImage, 0);
            }
        });

        mAuth.addAuthStateListener(mAuthListener);              // When Activity onRun, must be add to listener
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);           // When Activity OnStop, must be removed. Or won't logout
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if(data != null) {
                    Uri select = data.getData();
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    storageRef = storage.getReferenceFromUrl("gs://chatroom-b8b0b.appspot.com");
//                Uri fireFile = Uri.fromFile(new File(picturePath));
                    myStorageRef = storageRef.child(userID + "/" + select.getLastPathSegment());
                    UploadTask uploadTask = myStorageRef.putFile(select);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Photo", e.getMessage());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            photoLink = String.valueOf(taskSnapshot.getDownloadUrl());
                            new AsyncTask<String, Void, Bitmap>() {

                                @Override
                                protected Bitmap doInBackground(String... params) {
                                    URL url = null;
                                    Bitmap bitmap = null;
                                    try {
                                        url = new URL(params[0]);
                                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                        connection.setDoInput(true);
                                        connection.connect();
                                        InputStream input = connection.getInputStream();
                                        bitmap = BitmapFactory.decodeStream(input);
                                    } catch (MalformedURLException e) {
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return bitmap;
                                }

                                @Override
                                protected void onPostExecute(Bitmap bitmap) {
                                    imageView.setVisibility(View.VISIBLE);
                                    imageView.setImageBitmap(bitmap);
                                    super.onPostExecute(bitmap);
                                }
                            }.execute(String.valueOf(taskSnapshot.getDownloadUrl()));
                        }
                    });
                }
                    break;

        }
    }
}
