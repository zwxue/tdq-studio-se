// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.composite;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.talend.dataprofiler.core.PluginConstant;

/**
 * DOC rli class global comment. Detailled comment
 */
public class AbstractPagePart {

    private boolean isDirty;
    protected PropertyChangeSupport propertyChangeSupport;

    public AbstractPagePart() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void setDirty(boolean dirty) {
        if (isDirty != dirty) {
            this.isDirty = dirty;
            propertyChangeSupport.firePropertyChange(PluginConstant.ISDIRTY_PROPERTY, null, Boolean.valueOf(dirty));
        }
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
