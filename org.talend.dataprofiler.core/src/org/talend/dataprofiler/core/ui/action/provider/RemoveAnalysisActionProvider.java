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
package org.talend.dataprofiler.core.ui.action.provider;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.dataprofiler.core.ui.action.actions.RemoveAnalysisAction;
/**
 * DOC rli class global comment. Detailled comment
 */
public class RemoveAnalysisActionProvider extends CommonActionProvider {

    public RemoveAnalysisActionProvider() {
    }

    public void fillContextMenu(IMenuManager menu) {
        menu.add(new RemoveAnalysisAction());
    }
}
