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
package org.talend.cwm.compare.factory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.compare.factory.comparisonlevel.CatalogSchemaComparisonLevel;
import org.talend.cwm.compare.factory.comparisonlevel.DataProviderComparisonLevel;
import org.talend.cwm.compare.factory.comparisonlevel.RepositoryObjectComparisonLevel;
import org.talend.cwm.compare.factory.comparisonlevel.TableViewComparisonLevel;
import org.talend.dq.nodes.AnalysisFolderRepNode;
import org.talend.dq.nodes.DBColumnFolderRepNode;
import org.talend.dq.nodes.DBConnectionFolderRepNode;
import org.talend.dq.nodes.DBConnectionRepNode;
import org.talend.dq.nodes.DBTableFolderRepNode;
import org.talend.dq.nodes.DBViewFolderRepNode;
import org.talend.dq.writer.EMFSharedResources;


/**
 * DOC yyin  class global comment. Detailled comment
 */
@PrepareForTest({ EMFSharedResources.class })
public class TestComparisonLevelFactoryTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    IRepositoryViewObject resObject;
    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        resObject = mock(IRepositoryViewObject.class);
        PowerMockito.mockStatic(EMFSharedResources.class);
        when(EMFSharedResources.getInstance()).thenReturn(null);

    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {

    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.factory.ComparisonLevelFactory#creatComparisonLevel(java.lang.Object)}. test for
     * the type of : db connectin rep node
     */
    @Test
    public void testCreatComparisonLevelObject_1() {
        DBConnectionRepNode node = mock(DBConnectionRepNode.class);
        when(node.getObject()).thenReturn(resObject);
        
        IComparisonLevel level = ComparisonLevelFactory.creatComparisonLevel(node);
        Assert.assertTrue(level instanceof RepositoryObjectComparisonLevel);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.factory.ComparisonLevelFactory#creatComparisonLevel(java.lang.Object)}. test for
     * the type of : table folder rep node
     */
    @Test
    public void testCreatComparisonLevelObject_2() {
        DBTableFolderRepNode node = mock(DBTableFolderRepNode.class);
        
        IComparisonLevel level = ComparisonLevelFactory.creatComparisonLevel(node);
        Assert.assertTrue(level instanceof CatalogSchemaComparisonLevel);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.factory.ComparisonLevelFactory#creatComparisonLevel(java.lang.Object)}. test for
     * the type of : db view rep node
     */
    @Test
    public void testCreatComparisonLevelObject_3() {
        DBViewFolderRepNode node = mock(DBViewFolderRepNode.class);
        when(node.getObject()).thenReturn(resObject);
        when(node.getCatalog()).thenReturn(null);
        DBViewFolderRepNode parent = mock(DBViewFolderRepNode.class);
        when(node.getParent()).thenReturn(parent);

        IComparisonLevel level = ComparisonLevelFactory.creatComparisonLevel(node);
        Assert.assertTrue(level instanceof CatalogSchemaComparisonLevel);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.factory.ComparisonLevelFactory#creatComparisonLevel(java.lang.Object)}. test for
     * the type of : db view rep node
     */
    @Test
    public void testCreatComparisonLevelObject_4() {
        DBViewFolderRepNode node = mock(DBViewFolderRepNode.class);
        when(node.getCatalog()).thenReturn(null);
        DBConnectionFolderRepNode parent = mock(DBConnectionFolderRepNode.class);
        when(node.getParent()).thenReturn(parent);
        when(parent.getObject()).thenReturn(resObject);
        
        IComparisonLevel level = ComparisonLevelFactory.creatComparisonLevel(node);
        Assert.assertTrue(level instanceof RepositoryObjectComparisonLevel);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.factory.ComparisonLevelFactory#creatComparisonLevel(java.lang.Object)}. test for
     * the type of : rep node
     */
    @Test
    public void testCreatComparisonLevelObject_5() {
        DBColumnFolderRepNode node = mock(DBColumnFolderRepNode.class);

        IComparisonLevel level = ComparisonLevelFactory.creatComparisonLevel(node);
        Assert.assertTrue(level instanceof TableViewComparisonLevel);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.factory.ComparisonLevelFactory#creatComparisonLevel(java.lang.Object)}. test for
     * the type of : rep node
     */
    @Test
    public void testCreatComparisonLevelObject_6() {
        Connection node = mock(Connection.class);

        IComparisonLevel level = ComparisonLevelFactory.creatComparisonLevel(node);
        Assert.assertTrue(level instanceof DataProviderComparisonLevel);
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.factory.ComparisonLevelFactory#creatComparisonLevel(java.lang.Object)}. test for
     * the type of : null
     */
    @Test
    public void testCreatComparisonLevelObject_7() {
        IComparisonLevel level = ComparisonLevelFactory.creatComparisonLevel(null);
        Assert.assertNull(level);
    }

    // other type
    @Test
    public void testCreatComparisonLevelObject_8() {
        AnalysisFolderRepNode node = mock(AnalysisFolderRepNode.class);

        IComparisonLevel level = ComparisonLevelFactory.creatComparisonLevel(node);
        Assert.assertNull(level);
    }
}
