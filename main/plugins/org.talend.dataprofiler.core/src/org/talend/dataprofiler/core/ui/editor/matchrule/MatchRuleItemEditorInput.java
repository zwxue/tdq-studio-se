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
package org.talend.dataprofiler.core.ui.editor.matchrule;

import org.talend.core.model.properties.Item;
import org.talend.dataprofiler.core.ui.editor.AbstractItemEditorInput;
import org.talend.dataquality.properties.TDQMatchRuleItem;
import orgomg.cwm.objectmodel.core.ModelElement;


/**
 * created by zshen on Aug 19, 2013
 * Detailled comment
 *
 */
public class MatchRuleItemEditorInput extends AbstractItemEditorInput {


    /**
     * DOC zshen MatchRuleItemEditorInput constructor comment.
     *
     * @param tdqItem
     */
    public MatchRuleItemEditorInput(Item tdqItem) {
        super(tdqItem);
    }

    public ModelElement getMatchRule() {
        return ((TDQMatchRuleItem) this.getItem()).getMatchRule();
    }

}
