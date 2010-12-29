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
package org.talend.dataprofiler.core.ui.views.nodes;

import java.util.List;

import org.apache.log4j.Logger;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.FolderItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC klliu class global comment. Detailled comment
 */
public class SysIndicatorSubFolderRepNode extends RepositoryNode {

    private static Logger log = Logger.getLogger(IndicatorFolderRepNode.class);
    /**
     * DOC klliu SysIndicatorSubFolderRepNode constructor comment.
     * 
     * @param object
     * @param parent
     * @param type
     */
    public SysIndicatorSubFolderRepNode(IRepositoryViewObject object, RepositoryNode parent, ENodeType type) {
        super(object, parent, type);
        // TODO Auto-generated constructor stub
    }

    @Override
    public List<IRepositoryNode> getChildren() {
        RepositoryNode parent = new RepositoryNode(null, null, null);
        ERepositoryObjectType contentType = this.getContentType();
        if (contentType != null) {
            try {
                List<IRepositoryViewObject> all = ProxyRepositoryFactory.getInstance().getAll(getContentType());
                for (IRepositoryViewObject reposViewObj : all) {
                    Item item = reposViewObj.getProperty().getItem();

                    if ((item instanceof FolderItem)) {
                        SysIndicatorSubFolderRepNode childNodeFolder = new SysIndicatorSubFolderRepNode(reposViewObj, parent,
                                ENodeType.SIMPLE_FOLDER);
                        reposViewObj.setRepositoryNode(childNodeFolder);
                        parent.getChildren().add(childNodeFolder);
                    } else {
                        IndicatorDefinitionRepNode repNode = new IndicatorDefinitionRepNode(reposViewObj, parent,
                                ENodeType.REPOSITORY_ELEMENT);
                        reposViewObj.setRepositoryNode(repNode);
                        parent.getChildren().add(repNode);
                    }
                }
            } catch (PersistenceException e) {
                log.error(e, e);
            }
        }
        return parent.getChildren();
    }
}
