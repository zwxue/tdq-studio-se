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
package org.talend.dataprofiler.core.model.impl;

import org.talend.cwm.xml.TdXMLElement;
import org.talend.dataprofiler.core.model.XmlElementIndicator;


/**
 * DOC xqliu  class global comment. Detailled comment
 */
public class XmlElementIndicatorImpl extends ModelElementIndicatorImpl implements XmlElementIndicator {

    public XmlElementIndicatorImpl(TdXMLElement xmlElement) {
        super();
        this.setModelElement(xmlElement);
    }

    public TdXMLElement getXmlElement() {
        return (TdXMLElement) this.getModelElement();
    }

    public int getJavaType() {
        // TODO 10238
        return super.getJavaType();
    }
}
