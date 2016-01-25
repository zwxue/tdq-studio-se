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
package org.talend.cwm.compare;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.emf.compare.FactoryException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdExpression;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import orgomg.cwm.objectmodel.core.TaggedValue;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC yyin class global comment. Detailled comment
 */
public class ModelElementMatchEngineTest {

    private ModelElementMatchEngine matchEngine;

    /**
     * DOC yyin Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        matchEngine = new ModelElementMatchEngine();
    }

    /**
     * DOC yyin Comment method "tearDown".
     * 
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.talend.cwm.compare.ModelElementMatchEngine#isSimilar(org.eclipse.emf.ecore.EObject, org.eclipse.emf.ecore.EObject)}
     * .
     * 
     * @throws FactoryException
     */
    @Test
    public void testIsSimilarEObjectEObject() throws FactoryException {
        // Catalog
        Catalog catalog1 = mock(Catalog.class);
        Catalog catalog2 = mock(Catalog.class);
        when(catalog1.getName()).thenReturn("sameName");
        when(catalog2.getName()).thenReturn("sameName");
        Assert.assertTrue(matchEngine.isSimilar(catalog1, catalog2));
        when(catalog2.getName()).thenReturn("differentName");
        Assert.assertFalse(matchEngine.isSimilar(catalog1, catalog2));

        // Schema
        Schema schema1 = mock(Schema.class);
        Schema schema2 = mock(Schema.class);
        when(schema1.getName()).thenReturn("sameName");
        when(schema2.getName()).thenReturn("sameName");
        Assert.assertTrue(matchEngine.isSimilar(schema1, schema2));
        when(schema2.getName()).thenReturn("differentName");
        Assert.assertFalse(matchEngine.isSimilar(schema1, schema2));

        // TdTable
        TdTable table1 = mock(TdTable.class);
        TdTable table2 = mock(TdTable.class);
        when(table1.getName()).thenReturn("sameName");
        when(table2.getName()).thenReturn("sameName");
        Assert.assertTrue(matchEngine.isSimilar(table1, table2));
        when(table2.getName()).thenReturn("differentName");
        Assert.assertFalse(matchEngine.isSimilar(table1, table2));

        // TdView
        TdView view1 = mock(TdView.class);
        TdView view2 = mock(TdView.class);
        when(view1.getName()).thenReturn("sameName");
        when(view2.getName()).thenReturn("sameName");
        Assert.assertTrue(matchEngine.isSimilar(view1, view2));
        when(view2.getName()).thenReturn("differentName");
        Assert.assertFalse(matchEngine.isSimilar(view1, view2));

        // TdColumn
        TdColumn tdColumn1 = mock(TdColumn.class);
        TdColumn tdColumn2 = mock(TdColumn.class);
        when(tdColumn1.getName()).thenReturn("sameName");
        when(tdColumn2.getName()).thenReturn("sameName");
        TdSqlDataType type = mock(TdSqlDataType.class);
        when(tdColumn1.getSqlDataType()).thenReturn(type);
        when(tdColumn2.getSqlDataType()).thenReturn(type);
        Assert.assertTrue(matchEngine.isSimilar(tdColumn1, tdColumn2));
        when(tdColumn2.getName()).thenReturn("differentName");
        Assert.assertFalse(matchEngine.isSimilar(tdColumn1, tdColumn2));

        // TaggedValue
        TaggedValue tag1 = mock(TaggedValue.class);
        TaggedValue tag2 = mock(TaggedValue.class);
        when(tag1.getTag()).thenReturn("sameName");
        when(tag2.getTag()).thenReturn("sameName");
        when(tag1.getValue()).thenReturn("same");
        when(tag2.getValue()).thenReturn("same");

        Assert.assertTrue(matchEngine.isSimilar(tag1, tag2));
        when(tag2.getValue()).thenReturn("different");
        Assert.assertFalse(matchEngine.isSimilar(tag1, tag2));

        // TdExpression
        TdExpression exp1 = mock(TdExpression.class);
        TdExpression exp2 = mock(TdExpression.class);
        when(exp1.getLanguage()).thenReturn("sameName");
        when(exp2.getLanguage()).thenReturn("sameName");
        when(exp1.getBody()).thenReturn("same");
        when(exp2.getBody()).thenReturn("same");
        Assert.assertTrue(matchEngine.isSimilar(exp1, exp2));
        when(exp2.getBody()).thenReturn("different");
        Assert.assertFalse(matchEngine.isSimilar(exp1, exp2));

        // TdSqlDataType
        TdSqlDataType type1 = mock(TdSqlDataType.class);
        TdSqlDataType type2 = mock(TdSqlDataType.class);
        when(type1.getName()).thenReturn("sameName");
        when(type2.getName()).thenReturn("sameName");
        Assert.assertTrue(matchEngine.isSimilar(type1, type2));
        when(type2.getName()).thenReturn("differentName");
        Assert.assertFalse(matchEngine.isSimilar(type1, type2));

        // MetadataColumn
        MetadataColumn mColumn1 = mock(MetadataColumn.class);
        MetadataColumn mColumn2 = mock(MetadataColumn.class);
        when(mColumn1.getLabel()).thenReturn("sameName");
        when(mColumn2.getLabel()).thenReturn("sameName");
        Assert.assertTrue(matchEngine.isSimilar(mColumn1, mColumn2));
        when(mColumn2.getLabel()).thenReturn("differentName");
        Assert.assertFalse(matchEngine.isSimilar(mColumn1, mColumn2));

        // MetadataTable
        MetadataTable mTable1 = mock(MetadataTable.class);
        MetadataTable mTable2 = mock(MetadataTable.class);
        when(mTable1.getLabel()).thenReturn("sameName");
        when(mTable2.getLabel()).thenReturn("sameName");
        Assert.assertTrue(matchEngine.isSimilar(mTable1, mTable2));
        when(mTable2.getLabel()).thenReturn("differentName");
        Assert.assertFalse(matchEngine.isSimilar(mTable1, mTable2));

        // TdView
        DatabaseConnection con1 = mock(DatabaseConnection.class);
        DatabaseConnection con2 = mock(DatabaseConnection.class);
        Assert.assertTrue(matchEngine.isSimilar(con1, con2));

        //other type
        MDMConnection mcon1 = mock(MDMConnection.class);
        MDMConnection mcon2 = mock(MDMConnection.class);
        Assert.assertTrue(matchEngine.isSimilar(mcon1, mcon2));
    }
}
