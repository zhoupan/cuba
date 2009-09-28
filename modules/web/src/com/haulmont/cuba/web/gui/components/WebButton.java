/*
 * Copyright (c) 2008 Haulmont Technology Ltd. All Rights Reserved.
 * Haulmont Technology proprietary and confidential.
 * Use is subject to license terms.

 * Author: Dmitry Abramov
 * Created: 19.12.2008 17:21:57
 * $Id$
 */
package com.haulmont.cuba.web.gui.components;

import com.haulmont.cuba.gui.components.Action;
import com.haulmont.cuba.gui.components.Component;
import com.haulmont.cuba.gui.components.Button;
import org.apache.commons.lang.StringUtils;

public class WebButton
    extends
        WebAbstractComponent<com.vaadin.ui.Button>
    implements
        Button, Component.Wrapper
{
    private Action action;

    public WebButton() {
        component = new com.vaadin.ui.Button();
        component.addListener(new com.vaadin.ui.Button.ClickListener() {
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                if (action != null) {
                    action.actionPerform(WebButton.this);
                }
            }
        });
    }

    public String getCaption() {
        return component.getCaption();
    }

    public void setCaption(String caption) {
        component.setCaption(caption);
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;

        final String caption = action.getCaption();
        if (!StringUtils.isEmpty(caption)) {
            component.setCaption(caption);
        }

        component.setEnabled(action.isEnabled());
    }
}
