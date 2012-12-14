// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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
import org.talend.dataquality.indicators.Indicator;
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
        // countIndicator
        Property countIndicatorProperty = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createProperty();
        TDQIndicatorDefinitionItem countIndicatorItem = PropertiesFactory.eINSTANCE.createTDQIndicatorDefinitionItem();
        countIndicatorProperty.setItem(countIndicatorItem);
        countIndicatorItem.setProperty(countIndicatorProperty);
        // Analysis
        Property anaProperty = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createProperty();
        TDQAnalysisItem createItem = PropertiesFactory.eINSTANCE.createTDQAnalysisItem();

        Analysis createAnalysis = AnalysisFactory.eINSTANCE.createAnalysis();

        AnalysisResult createAnalysisResult = AnalysisFactory.eINSTANCE.createAnalysisResult();

        CountsIndicator createCountsIndicator = IndicatorsFactory.eINSTANCE.createCountsIndicator();

        BlankCountIndicator createBlankCountIndicator = IndicatorsFactory.eINSTANCE.createBlankCountIndicator();
        IndicatorDefinition createIndicatorDefinition = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        createIndicatorDefinition = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        createCountsIndicator.setIndicatorDefinition(createIndicatorDefinition);
        countIndicatorItem.setIndicatorDefinition(createIndicatorDefinition);
        createBlankCountIndicator.setIndicatorDefinition(createIndicatorDefinition);
        createCountsIndicator.getAllChildIndicators().add(createBlankCountIndicator);
        createAnalysisResult.getIndicators().add(createCountsIndicator);

        CompositeIndicator createCompositeIndicator = IndicatorsFactory.eINSTANCE.createTextIndicator();
        createAnalysisResult.getIndicators().add(createCompositeIndicator);
        createIndicatorDefinition = DefinitionFactory.eINSTANCE.createIndicatorDefinition();
        createCompositeIndicator.setIndicatorDefinition(createIndicatorDefinition);

        createAnalysis.setResults(createAnalysisResult);
        createItem.setAnalysis(createAnalysis);
        anaProperty.setItem(createItem);
        createItem.setProperty(anaProperty);

        //staticMock PropertyHelper
        PowerMockito.mockStatic(PropertyHelper.class);
        Mockito.when(PropertyHelper.getModelElement(anaProperty)).thenReturn(createAnalysis);
        Mockito.when(PropertyHelper.getProperty(createCountsIndicator.getIndicatorDefinition())).thenReturn(
                countIndicatorProperty);
        
        //staticMock ModelElementHelper 
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
        assert (clientDependencyFirst.size() == 0);

        TypedReturnCode<Dependency> setUsageDependencyOn = DependenciesHandler.getInstance().setUsageDependencyOn(ana, conn);
        assert (clientDependencyFirst.size() == 1);
        EList<ModelElement> supplier = clientDependencyFirst.get(0).getSupplier();
        assert (supplier.size() == 1);
        assert (supplier.get(0) == conn);
        if (setUsageDependencyOn.isOk()) {
            DependenciesHandler.getInstance().removeDependenciesBetweenModel(conn, ana);
        }
        assert (clientDependencyFirst.size() == 0);
        EList<Dependency> clientDependencyTwo = conn.getClientDependency();
        assert (clientDependencyTwo.size() == 0);
        setUsageDependencyOn = DependenciesHandler.getInstance().setUsageDependencyOn(conn, ana);
        assert (clientDependencyTwo.size() == 1);
        supplier = clientDependencyTwo.get(0).getSupplier();
        assert (supplier.size() == 1);
        assert (supplier.get(0) == ana);
        if (setUsageDependencyOn.isOk()) {
            DependenciesHandler.getInstance().removeDependenciesBetweenModel(ana, conn);
        }
        assert (clientDependencyTwo.size() == 0);
    }
    /**
     * Test method for {@link org.talend.cwm.dependencies.DependenciesHandler#getAnaDependency(org.talend.core.model.properties.Property)}.
     */
    @Test
    public void testGetAnaDependency() {
        Property property=mock(Property.class);
        TDQAnalysisItemImpl item = mock(TDQAnalysisItemImpl.class);
        when(property.getItem()).thenReturn(item);
        Analysis ana = mock(Analysis.class);
        when(item.getAnalysis()).thenReturn(ana);
        AnalysisResult anaResult = mock(AnalysisResult.class);
        when(ana.getResults()).thenReturn(anaResult);
        PowerMockito.mockStatic(IndicatorHelper.class);
        List<Indicator> indLs = new ArrayList<Indicator>();
        Indicator ind1 = mock(Indicator.class);
        IndicatorDefinition indDefinition1 = mock(IndicatorDefinition.class);
        when(ind1.getIndicatorDefinition()).thenReturn(indDefinition1);
        Indicator ind2 = mock(Indicator.class);
        indLs.add(ind1);
        indLs.add(ind2);
        when(IndicatorHelper.getIndicators(anaResult)).thenReturn(indLs);
        Property iniProperty = mock(Property.class);
        PowerMockito.mockStatic(PropertyHelper.class);
        Property indDefProperty = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createProperty();
        when(PropertyHelper.getProperty(indDefinition1)).thenReturn(indDefProperty);

        DependenciesHandler depHandler = DependenciesHandler.getInstance();
        List<Property> propLs = depHandler.getAnaDependency(anaProperty);
        assert (propLs.size() == 1);
        
    }
}
