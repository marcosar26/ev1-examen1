package dam.accesodatos;

import com.fasterxml.jackson.databind.ObjectMapper;
import dam.accesodatos.model.Cliente;
import dam.accesodatos.model.Proyecto;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Esto es un proyecto Maven con un pom.xml que ya incluye todas las dependencias que puedas necesitar (probablemente
 * haya más de la cuenta, no obstante, no hace falta que quites ninguna).
 * <p>
 * Lo primero que debes hacer es un Maven > Reload project.
 * <p>
 * Para continuar, debes crear la estructura de la base de datos con la que trabajará el proyecto. Para ello tienes un
 * script denominado bdscript.sql en src/main/resources. Cárgalo mediante DataGrip. Se creará una base de datos
 * ad_ev1_examen1 con dos tablas, clientes y proyectos sin datos.
 * <p>
 * El código que se proporciona en esta clase no es necesario que lo modifiques. Te servirá para probar la aplicación.
 * Si implementas correctamente todas las funcionalidades se te mostrará en consola una salida similar a lo que
 * aparece en el archvio src/main/resources/salida.txt.
 * <p>
 * No obstante, si prefieres estructurar la aplicación de otra manera, puedes hacerlo, siempre y cuando implementes
 * las funcionalidades que se piden.
 */
public class App {
    static final Connection con;

    static {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/ad_ev1_examen1", "root", "toor");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * No es necesario modificar este constructor.
     * La aplicación NO necesita tener un menú.
     */
    public App() {
        elimiarDatosEnTablas();
        List<Cliente> clientes = cargarClientesDeFichero("src/main/resources/clientes.csv");
        System.out.println("Se han insertado " + insertarClientesEnBD(clientes) + " clientes en la BD");
        System.out.println(buscarClientes("PAMPLONA", "9483"));
        Proyecto[] proyectos = cargarProyectosDeFichero("src/main/resources/proyectos.json");
        System.out.println("Se han insertado " + insertarProyectosEnBD(proyectos) + " proyectos en la BD");

        String localidad = "BarcelONa";
        System.out.printf("El precio medio de los proyectos de los clientes de %s es %.2f€\n", localidad, calcularPrecioMedioProyectos(localidad));
        System.out.println("Incrementado el precio de " + incrementarPrecioProyectos("Oviedo", 0.2) + " proyectos");
        int numProyectos = 1;
        exportarClientesConMasOMismosProyectosQueAJson(numProyectos, String.format("src/main/resources/clientes-con-%s-proyectos-o-mas.json", numProyectos));
    }

    public static void main(String[] args) {
        new App();
    }

    private void elimiarDatosEnTablas() {
        try {
            Statement st = con.createStatement();
            st.execute("DELETE FROM proyectos");
            st.execute("DELETE FROM clientes");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Importar clientes de fichero CSV
     * Abre el fichero clientes.csv que se te proporciona (en la ruta src/main/resources).
     * Tu programa debe leer ese fichero y cargarlo en una colección de objetos Cliente.
     * Para ello implementa este método.
     * <p>
     * 1 punto
     *
     * @param ruta ruta al fichero importar (incluye el nombre del fichero)
     * @return una lista con los clientes existentes en dicho fichero.
     */
    private List<Cliente> cargarClientesDeFichero(String ruta) {
        List<Cliente> clientes = new ArrayList<>();
        File file = new File(ruta);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(";");
                int codigo, telefono;
                try {
                    codigo = Integer.parseInt(campos[0]);
                    telefono = Integer.parseInt(campos[5]);
                } catch (NumberFormatException e) {
                    System.out.println("Saltando cliente inválido");
                    continue;
                }
                Cliente cliente = new Cliente(codigo, campos[1], campos[2], campos[3], campos[4], telefono);
                clientes.add(cliente);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No se ha encontrado el fichero");
        } catch (IOException e) {
            System.out.println("Error al leer el fichero");
        }
        return clientes;
    }

    /**
     * Insertar clientes en la base de datos
     * Debes guardar en la tabla correspondiente de la base de datos todos los clientes, con todos sus atributos,
     * que has leído previamente del fichero CSV con el método cargarClientesDeFichero.
     * <p>
     * 1,25 puntos
     *
     * @param clientes la lista de clientes a insertar en base de datos.
     * @return El número de clientes insertados en la base de datos.
     */
    private int insertarClientesEnBD(List<Cliente> clientes) {
        int numClientes = 0;
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO clientes VALUES (?, ?, ?, ?, ?, ?)")) {
            for (Cliente cliente : clientes) {
                ps.setInt(1, cliente.getCodigo_cli());
                ps.setString(2, cliente.getNombre_cli());
                ps.setString(3, cliente.getNif());
                ps.setString(4, cliente.getDireccion());
                ps.setString(5, cliente.getCiudad());
                ps.setInt(6, cliente.getTelefono());
                ps.addBatch();
            }
            int[] resultados = ps.executeBatch();
            for (int resultado : resultados) {
                if (resultado == 1) {
                    numClientes++;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar clientes en la base de datos");
        }
        return numClientes;
    }

    /**
     * Buscar clientes por ciudad y teléfono
     * Busca y retorna en una lista todos los clientes de una ciudad y con un determinado teléfono.
     * <p>
     * 1 punto.
     *
     * @param ciudad   la búsqueda de ciudad no debe ser sensible a mayúsculas o minúsculas. Por tanto, debe producir
     *                 el mismo resultado tanto si buscamos “OVIEDO” como “Oviedo” o “OvIeDo”.
     * @param telefono busca los teléfonos que empiecen por la cadena de texto que se pase como parámetro. Por tanto,
     *                 no es necesario pasar el teléfono completo, sino que podemos pasar solo la parte inicial.
     *                 Por ejemplo, si pasamos “948” encontrará todos los teléfonos que comiencen por “948”.
     * @return la lista de clientes que se ajustan a los criterios de búsqueda.
     */
    private List<Cliente> buscarClientes(String ciudad, String telefono) {
        List<Cliente> clientes = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM clientes WHERE ciudad LIKE ? AND telefono LIKE ?")) {
            ps.setString(1, "%" + ciudad.toUpperCase() + "%");
            ps.setString(2, telefono + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int codigo = rs.getInt("codigo_cli");
                String nombre = rs.getString("nombre_cli");
                String nif = rs.getString("nif");
                String direccion = rs.getString("direccion");
                String ciudadCliente = rs.getString("ciudad");
                int telefonoCliente = rs.getInt("telefono");
                clientes.add(new Cliente(codigo, nombre, nif, direccion, ciudadCliente, telefonoCliente));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar clientes");
        }
        return clientes;
    }

    /**
     * Importar proyectos de fichero JSON
     * Debes importar los proyectos que se encuentran en el archivo JSON ubicado en “src/main/resources/proyectos.json”.
     * Para ello puedes utilizar cualquiera de las dos librerías vistas en clase, ya que ambas dependencias están ya
     * incluidas en el pom.xml.
     * <p>
     * 1 punto
     *
     * @param ruta ruta al fichero JSON a importar (incluye el nombre del fichero).
     * @return un array de proyectos con los proyectos importados de fichero.
     */
    private Proyecto[] cargarProyectosDeFichero(String ruta) {
        Proyecto[] proyectos = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            proyectos = mapper.readValue(new File(ruta), Proyecto[].class);
        } catch (IOException e) {
            System.out.println("Error al leer el fichero JSON");
        }
        return proyectos;
    }

    /**
     * Insertar proyectos en la base de datos
     * Al igual que has hecho con los clientes, crea un método que recibe un array de proyectos y los inserta en la tabla
     * correspondiente de la base de datos. Debes introducir todos los valores, incluyendo el código de cliente de cada
     * proyecto.
     * <p>
     * 1,25 puntos
     *
     * @param proyectos Colección de proyectos a insertar en la base de datos.
     * @return Número de proyectos insertados en la base de datos.
     */
    private int insertarProyectosEnBD(Proyecto[] proyectos) {
        int numProyectos = 0;
        try (PreparedStatement ps = con.prepareStatement("INSERT INTO proyectos VALUES (?, ?, ?, ?)")) {
            for (Proyecto proyecto : proyectos) {
                ps.setInt(1, proyecto.getCodigo_proyec());
                ps.setString(2, proyecto.getNombre_proyec());
                ps.setDouble(3, proyecto.getPrecio());
                ps.setInt(4, proyecto.getCodigo_cliente());
                ps.addBatch();
            }
            int[] resultados = ps.executeBatch();
            for (int resultado : resultados) {
                if (resultado == 1) {
                    numProyectos++;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar proyectos en la base de datos");
        }
        return numProyectos;
    }

    /**
     * Buscar proyectos por cliente
     * Debes buscar todos los proyectos de un cliente determinado.
     * <p>
     * 1 punto
     *
     * @param codigoCliente el código del cliente del cual queremos recuperar sus proyectos.
     * @return una lista con todos los proyectos del cliente.
     */
    private List<Proyecto> buscarProyectos(int codigoCliente) {
        List<Proyecto> proyectos = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM proyectos WHERE codigo_cliente = ?")) {
            ps.setInt(1, codigoCliente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int codigo = rs.getInt("codigo_proyec");
                String nombre = rs.getString("nombre_proyec");
                double precio = rs.getDouble("precio");
                int codigoClienteProyecto = rs.getInt("codigo_cliente");
                proyectos.add(new Proyecto(codigo, nombre, precio, codigoClienteProyecto));
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar proyectos");
        }
        return proyectos;
    }

    /**
     * Calcular la media de los proyectos de una localidad
     * Implementa un método que retorne el precio medio de los proyectos de clientes de una determinada localidad.
     * La media debe ser calculada por la sentencia SQL, no a través de código Java.
     * <p>
     * 1,25 puntos.
     *
     * @param localidad La localidad no debe tener en cuenta mayúsculas ni minúsculas.
     * @return el precio medio de los proyectos que se ajustan al criterio de búsqueda.
     */
    private double calcularPrecioMedioProyectos(String localidad) {
        double precioMedio = 0;
        try (PreparedStatement ps = con.prepareStatement("SELECT AVG(precio) AS precio_medio FROM proyectos p " + "INNER JOIN clientes c ON p.codigo_cliente = c.codigo_cli WHERE UPPER(ciudad) = UPPER(?)")) {
            ps.setString(1, localidad);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                precioMedio = rs.getDouble("precio_medio");
            }
        } catch (SQLException e) {
            System.out.println("Error al calcular el precio medio de los proyectos");
        }
        return precioMedio;
    }

    /**
     * Incrementar el precio de los proyectos de los clientes de una localidad
     * Crea un método que reciba un nombre de localidad y un incremento y modifique el precio de todos los proyectos de
     * todos los clientes de esa localidad.
     * <p>
     * 1,25 puntos.
     *
     * @param localidadCliente localidad en la que viven los clientes cuyos proyectos deben sufrir el incremento de precio.
     * @param incremento       El incremento se expresa en número decimal, de manera que, por ejemplo, si el incremento vale 0.2,
     *                         habrá que subir el precio en un 20% al proyecto.
     * @return el número de proyectos a los cuáles se les ha incrementado el precio.
     */
    private int incrementarPrecioProyectos(String localidadCliente, double incremento) {
        int numProyectos = 0;
        try (PreparedStatement ps = con.prepareStatement("UPDATE proyectos p " + "INNER JOIN clientes c ON p.codigo_cliente = c.codigo_cli " + "SET precio = precio + (precio * ?) WHERE UPPER(ciudad) = UPPER(?)")) {
            ps.setDouble(1, incremento);
            ps.setString(2, localidadCliente);
            numProyectos = ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al incrementar el precio de los proyectos");
        }
        return numProyectos;
    }

    /**
     * Exportar a JSON los clientes (con sus proyectos) que tienen igual o más proyectos asignados que un número dado
     * Crea un método que busque en la base de datos todos los clientes con igual o mayor número de proyectos asignados
     * que el parámetro que recibe. Debe extraer los clientes y sus proyectos asignados, y a continuación exportar esa
     * información a un fichero JSON. El nombre de este fichero junto con su ruta relativa se recibe como segundo
     * parámetro del método. El resultado de la exportación a JSON debe ser como la que se proporciona en el directorio
     * de resources (el ejemplo exporta todos los clientes con al menos un proyecto).
     * Nota: si el parámetro numProyectos vale 0, se deben exportar también todos los clientes que no tengan proyectos
     * asignados.
     * <p>
     * 2 puntos
     *
     * @param numProyectos número de proyectos mínimo que tienen asignados los clientes a exportar.
     * @param ruta         ruta donde se guardará el fichero exportado (incluye el nombre del fichero):
     */
    private void exportarClientesConMasOMismosProyectosQueAJson(int numProyectos, String ruta) {
        List<Cliente> clientes = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM clientes c " + "LEFT JOIN proyectos p ON c.codigo_cli = p.codigo_cliente " + "WHERE p.codigo_cliente IS NOT NULL OR ? = 0 " + "GROUP BY c.codigo_cli " + "HAVING COUNT(p.codigo_proyec) >= ?")) {
            ps.setInt(1, numProyectos);
            ps.setInt(2, numProyectos);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int codigo = rs.getInt("codigo_cli");
                String nombre = rs.getString("nombre_cli");
                String nif = rs.getString("nif");
                String direccion = rs.getString("direccion");
                String ciudad = rs.getString("ciudad");
                int telefono = rs.getInt("telefono");
                List<Proyecto> proyectos = buscarProyectos(codigo);
                Cliente cliente = new Cliente(codigo, nombre, nif, direccion, ciudad, telefono, proyectos.toArray(Proyecto[]::new));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            System.out.println("Error al ejecutar la consulta de clientes con proyectos");
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(ruta), clientes);
        } catch (IOException e) {
            System.out.println("Error al escribir el fichero JSON");
        }
    }
}
