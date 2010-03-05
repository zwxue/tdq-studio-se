// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dq.writer.impl;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.writer.AElementPersistance;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author scorreia
 * 
 * This class saves the analysis.
 */
public class AnalysisWriter extends AElementPersistance {

    /**
     * DOC bZhou AnalysisWriter constructor comment.
     */
    AnalysisWriter() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addDependencies(ModelElement element) {
        // DependenciesHandler.getInstance().removeSupplierDependenciesBetweenModels(elementFromRemove, elementToRemove)
        Analysis analysis = (Analysis) element;
        List<IndicatorDefinition> userDefinedIndicators = AnalysisHelper.getUserDefinedIndicators(analysis);
        for (IndicatorDefinition udi : userDefinedIndicators) {
            TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(analysis, udi);
            if (dependencyReturn.isOk()) {
                EMFUtil.saveSingleResource(udi.eResource());
            }
        }
        List<Pattern> patterns = AnalysisHelper.getPatterns(analysis);
        for (Pattern pattern : patterns) {
            TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(analysis, pattern);
            if (dependencyReturn.isOk()) {
                EMFUtil.saveSingleResource(pattern.eResource());
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addResourceContent(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    protected void addResourceContent(ModelElement element) {
        Resource resource = element.eResource();
        if (resource != null) {
            EList<EObject> resourceContents = resource.getContents();
            EList<Domain> dataFilter = AnalysisHelper.getDataFilter((Analysis) element);
            List<Domain> domains = DomainHelper.getDomains(resourceContents);
            resourceContents.removeAll(domains);

            if (dataFilter != null) {
                // scorreia save them in their own file? -> no, it's ok to save them
                // in the analysis file.
                for (Domain domain : dataFilter) {
                    if (!resourceContents.contains(domain)) {
                        resourceContents.add(domain);
                    }
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return FactoriesUtil.ANA;
    }
}
