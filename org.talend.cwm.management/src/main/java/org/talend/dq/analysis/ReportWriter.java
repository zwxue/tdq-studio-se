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

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.helpers.ReportHelper;
import org.talend.dataquality.reports.TdReport;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Dependency;

/**
 * @author scorreia
 * 
 * This class saves the analysis.
 */
public class ReportWriter {

    public static final String VALID_EXTENSION = FactoriesUtil.REP;

    /**
     * Method "save".
     * 
     * @param report the analysis to save
     * @param file the file in which the analysis will be save
     * @return whether everything is ok
     */
    public ReturnCode save(TdReport report, IFile file) {
        assert file != null : "Cannot save report: No file name given.";
        assert report != null : "No report to save (null)";

        ReturnCode rc = new ReturnCode();
        if (!checkFileExtension(file)) {
            rc.setReturnCode("Bad file extension for " + file.getFullPath().toString() + ". Should be " + VALID_EXTENSION, false);
            return rc;
        }
        EMFUtil util = EMFSharedResources.getSharedEmfUtil();
        // Resource resource = util.getResourceSet().createResource(URI.createFileURI(file.getAbsolutePath()));
        // resource.getContents().addAll(report.getResults().getIndicators());

        boolean added = util.addPoolToResourceSet(file.getFullPath().toString(), report);

        if (!added) {
            rc.setReturnCode("Report  " + report.getName() + " won't be saved. " + util.getLastErrorMessage(), added);
            return rc;
        }

        // --- store descriptions (description and purpose) in the same resource
        EList<EObject> resourceContents = report.eResource().getContents();
        resourceContents.addAll(report.getDescription());

        addAnaResourceOfReport(report, util);
        boolean saved = util.saveSingleResource(report.eResource());
        if (!saved) {
            rc.setReturnCode("Problem while saving report " + report.getName() + ". " + util.getLastErrorMessage(), saved);
        }
        return rc;
    }

    /**
     * DOC rli Comment method "addAnaResourceOfReport".
     * 
     * @param report
     * @param util
     */
    private void addAnaResourceOfReport(TdReport report, EMFUtil util) {
        for (Analysis ana : ReportHelper.getAnalyses(report)) {
            TypedReturnCode<Dependency> dependencyReturn = DependenciesHandler.getInstance().setDependencyOn(report, ana);
            if (dependencyReturn.isOk()) {
                // util.getResourceSet().getResources().add(DependenciesHandler.getInstance().getDependencyResource());
                util.saveSingleResource(ana.eResource());
            }
        }
    }

    public ReturnCode save(TdReport report) {
        assert report != null : "No report to save (null)";
        ReturnCode rc = new ReturnCode();
        Resource resource = report.eResource();
        if (resource == null) {
            rc.setReturnCode("Error: No resource found! A file must be defined in which the report " + report.getName()
                    + " will be saved.", false);
            return rc;
        }

        // save the resource and related resources (when needed, for example when we change the data mining type of a
        // column)
        boolean saved = EMFSharedResources.getSharedEmfUtil().saveResource(resource);

        EMFUtil util = EMFSharedResources.getSharedEmfUtil();
        addAnaResourceOfReport(report, util);
        if (!saved) {
            rc.setReturnCode("Problem while saving report " + report.getName() + ". ", saved);
        }
        return rc;

    }

    private boolean checkFileExtension(IFile file) {
        return file.getFileExtension().equalsIgnoreCase(VALID_EXTENSION);
    }
}
