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
package org.talend.dataprofiler.core.ui.action.actions;

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
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.ComparisonLevelFactory;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.repository.ui.utils.SwitchContextGroupNameImpl;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
/**
 * DOC msjian class global comment. Detailled comment
 */
public class SwitchContextAction extends Action {

    private static Logger log = Logger.getLogger(SwitchContextAction.class);

    private Object selectedObject;

    public SwitchContextAction(Object selectedNode, String menuText) {
        super(menuText);
        this.selectedObject = selectedNode;
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.SWITCH_IMAGE));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {

        if (selectedObject instanceof DBConnectionRepNode) {
            DBConnectionRepNode dbConnectionRepNode = (DBConnectionRepNode) selectedObject;
            Item item = dbConnectionRepNode.getObject().getProperty().getItem();
            if (item instanceof ConnectionItem) {
                ConnectionItem connItem = (ConnectionItem) item;
                boolean isUpdated = SwitchContextGroupNameImpl.getInstance().updateContextGroup(connItem);
                DatabaseConnection dbcon = dbConnectionRepNode.getDatabaseConnection();
                String fileStr = dbcon.eResource().getURI().toFileString();
                if (isUpdated) {
                    if (log.isDebugEnabled()) {
                        log.debug(DefaultMessagesImpl.getString("SwitchContextAction.saveMessage", fileStr, "successful"));//$NON-NLS-1$ //$NON-NLS-2$
                    }
                    reloadDatabase();
                } else {
                    log.error(DefaultMessagesImpl.getString("SwitchContextAction.saveMessage", fileStr, "failed"));//$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
    }

    /**
     * DOC msjian Comment method "reloadDatabase".
     */
    public void reloadDatabase() {
        String confirmMessage = PluginChecker.isOnlyTopLoaded() ? DefaultMessagesImpl
                .getString("ReloadDatabaseAction.ConfirmMessage1") : DefaultMessagesImpl
                .getString("ReloadDatabaseAction.ConfirmMessage0"); //$NON-NLS-1$
        boolean openConfirm = MessageDialog.openConfirm(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                DefaultMessagesImpl.getString("ReloadDatabaseAction.ConfirmMessageTitle"), //$NON-NLS-1$
                confirmMessage);
        if (!openConfirm) {
            return;
        }

        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                final IComparisonLevel creatComparisonLevel = ComparisonLevelFactory.creatComparisonLevel(selectedObject);
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        try {
                            DataProvider oldDataProvider = creatComparisonLevel.reloadCurrentLevelElement();
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
            MessageUI.openError(DefaultMessagesImpl.getString(
                    "ReloadDatabaseAction.checkConnectionFailured", e.getCause().getMessage())); //$NON-NLS-1$
            log.error(e, e);
        } catch (InterruptedException e) {
            log.error(e, e);
        }

    }
}
