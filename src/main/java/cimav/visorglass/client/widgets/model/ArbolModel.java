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
package cimav.visorglass.client.widgets.model;

import cimav.visorglass.shared.data.Arbol;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.view.client.TreeViewModel;
import java.util.ArrayList;
import java.util.List;
import cimav.visorglass.shared.data.Depto;
import cimav.visorglass.shared.data.TipoDocumento;

/**
 *
 * @author juan.calderon
 */
public class ArbolModel implements TreeViewModel {

    private ListDataProvider<Arbol> arbolDataProvider;
    private final SingleSelectionModel<Object> selectionModel = new SingleSelectionModel<Object>();
    private final DefaultSelectionEventManager<Arbol> selectionManagerArbol = DefaultSelectionEventManager.createCheckboxManager();
    private final DefaultSelectionEventManager<Depto> selectionManagerDepto = DefaultSelectionEventManager.createCheckboxManager();
    private final DefaultSelectionEventManager<TipoDocumento> selectionManagerTipoDoc = DefaultSelectionEventManager.createCheckboxManager();
    
    private Cell celda = new Celda(selectionModel) {};

    public ArbolModel() {
        this.arbolDataProvider = new ListDataProvider<Arbol>(new ArrayList<Arbol>());
    }

    public SingleSelectionModel<Object> getSelectionModel() {
        return this.selectionModel;
    }

    public void setDataProvider(Arbol arbol) {
        List<Arbol> arboles = new ArrayList<Arbol>();
        arboles.add(arbol);
        arbolDataProvider.setList(arboles);
    }

    @Override
    public <T> NodeInfo<?> getNodeInfo(T value) {

        if (value instanceof TreeItem) { // Raiz
            return new DefaultNodeInfo<Arbol>(arbolDataProvider, celda, selectionModel, selectionManagerArbol, null);
        } else if (value instanceof Arbol) {
            ListDataProvider<Depto> deptosDataProvider = new ListDataProvider<Depto>(((Arbol) value).getDeptos());
            return new DefaultNodeInfo<Depto>(deptosDataProvider, celda, selectionModel, selectionManagerDepto, null);
        } else if (value instanceof Depto) {
            ListDataProvider<TipoDocumento> dataProvider = new ListDataProvider<TipoDocumento>(((Depto) value).getTipos());
            return new DefaultNodeInfo<TipoDocumento>(dataProvider, celda, selectionModel, selectionManagerTipoDoc, null);
        }

        return null;
    }

    @Override
    public boolean isLeaf(Object value) {
        return value instanceof TipoDocumento;
    }

    public void expandTreeNode(TreeNode node) {
        for (int i = 0; i < node.getChildCount(); i++) {
            if (!node.isChildLeaf(i)) {
                expandTreeNode(node.setChildOpen(i, true));
            }
        }
    }

    private abstract class Celda extends AbstractCell<Object> {

        private SingleSelectionModel<Object> selectionModel;

        public Celda(SingleSelectionModel<Object> selectionModel) {
            super("mousedown");
            this.selectionModel = selectionModel;
        }

        @Override
        public void onBrowserEvent(Cell.Context context, com.google.gwt.dom.client.Element parent, Object value, NativeEvent event, ValueUpdater<Object> valueUpdater) {
            if (event.getButton() == NativeEvent.BUTTON_LEFT) {
                if (selectionModel != null) {
                    selectionModel.clear();
                    selectionModel.setSelected(value, true);
                }
            }
            super.onBrowserEvent(context, parent, value, event, valueUpdater);
        }

        @Override
        public void render(Cell.Context context, Object value, SafeHtmlBuilder sb) {
            if (value != null) {
                if (value instanceof Arbol) {
                    sb.appendHtmlConstant("<span class='gwt-InlineLabel badge'>Todos</span>");
                } else if (value instanceof Depto) {
                    Depto depto = (Depto) value;
                    //sb.appendHtmlConstant("<div class='margenDeptoItemArbol'><span class='gwt-InlineLabel badge'>"+depto.getCodigo()+"</span>" + depto.getNombre() +"</div>");
                    sb.appendHtmlConstant("<div class='divDeptoArbolItem'><samp>"+depto.getCodigo()+"</samp>" + depto.getNombre() +"</div>");
                } else if (value instanceof TipoDocumento) {
                    TipoDocumento tipoDocumento = (TipoDocumento) value;
                    sb.appendHtmlConstant("<div class='divTipoDocArbolItem'><samp>"+tipoDocumento.getCodigo()+"</samp>" + tipoDocumento.getNombre() + "</div>");
                }
               
            }
        }
    }
    
}
