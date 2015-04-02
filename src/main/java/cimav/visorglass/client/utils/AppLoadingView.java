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
package cimav.visorglass.client.utils;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 *
 * @author juan.calderon
 */
public class AppLoadingView extends PopupPanel {
    
    private final FlowPanel container = new FlowPanel();
    
    public AppLoadingView() {  
        this.setGlassEnabled(true);
        final Image ajaxImage = new Image("images/ajax_loader_large.gif");
        ajaxImage.setHeight("200px");
        ajaxImage.setWidth("200px");
        final Grid grid = new Grid(2, 1);  
        grid.setWidget(0, 0, ajaxImage);
        
        HTMLTable.CellFormatter formatter = grid.getCellFormatter();
        formatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        
        this.container.add(grid);
        add(this.container);    
        
        this.container.setStyleName("transparente");
        this.setStyleName("transparente");
        ajaxImage.setStyleName("transparente");
    } 
    
}