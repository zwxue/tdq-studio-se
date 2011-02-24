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
package org.talend.dataprofiler.core.ui.action.provider;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.talend.dataprofiler.core.service.AbstractSvnRepositoryService;
import org.talend.dataprofiler.core.service.GlobalServiceRegister;

/**
 * 
 * DOC mzhao Abstract common action provider.
 */
public class AbstractCommonActionProvider extends CommonActionProvider {

    protected static final String NEW_MENU_NAME = "column.analysis.menu"; //$NON-NLS-1$

    public boolean isShowMenu() {
        // MOD mzhao user readonly role on svn repository mode.
        AbstractSvnRepositoryService svnReposService = GlobalServiceRegister.getDefault().getSvnRepositoryService(
                AbstractSvnRepositoryService.class);
        if (svnReposService != null && svnReposService.isReadonly()) {
            return false;
        }
        return true;
    }

    /**
     * DOC bZhou Comment method "getSubMenuManager".
     * 
     * @param subMenuId
     * @return
     */
    public IMenuManager getSubMenuManager(IMenuManager topMenu, String subMenuId) {
        if (topMenu != null) {
            for (IContributionItem item : topMenu.getItems()) {
                if (StringUtils.equals(item.getId(), subMenuId)) {
                    return (IMenuManager) item;
                }
            }
        }

        return null;
    }
}
