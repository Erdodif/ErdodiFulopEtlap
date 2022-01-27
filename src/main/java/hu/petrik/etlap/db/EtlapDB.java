package hu.petrik.etlap.db;

import javafx.fxml.FXML;

import java.sql.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class EtlapDB {
    Connection conn;

    public EtlapDB() throws SQLException {
        conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/etlapdb", "root", "");
    }

    public List<Etel> getEtelek() throws SQLException {
        List<Etel> etelek = new ArrayList<>();
        Statement stmt = conn.createStatement();
        String sql = "SELECT * FROM etlap;";
        ResultSet result = stmt.executeQuery(sql);
        while (result.next()) {
            etelek.add(new Etel(
                    result.getInt("id"),
                    result.getString("nev"),
                    result.getString("leiras"),
                    result.getInt("ar"),
                    Kategoria.fromId(result.getInt("kategoria_id"))
            ));
        }
        return etelek;
    }
    public List<Etel> getEtelek(Kategoria filterKategoria) throws SQLException {
        if (filterKategoria == Kategoria.EMPTY_CATEGORY){
            return getEtelek();
        }
        List<Etel> etelek = new ArrayList<>();
        String sql = "SELECT * FROM etlap WHERE `kategoria_id` = ?;";
        PreparedStatement prpstm = this.conn.prepareCall(sql);
        prpstm.setInt(1,filterKategoria.getId());
        ResultSet result = prpstm.executeQuery();
        while (result.next()) {
            etelek.add(new Etel(
                    result.getInt("id"),
                    result.getString("nev"),
                    result.getString("leiras"),
                    result.getInt("ar"),
                    Kategoria.fromId(result.getInt("kategoria_id"))
            ));
        }
        return etelek;
    }

    public boolean addEtel(Etel etel) throws SQLException {
        String sql = "INSERT INTO `etlap` (nev, leiras, ar, kategoria_id) VALUES(?,?,?,?)";
        PreparedStatement prpstm = this.conn.prepareCall(sql);
        prpstm.setString(1, etel.getNev());
        prpstm.setString(2, etel.getLeiras());
        prpstm.setInt(3, etel.getAr());
        prpstm.setInt(4, etel.getKategoria().getId());
        return prpstm.executeUpdate() == 1;
    }

    public boolean deleteEtel(int id) throws SQLException {
        String sql = "DELETE FROM `etlap` WHERE id = ?";
        PreparedStatement prpstmt = conn.prepareStatement(sql);
        prpstmt.setInt(1, id);
        return prpstmt.executeUpdate() == 1;
    }

    public boolean editEtel(Etel etel) throws SQLException {
        String sql = "UPDATE `etlap` SET nev = ?, leiras = ?, ar = ?, kategoria_id = ? WHERE id = ?;";
        PreparedStatement prpstm = this.conn.prepareCall(sql);
        prpstm.setString(1, etel.getNev());
        prpstm.setString(2, etel.getLeiras());
        prpstm.setInt(3, etel.getAr());
        prpstm.setInt(4, etel.getKategoria().getId());
        prpstm.setInt(5, etel.getId());
        return prpstm.executeUpdate() == 1;
    }

    public boolean raisePrice(int modifier, Integer etelID) throws SQLException {
        String whereIfSingle = etelID != null ? "WHERE id = ?" : "";
        if (modifier < 5) {
            throw new IllegalArgumentException("Áremelés nem lehet 5% alatt!");
        }
        if (modifier > 3000) {
            throw new IllegalArgumentException("Áremelés nem lehet 3000Ft felett!");
        }
        String sql = "UPDATE `etlap` SET ar = ar * (1 + (? / 100)) " + whereIfSingle + ";";
        if (modifier > 50) {
            sql = "UPDATE `etlap` SET ar = ar + ? " + whereIfSingle + ";";
        }
        PreparedStatement prpstm = this.conn.prepareCall(sql);
        prpstm.setInt(1, modifier);
        if(etelID != null){
            prpstm.setInt(2, etelID);
        }
        return prpstm.executeUpdate() == 1;
    }


}
