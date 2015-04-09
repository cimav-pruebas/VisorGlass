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

import cimav.visorglass.client.GWTServiceAsync;
import cimav.visorglass.client.widgets.model.ArbolModel;
import cimav.visorglass.client.widgets.resources.ICellListResources;
import cimav.visorglass.client.widgets.resources.ICellTreeMessages;
import cimav.visorglass.client.widgets.resources.ICellTreeResources;
import cimav.visorglass.shared.Constantes;
import cimav.visorglass.shared.data.Arbol;
import cimav.visorglass.shared.data.Documento;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.constants.IconSize;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.Date;

/**
 *
 * @author juan.calderon
 */
public class PanelesLayout extends Composite {

    private static PanelesLayoutUiBinder uiBinder = GWT.create(PanelesLayoutUiBinder.class);

    interface PanelesLayoutUiBinder extends UiBinder<Widget, PanelesLayout> {
    }
    
    // Ctes 
    private static final int IDX_ARBOL_PANEL = 0; // West
    private static final int IDX_DOCUMENTOS_PANEL = 4; 
    private static final int IDX_VISOR_PANEL = 2; 
    
    // Ctes Cookies
    private static String SGC_COOKIE_WIDTH_ARBOL_PANEL = "sgcWidthArbol";
    private static String SGC_COOKIE_WIDTH_VISOR_PANEL = "sgcWidthVisor";
    private static String SGC_COOKIE_ACTIVE_ARBOL_PANEL = "sgcCookieActiveArbol";
    private static String SGC_COOKIE_ACTIVE_DOCUMENTOS_PANEL = "sgcCookieActiveDocumentos";
    private static String SGC_COOKIE_ACTIVE_VISOR_PANEL = "sgcCookieActiveVisor";
    private static String SGC_COOKIE_DOCUMENTOS_ORDENADOS = "sgcDocumentosOrdenados";
    
    @UiField
    public Button btnArbol;
    @UiField
    public Button btnDocumentos;
    @UiField
    public Button btnVisor;
    @UiField
    public SplitLayoutPanel splitLayoutPanel;
    @UiField
    public FlowPanel flowPanelDecoratorArbol;
    @UiField
    public FlowPanel flowPanelArbol;
    @UiField
    public ScrollPanel scrollPanelArbol;
//    @UiField
//    public FlowPanel flowPanelDocumentos;
    @UiField
    public FlowPanel flowPanelDecoratorDocumentos;
    @UiField
    public ScrollPanel scrollPanelDocumentos;
    @UiField
    //public HTMLPanel htmlPanelDocViewer;
//    public TabLayoutPanel tabLayoutPanelVisor;
    public FlowPanel flowPanelDecoratorVisor;
    @UiField 
    public Frame frameDocViewer;
    // El incono de reload
    private Icon reloadIcon;
    // Arbol
    private ArbolModel arbolModel;
    private CellTree cellArbol;
    // CellList Documentos
    public CellList<Documento> documentosCellList;
    public SingleSelectionModel<Documento> documentoSelectionModel;
    // Documentos Label
    public Label documentosLabel;
    
    // Variables
    private static String urlGoogleDoc;
    
    private GWTServiceAsync gwtServiceAsync;
    
    private String servidorArchivosVigentes;;
    
//    private Widget widgetPanelDocumentos;
//    private Widget widgetPanelVisor;
    
    public PanelesLayout(GWTServiceAsync gwtServiceAsync) {
        initWidget(uiBinder.createAndBindUi(this));
        
        this.gwtServiceAsync = gwtServiceAsync;
        
        // Crea el Modelo
        arbolModel = new ArbolModel();
        // Crea el node Root
        TreeItem root = new TreeItem(SafeHtmlUtils.fromString("<h2>La Root</h2>"));
        // Cambia  Recursos del Arbol
        CellTree.Resources cellTreeResources = GWT.create(ICellTreeResources.class);
        CellTree.CellTreeMessages cellTreeMsgs = GWT.<CellTree.CellTreeMessages>create(ICellTreeMessages.class);
        // Crea el arbol
        cellArbol = new CellTree(arbolModel, root, cellTreeResources, cellTreeMsgs);
        cellArbol.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);
        // agrega el arbol a su Panel
        //flowPanelArbol.add(cellArbol);
        cellArbol.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        cellArbol.getElement().getStyle().setTop(10, Style.Unit.PX);
        cellArbol.getElement().getStyle().setLeft(0, Style.Unit.PX);
        cellArbol.getElement().getStyle().setBottom(0, Style.Unit.PX);
        cellArbol.getElement().getStyle().setRight(0, Style.Unit.PX);
        scrollPanelArbol.add(cellArbol);
        /* Inyectarle style absolute al Abuelo para que funcione el scroll del cellArbol */
        Element divAbue2 = cellArbol.getElement().getParentElement().getParentElement();
        divAbue2.getStyle().setPosition(Style.Position.ABSOLUTE);
        divAbue2.getStyle().setTop(0, Style.Unit.PX);
        divAbue2.getStyle().setLeft(0, Style.Unit.PX);
        divAbue2.getStyle().setBottom(0, Style.Unit.PX);
        divAbue2.getStyle().setRight(0, Style.Unit.PX);

        // Crea CellList de Documentos, sus resources, su Celda, su SelectionModel y su Listener.
        documentoSelectionModel = new SingleSelectionModel<Documento>();
        CellList.Resources cellListResources = GWT.create(ICellListResources.class);
        documentosCellList = new CellList<Documento>(new DocumentoCell(documentoSelectionModel), cellListResources);        
        documentosCellList.setKeyboardSelectionPolicy(HasKeyboardSelectionPolicy.KeyboardSelectionPolicy.ENABLED);
        documentosCellList.setSelectionModel(documentoSelectionModel);
        documentosCellList.setPageSize(500);
        // listener
        documentoSelectionModel.addSelectionChangeHandler(new DocumentoSelectionHandler());
        // Style absolute
        documentosCellList.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        documentosCellList.getElement().getStyle().setTop(0, Style.Unit.PX);
        documentosCellList.getElement().getStyle().setLeft(0, Style.Unit.PX);
        documentosCellList.getElement().getStyle().setBottom(0, Style.Unit.PX);
        documentosCellList.getElement().getStyle().setRight(0, Style.Unit.PX);
        // Agregarlo a su panel
        scrollPanelDocumentos.add(documentosCellList);
        
        /* Inyectarle style absolute al Abuelo para que funcione el scroll del cellList */
        Element divAbue = documentosCellList.getElement().getParentElement().getParentElement();
        divAbue.getStyle().setPosition(Style.Position.ABSOLUTE);
        divAbue.getStyle().setTop(0, Style.Unit.PX);
        divAbue.getStyle().setLeft(0, Style.Unit.PX);
        divAbue.getStyle().setBottom(0, Style.Unit.PX);
        divAbue.getStyle().setRight(0, Style.Unit.PX);
        
        // Listener de los Botones de Paneles
        btnArbol.addClickHandler(new BtnArbolClickHandler());
        btnDocumentos.addClickHandler(new BtnDocumentosClickHandler());
        btnVisor.addClickHandler(new BtnVisorClickHandler());

        // Arega etiquetas flotantes y sus listeners
        Label arbolLabel = new Label("Arbol");
        arbolLabel.setStyleName("jsfiddle_label");
        arbolLabel.addMouseOverHandler(new JSFiddlerMouseOver());
        arbolLabel.addMouseOutHandler(new JSFiddlerMouseOut());
        flowPanelArbol.add(arbolLabel);
        documentosLabel = new Label("Documentos");
        documentosLabel.setStyleName("jsfiddle_label");
        documentosLabel.addMouseOverHandler(new JSFiddlerMouseOver());
        documentosLabel.addMouseOutHandler(new JSFiddlerMouseOut());
        flowPanelDecoratorDocumentos.add(documentosLabel);
        Label visorLabel = new Label("Visor");
        visorLabel.setStyleName("jsfiddle_label");
        visorLabel.addMouseOverHandler(new JSFiddlerMouseOver());
        visorLabel.addMouseOutHandler(new JSFiddlerMouseOut());
        flowPanelDecoratorVisor.add(visorLabel);
        
        frameDocViewer.addLoadHandler(new FrameLoadHandler());
        
        reloadIcon = new Icon(IconType.ROTATE_RIGHT); 
        reloadIcon.setSize(IconSize.LARGE);
        reloadIcon.setMuted(false);
        reloadIcon.setSpin(false);
        reloadIcon.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        reloadIcon.getElement().getStyle().setTop(3, Style.Unit.PX);
        reloadIcon.getElement().getStyle().setLeft(3, Style.Unit.PX);
        reloadIcon.getElement().getStyle().setColor("gray");
        reloadIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        reloadIcon.addDomHandler(new ReloadGoogleDocMouseDownHandler(), MouseDownEvent.getType());
//        flowPanelDecoratorVisor.add(reloadIcon);
        
        // NOTE Quitar documentos 
        btnDocumentos.setVisible(false);
        
    }
    
    public ArbolModel getArbolModel() {
        return this.arbolModel;
    }
    
    public void cargarDatos(Arbol arbol) {
        arbolModel.setDataProvider(arbol);
        arbolModel.expandTreeNode(cellArbol.getRootTreeNode());
    }

    @Override
    protected void onLoad() {
        super.onLoad();

//        widgetPanelDocumentos = splitLayoutPanel.getWidget(IDX_DOCUMENTOS_PANEL);
//        widgetPanelVisor = splitLayoutPanel.getWidget(IDX_VISOR_PANEL);
        
        // Carga cookies

        String cookieActiveArbolPanel = Cookies.getCookie(SGC_COOKIE_ACTIVE_ARBOL_PANEL);
        cookieActiveArbolPanel = cookieActiveArbolPanel == null ? "true" : cookieActiveArbolPanel; // nulos son true
        boolean valor = cookieActiveArbolPanel.toLowerCase().equals("true") ? true : false;
        btnArbol.setActive(valor);
        // ojo: Active y Hidden tienen efectos contrarios
        splitLayoutPanel.setWidgetHidden(splitLayoutPanel.getWidget(IDX_ARBOL_PANEL), !valor);

        String cookieActiveDocumentos = Cookies.getCookie(SGC_COOKIE_ACTIVE_DOCUMENTOS_PANEL);
        cookieActiveDocumentos = cookieActiveDocumentos == null ? "true" : cookieActiveDocumentos; // nulos son true
        valor = cookieActiveDocumentos.toLowerCase().equals("true") ? true : false;
        btnDocumentos.setActive(valor);
        splitLayoutPanel.setWidgetHidden(splitLayoutPanel.getWidget(IDX_DOCUMENTOS_PANEL), !valor);

        String cookieActiveVisor = Cookies.getCookie(SGC_COOKIE_ACTIVE_VISOR_PANEL);
        cookieActiveVisor = cookieActiveVisor == null ? "true" : cookieActiveVisor; // nulos son true
        valor = cookieActiveVisor.toLowerCase().equals("true") ? true : false;
        btnVisor.setActive(valor);
        splitLayoutPanel.setWidgetHidden(splitLayoutPanel.getWidget(IDX_VISOR_PANEL), !valor);

        String cookieWidthArbolPanel = Cookies.getCookie(SGC_COOKIE_WIDTH_ARBOL_PANEL);
        String cookieWidthVisorPanel = Cookies.getCookie(SGC_COOKIE_WIDTH_VISOR_PANEL);
        int widthArbolPanel;
        int widthVisorPanel;
        try {
            widthArbolPanel = Integer.valueOf(cookieWidthArbolPanel);
            widthVisorPanel = Integer.valueOf(cookieWidthVisorPanel);
        } catch (Exception e) {
            widthArbolPanel = 360; // Default
            widthVisorPanel = 360; // Default
        }
        splitLayoutPanel.setWidgetSize(flowPanelDecoratorArbol, Integer.valueOf(widthArbolPanel));
        splitLayoutPanel.setWidgetSize(flowPanelDecoratorVisor, Integer.valueOf(widthVisorPanel));

    }

    @Override
    protected void onUnload() {
        super.onUnload();

        // Guardar Cookies

        {
            String valor = "" + btnArbol.isActive();
            Cookies.setCookie(SGC_COOKIE_ACTIVE_ARBOL_PANEL, valor, expira());

            valor = "" + btnDocumentos.isActive();
            Cookies.setCookie(SGC_COOKIE_ACTIVE_DOCUMENTOS_PANEL, valor, expira());

            valor = "" + btnVisor.isActive();
            Cookies.setCookie(SGC_COOKIE_ACTIVE_VISOR_PANEL, valor, expira());

            Double widthArbol = splitLayoutPanel.getWidgetSize(flowPanelDecoratorArbol);
            Double widthVisor = splitLayoutPanel.getWidgetSize(flowPanelDecoratorVisor);
            Cookies.setCookie(SGC_COOKIE_WIDTH_ARBOL_PANEL, String.valueOf(Math.round(widthArbol)), expira());
            Cookies.setCookie(SGC_COOKIE_WIDTH_VISOR_PANEL, String.valueOf(Math.round(widthVisor)), expira());
        }
    }

    private Date expira() {
        return new Date(2030, 10, 10);
    }

    private class BtnArbolClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            // llega en el edo nuevo
            boolean es = btnArbol.isActive();
            splitLayoutPanel.setWidgetHidden(splitLayoutPanel.getWidget(IDX_ARBOL_PANEL), es);
        }
    }

    private class BtnDocumentosClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            boolean visible = btnDocumentos.isActive();
            splitLayoutPanel.setWidgetHidden(splitLayoutPanel.getWidget(IDX_DOCUMENTOS_PANEL), visible);
        }
    }

    private class BtnVisorClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {
            boolean visible = btnVisor.isActive();
            splitLayoutPanel.setWidgetHidden(splitLayoutPanel.getWidget(IDX_VISOR_PANEL), visible);
        }
    }

    private class DocumentoCell extends AbstractCell<Documento> {
        
        private SingleSelectionModel<Documento> docSelectionModel;
        
        DocumentoCell(SingleSelectionModel<Documento> docSelectionModel) {
            this.docSelectionModel = docSelectionModel;
        }
        
        @Override
        public void render(Cell.Context context, Documento value, SafeHtmlBuilder sb) {
            if (value == null) {
                return;
            }
            boolean isSelected = this.docSelectionModel != null && this.docSelectionModel.getSelectedObject() != null
                    && this.docSelectionModel.getSelectedObject().equals(value);
            String iconTag = "<i class='fa ";
            iconTag = isSelected
                    ? iconTag + Constantes.ICON_DOCUMENTO_SELECTED + " fa-3x' style='color:white'></i>"
                    : iconTag + Constantes.ICON_DOCUMENTO + " fa-3x' style='color:#B8B8B8'></i>";
            
            String html =
                    "<table class='tableDoc' cellspacing='0' cellpadding='0'> " + 
                    "   <tr> " +
                    "       <td class='iconClass' rowspan='3' valign='top'>THE_ICON_TAG</td> " +
                    "       <td class='nombreClass' colspan='2' align='left'>NOMBRE_TAG</td> " +
                    "   </tr> " +
                    "   <tr> " +
                    "       <td aling='left'>RUTA_TAG</td> " +
                    "   </tr> " +
                    "   <tr> " +
                    "       <td aling='left'>FRAGMENTOS_TAG</td> " +
                    "   </tr> " +
                    "</table> ";

            String rutaBreadCrumb = 
                    "<ul id='breadcrumbs-ruta'>" +
                    "   <li><spam>DEPTO_TAG</spam></li>" +
                    "	<li><spam>TIPO_TAG</spam></li>" +
                    "   <li><spam>CODIGO_TAG</spam></li>" +
                    "</ul>";
            rutaBreadCrumb = rutaBreadCrumb.replace("CODIGO_TAG", value.getCodigo());
            rutaBreadCrumb = rutaBreadCrumb.replace("DEPTO_TAG", value.getCodigoDepto());
            rutaBreadCrumb = rutaBreadCrumb.replace("TIPO_TAG", value.getCodigoTipo());
            
            html = html.replace("THE_ICON_TAG", iconTag);
//            html = html.replace("CODIGO_TAG", value.getCodigo());
//            html = html.replace("NOMBRE_TAG", value.getNum() + ") " + value.getNombre());
            html = html.replace("NOMBRE_TAG", value.getNombre());
            html = html.replace("RUTA_TAG", rutaBreadCrumb);
            
            String fragmentos = "<p class='fragmentosClass'>";
            for(String frag :  value.getFragmentos()) {
                fragmentos = fragmentos + frag + "... ";
            }
            fragmentos = fragmentos + "</p>";
            html = html.replace("FRAGMENTOS_TAG",  fragmentos);

            sb.appendHtmlConstant(html);
        }
    }
    
    private class DocumentoSelectionHandler implements SelectionChangeEvent.Handler {
        @Override
        public void onSelectionChange(SelectionChangeEvent event) {
            if (event.getSource() instanceof SingleSelectionModel) {
                SingleSelectionModel selModel = (SingleSelectionModel) event.getSource();
                if (selModel.getSelectedObject() == null) {
                    // Limpiar seleccion
                } else {
                    if (selModel.getSelectedObject() instanceof Documento) {
                        
                        Documento documento = (Documento) selModel.getSelectedObject();
                        
                        // Set el urlGoogleDoc
                        urlGoogleDoc = "";
                        
                        
                        
                        urlGoogleDoc = documento.getFullUrl(servidorArchivosVigentes);
                        // TODO al final debe ser DNS Externo. 
                        //urlGoogleDoc = urlGoogleDoc.replace(Constantes.DNS_SERVER, Constantes.IP_SERVER);
                        urlGoogleDoc = "http://docs.google.com/viewer?url=" + urlGoogleDoc + "&embedded=true";
                        urlGoogleDoc = URL.encode(urlGoogleDoc);
                        urlGoogleDoc = URL.encode(urlGoogleDoc);
                        urlGoogleDoc = urlGoogleDoc.replaceAll("%25", "%"); // sucede con el doble encode
                        
                        /* despliega el documento en google-docs*/
                        loadGoogleDoc();
                        
                    }
                }
            }
        }
    }
    
    public void setServidorArchivosVigentes(String servidorArchivosVigentes) {
        this.servidorArchivosVigentes = servidorArchivosVigentes;
    }
    
    
    private class ReloadGoogleDocMouseDownHandler implements MouseDownHandler {
        @Override
        public void onMouseDown(MouseDownEvent event) {
            loadGoogleDoc();
        }        
    }
    
    private void loadGoogleDoc() {
        
        reloadIcon.setSpin(true); //arranca el spin
        if (urlGoogleDoc == null || urlGoogleDoc.trim().isEmpty()) {
            Timer t = new Timer() {
                @Override
                public void run() {
                    // si no hay URL/Doc, gira 2 seg.
                    reloadIcon.setSpin(false);
                }
            };
            t.schedule(2000);            
        } else {
            frameDocViewer.setUrl(urlGoogleDoc);
        }
    }
    
    private native String optimizeFrame(String id_param) 
    /*-{
            var r = "0 ";
            var frame = $doc.getElementById("id_frameDocViewer");
            r = r + "1 ";
            if (frame != null) {
                r = r + "2 ";
                var contenido = frame.contentWindow.contentDocument || frame.contentWindow.document;
                r = r + "3 ";
                if (contenido != null) {
                    r = r + "4 ";
                    var ele = contenido.getElementById(id_param);
                    r = r + "5 ";
                    if (ele != null) {
                        r = r + "6 ";
                        ele.parentNode.removeChild(ele);
                        r = r + "7 ";
                    }
                }
            } 
            r = r + "8 ";
            return r;
    }-*/;
    
    private class FrameLoadHandler implements LoadHandler {
        // Se dispara al cargarse el iFrame; NO su contenido (el google doc viewer)
        @Override
        public void onLoad(LoadEvent event) {
            reloadIcon.setSpin(false); // detiene el spin            
        }
    }
    
    private class JSFiddlerMouseOver implements MouseOverHandler {
        @Override
        public void onMouseOver(MouseOverEvent event) {
            if (event.getSource() instanceof Label) {
                ((Label)event.getSource()).getElement().getStyle().setOpacity(0.1);
            }
        }
        
    }
    private class JSFiddlerMouseOut implements MouseOutHandler {
        @Override
        public void onMouseOut(MouseOutEvent event) {
            if (event.getSource() instanceof Label) {
                ((Label)event.getSource()).getElement().getStyle().setOpacity(0.8);
            }
        }
    }
    
}