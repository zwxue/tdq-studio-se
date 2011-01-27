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
package org.talend.dataprofiler.core.helper;

import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.DelimitedFileIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.model.XmlElementIndicator;
import org.talend.dataprofiler.core.model.impl.ColumnIndicatorImpl;
import org.talend.dataprofiler.core.model.impl.DelimitedFileIndicatorImpl;
import org.talend.dataprofiler.core.model.impl.XmlElementIndicatorImpl;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;


/**
 * DOC xqliu  class global comment. Detailled comment
 */
public final class ModelElementIndicatorHelper {

    private ModelElementIndicatorHelper() {
    }

    public static final ModelElementIndicator createModelElementIndicator(RepositoryNode node) {
        if (node != null) {
            if (node.getObject() instanceof MetadataColumnRepositoryObject) {
                return createColumnIndicator(node);
            } else if (node.getObject() instanceof MetadataXmlElementTypeRepositoryObject) {
                return createXmlElementIndicator(node);
            }
        }
        return null;
    }

    public static final ColumnIndicator createColumnIndicator(IRepositoryNode repositoryNode) {
        return new ColumnIndicatorImpl(repositoryNode);
    }

    public static final XmlElementIndicator createXmlElementIndicator(IRepositoryNode reposObj) {
        return new XmlElementIndicatorImpl(reposObj);
    }

    public static final DelimitedFileIndicator createDFColumnIndicator(IRepositoryNode reposObj) {
        return new DelimitedFileIndicatorImpl(reposObj);
    }

    public static final ColumnIndicator switchColumnIndicator(IndicatorUnit indicatorUnit) {
        if (indicatorUnit.isColumn()) {
            return (ColumnIndicator) indicatorUnit.getModelElementIndicator();
        }
        return null;
    }

    public static final ColumnIndicator switchColumnIndicator(ModelElementIndicator indicator) {
        if (indicator instanceof ColumnIndicator) {
            return (ColumnIndicator) indicator;
        }
        return null;
    }

    public static final XmlElementIndicator switchXmlElementIndicator(IndicatorUnit indicatorUnit) {
        if (indicatorUnit.isXmlElement()) {
            return (XmlElementIndicator) indicatorUnit.getModelElementIndicator();
        }
        return null;
    }

    public static final XmlElementIndicator switchXmlElementIndicator(ModelElementIndicator indicator) {
        if (indicator instanceof XmlElementIndicator) {
            return (XmlElementIndicator) indicator;
        }
        return null;
    }

    public static final Connection getTdDataProvider(ModelElementIndicator indicator) {
        Property property = indicator.getModelElementRepositoryNode().getObject().getProperty();
        if (property != null && property.getItem() instanceof ConnectionItem) {
            return ((ConnectionItem) property.getItem()).getConnection();
        }
        return null;
    }

}
