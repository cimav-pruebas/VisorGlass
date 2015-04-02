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
package cimav.visorglass.client.widgets;

import com.google.api.gwt.oauth2.client.Auth;
import com.google.api.gwt.oauth2.client.AuthRequest;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.ArrayList;
import java.util.List;
import cimav.visorglass.client.GWTServiceAsync;
import cimav.visorglass.client.utils.Ajax;
import cimav.visorglass.shared.Constantes;
import cimav.visorglass.shared.data.Arbol;
import cimav.visorglass.shared.data.Depto;
import cimav.visorglass.shared.data.Documento;
import cimav.visorglass.shared.data.TipoDocumento;
import cimav.visorglass.shared.data.Usuario;
import com.google.gwt.dom.client.Style;
import java.util.Collections;
import java.util.Comparator;

/**
 * Crea el Layout Principal.
 * Maneja el login y logout.
 * Carga la mega-estructura: {@link Usuario}-{@link Depto}s-{@link TipoDocumento}s-{@link Documento}s
 * 
 * @author juan.calderon
 */
public class MainLayout extends Composite {

    private static MainLayoutUiBinder uiBinder = GWT.create(MainLayoutUiBinder.class);

    interface MainLayoutUiBinder extends UiBinder<Widget, MainLayout> {
    }
    // constants for OAuth2 (don't forget to update GOOGLE_CLIENT_ID  -  http://code.google.com/apis/console)
    private static final Auth AUTH = Auth.get();
    private static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    private static final String GOOGLE_CLIENT_ID = "411158167495.apps.googleusercontent.com";
    // The auth scope being requested. This scope will allow the application to identify who the authenticated user is.
    private static final String USER_INFO_PROFILE_SCOPE = "https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email";
    
    private GWTServiceAsync gwtServiceAsync;
    
    @UiField
    public Label loginLabel;
    @UiField
    public Image loginImage;
    @UiField
    public Image loginSignImage;
    @UiField
    public FlowPanel centralFlowPanel;

    private final PanelesLayout panelesLayout;

    private Usuario usuario;
    private List<String> administradores;
    private List<Depto> departamentos;
    private List<TipoDocumento> tipos;
    
    public MainLayout(GWTServiceAsync gwtServiceAsync) {
        
        // crea el layout principal
        initWidget(uiBinder.createAndBindUi(this));
        
        // crea el servicio
        this.gwtServiceAsync = gwtServiceAsync;
        
        // crea al Usuario (vacío y no logeado)
        this.usuario = new Usuario();
        
        // crear estucturas de Deptos y Tipos
        this.departamentos = new ArrayList<Depto>();
        this.getDepartamentos();
        this.tipos = new ArrayList<TipoDocumento>();
        this.getTipoDocumentos();
        
        // Listener para Sing In/Out !important
        this.loginSignImage.addClickHandler(new LogInOutClickHandler());
        this.loginLabel.addClickHandler(new LogInOutClickHandler());
        this.loginSignImage.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        this.loginLabel.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        
        // Listener para cargar imagen
        this.loginImage.addLoadHandler(new LgoinImageLoadHandler());
        
        // Crea los 3 Paneles
        this.panelesLayout = new PanelesLayout(gwtServiceAsync);
        
    }

    @Override
    protected void onLoad() {
        super.onLoad(); 
        
        // Agrega los 3 paneles
        this.centralFlowPanel.add(this.panelesLayout);
        
        // Listener de seleccion del Arbol
        this.panelesLayout.getArbolModel().getSelectionModel().addSelectionChangeHandler(new ArbolSelectionHandler());

    }

    
    
    private class LogInOutClickHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent event) {
            if (usuario.isLoggedIn()) {
                loginOut();
            } else {
                loginIn();
            }
        }
    }
    
    private void cargaArbol() {
        // Si ya existe, no recargarla
        gwtServiceAsync.cargarArbol(usuario, Ajax.call(new AsyncCallback<Arbol>() {
            @Override
            public void onFailure(Throwable caught) {
                GWT.log("cargarEstructura -> onFailure");
            }

            @Override
            public void onSuccess(Arbol result) {
                for(Depto depto : result.getDeptos()) {
                    for(TipoDocumento tipo : depto.getTipos()) {
                        if (tipo.getNombre() == null || tipo.getNombre().trim().isEmpty()) {
                            String nom = "NO HAY COINCIDENCIA";
                            for(TipoDocumento tipoTxt : tipos) {
                                if (tipo.getCodigo().equals(tipoTxt.getCodigo())) {
                                    nom = tipoTxt.getNombre();
                                    break;
                                }
                            }
                            tipo.setNombre(nom);
                        }
                    }
                }
                
                //printEstructura(usuario.getDeptos());
                
                panelesLayout.cargarDatos(result);
            }
        }));
    }
    
    private List<Depto> getDepartamentos() {
        if (departamentos.isEmpty()) {
            // Si ya existe, no recargarla
            gwtServiceAsync.cargarPermisosDeptos(Ajax.call(new AsyncCallback<List<Depto>>() {
                @Override
                public void onFailure(Throwable caught) {
                    GWT.log("cargarDeptos -> onFailure");
                }

                @Override
                public void onSuccess(List<Depto> result) {
                    departamentos.clear();
                    departamentos.addAll(result);
                }
            }));
        }
        return departamentos;
    }
    
    private List<TipoDocumento> getTipoDocumentos() {
        if (tipos.isEmpty()) {
            // Si ya existe, no recargarla
            gwtServiceAsync.cargarTipos(Ajax.call(new AsyncCallback<List<TipoDocumento>>() {
                @Override
                public void onFailure(Throwable caught) {
                    GWT.log("cargarTipos -> onFailure");
                }

                @Override
                public void onSuccess(List<TipoDocumento> result) {
                    tipos.clear();
                    tipos.addAll(result);
                }
            }));
        }
        return tipos;
    }
    
    private void printEstructura(List<Depto> deptos) {
        System.out.println("USER: " + deptos.size());
        for (Depto depto : deptos) {
            System.out.println("+++++DEPTO: " + depto.getCodigo() + " :: " + depto.getNombre()+ "  > " + depto.getTipos().size());
            for (TipoDocumento tipo : depto.getTipos()) {
                System.out.println("     -----Tipo: " + tipo.getCodigo() + " :: " + tipo.getNombre());
            }
        }
    }

    public void loginIn() {
        
        final AuthRequest req = new AuthRequest(GOOGLE_AUTH_URL, GOOGLE_CLIENT_ID).withScopes(USER_INFO_PROFILE_SCOPE);
        
        // Calling login() will display a popup to the user the first time it is
        // called. Once the user has granted access to the application,
        // subsequent calls to login() will not display the popup, and will
        // immediately result in the callback being given the token to use.
        
        /* Esta es la llamada al Login */

        AUTH.login(req, new Callback<String, Throwable>() {
            @Override
            public void onSuccess(final String tokenAutorizacion) {
                // Si se logea, regresa un Token Autorizado
                if (!tokenAutorizacion.trim().isEmpty()) { //TODO simpre considera que no tiene token
                    // Si no tiene AUN el token autorizado, busca la autorización
                    gwtServiceAsync.loginProfile(tokenAutorizacion, Ajax.call(new AsyncCallback<Usuario>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            // Falló la autorización
                            GWT.log("login.autorizacion -> onFailure");
                            
                            // se Deslogea
                            loginOut();
                        }
                        @Override
                        public void onSuccess(Usuario usuarioSrv) {
                            // Paso la autorización
                            
                            // asigna el usuario logeado del lado del servidor al usuario del lado del cliente
                            usuario = usuarioSrv;
                            
                            // Si tiene nombre es que se pudo logear.
                            usuario.setLoggedIn(usuario.getNombre() != null && !usuario.getNombre().trim().isEmpty());

                            // Actulizar componentes de Login
                            loginImage.setVisible(true);
                            loginImage.setUrl(usuario.getPictureUrl());
                            loginLabel.setText(usuario.getNombre());
                            loginSignImage.setTitle("Salir");
                            loginSignImage.setUrl(Constantes.ICON_SIGN_OUT);                            
                            
                            // Muestra toda el Area Central
                            centralFlowPanel.setVisible(true);
                            
                            // Carga la Estructura del Usuario
                            // TODO la re-carga no debería ir aqui. Falta revisar todo lo referente al re-login, re-authentication y re-carga.
                            // Al usuario logeado le cargo los Deptos-Permiso a lo que tiene derecho
                            // Al logearse
                            usuario.setDeptos(cargarDepartamentosUsuario());
                            
                            // Ahora, basado en los Departamentos del Usuario, le cargo sus Tipos y Documentos en el Arbol
                            cargaArbol();
                            
                            panelesLayout.setServidorArchivosVigentes(usuario.getServidorArchivosVigentes());
                        }
                    }));
                    
                }
                
            }
            @Override
            public void onFailure(Throwable caught) {
                // No se logeo
                GWT.log("login -> onFailure");
                
                // se Deslogea
                loginOut();
            }
        });
        
    }

    private List<Depto> cargarDepartamentosUsuario() {
        List<Depto> result = new ArrayList<Depto>();
        for (Depto depto : departamentos) {
            if (usuario.isEsAdministrador() || esDeptoDelUsuario(depto)) {
                result.add(depto);
            }
        }
        return result;
    }
    private boolean esDeptoDelUsuario(Depto depto) {
        // Regresa true si el Usuario pertenece al Depto o la cuenta es TODOS
        boolean result = false;
        
        for (String cuenta : depto.getCuentas()) {
            if ("publico".equals(cuenta.toLowerCase()) || cuenta.equals(usuario.getCuenta())) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    private class LgoinImageLoadHandler implements LoadHandler {

        @Override
        public void onLoad(LoadEvent event) {
            final int newWidth = 32;
            final com.google.gwt.dom.client.Element element = event.getRelativeElement();
            if (element.equals(loginImage.getElement())) {
                final int originalHeight = loginImage.getOffsetHeight();
                final int originalWidth = loginImage.getOffsetWidth();
                if (originalHeight > originalWidth) {
                    loginImage.setHeight(newWidth + "px");
                } else {
                    loginImage.setWidth(newWidth + "px");
                }

            }
        }
        
    }
    
    public void loginOut() {
        // Borra todas las cuentas logeadas de esta clase
        Auth.get().clearAllTokens(); 
        
        // Descarga al usuario. Lo vacia y lo pone en No-logeado.
        usuario = new Usuario();
        Arbol arbol = new Arbol(usuario, new ArrayList<Depto>());
        // vacia el Arbol
        panelesLayout.cargarDatos(arbol);
        
        // vacia los documentos
        arbolSelectedObject = null;
        cargarDocumentos();
        
        loginImage.setVisible(false);
        loginLabel.setText(usuario.getNombre());
        loginSignImage.setTitle("Firmarse");
        loginSignImage.setUrl(Constantes.ICON_SIGN_IN);
        
        // Esconde toda el area central 
        centralFlowPanel.setVisible(false);
    }
    
    private class ArbolSelectionHandler implements SelectionChangeEvent.Handler {
        /*  Al seleccionar Item del Arbol */
        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            if (event.getSource() instanceof SingleSelectionModel) {
                SingleSelectionModel selModel = (SingleSelectionModel) event.getSource();
                if (selModel.getSelectedObject() == null) {
                    // Limpiar seleccion
                    arbolSelectedObject = null;
                } else {
                    arbolSelectedObject = selModel.getSelectedObject();
                }
                
                cargarDocumentos();
            }
        }
    }

    private Object arbolSelectedObject;

    private void cargarDocumentos() {
        
        List<Documento> documentos = new ArrayList<>();
        
        String deptos = "RXRXRX"; // <-- No existente
        String tipo = "";
        
        if (arbolSelectedObject != null) {
            if (arbolSelectedObject instanceof Arbol) {
                for (Depto depto : usuario.getDeptos()) {
                    deptos = deptos + " " + depto.getCodigo(); // Todos los Departamentos (todos los Tipos)
                }
            } else if (arbolSelectedObject instanceof Depto) {
                Depto depto = (Depto) arbolSelectedObject;
                deptos = depto.getCodigo(); // Un Departamento
            } else if (arbolSelectedObject instanceof TipoDocumento) {
                TipoDocumento tipoDoc = (TipoDocumento) arbolSelectedObject;
                deptos = tipoDoc.getCodigoDepto(); // (depto padre del tipo seleccionado)
                tipo = tipoDoc.getCodigo(); // tipo seleccionado
                
                documentos.addAll(tipoDoc.getDocs());                
            }
        }
        
        Collections.sort(documentos, new Comparator<Documento>() {
            @Override
            public int compare(Documento doc1, Documento doc2) {
                return doc1.getCodigo().compareTo(doc2.getCodigo());
            }
        });
        
        for(int i=0; i<documentos.size(); i++) {
            documentos.get(i).setNum(i);
        }
        
        panelesLayout.documentosCellList.setRowCount(documentos.size(), true);
        panelesLayout.documentosCellList.setRowData(0, documentos);
        panelesLayout.documentosCellList.redraw();
        
        // Num de documentos encontrados
        String nDocs = documentos.size() < 500 ? "Documentos (" + documentos.size() + ")" : "Documentos (MAXIMO 500 DOCS)";
        panelesLayout.documentosLabel.setText(nDocs);
    }
    
    
}