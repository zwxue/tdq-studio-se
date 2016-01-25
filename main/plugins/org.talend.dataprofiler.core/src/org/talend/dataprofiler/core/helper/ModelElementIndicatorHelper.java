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
package org.talend.dataprofiler.core.helper;

import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.DelimitedFileIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.model.XmlElementIndicator;
import org.talend.dataprofiler.core.model.impl.ColumnIndicatorImpl;
import org.talend.dataprofiler.core.model.impl.DelimitedFileIndicatorImpl;
import org.talend.dataprofiler.core.model.impl.XmlElementIndicatorImpl;
import org.talend.dataprofiler.core.ui.editor.preview.ColumnIndicatorUnit;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.DFColumnRepNode;
import org.talend.dq.nodes.MDMXmlElementRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.utils.sql.TalendTypeConvert;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class ModelElementIndicatorHelper {

    private ModelElementIndicatorHelper() {
    }

    public static final ModelElementIndicator createModelElementIndicator(IRepositoryNode node) {
        if (node != null) {
            if (node instanceof DBColumnRepNode) {
                return createColumnIndicator(node);
            } else if (node instanceof MDMXmlElementRepNode) {
                return createXmlElementIndicator(node);
            } else if (node instanceof DFColumnRepNode) {
                return createDFColumnIndicator(node);
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

    /**
     * 
     * 
     * @deprecated
     * 
     * use {@link #switchColumnIndicator(ColumnIndicatorUnit)} instead of it
     * @param indicatorUnit
     * @return
     */
    @Deprecated
    public static final ColumnIndicator switchColumnIndicator(IndicatorUnit indicatorUnit) {
        if (indicatorUnit instanceof ColumnIndicatorUnit) {
            return switchColumnIndicator((ColumnIndicatorUnit) indicatorUnit);
        }
        return null;
    }

    /**
     * 
     * get ColumnIndicator from columnIndicatorUnit
     * 
     * @param indicatorUnit
     * @return
     */
    public static final ColumnIndicator switchColumnIndicator(ColumnIndicatorUnit indicatorUnit) {
        if (indicatorUnit.isColumn()) {
            return (ColumnIndicator) indicatorUnit.getModelElementIndicator();
        }
        return null;
    }

    /**
     * 
     * get ColumnIndicator from ModelElementIndicator
     * 
     * @param indicatorUnit
     * @return
     */
    public static final ColumnIndicator switchColumnIndicator(ModelElementIndicator indicator) {
        if (indicator instanceof ColumnIndicator) {
            return (ColumnIndicator) indicator;
        }
        return null;
    }

    /**
     * 
     * use {@link #switchXmlElementIndicator(ColumnIndicatorUnit)} instead of it
     * 
     * @deprecated
     * @param indicatorUnit
     * @return
     */
    @Deprecated
    public static final XmlElementIndicator switchXmlElementIndicator(IndicatorUnit indicatorUnit) {
        if (indicatorUnit instanceof ColumnIndicatorUnit) {
            return switchXmlElementIndicator((ColumnIndicatorUnit) indicatorUnit);
        }
        return null;
    }

    /**
     * 
     * get XmlElementIndicator from ColumnIndicatorUnit
     * 
     * @param indicatorUnit
     * @return
     */
    public static final XmlElementIndicator switchXmlElementIndicator(ColumnIndicatorUnit indicatorUnit) {
        if (indicatorUnit.isXmlElement()) {
            return (XmlElementIndicator) indicatorUnit.getModelElementIndicator();
        }
        return null;
    }

    /**
     * 
     * get XmlElementIndicator from ModelElementIndicator
     * 
     * @param indicator
     * @return
     */
    public static final XmlElementIndicator switchXmlElementIndicator(ModelElementIndicator indicator) {
        if (indicator instanceof XmlElementIndicator) {
            return (XmlElementIndicator) indicator;
        }
        return null;
    }

    /**
     * 
     * get Connection from ModelElementIndicator
     * 
     * @param indicator
     * @return
     */
    public static final Connection getTdDataProvider(ModelElementIndicator indicator) {
        Property property = indicator.getModelElementRepositoryNode().getObject().getProperty();
        if (property != null && property.getItem() instanceof ConnectionItem) {
            return ((ConnectionItem) property.getItem()).getConnection();
        }
        return null;
    }

    /**
     * DOC xqliu Comment method "getModelElementDisplayName".
     * 
     * @param meIndicator
     * @return
     */
    public static final String getModelElementDisplayName(ModelElementIndicator meIndicator) {
        String meName = meIndicator.getElementName();
        String typeName = "";//$NON-NLS-1$
        if (meIndicator instanceof ColumnIndicator) {
            // MOD scorreia 2010-10-20 bug 16403 avoid NPE here
            TdSqlDataType sqlDataType = ((ColumnIndicator) meIndicator).getTdColumn().getSqlDataType();
            typeName = sqlDataType != null ? sqlDataType.getName() : "unknown";//$NON-NLS-1$
        } else if (meIndicator instanceof XmlElementIndicator) {
            typeName = ((MDMXmlElementRepNode) meIndicator.getModelElementRepositoryNode()).getTdXmlElementType().getJavaType();
        } else if (meIndicator instanceof DelimitedFileIndicatorImpl) {
            MetadataColumn mColumn = ((DelimitedFileIndicatorImpl) meIndicator).getMetadataColumn();
            typeName = TalendTypeConvert.convertToJavaType(mColumn.getTalendType());
        }
        return meName != null ? meName + PluginConstant.SPACE_STRING + PluginConstant.PARENTHESIS_LEFT + typeName
                + PluginConstant.PARENTHESIS_RIGHT : "null";//$NON-NLS-1$
    }
}
