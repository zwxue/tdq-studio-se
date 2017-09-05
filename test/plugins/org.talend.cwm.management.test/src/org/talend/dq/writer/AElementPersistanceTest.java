// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.writer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.*;
import static org.powermock.api.support.membermodification.MemberModifier.*;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.repository.model.IRepositoryFactory;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.repository.utils.XmiResourceManager;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.reports.ReportsFactory;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.ReportWriter;
import org.talend.utils.sugars.ReturnCode;

/**
 * created by xqliu on Jul 22, 2013 Detailled comment
 * 
 */
@PrepareForTest({ ProxyRepositoryFactory.class, IRepositoryFactory.class, XmiResourceManager.class })
public class AElementPersistanceTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * Test method for
     * {@link org.talend.dq.writer.AElementPersistance#saveWithDependencies(org.talend.core.model.properties.Item, orgomg.cwm.objectmodel.core.ModelElement)}
     * .
     * 
     * @throws PersistenceException
     */
    @Test
    public void testSaveWithDependenciesAnalysis() throws PersistenceException {
        PowerMockito.mockStatic(ProxyRepositoryFactory.class);
        ProxyRepositoryFactory mockProxyRepositoryFactory = PowerMockito.mock(ProxyRepositoryFactory.class);
        when(ProxyRepositoryFactory.getInstance()).thenReturn(mockProxyRepositoryFactory);
        stub(method(ProxyRepositoryFactory.class, "save", org.talend.core.model.properties.Item.class, boolean.class)); //$NON-NLS-1$

        PowerMockito.mockStatic(IRepositoryFactory.class);
        IRepositoryFactory mockIRepositoryFactory = PowerMockito.mock(IRepositoryFactory.class);
        when(mockProxyRepositoryFactory.getRepositoryFactoryFromProvider()).thenReturn(mockIRepositoryFactory);

        PowerMockito.mockStatic(XmiResourceManager.class);
        when(mockIRepositoryFactory.getResourceManager()).thenReturn(new XmiResourceManager());

        AnalysisWriter createAnalysisWrite = ElementWriterFactory.getInstance().createAnalysisWrite();
        Analysis createAnalysis = AnalysisFactory.eINSTANCE.createAnalysis();
        AnalysisResult createAnalysisResult = AnalysisFactory.eINSTANCE.createAnalysisResult();
        AnalysisContext createAnalysisContext = AnalysisFactory.eINSTANCE.createAnalysisContext();
        TDQAnalysisItem analysisItem = org.talend.dataquality.properties.PropertiesFactory.eINSTANCE.createTDQAnalysisItem();
        analysisItem.setAnalysis(createAnalysis);
        String anaName = "ana1"; //$NON-NLS-1$
        String exceptedFileName = anaName + "_0.1.ana"; //$NON-NLS-1$
        createAnalysis.setName(anaName);
        createAnalysis.setResults(createAnalysisResult);
        createAnalysis.setContext(createAnalysisContext);

        ReturnCode create = createAnalysisWrite.saveWithDependencies(analysisItem, createAnalysis);
        assertTrue(create.isOk());
        Assert.assertEquals(exceptedFileName, analysisItem.getFilename());

        String anaName2 = "ana2"; //$NON-NLS-1$
        String exceptedFileName2 = anaName2 + "_0.1.ana"; //$NON-NLS-1$
        createAnalysis.setName(anaName2);

        create = createAnalysisWrite.saveWithDependencies(analysisItem, createAnalysis);
        assertTrue(create.isOk());
        Assert.assertEquals(exceptedFileName2, analysisItem.getFilename());
    }

    /**
     * Test method for
     * {@link org.talend.dq.writer.AElementPersistance#saveWithoutDependencies(org.talend.core.model.properties.Item, orgomg.cwm.objectmodel.core.ModelElement)}
     * .
     * 
     * @throws PersistenceException
     */
    @Test
    public void testSaveWithoutDependenciesReport() throws PersistenceException {
        PowerMockito.mockStatic(ProxyRepositoryFactory.class);
        ProxyRepositoryFactory mockProxyRepositoryFactory = PowerMockito.mock(ProxyRepositoryFactory.class);
        when(ProxyRepositoryFactory.getInstance()).thenReturn(mockProxyRepositoryFactory);
        stub(method(ProxyRepositoryFactory.class, "save", org.talend.core.model.properties.Item.class, boolean.class)); //$NON-NLS-1$

        PowerMockito.mockStatic(IRepositoryFactory.class);
        IRepositoryFactory mockIRepositoryFactory = PowerMockito.mock(IRepositoryFactory.class);
        when(mockProxyRepositoryFactory.getRepositoryFactoryFromProvider()).thenReturn(mockIRepositoryFactory);

        PowerMockito.mockStatic(XmiResourceManager.class);
        when(mockIRepositoryFactory.getResourceManager()).thenReturn(new XmiResourceManager());

        ReportWriter createReportWriter = ElementWriterFactory.getInstance().createReportWriter();
        TdReport createTdReport = ReportsFactory.eINSTANCE.createTdReport();
        TDQReportItem reportItem = org.talend.dataquality.properties.PropertiesFactory.eINSTANCE.createTDQReportItem();
        reportItem.setReport(createTdReport);

        String repName = "rep1"; //$NON-NLS-1$
        String exceptedFileName = repName + "_0.1.rep"; //$NON-NLS-1$
        createTdReport.setName(repName);

        ReturnCode create = createReportWriter.saveWithoutDependencies(reportItem, createTdReport);
        assertTrue(create.isOk());
        Assert.assertEquals(exceptedFileName, reportItem.getFilename());

        String repName2 = "rep2"; //$NON-NLS-1$
        String exceptedFileName2 = repName2 + "_0.1.rep"; //$NON-NLS-1$
        createTdReport.setName(repName2);

        create = createReportWriter.saveWithDependencies(reportItem, createTdReport);
        assertTrue(create.isOk());
        Assert.assertEquals(exceptedFileName2, reportItem.getFilename());
    }
}
