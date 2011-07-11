// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.nodes;

import org.talend.commons.i18n.internal.DefaultMessagesImpl;
import org.talend.commons.ui.runtime.image.ECoreImage;
import org.talend.commons.ui.runtime.image.IImage;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class RecycleBinRepNode extends DQRepositoryNode {

    private IImage icon;

    private String label;

    /**
     * DOC klliu RecyleBinRepNode constructor comment.
     * 
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
        if (this.label == null) {
            this.label = DefaultMessagesImpl.getString("RecycleBin.resBinName"); //$NON-NLS-1$
        }
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

    @Override
    public boolean isBin() {
        return true;
    }
}
