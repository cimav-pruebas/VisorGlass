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
package cimav.visorglass.client;

import cimav.visorglass.shared.data.Arbol;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import java.util.List;
import cimav.visorglass.shared.data.Depto;
import cimav.visorglass.shared.data.TipoDocumento;
import cimav.visorglass.shared.data.Usuario;

/**
 *
 * @author juan.calderon
 */
@RemoteServiceRelativePath("gwtservice")
public interface GWTService extends RemoteService {

    Usuario loginProfile(String tokenAutorizado);	

    Arbol cargarArbol(Usuario usuario);
    
    List<Depto> cargarPermisosDeptos();
    List<TipoDocumento> cargarTipos();
}
