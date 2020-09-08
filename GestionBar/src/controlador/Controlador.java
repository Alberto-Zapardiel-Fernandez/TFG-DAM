package controlador;

import static controlador.Controlador.conexion;
import modelo.Conexion;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import vista.Login;
import vista.InterfazJefe;
import vista.Principal;
import vista.RegistroBD;

/**
 *
 * @author alberto
 */
public class Controlador {

    static Connection conexion;

    public static String nombreBD = "";
    public static String usuarioLogeado = "";

    static ArrayList<String> bds = new ArrayList<>();
    public static String nombreBDLimpio;

    public static void main(String[] args) throws SQLException {

        Conexion cn = new Conexion();
        
        bds = cn.verBD();
        int contador = 0;
        for (String bd : bds) {
            //Revisamos si ya está la nueva bd entre todas
            if (!bd.equalsIgnoreCase("information_schema")
                    && !bd.equalsIgnoreCase("mysql")
                    && !bd.equalsIgnoreCase("performance_schema")
                    && !bd.equalsIgnoreCase("phpmyadmin")
                    && !bd.equalsIgnoreCase("test")) {
                nombreBD = bd;
                contador++;
                Conexion con = new Conexion(nombreBD);
                conexion = con.getConexion();
                if (conexion != null) {
                    Login login = new Login();
                    login.setVisible(true);

                }
            } else {
                contador++;
            }
        }
        if (contador <=5 && contador >0) {
            //Aquí crear el dialogo para recoger los datos
            RegistroBD registroBD = new RegistroBD();
            registroBD.setVisible(true);
        }

    }

    public static String reemplazarEspacios(String nombreBD) {
        return nombreBD.trim().replace(" ", "_");
    }

    public static String reemplazarGuiones(String nombreBD) {
        return nombreBD.trim().replace("_", " ");
    }

    public static boolean comprobarUser(String usuario, String pass) {
        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from usuarios where nombre='" + usuario + "' and pass='" + pass + "'");
            resultSet.next();
            if (resultSet.getString(1).equalsIgnoreCase(usuario)) {
                resultSet.close();
                return true;
            }
        } catch (SQLException ex) {
            return false;
        }
        return false;
    }

    //Método para crear la interfaz, si es jefe crear una y si no otra
    public static void seguirLogeado(String usuario, String pass) {

        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from usuarios where nombre='" + usuario.toUpperCase() + "' and pass='" + pass + "'");
            resultSet.next();
            if (resultSet.getInt(3) == 0) {
                //Es jefe
                InterfazJefe interfazJefe = new InterfazJefe();
                interfazJefe.setVisible(true);
                interfazJefe.ponerNombre(resultSet.getString(4));
                usuarioLogeado = usuario;
                nombreBDLimpio = resultSet.getString(4);
            } else {
                //Si no es jefe creas la principal de gestion
                Principal principal = new Principal();
                principal.setVisible(true);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void iniciarBD(String nombreBD, String usuario, String pass) {
        String nombreLimpio = nombreBD;
        nombreBD = reemplazarEspacios(nombreBD);
        Conexion con = new Conexion(nombreBD);
        conexion = con.crearConexion(usuario, pass);
        if (conexion != null) {
            Login login = new Login(nombreLimpio);
            login.setVisible(true);
        }
    }

    public static String addUser(String usuario, String pass, int rol) {
        try {
            Statement statement = conexion.createStatement();
            statement.executeUpdate("insert into usuarios values('" + usuario + "','" + pass + "'," + rol + ",'" + Controlador.nombreBDLimpio + "',1)");
            return "INSERTADO";
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            if (ex.getMessage().contains("Duplicate entry ")) {
                return "YA EXISTE";
            }
            return "NO INSERTADO";
        }
    }

    public static String borrar(String nombre, String tabla) {
        try {
            Statement statement = conexion.createStatement();
            int resultSet = statement.executeUpdate("delete from " + tabla + " where nombre='" + nombre + "'");
            if (resultSet == 0) {
                return "YA EXISTE";
            } else {
                return "BORRADO";
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            if (ex.getMessage().contains("Duplicate entry ")) {
                return "No existe";
            }
            return "NO BORRADO";
        }
    }

    public static ArrayList<Object[]> obtenerUsuarios() {

        ArrayList<Object[]> datos = new ArrayList<>();

        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select nombre,rol from usuarios");
            while (resultSet.next()) {
                String salida;
                if (resultSet.getInt(2) == 0) {
                    salida = "Jefe";
                } else {
                    salida = "Empleado";
                }
                Object[] usuarioBD = new Object[]{resultSet.getString(1), salida};
                datos.add(usuarioBD);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }

        return datos;
    }

    public static ArrayList<Object[]> obtenerLista(String tabla) {
        ArrayList<Object[]> datos = new ArrayList<>();

        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select nombre,precio from " + tabla);
            while (resultSet.next()) {
                Object[] usuarioBD = new Object[]{resultSet.getString(1), resultSet.getDouble(2)};
                datos.add(usuarioBD);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }

        return datos;
    }

    public static boolean comprobar(String dato, String tabla) {
        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select nombre from " + tabla + " where nombre='" + dato + "'");
            resultSet.next();
            if (resultSet.getString(1).equalsIgnoreCase(dato)) {
                resultSet.close();
                return true;
            }
        } catch (SQLException ex) {
            return false;
        }
        return false;
    }

    public static String add(String nombre, double precio, String tabla) {
        try {
            Statement statement = conexion.createStatement();
            statement.executeUpdate("insert into " + tabla + " values('" + nombre + "'," + precio + ")");
            return "INSERTADO";
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            if (ex.getMessage().contains("Duplicate entry ")) {
                return "Ya existe";
            }
            return "NO INSERTADO";
        }
    }

    public static Double verPrecioMenu() {
        Double precio = 0.00;
        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select precio from comida where nombre='menú'");
            resultSet.next();
            precio = resultSet.getDouble(1);
        } catch (SQLException ex) {
            return precio;
        }
        return precio;
    }

    public static String insertarPrecioMenu(double precio) {
        try {
            Statement statement = conexion.createStatement();
            statement.executeUpdate("update comida set precio =" + precio + " where nombre='Menú'");
            return "Modificado";
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return "No modificado";
        }
    }

    public static ArrayList<String> obtenerNombres(String tabla) {
        ArrayList<String> nombres = new ArrayList<>();
        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select nombre from " + tabla);
            while (resultSet.next()) {
                nombres.add(resultSet.getString(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }

        return nombres;
    }

    public static Double damePrecio(String tabla, String nombre) {
        Double precio = 0.00;
        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select precio from " + tabla + " where nombre='" + nombre + "'");
            resultSet.next();
            precio = resultSet.getDouble(1);
        } catch (SQLException ex) {
            return precio;
        }
        return precio;
    }

    public static void rehacerMesas(int numMesas) {

        Statement statement;
        try {
            statement = conexion.createStatement();

            String sqlBorrarMesas = "DROP TABLE mesas";
            statement.executeUpdate(sqlBorrarMesas);

            //Crea la tabla para las mesas
            String sqlMesas = "CREATE TABLE mesas(numero int(4))";
            statement.executeUpdate(sqlMesas);
            System.out.println("TABLA CREADA");

            for (int i = 0; i < numMesas; i++) {
                //Inserta mesas
                String sqlNMesas = "INSERT INTO mesas VALUES(" + (i + 1) + ")";
                statement.executeUpdate(sqlNMesas);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ArrayList<Integer> obtenerNumeros() {
        ArrayList<Integer> numeros = new ArrayList<>();
        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select numero from mesas");
            while (resultSet.next()) {
                numeros.add(resultSet.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }

        return numeros;
    }

    public static void addRowADetalle(String nombre, int cantidad, Double precio, double total, int nMesa) {
        try {
            Statement statement = conexion.createStatement();
            statement.executeUpdate("insert into detallemesas values('" + nombre + "'," + cantidad + "," + precio + "," + total + "," + nMesa + ")");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        System.out.println("INSERTADO");
    }

    public static void limpiaDetalles() {
        Statement statement;
        try {
            statement = conexion.createStatement();

            String sqlBorrarMesas = "DROP TABLE detallemesas";
            statement.executeUpdate(sqlBorrarMesas);

            //Crea la tabla para los detalles de la mesa
            String sqlDetalleMesas = "CREATE TABLE detallemesas("
                    + "nombre varchar(50),"
                    + "cantidad int(4),"
                    + "precio DEC(10,2),"
                    + "total DEC(10,2),"
                    + "numero int(4))";
            statement.executeUpdate(sqlDetalleMesas);
            System.out.println("TABLA CREADA");

        } catch (SQLException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static ArrayList<Detalle> traeLineasMesa(Object selectedItem) {
        ArrayList<Detalle> lineas = new ArrayList<>();

        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from detallemesas where numero=" + Integer.parseInt(selectedItem.toString()));
            while (resultSet.next()) {
                Detalle detalle = new Detalle(resultSet.getString(1), resultSet.getInt(2), resultSet.getDouble(3), resultSet.getDouble(4), resultSet.getInt(5));
                lineas.add(detalle);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return lineas;
    }

    public static int cantidadLineas(Object selectedItem) {
        ArrayList<Detalle> lineas = new ArrayList<>();

        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from detallemesas where numero=" + Integer.parseInt(selectedItem.toString()));
            while (resultSet.next()) {
                Detalle detalle = new Detalle(resultSet.getString(1), resultSet.getInt(2), resultSet.getDouble(3), resultSet.getDouble(4), resultSet.getInt(5));
                lineas.add(detalle);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return lineas.size();
    }

    public static void borrarTodoDetalle(Object numero) {
        try {
            Statement statement = conexion.createStatement();
            statement.executeUpdate("delete from detallemesas where numero=" + numero);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void removeLineaDetalle(String nombre, int cantidad, Object numero) {
        try {
            Statement statement = conexion.createStatement();
            statement.executeUpdate("delete from detallemesas where numero=" + numero + " and nombre ='" + nombre + "' and cantidad=" + cantidad);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void insertarDetalleTotal(String nombre, int cantidad, double totalASumar) {
        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from detalletotal where nombre='" + nombre + "'");
            int cantidadASumar = cantidad+0;
            double total = totalASumar+0;
            if (resultSet.next()) {

                cantidadASumar = resultSet.getInt(2) + cantidad;
                total = resultSet.getDouble(3) + totalASumar;

                Statement statement2 = conexion.createStatement();
                statement2.executeUpdate("delete from detalletotal where nombre ='" + nombre + "'");

            }
            Statement statement3 = conexion.createStatement();
            statement3.executeUpdate("insert into detalletotal values('" + nombre + "'," + cantidadASumar + "," + total + ")");
            System.out.println("Insertado");
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void borrarFinDia() {
        Statement statement;
        try {
            statement = conexion.createStatement();
            String sqlBorrarTotal = "DROP TABLE detalletotal";
            statement.executeUpdate(sqlBorrarTotal);

            //Crea la tabla para los detalles del total
            String sqlDetalleTotal = "CREATE TABLE detalletotal("
                    + "nombre varchar(50),"
                    + "cantidad int(4),"
                    + "total DEC(10,2))";
            statement.executeUpdate(sqlDetalleTotal);
            System.out.println("TABLA CREADA");
        } catch (SQLException ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static String verFinDia() {
        
            String salida="";
            double total=0;
        try {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from detallemesas");
            while (resultSet.next()) {
                salida+=resultSet.getString(1)+"   "+resultSet.getInt(2)+" unidades,   "+resultSet.getDouble(3)+"€\n";
                total+=resultSet.getDouble(3);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        salida+="TOTAL: "+total+"€";
        return salida;
    }

}
