
import java.io.*;
import java.sql.*;
import java.util.*;

// beinhaltet die Aufgabenstellungs-Lösungen
public class Main {

    // Bietet das Menü zur Steuerung zwischen den Aufgaben
    public static void main(String args[]) throws IOException {

        OracleConnection con;
        BufferedReader read = new BufferedReader(new InputStreamReader(System.in));
        int choice;
        int nurPruefen = 0;
        try {
            con = new OracleConnection("dbprak45", "TiMo45");
            do {
                System.out.println("\nBitte wählen Sie einen der folgenden Menüpunkte");
                System.out.println("(1) JDBC Insert");
                System.out.println("(2) Logistik Verwaltung");
                System.out.println("(3) Erfassung Kundenbestellung");
                System.out.println("(0) Beenden\n");
                choice = Integer.parseInt(read.readLine());
                switch (choice) {
                    case 1:
                        nr4(con);
                        break;
                    case 2:
                        nr5(con, nurPruefen);
                        break;
                    case 3:
                        nurPruefen = 1;
                        nr6(con, nurPruefen);
                        break;
                }
            } while (choice != 0);
            con.close();
        } catch (ClassNotFoundException e) {
            if (e.getCause() != null) {
                System.err.println(e.getCause().toString());
            } else {
                e.printStackTrace();
            }
            System.exit(40);
        } catch (SQLException e) {
            if (e.getCause() != null) {
                System.err.println(e.getCause().toString());
            } else {
                e.printStackTrace();
            }
            System.exit(41);
        }

    }

    // Liest .CSV Datei mit Datensätzen ein und schreibt diese in die Tabelle Artikel der Datenbank
    // Zählt die Anzahl gültiger Einträge und gibt sie aus
    public static void nr4(OracleConnection con) throws FileNotFoundException, IOException {

        String tmp, split[];
        int anzahl = 0;
        File file = new File("ARTIKEL.CSV");
        BufferedReader fileR = new BufferedReader(new FileReader(file));

        // Einlesen der Datensätze
        while (null != (tmp = fileR.readLine())) {
            split = tmp.split(";");
            String spalten[] = {"ARTBEZ", "MGE", "PREIS", "STEU", "EDAT"}, tabelle = "Artikel", werte[];

            // Aufbau des Statements
            for (int i = 0; i < split.length; i += 5) {
                werte = new String[5];
                werte[0] = "'" + split[i + 0] + "'";
                werte[1] = "'" + split[i + 1] + "'";
                werte[2] = split[i + 2];
                werte[3] = split[i + 3];
                werte[4] = "to_date('" + split[i + 4] + "','DD.MM.YYYY')";

                // Ausführen
                try {
                    Util.insert(con, tabelle, spalten, werte);
                    anzahl++;
                } catch (SQLException e) {
                    if (e.getCause() != null) {
                        System.err.println(e.getCause().toString());
                    } else {
                        e.printStackTrace();
                    }
                }
            }
        }

        System.out.println("\nAnzahl gültiger einträge: " + anzahl + "\n");

    }

    // Bietet das Menü zur Steuerung der Aufgabe 5
    public static void nr5(OracleConnection con, int nurPruefen) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int choice;

        do {
            System.out.println("\nBitte wählen sie den entsprechenden Menüpunkt. ");
            System.out.println("(1) Anzeigen aller Artikel");
            System.out.println("(2) Anzeigen aller Lager");
            System.out.println("(3) Anzeigen aller Kunden");
            System.out.println("(4) Anzeigen der Stammdaten eines Artikels");
            System.out.println("(5) Erfassen eines neuen Lagerbestandes");
            System.out.println("(6) anpassen der Menge eines Artikels");
            System.out.println("(0) Zurück");

            choice = Integer.parseInt(reader.readLine());

            switch (choice) {
                case 1:
                    try {
                        anzeigenAllerArtikel(con);
                    } catch (SQLException e) {
                        if (e.getCause() != null) {
                            System.err.println(e.getCause().toString());
                        } else {
                            e.printStackTrace();
                        }
                    }

                    break;
                case 2:
                    try {
                        anzeigenAllerLager(con);
                    } catch (SQLException e) {
                        if (e.getCause() != null) {
                            System.err.println(e.getCause().toString());
                        } else {
                            e.printStackTrace();
                        }
                    }

                    break;

                case 3:
                    try {
                        anzeigenAllerKunden(con);
                    } catch (SQLException e) {
                        if (e.getCause() != null) {
                            System.err.println(e.getCause().toString());
                        } else {
                            e.printStackTrace();
                        }
                    }

                    break;
                case 4:
                    System.out.println("Bitte Artikelnummer eingeben: ");

                    try {
                        nurPruefen = 0;
                        stammdaten(con, nurPruefen, reader.readLine());
                    } catch (SQLException e) {
                        if (e.getCause() != null) {
                            System.out.println(e.getCause().toString());
                        } else {
                            e.printStackTrace();
                        }
                    }

                    break;

                case 5: {
                    String bstnr,
                            artnr,
                            lnr,
                            menge;
                    System.out.println("Bitte Bestandsnummer eingeben: ");
                    bstnr = reader.readLine();
                    System.out.println("Bitte Artikelnummer eingeben: ");
                    artnr = reader.readLine();
                    System.out.println("Bitte Lagernummer eingeben: ");
                    lnr = reader.readLine();
                    System.out.println("Bitte Menge eingeben: ");
                    menge = reader.readLine();
                    try {
                        erfassenLagerbestand(con, bstnr, artnr, lnr, menge);
                    } catch (SQLException e) {
                        if (e.getCause() != null) {
                            System.err.println(e.getCause().toString());
                        } else {
                            e.printStackTrace();
                        }
                    }
                    System.out.println("Ihre Eingabe war erfolgreich\n");
                    break;
                }

                case 6: {
                    String bstnr,
                            menge;
                    System.out.println("Bitte Bestandsnummer eingeben: ");
                    bstnr = reader.readLine();
                    System.out.println("Bitte neue Menge eingeben: ");
                    menge = reader.readLine();

                    try {
                        updateMenge(con, bstnr, menge);
                    } catch (SQLException e) {
                        if (e.getCause() != null) {
                            System.err.println(e.getCause().toString());
                        } else {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            }
        } while (choice != 0);
    }
// Methode zum Erfassen einer Kundenbestellung
    public static void nr6(OracleConnection con, int nurPruefen) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String kundennummer,
                artikelnummer,
                bmenge;
                
        
        int i = 0; //wird zur differnzierung bei Methode stammdaten benötigt, hätte man auch überladen machen können. 
        
        //Einlesen und ueberpruefen der Daten auf Richtigkeit 
        System.out.println("----------Automatisches Bestellsystem----------\n\n");
        
        do {
            System.out.println("Bitte geben Sie die Kundenummer ein\n");
            kundennummer = reader.readLine();
            
            if (kundennummer.equals(con.select("KUNDE", "KNR", "KNR = " + kundennummer))) {
                System.out.println("\nDie Eingabe des Kunden war erfolgreich\n");
                i = 1;
            }
        } while (i != 1);
       
        i = 0;
      
        do {
            System.out.println("\nBitte die Artikelnummer eingeben");
            artikelnummer = reader.readLine();
           
            if (artikelnummer.equals(con.select("ARTIKEL", "ARTNR", "ARTNR = " + artikelnummer))) {
                System.out.println("\nDie Eingabe des Artikels war erfolgreich\n");
                i = 1;
            }
        } while (i != 1);

        System.out.println("Bitte geben sie die Bestellmenge ein");
        bmenge = reader.readLine();
        int h1 = Integer.parseInt(bmenge);
        int h2 = Integer.parseInt(stammdaten(con, nurPruefen, artikelnummer));
        if (h1 <= h2) {
            int h3= h2-h1; 
            // ToDO: Update auf Datenbank KUBEST         
            // ToDo: Update auf Tabelle Artikel; Beachten: Lagerort  
            
            
            
            System.out.println("");

        } else {
            System.out.println("Nicht genügend Lagerbestand vorhanden!");

               }
    }

    // Zeig alle Datensätze der Tabelle Artikel
    public static void anzeigenAllerArtikel(OracleConnection con) throws SQLException {

        String spalten[] = {"ARTNR", "ARTBEZ", "MGE", "PREIS", "STEU", "EDAT"};
        ArrayList<String[]> artikel;

        artikel = con.select("Artikel", spalten, null);
        ausgabe(spalten, artikel);
    }

    // Zeig alle Datensätze der Tabelle Lager
    public static void anzeigenAllerLager(OracleConnection con) throws SQLException {
        String spalten[] = {"LNR", "LORT", "LPLZ"};
        ArrayList<String[]> lager;

        lager = con.select("LAGER", spalten, null);
        ausgabe(spalten, lager);
    }

    // Zeig alle Datensätze der Tabelle Kunden
    public static void anzeigenAllerKunden(OracleConnection con) throws SQLException {
        String spalten[] = {"KNR", "KNAME", "PLZ", "ORT", "STRASSE"};
        ArrayList<String[]> kunde;

        kunde = con.select("KUNDE", spalten, null);
        ausgabe(spalten, kunde);
    }

    // Gibt alle Stammdaten eines Artikels und seiner Lagerbestände aus
    public static String stammdaten(OracleConnection con, int nurPruefen, String artikelnummer) throws SQLException {

        String spaltenArtikel[] = {"ARTNR", "ARTBEZ", "MGE", "PREIS", "STEU", "EDAT"},
                spaltenLagerbestand[] = {"BSTNR", "ARTNR", "LNR", "MENGE"},
                spaltenLager[] = {"LNR", "LORT", "LPLZ"},
                spaltenErgebnis[] = {"ARTNR", "ARTBEZ", "MGE", "PREIS", "STEU", "EDAT", "BSTNR", "LNR", "MENGE", "LORT", "LPLZ"},
                artnr, artbez, mge, preis, steu, edat, bstnr, lnr, menge, lort, lplz;
        HashSet<Integer> lagerNr = new HashSet<Integer>();
        ArrayList<String[]> artikel, lagerbestand, lager, listErgebnis = new ArrayList<String[]>();
        int s1 = 0;
        StringBuilder line;

        // Holen aller Artikel mit gegebener Artikelnummer
        artikel = con.select("Artikel", spaltenArtikel, "ARTNR = " + artikelnummer);

        if (artikel.size() > 0) {
            // Holen aller Lagerbestände mit gegebener Artikelnummer
            lagerbestand = con.select("LAGERBESTAND", spaltenLagerbestand, "ARTNR = " + artikelnummer);

            artnr = artikel.get(0)[0];
            artbez = artikel.get(0)[1];
            mge = artikel.get(0)[2];
            preis = artikel.get(0)[3];
            steu = artikel.get(0)[4];
            edat = artikel.get(0)[5];

            if (lagerbestand.size() > 0) {

                // sammeln der Lagernummer aller beteiligten Lager
                for (String arr[] : lagerbestand) {
                    lagerNr.add(Integer.parseInt(arr[2]));
                }

                // Aufbauen des select-Statements für die Tabelle Lager
                line = new StringBuilder();
                Integer tmpLnr[] = lagerNr.toArray(new Integer[lagerNr.size()]);
                line.append("(");
                for (int i = 0; i < lagerNr.size(); i++) {
                    if (i > 0) {
                        line.append(", ");
                    }
                    line.append(tmpLnr[i].toString());
                }
                line.append(")");

                // Holen der Lager
                lager = con.select("LAGER", spaltenLager, "LNR IN " + line.toString());

                for (String arrLB[] : lagerbestand) {
                    bstnr = arrLB[0];
                    lnr = arrLB[2];
                    menge = arrLB[3];

                    s1 += Integer.parseInt(arrLB[3]);

                    for (String arrL[] : lager) {
                        if (lnr.equals(arrL[0])) {
                            lort = arrL[1];
                            lplz = arrL[2];

                            // speichern der Ergebniszeilen
                            String tmpErg[] = {artnr, artbez, mge, preis, steu, edat, bstnr, lnr, menge, lort, lplz};
                            listErgebnis.add(tmpErg);
                        }
                    }
                }
            } else {
                // speichern der Ergebniszeilen falls kein Lagerbestand vorhanden
                String tmpErg[] = {artnr, artbez, mge, preis, steu, edat, null, null, null, null, null};
                listErgebnis.add(tmpErg);
            }

            if (nurPruefen == 0) {
                // Direktes ausgeben des Ergebnises
                ausgabe(spaltenErgebnis, listErgebnis);
                System.out.println("Menge: " + s1 + "\n");
            } else {
                return Integer.toString(s1);
            }

        } else {

            System.out.println("\nKein Artikel gefunden\n");

        }
        return "FEHLER in Methode stammdaten";
    }

    // neuen Lagerbestand erfassen
    public static void erfassenLagerbestand(OracleConnection con, String bstnr, String artnr, String lnr, String menge) throws SQLException {
        String tabelle = "LAGERBESTAND", spalten[] = {"BSTNR", "ARTNR", "LNR", "MENGE"}, werte[] = {bstnr, artnr, lnr, menge};

        Util.insert(con, tabelle, spalten, werte);
    }

    // die Menge eines Lagerbestandes ändern
    public static void updateMenge(OracleConnection con, String bstnr, String menge) throws SQLException {
        String spalten[] = {"MENGE"},
                werte[] = {menge},
                where = "BSTNR = " + bstnr;

        Util.update(con, "LAGERBESTAND", spalten, werte, where);

    }

    // Hilfsfunktion für Ausgabe
    public static void ausgabe(String spalten[], ArrayList<String[]> arrList) {
        StringBuilder line;

        System.out.println();

        line = new StringBuilder();
        //Ausgabe der Spaltennamen
        for (String str : spalten) {
            line.append(str).append("\t\t\t");
            if (str.equals("LORT")) {
                line.append("\t");
            }
            if (str.equals("KNAME")) {
                line.append("\t");
            }
        }
        System.out.println(line.toString());

        //Ausgabe des Tabellen Inhalts
        for (String[] arr : arrList) {
            line = new StringBuilder();
            for (String str : arr) {
                line.append(str).append("\t\t\t");
            }
            System.out.println(line.toString());
        }

        System.out.println();
    }
}
