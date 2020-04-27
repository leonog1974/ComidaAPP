package com.example.escritorio.merkapp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FragmentPerfil extends Fragment {


    private DatabaseReference mReference;
    private FirebaseAuth mAuth;
    private String mUser;

    private TextView mTxtNom, mTxtsaldo;
    private ImageView mImgPer;
    private String mCategoria;
    private RecyclerView mRvInicio;
    private ProgressBar mProgInicio;
    private LinearLayoutManager mLayout;
    private View mPeopleRV;
    private FirebaseRecyclerAdapter<DatosP, PeopleVH> mPeopleRVAdapter;


    public FragmentPerfil() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.fragment_perfil, container, false);


        mReference = FirebaseDatabase.getInstance().getReference().child("usuarios");

        mAuth=FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser().getUid().trim();

        mProgInicio = (ProgressBar)view.findViewById(R.id.progress);
        mRvInicio =(RecyclerView)view.findViewById(R.id.listaperfil);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mRvInicio.setLayoutManager(linearLayoutManager);



        // Llamamos los datos del usuario
        mReference.child(mUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    String url = null, nombre, saldo;
                    mTxtNom = (TextView) view.findViewById(R.id.txtnompro);
                    mTxtsaldo = (TextView)view.findViewById(R.id.txtsaldo);
                    mImgPer = (ImageView)view.findViewById(R.id.imgpp);


                    nombre = dataSnapshot.child("nombre").getValue().toString();
                    saldo = dataSnapshot.child("saldo").getValue().toString();
                    url = dataSnapshot.child("imgp").getValue().toString().trim();

                    // Cargamos los datos
                    if (url != null && nombre != null && saldo != null){
                        mTxtsaldo.setText("$"+" "+saldo);
                        mTxtNom.setText(nombre);
                        Glide.with(getContext()).load(url).bitmapTransform(new CircleTransform(getContext())).into(mImgPer);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        return view;
    }
    @Override
    public void onStart(){
        super.onStart();






        Query personsQuery = mReference.child(mUser).child("pedidos").orderByKey();

        FirebaseRecyclerOptions<DatosP> options = new FirebaseRecyclerOptions.Builder<DatosP>().setQuery(personsQuery, DatosP.class).build();

        mPeopleRVAdapter = new FirebaseRecyclerAdapter<DatosP, PeopleVH>(options) {
            @Override
            protected void onBindViewHolder(PeopleVH holder, int position, DatosP model) {
                if (holder != null && model!=null && position >=0){
                    holder.setPrecio(model.getPrecio());
                    holder.setNombre(model.getNombre());

                }




            }

           /* protected void onBindViewHolder(PeopleVH holder, int position, PersonModel model) {
                holder.setPersonFirstName(model.getFirstName());
                holder.setPersonLastName(model.getLastName());
            }*/

            @Override
            public PeopleVH onCreateViewHolder(ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.cargaperfil, parent, false);

                return new PeopleVH(view);
            }
        };

        mPeopleRVAdapter.startListening();
        mRvInicio.setAdapter(mPeopleRVAdapter);



        /*final FirebaseRecyclerAdapter<DatosP,DatosPViewHolder> firebaseRecyclerAdapter  = new FirebaseRecyclerAdapter<DatosP, DatosPViewHolder>(

                DatosP.class,R.layout.cargaperfil,FragmentPerfil.DatosPViewHolder.class,mReference.child(mUser).child("pedidos")
        ) {
            @Override
            protected void populateViewHolder(DatosPViewHolder viewHolder, DatosP model, int position) {

                if (viewHolder != null && model!=null && position >=0){
                    viewHolder.setNombre(model.getNombre());
                    viewHolder.setPrecio(model.getPrecio());

                }


            }
        };*/

        //mRvInicio.setAdapter(firebaseRecyclerAdapter);





    }

    private static  class testHold extends RecyclerView.ViewHolder{
        public testHold(View itemView) {
            super(itemView);
        }
    }


    private  class DatosPViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public DatosPViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        void setNombre(String nombre){
            if (nombre!=null){
                TextView viewnombre=(TextView)mView.findViewById(R.id.txtnompro);
                viewnombre.setText(nombre);
            }

        }

        void setPrecio(String precio){

            if (precio!=null){
                TextView viewprecio=(TextView)mView.findViewById(R.id.txtpreper);
                viewprecio.setText(precio);

            }


        }
    }

    private class PeopleVH extends RecyclerView.ViewHolder {

        View mView;
        PeopleVH(View view) {
            super(view);

            mView = view;
        }

        void setNombre(String nombre){
            if (nombre!=null){
                TextView viewnombre=(TextView)mView.findViewById(R.id.txtnompro);
                viewnombre.setText(nombre);
            }

        }

        void setPrecio(String precio){

            if (precio!=null){
                TextView viewprecio=(TextView)mView.findViewById(R.id.txtpreper);
                viewprecio.setText(precio);

            }


        }
    }



    @Override
    public void onStop() {
        super.onStop();
        mPeopleRVAdapter.stopListening();
    }
}
