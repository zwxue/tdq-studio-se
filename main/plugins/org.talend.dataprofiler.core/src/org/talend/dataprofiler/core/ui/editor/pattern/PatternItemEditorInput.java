// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.editor.pattern;

import org.talend.core.model.properties.Item;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataquality.properties.TDQPatternItem;

/**
 * DOC klliu Pattern item editor input.
 */
public class PatternItemEditorInput extends AbstractItemEditorInput {


    private TDQPatternItem item = null;
    /**
     * DOC klliu TDQPatternItemEditorInput constructor comment.
     * @param reposViewObj
     */
    public PatternItemEditorInput(Item item) {
        super(item);
        this.item = (TDQPatternItem) item;

    }

    @Override
    public String getName() {
        return getPath() + item.getPattern().getName();
    }

    @Override
    public String getToolTipText() {
        return getPath() + item.getPattern().getName();
    }

    public TDQPatternItem getTDQPatternItem() {
        return item;
    }

    public String getModelElementUuid() {
        if (this.item != null) {
            return ResourceHelper.getUUID(this.item.getParent());
        }
        return super.getModelElementUuid();
    }
}
