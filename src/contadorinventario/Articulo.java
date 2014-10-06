/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contadorinventario;

/**
 *
 * @author maestro
 */
public class Articulo {
    public String codigo;
    public String descripcion;
    public String costo;
    public String venta;
    public String mayoreo;
    public String existencia;
    public String minimo;
    public String departamento;

    public Articulo() {
    }

    public Articulo(String codigo, String descripcion, String costo, String venta, String mayoreo, String existencia, String minimo, String departamento) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.costo = costo;
        this.venta = venta;
        this.mayoreo = mayoreo;
        this.existencia = existencia;
        this.minimo = minimo;
        this.departamento = departamento;
    }

    @Override
    public String toString() {
        return "Articulo{" + "codigo=" + codigo + ", descripcion=" + descripcion + ", costo=" + costo + ", venta=" + venta + ", mayoreo=" + mayoreo + ", existencia=" + existencia + ", minimo=" + minimo + ", departamento=" + departamento + '}';
    }
    
    
    
    
}
