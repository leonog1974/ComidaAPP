package com.example.escritorio.merkapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

public class ActivityPrincipal extends AppCompatActivity {



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {



        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Boolean FragmentTransaction= false;
            Fragment fragment=null;
            switch (item.getItemId()) {



                case R.id.navigation_home:

                    fragment = new FragmentInicio();
                    FragmentTransaction = true;
                    break;

                case R.id.navigation_notifications:

                    fragment = new FragmentPerfil();
                    FragmentTransaction = true;
                    break;
            }

            if (FragmentTransaction){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content,fragment)
                        .commit();
                item.setCheckable(true);

            }
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

       /* //Paso 1: Obtener la instancia del administrador de fragmentos
        FragmentManager fragmentManager = getFragmentManager();

        //Paso 2: Crear una nueva transacción
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //Paso 3: Crear un nuevo fragmento y añadirlo

        FragmentInicio fragment2 = new FragmentInicio();
        transaction.add(R.id.content,fragment2);

        //Paso 4: Confirmar el cambio
        transaction.commit();*/



    }

    public void btnsalir(View view){


        Intent intent = new Intent(getApplicationContext(),ActivityLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }

}
