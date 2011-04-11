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
package org.talend.dq.helper.resourcehelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.dataquality.reports.TdReport;
import org.talend.dataquality.reports.util.ReportsSwitch;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.ReportWriter;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

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

    public Collection<TdReport> getAllReports(IFolder reportsFolder) {
        try {
            searchAllReports(reportsFolder);
        } catch (CoreException e) {
            log.error(e, e);
        }
        return allRepMap.values();
    }

    public Collection<TdReport> getAllReports() {
        IFolder reportFolder = ResourceManager.getDataProfilingFolder().getFolder("Reports");
        return getAllReports(reportFolder);
    }

    private void searchAllReports(IFolder folder) throws CoreException {
        for (IResource resource : folder.members()) {
            // ~MOD mzhao if the member is a instance of IFolder, forget it.
            if (resource instanceof IFolder) {
                continue;
            }
            // ~
            if (resource.getType() == IResource.FOLDER) {
                searchAllReports(folder.getFolder(resource.getName()));
            }
            IFile file = (IFile) resource;
            if (checkFile(file)) {
                findReport(file);
            }
        }
    }

    public TdReport findReport(IFile file) {
        if (checkFile(file)) {
            TdReport report = allRepMap.get(file);
            if (report == null) {
                report = retireReport(getFileResource(file, true));
            }

            return report;
        }

        return null;
    }

    private TdReport readFromFile(IFile file) {
        this.remove(file);
        Resource fileResource = getFileResource(file);
        Iterator<IFile> fileIterator = allRepMap.keySet().iterator();
        while (fileIterator.hasNext()) {
            IFile key = fileIterator.next();
            TdReport rePort = allRepMap.get(key);
            Resource resourceObj = rePort.eResource();
            if (resourceObj == fileResource) {
                registedResourceMap.remove(key);
                allRepMap.remove(key);
                break;
            }
        }
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
        if (log.isDebugEnabled())
            log.debug("Nb elements in contents " + contents.size());
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

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.resourcehelper.ResourceFileMap#deleteRelated(org.eclipse.core.resources.IFile)
     */
    @Override
    protected void deleteRelated(IFile file) {
        EObjectHelper.getDependencyClients(file);

    }

    public ReturnCode save(TdReport report) {
        ReportWriter writer = ElementWriterFactory.getInstance().createReportWriter();
        ReturnCode saved = writer.save(report);
        return saved;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.resourcehelper.ResourceFileMap#checkFile(org.eclipse.core.resources.IFile)
     */
    @Override
    protected boolean checkFile(IFile file) {
        return file != null && FactoriesUtil.REP.equalsIgnoreCase(file.getFileExtension());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dq.helper.resourcehelper.ResourceFileMap#findCorrespondingFile(orgomg.cwm.objectmodel.core.ModelElement
     * )
     */
    @Override
    public IFile findCorrespondingFile(ModelElement element) {
        if (allRepMap == null || allRepMap.isEmpty()) {
            getAllReports();
        }

        Iterator<IFile> iterator = allRepMap.keySet().iterator();
        while (iterator.hasNext()) {
            IFile next = iterator.next();

            if (ResourceHelper.areSame(element, allRepMap.get(next))) {
                return next;
            }
        }
        return null;
    }
}
