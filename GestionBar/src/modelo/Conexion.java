package modelo;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import vista.RegistroBD;

public class Conexion {

    static Connection conexion = null;
    static String DRIVER = "";
    static String URLDB = "";
    static String USUARIO = "root";
    static String CLAVE = "";
    static String nombreBD;

    public Conexion(String nombreBD) {
        Conexion.nombreBD = nombreBD;
        DRIVER = "com.mysql.jdbc.Driver";
        URLDB = "jdbc:mysql://localhost/" + nombreBD;
    }

    public Conexion() {
        verBD();
    }

    public static Connection getConexion() {
        if (conexion == null) {
            try {
                Class.forName(DRIVER);
                conexion = DriverManager.getConnection(URLDB, USUARIO, CLAVE);
                return conexion;
            } catch (SQLException | ClassNotFoundException ex) {
            }
        }
        return conexion;
    }

    public static Connection crearConexion(String usuario, String pass) {
        if (conexion == null) {
            try {
                Class.forName(DRIVER);
                conexion = DriverManager.getConnection(URLDB, USUARIO, CLAVE);
                return conexion;
            } catch (SQLException ex) {
                if (ex.getMessage().contains("Unknown database")) {
                    try {
                        conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USUARIO, CLAVE);
                        Statement statement = conexion.createStatement();
                        statement.executeUpdate("CREATE DATABASE " + nombreBD);
                        System.out.println("BD CREADA");
                        crearTablas(usuario, pass);
                    } catch (SQLException ex1) {
                        Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex1);
                        System.out.println("Error al crear la BD " + nombreBD);
                    }
                } else {
                    System.out.printf("HA OCURRIDO UNA EXCEPCIÓN:%n");
                    System.out.printf("Mensaje   : %s %n", ex.getMessage());
                    System.out.printf("SQL estado: %s %n", ex.getSQLState());
                    System.out.printf("Cód error : %s %n", ex.getErrorCode());
                }
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return conexion;
    }

    private static void crearTablas(String usuario, String pass) {

        Statement statement;
        try {
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + nombreBD, USUARIO, CLAVE);
            statement = conexion.createStatement();

            //Crea la tabla de usuarios
            String sqlCrearUsuarios = "CREATE TABLE usuarios("
                    + "nombre varchar(20) UNIQUE NOT NULL,"
                    + "pass varchar(15),"
                    + "rol int(2),"
                    + "nombreBD varchar(50),"
                    + "registrado int(2),"
                    + "PRIMARY KEY (nombre))";
            statement.executeUpdate(sqlCrearUsuarios);
            System.out.println("TABLA CREADA");

            //Crea la tabla para la comida
            String sqlCrearComida = "CREATE TABLE comida("
                    + "nombre varchar(50) UNIQUE NOT NULL,"
                    + "precio DEC(10,2),"
                    + "PRIMARY KEY (nombre))";
            statement.executeUpdate(sqlCrearComida);
            System.out.println("TABLA CREADA");

            //Crea la tabla para la bebida
            String sqlCrearBebida = "CREATE TABLE bebida("
                    + "nombre varchar(50) UNIQUE NOT NULL,"
                    + "precio DEC(10,2),"
                    + "PRIMARY KEY (nombre))";
            statement.executeUpdate(sqlCrearBebida);
            System.out.println("TABLA CREADA");

            //Crea la tabla para el postre
            String sqlCrearPostre = "CREATE TABLE postre("
                    + "nombre varchar(50) UNIQUE NOT NULL,"
                    + "precio DEC(10,2),"
                    + "PRIMARY KEY (nombre))";
            statement.executeUpdate(sqlCrearPostre);
            System.out.println("TABLA CREADA");

            //Crea la tabla para las mesas
            String sqlMesas = "CREATE TABLE mesas(numero int(4))";
            statement.executeUpdate(sqlMesas);
            System.out.println("TABLA CREADA");

            for (int i = 0; i < RegistroBD.numeroMesas; i++) {
                //Inserta mesas
                String sqlNMesas = "INSERT INTO mesas VALUES(" + (i+1) + ")";
                statement.executeUpdate(sqlNMesas);
            }

            //Crea la tabla para los detalles de la mesa
            String sqlDetalleMesas = "CREATE TABLE detallemesas("
                    + "nombre varchar(50),"
                    + "cantidad int(4),"
                    + "precio DEC(10,2),"
                    + "total DEC(10,2),"
                    + "numero int(4))";
            statement.executeUpdate(sqlDetalleMesas);
            System.out.println("TABLA CREADA");

            //Crea la tabla para los detalles del total
            String sqlDetalleTotal = "CREATE TABLE detalletotal("
                    + "nombre varchar(50),"
                    + "cantidad int(4),"
                    + "total DEC(10,2))";
            statement.executeUpdate(sqlDetalleTotal);
            System.out.println("TABLA CREADA");

            //Inserta el usuario principal
            String sqlCrearUsuario = "INSERT INTO USUARIOS VALUES('" + usuario + "','" + pass + "',0,'" + nombreBD.trim().replace("_", " ") + "',1)";
            statement.executeUpdate(sqlCrearUsuario);

            //Inserta menu
            String sqlMenu = "INSERT INTO comida VALUES('Menú',10.00)";
            statement.executeUpdate(sqlMenu);

            //Inserta un plato
            String sqlUnPlato = "INSERT INTO comida VALUES('Macarrones con tomate',6.50)";
            statement.executeUpdate(sqlUnPlato);

            //Inserta otro plato
            String sqlDosPlato = "INSERT INTO comida VALUES('Chuletón de Ávila',16.00)";
            statement.executeUpdate(sqlDosPlato);

            //Inserta un postre
            String sqlUnPostre = "INSERT INTO postre VALUES('Arroz con leche',4.50)";
            statement.executeUpdate(sqlUnPostre);

            //Inserta otro postre
            String sqlDosPostre = "INSERT INTO postre VALUES('Tiramisú',8.00)";
            statement.executeUpdate(sqlDosPostre);

            //Inserta una bebida
            String sqlUnaBebida = "INSERT INTO bebida VALUES('Coca-Cola',2.50)";
            statement.executeUpdate(sqlUnaBebida);

            //Inserta otra bebida
            String sqlDosBebida = "INSERT INTO bebida VALUES('Agua 50 cl.',1.20)";
            statement.executeUpdate(sqlDosBebida);

            //Inserta otra bebida
            String sqlTresBebida = "INSERT INTO bebida VALUES('Fanta Naranja',2.50)";
            statement.executeUpdate(sqlTresBebida);

        } catch (SQLException ex2) {
            System.out.println("YA HAY UNA TABLA con ese nombre");
            System.out.println(ex2.getMessage());
            ex2.getStackTrace();
        }
    }

    public static ArrayList<String> verBD() {
        DRIVER = "com.mysql.jdbc.Driver";
        ArrayList<String> bds = new ArrayList<>();
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Connection cn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USUARIO, CLAVE);
            DatabaseMetaData metaData = cn.getMetaData();
            ResultSet resultSet = metaData.getCatalogs();

            while (resultSet.next()) {
                String bd = resultSet.getString(1);
                bds.add(bd);
            }
            cn.close();
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bds;
    }

}
