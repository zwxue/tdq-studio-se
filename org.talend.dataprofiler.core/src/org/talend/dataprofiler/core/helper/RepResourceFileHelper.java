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
package org.talend.dataprofiler.core.helper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataquality.reports.TdReport;
import org.talend.dataquality.reports.util.ReportsSwitch;
import org.talend.dq.analysis.ReportWriter;
import org.talend.utils.sugars.ReturnCode;

/**
 * DOC rli class global comment. Detailled comment
 */
public final class RepResourceFileHelper extends ResourceFileMap {

    private static Logger log = Logger.getLogger(RepResourceFileHelper.class);

    private static RepResourceFileHelper instance;

    private Map<IFile, TdReport> allRepMap = new HashMap<IFile, TdReport>();

    private RepResourceFileHelper() {
        super();
    }

    public static RepResourceFileHelper getInstance() {
        if (instance == null) {
            instance = new RepResourceFileHelper();
        }
        return instance;
    }

    public Collection<TdReport> getAllReports() {
        allRepMap.clear();
        IFolder defaultAnalysFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(
                PluginConstant.DATA_PROFILING_PROJECTNAME).getFolder(DQStructureManager.REPORTS);
        try {
            searchAllReports(defaultAnalysFolder);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return allRepMap.values();
    }

    private void searchAllReports(IFolder folder) throws CoreException {
        for (IResource resource : folder.members()) {
            if (resource.getType() == IResource.FOLDER) {
                searchAllReports(folder.getFolder(resource.getName()));
            }
            IFile file = (IFile) resource;
            findReport(file);
        }
    }

    public TdReport findReport(IFile file) {
        TdReport report = allRepMap.get(file);
        if (report != null) {
            return report;
        }
        Resource fileResource = getFileResource(file);
        report = retireReport(fileResource);
        if (report != null) {
            allRepMap.put(file, report);
        }
        return report;
    }

    public TdReport readFromFile(IFile file) {
        registedResourceMap.remove(file);
        Resource fileResource = getFileResource(file);
        TdReport report = retireReport(fileResource);
        if (report != null) {
            allRepMap.put(file, report);
        }
        return report;
    }

    /**
     * DOC rli Comment method "retireAnalysis".
     * 
     * @param fileResource
     * @return
     */
    private TdReport retireReport(Resource fileResource) {
        EList<EObject> contents = fileResource.getContents();
        if (contents.isEmpty()) {
            log.error("No content in " + fileResource);
            return null;
        }
        log.info("Nb elements in contents " + contents.size());
        ReportsSwitch<TdReport> mySwitch = new ReportsSwitch<TdReport>() {

            public TdReport caseTdReport(TdReport object) {
                return object;
            }
        };
        TdReport report = null;
        if (contents != null) {
            report = mySwitch.doSwitch(contents.get(0));
        }
        return report;
    }

    public void remove(IFile file) {
        super.remove(file);
        this.allRepMap.remove(file);
    }

    public void clear() {
        super.clear();
        this.allRepMap.clear();
    }

    public ReturnCode save(TdReport report) {
        ReportWriter writer = new ReportWriter();
        ReturnCode saved = writer.save(report);
        if (saved.isOk()) {
            setResourceChanged(true);
        }
        return saved;
    }

}
