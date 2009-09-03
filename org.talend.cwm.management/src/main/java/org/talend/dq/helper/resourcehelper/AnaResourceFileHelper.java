// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.util.AnalysisSwitch;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.writer.impl.AnalysisWriter;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.analysis.informationvisualization.RenderedObject;

/**
 * DOC rli class global comment. Detailled comment
 */
public final class AnaResourceFileHelper extends ResourceFileMap {

    private static Logger log = Logger.getLogger(AnaResourceFileHelper.class);

    private static AnaResourceFileHelper instance;

    private Map<IFile, Analysis> allAnalysisMap = new HashMap<IFile, Analysis>();

    private AnaResourceFileHelper() {
        super();
    }

    public static AnaResourceFileHelper getInstance() {
        if (instance == null) {
            instance = new AnaResourceFileHelper();
        }
        return instance;
    }

    public Collection<Analysis> getAllAnalysis(IFolder analysesFolder) {
        if (resourcesNumberChanged) {
            try {
                allAnalysisMap.clear();
                searchAllAnalysis(analysesFolder);
            } catch (CoreException e) {
                log.error(e, e);
            }
            resourcesNumberChanged = false;
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
            if (file.getFileExtension().equals(FactoriesUtil.ANA)) {
                findAnalysis(file);
            }
        }
    }

    /**
     * DOC xy Comment method "findPathAnalysis".
     * 
     * @param file
     * @return
     */
    public Analysis findAnalysis(IFile file) {
        Analysis analysisEntity = allAnalysisMap.get(file);
        if (analysisEntity != null) {
            return analysisEntity;
        }
        return readFromFile(file);
    }

    public Analysis readFromFile(IFile file) {
        this.remove(file);
        Resource fileResource = getFileResource(file);
        Iterator<IFile> fileIterator = allAnalysisMap.keySet().iterator();
        while (fileIterator.hasNext()) {
            IFile key = fileIterator.next();
            Analysis entity = allAnalysisMap.get(key);
            Resource resourceObj = entity.eResource();
            if (resourceObj == fileResource) {
                registedResourceMap.remove(key);
                allAnalysisMap.remove(key);
                break;
            }
        }
        Analysis analysis = retireAnalysis(fileResource);
        if (analysis != null) {
            allAnalysisMap.put(file, analysis);
        }
        return analysis;
    }

    public List<IFile> findCorrespondingFile(List<RenderedObject> renderObjs, IFolder analysesFolder) {
        this.getAllAnalysis(analysesFolder);
        List<IFile> fileList = new ArrayList<IFile>();
        for (int i = 0; i < renderObjs.size(); i++) {
            // if (this.registedResourceMap.containsValue(renderObjs.get(i).eResource())) {
            Iterator<IFile> iterator = this.registedResourceMap.keySet().iterator();
            Resource renderObjResource = renderObjs.get(i).eResource();
            while (iterator.hasNext()) {
                IFile next = iterator.next();
                if (registedResourceMap.get(next).getURI().toString().equals(renderObjResource.getURI().toString())) {
                    // this.register(next, renderObjResource);
                    fileList.add(next);
                    break;
                }
            }
            // }
        }
        return fileList;
    }

    /**
     * DOC rli Comment method "retireAnalysis".
     * 
     * @param fileResource
     * @return
     */
    public Analysis retireAnalysis(Resource fileResource) {
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
        String version = MetadataHelper.getVersion(analysis);

        AnalysisWriter writer = ElementWriterFactory.getInstance().createAnalysisWrite();
        ReturnCode saved = writer.save(analysis);
        return saved;
    }
}
