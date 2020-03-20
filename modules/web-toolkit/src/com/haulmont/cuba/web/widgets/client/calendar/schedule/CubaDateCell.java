/*
 * Copyright (c) 2008-2020 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.haulmont.cuba.web.widgets.client.calendar.schedule;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.haulmont.cuba.web.widgets.client.calendar.CubaCalendarWidget;
import com.vaadin.v7.client.ui.calendar.schedule.DateCell;
import com.vaadin.v7.client.ui.calendar.schedule.WeekGrid;

import java.util.Date;

public class CubaDateCell extends DateCell {

    public static final String SLOT_NUMBER_STYLENAME = "c-date-number";

    protected boolean mouseDown = false;

    public CubaDateCell(WeekGrid parent, Date date) {
        super(parent, date);

        for (int i = 0; i < slots.size(); i++) {
            Element slotElement = slots.get(i).getElement();
            slotElement.addClassName(SLOT_NUMBER_STYLENAME + "-" + i);
        }
    }

    @Override
    public void onMouseDown(MouseDownEvent event) {
        super.onMouseDown(event);

        if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
            mouseDown = true;
        }
    }

    @Override
    public void onMouseUp(MouseUpEvent event) {
        super.onMouseUp(event);

        mouseDown = false;
    }

    /**
     * CAUTION! This is not actually Click event. It is invoked from MouseUpEvent when calendar event move is not
     * handled.
     *
     * @param event mouse up event
     */
    @Override
    protected void handleClick(MouseUpEvent event) {
        super.handleClick(event);
        // if mouse down was not in calendar surface
        if (!mouseDown) {
            return;
        }

        Element target = Element.as(event.getNativeEvent().getEventTarget());
        String targetSlotNumber = getSlotNumberStyle(target);

        for (DateCellSlot slot : slots) {
            String className = slot.getElement().getClassName();

            if (className.contains(targetSlotNumber)) {
                CubaCalendarWidget calendar = (CubaCalendarWidget) weekgrid.getCalendar();
                if (calendar.getWeekDayClickListener() != null) {
                    calendar.getWeekDayClickListener().accept(
                            new WeekDayClickEvent(slot.getFrom(), slot.getTo(), getDate())
                    );
                }
                break;
            }
        }
    }

    public String getSlotNumberStyle(Element element) {
        for (int i = 0; i < slots.size(); i++) {
            String className = element.getClassName();
            String slotNumberStyle = SLOT_NUMBER_STYLENAME + "-" + i;

            if (className.contains(slotNumberStyle)) {
                return slotNumberStyle;
            }
        }

        return null;
    }
}
