/*
 * Copyright 2013 juan.calderon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cimav.visorglass.shared.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juan.calderon
 */
public class Documento implements Serializable {
    
    private String codigoDepto;
    private String codigoTipo; 
    private String codigo; // de acuerdo a CPIC
    private String nombre; // del archivo (sin codigo)
    private String formato; // Pdf, Xls, Doc, Ppt, Txt, etc.
    private String contenido; // Contenido textual
    private String codigosReferenciados; 
    private List<String> fragmentos; // fragmentos del highlithing
    
    private int num; //simple consecutivo
    
    public Documento() {
        codigo = "";
        nombre = "";
        num = -1;
    }

    public Documento(String codigoDepto, String codigoTipo, String codigo, String nombre, String contenido, String codigosReferenciados) {
        this.codigoDepto = codigoDepto;
        this.codigoTipo = codigoTipo;
        this.codigo = codigo;
        this.nombre = nombre;
        this.contenido = contenido;
        this.codigosReferenciados = codigosReferenciados;
    }
    
    public String getFullUrl(String servidorArchivosVigentes) {
        return servidorArchivosVigentes + "/" + this.codigoDepto + "/" + this.codigoTipo + "/" + getLongNombre();
    }
    
    public String getShortPath() {
        return this.codigoDepto + "/" + this.codigoTipo + "/";
    }
    
    public String getLongNombre() {
        //return codigo.trim() + " " + nombre.trim();
        // no se deben eliminar los espacios que ya tienen el codigo y el nombre
        return codigo + " " + nombre;
    }
    
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getCodigoDepto() {
        return codigoDepto;
    }

    public void setCodigoDepto(String codigoDepto) {
        this.codigoDepto = codigoDepto;
    }

    public String getCodigoTipo() {
        return codigoTipo;
    }

    public void setCodigoTipo(String codigoTipo) {
        this.codigoTipo = codigoTipo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getCodigosReferenciados() {
        return codigosReferenciados;
    }

    public void setCodigosReferenciados(String codigosReferenciados) {
        this.codigosReferenciados = codigosReferenciados;
    }

    public List<String> getFragmentos() {
        if (this.fragmentos == null) {
            this.fragmentos = new ArrayList<String>();
        }
        return fragmentos;
    }

    public void setFragmentos(List<String> fragmentos) {
        this.fragmentos = fragmentos;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return codigoDepto + " " + codigoTipo + " " + codigo + " " + nombre;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.nombre != null ? this.nombre.hashCode() : 0);
        hash = 47 * hash + (this.codigoDepto != null ? this.codigoDepto.hashCode() : 0);
        hash = 47 * hash + (this.codigoTipo != null ? this.codigoTipo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Documento other = (Documento) obj;
        if ((this.nombre == null) ? (other.nombre != null) : !this.nombre.equals(other.nombre)) {
            return false;
        }
        if ((this.codigoDepto == null) ? (other.codigoDepto != null) : !this.codigoDepto.equals(other.codigoDepto)) {
            return false;
        }
        if ((this.codigoTipo == null) ? (other.codigoTipo != null) : !this.codigoTipo.equals(other.codigoTipo)) {
            return false;
        }
        return true;
    }

    
}
