package dam.accesodatos.model;

public class Proyecto {
    private int codigo_proyec;
    private String nombre_proyec;
    private double precio;
    private int codigo_cliente;

    public Proyecto() {
    }

    public Proyecto(int codigo_proyec, String nombre_proyec, double precio, int codigo_cliente) {
        this.codigo_proyec = codigo_proyec;
        this.nombre_proyec = nombre_proyec;
        this.precio = precio;
        this.codigo_cliente = codigo_cliente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Proyecto proyecto)) return false;

        return codigo_proyec == proyecto.codigo_proyec;
    }

    @Override
    public int hashCode() {
        return codigo_proyec;
    }

    public int getCodigo_proyec() {
        return codigo_proyec;
    }

    public void setCodigo_proyec(int codigo_proyec) {
        this.codigo_proyec = codigo_proyec;
    }

    public String getNombre_proyec() {
        return nombre_proyec;
    }

    public void setNombre_proyec(String nombre_proyec) {
        this.nombre_proyec = nombre_proyec;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getCodigo_cliente() {
        return codigo_cliente;
    }

    public void setCodigo_cliente(int codigo_cliente) {
        this.codigo_cliente = codigo_cliente;
    }
}
