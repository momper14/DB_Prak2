// banane
import java.sql.SQLException;

// bietet Hilfsfunktionen
public class Util {

    // wandelt den Array in String mit ", " zwichen Elementen um
    public static String buildSpalten(String spalten[]) {

        StringBuilder line = new StringBuilder();

        for (int i = 0; i < spalten.length; i++) {
            if (i > 0) {
                line.append(", ");
            }
            line.append(spalten[i]);
        }

        return line.toString();
    }

    // erstellt und verarbeitet Inserts
    // @return Gültigeit der Kombination aus Spalten und Werte
    public static boolean insert(OracleConnection con, String tabelle, String spalten[], String werte[]) throws SQLException {

        boolean ret;
        StringBuilder line = new StringBuilder();

        // Aufbau des Statements
        if (spalten.length == werte.length) {
            line.append("INSERT INTO ").append(tabelle).append(" (");
            line.append(Util.buildSpalten(spalten));
            line.append(") VALUES(");
            line.append(buildSpalten(werte));
            line.append(")");

            // Ausführen
            con.insUpdDel(line.toString());

            ret = true;
        } else {
            ret = false;
        }
        return ret;
    }

    // erstellt und verarbeitet Updates
    // @return Gültigeit der Kombination aus Spalten und Werte
    public static boolean update(OracleConnection con, String tabelle, String spalten[], String werte[], String where) throws SQLException {

        boolean ret;
        StringBuilder line = new StringBuilder();

        if (spalten.length == werte.length) {
            // Aufbau des Statements
            line.append("UPDATE ").append(tabelle).append(" SET ");
            for (int i = 0; i < spalten.length; i++) {
                if (i > 0) {
                    line.append(", ");
                }
                line.append(spalten[i]).append(" = ").append(werte[i]);
            }
            line.append(" WHERE ").append(where);

            // Ausführen
            con.insUpdDel(line.toString());

            ret = true;
        } else {
            ret = false;
        }

        return ret;
    }

}
