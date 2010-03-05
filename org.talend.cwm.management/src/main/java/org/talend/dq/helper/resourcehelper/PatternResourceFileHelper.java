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
package org.talend.dq.helper.resourcehelper;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.PatternFactory;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.domain.pattern.util.PatternSwitch;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.PatternWriter;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

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
        if (checkFile(file)) {
            Pattern pattern = patternsMap.get(file);
            if (pattern == null) {
                pattern = retirePattern(getFileResource(file));
            }

            patternsMap.put(file, pattern);

            return pattern;
        }
        return null;
    }

    /**
     * DOC zqin Comment method "getAllAnalysis".
     * 
     * @return
     */
    public Collection<Pattern> getAllPatternes(IFolder patternFodler) {
        if (resourcesNumberChanged) {
            try {
                searchAllPatternes(patternFodler);
            } catch (CoreException e) {
                log.error(e, e);
            }
            resourcesNumberChanged = false;
        }
        return patternsMap.values();
    }

    public Collection<Pattern> getAllPatternes() {
        IFolder patternFolder = ResourceManager.getLibrariesFolder().getFolder("Patterns");
        return getAllPatternes(patternFolder);
    }

    /**
     * DOC bZhou Comment method "getPatternes".
     * 
     * @param folder
     * @param allPattern
     */
    public void getPatternes(IFolder folder, List<Pattern> allPattern) {

        try {
            for (IResource resource : folder.members()) {
                if (resource.getType() == IResource.FOLDER) {
                    getPatternes(folder.getFolder(resource.getName()), allPattern);
                    continue;
                }

                IFile file = (IFile) resource;
                if (file.exists() && FactoriesUtil.isPatternFile(file.getFileExtension())) {
                    Resource fileResource = getFileResource(file);
                    Pattern pattern = retirePattern(fileResource);
                    if (pattern != null) {
                        allPattern.add(pattern);
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

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
            if (checkFile(file)) {
                findPattern(file);
            }
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
        MetadataHelper.setAuthor(pattern, author == null ? "" : author); //$NON-NLS-1$
        MetadataHelper.setDescription(description == null ? "" : description, pattern); //$NON-NLS-1$
        MetadataHelper.setPurpose(purpose == null ? "" : purpose, pattern); //$NON-NLS-1$
        // MOD mzhao feature 7479 2009-10-16
        MetadataHelper.setDevStatus(pattern, status == null ? "" : status); //$NON-NLS-1$
        return pattern;
    }

    public boolean save(Pattern pattern) {
        PatternWriter writer = ElementWriterFactory.getInstance().createPatternWriter();
        ReturnCode save = writer.save(pattern);
        return save.isOk();
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
                log.error(e, e);
            }
            resourcesNumberChanged = false;
        }
        Set<IFile> keySet = patternsMap.keySet();
        for (IFile file2 : keySet) {
            Pattern pattern2 = patternsMap.get(file2);
            EList<PatternComponent> components = pattern.getComponents();
            EList<PatternComponent> components2 = pattern2.getComponents();
            if (!components.isEmpty() && !components2.isEmpty()) {
                RegularExpression e2 = (RegularExpression) components.get(0);
                RegularExpression e = (RegularExpression) components2.get(0);
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
        }
        return file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.resourcehelper.ResourceFileMap#checkFile(org.eclipse.core.resources.IFile)
     */
    @Override
    protected boolean checkFile(IFile file) {
        return file != null && FactoriesUtil.PATTERN.equalsIgnoreCase(file.getFileExtension());
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
        if (patternsMap == null || patternsMap.isEmpty()) {
            getAllPatternes();
        }

        Iterator<IFile> iterator = patternsMap.keySet().iterator();
        while (iterator.hasNext()) {
            IFile next = iterator.next();

            if (ResourceHelper.areSame(element, patternsMap.get(next))) {
                return next;
            }
        }
        return null;
    }
}
