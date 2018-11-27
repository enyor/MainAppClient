package co.kaizenpro.mainapp.mainapp;


import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

public class TraderDatos extends Application {

    public String img;
    public String imgcover;
    public String nombre;
    public Integer edad;
    public String TraderId;
    public String UserId;
    public String UserName;
    public String especialidad;
    public String enable;
    public String limite;
    public String dir;
    public int duracion;
    public LatLng frompos;
    public LatLng topos;
    public Integer rating;

    public TraderDatos (){
    }

    public TraderDatos(String img, String imgcover, String nombre, Integer edad, String TraderId, String UserId, String UserName, String especialidad, String enable, String limite, String dir, int duracion, Integer rating){

        this.img    =   img;
        this.imgcover = imgcover;
        this.nombre =   nombre;
        this.TraderId   =   TraderId;
        this.UserId =   UserId;
        this.UserName   =   UserName;
        this.especialidad   =   especialidad;
        this.enable =   enable;
        this.edad = edad;
        this.limite = limite;
        this.dir    = dir;
        this.duracion   = duracion;
        this.rating     = rating;

    }
    public String getImg() {
        return img;
    }
    public void setImg(String img) {
        this.img = img;

    }
    public String getImgcover() {
        return imgcover;
    }
    public void setImgcover(String imgcover) {
        this.imgcover = imgcover;

    }
    public String getNombre() {
        return nombre;
    }
    public String getTraderId() {
        return TraderId;
    }
    public String getUserId() {
        return UserId;
    }
    public String getUserName() {
        return UserName;
    }
    public String getEspecialidad() {
        return especialidad;
    }
    public String getEnable() {
        return enable;
    }
    public String getLimite() {
        return limite;
    }
    public String getDir() {
        return dir;
    }
    public int getDuracion() {
        return duracion;
    }
    public LatLng getFrompos() {
        return frompos;
    }
    public LatLng getTopos() {
        return topos;
    }
    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating){
        this.rating = rating;
    }
    public Integer getEdad() {
        return edad;
    }
    public void setEdad(Integer edad){
        this.edad = edad;
    }
}
