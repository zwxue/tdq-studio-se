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
package org.talend.dataprofiler.core.ui.action.actions;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dq.helper.DQConnectionReposViewObjDelegator;
import org.talend.repository.model.ProxyRepositoryFactory;

/**
 * 
 * DOC mzhao Delete repository object feature:14891
 */
public class DeleteRepositoryObjectAction extends Action {

    private static Logger log = Logger.getLogger(DeleteRepositoryObjectAction.class);
    private IRepositoryViewObject reposViewObj;

    private boolean isDeleteForever;
    public DeleteRepositoryObjectAction(boolean isDeleteForever, IRepositoryViewObject reposViewObj) {
        this.reposViewObj = reposViewObj;
        
        if (isDeleteForever) {
            setText(DefaultMessagesImpl.getString("DeleteObjectsAction.deleteForever")); //$NON-NLS-1$
        } else {
            setText(DefaultMessagesImpl.getString("DeleteObjectsAction.logicalDelete")); //$NON-NLS-1$
        }
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.DELETE_ACTION));
        setActionDefinitionId("org.talend.dataprofiler.core.removeAnalysis"); //$NON-NLS-1$
        this.isDeleteForever = isDeleteForever;
    }
    @Override
    public void run() {
        try {
            if (!isDeleteForever) {
                ProxyRepositoryFactory.getInstance().deleteObjectLogical(reposViewObj);
            } else {
                ProxyRepositoryFactory.getInstance().deleteObjectPhysical(reposViewObj);
            }
            DQConnectionReposViewObjDelegator.getInstance().fetchRepositoryViewObjects(Boolean.TRUE);
            CorePlugin.getDefault().refreshDQView();
            CorePlugin.getDefault().refreshWorkSpace();
        } catch (PersistenceException e) {
            log.error(e, e);
        } catch (BusinessException e) {
            log.error(e, e);
        }
    }
}
