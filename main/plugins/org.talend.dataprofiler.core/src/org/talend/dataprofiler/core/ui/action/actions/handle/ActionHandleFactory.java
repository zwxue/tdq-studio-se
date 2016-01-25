// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions.handle;

import org.eclipse.core.resources.IFile;
import org.talend.core.model.properties.Property;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.EResourceConstant;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public final class ActionHandleFactory {

    private ActionHandleFactory() {
        // nothing to do.
    }

    /**
     * DOC bZhou Comment method "createJrxmlHandle".
     * 
     * @param file
     * @return
     */
    public static JrxmlHandle createJrxmlHandle(IFile file) {
        return new JrxmlHandle(file);
    }

    /**
     * DOC bZhou Comment method "createDuplicateHandle".
     * 
     * @param property
     * @return
     * @deprecated use createDuplicateHandle(IRepositoryNode) instead
     */
    public static IDuplicateHandle createDuplicateHandle(Property property) {
        IDuplicateHandle handle = null;

        EResourceConstant typedConstant = EResourceConstant.getTypedConstant(property.getItem());

        if (typedConstant == null) {
            handle = new SimpleHandle(property);
        } else {
            switch (typedConstant) {
            case DB_CONNECTIONS:
                handle = new ConnectionHandle(property);
                break;
            case MDM_CONNECTIONS:
                handle = new XMLDataProviderHandle(property);
                break;
            case JRXML_TEMPLATE:
                handle = new JrxmlHandle(property);
                break;
            case ANALYSIS:
                handle = new AnalysisHandle(property);
                break;
            case REPORTS:
                handle = new ReportHandle(property);
                break;
            case PATTERNS:
            case RULES_SQL:
                handle = new EMFResourceHandle(property);
                break;
            case INDICATORS:
                handle = new UDIHandle(property);
                break;

            default:
                break;
            }
        }

        return handle;
    }

    public static IDuplicateHandle createDuplicateHandle(IRepositoryNode node) {
        IDuplicateHandle handle = null;

        EResourceConstant typedConstant = EResourceConstant.getTypedConstant(node.getObject().getProperty().getItem());

        if (typedConstant == null) {
            handle = new SimpleHandle(node);
        } else {
            switch (typedConstant) {
            case DB_CONNECTIONS:
                handle = new ConnectionHandle(node);
                break;
            case MDM_CONNECTIONS:
                handle = new XMLDataProviderHandle(node);
                break;
            case JRXML_TEMPLATE:
                handle = new JrxmlHandle(node);
                break;
            case ANALYSIS:
                handle = new AnalysisHandle(node);
                break;
            case REPORTS:
                handle = new ReportHandle(node);
                break;
            case PATTERNS:
            case RULES_PARSER:
            case RULES_SQL:
                handle = new EMFResourceHandle(node);
                break;
            case INDICATORS:
                handle = new UDIHandle(node);
                break;
            case SOURCE_FILES:
                handle = new SimpleHandle(node);
                break;

            default:
                break;
            }
        }

        return handle;
    }

}
