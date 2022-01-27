package hu.petrik.etlap.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Kategoria {
    static private List<Kategoria> kategoriak;
    private int id;
    private String nev;
    static public final Kategoria EMPTY_CATEGORY = new Kategoria(0, "Nincs szűrés");

    public static void initialize(EtlapDB etlapDB) throws SQLException {
        Connection conn = etlapDB.conn;
        kategoriak = new ArrayList<>();
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM kategoria;";
        ResultSet result = stmt.executeQuery(sql);
        while (result.next()) {
            kategoriak.add(new Kategoria(
                    result.getInt("id"),
                    result.getString("nev")
            ));
        }
    }

    Kategoria(int id, String nev) {
        this.id = id;
        this.nev = nev;
    }

    public int getId() {
        return id;
    }

    public String getNev() {
        return nev;
    }

    @Override
    public String toString() {
        return this.getNev();
    }

    static public Kategoria fromId(int id) {
        return kategoriak.stream().filter(kategoria -> kategoria.id == id).findFirst().get();
    }

    static public Kategoria fromNev(String nev) {
        return kategoriak.stream().filter(kategoria -> kategoria.nev.equals(nev)).findFirst().get();
    }

    static public List<Kategoria> getKategoriak() {
        return Kategoria.kategoriak;
    }
}
