/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cimav.visorglass.shared.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author juan.calderon
 */
public class Arbol implements Serializable {
    
    private Usuario usuario;
    private List<Depto> deptos;

    public Arbol() {
        this.usuario = new Usuario();
        this.deptos = new ArrayList<Depto>();
    }
    
    public Arbol(Usuario usuario, List<Depto> deptos) {
        this.usuario = usuario;

        // ordenar deptos
        if (deptos != null) {
            Collections.sort(deptos, new Comparator<Depto>() {
                @Override
                public int compare(final Depto d1, final Depto d2) {
                    return d1.getCodigo().compareTo(d2.getCodigo());
                }
            });
        }
        
        this.deptos = deptos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Depto> getDeptos() {
        return deptos;
    }

    public void setDeptos(List<Depto> deptos) {
        this.deptos = deptos;
    }
    
}
