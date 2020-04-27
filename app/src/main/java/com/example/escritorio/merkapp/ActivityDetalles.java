package com.example.escritorio.merkapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityDetalles extends AppCompatActivity {

    
    private TextView mTxtNom, mTxtpre, mTxtDesc;
    private Button mBtnpedido;
    private ImageView mImgPro;
    private AlertDialog.Builder builder;
    private ProgressDialog dialog;

    // Variables de base de datos
    private DatabaseReference mReference, mReferencep;
    private FirebaseAuth mAuth;
    private String mUser, saldofinal;
    private FloatingActionButton mBtnFlo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);

        // Obtencion del id del producto
        final String sID = getIntent().getStringExtra("ID");

        builder = new AlertDialog.Builder(ActivityDetalles.this);
        mTxtDesc = (TextView)findViewById(R.id.txtdetallesd);
        mBtnpedido = (Button) findViewById(R.id.btnpedir);
        mTxtNom = (TextView) findViewById(R.id.txtnomd);
        mTxtpre = (TextView) findViewById(R.id.txtpred);
        mImgPro = (ImageView) findViewById(R.id.imgdetalles);
        mBtnFlo = (FloatingActionButton)findViewById(R.id.btnflo);

        dialog = new ProgressDialog(this);

        // Referencia a la base de datos
        mReferencep = FirebaseDatabase.getInstance().getReference().child("productos");
        mReference = FirebaseDatabase.getInstance().getReference().child("usuarios");
        mAuth=FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser().getUid().trim();

        // Aceeso a la base de datos

        mReference.child(mUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot!= null){

                    final String saldo = dataSnapshot.child("saldo").getValue().toString();


                    final String categoria=dataSnapshot.child("categoria").getValue().toString();

                    mReferencep.child(categoria).child(sID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            if (dataSnapshot!=null){

                                String nombre = null;
                                String precio = null;
                                String descripcion = null;
                                String url = null;

                                // Solicitud de datos
                                if (dataSnapshot.child("nombrep").exists()){
                                    nombre = dataSnapshot.child("nombrep").getValue().toString().trim();
                                    precio = dataSnapshot.child("preciop").getValue().toString().trim();
                                    descripcion = dataSnapshot.child("descripcion").getValue().toString();
                                    url = dataSnapshot.child("imgp").getValue().toString();

                                }

                                // inserccion de datos
                                if (nombre!=null && precio!=null && descripcion!=null && url!=null){
                                    mTxtDesc.setText(descripcion);
                                    mTxtNom.setText(nombre);
                                    mTxtpre.setText("$" + " "+ precio);
                                    Glide.with(getApplicationContext()).load(url).into(mImgPro);



                                    // Cargando pedido
                                    final String finalNombre = nombre;
                                    final String finalPrecio = precio;
                                    final String finalPrecio1 = precio;



                                    int resta = Integer.parseInt(saldo) - Integer.parseInt(finalPrecio1);

                                    saldofinal = String.valueOf(resta);
                                    final String finalPrecio2 = precio;
                                    mBtnpedido.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            if (Integer.parseInt(saldo) < Integer.parseInt(finalPrecio2)){

                                                Toast.makeText(getApplicationContext(),"Saldo insufuciente",Toast.LENGTH_SHORT).show();

                                            }else {
                                                dialog.setMessage("Solicitando pedido");
                                                dialog.show();
                                                dialog.setCanceledOnTouchOutside(false);



                                                final DatabaseReference referencia = mReference.child(mUser).child("pedidos").push();



                                                referencia.child("nombre").setValue(finalNombre);
                                                referencia.child("precio").setValue(finalPrecio);
                                                referencia.child("id").setValue(sID);

                                                referencia.child("categoria").setValue(categoria).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful() && task.isComplete()){
                                                            mReference.child(mUser).child("saldo").setValue(saldofinal);
                                                            dialog.dismiss();

                                                            startActivity(new Intent(getApplicationContext(),ActivityPrincipal.class));

                                                            Toast.makeText(getApplicationContext(),"Pedido Realizado\nRevise su perfil",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }






                                        }
                                    });

                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });











    }
}
