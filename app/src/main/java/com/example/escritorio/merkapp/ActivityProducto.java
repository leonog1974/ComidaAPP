package com.example.escritorio.merkapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ActivityProducto extends AppCompatActivity {

        private DatabaseReference mReference;
        private FirebaseAuth mAuth;
        private String mUser, mNombre, mPrecio, mUrl, mCategoria, mDes;
        private StorageReference referenciaIMG;

        private EditText mEditnombre, mEditPrecio, mEditDes;
        private ImageView mImgPro, mImgPizza, mImgCH, mImgHam;
        private static final int GALLERY_REGUEST = 1;
        private Uri mUrlPerfil;
        private ProgressDialog mProgres;
        private Button mBtnCargar;
        private TextView mTxtSeleccion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_producto);


        // Referencia a la base de datos y al Storage de las imagenes
        referenciaIMG = FirebaseStorage.getInstance().getReference();
        mReference = FirebaseDatabase.getInstance().getReference().child("productos");
        mAuth=FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser().getUid().trim();


        mTxtSeleccion = (TextView)findViewById(R.id.txtseleccione);
        mEditnombre = (EditText)findViewById(R.id.txtnompp);
        mEditDes = (EditText)findViewById(R.id.txtdespp);
        mEditPrecio = (EditText)findViewById(R.id.txtpreciopp);
        mImgPro = (ImageView)findViewById(R.id.imgprop);
        mImgCH = (ImageView)findViewById(R.id.imgch);
        mImgHam = (ImageView)findViewById(R.id.imgham);
        mImgPizza = (ImageView)findViewById(R.id.imgpizza);
        mBtnCargar = (Button)findViewById(R.id.btncargarp);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tbladd);

        mProgres = new ProgressDialog(this);

        // Se Accede a la galeria para elegir una imagen
        mImgPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentimg = new Intent(Intent.ACTION_GET_CONTENT);
                intentimg.setType("image/*");
                startActivityForResult(intentimg, GALLERY_REGUEST);
            }
        });
        // Seleccion de categoria
        mImgPizza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTxtSeleccion.setText("Carge una foto de referencia");
                mCategoria = "pizza";
                mImgPro.setVisibility(View.VISIBLE);
                mImgCH.setVisibility(View.GONE);
                mImgHam.setVisibility(View.GONE);
                mImgPizza.setVisibility(View.GONE);
            }
        });

        // Seleccion de categoria
        mImgHam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTxtSeleccion.setText("Carge una foto de referencia");
                mCategoria = "hamburgesa";
                mImgPro.setVisibility(View.VISIBLE);
                mImgCH.setVisibility(View.GONE);
                mImgPizza.setVisibility(View.GONE);
                mImgHam.setVisibility(View.GONE);
            }
        });
        // Seleccion de categoria
        mImgCH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTxtSeleccion.setText("Carge una foto de referencia");
                mCategoria = "comidac";
                mImgPro.setVisibility(View.VISIBLE);
                mImgPizza.setVisibility(View.GONE);
                mImgHam.setVisibility(View.GONE);
                mImgCH.setVisibility(View.GONE);
            }
        });

        mBtnCargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicar();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REGUEST && resultCode == RESULT_OK) {
            mUrlPerfil = data.getData();
            mImgPro.setImageURI(mUrlPerfil);

        }
    }


    private void publicar(){
        mNombre = mEditnombre.getText().toString();
        mPrecio = mEditPrecio.getText().toString();
        mDes = mEditDes.getText().toString();


        if (mNombre != null && mPrecio!=null && mCategoria != null){

            mProgres.setMessage("Cargando...");
            mProgres.show();
            mProgres.setCanceledOnTouchOutside(false);
            // Carga de datos
            final DatabaseReference referencia = mReference.child(mCategoria).push();
            // El parametro "PUSH" se utiliza para generar una clave aleatoria
            referencia.child("nombrep").setValue(mNombre);
            referencia.child("preciop").setValue(mPrecio);
            referencia.child("descripcion").setValue(mDes);
            referencia.child("carritop").setValue("no");

            // Referencia al Storage donde se guardan las imagenes
            StorageReference carpetaperfil = referenciaIMG.child("Imagenes_perfil").child(mUrlPerfil.getLastPathSegment());
            carpetaperfil.putFile(mUrlPerfil).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    // Extraccion de la url donde esta la imagen
                    Uri descargarimg = taskSnapshot.getDownloadUrl();

                    if (descargarimg != null) {

                        referencia.child("imgp").setValue(descargarimg.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()){

                                    mProgres.dismiss();

                                    Toast.makeText(getApplicationContext(), "Carga Completa!", Toast.LENGTH_LONG).show();
                                    Intent volver = new Intent(getApplicationContext(), ActivityPrincipal.class);
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
}
