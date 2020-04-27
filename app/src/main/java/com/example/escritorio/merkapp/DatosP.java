package com.example.escritorio.merkapp;

public class DatosP {

    //Variables con el mismo nombre de la base de datos

    private String nombre;
    private String precio;


    public DatosP(){

    }




    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }



    public DatosP(String imgp, String nombre, String precio, String carritop){



        this.nombre = nombre;
        this.precio = precio;


    }




}
