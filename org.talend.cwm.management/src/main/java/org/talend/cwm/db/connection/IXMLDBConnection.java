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
package org.talend.cwm.db.connection;

import java.util.Collection;

import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.xml.TdXMLDocument;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC mzhao class global comment. Detailled comment
 */
public interface IXMLDBConnection {

    public ReturnCode checkDatabaseConnection();

    public Collection<TdXMLDocument> createConnection();

    public void setSofewareSystem(TdDataProvider dataProvider, DBConnectionParameter parameter);

    public void setProviderConnection(TdDataProvider dataProvider, DBConnectionParameter parameter);
}
