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

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;
import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.BenfordLawFrequencyIndicator;
import org.talend.dq.dbms.DbmsLanguage;
import org.talend.dq.dbms.DbmsLanguageFactory;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

/**
 * DOC yyin class global comment. Detailled comment
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DbmsLanguageFactory.class, Messages.class, IndicatorEnum.class, DataExplorer.class })
public class BenfordLawFrequencyExplorerTest {

    BenfordLawFrequencyExplorer benExp;

    DbmsLanguage mockDbLanguage;

    BenfordLawFrequencyIndicator benfordIndicator;

    /**
     * DOC yyin Comment method "setUp".
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        DataExplorerTestHelper.initDataExplorer();
        benExp = new BenfordLawFrequencyExplorer();

        mockDbLanguage = mock(DbmsLanguage.class);
        when(mockDbLanguage.like()).thenReturn(" like ");
        stub(method(DbmsLanguageFactory.class, "createDbmsLanguage", DataManager.class)).toReturn(mockDbLanguage);

        benfordIndicator = mock(BenfordLawFrequencyIndicator.class);
        when(benfordIndicator.eClass()).thenReturn(null);
        Analysis analysis = DataExplorerTestHelper.getAnalysis(benfordIndicator, mockDbLanguage);
        benExp.setAnalysis(analysis);

        setValueToDrillDown("1");
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
     * Test method for {@link org.talend.dq.analysis.explore.BenfordLawFrequencyExplorer#getInstantiatedClause()}.
     * normal case: for default DB
     */
    @Test
    public void testGetInstantiatedClause_1() {
        when(mockDbLanguage.getDbmsName()).thenReturn("SQL");
        when(mockDbLanguage.castColumnNameToChar(anyString())).thenReturn("firstName");

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals("firstName like '1%'", clause);
    }

    /**
     * test for Teradata db type
     */
    @Test
    public void testGetInstantiatedClause_2() {
        when(mockDbLanguage.getDbmsName()).thenReturn("Teradata");
        when(mockDbLanguage.castColumnNameToChar(anyString())).thenReturn("cast(firstName as char)");

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals("cast(firstName as char) like '1%'", clause);
    }

    /**
     * test for postgrsql db type
     */
    @Test
    public void testGetInstantiatedClause_3() {
        when(mockDbLanguage.getDbmsName()).thenReturn("PostgreSQL");
        when(mockDbLanguage.castColumnNameToChar(anyString())).thenReturn("cast(firstName as char)");

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals("cast(firstName as char) like '1%'", clause);
    }

    /**
     * test for sybase db type
     */
    @Test
    public void testGetInstantiatedClause_4() {
        when(mockDbLanguage.getDbmsName()).thenReturn("Adaptive Server Enterprise | Sybase Adaptive Server IQ");
        when(mockDbLanguage.castColumnNameToChar(anyString())).thenReturn("convert(char(15),firstName)");

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals("convert(char(15),firstName) like '1%'", clause);
    }

    /**
     * test for Oracle
     */
    @Test
    public void testGetInstantiatedClause_5() {
        when(mockDbLanguage.getDbmsName()).thenReturn("Oracle");
        when(mockDbLanguage.castColumnNameToChar(anyString())).thenReturn("firstName");

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals("firstName like '1%'", clause);
    }

    /**
     * test for Informix
     */
    @Test
    public void testGetInstantiatedClause_6() {
        when(mockDbLanguage.getDbmsName()).thenReturn("Informix");
        when(mockDbLanguage.castColumnNameToChar(anyString())).thenReturn("firstName");

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals(" SUBSTR(firstName,0,1) like '1%'", clause);
    }

    /**
     * test for Vertica
     */
    @Test
    public void testGetInstantiatedClauseVertica() {
        when(mockDbLanguage.getDbmsName()).thenReturn("Vertica");
        when(mockDbLanguage.castColumnNameToChar(anyString())).thenReturn("to_char(firstName)");

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals("to_char(firstName) like '1%'", clause);
    }

    private void setValueToDrillDown(String value) {
        ChartDataEntity entity = mock(ChartDataEntity.class);
        when(entity.getIndicator()).thenReturn(benfordIndicator);
        PowerMockito.mockStatic(IndicatorEnum.class);
        when(IndicatorEnum.findIndicatorEnum(benfordIndicator.eClass())).thenReturn(
                IndicatorEnum.BenfordLawFrequencyIndicatorEnum);

        // mock the column name is "firstName"
        stub(method(DataExplorer.class, "getAnalyzedElementName", BenfordLawFrequencyIndicator.class)).toReturn("firstName"); //$NON-NLS-1$ //$NON-NLS-2$

        benExp.setEnitty(entity);
        when(entity.getKey()).thenReturn(value);
        when(entity.isLabelNull()).thenReturn(false);
    }

}
