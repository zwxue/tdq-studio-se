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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import org.eclipse.core.resources.IFile;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
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
            case RULES:
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

    /**
     * DOC bZhou Comment method "createDeletionHandle".
     * 
     * @param property
     * @return
     */
    public static IDeletionHandle createDeletionHandle(Property property) {
        IDeletionHandle handle = null;

        Item item = property.getItem();

        if (item != null) {
            EResourceConstant typedConstant = EResourceConstant.getTypedConstant(item);

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
                case REPORTS:
                case PATTERNS:
                case RULES:
                case INDICATORS:
                    handle = new EMFResourceHandle(property);
                    break;
                case FOLDER:
                    handle = new FolderHandle(property);
                    break;

                default:
                    break;
                }
            }
        }

        return handle;
    }
}
