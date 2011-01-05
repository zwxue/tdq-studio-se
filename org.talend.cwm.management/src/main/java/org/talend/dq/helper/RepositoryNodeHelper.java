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
package org.talend.dq.helper;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.repository.model.RepositoryNode;

/**
 * DOC xqliu  class global comment. Detailled comment
 */
public final class RepositoryNodeHelper {

    private RepositoryNodeHelper() {
    }

    /**
     * DOC xqliu Comment method "getPath".
     * 
     * @param node
     * @return
     */
    public static IPath getPath(RepositoryNode node) {
        if (node == null) {
            return null;
        }
        if (node.isBin()) {
            return new Path(""); //$NON-NLS-1$
        }
        if (node.getType() == null) {
            return null;
        }
        switch (node.getType()) {
        case SYSTEM_FOLDER:
            ERepositoryObjectType contentType = node.getContentType();
            if (contentType == null) {
                Item item = node.getObject().getProperty().getItem();
                contentType = ERepositoryObjectType.getItemType(item);
            }
            return new Path(ERepositoryObjectType.getFolderName(contentType)); //$NON-NLS-1$
        case SIMPLE_FOLDER:
            String label = node.getObject().getProperty().getLabel();
            return getPath(node.getParent()).append(label);
        default:
        }
        return getPath(node.getParent());
    }
}
