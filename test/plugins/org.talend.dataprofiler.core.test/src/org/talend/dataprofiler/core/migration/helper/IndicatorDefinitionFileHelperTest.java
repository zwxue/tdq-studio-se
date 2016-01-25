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
package org.talend.dataprofiler.core.migration.helper;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.junit.Before;
import org.junit.Test;
import org.talend.cwm.relational.TdExpression;
import org.talend.dataquality.helpers.BooleanExpressionHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;

/**
 * created by qiongli on Sep 5, 2012 Detailled comment
 * 
 */
public class IndicatorDefinitionFileHelperTest {

    private IndicatorDefinition indiDefinition;

    @Before
    public void setup() {
        indiDefinition = mock(IndicatorDefinition.class);
        EList<TdExpression> tdExpessionLs = new BasicEList<TdExpression>();
        TdExpression tdExpression1 = BooleanExpressionHelper.createTdExpression("MySql",//$NON-NLS-1$
                "SELECT SUM(CHAR_LENGTH(<%=__COLUMN_NAMES__%>)), COUNT(<%=__COLUMN_NAMES__%>) FROM <%=__TABLE_NAME__%>");//$NON-NLS-1$
        TdExpression tdExpression2 = BooleanExpressionHelper
                .createTdExpression("DB2",//$NON-NLS-1$
                        "SELECT SUM(LENGTH(<%=__COLUMN_NAMES__%>)), COUNT(<%=__COLUMN_NAMES__%>) FROM <%=__TABLE_NAME__%> <%=__WHERE_CLAUSE__%>");//$NON-NLS-1$
        tdExpessionLs.add(tdExpression1);
        tdExpessionLs.add(tdExpression2);
        when(indiDefinition.getSqlGenericExpression()).thenReturn(tdExpessionLs);
    }

    /**
     * Test method for judgeing if exist TdExpression of the specify lanuage. abnormal case
     * {@link org.talend.dataprofiler.core.migration.helper.IndicatorDefinitionFileHelper#isExistSqlExprWithLanguage(org.talend.dataquality.indicators.definition.IndicatorDefinition, java.lang.String)}
     * .
     */
    @Test
    public void testIsExistSqlExprWithLanguage_1() {

        boolean flag = IndicatorDefinitionFileHelper.isExistSqlExprWithLanguage(indiDefinition, "Hive");//$NON-NLS-1$
        assertFalse(flag);

    }

    /**
     * Test method for judgeing if exist TdExpression of the specify lanuage. normal case.
     * {@link org.talend.dataprofiler.core.migration.helper.IndicatorDefinitionFileHelper#isExistSqlExprWithLanguage(org.talend.dataquality.indicators.definition.IndicatorDefinition, java.lang.String)}
     * .
     */
    @Test
    public void testIsExistSqlExprWithLanguage_2() {

        boolean flag = IndicatorDefinitionFileHelper.isExistSqlExprWithLanguage(indiDefinition, "MySql");//$NON-NLS-1$
        assertTrue(flag);

    }

}
