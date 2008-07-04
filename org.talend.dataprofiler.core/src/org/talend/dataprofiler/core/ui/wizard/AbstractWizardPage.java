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

import java.util.EventListener;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.wizard.WizardPage;
import org.talend.dq.analysis.parameters.ConnectionParameter;

/**
 * DOC zqin class global comment. Detailled comment
 */
public abstract class AbstractWizardPage extends WizardPage {

    // ----message define----

    public static final String MSG_EMPTY = "some fieldes are empty, please check!";

    public static final String MSG_ONLY_CHAR = "this field just allowed to input character text, please check!";

    public static final String MSG_ONLY_NUMBER = "this field just allowed to input numberic text, please check!";

    public static final String MSG_ONLY_DATE = "this field just allowed to input date formate text, please check!";

    public static final String MSG_OK = "your input is valid!";

    private static ConnectionParameter connectionParams;

    protected AbstractWizardPage.ICheckListener listener;

    private int statusLevel = IStatus.OK;

    private String status;

    /**
     * DOC zqin AbstractWizardPage constructor comment.
     */
    protected AbstractWizardPage() {
        super("wizard");

        this.listener = new AbstractWizardPage.ICheckListener() {

            public void checkPerformed(AbstractWizardPage sourcePage) {
                if (sourcePage.isStatusOnError()) {
                    setPageComplete(false);
                    setErrorMessage(sourcePage.getStatus());
                } else {
                    setPageComplete(true);
                    setErrorMessage(null);
                    setMessage(sourcePage.getStatus(), sourcePage.getStatusLevel());
                }
            }

        };
    }

    /**
     * @return the connectionParams
     */
    public static ConnectionParameter getConnectionParams() {
        return AbstractWizardPage.connectionParams;
    }

    /**
     * @param connectionParams the connectionParams to set
     */
    public static synchronized void setConnectionParams(ConnectionParameter connectionParams) {
        AbstractWizardPage.connectionParams = connectionParams;
    }

    /**
     * DOC zqin Comment method "checkFieldsValue".
     * 
     * @return
     */
    public boolean checkFieldsValue() {

        return true;
    }

    /**
     * DOC zqin AbstractWizardPage class global comment. Detailled comment
     */
    public static interface ICheckListener extends EventListener {

        void checkPerformed(AbstractWizardPage sourcePage);
    }

    /**
     * update Status of the Wizard OR of the label Status.
     * 
     * @param String
     */
    protected void updateStatus(final int status, final String statusLabelText) {

        this.status = statusLabelText;
        this.statusLevel = status;
        if (listener != null) {
            listener.checkPerformed(this);
        }
    }

    public String getStatus() {
        return status;
    }

    /**
     * Getter for statusOnError.
     * 
     * @return the statusOnError
     */
    public boolean isStatusOnError() {
        return this.statusLevel == IStatus.ERROR;
    }

    /**
     * Getter for statusOnValid.
     * 
     * @return the statusOnValid
     */
    public boolean isStatusOnValid() {
        return this.statusLevel == IStatus.OK;
    }

    public int getStatusLevel() {
        return statusLevel;
    }
}
