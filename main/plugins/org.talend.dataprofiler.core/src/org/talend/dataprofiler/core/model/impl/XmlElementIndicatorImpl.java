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
package org.talend.dataprofiler.core.model.impl;

import org.talend.core.repository.model.repositoryObject.MetadataXmlElementTypeRepositoryObject;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.dataprofiler.core.model.XmlElementIndicator;
import org.talend.repository.model.IRepositoryNode;
import org.talend.utils.sql.TalendTypeConvert;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class XmlElementIndicatorImpl extends ModelElementIndicatorImpl implements XmlElementIndicator {

    public XmlElementIndicatorImpl(IRepositoryNode repositoryNode) {
        super();
        this.setModelElement(repositoryNode);
    }

    public TdXmlElementType getXmlElementType() {
        return ((MetadataXmlElementTypeRepositoryObject) this.getModelElementRepositoryNode().getObject()).getTdXmlElementType();
    }

    @Override
    public int getJavaType() {

        return TalendTypeConvert.convertToJDBCType(getXmlElementType().getJavaType());
    }
}
