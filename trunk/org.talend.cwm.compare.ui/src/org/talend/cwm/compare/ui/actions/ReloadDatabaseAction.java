// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.compare.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.utils.platform.PluginChecker;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.ComparisonLevelFactory;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.cwm.compare.ui.ImageLib;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ReloadDatabaseAction extends Action {

    private static Logger log = Logger.getLogger(ReloadDatabaseAction.class);

    private static final String ANALYSIS_EDITOR_ID = "org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor"; //$NON-NLS-1$

    private Object selectedObject;

    public ReloadDatabaseAction(Object selectedNode, String menuText) {
        super(menuText);
        this.selectedObject = selectedNode;
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.UPDATE_IMAGE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        // MOD klliu 2011-06-13 bug unified the confirm message TOP with TDQ
        String confirmMessage = PluginChecker.isOnlyTopLoaded() ? Messages.getString("ReloadDatabaseAction.ConfirmMessage1") : Messages.getString("ReloadDatabaseAction.ConfirmMessage0"); //$NON-NLS-1$
        boolean openConfirm = MessageDialog.openConfirm(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                Messages.getString("ReloadDatabaseAction.ConfirmMessageTitle"), //$NON-NLS-1$
                confirmMessage);
        if (!openConfirm) {
            return;
        }
        // ~ 22251
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                final IComparisonLevel creatComparisonLevel = ComparisonLevelFactory.creatComparisonLevel(selectedObject);
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        try {

                            DataProvider oldDataProvider = creatComparisonLevel.reloadCurrentLevelElement();

                            // MOD mzhao 2009-07-13 bug 7454 Impact existing
                            // analysis.
                            // MOD qiongli 2011-9-8,move method 'impactExistingAnalyses(...)' to class WorkbenchUtils
                            WorkbenchUtils.impactExistingAnalyses(oldDataProvider);
                        } catch (ReloadCompareException e) {
                            log.error(e, e);
                        } catch (PartInitException e) {
                            log.error(e, e);
                        }

                    }
                });
            }

        };
        try {
            ProgressUI.popProgressDialog(op);
            CorePlugin.getDefault().refreshDQView();
        } catch (InvocationTargetException e) {
            MessageUI.openError(Messages.getString("ReloadDatabaseAction.checkConnectionFailured", e.getCause().getMessage())); //$NON-NLS-1$
            log.error(e, e);
        } catch (InterruptedException e) {
            log.error(e, e);
        }

    }

}
