
import java.sql.*;
import java.util.ArrayList;

// stellt die Verbindung zur Oracle Datenbank her und Verwaltet diese
public class OracleConnection {

    private Connection con;

    // baut die Verbindung auf
    public OracleConnection(String user, String pw) throws ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
//        this.con = DriverManager.getConnection("jdbc:oracle:thin:@schelling.nt.fh-koeln.de:1521:xe", user, pw);
        this.con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", user, pw);
    }

    // bearbeitet Insert, Update und Delete befehle
    public void insUpdDel(String sql) throws SQLException {
        try (Statement sm = con.createStatement()) {
            sm.execute(sql);
        }
    }

    // bearbeitet Select-Anfragen
    // gibt das Ergebniss Zeilen für Zeilen als Array-Eintrag in der ArrayList zurück
    // @return ArrayList mit Zeilen als Array
    public ArrayList<String[]> select(String tabelle, String spalten[], String where) throws SQLException {

        ArrayList<String[]> ret = new ArrayList<String[]>();
        StringBuilder line = new StringBuilder();
        ResultSet rs;

        try (Statement sm = con.createStatement()) {

            // Aufbau des Statements
            line.append("SELECT ").append(Util.buildSpalten(spalten)).append(" FROM ").append(tabelle);
            if (where != null) {
                line.append(" WHERE ").append(where);
            }

            // Ausführen
            rs = sm.executeQuery(line.toString());

            // Ergebnis verarbeiten
            while (rs.next()) {
                String tmp[] = new String[spalten.length];
                for (int i = 0; i < tmp.length; i++) {
                    tmp[i] = rs.getString(spalten[i]);
                }
                ret.add(tmp);
            }
        }

        return ret;
    }

    // Schließt die Verbindung zur Datenbank
    public void close() throws SQLException {
        con.close();
    }
}
