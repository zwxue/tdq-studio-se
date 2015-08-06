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
package org.talend.dq.analysis.explore;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;
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
 * DOC yyin  class global comment. Detailled comment
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ DbmsLanguageFactory.class, Messages.class, IndicatorEnum.class })
public class BenfordLawFrequencyExplorerTest {

    BenfordLawFrequencyExplorer benExp;

    DbmsLanguage mockDbLanguage;
    /**
     * DOC yyin Comment method "setUp".
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        DataExplorerTestHelper.initDataExplorer();
        benExp = new BenfordLawFrequencyExplorer();

        mockDbLanguage = mock(DbmsLanguage.class);
        when(mockDbLanguage.like()).thenReturn(" like ");
        stub(method(DbmsLanguageFactory.class, "createDbmsLanguage", DataManager.class)).toReturn(mockDbLanguage);
        BenfordLawFrequencyIndicator indicator = mock(BenfordLawFrequencyIndicator.class);
        when(indicator.eClass()).thenReturn(null);
        Analysis analysis = DataExplorerTestHelper.getAnalysis(indicator, mockDbLanguage);
        benExp.setAnalysis(analysis);

        ChartDataEntity entity = mock(ChartDataEntity.class);
        when(entity.getIndicator()).thenReturn(indicator);
        PowerMockito.mockStatic(IndicatorEnum.class);
        when(IndicatorEnum.findIndicatorEnum(indicator.eClass())).thenReturn(IndicatorEnum.BenfordLawFrequencyIndicatorEnum);

        benExp.setEnitty(entity);
        when(entity.getKey()).thenReturn("1");
        when(entity.isLabelNull()).thenReturn(false);
    }

    /**
     * DOC yyin Comment method "tearDown".
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

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals(" like '1%'", clause);
    }

    /**
     * test for Teradata db type
     */
    @Test
    public void testGetInstantiatedClause_2() {
        when(mockDbLanguage.getDbmsName()).thenReturn("Teradata");

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals("cast( as char) like '1%'", clause);
    }

    /**
     * test for postgrsql db type
     */
    @Test
    public void testGetInstantiatedClause_3() {
        when(mockDbLanguage.getDbmsName()).thenReturn("PostgreSQL");

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals("cast( as char) like '1%'", clause);
    }

    /**
     * test for sybase db type
     */
    @Test
    public void testGetInstantiatedClause_4() {
        when(mockDbLanguage.getDbmsName()).thenReturn("Adaptive Server Enterprise | Sybase Adaptive Server IQ");

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals("convert(char(15),) like '1%'", clause);
    }

    /**
     * test for Oracle
     */
    @Test
    public void testGetInstantiatedClause_5() {
        when(mockDbLanguage.getDbmsName()).thenReturn("Oracle");

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals(" like '1%'", clause);
    }

    /**
     * test for Informix
     */
    @Test
    public void testGetInstantiatedClause_6() {
        when(mockDbLanguage.getDbmsName()).thenReturn("Informix");

        String clause = benExp.getInstantiatedClause();
        Assert.assertEquals(" SUBSTR(  ,0,1) like '1%'", clause);
    }

}
