// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.views.nodes;

import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.IImage;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC klliu  class global comment. Detailled comment
 */
public class RecycleBinRepNode extends RepositoryNode {

    private IImage icon;

    private String label;
    /**
     * DOC klliu RecyleBinRepNode constructor comment.
     * @param object
     * @param parent
     * @param type
     */
    public RecycleBinRepNode(String label) {
        super(null, null, ENodeType.STABLE_SYSTEM_FOLDER);
        this.label = label;
        this.icon = ECoreImage.RECYCLE_BIN_EMPTY_ICON;
    }

    /**
     * Getter for icon.
     * 
     * @return the icon
     */
    public IImage getIcon() {
        return this.icon;
    }

    /**
     * Sets the icon.
     * 
     * @param icon the icon to set
     */
    public void setIcon(IImage icon) {
        this.icon = icon;
    }

    /**
     * Getter for label.
     * 
     * @return the label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Sets the label.
     * 
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }


}
