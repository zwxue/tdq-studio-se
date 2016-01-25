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
package org.talend.cwm.dependencies;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.BlankCountIndicator;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.CountsIndicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.definition.DefinitionFactory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.PropertiesFactory;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dq.helper.PropertyHelper;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC zshen class global comment. Detailled comment
 */

@PrepareForTest({ PropertyHelper.class, ModelElementHelper.class, IndicatorHelper.class })
public class DependenciesHandlerTest {

    @Rule
    public PowerMockRule powerMockRule = new PowerMockRule();

    /**
     * Test method for
     * {@link org.talend.cwm.dependencies.DependenciesHandler#getClintDependency(org.talend.core.model.properties.Property)}
     * .
     */
    @Test
    public void testGetClintDependencyProperty() {
        // DependenciesHandler is a final class we can not mock it. so test this method need to call really
        // getAnaDependency method that we must to create so many element
        // countIndicator
        Property countIndicatorProperty = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createProperty();
        TDQIndicatorDefinitionItem countIndicatorItem = PropertiesFactory.eINSTANCE.createTDQIndicatorDefinitionItem();
        countIndicatorProperty.setItem(countIndicatorItem);
        countIndicatorItem.setProperty(countIndicatorProperty);
        // Analysis
        Property anaProperty = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createProperty();
        TDQAnalysisItem createItem = PropertiesFactory.eINSTANCE.createTDQAnalysisItem();

        Analysis createAnalysis = AnalysisFactory.eINSTANCE.createAnalysis();

        // create analysisResult
        AnalysisResult createAnalysisResult = AnalysisFactory.eINSTANCE.createAnalysisResult();
        // for getAnaDependency method
        CountsIndicator createCountsIndicator = IndicatorsFactory.eINSTANCE.createCountsIndicator();

        BlankCountIndicator createBlankCountIndicator = IndicatorsFactory.eINSTANCE.createBlankCountIndicator();
        IndicatorDefinition createIndicatorDefinition = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        createCountsIndicator.setIndicatorDefinition(createIndicatorDefinition);
        countIndicatorItem.setIndicatorDefinition(createIndicatorDefinition);
        createBlankCountIndicator.setIndicatorDefinition(createIndicatorDefinition);
        createCountsIndicator.getAllChildIndicators().add(createBlankCountIndicator);
        createAnalysisResult.getIndicators().add(createCountsIndicator);

        // two different Indicator use same IndicatorDefinition the second one will be filter out.
        CompositeIndicator createCompositeIndicator = IndicatorsFactory.eINSTANCE.createTextIndicator();
        createAnalysisResult.getIndicators().add(createCompositeIndicator);
        createIndicatorDefinition = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        createCompositeIndicator.setIndicatorDefinition(createIndicatorDefinition);
        // ~
        // ~for getAnaDependency method
        createAnalysis.setResults(createAnalysisResult);
        createItem.setAnalysis(createAnalysis);
        anaProperty.setItem(createItem);
        createItem.setProperty(anaProperty);

        // staticMock PropertyHelper
        PowerMockito.mockStatic(PropertyHelper.class);
        Mockito.when(PropertyHelper.getModelElement(anaProperty)).thenReturn(createAnalysis);
        Mockito.when(PropertyHelper.getProperty(createCountsIndicator.getIndicatorDefinition())).thenReturn(
                countIndicatorProperty);

        // staticMock ModelElementHelper
        PowerMockito.mockStatic(ModelElementHelper.class);
        ModelElementMatcher modelElementMatcher = new ModelElementMatcher();
        Mockito.when(ModelElementHelper.compareUUID(Mockito.argThat(modelElementMatcher), Mockito.argThat(modelElementMatcher)))
                .thenReturn(modelElementMatcher.equals(modelElementMatcher));

        List<Property> clintDependency = DependenciesHandler.getInstance().getClintDependency(anaProperty);
        assertTrue(clintDependency.size() == 1);
        assertTrue(clintDependency.get(0).equals(countIndicatorProperty));

    }

    @Test
    /**
     * Add by qiongli 2012-5-16,it is for TDQ-5259.
     */
    public void testGetClintDependencyProperty_2() {
        Property property = mock(Property.class);
        PowerMockito.mockStatic(PropertyHelper.class);
        when(PropertyHelper.getModelElement(property)).thenReturn(null);
        List<Property> clintDependency = DependenciesHandler.getInstance().getClintDependency(property);
        assertTrue(clintDependency.size() == 0);

    }

    class ModelElementMatcher extends ArgumentMatcher<ModelElement> {

        @Override
        public boolean matches(Object list) {
            if (list instanceof ModelElement) {
                return true;
            }
            return false;
        }
    }

    /**
     * Test method for
     * {@link org.talend.cwm.dependencies.DependenciesHandler#removeDependenciesBetweenModel(orgomg.cwm.objectmodel.core.ModelElement, orgomg.cwm.objectmodel.core.ModelElement)}
     * .
     */
    @Test
    public void testRemoveDependenciesBetweenModel() {
        ModelElement ana = AnalysisHelper.createAnalysis("ana1");
        ModelElement conn = ConnectionHelper.createDatabaseConnection("conn1");
        EList<Dependency> clientDependencyFirst = ana.getClientDependency();
        assertEquals(0, clientDependencyFirst.size());

        TypedReturnCode<Dependency> setUsageDependencyOn = DependenciesHandler.getInstance().setUsageDependencyOn(ana, conn);
        assertEquals(1, clientDependencyFirst.size());
        EList<ModelElement> supplier = clientDependencyFirst.get(0).getSupplier();
        assertEquals(1, supplier.size());
        assertEquals(conn, supplier.get(0));
        if (setUsageDependencyOn.isOk()) {
            DependenciesHandler.getInstance().removeDependenciesBetweenModel(conn, ana);
        }
        assertEquals(0, clientDependencyFirst.size());
        EList<Dependency> clientDependencyTwo = conn.getClientDependency();
        assertEquals(0, clientDependencyTwo.size());
        setUsageDependencyOn = DependenciesHandler.getInstance().setUsageDependencyOn(conn, ana);
        assertEquals(1, clientDependencyTwo.size());
        supplier = clientDependencyTwo.get(0).getSupplier();
        assertEquals(1, supplier.size());
        assertEquals(ana, supplier.get(0));
        if (setUsageDependencyOn.isOk()) {
            DependenciesHandler.getInstance().removeDependenciesBetweenModel(ana, conn);
        }
        assertEquals(0, clientDependencyTwo.size());
    }

}
