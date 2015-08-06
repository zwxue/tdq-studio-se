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
package org.talend.dataprofiler.core.ui.imex;

/**
 * created by xqliu on Feb 1, 2013 Detailled comment
 */
public class ImportFromExchangeWizard extends ImportWizard {

    public ImportFromExchangeWizard(String basePath) {
        setWindowTitle("Import From Talend Exchange");//$NON-NLS-1$ 
        this.importPage = new ImportFromExchangeWizardPage(basePath);
    }
}
