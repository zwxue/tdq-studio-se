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
package org.talend.dq.analysis;

import java.util.Date;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.cwm.management.i18n.Messages;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalysisContext;
import org.talend.dataquality.analysis.AnalysisFactory;
import org.talend.dataquality.analysis.AnalysisParameters;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.analysis.ExecutionInformations;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author scorreia
 * 
 * Creates an Analysis and its attribute from the given informations. Use one AnalysisBuilder per Analysis to create.
 */
public class AnalysisBuilder {

    /**
     * 
     */
    private static final String ANALYSIS_NOT_INITIALIZED = "Analysis has not been initialized. Call initializeAnalysis() method before."; //$NON-NLS-1$

    private static Logger log = Logger.getLogger(AnalysisBuilder.class);

    private boolean initialized = false;

    // TODO add an enum to know the type of the analysis

    private Analysis analysis;

    /**
     * Method "initializeAnalysis" creates an Analysis from the given name.
     * 
     * @param analysisName
     * @return
     */
    public boolean initializeAnalysis(String analysisName, AnalysisType type) {
        if (initialized) {
            log.warn(Messages.getString("AnalysisBuilder.ANALYSISALREADYINITIALIZED"));//$NON-NLS-1$
            return false;
        }

        // create the analysis
        this.analysis = AnalysisHelper.createAnalysis(analysisName);

        // set the creation date
        Date date = new Date(System.currentTimeMillis());
        analysis.setCreationDate(date);

        // prepare the context
        analysis.setContext(createAnalysisContext());

        // prepare the parameters
        analysis.setParameters(createAnalysisParameters(type));

        // prepare result
        analysis.setResults(createAnalysisResult());

        initialized = true;
        return initialized;
    }

    private AnalysisParameters createAnalysisParameters(AnalysisType type) {
        AnalysisParameters context = AnalysisFactory.eINSTANCE.createAnalysisParameters();
        context.setAnalysisType(type);
        return context;
    }

    private AnalysisContext createAnalysisContext() {
        AnalysisContext context = AnalysisFactory.eINSTANCE.createAnalysisContext();
        return context;
    }

    private AnalysisResult createAnalysisResult() {
        AnalysisResult result = AnalysisFactory.eINSTANCE.createAnalysisResult();
        // create its metadata
        ExecutionInformations executionInformations = AnalysisFactory.eINSTANCE.createExecutionInformations();
        result.setResultMetadata(executionInformations);
        return result;
    }

    public boolean setAnalysisConnection(DataManager dataManager) {
        if (!initialized) {
            log.error(ANALYSIS_NOT_INITIALIZED);
            return false;
        }
        AnalysisContext context = analysis.getContext();
        if (context == null) {
            log.error(Messages.getString("AnalysisBuilder.CONTEXTISNULL"));//$NON-NLS-1$
            return false;
        }
        context.setConnection(dataManager);
        return true;
    }

    /**
     * Method "setElementToAnalyze". The association between the element and the indicators is not done in this method.
     * It must be done before.
     * 
     * @param element the element to analyze (for several columns, it is a QueryColumnSet)
     * @param indicator the indicator used for analyzing the element (or part of it).
     * @return true if ok
     */
    public boolean addElementToAnalyze(ModelElement element, Indicator... indicators) {
        if (!isOfGoodType(element, indicators)) {
            log.error(Messages.getString("AnalysisBuilder.CANNOTANALYZEDINANALYSIS", element.getName()));//$NON-NLS-1$
            return false;
        }

        // store element in context
        addElementToContext(element);

        for (Indicator indicator : indicators) {
            // do not attach element to indicators (because they should be attached outside in the case of a column
            // set.)
            // indicator.setAnalyzedElement(element);
            // store indicators in results
            analysis.getResults().getIndicators().add(indicator);
        }

        return true;

    }

    public boolean addElementsToAnalyze(ModelElement[] elements, Indicator... indicators) {

        for (int i = 0; i < elements.length; i++) {
            if (!isOfGoodType(elements[i], indicators)) {
                log.error(Messages.getString("AnalysisBuilder.CANNOTANALYZEDINANALYSIS", elements[i].getName()));//$NON-NLS-1$
                continue;
            }
            // store element in context
            addElementToContext(elements[i]);
        }
        for (Indicator indicator : indicators) {
            // do not attach element to indicators (because they should be attached outside in the case of a column
            // set.)
            // indicator.setAnalyzedElement(element);
            // store indicators in results
            analysis.getResults().getIndicators().add(indicator);
        }

        return true;

    }

    /**
     * Method "addFilterOnData". Several filter can be added for one analysis.
     * 
     * @param dataFilter the data filter
     * @return
     */
    public boolean addFilterOnData(Domain dataFilter) {
        return analysis.getParameters().getDataFilter().add(dataFilter);
    }

    /**
     * DOC scorreia Comment method "addElementToContext".
     * 
     * @param element
     */
    private void addElementToContext(ModelElement element) {
        // add the element to the context
        analysis.getContext().getAnalysedElements().add(element);
    }

    /**
     * DOC scorreia Comment method "isOfGoodType".
     * 
     * @param element
     * @return
     */
    private boolean isOfGoodType(ModelElement element, Indicator... indicators) {
        // TODO scorreia check the type of the element and see if it can be added to this analysis.
        // an element cannot be added if it is not of the same type as other elements (e.g. for multicolumn analysis)
        // an element cannot be added if the associated indicator is not adapted to the type of the element.
        // TODO scorreia use AnalysisType
        // AnalysisType analysisType = this.analysis.getParameters().getAnalysisType();
        return true;
    }

    /**
     * Set analysis.
     * 
     * @param analysis
     */
    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    /**
     * Method "getAnalysis".
     * 
     * @return the analysis built and filled in other methods.
     */
    public Analysis getAnalysis() {
        return this.analysis;
    }

    /**
     * Method "saveAnalysis". This method should be called when the creation of the analysis definition (analysis
     * context + parameters) is created. It is not safe to use it for saving the analysis after the evaluation of the
     * analysis result. The domain should be saved before calling this method.
     * 
     * @param folder the folder where the analysis is saved.
     * @return true if saved without problem
     */
    public boolean saveAnalysis(IFolder folder) {
        assert analysis != null;
        AnalysisWriter writer = ElementWriterFactory.getInstance().createAnalysisWrite();
        String filename = DqRepositoryViewService.createFilename(this.analysis.getName(), FactoriesUtil.ANA);
        IFile file = folder.getFile(filename);
        ReturnCode saved = writer.create(analysis, folder);
        // Assert.assertTrue(saved.getMessage(), saved.isOk());
        if (saved.isOk()) {
            if (log.isDebugEnabled()) {
                log.debug("Saved in  " + file.getFullPath());//$NON-NLS-1$
            }
        }
        return saved.isOk();
    }
}
