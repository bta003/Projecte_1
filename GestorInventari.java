package gestorInventariApp;

import java.util.Scanner;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class GestorInventari {

    static final String PATHPENDENTS = "Files/ENTRADES_PENDENTS/";

    static final String PATHPROCESSADES = "Files/ENTRADES_PROCESSADES/";

    static Connection connexioBD = null;
    PreparedStatement ps = null;

    static String nomempresa1 = "nomempresa";
    static String codiempresa = "98436387F";
    static String direccioempresa = "Av. de Catalunya 13, Tàrrega, Lleida";

    static String[] arrayproveidors = new String[100];

    static int[] arrayproductes = new int[100];

    public static void main(String[] args) {

        try {
            connexioBD();
            menuAPP();
            tancarConnexioBD();
        } catch (SQLException ex) {
            System.out.println("Hi ha hagut un problema amb la BD");
            ex.printStackTrace();
        } catch (Exception e) {
            System.out.println("Hi ha hagut un problema amb l'aplicació");
            e.printStackTrace();
        }

    }

    // MENU PRINCIPAL DE L'APLICACIÓ
    static void menuAPP() throws SQLException, IOException {
        Scanner teclat = new Scanner(System.in);
        boolean sortir = false;
        do {
            System.out.println("\n*MENU GESTOR INVENTARI*");
            System.out.println("1. Gestió productes");
            System.out.println("2. Actualització stock");
            System.out.println("3. Generació comandes");
            System.out.println("4. Analitzar comandes");
            System.out.println("5. Sortir");
            System.out.println("\nTRIA UNA OPCIÓ:");

            int opcio = teclat.nextInt();

            switch (opcio) {
                case 1:
                    // MENÚ GESTIÓ PRODUCTES
                    gestioProductes();
                    break;
                case 2:
                    actualitzarStock();
                    break;
                case 3:
                    generacioComandes_2();
                    break;
                case 4:
                    analitzarComandes();
                    break;
                case 5:
                    sortir = true;
                    break;
                default:
                    System.out.println("\n" + opcio + " NO ÉS UNA OPCIÓ NO VÀLIDA");
            }
        } while (!sortir);
        // tancarConnexioBD
    }

    // MENÚ GESTIÓ PRODUCTES
    static void gestioProductes() throws SQLException {
        Scanner teclat = new Scanner(System.in);
        boolean sortir = false;

        do {
            System.out.println("\n*GESTIÓ PRODUCTES*");

            System.out.println("1. Consulta de tots els productes");
            System.out.println("2. Consulta d'un producte");
            System.out.println("3. Alta d'un producte");
            System.out.println("4. Modificació d'un producte");
            System.out.println("5. Baixa d'un producte");
            System.out.println("6. Sortir a MENU GESTOR INVENTARI");
            System.out.println("\nTRIA UNA OPCIÓ:");

            int opcio = teclat.nextInt();
            teclat.nextLine();

            switch (opcio) {
                case 1:
                    llistarTotsProductes();
                    break;
                case 2:
                    consultaProducte();
                    break;
                case 3:
                    altaProducte();
                    break;
                case 4:
                    modificaProducte();
                    break;
                case 5:
                    eliminaProducte();
                    break;
                case 6:
                    sortir = true;
                    break;
                default:
                    System.out.println("\n" + opcio + " NO ÉS UNA OPCIÓ NO VÀLIDA");
            }
        } while (!sortir);
        // desconnexioBD();

    }

    static void connexioBD() throws SQLException {
        String servidor = "jdbc:mysql://localhost:3306/";
        String bbdd = "empresa";
        String user = "root";
        String password = " ";
        try {
            connexioBD = DriverManager.getConnection(servidor + bbdd, user, password);
            System.out.println("*Connexio amb la base de dades amb èxit*");
        } catch (SQLException ex) {
            System.out.println("*No s'ha pogut conectar amb la base de dades*");
            ex.printStackTrace();
        }

    }

    static void llistarTotsProductes() throws SQLException {
        String consulta = "SELECT * FROM producto ORDER BY nom;";
        PreparedStatement ps = connexioBD.prepareStatement(consulta);
        ResultSet rs = ps.executeQuery();

        System.out.println("Llista de tots els productes: ");

        while (rs.next()) {
            System.out.print("codi: " + rs.getInt("codi") + " ");
            System.out.print("nom: " + rs.getString("nom") + " ");
            System.out.print("marca: " + rs.getString("marca") + " ");
            System.out.print("preu: " + rs.getInt("preu") + " ");
            System.out.print("stock: " + rs.getInt("estoc") + " ");
            System.out.print("categoria: " + rs.getString("categoria") + " ");
            System.out.print("codi proveïdor: " + rs.getString("nif") + " \n");
        }

    }

    static void consultaProducte() throws SQLException {
        Scanner teclat = new Scanner(System.in);
        System.out.println("*Consulta un producte*");
        System.out.println("Codi del producte a consultar: ");
        String codi = teclat.nextLine();

        String consulta = "SELECT * FROM producto WHERE codi = ?";
        PreparedStatement sentencia = connexioBD.prepareStatement(consulta);

        sentencia.setString(1, codi);

        ResultSet rs = sentencia.executeQuery();

        while (rs.next()) {
            System.out.print("codi: " + rs.getInt("codi") + ", ");
            System.out.print("nom: " + rs.getString("nom") + ", ");
            System.out.print("marca: " + rs.getString("marca") + ", ");
            System.out.print("preu: " + rs.getInt("preu") + ", ");
            System.out.print("stock: " + rs.getInt("estoc") + ", ");
            System.out.print("categoria: " + rs.getString("categoria") + ", ");
            System.out.print("codi proveïdor: " + rs.getString("nif") + " \n");
        }
    }

    static void altaProducte() throws SQLException {
        Scanner teclat = new Scanner(System.in);
        System.out.println("*Donar d'alta un producte*");

        System.out.println("Codi: ");
        int codi = teclat.nextInt();
        teclat.nextLine();

        System.out.println("Nom: ");
        String nom = teclat.nextLine();

        System.out.println("Marca: ");
        String marca = teclat.nextLine();

        System.out.println("Preu: ");
        String preu = teclat.nextLine();

        System.out.println("Stock: ");
        int estoc = teclat.nextInt();
        teclat.nextLine();

        System.out.println("Categoria: ");
        String categoria = teclat.nextLine();

        System.out.println("Codi proveïdor: ");
        String nif = teclat.nextLine();

        String consulta = "INSERT INTO producto (codi, nom, marca, preu, estoc, categoria, nif) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement sentencia = connexioBD.prepareStatement(consulta);

        sentencia.setInt(1, codi);
        sentencia.setString(2, nom);
        sentencia.setString(3, marca);
        sentencia.setString(4, preu);
        sentencia.setInt(5, estoc);
        sentencia.setString(6, categoria);
        sentencia.setString(7, nif);

        if (sentencia.executeUpdate() != 0) {
            System.out.println("Producte donat d'alta: " + nom);
        } else {
            System.out.println("***Error al donar d'alta el producte " + nom + "***");
        }

    }

    static void modificaProducte() throws SQLException {
        Scanner teclat = new Scanner(System.in);
        System.out.println("*MODIFICAR PRODUCTE*");

        System.out.println("Codi del producte a modificar: ");
        int codi = teclat.nextInt();
        teclat.nextLine();

        System.out.println("Nou nom: ");
        String nom = teclat.nextLine();

        System.out.println("Nou preu: ");
        String preu = teclat.nextLine();

        System.out.println("Nou stock: ");
        int estoc = teclat.nextInt();
        teclat.nextLine();

        System.out.println("Nova categoria: ");
        String categoria = teclat.nextLine();

        System.out.println("Nou codi de proveïdor: ");
        String nif = teclat.nextLine();

        String modifica = "UPDATE producto SET nom = ?, preu= ?, estoc = ?, categoria = ?, nif = ? WHERE codi = ?";
        PreparedStatement sentencia = null;

        try {
            sentencia = connexioBD.prepareStatement(modifica);
            sentencia.setInt(6, codi);
            sentencia.setString(1, nom);
            sentencia.setString(2, preu);
            sentencia.setInt(3, estoc);
            sentencia.setString(4, categoria);
            sentencia.setString(5, nif);
            sentencia.executeUpdate();
            System.out.println("Producte modificat: " + nom);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("*Hi ha hagut un error*");
        } finally {
            if (sentencia != null)
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
        }
    }

    static void eliminaProducte() throws SQLException {
        Scanner teclat = new Scanner(System.in);
        System.out.println("*Elimina un producte*");

        System.out.println("Nom del producte a eliminar: ");
        String nom = teclat.nextLine();

        String elimina = "DELETE FROM producto WHERE nom = ?";
        PreparedStatement sentencia = null;

        try {
            sentencia = connexioBD.prepareStatement(elimina);
            sentencia.setString(1, nom);
            sentencia.executeUpdate();
            System.out.println("Producte eliminat: " + nom);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("*Hi ha hagut algun error*");
        } finally {
            if (sentencia != null)
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
        }

    }

    static void actualitzarStock() throws IOException, SQLException {
        System.out.println("***ACTUALITZACIO STOCK***");

        File file = new File(PATHPENDENTS);
        file.mkdirs();
        File file2 = new File(PATHPROCESSADES);
        file.mkdirs();

        if (file.isDirectory()) {

            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++) {
                System.out.println("Fitxer: " + files[i]);
                visualitzarActualitzarFitxer(files[i]);
                moureFitxerAProcessat(files[i]);
            }

        }

    }

    static void visualitzarActualitzarFitxer(File file) throws IOException, SQLException {
        FileReader reader = new FileReader(file);
        BufferedReader buffer = new BufferedReader(reader);

        String linea;
        while ((linea = buffer.readLine()) != null) {
            System.out.println(linea);
            int posSep = linea.indexOf(":");
            int idprod = Integer.parseInt(linea.substring(0, posSep));
            System.out.println("El codi del producte es: " + idprod);
            int unitats = Integer.parseInt(linea.substring(posSep + 1));
            System.out.println("El numero de unitats es: " + unitats);
            actualitzaBD(idprod, unitats);

        }
        buffer.close();
        reader.close();
    }

    static void actualitzaBD(int idprod, int unitats) throws SQLException {

        int idprod2 = idprod;

        String actualitza = "UPDATE producto SET estoc = estoc + ? WHERE codi = ?";
        PreparedStatement sentencia = null;

        try {
            sentencia = connexioBD.prepareStatement(actualitza);
            sentencia.setInt(1, unitats);
            sentencia.setInt(2, idprod2);
            sentencia.executeUpdate();
            System.out.println("S'han afegit " + unitats + " unitats al producte: " + idprod);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("*Hi ha hagut un error al actualitzar stock*");
        } finally {
            if (sentencia != null)
                try {
                    sentencia.close();
                } catch (SQLException sqle) {
                    sqle.printStackTrace();
                }
        }
    }

    static void moureFitxerAProcessat(File file) throws IOException {
        FileSystem sistemaFicheros = FileSystems.getDefault();
        Path origen = sistemaFicheros.getPath(PATHPENDENTS + file.getName());
        Path desti = sistemaFicheros.getPath(PATHPROCESSADES + file.getName());

        Files.move(origen, desti, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("S'ha mogut a ENTRADES PROCESSADES EL FITXER: " + file.getName());

    }

    static void generacioComandes_2() throws SQLException, IOException {
        System.out.println("**GENERACIO DE COMANDES**");
        System.out.println("Productes amb menys de 20 unitats disponibles: ");

        int contproveidors = 0;
        int contproductes = 0;

        PrintWriter pw = null;

        String consulta = "SELECT P.codi, P.nom, P.marca, P.preu, P.nif, P.estoc, R.nombre, R.num_telefon, R.ciudad FROM producto P, proveïdor R WHERE P.nif=R.nif AND estoc <20 ORDER BY nif";

        PreparedStatement ps = connexioBD.prepareStatement(consulta);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {

            String actproveidor = rs.getString("nif");
            String nomprov = rs.getString("R.nombre");
            String telefon = rs.getString("R.num_telefon");
            String ciutat = rs.getString("ciudad");

            pw = escriuCapçaleraComanda(nomprov, telefon, ciutat, contproveidors);

            arrayproveidors[contproveidors] = actproveidor;

            contproveidors++;
            do {
                // comprovar si el proveidor ha canviat
                if (!actproveidor.equals(rs.getString("P.nif"))) {
                    actproveidor = rs.getString("P.nif");
                    nomprov = rs.getString("R.nombre");
                    telefon = rs.getString("R.num_telefon");
                    ciutat = rs.getString("ciudad");

                    arrayproductes[contproveidors] = contproductes;

                    contproductes = 0;

                    contproveidors++;

                    arrayproveidors[contproveidors] = actproveidor;

                    pw.close();

                    pw = escriuCapçaleraComanda(nomprov, telefon, ciutat, contproveidors);

                }
                contproductes++;
                System.out.println("\nCodi de producte: " + rs.getInt("codi"));
                System.out.println("Nom: " + rs.getString("nom"));
                System.out.println("Unitats solicitades: " + (100 - rs.getInt("estoc")));

                pw.println(rs.getInt("codi") + "    " + rs.getString("nom") + "     " + rs.getString("marca")
                        + "   UNITATS: " + (100 - rs.getInt("estoc")));
            } while (rs.next());
            arrayproductes[contproveidors] = contproductes;
            pw.close();
        }

    }

    static PrintWriter escriuCapçaleraComanda(String nomprov, String telefon, String ciutat, int contproveidor)
            throws IOException {
        // CREEM NOU FITXER
        FileWriter fw = new FileWriter("Files/COMANDES/" + nomprov + "-" +
                LocalDate.now() + ".txt", false);
        BufferedWriter bf = new BufferedWriter(fw);
        PrintWriter pw = new PrintWriter(bf);

        // Escrivim la capçalera
        pw.println("***EMPRESA SOLICITANT***");
        pw.println(nomempresa1);
        pw.println("codi: " + codiempresa);
        pw.println("direcció: " + direccioempresa);
        pw.println("\n***EMPRESA PROVEIDORA***");
        pw.println(nomprov);
        pw.println("telefon: " + telefon);
        pw.println("direcció: " + ciutat);
        pw.println("\n***PRODUCTEs SOLICITATS***");
        pw.println("\nCodi/     Nom/        Marca/      Unitats solicitades");

        return pw;
    }

    static void analitzarComandes() {
        visualitzarProductes();
        maxProducte();
        minProducte();
        mitjanaProducte();
    }

    static void visualitzarProductes() {
        System.out.println("Proveïdors als que hem solicitat productes: ");
        for (int i = 0; i < arrayproveidors.length; i++) {
            System.out.println("Al proveïdor " + arrayproveidors[i] + " li han sigut encomanats " + arrayproductes[i]
                    + " productes");
        }
    }

    static void maxProducte() {
        System.out.println("Proveidor amb més productes demanats: ");
        int max = arrayproductes[0];
        int prodmax = 0;

        for (int i = 0; i < arrayproductes.length; i++) {
            if (arrayproductes[i] > max) {
                max = arrayproductes[i];
                prodmax = i;
            }
        }
        System.out.println("El proveïdor " + arrayproveidors[prodmax] + " és el més demandat amb "  + max + " unitats.");
    }

    static void minProducte() {
        System.out.println("Proveidor amb menys productes demanats: ");
        int min = arrayproductes[0];
        int prodmin = 0;

        for (int i = 0; i < arrayproductes.length; i++) {
            if (arrayproductes[i] < min) {
                min = arrayproductes[i];
                prodmin = i;
            }
        }
        System.out.println("El proveïdor " + arrayproveidors[prodmin] + " és el menys demandat amb: : " + min + " unitats.");
    }

    static void mitjanaProducte() {
        System.out.println("Mitjana de productes demanats");

        double mitjana = 0;
        double suma = 0;

        for (int i = 0; i < arrayproductes.length; i++) {
            suma += arrayproductes[i];

        }
        mitjana = suma / arrayproductes.length;
        System.out.println("La mitja de productes solicitats per proveidor es de " + mitjana + "productes");
    }

    static void tancarConnexioBD() throws SQLException {
        connexioBD.close();
    }

}
