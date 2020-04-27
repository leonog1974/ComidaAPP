package com.example.escritorio.merkapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ActivityLogin extends AppCompatActivity {


    private ProgressDialog mPdLogin;
    private EditText mEditUsu, mEditCon;
    private Button mBtnAcceder, mBtnRegistar;




    private FirebaseAuth autenticar;
    private FirebaseUser user;
    private DatabaseReference referenciaBD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEditUsu = (EditText)findViewById(R.id.txtusu);
        mEditCon = (EditText)findViewById(R.id.txtcont);
        mBtnRegistar = (Button)findViewById(R.id.btnregistrar);
        mBtnAcceder = (Button)findViewById(R.id.btnacceder);

        mPdLogin = new ProgressDialog(this);

        autenticar = FirebaseAuth.getInstance();



        mBtnAcceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Autenticar();
            }
        });

        mBtnRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),ActivityRegistro.class));
            }
        });
    }


    private void Autenticar(){

        String email = mEditUsu.getText().toString();
        String password = mEditCon.getText().toString();


        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)) {

            mPdLogin.setMessage("Validando datos ...");
            mPdLogin.show();

           // Validamos datos de ingreso
            autenticar.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // Si los datos son correctos accede
                            if (task.isSuccessful()) {
                                mPdLogin.dismiss();

                                startActivity(new Intent(getApplicationContext(),ActivityPrincipal.class));


                            }
                            // Si no mensaje de error
                            else {

                                mPdLogin.dismiss();

                                Toast.makeText(ActivityLogin.this, "Usuario o contraseña incorrecta",
                                        Toast.LENGTH_SHORT).show();

                            }


                        }
                    });


        }



    }
}
