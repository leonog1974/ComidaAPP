package com.example.escritorio.merkapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ActivityRegistro extends AppCompatActivity {
    // Varibales de registo
    private EditText mEditNom, mEditCorr, mEditCont;
    private Button mBtnReg;
    private ImageView mImgPerfil;
    private static final int GALLERY_REGUEST = 1;
    private Uri mUrlPerfil;


    // Variables para enlazar a la base de datos
    private ProgressDialog progress;
    private StorageReference mRecerenceST;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mReferenceDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Toolbar toolbar = (Toolbar)findViewById(R.id.tblreg);
        setSupportActionBar(toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mEditCont = (EditText)findViewById(R.id.txtconr);
        mEditCorr = (EditText)findViewById(R.id.txtcorreor);
        mEditNom = (EditText)findViewById(R.id.txtnombrer);
        mBtnReg = (Button)findViewById(R.id.btnregistro);
        mImgPerfil = (ImageView)findViewById(R.id.imgperfil);

        progress = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();
        mReferenceDB = FirebaseDatabase.getInstance().getReference();
        mRecerenceST = FirebaseStorage.getInstance().getReference();


        // Se Accede a la galeria para elegir una imagen
        mImgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentimg = new Intent(Intent.ACTION_GET_CONTENT);
                intentimg.setType("image/*");
                startActivityForResult(intentimg, GALLERY_REGUEST);
            }
        });

        mBtnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegistar();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REGUEST && resultCode == RESULT_OK) {
            mUrlPerfil = data.getData();
            mImgPerfil.setImageURI(mUrlPerfil);

            // Darle forma redonda a la imagen
            Drawable originalDrawable = mImgPerfil.getDrawable();

            Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

            //creamos el drawable redondeado
            RoundedBitmapDrawable roundedDrawable =
                    RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

            //asignamos el CornerRadius
            roundedDrawable.setCornerRadius(originalBitmap.getHeight());



            mImgPerfil.setImageDrawable(roundedDrawable);

        }
    }


    private void startRegistar() {

        final String nombreR = mEditNom.getText().toString().trim();
        final String contraseR = mEditCont.getText().toString().trim();
        final String correoR = mEditCorr.getText().toString().trim();
        final String emailvalidacion = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


        if (mUrlPerfil!=null){


                if (!TextUtils.isEmpty(nombreR)&& !TextUtils.isEmpty(contraseR) && !TextUtils.isEmpty(correoR)) {

                    if (correoR.matches(emailvalidacion)){

                        if (contraseR.length()>=7){

                                    progress.setMessage("Registrando...");
                                    progress.show();
                                    progress.setCanceledOnTouchOutside(false);



                                    // Tomamos el correo y la contraseña para crear una cuenta
                                    mAuth.createUserWithEmailAndPassword(correoR, contraseR).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {

                                                // Tomamos el usuario creado
                                                String id_usu = mAuth.getCurrentUser().getUid();

                                                // Creamos una referencia a donde guardaremos los datos
                                                final DatabaseReference referencia = mReferenceDB.child("usuarios").child(id_usu);

                                                // Guardamos los datos
                                                referencia.child("id").setValue(id_usu);
                                                referencia.child("correo").setValue(correoR);
                                                referencia.child("contraseña").setValue(contraseR);
                                                referencia.child("nombre").setValue(nombreR);
                                                referencia.child("tipousu").setValue("Comprador");
                                                referencia.child("saldo").setValue("500000");
                                                referencia.child("categoria").setValue("pizza");


                                                progress.dismiss();

                                                progress.setMessage("¡Registro completo! \nCargando foto de perfil...");
                                                progress.show();
                                                progress.setCanceledOnTouchOutside(false);

                                                // Cargamos la imagen de perfil
                                                StorageReference carpetaperfil = mRecerenceST.child("Imagenes_perfil").child(mUrlPerfil.getLastPathSegment());
                                               carpetaperfil.putFile(mUrlPerfil).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        Uri descargarimg = taskSnapshot.getDownloadUrl();

                                                        if (descargarimg != null) {
                                                            referencia.child("imgp").setValue(descargarimg.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isComplete()){
                                                                        progress.dismiss();
                                                                        Toast.makeText(getApplicationContext(), "Registro Completo", Toast.LENGTH_LONG).show();
                                                                        Intent volver = new Intent(getApplicationContext(), ActivityLogin.class);
                                                                        volver.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                        startActivity(volver);

                                                                    }
                                                                }
                                                            });
                                                        }

                                                    }
                                                });



                                            }


                                        }
                                    });






                        }

                        else {

                            Toast.makeText(getApplicationContext(),"Su contraseña debe tener minimo 7 digitos",Toast.LENGTH_LONG).show();
                        }



                    }else
                    {

                        Toast.makeText(getApplicationContext(),"Verifique su correo",Toast.LENGTH_LONG).show();
                    }



                }



        }
        else {

            Toast.makeText(getApplicationContext()," Seleccione una foto de perfil",Toast.LENGTH_LONG).show();
        }


    }
}
