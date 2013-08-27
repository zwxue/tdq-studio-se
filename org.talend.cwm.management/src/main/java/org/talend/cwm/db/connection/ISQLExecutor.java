// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.sql.SQLException;
import java.util.List;

import org.talend.dataquality.analysis.Analysis;

/**
 * DOC yyin  class global comment. Detailled comment
 */
public interface ISQLExecutor {

    public List<Object[]> executeQuery(Analysis analysis) throws SQLException;

    void setLimit(int limit);
}
