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
package org.talend.dataprofiler.core.ui.perspective;

import static org.talend.dataprofiler.core.PluginConstant.CHEAT_SHEET_VIEW;
import static org.talend.dataprofiler.core.PluginConstant.PERSPECTIVE_ID;
import static org.talend.dataprofiler.core.PluginConstant.SE_ID;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.sqlexplorer.EDriverName;
import net.sourceforge.sqlexplorer.ExplorerException;
import net.sourceforge.sqlexplorer.dbproduct.Alias;
import net.sourceforge.sqlexplorer.dbproduct.AliasManager;
import net.sourceforge.sqlexplorer.dbproduct.ManagedDriver;
import net.sourceforge.sqlexplorer.dbproduct.User;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * Changes the active perspective. <br/>
 * 
 * $Id: ChangePerspectiveAction.java 1774 2007-02-03 02:05:47 +0000 (Sat, 03 Feb 2007) bqian $
 * 
 */
public class ChangePerspectiveAction extends Action {

    private static Logger log = Logger.getLogger(ChangePerspectiveAction.class);

    private static final String SWITCH_TO_DATA_PROFILING = "Switch to " + PluginConstant.DATAPROFILER_PERSPECTIVE; //$NON-NLS-1$

    private static final String SWITCH_TO_DATA_DISCOVERY = "Switch to " + PluginConstant.DATAEXPLORER_PERSPECTIVE; //$NON-NLS-1$

    static ChangePerspectiveAction action;

    IPerspectiveRegistry registry = PlatformUI.getWorkbench().getPerspectiveRegistry();

    /** Id of the perspective to move to front. */
    private String perspectiveId;

    private boolean toolbar;

    /**
     * Constructs a new ChangePerspectiveAction.
     */
    public ChangePerspectiveAction(String perspectiveId) {
        super(perspectiveId, AS_CHECK_BOX);
        this.perspectiveId = perspectiveId;
    }

    /**
     * DOC qzhang ChangePerspectiveAction constructor comment.
     */
    public ChangePerspectiveAction(boolean toolbar) {
        super(""); //$NON-NLS-1$
        this.toolbar = toolbar;
        action = this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();

        if (!perspectiveId.equals(page.getPerspective().getId())) {
            try {
                workbench.showPerspective(perspectiveId, workbench.getActiveWorkbenchWindow());
            } catch (WorkbenchException e) {
                IStatus status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, IStatus.OK, "Show perspective failed.", e); //$NON-NLS-1$
                CorePlugin.getDefault().getLog().log(status);
            }
        }
        activeData();
        IPreferenceStore preferenceStore = CorePlugin.getDefault().getPreferenceStore();
        IViewPart findView = page.findView(CHEAT_SHEET_VIEW);
        if (PERSPECTIVE_ID.equals(perspectiveId)) {
            if (preferenceStore.getBoolean(CHEAT_SHEET_VIEW)) {
                try {
                    page.showView(CHEAT_SHEET_VIEW);
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
            action.perspectiveId = SE_ID;
            action.setToolTipText(SWITCH_TO_DATA_DISCOVERY);
            IPerspectiveDescriptor fp = registry.findPerspectiveWithId(SE_ID);
            if (fp != null) {
                action.setImageDescriptor(fp.getImageDescriptor());
            }
        } else {
            preferenceStore.setValue(CHEAT_SHEET_VIEW, findView != null);
            if (findView != null) {
                page.hideView(findView);
            }
            action.perspectiveId = PERSPECTIVE_ID;
            action.setToolTipText(SWITCH_TO_DATA_PROFILING);
            IPerspectiveDescriptor fp = registry.findPerspectiveWithId(PERSPECTIVE_ID);
            if (fp != null) {
                action.setImageDescriptor(fp.getImageDescriptor());
            }
        }
    }

    /**
     * DOC qzhang Comment method "activeData".
     */
    public void activeData() {
        // PTODO qzhang switch to DB Discovery
        if (SE_ID.equals(perspectiveId)) {
            IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.METADATA).getFolder(
                    DQStructureManager.DB_CONNECTIONS);
            List<TdDataProvider> listTdDataProviders = DqRepositoryViewService.listTdDataProviders(folder, true);
            SQLExplorerPlugin default1 = SQLExplorerPlugin.getDefault();
            AliasManager aliasManager = default1.getAliasManager();
            aliasManager.getAliases().clear();
            Set<User> users = new HashSet<User>();
            try {
                aliasManager.closeAllConnections();
            } catch (ExplorerException e1) {
                log.error(e1, e1);
            }
            for (TdDataProvider tdDataProvider : listTdDataProviders) {
                try {
                    TypedReturnCode<TdProviderConnection> tdPc = DataProviderHelper.getTdProviderConnection(tdDataProvider);
                    TdProviderConnection providerConnection = tdPc.getObject();
                    String url = providerConnection.getConnectionString();
                    Alias alias = new Alias(tdDataProvider.getName());
                    String user = TaggedValueHelper.getValue("user", providerConnection); //$NON-NLS-1$
                    // MOD scorreia 2009-01-09 password decryption
                    String password = DataProviderHelper.getClearTextPassword(providerConnection);
                    User previousUser = new User(user, password);
                    alias.setDefaultUser(previousUser);
                    alias.setAutoLogon(false);
                    alias.setConnectAtStartup(true);
                    alias.setUrl(url);
                    ManagedDriver manDr = default1.getDriverModel().getDriver(
                            EDriverName.getId(providerConnection.getDriverClassName()));
                    alias.setDriver(manDr);
                    aliasManager.addAlias(alias);
                    users.add(previousUser);
                } catch (ExplorerException e) {
                    log.error(e, e);
                }
            }
            aliasManager.modelChanged();
            // SQLExplorerPlugin.getDefault().getPluginPreferences().setValue(IConstants.AUTO_OPEN_EDITOR, false);
            // for (User user : users) {
            // OpenPasswordConnectDialogAction openDlgAction = new OpenPasswordConnectDialogAction(user.getAlias(),
            // user, false);
            // openDlgAction.run();
            // }
        }
    }

    /**
     * Getter for action.
     * 
     * @return the action
     */
    public static ChangePerspectiveAction getAction() {
        return action;
    }

    /**
     * DOC qzhang Comment method "switchTitle".
     */
    public void switchTitle() {
        String id2 = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId();
        String toID = SE_ID;
        if (SE_ID.equals(id2)) {
            toID = PERSPECTIVE_ID;
            setToolTipText(SWITCH_TO_DATA_PROFILING);
        } else {
            setToolTipText(SWITCH_TO_DATA_DISCOVERY);
            toID = SE_ID;
        }
        this.perspectiveId = toID;
        IPerspectiveDescriptor fp = registry.findPerspectiveWithId(toID);
        if (fp != null) {
            setImageDescriptor(fp.getImageDescriptor());
        }
    }
}
