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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.reports.TdReport;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.writer.AElementPersistance;
import org.talend.repository.model.RepositoryNode;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * @author scorreia
 * 
 * This class saves the analysis.
 */
public class ReportWriter extends AElementPersistance {

    /**
     * DOC bZhou ReportWriter constructor comment.
     */
    ReportWriter() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#addDependencies(orgomg.cwm.objectmodel.core.ModelElement)
     */
    @Override
    public void addDependencies(ModelElement element) {
        TdReport report = (TdReport) element;
        for (Analysis ana : ReportHelper.getAnalyses(report)) {
            TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(report, ana);
            if (dependencyReturn.isOk()) {
                try {
                    RepositoryNode repositoryNode = RepositoryNodeHelper.recursiveFind(ana);
                    if (repositoryNode != null) {
                        TDQAnalysisItem anaItem = (TDQAnalysisItem) repositoryNode.getObject().getProperty().getItem();
                        anaItem.setAnalysis(ana);
                    }
                    ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider().getResourceManager()
                            .saveResource(ana.eResource());
                } catch (PersistenceException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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
        EList<EObject> resourceContents = element.eResource().getContents();
        resourceContents.addAll(element.getDescription());
    }

    public void addResourceContent(Resource resource, Report element) {
        if (resource != null) {
            EList<EObject> resourceContents = resource.getContents();
            resourceContents.addAll(element.getDescription());
            resource.getContents().add(element);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.AElementPersistance#getFileExtension()
     */
    @Override
    protected String getFileExtension() {
        return FactoriesUtil.REP;
    }

    public ReturnCode save(Item item) {
        ReturnCode rc = new ReturnCode();
//        try {
//            TDQAnalysisItem anaItem = (TDQAnalysisItem) item;
//            if (anaItem != null && anaItem.eIsProxy()) {
//                anaItem = (TDQAnalysisItem) EObjectHelper.resolveObject(anaItem);
//                anaItem.getProperty().setLabel(anaItem.getAnalysis().getName());
//            }
//            Analysis analysis = anaItem.getAnalysis();
//            addDependencies(analysis);
//            addResourceContent(analysis.eResource(), analysis);
//            ProxyRepositoryFactory.getInstance().save(anaItem);
//        } catch (PersistenceException e) {
//            log.error(e, e);
//            rc.setOk(Boolean.FALSE);
//            rc.setMessage(e.getMessage());
//        }
        return rc;
    }

    @Override
    protected void notifyResourceChanges() {
        ProxyRepositoryManager.getInstance().save(Boolean.TRUE);

    }
}
