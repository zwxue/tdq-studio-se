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
package org.talend.dataprofiler.core.helper.resourcehelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFUtil;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.domain.pattern.util.PatternSwitch;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;

/**
 * DOC rli class global comment. Detailled comment
 */
public final class PatternResourceFileHelper extends ResourceFileMap {

    private static Logger log = Logger.getLogger(PatternResourceFileHelper.class);

    private static PatternResourceFileHelper instance;

    private Map<IFile, Pattern> patternsMap = new HashMap<IFile, Pattern>();

    private PatternResourceFileHelper() {
        super();
    }

    public static PatternResourceFileHelper getInstance() {
        if (instance == null) {
            instance = new PatternResourceFileHelper();
        }
        return instance;
    }

    /**
     * DOC xy Comment method "findPathAnalysis".
     * 
     * @param file
     * @return
     */
    public Pattern findPattern(IFile file) {
        Pattern pattern = patternsMap.get(file);
        if (pattern != null) {
            return pattern;
        }
        Resource fileResource = getFileResource(file);
        pattern = retirePattern(fileResource);
        if (pattern != null) {
            patternsMap.put(file, pattern);
        }
        return pattern;
    }

    /**
     * DOC zqin Comment method "getAllAnalysis".
     * 
     * @return
     */
    public Collection<Pattern> getAllPatternes() {
        if (resourcesNumberChanged) {
            // patternsMap.clear();
            IFolder defaultPatternFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(DQStructureManager.LIBRARIES)
                    .getFolder(DQStructureManager.PATTERNS);
            try {
                searchAllPatternes(defaultPatternFolder);
            } catch (CoreException e) {
                e.printStackTrace();
            }
            resourcesNumberChanged = false;
        }
        return patternsMap.values();
    }

    /**
     * DOC zqin Comment method "searchAllPatternes".
     * 
     * @param folder
     * @throws CoreException
     */
    private void searchAllPatternes(IFolder folder) throws CoreException {
        for (IResource resource : folder.members()) {
            if (resource.getType() == IResource.FOLDER) {
                searchAllPatternes(folder.getFolder(resource.getName()));
                continue;
            }
            IFile file = (IFile) resource;
            findPattern(file);

        }
    }

    /**
     * DOC rli Comment method "retireAnalysis".
     * 
     * @param fileResource
     * @return
     */
    private Pattern retirePattern(Resource fileResource) {
        EList<EObject> contents = fileResource.getContents();
        if (contents.isEmpty()) {
            log.error("No content in " + fileResource);
        }
        if (log.isDebugEnabled()) {
            log.debug("Nb elements in contents " + contents.size());
        }
        PatternSwitch<Pattern> mySwitch = new PatternSwitch<Pattern>() {

            public Pattern casePattern(Pattern object) {
                return object;
            }
        };
        Pattern pattern = null;
        if (contents != null && contents.size() != 0) {
            pattern = mySwitch.doSwitch(contents.get(0));
        }
        return pattern;
    }

    public void remove(IFile file) {
        super.remove(file);
        this.patternsMap.remove(file);
    }

    public void clear() {
        super.clear();
        this.patternsMap.clear();
    }

    /**
     * DOC qzhang Comment method "createPattern".
     * 
     * @param name
     * @param author
     * @param description
     * @param purpose
     * @param status
     * @return
     */
    public Pattern createPattern(String name, String author, String description, String purpose, String status) {
        Pattern pattern = PatternFactory.eINSTANCE.createPattern();
        pattern.setName(name);
        TaggedValueHelper.setAuthor(pattern, author == null ? "" : author);
        TaggedValueHelper.setDescription(description == null ? "" : description, pattern);
        TaggedValueHelper.setPurpose(purpose == null ? "" : purpose, pattern);
        TaggedValueHelper.setDevStatus(pattern, DevelopmentStatus.get(status == null ? "" : status));

        return pattern;
    }

    public boolean save(Pattern pattern) {
        boolean saved = EMFUtil.saveSingleResource(pattern.eResource());
        // if (saved) {
        // setResourcesNumberChanged(true);
        // }
        return saved;
    }

    /**
     * DOC qzhang Comment method "getPatternFile".
     * 
     * @param pattern
     */
    public IFile getPatternFile(Pattern pattern, IFolder[] folders) {
        IFile file = null;
        if (resourcesNumberChanged) {
            try {
                for (int i = 0; i < folders.length; i++) {
                    searchAllPatternes(folders[i]);
                }
            } catch (CoreException e) {
                e.printStackTrace();
            }
            resourcesNumberChanged = false;
        }
        Set<IFile> keySet = patternsMap.keySet();
        for (IFile file2 : keySet) {
            Pattern pattern2 = patternsMap.get(file2);
            RegularExpression e2 = (RegularExpression) pattern.getComponents().get(0);
            RegularExpression e = (RegularExpression) pattern2.getComponents().get(0);
            String et = e.getExpressionType();
            String et2 = e2.getExpressionType();
            if (pattern2.getName().equals(pattern.getName())) {
                boolean b = et == null && et2 == null;
                b = b || (et != null && et.equals(et2));
                if (b) {
                    file = file2;
                }
            }
        }
        return file;
    }
}
