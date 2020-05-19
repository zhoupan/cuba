/*
 * Copyright (c) 2008-2019 Haulmont.
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

package com.haulmont.cuba.gui.components;

import com.haulmont.cuba.gui.Dialogs;
import com.haulmont.cuba.gui.meta.PropertyType;
import com.haulmont.cuba.gui.meta.StudioFacet;
import com.haulmont.cuba.gui.meta.StudioProperties;
import com.haulmont.cuba.gui.meta.StudioProperty;

/**
 * Prepares and shows message dialogs.
 */
@StudioFacet(
        xmlElement = "messageDialog",
        caption = "Message Dialog",
        description = "Prepares and shows message dialogs",
        defaultProperty = "message",
        category = "Facets",
        icon = "icon/dialog.svg",
        documentationURL = "https://doc.cuba-platform.com/manual-%VERSION%/gui_MessageDialogFacet.html"
)
@StudioProperties(
        properties = {
                @StudioProperty(name = "id", type = PropertyType.COMPONENT_ID, required = true)
        }
)
public interface MessageDialogFacet extends Facet {

    /**
     * Sets dialog caption.
     * @param caption caption
     */
    @StudioProperty(type = PropertyType.LOCALIZED_STRING)
    void setCaption(String caption);

    /**
     * @return dialog caption
     */
    String getCaption();

    /**
     * Sets dialog message.
     * @param message message
     */
    @StudioProperty(type = PropertyType.LOCALIZED_STRING)
    void setMessage(String message);

    /**
     * @return dialog message
     */
    String getMessage();

    /**
     * Sets dialog type.
     * @param type type
     */
    @StudioProperty(type = PropertyType.ENUMERATION)
    void setType(Dialogs.MessageType type);

    /**
     * @return dialog type
     */
    Dialogs.MessageType getType();

    /**
     * Sets dialog message content mode.
     * @param contentMode content mode
     */
    @StudioProperty(type = PropertyType.ENUMERATION, defaultValue = "TEXT")
    void setContentMode(ContentMode contentMode);

    /**
     * @return dialog message content mode
     */
    ContentMode getContentMode();

    /**
     * Sets whether dialog should be maximized.
     * @param maximized maximized
     */
    @StudioProperty(type = PropertyType.BOOLEAN)
    void setMaximized(boolean maximized);

    /**
     * @return whether dialog should be maximized
     */
    boolean isMaximized();

    /**
     * Sets whether dialog should be modal
     * @param modal modal
     */
    @StudioProperty(type = PropertyType.BOOLEAN)
    void setModal(boolean modal);

    /**
     * @return whether dialog should be modal
     */
    boolean isModal();

    /**
     * Sets dialog style name.
     * @param styleName style name
     */
    @StudioProperty(type = PropertyType.STRING)
    void setStyleName(String styleName);

    /**
     * @return dialog style name
     */
    String getStyleName();

    /**
     * Sets dialog width.
     * @param width width
     */
    @StudioProperty(type = PropertyType.SIZE)
    void setWidth(String width);

    /**
     * @return dialog width
     */
    float getWidth();

    /**
     * @return dialog width size unit
     */
    SizeUnit getWidthSizeUnit();

    /**
     * Sets dialog height.
     * @param height height
     */
    @StudioProperty(type = PropertyType.SIZE)
    void setHeight(String height);

    /**
     * @return dialog height
     */
    float getHeight();

    /**
     * @return dialog height size unit
     */
    SizeUnit getHeightSizeUnit();

    /**
     * Sets that dialog should be shown when action with id {@code actionId}
     * is performed.
     *
     * @param actionId action id
     */
    @StudioProperty(name = "onAction", type = PropertyType.COMPONENT_REF,
            options = "com.haulmont.cuba.gui.components.Action")
    void setActionTarget(String actionId);

    /**
     * @return id of action that triggers dialog
     */
    String getActionTarget();

    /**
     * Sets that dialog should be shown when button with id {@code actionId}
     * is clicked.
     *
     * @param buttonId button id
     */
    @StudioProperty(name = "onButton", type = PropertyType.COMPONENT_REF,
            options = "com.haulmont.cuba.gui.components.Button")
    void setButtonTarget(String buttonId);

    /**
     * @return id of button that triggers dialog
     */
    String getButtonTarget();

    /**
     * Sets whether the dialog should be closed on click outside.
     *
     * @param closeOnClickOutside close on click outside
     */
    @StudioProperty(type = PropertyType.BOOLEAN)
    void setCloseOnClickOutside(boolean closeOnClickOutside);

    /**
     * @return whether the dialog should be closed on click outside
     */
    boolean isCloseOnClickOutside();

    /**
     * Sets whether html sanitizer is enabled or not for dialog content.
     *
     * @param htmlSanitizerEnabled specifies whether html sanitizer is enabled
     */
    @StudioProperty(type = PropertyType.BOOLEAN)
    void setHtmlSanitizerEnabled(boolean htmlSanitizerEnabled);

    /**
     * @return html sanitizer is enabled for dialog content
     */
    boolean isHtmlSanitizerEnabled();

    /**
     * Shows dialog.
     */
    void show();
}
