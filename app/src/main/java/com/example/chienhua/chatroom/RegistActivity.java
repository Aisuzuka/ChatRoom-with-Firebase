package com.example.chienhua.chatroom;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Created by chienhua on 2016/7/4.
 */
public class RegistActivity extends Activity {
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);
        editText1 = (EditText) findViewById(R.id.editText5);
        editText2 = (EditText) findViewById(R.id.editText6);
        editText3 = (EditText) findViewById(R.id.editText7);
        btn = (Button) findViewById(R.id.button5);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);                           // If Auth_Listener isn't remove, will Listener Auth state for ever, even Activity had destroyed
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = String.valueOf(editText2.getText());
                String password = String.valueOf(editText3.getText());
                mAuth.createUserWithEmailAndPassword(email, password).            // Use 'createUserWithEmailAndPassWord' to create an account
                        addOnFailureListener(new OnFailureListener() {            // If create an account has finish, check the result
                            @Override
                            public void onFailure(@NonNull Exception e) {         // We can use 'onFailure' or 'onCompelete' to know what state now
                                Log.e("Regist", e.getMessage());
                            }
                        });
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    String name = String.valueOf(editText1.getText());
                    UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(name).build();     //Change user's profile, use 'UserProfileChangeRequest' to setting detail
                    user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {                          //And use 'updateProfile' to update
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {                                                      //'onComplete' will be use when update finished and completed
                            if (task.isSuccessful()) {
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {                                                           //'onFailure' will be use when update finished but failed
                            Log.e("Profile", e.getMessage());
                        }
                    });
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }
}
