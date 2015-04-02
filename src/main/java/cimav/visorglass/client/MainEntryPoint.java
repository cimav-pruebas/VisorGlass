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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import cimav.visorglass.client.widgets.MainLayout;

/**
 * Main entry point.
 *
 * @author juan.calderon
 */
public class MainEntryPoint implements EntryPoint {

    private final GWTServiceAsync gwtService;
    private final MainLayout mainLayout;
    
    public MainEntryPoint() {
        
//        System.out.println("URL: " + GWT.getModuleBaseURL()); 
//        Browsing: http://localhost:8080/visor?gwt.codesvr=127.0.0.1:9997
//        URL: http://localhost:8080/visor/cimav.visorglass.Main/

        gwtService = GWT.create(GWTService.class);
        mainLayout = new MainLayout(gwtService);
    }

    @Override
    public void onModuleLoad() {

        // Carga el Layout principal
        RootPanel.get("gwtContainer").add(mainLayout);
        
        // Intenta logearse
        mainLayout.loginOut(); // 1ero, se sale
       // mainLayout.loginIn();
        
        
    }
    
}
