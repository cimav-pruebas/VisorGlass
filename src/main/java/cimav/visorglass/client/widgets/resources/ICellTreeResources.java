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
package cimav.visorglass.client.widgets.resources;

import com.google.gwt.user.cellview.client.CellTree;

/**
 *
 * @author juan.calderon
 */
public interface ICellTreeResources extends CellTree.Resources {

    @Source("CellTree.css") 
    @Override
    CellTree.Style cellTreeStyle(); 

//    @ImageOptions(flipRtl = true)
//    @Source("cellTreeClosedArrow.gif")
//    @Override
//    ImageResource cellTreeClosedItem();
//
//    /**
//    * An image indicating an open branch.
//    */
//    @ImageOptions(flipRtl = true)
//    @Source("cellTreeOpenArrow.gif")
//    @Override
//    ImageResource cellTreeOpenItem();

}
