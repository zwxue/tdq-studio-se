// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.utils;

import org.eclipse.core.runtime.Assert;
import org.junit.Test;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisFactory;

/**
 * created by xqliu on 2013-10-28 Detailled comment
 * 
 */
public class AnalysisUtilsTest {

    /**
     * Test method for
     * {@link org.talend.dataprofiler.core.ui.utils.AnalysisUtils#deleteConnectionDependency(org.talend.dataquality.analysis.Analysis)}
     * .
     */
    @Test
    public void testDeleteConnectionDependency() {
        Analysis analysis = AnalysisFactory.eINSTANCE.createAnalysis();
        AnalysisContext analysisContext = AnalysisFactory.eINSTANCE.createAnalysisContext();
        Connection connection = ConnectionFactory.eINSTANCE.createConnection();

        analysis.setContext(analysisContext);
        analysisContext.setConnection(connection);

        // if the connection's supplier dependency's size is 0, should remove the analysis context's connection also
        AnalysisUtils.deleteConnectionDependency(analysis);
        Assert.isTrue(analysis.getContext().getConnection() == null);
        Assert.isTrue(analysis.getClientDependency().isEmpty());
    }

}
