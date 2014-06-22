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
package org.talend.dq.analysis;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.builder.database.dburl.SupportDBUrlType;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.RelationalPackage;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.util.RelationalSwitch;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC msjian class global comment. Detailled comment
 */
// @RunWith(PowerMockRunner.class)
@PrepareForTest({ SupportDBUrlType.class, Logger.class, SwitchHelpers.class, PackageHelper.class, AnalysisHelper.class,
        DbmsLanguageFactory.class })
public class ColumnAnalysisExecutorTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * Test method for
     * {@link org.talend.dq.analysis.ColumnAnalysisExecutor#createSqlStatement(org.talend.dataquality.analysis.Analysis)}
     * .
     */
    @Test
    public void testCreateSqlStatement() {

        Analysis analysis = mock(Analysis.class);
        AnalysisContext context = mock(AnalysisContext.class);
        when(analysis.getContext()).thenReturn(context);
        EList<ModelElement> modelElementList = mock(EList.class);
        TdColumn tdColumn = mock(TdColumn.class);
        modelElementList.add(tdColumn);

        Iterator<ModelElement> iterator = mock(Iterator.class);
        when(modelElementList.iterator()).thenReturn(iterator);
        when(iterator.hasNext()).thenReturn(true).thenReturn(false);
        when(iterator.next()).thenReturn(tdColumn);

        TdTable tdTable = mock(TdTable.class);
        when(tdColumn.getOwner()).thenReturn(tdTable);
        when(tdColumn.getName()).thenReturn("columnName"); // $NON-NLS-1$

        AnalysisParameters analysisPara = mock(AnalysisParameters.class);
        when(analysis.getParameters()).thenReturn(analysisPara);
        when(analysisPara.isStoreData()).thenReturn(false);

        when(context.getAnalysedElements()).thenReturn(modelElementList);

        PowerMockito.mockStatic(SwitchHelpers.class);
        RelationalSwitch<TdColumn> relationSwitch = mock(RelationalSwitch.class);

        RelationalPackage eContainer = RelationalPackage.eINSTANCE;
        EClass eclass = mock(EClass.class);
        when(tdColumn.eClass()).thenReturn(eclass);
        when(eclass.eContainer()).thenReturn(eContainer);
        when(eclass.getClassifierID()).thenReturn(RelationalPackage.TD_COLUMN);
        List<EClass> eSuperTypes = mock(EList.class);
        when(eclass.getESuperTypes()).thenReturn((EList<EClass>) eSuperTypes);
        when(eSuperTypes.isEmpty()).thenReturn(false);
        when(eSuperTypes.get(0)).thenReturn(eclass);
        when(relationSwitch.doSwitch(tdColumn)).thenReturn(tdColumn);

        RelationalSwitch<ColumnSet> relationSwitchColumnSet = mock(RelationalSwitch.class);
        ColumnSet colSet = mock(ColumnSet.class);
        EClass eclassColSet = mock(EClass.class);
        when(tdTable.eClass()).thenReturn(eclassColSet);

        RelationalPackage eContainerColSet = RelationalPackage.eINSTANCE;
        when(eclassColSet.eContainer()).thenReturn(eContainerColSet);
        when(eclassColSet.getClassifierID()).thenReturn(RelationalPackage.TD_TABLE);
        when(relationSwitchColumnSet.doSwitch(tdTable)).thenReturn(colSet);

        Package parentRelation = mock(Schema.class);
        PowerMockito.mockStatic(PackageHelper.class);
        when(PackageHelper.getParentPackage((MetadataTable) tdTable)).thenReturn(parentRelation);
        
        when(tdTable.getName()).thenReturn("tableName"); // $NON-NLS-1$
        
        AnalysisResult analysisResult = mock(AnalysisResult.class);
        when(analysis.getResults()).thenReturn(analysisResult);
        ExecutionInformations info = mock(ExecutionInformations.class);
        when(analysisResult.getResultMetadata()).thenReturn(info);
        
        PowerMockito.mockStatic(AnalysisHelper.class);
        when(AnalysisHelper.getStringDataFilter(analysis)).thenReturn("data filter"); // $NON-NLS-1$

        DataManager dataManager = mock(org.talend.core.model.metadata.builder.connection.Connection.class);
        when(context.getConnection()).thenReturn(dataManager);
        
        DbmsLanguage dbmsLanguage = DbmsLanguageFactory.createDbmsLanguage("Microsoft SQL Server", "9.0"); // $NON-NLS-1$
                                                                                                           // $NON-NLS-2$
        dbmsLanguage.setDbQuoteString("\""); // $NON-NLS-1$
        PowerMockito.mockStatic(DbmsLanguageFactory.class);
        when(DbmsLanguageFactory.createDbmsLanguage(dataManager)).thenReturn(dbmsLanguage);
        
        Package catalog = mock(Catalog.class);
        when(PackageHelper.getParentPackage(parentRelation)).thenReturn(catalog);
        when(catalog.getName()).thenReturn("catalogName"); // $NON-NLS-1$
        
        ColumnAnalysisExecutor columnAnalysisExecutor = new ColumnAnalysisExecutor();
        assertEquals("SELECT \"columnName\" FROM \"catalogName\".\"dbo\".\"tableName\" WHERE data filter", // $NON-NLS-1$
                columnAnalysisExecutor.createSqlStatement(analysis));

    }

}
