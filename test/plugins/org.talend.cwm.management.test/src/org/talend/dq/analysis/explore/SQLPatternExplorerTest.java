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
package org.talend.dq.analysis.explore;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.SqlPatternMatchingIndicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * created by talend on 2015-07-28 Detailled comment.
 *
 */
@PrepareForTest({ DbmsLanguageFactory.class, IndicatorEnum.class, Messages.class })
public class SQLPatternExplorerTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    private static final String RES_INVALIED_ROWS = "SELECT *  FROM `tbi`.`customer`  WHERE (`lname` NOT  LIKE 'su%' OR `lname` IS NULL )"; //$NON-NLS-1$

    private static final String RES_VALIED_ROWS = "SELECT *  FROM `tbi`.`customer`  WHERE `lname` LIKE 'su%'"; //$NON-NLS-1$

    private static final String RES_INVALIED_VALUES = "SELECT `lname` FROM `tbi`.`customer`  WHERE (`lname` NOT  LIKE 'su%' OR `lname` IS NULL )"; //$NON-NLS-1$

    private static final String RES_VALIED_VALUES = "SELECT `lname` FROM `tbi`.`customer`  WHERE `lname` LIKE 'su%'"; //$NON-NLS-1$

    private SQLPatternExplorer sqlPatternExplorer;

    /**
     * DOC msjian Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        DataExplorerTestHelper.initDataExplorer();

        sqlPatternExplorer = new SQLPatternExplorer();

        // mock setEntity
        SqlPatternMatchingIndicator indicator = mock(SqlPatternMatchingIndicator.class);
        when(indicator.eClass()).thenReturn(null);

        ModelElement element = mock(ModelElement.class);
        when(element.getName()).thenReturn("lname"); //$NON-NLS-1$

        when(indicator.getAnalyzedElement()).thenReturn(element);
        indicator.setAnalyzedElement(element);

        DbmsLanguage dbmsLanguage = mock(DbmsLanguage.class);
        when(dbmsLanguage.getRegexPatternString(indicator)).thenReturn("'su%'");//$NON-NLS-1$
        when(dbmsLanguage.quote(anyString())).thenReturn("`lname`"); //$NON-NLS-1$
        when(dbmsLanguage.regexLike(anyString(), anyString())).thenReturn(""); //$NON-NLS-1$
        when(dbmsLanguage.regexNotLike(anyString(), anyString())).thenReturn(""); //$NON-NLS-1$
        when(dbmsLanguage.getFunctionReturnValue()).thenReturn(""); //$NON-NLS-1$ 

        when(dbmsLanguage.where()).thenReturn(" WHERE "); //$NON-NLS-1$
        when(dbmsLanguage.and()).thenReturn(" AND "); //$NON-NLS-1$
        when(dbmsLanguage.from()).thenReturn(" FROM "); //$NON-NLS-1$
        when(dbmsLanguage.or()).thenReturn(" OR "); //$NON-NLS-1$
        when(dbmsLanguage.isNull()).thenReturn(" IS NULL "); //$NON-NLS-1$
        when(dbmsLanguage.not()).thenReturn(" NOT ");//$NON-NLS-1$
        when(dbmsLanguage.like()).thenReturn(" LIKE ");//$NON-NLS-1$

        Analysis analysis = DataExplorerTestHelper.getAnalysis(indicator, dbmsLanguage);
        sqlPatternExplorer.setAnalysis(analysis);

        ChartDataEntity cdEntity = mock(ChartDataEntity.class);
        when(cdEntity.getIndicator()).thenReturn(indicator);

        PowerMockito.mockStatic(IndicatorEnum.class);
        when(IndicatorEnum.findIndicatorEnum(indicator.eClass())).thenReturn(IndicatorEnum.RowCountIndicatorEnum);

        sqlPatternExplorer.setEnitty(cdEntity);

        // for method: getFromClause()
        Expression instantiatedExpression = mock(Expression.class);
        when(dbmsLanguage.getInstantiatedExpression(indicator)).thenReturn(instantiatedExpression);
        when(instantiatedExpression.getBody()).thenReturn(" FROM `tbi`.`customer` "); //$NON-NLS-1$
    }

    /**
     * Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#PatternExplorer()}.
     */
    @Test
    public void testSQLPatternExplorer() {
        try {
            SQLPatternExplorer pe = new SQLPatternExplorer();
            Assert.assertNotNull(pe);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Mock Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getInvalidRowsStatement()}.
     */
    @Test
    public void testGetInvalidRowsStatement() {
        String strStatement = sqlPatternExplorer.getInvalidRowsStatement();
        Assert.assertEquals(RES_INVALIED_ROWS, strStatement);
    }

    /**
     * mock test method
     */
    @Test
    public void testGetValidRowsStatement() {
        String strStatement = sqlPatternExplorer.getValidRowsStatement();
        Assert.assertEquals(RES_VALIED_ROWS, strStatement);
    }

    /**
     * Mock Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getInvalidValuesStatement()}.
     */
    @Test
    public void testGetInvalidValuesStatement() {
        String strStatement = sqlPatternExplorer.getInvalidValuesStatement();
        Assert.assertEquals(RES_INVALIED_VALUES, strStatement);
    }

    /**
     * Mock Test method for {@link org.talend.dq.analysis.explore.PatternExplorer#getValidValuesStatement()}.
     */
    @Test
    public void testGetValidValuesStatement() {
        String strStatement = sqlPatternExplorer.getValidValuesStatement();
        Assert.assertEquals(RES_VALIED_VALUES, strStatement);
    }

}
