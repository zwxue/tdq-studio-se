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
package org.talend.dq.analysis;

import static org.mockito.Mockito.mock;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import junit.framework.Assert;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.schema.SchemaFactory;
import org.talend.dq.analysis.connpool.TdqAnalysisConnectionPool;
import org.talend.utils.sugars.ReturnCode;

/**
 * created by zshen on Apr 2, 2013 Detailled comment
 * 
 */
@PrepareForTest({ TdqAnalysisConnectionPool.class, Messages.class })
public class ConnectionAnalysisCreationTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    // private static final DomainFactory DOMAIN = DomainFactory.eINSTANCE;
    @Before
    public void setUp() throws Exception {

        ResourceBundle rb = mock(ResourceBundle.class);
        stub(method(ResourceBundle.class, "getBundle", String.class)).toReturn(rb);
        PowerMockito.mock(Messages.class);
        stub(method(Messages.class, "getString", String.class)).toReturn("String");
    }

    @Test
    public void testCreateConnectionAnalysis() {
        Connection connection = createConnection("conn1", null, false); //$NON-NLS-1$
        Assert.assertNotNull("No datamanager found!", connection);

        // initialized analysis
        AnalysisBuilder analysisBuilder = new AnalysisBuilder();
        String analysisName = "My Connection analysis"; //$NON-NLS-1$

        boolean analysisInitialized = analysisBuilder.initializeAnalysis(analysisName, AnalysisType.CONNECTION);
        Assert.assertTrue(analysisName + " failed to initialize!", analysisInitialized); //$NON-NLS-1$

        analysisBuilder.setAnalysisConnection(connection);
        Indicator indicator = SchemaFactory.eINSTANCE.createConnectionIndicator();
        IndicatorDefinition createIndicatorDefinition = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        indicator.setIndicatorDefinition(createIndicatorDefinition);
        indicator.setAnalyzedElement(connection);
        analysisBuilder.addElementToAnalyze(connection, indicator);

        // get the domain constraint
        Domain dataFilter = Mockito.mock(Domain.class);
        analysisBuilder.addFilterOnData(dataFilter);

        // mock connection
        java.sql.Connection mockSqlConn = Mockito.mock(java.sql.Connection.class);
        ResultSet mockSchemaResults = Mockito.mock(ResultSet.class);
        // ~connection

        Analysis analysis = analysisBuilder.getAnalysis();
        // mock connectionPool
        PowerMockito.mockStatic(TdqAnalysisConnectionPool.class);
        TdqAnalysisConnectionPool connPool = Mockito.mock(TdqAnalysisConnectionPool.class);
        try {
            Mockito.when(connPool.getConnection()).thenReturn(mockSqlConn);
        } catch (SQLException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        Mockito.when(TdqAnalysisConnectionPool.getConnectionPool(analysis)).thenReturn(connPool);
        // ~connectionPool

        // run analysis

        ConnectionAnalysisExecutor exec = Mockito.spy(new ConnectionAnalysisExecutor());
        Mockito.doReturn(true).when(exec).check(analysis);
        ReturnCode executed = exec.execute(analysis);
        Assert.assertTrue("Problem executing analysis: '" + analysisName + "': " + executed.getMessage(), executed.isOk());

        // factory.create(createDatabaseConnectionItem, createPath, false);
    }

    private Connection createConnection(String name, IFolder folder, Boolean isDelete) {
        IPath createPath = Path.EMPTY;
        if (folder != null) {
            createPath = new Path(folder.getFullPath().lastSegment());
        }
        // connection
        DatabaseConnection createConnection = ConnectionFactory.eINSTANCE.createDatabaseConnection();
        createConnection.setName(name);
        createConnection.setUsername("UserName"); //$NON-NLS-1$
        createConnection.setPassword("Password"); //$NON-NLS-1$
        createConnection.setURL("URL"); //$NON-NLS-1$
        createConnection.setDatabaseType(EDatabaseTypeName.MYSQL.getXmlName());
        createConnection.setContextMode(true);
        // ~connection

        return createConnection;
    }

}
