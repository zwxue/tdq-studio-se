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
package org.talend.dataprofiler.core.ui.exchange;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.ecos.jobs.ComponentSearcher;
import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class ExchangeFolderRepNode extends RepositoryNode {

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
        try {
            if (timeoutFlag) {
                String version = CorePlugin.getDefault().getProductVersion().toString();
                result = ComponentSearcher.getAvailableCategory(version).toArray();
            } else {
                result = new String[] { "Connection failed: time out" };
            }
        } catch (SocketTimeoutException e) {
            timeoutFlag = false;
            result = new String[] { "Connection failed:" + e.getMessage() };

        } catch (Exception e) {
            timeoutFlag = false;
            result = new String[] { e.getMessage() };
        }
        return buildRepositoryNode(result);
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
                    ExchangeCategoryRepNode exchangeCategoryRepNode = new ExchangeCategoryRepNode((IEcosCategory) obj,
                            this, ENodeType.REPOSITORY_ELEMENT);
                    exchangeCategoryRepNode.setFlag(true);
                    exchangeCategoryRepNode.setMsg("");
                    list.add(exchangeCategoryRepNode);
                }
            }
        }
        return list;
    }
}
