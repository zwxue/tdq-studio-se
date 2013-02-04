// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.util.MetadataConnectionUtils;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.compare.factory.ComparisonLevelFactory;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.compare.i18n.Messages;
import org.talend.cwm.compare.ui.ImageLib;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.WorkbenchUtils;
import org.talend.dq.CWMPlugin;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.nodes.foldernode.IConnectionElementSubFolder;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ReloadDatabaseAction extends Action {

    private static Logger log = Logger.getLogger(ReloadDatabaseAction.class);

    private static final String ANALYSIS_EDITOR_ID = "org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor"; //$NON-NLS-1$

    private Object selectedObject;

    private ReturnCode returnCode = new ReturnCode(true);

    /**
     * Getter for returnCode.
     * 
     * @return the returnCode
     */
    public ReturnCode getReturnCode() {
        return this.returnCode;
    }

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
        returnCode = new ReturnCode(true);
        if (!isSupport()) {
            returnCode.setReturnCode(Messages.getString("ReloadDatabaseAction.NotSupportMessage"), false); //$NON-NLS-1$
            return;
        }
        // Added yyin 20130131 TDQ-6780, warn the user to check the compare result before copy
        boolean isContinue = isContinueReload();
        if (!isContinue) {
            // go to compare instead of reloading now
            new PopComparisonUIAction(selectedObject, Messages.getString("ReloadDatabaseAction.CompareLabel")).run();//$NON-NLS-1$
            returnCode.setReturnCode(Messages.getString("ReloadDatabaseAction.IsContinue"), false);//$NON-NLS-1$
            return;
        }// ~

        Connection conn = getConnection();
        List<ModelElement> dependencyClients = EObjectHelper.getDependencyClients(conn);
        if (!(dependencyClients == null || dependencyClients.isEmpty())) {
            int isOk = DeleteModelElementConfirmDialog.showElementImpactConfirmDialog(null, new ModelElement[] { conn },
                    DefaultMessagesImpl.getString("TOPRepositoryService.dependcyTile"), //$NON-NLS-1$
                    DefaultMessagesImpl.getString("TOPRepositoryService.dependcyMessage", conn.getLabel())); //$NON-NLS-1$
            if (isOk != Dialog.OK) {
                returnCode.setReturnCode("The user canceled the operation!", false); //$NON-NLS-1$
                return;
            }
        }
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                final IComparisonLevel creatComparisonLevel = ComparisonLevelFactory.creatComparisonLevel(selectedObject);
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {
                        try {

                            Connection oldDataProvider = creatComparisonLevel.reloadCurrentLevelElement();

                            // MOD mzhao 2009-07-13 bug 7454 Impact existing
                            // analysis.
                            Property property = PropertyHelper.getProperty(oldDataProvider);
                            if (property != null) {
                                Item newItem = property.getItem();
                                if (newItem != null) {
                                    CWMPlugin.getDefault()
                                            .updateConnetionAliasByName(oldDataProvider, oldDataProvider.getLabel());
                                }
                            }
                            // MOD qiongli 2011-9-8,move method 'impactExistingAnalyses(...)' to class WorkbenchUtils
                            WorkbenchUtils.impactExistingAnalyses(oldDataProvider);
                        } catch (ReloadCompareException e) {
                            log.error(e, e);
                            returnCode.setReturnCode(e.getMessage(), false);
                        } catch (PartInitException e) {
                            log.error(e, e);
                            returnCode.setReturnCode(e.getMessage(), false);
                        }

                    }
                });
            }

        };
        try {
            ProgressUI.popProgressDialog(op);
            CorePlugin.getDefault().refreshDQView(selectedObject);
        } catch (InvocationTargetException e) {
            MessageUI.openError(Messages.getString("ReloadDatabaseAction.checkConnectionFailured", e.getCause().getMessage())); //$NON-NLS-1$
            log.error(e, e);
        } catch (InterruptedException e) {
            log.error(e, e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#isEnabled()
     */

    private boolean isSupport() {
        Connection conn = getConnection();
        if (!(conn instanceof DatabaseConnection)) {
            return false;
        }
        if (!MetadataConnectionUtils.isTDQSupportDBTemplate(conn)) {
            MessageDialog.openInformation(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
                    Messages.getString("ReloadDatabaseAction.NotSupportTitle"), //$NON-NLS-1$
                    Messages.getString("ReloadDatabaseAction.NotSupportMessage")); //$NON-NLS-1$
            return false;
        }
        return true;
    }

    private Connection getConnection() {
        Connection conn = null;
        IRepositoryViewObject object = null;
        if (selectedObject == null) {
            return conn;
        }
        if (selectedObject instanceof RepositoryNode) {
            object = ((RepositoryNode) selectedObject).getObject();
        }
        if (object != null) {
            Item item = object.getProperty().getItem();
            if (item instanceof ConnectionItem) {
                conn = ((ConnectionItem) item).getConnection();
            }
        } else if (selectedObject instanceof IConnectionElementSubFolder) {
            conn = ((IConnectionElementSubFolder) selectedObject).getConnection();
        } else if (selectedObject instanceof Connection) {
            conn = (Connection) selectedObject;
        }
        return conn;
    }

    /**
     * popup a dialog to warn the user better do the compare before the reload, and provide two buttons: compare, reload
     * if the user click the compare button, the compare will be executed. if the user click the reload button, the
     * reload will continue.
     * 
     * @return true if the user select reload, false: when select compare
     */
    private boolean isContinueReload() {
        String[] dialogButtonLabels = {
                Messages.getString("ReloadDatabaseAction.CompareLabel"), Messages.getString("ReloadDatabaseAction.ReloadLabel") };//$NON-NLS-1$
        MessageDialog dialog = new MessageDialog(CorePlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
                "Reload", null, Messages.getString("ReloadDatabaseAction.IsContinue"), 3, dialogButtonLabels, SWT.NONE);//$NON-NLS-1$
        return dialog.open() == 1;
    }
}
