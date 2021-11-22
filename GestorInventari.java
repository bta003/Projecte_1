package Projecte;

import java.util.Scanner;

import java.sql.*;

public class GestorInventari {

    static Connection connexioBD = null;
    PreparedStatement ps = null;

    public static void main(String[] args) {

        try {
            connexioBD();
            menuAPP();
        } catch (SQLException ex) {
            System.out.println("Hi ha hagut un problema amb la BD");
            ex.printStackTrace();
        } catch (Exception e) {
            System.out.println("Hi ha hagut un problema amb l'aplicació");
            e.printStackTrace();
        }

    }

    // MENU PRINCIPAL DE L'APLICACIÓ
    static void menuAPP() throws SQLException {
        Scanner teclat = new Scanner(System.in);
        boolean sortir = false;
        do {
            System.out.println("*MENU GESTOR INVENTARI*");
            System.out.println("1. Gestió productes");
            System.out.println("2. Actualització stock");
            System.out.println("3. Generació comandes");
            System.out.println("4. Analitzar comandes");
            System.out.println("5. Sortir");
            System.out.println("TRIA UNA OPCIÓ:");

            int opcio = teclat.nextInt();

            switch (opcio) {
            case 1:
                // MENÚ GESTIÓ PRODUCTES
                gestioProductes();
                break;
            case 2:
                // actualitzarStock();
                break;
            case 3:
                // generacioComandes();
                break;
            case 4:
                // analitzarComandes();
                break;
            case 5:
                sortir = true;
                break;
            default:
                System.out.println("\n" + opcio + " NO ÉS UNA OPCIÓ NO VÀLIDA");
            }
        } while (!sortir);
        // desconnexioBD();
    }

    // MENÚ GESTIÓ PRODUCTES
    static void gestioProductes() throws SQLException {
        Scanner teclat = new Scanner(System.in);
        boolean sortir = false;

        do {
            System.out.println("\n*GESTIÓ PRODUCTES*");

            System.out.println("1. Consultat de tots els productes");
            System.out.println("2. Consulta d'un producte");
            System.out.println("3. Alta d'un producte");
            System.out.println("4. Modificació d'un producte");
            System.out.println("5. Baixa d'un producte");
            System.out.println("6. Sortir");
            System.out.println("TRIA UNA OPCIÓ:");

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
        String password = "costa2021";
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
            System.out.print("codi proveïdor: " + rs.getInt("nif") + " \n");
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
            System.out.print("codi proveïdor: " + rs.getInt("nif") + " \n");
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
        int nif = teclat.nextInt();
        teclat.nextLine();

        String consulta = "INSERT INTO producto (codi, nom, marca, preu, estoc, categoria, nif) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement sentencia = connexioBD.prepareStatement(consulta);

        sentencia.setInt(1, codi);
        sentencia.setString(2, nom);
        sentencia.setString(3, marca);
        sentencia.setString(4, preu);
        sentencia.setInt(5, estoc);
        sentencia.setString(6, categoria);
        sentencia.setInt(7, nif);

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
        int nif = teclat.nextInt();
        teclat.nextLine();

        String modifica = "UPDATE producto SET nom = ?, preu= ?, estoc = ?, categoria = ?, nif = ? WHERE codi = ?";
        PreparedStatement sentencia = null;

        try {
            sentencia = connexioBD.prepareStatement(modifica);
            sentencia.setInt(6, codi);
            sentencia.setString(1, nom);
            sentencia.setString(2, preu);
            sentencia.setInt(3, estoc);
            sentencia.setString(4, categoria);
            sentencia.setInt(5, nif);
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

}
