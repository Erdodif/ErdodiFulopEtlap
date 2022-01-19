package hu.petrik.etlap.db;

import java.util.Objects;

public class Etel {

    private int id;
    private String nev;
    private String leiras;
    private int ar;
    private Kategoria kategoria;

    public Etel(int id, String nev, String leiras, int ar,Kategoria kategoria){
        this.id = id;
        this.nev = nev;
        this.leiras = leiras;
        this.ar = ar;
        this.kategoria = kategoria;
    }

    public Etel(int id, String nev, String leiras, int ar,String kategoriaNev){
        this.id = id;
        this.nev = nev;
        this.leiras = leiras;
        this.ar = ar;
        this.kategoria = Kategoria.fromNev(kategoriaNev);
    }

    public Etel(int id, String nev, String leiras, int ar,int kategoriaId){
        this.id = id;
        this.nev = nev;
        this.leiras = leiras;
        this.ar = ar;
        this.kategoria = Kategoria.fromId(kategoriaId);
    }

    public int getId() {
        return id;
    }

    public String getNev() {
        return nev;
    }

    public String getLeiras() {
        return leiras;
    }

    public int getAr() {
        return ar;
    }

    public Kategoria getKategoria() {
        return kategoria;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public void setLeiras(String leiras) {
        this.leiras = leiras;
    }

    public void setAr(int ar) {
        this.ar = ar;
    }

    public void setKategoria(Kategoria kategoria) {
        this.kategoria = kategoria;
    }
}
