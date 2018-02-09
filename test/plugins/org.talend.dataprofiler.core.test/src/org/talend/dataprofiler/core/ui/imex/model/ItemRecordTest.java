// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.imex.model;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.Assert;
import org.junit.Test;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.NullCountIndicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.PropertiesFactory;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC talend class global comment. Detailled comment
 */
public class ItemRecordTest {

    /**
     * Test method for {@link org.talend.dataprofiler.core.ui.imex.model.ItemRecord#loadProperty()}.
     * 
     * @throws PersistenceException
     */
    @Test
    public void testLoadProperty() throws PersistenceException {

        Property analysisProperty = createAnalysis("AElementPersistanceRealTestanalysis1"); //$NON-NLS-1$
        TDQAnalysisItem item = (TDQAnalysisItem) analysisProperty.getItem();
        Analysis analysis = item.getAnalysis();
        AnalysisResult createAnalysisResult = analysis.getResults();
        // create Indicator
        RowCountIndicator rowCountIndicator = IndicatorsFactory.eINSTANCE.createRowCountIndicator();
        String rowCountPropertyID = EcoreUtil.generateUUID();
        saveIndicatorDefintion(rowCountPropertyID, "ItemRecordWithRefreshedTestIndicatorDefinition1"); //$NON-NLS-1$
        rowCountIndicator.setIndicatorDefinition(((TDQIndicatorDefinitionItem) ProxyRepositoryFactory.getInstance()
                .getLastVersion(rowCountPropertyID).getProperty().getItem()).getIndicatorDefinition());
        createAnalysisResult.getIndicators().add(rowCountIndicator);
        ReturnCode saveAnalysis = saveAnalysis(analysis);
        Assert.assertTrue("The analysis first time saving is not work", saveAnalysis.isOk()); //$NON-NLS-1$

        File analysisFile = WorkspaceUtils.ifileToFile(PropertyHelper.getItemFile(analysisProperty));
        ItemRecord itemRecord = new ItemRecord(analysisFile);
        Assert.assertEquals(1, itemRecord.getDependencySet().size());

        // create Indicator
        NullCountIndicator nullCountIndicator = IndicatorsFactory.eINSTANCE.createNullCountIndicator();
        String nullCountPropertyID = EcoreUtil.generateUUID();
        saveIndicatorDefintion(nullCountPropertyID, "ItemRecordWithRefreshedTestIndicatorDefinition2"); //$NON-NLS-1$
        nullCountIndicator.setIndicatorDefinition(((TDQIndicatorDefinitionItem) ProxyRepositoryFactory.getInstance()
                .getLastVersion(nullCountPropertyID).getProperty().getItem()).getIndicatorDefinition());
        analysis = item.getAnalysis();
        analysis.getResults().getIndicators().add(nullCountIndicator);
        ReturnCode saveAnalysis2 = saveAnalysis(analysis);
        Assert.assertTrue("The analysis second time saving is not work", saveAnalysis2.isOk()); //$NON-NLS-1$
        // get last resource so that the dependecy will not changed
        itemRecord = new ItemRecord(analysisFile);
        Assert.assertEquals(1, itemRecord.getDependencySet().size());
        // after clear the resource will be lastest so that the dependency is added
        ItemRecord.clear();
        itemRecord = new ItemRecord(analysisFile);
        Assert.assertEquals(2, itemRecord.getDependencySet().size());
    }

    /**
     * DOC zshen Comment method "saveAnalysis".
     * 
     * @param analysis
     */
    private ReturnCode saveAnalysis(Analysis analysis) {
        AnalysisWriter createAnalysisWriter = ElementWriterFactory.getInstance().createAnalysisWrite();
        return createAnalysisWriter.save(analysis);
    }

    private Property createAnalysis(String name) throws PersistenceException {

        // create analysis
        Analysis createAnalysis = AnalysisFactory.eINSTANCE.createAnalysis();
        AnalysisResult createAnalysisResult = AnalysisFactory.eINSTANCE.createAnalysisResult();
        AnalysisContext createAnalysisContext = AnalysisFactory.eINSTANCE.createAnalysisContext();
        createAnalysis.setResults(createAnalysisResult);
        createAnalysis.setContext(createAnalysisContext);
        createAnalysis.setName(name);

        // create analysis item
        TDQAnalysisItem createTDQAnalysisItem = PropertiesFactory.eINSTANCE.createTDQAnalysisItem();
        createTDQAnalysisItem.setAnalysis(createAnalysis);
        Property createAnalysisProperty = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createProperty();
        createAnalysisProperty.setLabel(name);
        createTDQAnalysisItem.setProperty(createAnalysisProperty);
        createAnalysisProperty.setId(EcoreUtil.generateUUID());
        ProxyRepositoryFactory.getInstance().create(createTDQAnalysisItem, Path.EMPTY, false);
        return createAnalysisProperty;
    }

    private void saveIndicatorDefintion(String uuid, String name) throws PersistenceException {

        // create definition
        IndicatorDefinition createIndicatorDefinition = DefinitionFactory.eINSTANCE.createIndicatorDefinition();

        TDQIndicatorDefinitionItem createTDQIndicatorDefinitionItem = PropertiesFactory.eINSTANCE
                .createTDQIndicatorDefinitionItem();
        createTDQIndicatorDefinitionItem.setIndicatorDefinition(createIndicatorDefinition);
        Property createProperty = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createProperty();
        createProperty.setLabel(name);
        createTDQIndicatorDefinitionItem.setProperty(createProperty);
        createProperty.setId(uuid);
        ProxyRepositoryFactory.getInstance().create(createTDQIndicatorDefinitionItem,
                new Path(ERepositoryObjectType.TDQ_SYSTEM_INDICATORS.getFolder()).removeFirstSegments(2), false);
    }

}
