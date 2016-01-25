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
package org.talend.cwm.compare.factory.comparisonlevel;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.metamodel.impl.DiffFactoryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.impl.RelationalFactoryImpl;
import org.talend.dq.nodes.DBColumnFolderRepNode;
import org.talend.dq.writer.EMFSharedResources;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.ForeignKey;
import orgomg.cwm.resource.relational.PrimaryKey;

/**
 * FIXME implement the empty methods.
 * 
 */
@PrepareForTest({ EMFSharedResources.class })
public class TableViewComparisonLevelTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    @Test
    @Ignore
    public void testReloadCurrentLevelElement() {

    }

    @Test
    @Ignore
    public void testCreateTempConnectionFile() {

    }

    @Test
    @Ignore
    public void testGetSavedReloadObject() {

    }

    @Test
    @Ignore
    public void testGetRightResource() {

    }

    @Test
    @Ignore
    public void testFindDataProvider() {

    }

    @Test
    @Ignore
    public void testCompareWithReloadObject() {

    }

    @Test
    @Ignore
    public void testGetLeftResource() {

    }

    @Test
    @Ignore
    public void testHandleRemoveElement() {

    }

    @Test
    public void testHandleAddElement() {
        // //mock ModelElementChangeRightTarget
        // ModelElementChangeRightTarget mockModelElementChange=Mockito.mock(ModelElementChangeRightTarget.class);
        // Mockito.when(mockModelElementChange.getRightElement()).thenReturn(EDatabaseTypeName.TERADATA.getXmlName());
        // //~mock

        RelationalFactory einstance = RelationalFactoryImpl.eINSTANCE;
        // TdColumn
        TdColumn createTdColumn1 = einstance.createTdColumn();
        TdColumn createTdColumn2 = einstance.createTdColumn();
        TdColumn createTdColumn3 = einstance.createTdColumn();
        TdColumn createTdColumn4 = einstance.createTdColumn();
        // ~TdColumn

        // TdTable
        ColumnSet createTdTable = einstance.createTdTable();
        createTdTable.getFeature().add(createTdColumn2);

        // ~TdTable

        // PrimaryKey
        PrimaryKey createPrimaryKey = orgomg.cwm.resource.relational.impl.RelationalFactoryImpl.eINSTANCE.createPrimaryKey();
        createPrimaryKey.setName("PrimaryKey");//$NON-NLS-1$
        createPrimaryKey.getFeature().add(createTdColumn2);
        createTdTable.getOwnedElement().add(createPrimaryKey);
        createTdColumn2.getUniqueKey().add(createPrimaryKey);

        PrimaryKey createPrimaryKey2 = orgomg.cwm.resource.relational.impl.RelationalFactoryImpl.eINSTANCE.createPrimaryKey();
        createPrimaryKey2.setName("PrimaryKey");//$NON-NLS-1$
        createPrimaryKey2.getFeature().add(createTdColumn3);
        createPrimaryKey2.getFeature().add(createTdColumn4);
        createPrimaryKey2.getFeature().add(createTdColumn1);

        createTdColumn1.getUniqueKey().add(createPrimaryKey2);
        createTdColumn3.getUniqueKey().add(createPrimaryKey2);
        createTdColumn3.getUniqueKey().add(createPrimaryKey2);

        // ~PrimaryKey

        // ForeginKey
        Set<ForeignKey> foreginKeySet = new HashSet<ForeignKey>();
        ForeignKey createForeignKey1 = orgomg.cwm.resource.relational.impl.RelationalFactoryImpl.eINSTANCE.createForeignKey();
        createForeignKey1.setName("foreginKey");//$NON-NLS-1$
        createForeignKey1.getFeature().add(createTdColumn2);
        createTdColumn2.getKeyRelationship().add(createForeignKey1);
        createTdTable.getOwnedElement().add(createForeignKey1);
        // ForeignKey createForeignKey2 =
        // orgomg.cwm.resource.relational.impl.RelationalFactoryImpl.eINSTANCE.createForeignKey();
        foreginKeySet.add(createForeignKey1);
        // foreginKeySet.add(createForeignKey2);
        ForeignKey createForeignKey2 = orgomg.cwm.resource.relational.impl.RelationalFactoryImpl.eINSTANCE.createForeignKey();
        createForeignKey2.setName("foreginKey");//$NON-NLS-1$

        createForeignKey2.getFeature().add(createTdColumn1);
        createForeignKey2.getFeature().add(createTdColumn3);
        createForeignKey2.getFeature().add(createTdColumn4);

        createTdColumn1.getKeyRelationship().add(createForeignKey2);
        createTdColumn3.getKeyRelationship().add(createForeignKey2);
        createTdColumn3.getKeyRelationship().add(createForeignKey2);
        // ~ForeginKey

        // mock DBColumnFolderRepNode
        DBColumnFolderRepNode columnFolderRepNodeMock = Mockito.mock(DBColumnFolderRepNode.class);
        Mockito.when(columnFolderRepNodeMock.getColumnSet()).thenReturn(createTdTable);
        // ~mock

        ModelElementChangeRightTarget createModelElementChangeRightTarget = DiffFactoryImpl.eINSTANCE
                .createModelElementChangeRightTarget();
        createModelElementChangeRightTarget.setRightElement(createTdColumn1);

        // PowerMock TableHelper
        // PowerMockito.mockStatic(TableHelper.class);
        // Mockito.when(TableHelper.addPrimaryKey((TdTable)createTdTable, createTdColumn)).thenReturn(createPrimaryKey);
        // Mockito.when(TableHelper.addForeignKey((TdTable)createTdTable,
        // createForeignKey1)).thenReturn(createForeignKey2);
        // ~TableHelper
        // PowerMock ColumnHelper
        // PowerMockito.mockStatic(ColumnHelper.class);
        // Mockito.when(ColumnHelper.getForeignKey(createTdColumn1)).thenReturn(foreginKeySet);
        // ~ColumnHelper

        // PowerMockit EMFSharedResources
        EMFSharedResources resourcesMock = Mockito.mock(EMFSharedResources.class);
        PowerMockito.mockStatic(EMFSharedResources.class);
        Mockito.when(EMFSharedResources.getInstance()).thenReturn(resourcesMock);
        Mockito.when(resourcesMock.saveResource((Resource) Mockito.any())).thenReturn(true);
        // ~EMFSharedResources

        // new CoreRuntimePlugin().start(BundleContextImpl.this);
        TableViewComparisonLevel tableViewComparisonLevel = new TableViewComparisonLevel(createTdTable);
        tableViewComparisonLevel.handleAddElement(createModelElementChangeRightTarget);

        // Mockito.verify(resourcesMock).saveResource((Resource)Mockito.any());

        // Mockito.verify(sqlConn).getMetaData();

        Mockito.verifyZeroInteractions(columnFolderRepNodeMock, resourcesMock);
        PrimaryKey primaryKey = (PrimaryKey) createTdTable.getOwnedElement().get(0);
        ForeignKey foreginKey = (ForeignKey) createTdTable.getOwnedElement().get(1);
        assertNotNull(primaryKey);
        assertNotNull(foreginKey);
        assertTrue(primaryKey.getFeature().size() == 2);
        assertTrue(foreginKey.getFeature().size() == 2);
    }

    @Test
    @Ignore
    public void testTableViewComparisonLevelDBColumnFolderRepNode() {

    }

    @Test
    @Ignore
    public void testTableViewComparisonLevelColumnSet() {

    }

}
