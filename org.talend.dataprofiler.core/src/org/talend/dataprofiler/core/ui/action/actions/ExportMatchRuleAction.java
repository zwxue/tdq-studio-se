// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.talend.dataprofiler.core.ImageLib;


/**
 * DOC yyin  class global comment. Detailled comment
 */
public class ExportMatchRuleAction extends Action {

    public ExportMatchRuleAction(List rules) {
        ImageDescriptor imageDescriptor = ImageLib.getImageDescriptor(ImageLib.EXPORT_MATCH_RULE_ICON);

        this.setImageDescriptor(imageDescriptor);

    }
}
