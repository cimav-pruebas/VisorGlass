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

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author juan.calderon
 */
public class Ajax {
    private static final AppLoadingView loadingIndicator = new AppLoadingView();
    private static final List<AsyncCallback<?>> callstack = new ArrayList<AsyncCallback<?>>();

    public static <T> AsyncCallback<T> call(final AsyncCallback<T> callback) {
        if(!loadingIndicator.isShowing()){
            loadingIndicator.center();
        }
        callstack.add(callback);
        return new AsyncCallback<T>() {
            @Override
            public void onFailure(Throwable caught) {
                handleReturn();
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(T result) {
                handleReturn();
                callback.onSuccess(result);
            }

            private void handleReturn(){
                callstack.remove(callback);
                if(callstack.size() < 1) {
                    loadingIndicator.hide();
                }
            }
        };
    }    
}
