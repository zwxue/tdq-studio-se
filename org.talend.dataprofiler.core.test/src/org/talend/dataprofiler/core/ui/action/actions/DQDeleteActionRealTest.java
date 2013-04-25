// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.jfree.util.Log;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.ConnectionFactory;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.dataprofiler.core.helper.UnitTestBuildHelper;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dataquality.indicators.CountsIndicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.nodes.AnalysisRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.repository.localprovider.model.LocalRepositoryFactory;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;

/**
 * created by zshen on Apr 11, 2013 Detailled comment
 * 
 */
public class DQDeleteActionRealTest {

    private static LocalRepositoryFactory repositoryFactory;

    ProxyRepositoryFactory factory = ProxyRepositoryFactory.getInstance();

    private static Project originalProject;

    DQDeleteAction dqDeleteAction_real = null;

    @BeforeClass
    public static void beforeAllTests() {
        Context ctx = CoreRuntimePlugin.getInstance().getContext();
        RepositoryContext repositoryContext = (RepositoryContext) ctx.getProperty(Context.REPOSITORY_CONTEXT_KEY);
        if (repositoryContext != null) {
            originalProject = repositoryContext.getProject();
        }
        repositoryFactory = new LocalRepositoryFactory();
    }

    @AfterClass
    public static void afterAllTests() {
        repositoryFactory = null;
        if (originalProject != null) {
            Context ctx = CoreRuntimePlugin.getInstance().getContext();
            RepositoryContext repositoryContext = (RepositoryContext) ctx.getProperty(Context.REPOSITORY_CONTEXT_KEY);
            repositoryContext.setProject(originalProject);
        }
    }

    /**
     * DOC zshen Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        UnitTestBuildHelper.deleteCurrentProject("testForDeleteActionTDQ"); //$NON-NLS-1$
        UnitTestBuildHelper.createRealProject("testForDeleteActionTDQ"); //$NON-NLS-1$
        dqDeleteAction_real = PowerMockito.spy(new DQDeleteAction());
        PowerMockito.doReturn(true).when(dqDeleteAction_real, "showConfirmDialog"); //$NON-NLS-1$
        PowerMockito.doReturn(null).when(dqDeleteAction_real).getActivePage();
        // Mockito.when(dqDeleteAction_real.showConfirmDialog()).thenReturn(true);
    }

    @After
    public void tearDown() throws Exception {
        UnitTestBuildHelper.deleteCurrentProject();
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.action.actions.DQDeleteAction#run()}.
     * 
     * case 1: set special node to delete for logic delete
     */
    @Test
    public void testRun() {

        // connectionNode
        DatabaseConnectionItem createConnectionItem = createConnectionItem("conn1", null, false);
        RepositoryNode dbParentRepNode = createConnectionNode(createConnectionItem);

        // ~connectionNode

        // analysisNode
        TDQAnalysisItem createAnaItem = createAnalysisItem("ana1", null, false);
        RepositoryNode anaRepNode = createAnalysisNode(createAnaItem);
        // ~analysisNode

        ISelection selection = new StructuredSelection(dbParentRepNode);
        Mockito.doReturn(selection).when(dqDeleteAction_real).getSelection();
        dqDeleteAction_real.setCurrentNode(dbParentRepNode);
        // logic delete
        dqDeleteAction_real.run();
        Assert.assertTrue(createConnectionItem.getState().isDeleted());
        IFile file = ResourcesPlugin.getWorkspace().getRoot()
                .getFile(new Path(createConnectionItem.getConnection().eResource().getURI().toPlatformString(false)));
        Assert.assertTrue(file.exists());
        // physical delete
        dqDeleteAction_real.run();
        Assert.assertFalse(file.exists());

        selection = new StructuredSelection(anaRepNode);
        Mockito.doReturn(selection).when(dqDeleteAction_real).getSelection();
        dqDeleteAction_real.setCurrentNode(anaRepNode);
        // logic delete
        dqDeleteAction_real.run();
        Assert.assertTrue(createAnaItem.getState().isDeleted());
        file = ResourcesPlugin.getWorkspace().getRoot()
                .getFile(new Path(createAnaItem.getAnalysis().eResource().getURI().toPlatformString(false)));
        Assert.assertTrue(file.exists());
        // physical delete
        dqDeleteAction_real.run();
        Assert.assertFalse(file.exists());
    }

    /**
     * DOC zshen Comment method "createAnalysisNode".
     * 
     * @param createAnaItem
     * @return
     */
    private RepositoryNode createAnalysisNode(TDQAnalysisItem createAnaItem) {
        IRepositoryViewObject analysisViewObject = null;
        try {
            analysisViewObject = factory.getLastVersion(createAnaItem.getProperty().getId());
        } catch (PersistenceException e) {
            Log.error(e, e);
            Assert.fail(e.getMessage());
        }

        RepositoryNode dbParentRepNode = new AnalysisRepNode(analysisViewObject, null, ENodeType.REPOSITORY_ELEMENT);
        return dbParentRepNode;
    }

    /**
     * DOC zshen Comment method "createAnalysisItem".
     * 
     * @param string
     * @param object
     * @param boolean
     * @return
     */
    private TDQAnalysisItem createAnalysisItem(String name, IFolder folder, boolean isDelete) {
        IPath createPath = Path.EMPTY;
        if (folder != null) {
            createPath = new Path(folder.getFullPath().lastSegment());
        }
        // connection
        Analysis createAnalysis = AnalysisFactory.eINSTANCE.createAnalysis();
        createAnalysis.setName(name);

        // ~connection
        TDQAnalysisItem createAnalysisItem = org.talend.dataquality.properties.PropertiesFactory.eINSTANCE
                .createTDQAnalysisItem();

        org.talend.core.model.properties.Property createAnaProperty = PropertiesFactory.eINSTANCE.createProperty();
        createAnaProperty.setId(EcoreUtil.generateUUID());
        createAnaProperty.setItem(createAnalysisItem);
        createAnaProperty.setLabel(createAnalysis.getName());
        createAnalysisItem.setProperty(createAnaProperty);
        createAnalysisItem.setAnalysis(createAnalysis);

        // Indicator
        CountsIndicator createCountsIndicator = IndicatorsFactory.eINSTANCE.createCountsIndicator();
        List<IRepositoryViewObject> all = null;
        IndicatorDefinition createIndicatorDefinition = null;
        try {
            all = factory.getAll(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS);
            for (IRepositoryViewObject indicatorViewObject : all) {
                if (indicatorViewObject.getLabel().equalsIgnoreCase("ROW COUNT")) {
                    createIndicatorDefinition = (IndicatorDefinition) PropertyHelper.getModelElement(indicatorViewObject
                            .getProperty());
                    break;
                }
            }
        } catch (PersistenceException e1) {
            e1.printStackTrace();
            Assert.fail(e1.getMessage());
        }
        createCountsIndicator.setIndicatorDefinition(createIndicatorDefinition);

        // CountsIndicator createCountsIndicator = IndicatorsFactory.eINSTANCE.createCountsIndicator();
        // IndicatorDefinition createIndicatorDefinition = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        // createCountsIndicator.setIndicatorDefinition(createIndicatorDefinition);
        // createCountsIndicator.setAnalyzedElement(createColumn);
        // ~Indicator
        // Analysis
        AnalysisParameters createAnalysisParameters = AnalysisFactory.eINSTANCE.createAnalysisParameters();
        AnalysisContext createAnalysisContext = AnalysisFactory.eINSTANCE.createAnalysisContext();
        // createAnalysisContext.getAnalysedElements().add(createColumn);
        createAnalysis.setContext(createAnalysisContext);
        AnalysisResult createAnalysisResult = AnalysisFactory.eINSTANCE.createAnalysisResult();
        ExecutionInformations createExecutionInformations = AnalysisFactory.eINSTANCE.createExecutionInformations();
        createAnalysisResult.setResultMetadata(createExecutionInformations);
        createAnalysisResult.getIndicators().add(createCountsIndicator);
        createAnalysis.setResults(createAnalysisResult);
        createAnalysis.setParameters(createAnalysisParameters);

        try {
            factory.create(createAnalysisItem, createPath, false);
        } catch (PersistenceException e) {
            Log.error(e, e);
            Assert.fail(e.getMessage());
        }
        return createAnalysisItem;
    }

    /**
     * DOC zshen Comment method "createConnectionNode".
     * 
     * @return
     */
    private RepositoryNode createConnectionNode(DatabaseConnectionItem createConnectionItem) {
        IRepositoryViewObject connectionViewObject = null;
        try {
            connectionViewObject = factory.getLastVersion(createConnectionItem.getProperty().getId());
        } catch (PersistenceException e) {
            Log.error(e, e);
            Assert.fail(e.getMessage());
        }

        RepositoryNode dbParentRepNode = new DBConnectionRepNode(connectionViewObject, null, ENodeType.REPOSITORY_ELEMENT);
        return dbParentRepNode;
    }

    private DatabaseConnectionItem createConnectionItem(String name, IFolder folder, Boolean isDelete) {
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
        DatabaseConnectionItem createDatabaseConnectionItem = PropertiesFactory.eINSTANCE.createDatabaseConnectionItem();

        org.talend.core.model.properties.Property createDatabaseConnectionProperty = PropertiesFactory.eINSTANCE.createProperty();
        createDatabaseConnectionProperty.setId(EcoreUtil.generateUUID());
        createDatabaseConnectionProperty.setItem(createDatabaseConnectionItem);
        createDatabaseConnectionProperty.setLabel(createConnection.getName());
        createDatabaseConnectionItem.setProperty(createDatabaseConnectionProperty);
        createDatabaseConnectionItem.setConnection(createConnection);
        try {
            factory.create(createDatabaseConnectionItem, createPath, false);
        } catch (PersistenceException e) {
            Log.error(e, e);
            Assert.fail(e.getMessage());
        }
        return createDatabaseConnectionItem;
    }
}
