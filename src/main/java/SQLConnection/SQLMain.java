package SQLConnection;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import me.tongfei.progressbar.ProgressBar;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLMain {
    //Erstellt die Daten bank und kann einmalig alle Tabellen als csv Daten einlese,
    //Dafür müssen alle csv Datein vorhanden sein (pro Tabelle eine csv Datei)
    private Connection CONNECTION = null;
    private int TRY = 0;
    private boolean build_db_schema = true;

    public void sqlStart() {


        try {
            Class.forName("org.sqlite.JDBC");
            CONNECTION = DriverManager.getConnection(String.format("jdbc:sqlite:finanzplannerDB.db", TRY));

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Opened database successfully");

        if (!build_db_schema || build_schema()) {
            System.out.println("Successfully built the schema!");
        }

    }

    public boolean build_schema() {
        Statement statement = null;
        try {
            statement = CONNECTION.createStatement();

            String create_Table_Konto = "CREATE TABLE IF NOT EXISTS Konto(" +
                    "KartenNr BIGINT NOT NULL," +
                    "Name VARCHAR(30)," +
                    "Beschreibung VARCHAR(100), " +
                    "Kontostand INTEGER," +
                    "PRIMARY KEY(KartenNr));";

            String create_Table_Kategorie = "CREATE TABLE IF NOT EXISTS Kategorie(" +
                    "Name VARCHAR(30) NOT NULL," +
                    "Beschreibung VARCHAR(200)," +
                    "PRIMARY KEY (Name));";

            String create_Table_Buchungen = "CREATE TABLE IF NOT EXISTS Buchung( " +
                    "ID INTEGER , " +
                    "Datum TIMESTAMP, " +
                    "Kontoname VARCHAR(30) REFERENCES Konto(Name) ON DELETE SET NULL, " +
                    "Kategorie VARCHAR(30) REFERENCES Kategorie(Name) ON DELETE SET NULL, " +
                    "Kommentar VARCHAR(150), " +
                    "Betrag INTEGER, " +
                    "IstDauerauftrag BOOLEAN," +
                    "DauerauftragName VARCHAR(30) REFERENCES Dauerauftrag(NAME) ON DELETE SET NULL, " +
                    "PRIMARY KEY(ID));";

            String create_Table_Dauerauftrag = "CREATE TABLE IF NOT EXISTS Dauerauftrag(" +
                    "Name VARCHAR(30), " +
                    "Kontoname VARCHAR(30)REFERENCES Konto(Name) ON DELETE CASCADE ," +
                    "Kategorie VARCHAR(30) REFERENCES Kategorie(Name) ON DELETE SET NULL," +
                    "Kommentar VARCHAR(150)," +
                    "Betrag DOUBLE ," +
                    "isMonat BOOLEAN," +
                    "PRIMARY KEY (NAME));";

            //System.out.println(create_Table_Konto);
            //System.out.println(create_Table_Kategorie);
            //System.out.println(create_Table_Buchungen);
            //System.out.println(create_Table_Dauerauftrag);
            statement.executeUpdate(create_Table_Konto);
            statement.executeUpdate(create_Table_Kategorie);
            statement.executeUpdate(create_Table_Buchungen);
            statement.executeUpdate(create_Table_Dauerauftrag);
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public void fill_database_Example(String[] fileNamen, boolean isExample) {
        int counterEintrag = 0;
        int counterFail = 0;
        Statement statement = null;
        CSVReader csvReader = null;
        List<String[]> ret;
        ProgressBar pb = new ProgressBar("Read Input", 100);
        for (int j = 0; j < fileNamen.length; j++) {
            try {
                statement = CONNECTION.createStatement();
                csvReader = new CSVReader(new FileReader(fileNamen[j]), ',', '"');
                ret = csvReader.readAll();
                if (isExample) {
                    ret.remove(0);
                }
            } catch (Exception e) {
                System.err.println("Datei nicht Gefunden: ");
                System.out.println(fileNamen[j]);
                e.printStackTrace();
                pb.close();
                return;
            }
            if (pb.getMax() == 100) {
                pb.maxHint(ret.size() * fileNamen.length);
            }
            for (int i = 0; i < ret.size(); i++) {

                String sqlinsert = "Kein sqlinsert gepeichert.";
                if (j == 0) {
                    sqlinsert = "INSERT INTO Konto(KartenNr, Name, Beschreibung, Kontostand) VALUES (" +
                            ret.get(i)[0] + ", " +
                            "'" + ret.get(i)[1] + "'," +
                            "'" + ret.get(i)[2] + "'," +
                            ret.get(i)[3] + ");";
                }
                if (j == 1) {
                    sqlinsert = "INSERT INTO Kategorie(Name, Beschreibung) VALUES (" +
                            "'" + ret.get(i)[0] + "'," +
                            "'" + ret.get(i)[1] + "');";
                }
                if (j == 2) {
                    sqlinsert = "INSERT INTO Dauerauftrag(Name, Kontoname, Kategorie, Kommentar, Betrag, isMonat) VALUES (" +
                            "'" + ret.get(i)[0] + "'," +
                            "'" + ret.get(i)[1] + "'," +
                            "'" + ret.get(i)[2] + "'," +
                            "'" + ret.get(i)[3] + "'," +
                            ret.get(i)[4] + "," +
                            ret.get(i)[5] + ");";
                    //System.out.println(sqlinsert);
                }
                if (j == 3) {
                    sqlinsert = "INSERT INTO Buchung(ID, Datum, Kontoname, Kategorie, Kommentar, Betrag, IstDauerauftrag, DauerauftragName) VALUES (" +
                            ret.get(i)[0] + "," +
                            "'" + ret.get(i)[1] + "'," +
                            "'" + ret.get(i)[2] + "'," +
                            "'" + ret.get(i)[3] + "'," +
                            "'" + ret.get(i)[4] + "'," +
                            ret.get(i)[5] + "," +
                            ret.get(i)[6] + "," +
                            "'" + ret.get(i)[3] + "'" + ");";
                }
                try {
                    statement.executeUpdate(sqlinsert);
                    counterEintrag++;
                    pb.step();
                } catch (Exception e) {
                    pb.maxHint(pb.getMax() - 1);
                    System.err.println("Eintrag könnte nicht eingelesen werden.");
                    System.out.println("Eintrag:   ");
                    System.out.println(sqlinsert);
                    counterFail++;
                }

            }            //
        }
        pb.close();
        System.out.println("-----------------------------------------");
        System.out.println("Erfolgreich eingelesen: " + counterEintrag);
        System.out.println("Fehlgeschlagen: " + counterFail);
        System.out.println("-----------------------------------------");
        return;
    }

    public void createCSVFiles(String[] tableName, String[] futureFileName) {
        for (int i = 0; i < tableName.length; i++) {
            List<String> result = new ArrayList<>();
            try {
                Statement statement = CONNECTION.createStatement();
                String sqlSelect = "SELECT * FROM " + tableName[i] + ";";
                ResultSet rs = statement.executeQuery(sqlSelect);
                int numCols = rs.getMetaData().getColumnCount();
                while (rs.next()) {
                    StringBuilder sb = new StringBuilder();
                    for (int j = 1; j <= numCols; j++) {
                        if (j != numCols) {
                            sb.append(String.format(String.valueOf(rs.getString(j) + ",")));
                        } else {
                            sb.append(String.format(String.valueOf(rs.getString(j))));
                        }

                    }
                    System.out.println(sb.toString());
                    result.add(sb.toString());
                }
                printToCsv(result, futureFileName[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    private void printToCsv(List<String> resultArray, String fileName) throws IOException {
        File csvOutput = new File(fileName);
        FileWriter fileWriter = new FileWriter(csvOutput, false);
        //CSVWriter csvWriter = new CSVWriter(csvOutput);
        for (String mapping : resultArray) {
            fileWriter.write(mapping + "\n");
        }
        fileWriter.close();
    }

}