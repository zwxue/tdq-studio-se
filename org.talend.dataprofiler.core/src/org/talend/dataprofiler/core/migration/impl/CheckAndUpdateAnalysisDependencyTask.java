// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.migration.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.resource.EResourceConstant;
import orgomg.cwm.foundation.softwaredeployment.DataManager;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * created by yyin on 2013-7-10 check the client dependency of the analysis according to TDQ-7327 if there are more than
 * one client dependency(db connection), remove the not used one, and remove the related dependency in the removed db
 * connection
 * 
 */
public class CheckAndUpdateAnalysisDependencyTask extends AbstractWorksapceUpdateTask {

    private static Logger log = Logger.getLogger(CheckAndUpdateAnalysisDependencyTask.class);

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.IMigrationTask#getOrder()
     */
    public Date getOrder() {
        return createDate(2013, 7, 10);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.IMigrationTask#getMigrationTaskType()
     */
    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.migration.AMigrationTask#doExecute()
     */
    @Override
    protected boolean doExecute() throws Exception {
        // load all analyses and check their dependency correct or not.
        checkAndRemoveWrongDependencies(getAnalyses());

        return true;
    }

    private List<Analysis> getAnalyses() {
        List<Analysis> analyses = new ArrayList<Analysis>();
        File sysIndsFolder = getWorkspacePath().append(EResourceConstant.ANALYSIS.getPath()).toFile();
        ArrayList<File> fileList = new ArrayList<File>();
        FilesUtils.getAllFilesFromFolder(sysIndsFolder, fileList, new FilenameFilter() {

            public boolean accept(File dir, String name) {
                if (name.endsWith("ana")) {//$NON-NLS-1$
                    return true;
                }
                return false;
            }
        });
        for (File file : fileList) {
            Analysis indDef = getAnalysisFromFile(file);
            if (indDef != null) {
                analyses.add(indDef);
            }
        }

        return analyses;
    }

    /**
     * DOC xqliu Comment method "getIndicatorDefinitionFromFile".
     * 
     * @param file
     * @return
     */
    private Analysis getAnalysisFromFile(File file) {
        Resource itemResource = getResource(file);
        for (EObject object : itemResource.getContents()) {
            if (object instanceof Analysis) {
                return (Analysis) object;
            }
        }

        return null;
    }

    /**
     * check each analysis if it has only one client dependency, if more than one, remove the useless one.
     * 
     * @param list
     * @throws CoreException
     */
    private void checkAndRemoveWrongDependencies(List<Analysis> analyses) throws CoreException {

        for (Analysis analysis : analyses) {
            if (analysis != null) {
                boolean isAnalysisModified = false;

                TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(analysis.getContext().getAnalysedElements().get(0));
                // find the correct db connection from analyzed element
                DataManager correctDB = ConnectionHelper.getConnection(tdColumn);
                // check if the connection is correct or not
                DataManager connection = analysis.getContext().getConnection();
                if (!correctDB.getName().equals(connection.getName())) {
                    analysis.getContext().setConnection(correctDB);
                    isAnalysisModified = true;
                }

                List<DataProvider> clientDependencyDB = null;
                for (Dependency dependency : analysis.getClientDependency()) {
                    ModelElement supplier = dependency.getSupplier().get(0);

                    if (supplier instanceof DataProvider) {
                        if (supplier != null && correctDB.getName().equals(supplier.getName())) {
                            continue;
                        } else {
                            if (clientDependencyDB == null) {
                                clientDependencyDB = new ArrayList<DataProvider>();
                            }
                            clientDependencyDB.add((DataProvider) supplier);
                            // save the related connection
                            // EMFSharedResources.getInstance().saveResource(supplier.eResource());
                        }
                    }
                }
                if (clientDependencyDB != null) {
                    for (DataProvider uselessDB : clientDependencyDB) {
                        // if the db in client dependency do not equal to the correct db, remove it from both the
                        // analysis and db connection
                        removeDependenciesBetweenAnaCon(uselessDB, analysis);
                        isAnalysisModified = true;
                    }
                }
                if (isAnalysisModified) {
                    EMFSharedResources.getInstance().saveResource(analysis.eResource());
                }
            }
        }

    }

    private void removeDependenciesBetweenAnaCon(DataProvider oldDataProvider, Analysis tempAnalysis) {
        List<ModelElement> tempList = new ArrayList<ModelElement>();
        tempList.add(oldDataProvider);
        // remove the cliend dependency in the analysis
        List<Resource> modified = DependenciesHandler.getInstance().removeDependenciesBetweenModels(tempAnalysis, tempList);
        for (Resource me : modified) {
            EMFUtil.saveSingleResource(me);
        }
        // remove the supplier dependency in the dataprovider
        tempList.clear();
        tempList.add(tempAnalysis);
        modified = removeSupplierDependenciesBetweenModels(oldDataProvider, tempList);
        for (Resource me : modified) {
            EMFUtil.saveSingleResource(me);
        }
        EMFUtil.saveSingleResource(oldDataProvider.eResource());
    }

    /*
     * This task should be done before the import
     * 
     * @see org.talend.dataprofiler.migration.AMigrationTask#isModelTask()
     */
    @Override
    public Boolean isModelTask() {
        return true;
    }

    private List<Resource> removeSupplierDependenciesBetweenModels(ModelElement elementFromRemove,
            List<? extends ModelElement> elementToRemove) {
        EList<Dependency> supplierDependencies;
        List<String> toRemoveResources = new ArrayList<String>();
        for (ModelElement modelElement : elementToRemove) {
            toRemoveResources.add(modelElement.getName());
        }
        // get the client dependencies (
        supplierDependencies = elementFromRemove.getSupplierDependency();

        List<Resource> modifiedResources = new ArrayList<Resource>();
        for (Dependency dependency : supplierDependencies) {
            EList<ModelElement> client = dependency.getClient();
            // get the resource of each client
            Iterator<ModelElement> dependencyIterator = client.iterator();
            while (dependencyIterator.hasNext()) {
                ModelElement dependencyModel = dependencyIterator.next();
                Resource clientResource = dependencyModel.eResource();

                if (clientResource != null) {
                    if (toRemoveResources.contains(dependencyModel.getName())) {
                        modifiedResources.add(clientResource);
                        dependencyIterator.remove();
                    }
                }
            }
        }

        return modifiedResources;
    }
}
