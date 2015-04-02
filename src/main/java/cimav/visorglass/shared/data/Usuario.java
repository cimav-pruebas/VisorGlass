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

import cimav.visorglass.shared.Constantes;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juan.calderon
 */
public class Usuario implements Serializable {
    
    private boolean loggedIn;
    private String email;
    private String nombre;
    private String pictureUrl;
    private List<Depto> deptos;
    private boolean esAdministrador;

    private String servidorArchivosVigentes;
    
    public Usuario() {
        // De inicio, no esta logeado
        this.loggedIn = false;
        this.email = "";
        this.nombre = Constantes.MSG_USUARIO_NO_LOGEADO;
        this.pictureUrl = Constantes.ICON_USUARIO_SIN_FOTO;
        this.esAdministrador = false;
        
        this.servidorArchivosVigentes = "NONE";
    }

    public String getCuenta() {
        int idx = email.indexOf("@");
        String cuenta = email.substring(0, idx);
        return cuenta;
    }
    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        try {
            byte[] utf8 = nombre.getBytes();
            this.nombre = new String(utf8, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public List<Depto> getDeptos() {
        if (deptos == null) {
            deptos = new ArrayList<Depto>();
        }
        return deptos;
    }

    public void setDeptos(List<Depto> deptos) {
        this.deptos = deptos;
    }

    public boolean isEsAdministrador() {
        return esAdministrador;
    }

    public void setEsAdministrador(boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
    }
    
    public String getServidorArchivosVigentes() {
        return this.servidorArchivosVigentes;
    }

    public void setServidorArchivosVigentes(String servidorArchivosVigentes) {
        this.servidorArchivosVigentes = servidorArchivosVigentes;
    }
}
