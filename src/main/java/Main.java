import SQLConnection.*;

public class Main {
    private static String[] fileNamenExample = new String[]{"KontoDataExample.csv", "KategorieDataExample.csv", "DauerauftragDataExample.csv", "BuchungDataExample.csv"};
    private static String[] fileNamen = new String[]{"KontoData.csv", "KategorieData.csv", "DauerauftragData.csv", "BuchungData.csv"};
    private static String[] tableNamen = new String[]{"Konto", "Kategorie", "Dauerauftrag", "Buchung"};

    public static void main(String[] args) {
        System.out.println("Wilkommen im Finanzlpanner");
        SQLMain sqlMain = new SQLMain();
        sqlMain.sqlStart();
        if(args.length>0) {
            if (args[0].equals("-h")) {
                printhelp();
            } else if (args[0].equals("-r")) {
                sqlMain.fill_database_Example(fileNamen, false);
            } else if (args[0].equals("-e")) {
                sqlMain.fill_database_Example(fileNamenExample, true);
            } else if (args[0].equals("-c")) {
                sqlMain.createCSVFiles(tableNamen,fileNamen);
            }
        }
    }


    private static void printhelp() {
        System.out.println("------------------------------");
        System.out.println(" Wilkommen in der Hilfe(Help)");
        System.out.println("------------------------------");
        System.out.println("-h   = Hilfe");
        System.out.println("-r   =Lese alle csv files ein (read)");
        System.out.println("-e   =Lese alle Example csv files ein");
        System.out.println("-c   =create csv aus der aktuellen Datenbank.");
        System.out.println("Ohne Parameter = leere Datenbank wird erstellt");

    }
}