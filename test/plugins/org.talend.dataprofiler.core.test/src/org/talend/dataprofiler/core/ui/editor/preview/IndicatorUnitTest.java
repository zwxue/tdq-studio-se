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
package org.talend.dataprofiler.core.ui.editor.preview;

import java.sql.Types;

import junit.framework.Assert;

import org.junit.Test;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.cwm.relational.RelationalFactory;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSqlDataType;
import org.talend.cwm.relational.TdTable;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.model.impl.ColumnIndicatorImpl;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.IRepositoryNode.ENodeType;
import org.talend.repository.model.RepositoryNode;

/**
 * created by talend on Mar 28, 2014 Detailled comment
 * 
 */
public class IndicatorUnitTest {

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit#isExsitingForm()}.
     * 
     * test case 1:ColumnIndicatorUnit
     */
    @Test
    public void testIsExsitingFormCase1() {
        Indicator indicator = IndicatorsFactory.eINSTANCE.createIndicator();
        TdColumn tdColumn = RelationalFactory.eINSTANCE.createTdColumn();
        TdTable createTdTable = RelationalFactory.eINSTANCE.createTdTable();

        tdColumn.setOwner(createTdTable);
        TdSqlDataType dataType = RelationalFactory.eINSTANCE.createTdSqlDataType();
        dataType.setJavaDataType(Types.VARCHAR);
        tdColumn.setSqlDataType(dataType);
        MetadataColumnRepositoryObject columnObject = new MetadataColumnRepositoryObject(null, tdColumn);
        IRepositoryNode columnRepNode = new DBColumnRepNode(columnObject, new RepositoryNode(null, null, null),
                ENodeType.REPOSITORY_ELEMENT);
        ModelElementIndicator modelElementIndicator = new ColumnIndicatorImpl(columnRepNode);
        ColumnIndicatorUnit colUnit = new ColumnIndicatorUnit(IndicatorEnum.RowCountIndicatorEnum, indicator,
                modelElementIndicator);
        boolean exsitingForm = colUnit.isExsitingForm();
        Assert.assertEquals("indicator " + IndicatorEnum.RowCountIndicatorEnum.getLabel() + "is not exist Form enum", true,
                exsitingForm);
//Count UDI case
        IndicatorDefinition createIndicatorDefinition = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        createIndicatorDefinition.getCategories().add(DefinitionHandler.getInstance().getUserDefinedCountIndicatorCategory());
        indicator.setIndicatorDefinition(createIndicatorDefinition);
        colUnit = new ColumnIndicatorUnit(IndicatorEnum.UserDefinedIndicatorEnum, indicator, modelElementIndicator);
        exsitingForm = colUnit.isExsitingForm();
        Assert.assertEquals("indicator " + IndicatorEnum.UserDefinedIndicatorEnum.getLabel()
                + " User Defined Count should not exist Form enum", false, exsitingForm);
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit#isExsitingForm()}.
     * 
     * test case 2:ColumnSetIndicatorUnit
     */
    @Test
    public void testIsExsitingFormCase2() {
        Indicator indicator = IndicatorsFactory.eINSTANCE.createIndicator();
        TdColumn tdColumn = RelationalFactory.eINSTANCE.createTdColumn();
        TdTable createTdTable = RelationalFactory.eINSTANCE.createTdTable();

        tdColumn.setOwner(createTdTable);
        TdSqlDataType dataType = RelationalFactory.eINSTANCE.createTdSqlDataType();
        dataType.setJavaDataType(Types.VARCHAR);
        tdColumn.setSqlDataType(dataType);
        MetadataColumnRepositoryObject columnObject = new MetadataColumnRepositoryObject(null, tdColumn);
        IRepositoryNode columnRepNode = new DBColumnRepNode(columnObject, new RepositoryNode(null, null, null),
                ENodeType.REPOSITORY_ELEMENT);
        ModelElementIndicator modelElementIndicator = new ColumnIndicatorImpl(columnRepNode);

        ColumnSetIndicatorUnit colSetUnit = new ColumnSetIndicatorUnit(IndicatorEnum.RowCountIndicatorEnum, indicator);
        boolean exsitingForm = colSetUnit.isExsitingForm();
        Assert.assertEquals("indicator " + IndicatorEnum.RowCountIndicatorEnum.getLabel() + "is not exist Form enum", true,
                exsitingForm);
    }

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit#isExsitingForm()}.
     * 
     * test case 3:TableIndicatorUnit
     */
    @Test
    public void testIsExsitingFormCase3() {
        Indicator indicator = IndicatorsFactory.eINSTANCE.createIndicator();
        TdColumn tdColumn = RelationalFactory.eINSTANCE.createTdColumn();
        TdTable createTdTable = RelationalFactory.eINSTANCE.createTdTable();

        tdColumn.setOwner(createTdTable);
        TdSqlDataType dataType = RelationalFactory.eINSTANCE.createTdSqlDataType();
        dataType.setJavaDataType(Types.VARCHAR);
        tdColumn.setSqlDataType(dataType);
        MetadataColumnRepositoryObject columnObject = new MetadataColumnRepositoryObject(null, tdColumn);
        IRepositoryNode columnRepNode = new DBColumnRepNode(columnObject, new RepositoryNode(null, null, null),
                ENodeType.REPOSITORY_ELEMENT);
        ModelElementIndicator modelElementIndicator = new ColumnIndicatorImpl(columnRepNode);

        TableIndicatorUnit tableUnit = new TableIndicatorUnit(IndicatorEnum.RowCountIndicatorEnum, indicator, null);
        boolean exsitingForm = tableUnit.isExsitingForm();
        Assert.assertEquals("indicator " + IndicatorEnum.RowCountIndicatorEnum.getLabel() + "is not exist Form enum", true,
                exsitingForm);
        tableUnit = new TableIndicatorUnit(IndicatorEnum.WhereRuleIndicatorEnum, indicator, null);
        exsitingForm = tableUnit.isExsitingForm();
        Assert.assertEquals("indicator " + IndicatorEnum.WhereRuleIndicatorEnum.getLabel() + "is not exist Form enum", true,
                exsitingForm);
    }

}
