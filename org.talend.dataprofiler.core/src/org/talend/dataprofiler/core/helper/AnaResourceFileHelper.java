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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.util.AnalysisSwitch;
import org.talend.dq.analysis.AnalysisWriter;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.analysis.informationvisualization.RenderedObject;

/**
 * DOC rli class global comment. Detailled comment
 */
public final class AnaResourceFileHelper extends ResourceFileMap {

    private static Logger log = Logger.getLogger(AnaResourceFileHelper.class);

    private static AnaResourceFileHelper instance;

    private Map<IFile, AnalysisEntity> allAnalysisMap = new HashMap<IFile, AnalysisEntity>();

    private AnaResourceFileHelper() {
        super();
    }

    public static AnaResourceFileHelper getInstance() {
        if (instance == null) {
            instance = new AnaResourceFileHelper();
        }
        return instance;
    }

    public Collection<AnalysisEntity> getAllAnalysis() {
        if (resourceChanged) {
            allAnalysisMap.clear();
            IFolder defaultAnalysFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(
                    PluginConstant.DATA_PROFILING_PROJECTNAME).getFolder(DQStructureManager.ANALYSIS);
            try {
                searchAllAnalysis(defaultAnalysFolder);
            } catch (CoreException e) {
                e.printStackTrace();
            }
            resourceChanged = false;
        }
        return allAnalysisMap.values();
    }

    private void searchAllAnalysis(IFolder folder) throws CoreException {
        for (IResource resource : folder.members()) {
            if (resource.getType() == IResource.FOLDER) {
                searchAllAnalysis(folder.getFolder(resource.getName()));
                continue;
            }
            IFile file = (IFile) resource;
            findAnalysis(file);

        }
    }

    /**
     * DOC xy Comment method "findPathAnalysis".
     * 
     * @param file
     * @return
     */
    public Analysis findAnalysis(IFile file) {
        AnalysisEntity analysisEntity = allAnalysisMap.get(file);
        if (analysisEntity != null) {
            return analysisEntity.getAnalysis();
        }
        Resource fileResource = getFileResource(file);
        Analysis analysis = retireAnalysis(fileResource);
        if (analysis != null) {
            AnalysisEntity entity = new AnalysisEntity(analysis);
            allAnalysisMap.put(file, entity);
        }
        return analysis;
    }

    public List<IFile> findCorrespondingFile(List<RenderedObject> renderObjs) {
        this.getAllAnalysis();
        List<IFile> fileList = new ArrayList<IFile>();
        for (int i = 0; i < renderObjs.size(); i++) {
            if (this.registedResourceMap.containsValue(renderObjs.get(i).eResource())) {
                Iterator<IFile> iterator = this.registedResourceMap.keySet().iterator();
                while (iterator.hasNext()) {
                    IFile next = iterator.next();
                    if (registedResourceMap.get(next) == renderObjs.get(i).eResource()) {
                        fileList.add(next);
                    }
                }
            }
        }
        return fileList;
    }

    /**
     * DOC rli Comment method "retireAnalysis".
     * 
     * @param fileResource
     * @return
     */
    private Analysis retireAnalysis(Resource fileResource) {
        EList<EObject> contents = fileResource.getContents();
        if (contents.isEmpty()) {
            log.error("No content in " + fileResource);
        }
        if (log.isDebugEnabled()) {
            log.debug("Nb elements in contents " + contents.size());
        }
        AnalysisSwitch<Analysis> mySwitch = new AnalysisSwitch<Analysis>() {

            public Analysis caseAnalysis(Analysis object) {
                return object;
            }
        };
        Analysis analysis = null;
        if (contents != null && contents.size() != 0) {
            analysis = mySwitch.doSwitch(contents.get(0));
        }
        return analysis;
    }

    public void remove(IFile file) {
        super.remove(file);
        this.allAnalysisMap.remove(file);
    }

    public void clear() {
        super.clear();
        this.allAnalysisMap.clear();
    }

    public ReturnCode save(Analysis analysis) {
        AnalysisWriter writer = new AnalysisWriter();
        // MODSCA 20080425 do not overwrite existing file.
        // File file = new File(analysis.getUrl());
        ReturnCode saved = writer.save(analysis);
        if (saved.isOk()) {
            setResourceChanged(true);
        }
        return saved;
    }
}
