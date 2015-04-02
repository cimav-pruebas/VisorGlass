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
public class Depto implements Serializable {
    
    private String codigo;
    private String nombre;
    private List<TipoDocumento> tipos;
    private List<String> cuentas;

    public Depto() {
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

    public List<TipoDocumento> getTipos() {
        if (tipos == null) {
            tipos= new ArrayList<TipoDocumento>();
        }
        return tipos;
    }

    public void setTipos(List<TipoDocumento> tipos) {
        this.tipos = tipos;
    }

    public List<String> getCuentas() {
        if (cuentas == null) {
            cuentas = new ArrayList<String>();
        }
        return cuentas;
    }

    public void setCuentas(List<String> cuentas) {
        this.cuentas = cuentas;
    }
    
    
    
}
