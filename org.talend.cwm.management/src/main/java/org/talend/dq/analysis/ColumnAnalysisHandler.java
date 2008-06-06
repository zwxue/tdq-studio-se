// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author scorreia
 * 
 * This class helps to handle a Column analysis.
 */
public class ColumnAnalysisHandler {

    private static Logger log = Logger.getLogger(ColumnAnalysisHandler.class);

    /**
     * The resources that are connected to this analysis and that are potentially modified.
     */
    private Collection<Resource> modifiedResources = new HashSet<Resource>();

    private Analysis analysis;

    /**
     * Method "setAnalysis".
     * 
     * @param columnAnalysis the analysis to set
     */
    public void setAnalysis(Analysis columnAnalysis) {
        this.analysis = columnAnalysis;
    }

    /**
     * Method "getAnalysis".
     * 
     * @return the analysis
     */
    public Analysis getAnalysis() {
        return this.analysis;
    }

    public String getName() {
        assert analysis != null;
        return this.analysis.getName();
    }

    public void setName(String name) {
        assert analysis != null;
        this.analysis.setName(name);
    }

    public String getPurpose() {
        assert analysis != null;
        return TaggedValueHelper.getPurpose(analysis);
    }

    public void setPurpose(String purpose) {
        assert analysis != null;
        TaggedValueHelper.setPurpose(purpose, analysis);
    }

    public String getDescription() {
        assert analysis != null;
        return TaggedValueHelper.getDescription(analysis);
    }

    public void setDescription(String description) {
        assert analysis != null;
        TaggedValueHelper.setDescription(description, analysis);
    }

    public String getAuthor() {

        assert analysis != null;
        return TaggedValueHelper.getAuthor(analysis);
    }

    public void setAuthor(String anthor) {

        assert analysis != null;
        TaggedValueHelper.setAuthor(analysis, anthor);
    }

    public String getStatus() {

        assert analysis != null;
        return TaggedValueHelper.getDevStatus(analysis).getLiteral();
    }

    public void setStatus(String status) {

        assert analysis != null;
        TaggedValueHelper.setDevStatus(analysis, DevelopmentStatus.get(status));
    }

    /**
     * Method "addColumnToAnalyze".
     * 
     * @param column
     * @return
     */
    public boolean addColumnToAnalyze(TdColumn column) {
        assert analysis != null;
        assert analysis.getContext() != null;
        return analysis.getContext().getAnalysedElements().add(column);
    }

    public boolean addColumnsToAnalyze(Collection<TdColumn> column) {
        assert analysis != null;
        assert analysis.getContext() != null;
        return analysis.getContext().getAnalysedElements().addAll(column);
    }

    public EList<ModelElement> getAnalyzedColumns() {
        return analysis.getContext().getAnalysedElements();
    }

    public boolean addIndicator(TdColumn column, Indicator... indicators) {
        if (!analysis.getContext().getAnalysedElements().contains(column)) {
            analysis.getContext().getAnalysedElements().add(column);
        }

        for (Indicator indicator : indicators) {
            // store first level of indicators in result.
            analysis.getResults().getIndicators().add(indicator);
            initializeIndicator(indicator, column);
        }
        DataManager connection = analysis.getContext().getConnection();
        if (connection == null) {
            // try to get one
            log.error("Connection has not been set in analysis Context");
            connection = DataProviderHelper.getTdDataProvider(column);
            analysis.getContext().setConnection(connection);
            // FIXME connection should be set elsewhere
        }
        TypedReturnCode<Dependency> rc = DependenciesHandler.getInstance().setDependencyOn(analysis, connection);
        if (rc.isOk()) {
            // DependenciesHandler.getInstance().addDependency(rc.getObject());
            Resource resource = connection.eResource();
            if (resource != null) {
                this.modifiedResources.add(resource);
            }
        }
        return true;
    }

    private void initializeIndicator(Indicator indicator, TdColumn column) {
        indicator.setAnalyzedElement(column);
        // ADDED MODSCA 2008-04-24 set the default indicator definitions
        // // FIXME following code should be executed as soon as an indicator is created, not here.
        boolean definitionSet = DefinitionHandler.getInstance().setDefaultIndicatorDefinition(indicator);
        if (log.isDebugEnabled()) {
            log.debug("Definition set for " + indicator.getName() + ": " + definitionSet);
        }

        // FIXME scorreia in case of composite indicators, add children to result.
        if (indicator instanceof CompositeIndicator) {
            for (Indicator child : ((CompositeIndicator) indicator).getChildIndicators()) {
                initializeIndicator(child, column); // recurse
            }
        }

    }

    public void clearAnalysis() {
        assert analysis != null;
        assert analysis.getContext() != null;
        analysis.getContext().getAnalysedElements().clear();
        analysis.getResults().getIndicators().clear();
    }

    /**
     * Method "setDatamingType".
     * 
     * @param dataminingTypeLiteral the literal expression of the datamining type used for the analysis
     * @param column a column
     */
    public void setDatamingType(String dataminingTypeLiteral, TdColumn column) {
        DataminingType type = DataminingType.get(dataminingTypeLiteral);
        MetadataHelper.setDataminingType(type, column);
        Resource resource = column.eResource();
        if (resource != null) {
            resource.setModified(true); // tell that the resource has been modified.
            // it would be better to handle modifications with EMF Commands
            this.modifiedResources.add(resource);
        }
    }

    /**
     * Method "getDatamingType".
     * 
     * @param column
     * @return the datamining type literal if any or empty string
     */
    public String getDatamingType(TdColumn column) {
        DataminingType dmType = MetadataHelper.getDataminingType(column);
        if (dmType == null) {
            return "";
        }
        // else
        return dmType.getLiteral();
    }

    /**
     * Method "getIndicators".
     * 
     * @param column
     * @return the indicators attached to this column
     */
    public Collection<Indicator> getIndicators(TdColumn column) {
        Collection<Indicator> indics = new ArrayList<Indicator>();
        EList<Indicator> allIndics = analysis.getResults().getIndicators();
        for (Indicator indicator : allIndics) {
            if (indicator.getAnalyzedElement() != null && indicator.getAnalyzedElement().equals(column)) {
                indics.add(indicator);
            }
        }
        return indics;
    }

    /**
     * Method "getIndicatorLeaves" returns the indicators for the given column at the leaf level.
     * 
     * @param column
     * @return the indicators attached to this column
     */
    public Collection<Indicator> getIndicatorLeaves(TdColumn column) {
        // get the leaf indicators
        Collection<Indicator> leafIndics = IndicatorHelper.getIndicatorLeaves(analysis.getResults());
        // filter only indicators for this column
        Collection<Indicator> indics = new ArrayList<Indicator>();
        for (Indicator indicator : leafIndics) {
            if (indicator.getAnalyzedElement() != null && indicator.getAnalyzedElement().equals(column)) {
                indics.add(indicator);
            }
        }
        return indics;
    }

    /**
     * Method "setStringDataFilter".
     * 
     * @param datafilterString
     * @return true when a new data filter is created, false if it is only updated
     */
    public boolean setStringDataFilter(String datafilterString) {
        return AnalysisHelper.setStringDataFilter(analysis, datafilterString);
    }

    public String getStringDataFilter() {
        return AnalysisHelper.getStringDataFilter(analysis);
    }

    public boolean saveModifiedResources() {
        EMFUtil util = new EMFUtil();
        util.getResourceSet().getResources().addAll(this.modifiedResources);
        return util.save();
    }
}
