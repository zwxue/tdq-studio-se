// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.preview.model;

import org.eclipse.swt.graphics.Image;
import org.talend.dataprofiler.core.ImageLib;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class MenuItemEntity {

    private String label;

    private Image icon;

    private String query;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Image getIcon() {
        return icon == null ? ImageLib.getImage(ImageLib.EXPLORE_IMAGE) : icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public MenuItemEntity(String label, Image icon, String query) {
        this.label = label;
        this.icon = icon;
        this.query = query;
    }
}
