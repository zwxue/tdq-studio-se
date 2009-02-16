// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdCatalog;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dq.analysis.parameters.PackagesAnalyisParameter;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class OverviewAnalysisAction extends Action implements ICheatSheetAction {

    private static final int WIDTH = 600;

    private static final int HEIGHT = 450;

    private Package[] packageObjs;

    public OverviewAnalysisAction() {
        super(DefaultMessagesImpl.getString("OverviewAnalysisAction.OverviewAnalysis")); //$NON-NLS-1$
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ACTION_NEW_ANALYSIS));
    }

    public OverviewAnalysisAction(Package[] packageObjs) {
        this();
        this.packageObjs = packageObjs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        PackagesAnalyisParameter packaFilterParameter = new PackagesAnalyisParameter();
        packaFilterParameter.setTdDataProvider(DataProviderHelper.getTdDataProvider(packageObjs[0]));
        packaFilterParameter.setPackages(packageObjs);
        Wizard wizard;
        TdCatalog catalogSwitch = SwitchHelpers.CATALOG_SWITCH.doSwitch(packageObjs[0]);
        if (catalogSwitch != null) {
            wizard = WizardFactory.createAnalysisWizard(AnalysisType.CATALOG, packaFilterParameter);
        } else {
            wizard = WizardFactory.createAnalysisWizard(AnalysisType.SCHEMA, packaFilterParameter);
        }

        WizardDialog dialog = new WizardDialog(null, wizard);
        dialog.setPageSize(WIDTH, HEIGHT);
        dialog.open();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[],
     * org.eclipse.ui.cheatsheets.ICheatSheetManager)
     */
    public void run(String[] params, ICheatSheetManager manager) {
        run();
    }
}
