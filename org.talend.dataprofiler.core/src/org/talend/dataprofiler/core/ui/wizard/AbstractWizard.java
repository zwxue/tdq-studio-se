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
package org.talend.dataprofiler.core.ui.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.talend.dq.analysis.parameters.ConnectionParameter;

/**
 * DOC zqin class global comment. Detailled comment
 */
public abstract class AbstractWizard extends Wizard {

    protected abstract ConnectionParameter getConnectionParameter();
}
