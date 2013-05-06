// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.exchange;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.utils.platform.PluginChecker;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.ecos.jobs.ComponentSearcher;
import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.dq.nodes.DQRepositoryNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class ExchangeFolderRepNode extends DQRepositoryNode {

    private boolean timeoutFlag = true;

    /**
     * DOC klliu ExchangeFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public ExchangeFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        Object[] result = null;
        // MOD msjian 2012-5-30 TDQ-4997 :no information about Exchange time out
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        String title = DefaultMessagesImpl.getString("ExchangeFolderRepNode.connectFailedTitle"); //$NON-NLS-1$
        try {
            if (timeoutFlag) {
                String version = CorePlugin.getDefault().getProductVersion().toString();
                result = ComponentSearcher.getAvailableCategory(version).toArray();
            } else {
                MessageDialogWithToggle.openInformation(shell, title,
                        DefaultMessagesImpl.getString("ExchangeFolderRepNode.connectFailed1")); //$NON-NLS-1$
                result = new String[] {};
            }
        } catch (SocketTimeoutException e) {
            timeoutFlag = false;
            MessageDialogWithToggle.openInformation(shell, title, e.getMessage());
            result = new String[] {};
        } catch (Exception e) {
            timeoutFlag = false;
            MessageDialogWithToggle.openInformation(shell, title,
                    DefaultMessagesImpl.getString("ExchangeFolderRepNode.connectFailed2")); //$NON-NLS-1$
            result = new String[] {};
        }
        // TDQ-4997~
        // MOD gdbu 2011-6-29 bug : 22204
        return filterResultsIfAny(buildRepositoryNode(result));
        // ~22204
    }

    /**
     * DOC xqliu Comment method "buildRepositoryNode".
     * 
     * @param result
     * @return
     */
    private List<IRepositoryNode> buildRepositoryNode(Object[] result) {
        List<IRepositoryNode> list = new ArrayList<IRepositoryNode>();
        if (result != null && result.length > 0) {
            if (result[0] instanceof String) {
                for (Object obj : result) {
                    ExchangeCategoryRepNode exchangeCategoryRepNode = new ExchangeCategoryRepNode(null, this,
                            ENodeType.REPOSITORY_ELEMENT);
                    exchangeCategoryRepNode.setFlag(false);
                    exchangeCategoryRepNode.setMsg((String) obj);
                    list.add(exchangeCategoryRepNode);
                }
            } else if (result[0] instanceof IEcosCategory) {
                for (Object obj : result) {
                    // MOD klliu bug TDQ-3754: Hide the parser rules when in TOP 2011-10-24
                    IEcosCategory category = (IEcosCategory) obj;
                    String name = category.getName();
                    ExchangeCategoryRepNode exchangeCategoryRepNode = new ExchangeCategoryRepNode(category, this,
                            ENodeType.REPOSITORY_ELEMENT);
                    exchangeCategoryRepNode.setFlag(true);
                    exchangeCategoryRepNode.setMsg("");//$NON-NLS-1$ 
                    if (!(name.equals("ParserRule") && PluginChecker.isOnlyTopLoaded())) {//$NON-NLS-1$ 
                        list.add(exchangeCategoryRepNode);
                    }
                }
            }
        }
        return list;
    }

}
