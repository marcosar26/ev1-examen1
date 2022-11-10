package dam.accesodatos.model;

import java.util.List;

public class Cliente {
    private int codigo_cli;
    private String nombre_cli;
    private String nif;
    private String direccion;
    private String ciudad;
    private int telefono;
    private List<Proyecto> proyectos;

    public Cliente(int codigo_cli, String nombre_cli, String nif, String direccion, String ciudad, int telefono, Proyecto... proyectos) {
        this.codigo_cli = codigo_cli;
        this.nombre_cli = nombre_cli;
        this.nif = nif;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.telefono = telefono;
        this.proyectos = List.of(proyectos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cliente cliente)) return false;

        return codigo_cli == cliente.codigo_cli;
    }

    @Override
    public int hashCode() {
        return codigo_cli;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Cliente{");
        sb.append("codigo_cli=").append(codigo_cli);
        sb.append(", nombre_cli='").append(nombre_cli).append('\'');
        sb.append(", nif='").append(nif).append('\'');
        sb.append(", direccion='").append(direccion).append('\'');
        sb.append(", ciudad='").append(ciudad).append('\'');
        sb.append(", telefono=").append(telefono);
        if (proyectos.size() > 0) sb.append(", proyectos=").append(proyectos);
        sb.append('}');
        sb.append(System.lineSeparator());
        return sb.toString();
    }

    public int getCodigo_cli() {
        return codigo_cli;
    }

    public void setCodigo_cli(int codigo_cli) {
        this.codigo_cli = codigo_cli;
    }

    public String getNombre_cli() {
        return nombre_cli;
    }

    public void setNombre_cli(String nombre_cli) {
        this.nombre_cli = nombre_cli;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public List<Proyecto> getProyectos() {
        return proyectos;
    }

    public void setProyectos(List<Proyecto> proyectos) {
        this.proyectos = proyectos;
    }
}
