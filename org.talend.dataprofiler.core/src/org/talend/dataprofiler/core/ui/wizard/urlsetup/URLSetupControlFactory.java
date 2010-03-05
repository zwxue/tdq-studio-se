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
package org.talend.dataprofiler.core.ui.wizard.urlsetup;

import org.eclipse.swt.widgets.Composite;
import org.talend.cwm.dburl.SupportDBUrlType;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;
import org.talend.dq.analysis.parameters.DBConnectionParameter;

/**
 * 
 */
public class URLSetupControlFactory {

    public static boolean hasControl(SupportDBUrlType dbType) {
        return null != getControlClass(dbType);
    }

    public static URLSetupControl create(SupportDBUrlType dbType, Composite composite, DBConnectionParameter connectionParam) {
        Class controlClass = getControlClass(dbType);

        if (BasicThreePartURLSetupControl.class == controlClass) {
            URLSetupControl control = new BasicThreePartURLSetupControl(composite, dbType);
            control.createPart(connectionParam);
            return control;
        } else {
            return null;
        }
    }

    public static URLSetupControl create(SupportDBUrlType dbType, Composite composite, DBConnectionParameter connectionParam,
            AbstractWizardPage abstractWizardPage) {
        Class controlClass = getControlClass(dbType);

        if (BasicThreePartURLSetupControl.class == controlClass) {
            URLSetupControl control = new BasicThreePartURLSetupControl(composite, dbType, abstractWizardPage);
            control.createPart(connectionParam);
            return control;
        } else {
            return null;
        }
    }

    /**
     * Looks into the parameter list of the URL and decides which of the subclasses matches the needed parameters, to
     * use it to display the text boxes needed fot those parameters.
     * 
     * @param driver
     * @return
     */
    private static Class getControlClass(SupportDBUrlType dbType) {
        return BasicThreePartURLSetupControl.class;
    }
}
