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
package org.talend.dataprofiler.core.ui.wizard.analysis;

import org.eclipse.jface.wizard.Wizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.column.ColumnWizard;
import org.talend.dataprofiler.core.ui.wizard.analysis.connection.ConnectionWizard;

/**
 * @author zqin
 * 
 */
public class WizardFactory {

    public static Wizard createConnectionWizard() {
        return new ConnectionWizard();
    }

    /**
     * DOC qzhang Comment method "createConnectionWizard".
     * 
     * @param containStep0
     * @return
     */
    public static Wizard createConnectionWizard(boolean containStep0) {
        return new ConnectionWizard(containStep0);
    }

    public static Wizard createColumnWizard() {
        return new ColumnWizard();
    }
}
