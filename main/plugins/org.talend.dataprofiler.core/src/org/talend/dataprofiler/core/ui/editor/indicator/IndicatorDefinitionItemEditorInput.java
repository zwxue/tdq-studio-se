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
package org.talend.dataprofiler.core.ui.editor.indicator;

import org.talend.core.model.properties.Item;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;

/**
 * DOC klliu Indicator definition editor input.
 */
public class IndicatorDefinitionItemEditorInput extends AbstractItemEditorInput {


    private TDQIndicatorDefinitionItem item = null;

    /**
     * DOC klliu IndicatorDefinitionItemEditorInput constructor comment.
     * 
     * @param reposViewObj
     */
    public IndicatorDefinitionItemEditorInput(Item item) {
        super(item);
        this.item = (TDQIndicatorDefinitionItem) item;

    }

    @Override
    public String getName() {
        return getPath() + item.getIndicatorDefinition().getName();
    }

    @Override
    public String getToolTipText() {
        return getPath() + item.getIndicatorDefinition().getName();
    }

    public TDQIndicatorDefinitionItem getTDQIndicatorDefinitionItem() {
        return item;
    }

    public String getModelElementUuid() {
        if (this.item != null) {
            return ResourceHelper.getUUID(this.item.getIndicatorDefinition());
        }
        return super.getModelElementUuid();
    }
}
