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

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.util.DefinitionSwitch;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.IndicatorDefinitionWriter;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. Detailled comment MOD mzhao 13676,split system indicators. 2010-07-08
 */
public final class IndicatorResourceFileHelper extends ResourceFileMap {

    private static Logger log = Logger.getLogger(IndicatorResourceFileHelper.class);

    private static IndicatorResourceFileHelper instance;

    private IndicatorResourceFileHelper() {
        super();
    }

    public static IndicatorResourceFileHelper getInstance() {
        if (instance == null) {
            instance = new IndicatorResourceFileHelper();
        }
        return instance;
    }

    public IndicatorDefinition findIndDefinition(IFile file) {
        if (checkFile(file)) {
            IndicatorDefinition id = recognizeIndicatorDefinition(getFileResource(file));
            return id;
        }
        return null;
    }

    private IndicatorDefinition recognizeIndicatorDefinition(Resource fileResource) {
        EList<EObject> contents = fileResource.getContents();
        if (contents.isEmpty()) {
            log.error("No content in " + fileResource);
        }
        if (log.isDebugEnabled()) {
            log.debug("Nb elements in contents " + contents.size());
        }
        DefinitionSwitch<IndicatorDefinition> mySwitch = new DefinitionSwitch<IndicatorDefinition>() {

            @Override
            public IndicatorDefinition caseIndicatorDefinition(IndicatorDefinition object) {
                return object;
            }

        };
        IndicatorDefinition id = null;
        if (contents != null && contents.size() != 0) {
            id = mySwitch.doSwitch(contents.get(0));
        }
        return id;
    }

    /**
     * 
     * DOC mzhao Get all indicators (depend on the folder which is SYSTEM or USER DEFINE).
     * 
     * @param IndicatorFodler
     * @return
     */
    public Collection<IndicatorDefinition> getAllIndicators(IFolder indicatorFodler) {
        Collection<IndicatorDefinition> indDefs = searchAllIndicators(indicatorFodler);
        return indDefs;
    }

    /**
     * 
     * DOC zshen Fetch all system indicators.
     * 
     * @return
     */
    public Collection<IndicatorDefinition> getAllSysIs() {
        IFolder udiFolder = ResourceManager.getLibrariesFolder().getFolder(EResourceConstant.INDICATORS.getName())
                .getFolder(EResourceConstant.SYSTEM_INDICATORS.getName());
        return getAllIndicators(udiFolder);
    }

    /**
     * 
     * DOC mzhao Fetch all user defined indicators.
     * 
     * @return
     */
    public Collection<IndicatorDefinition> getAllUDIs() {
        IFolder udiFolder = ResourceManager.getLibrariesFolder().getFolder(EResourceConstant.INDICATORS.getName()).getFolder(
                EResourceConstant.USER_DEFINED_INDICATORS.getName());
        return getAllIndicators(udiFolder);
    }

    public IFile getIndicatorFile(IndicatorDefinition id) {
        if (id != null && id.eResource() != null) {
            return WorkspaceUtils.getModelElementResource(id);
        }
        return null;
    }

    private Collection<IndicatorDefinition> searchAllIndicators(IFolder folder) {
        Collection<IndicatorDefinition> definitions = new ArrayList<IndicatorDefinition>();
        try {
            for (IResource resource : folder.members()) {
                if (resource.getType() == IResource.FOLDER) {
                    definitions.addAll(searchAllIndicators(folder.getFolder(resource.getName())));
                    continue;
                }
                IFile file = (IFile) resource;
                if (checkFile(file)) {
                    definitions.add(findIndDefinition(file));
                }
            }
        } catch (CoreException e) {
            log.error(e);
        }
        return definitions;
    }

    public ReturnCode save(IndicatorDefinition indicator) {
        IndicatorDefinitionWriter writer = ElementWriterFactory.getInstance().createIndicatorDefinitionWriter();
        ReturnCode saved = writer.save(indicator);
        return saved;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.resourcehelper.ResourceFileMap#deleteRelated(org.eclipse.core.resources.IFile)
     */
    @Override
    protected void deleteRelated(IFile file) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.helper.resourcehelper.ResourceFileMap#checkFile(org.eclipse.core.resources.IFile)
     */
    @Override
    protected boolean checkFile(IFile file) {
        return file != null && FactoriesUtil.DEFINITION.equalsIgnoreCase(file.getFileExtension());
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
        if (element != null && element.eResource() != null) {
            return WorkspaceUtils.getModelElementResource(element);
        } else {
            return null;
        }
    }
}
