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
package org.talend.dataprofiler.core.ui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.internal.cheatsheets.views.CheatSheetView;
import org.talend.dataprofiler.core.PluginConstant;

/**
 * DOC qiongli class global comment. bug 15191
 */

@SuppressWarnings("restriction")
public class DQCheatSheetView extends CheatSheetView {


    @Override
    public void createPartControl(Composite parent) {

        super.createPartControl(parent);
        this.setInput(PluginConstant.CHEAT_SHEET_GETSTART_ID);
    }


}
