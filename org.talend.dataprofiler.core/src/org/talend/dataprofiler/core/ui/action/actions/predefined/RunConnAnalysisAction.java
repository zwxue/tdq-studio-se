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
package org.talend.dataprofiler.core.ui.action.actions.predefined;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.wizard.WizardDialog;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.helper.PrvResourceFileHelper;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction;
import org.talend.dataprofiler.core.ui.wizard.analysis.AbstractAnalysisWizardPage;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dq.analysis.parameters.ConnectionAnalysisParameter;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * DOC qzhang class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z nrousseau $
 * 
 */
public class RunConnAnalysisAction extends AbstractPredefinedAnalysisAction {

    private IFile file;

    /**
     * DOC qzhang RunConnAnalysisAction constructor comment.
     */
    public RunConnAnalysisAction(IFile file) {
        super("Create connection analysis", ImageLib.getImageDescriptor(ImageLib.ACTION_NEW_ANALYSIS));
        this.file = file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction#getPredefinedColumnIndicator()
     */
    @Override
    protected ColumnIndicator[] getPredefinedColumnIndicator() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction#getPredefinedDialog()
     */
    @Override
    protected WizardDialog getPredefinedDialog() {
        WizardDialog sana = getStandardAnalysisWizardDialog(AnalysisType.CONNECTION);

        setTdDataProvider();

        return sana;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction#isAllowed()
     */
    @Override
    protected boolean isAllowed() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction#preDo()
     */
    @Override
    protected boolean preDo() {
        return true;
    }

    /**
     * DOC qzhang Comment method "setTdDataProvider".
     */
    private void setTdDataProvider() {
        ConnectionAnalysisParameter connectionParams = (ConnectionAnalysisParameter) AbstractAnalysisWizardPage
                .getConnectionParams();
        if (connectionParams == null) {
            connectionParams = new ConnectionAnalysisParameter();
        }

        file = (IFile) getSelection().getFirstElement();
        TypedReturnCode<TdDataProvider> tdProvider = PrvResourceFileHelper.getInstance().getTdProvider(file);
        TdDataProvider dataProvider = tdProvider.getObject();
        connectionParams.setTdDataProvider(dataProvider);

        AbstractAnalysisWizardPage.setConnectionParams(connectionParams);
    }

}
