package com.example.escritorio.merkapp;

public class Datos {

    //Variables con el mismo nombre de la base de datos
    private String imgp;
    private String nombrep;
    private String preciop;
    private String carritop;

    public Datos(){

    }

    public String getImgp() {
        return imgp;
    }

    public void setImgp(String imgp) {
        this.imgp = imgp;
    }

    public String getNombrep() {
        return nombrep;
    }

    public void setNombrep(String nombrep) {
        this.nombrep = nombrep;
    }

    public String getPreciop() {
        return preciop;
    }

    public void setPreciop(String preciop) {
        this.preciop = preciop;
    }

    public String getCarritop() {
        return carritop;
    }

    public void setCarritop(String carritop) {
        this.carritop = carritop;
    }

    public Datos (String imgp, String nombrep, String preciop, String carritop){

        this.imgp =imgp;

        this.nombrep = nombrep;
        this.preciop = preciop;
        this.carritop = carritop;

    }




}
