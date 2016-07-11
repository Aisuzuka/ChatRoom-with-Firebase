package com.example.chienhua.chatroom;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private LinearLayout mTabLayout;
    private LinearLayout mTabLayoutB;
    private String photoLink = "";
    private ChatRoomAdapter chatRoomAdapter;
    private ArrayList<DataStruct> dataStruct = new ArrayList<DataStruct>();
    int[] mTabOrigionLocation = new int[4];
    private ListView list;
    private LinearLayout listViewHeight;
    private LinearLayout listViewBottom;
    private int[] listViewLocation = new int[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        list = (ListView) findViewById(R.id.listView);
        button = (Button) findViewById(R.id.button2);
        button1 = (Button) findViewById(R.id.button3);
        button2 = (Button) findViewById(R.id.button4);
        button3 = (Button) findViewById(R.id.button6);
        imageView = (ImageView) findViewById(R.id.imageView);
        mTabLayout = (LinearLayout) findViewById(R.id.mTabLayout);
        mTabLayoutB = (LinearLayout) findViewById(R.id.mTabLayoutBottom);
        listViewHeight = (LinearLayout) findViewById(R.id.listViewHeight);
        listViewBottom = (LinearLayout) findViewById(R.id.listViewBottom);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int[] location = new int[2];
        mTabLayout.getLocationOnScreen(location);
        mTabOrigionLocation[0] = location[0];
        mTabOrigionLocation[1] = location[1];
        mTabLayoutB.getLocationOnScreen(location);
        mTabOrigionLocation[2] = location[0];
        mTabOrigionLocation[3] = location[1];
        Log.e("OriginLocation", String.valueOf(mTabOrigionLocation[1]));

        listViewHeight.getLocationOnScreen(location);
        listViewLocation[0] = location[0];
        listViewLocation[1] = location[1];
        listViewBottom.getLocationOnScreen(location);
        listViewLocation[2] = location[0];
        listViewLocation[3] = location[1];

//        ViewGroup.LayoutParams params = list.getLayoutParams();
//        params.height = 400;
//        list.setLayoutParams(params);
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
        chatRoomAdapter = new ChatRoomAdapter(MainActivity.this, dataStruct);
        list.setAdapter(chatRoomAdapter);

        list.setOnTouchListener(new View.OnTouchListener() {
            float pastY;
            float nowY;
            boolean isDowntoUp = false;
            HideActionBar hideActionBar;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        Log.e("Down", String.valueOf(event.getY()));
                        pastY = event.getY();
                    case MotionEvent.ACTION_MOVE:
                        nowY = event.getY();
                        ViewConfiguration vc = ViewConfiguration.get(MainActivity.this);
                        int slop = vc.getScaledTouchSlop();
//                        if(Math.abs(nowY - pastY) < slop)
//                            return true;
                        Log.e("Move", String.valueOf(event.getY()));
                        if (pastY > nowY) {
                            isDowntoUp = true;
                        }
                        if (nowY > pastY) {
                            isDowntoUp = false;
                        }
                        if (nowY != pastY) {
                            hideActionBar = new HideActionBar(isDowntoUp, (int) (nowY - pastY));
                        }
                        pastY = nowY;
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.e("Up", String.valueOf(event.getY()));
                        int[] nowLoaction = new int[2];
                        mTabLayoutB.getLocationOnScreen(nowLoaction);
                        int viewHeight = mTabOrigionLocation[3] - mTabOrigionLocation[1];
                        if (isDowntoUp) {
                            if (nowLoaction[1] < mTabOrigionLocation[1] + viewHeight / 2) {
                                mTabLayout.setTranslationY(-viewHeight);
                                listViewHeight.setTranslationY(-viewHeight);
                                int[] listNowLocation = new int[2];
                                listViewHeight.getLocationOnScreen(listNowLocation);
                                ViewGroup.LayoutParams params = list.getLayoutParams();
                                params.height = listViewLocation[3] - listNowLocation[1] ;
                                list.setLayoutParams(params);
                                listViewHeight.setLayoutParams(params);
                            } else {
                                mTabLayout.setTranslationY(0);
                                listViewHeight.setTranslationY(0);
                            }

                        } else {
                            if (nowLoaction[1] > mTabOrigionLocation[1] + viewHeight / 2) {
                                mTabLayout.setTranslationY(0);
                                listViewHeight.setTranslationY(0);
                            } else {
                                mTabLayout.setTranslationY(-viewHeight);
                                listViewHeight.setTranslationY(-viewHeight);

                            }

                        }
                        pastY = 0;
                        nowY = 0;
                        break;
                }
                return false;
            }

            class HideActionBar{
                int position;
                boolean tag;
                boolean mode = false;
                public HideActionBar(boolean tag, int position){
                    this.tag = tag;
                    this.position = position;
                    test();
                };
                public HideActionBar(boolean tag, boolean mode){
                    this.tag = tag;
                    this.mode = mode;
                    test();
                };
                private void test() {
                int move = 0;
                int[] mTabNowLoaction = new int[2];
                mTabLayout.getLocationOnScreen(mTabNowLoaction);
                int[] listNowLocation = new int[2];
                listViewHeight.getLocationOnScreen(listNowLocation);
                int viewHeight = mTabOrigionLocation[3] - mTabOrigionLocation[1];
//                Log.e("viewHeight", String.valueOf(viewHeight));
                if (tag) {
                    if (mTabNowLoaction[1] + viewHeight + position <= mTabOrigionLocation[1]) {
                        move = -viewHeight;
                    } else {
                        move = mTabNowLoaction[1] - mTabOrigionLocation[1] + position;
                    }
                    mTabLayout.setTranslationY(move);
                    listViewHeight.setTranslationY(move);
                    ViewGroup.LayoutParams params = list.getLayoutParams();
                    params.height = listViewLocation[3] - listNowLocation[1];
                    list.setLayoutParams(params);
                    listViewHeight.setLayoutParams(params);

                    Log.e("nowLoaction[1]", String.valueOf(mTabNowLoaction[1]));
                    Log.e("position", String.valueOf(position));
                    Log.e("setLocation", String.valueOf(move));

                } else {
                    if (mTabNowLoaction[1] + position >= mTabOrigionLocation[1])
                        move = 0;
                    else {
                        move = mTabNowLoaction[1] - mTabOrigionLocation[1] + position;
                    }
                    mTabLayout.setTranslationY(move);
                    listViewHeight.setTranslationY(move);
                }
                }
            }
        });

        FirebaseDatabase database = FirebaseDatabase.getInstance(); // Initial Firebase service
        mRef = database.getReference("chat");                       // Read data from Table 'chat'
        DataStruct[] data = new DataStruct[10];
        for (int i = 0; i < data.length; i++) {
            data[i] = new DataStruct();
            data[i].message = String.valueOf(i);
            data[i].name = String.valueOf(i);
            data[i].messagePhoto = "";
            dataStruct.add(data[i]);
        }
        mRef.addValueEventListener(new ValueEventListener() {       // Listener datas' state in following case
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                dataStruct.clear();
//                for (DataSnapshot message : dataSnapshot.getChildren()) {
//                    DataStruct data = message.getValue(DataStruct.class);
//                    dataStruct.add(data);
//                }
//                list.setSelection(chatRoomAdapter.getCount() - 1);
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
                if (data != null) {
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
