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
package cimav.visorglass.client.widgets.components;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;

/**
 *
 * @author juan.calderon
 */
public class ScrollingPager extends AbstractPager {

    private int incrementSize = 20;
    private int lastScrollPos = 0;
    private final ScrollPanel scrollable = new ScrollPanel();

    public ScrollingPager() {
        initWidget(scrollable);
        scrollable.setPixelSize(250, 350);
        scrollable.getElement().getStyle().setBorderWidth(1, Style.Unit.PX);
        scrollable.getElement().getStyle().setBorderColor("red");
        scrollable.getElement().setTabIndex(-1);

        // Handle scroll events.
        scrollable.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent p_event) {
                // If scrolling up, ignore the event.
                int oldScrollPos = lastScrollPos;
                lastScrollPos = scrollable.getVerticalScrollPosition();
                if (oldScrollPos >= lastScrollPos) {
                    return;
                }
                HasRows display = getDisplay();
                if (display == null) {
                    return;
                }
                int maxScrollTop =
                        scrollable.getWidget().getOffsetHeight()
                        - scrollable.getOffsetHeight();
                if (lastScrollPos >= maxScrollTop) {
                    // We are near the end, so increase the page size.
                    int newPageSize =
                            Math.min(display.getVisibleRange()
                            .getLength()
                            + incrementSize,
                            display.getRowCount());
                    display.setVisibleRange(0, newPageSize);
                }
            }
        });
    }

    public int getIncrementSize() {
        return incrementSize;
    }

    @Override
    public void setDisplay(HasRows display) {
        assert display instanceof Widget : "display must extend Widget";
        scrollable.setWidget((Widget) display);
        super.setDisplay(display);
    }

    public void setIncrementSize(int incrementSize) {
        this.incrementSize = incrementSize;
    }

    @Override
    protected void onRangeOrRowCountChanged() {
    }
    
}
