package com.example.chienhua.chatroom;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by chienhua on 2016/7/4.
 */
public class LoginActivity extends Activity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        final EditText edit1 = (EditText) findViewById(R.id.editText3);
        final EditText edit2 = (EditText) findViewById(R.id.editText4);
        Button btn = (Button) findViewById(R.id.button);


        mAuth = FirebaseAuth.getInstance();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signInWithEmailAndPassword(String.valueOf(edit1.getText()), String.valueOf(edit2.getText()))      //'signInWithEmailAndPassword' could provide login method
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                finish();
                            }
                        });
            }
        });
    }
}