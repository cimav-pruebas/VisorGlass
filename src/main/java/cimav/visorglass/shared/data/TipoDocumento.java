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
import java.util.List;

/**
 *
 * @author juan.calderon
 */
public class TipoDocumento implements Serializable {
    
    private String codigo;
    private String nombre;
    private String codigoDepto; //usado para diferenciarlo de Tipos iguales en otros Deptos.

    private List<Documento> docs;
    
    public TipoDocumento() {
        this.codigoDepto = "_N_O_N_E_";
    }

    public TipoDocumento(String codigoDepto) {
        this.codigoDepto = codigoDepto;
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

    public String getCodigoDepto() {
        return codigoDepto;
    }

    public void setCodigoDepto(String codigoDepto) {
        this.codigoDepto = codigoDepto;
    }

    public List<Documento> getDocs() {
        return docs;
    }

    public void setDocs(List<Documento> docs) {
        this.docs = docs;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.codigo != null ? this.codigo.hashCode() : 0);
        hash = 89 * hash + (this.codigoDepto != null ? this.codigoDepto.hashCode() : 0);
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
        final TipoDocumento other = (TipoDocumento) obj;
        if ((this.codigo == null) ? (other.codigo != null) : !this.codigo.equals(other.codigo)) {
            return false;
        }
        if ((this.codigoDepto == null) ? (other.codigoDepto != null) : !this.codigoDepto.equals(other.codigoDepto)) {
            return false;
        }
        return true;
    }

    
    
}
