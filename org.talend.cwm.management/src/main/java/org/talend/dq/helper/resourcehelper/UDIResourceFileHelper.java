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
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.util.DefinitionSwitch;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.IndicatorDefinitionWriter;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class UDIResourceFileHelper extends ResourceFileMap {

    private static Logger log = Logger.getLogger(UDIResourceFileHelper.class);

    private static UDIResourceFileHelper instance;

    private Map<IFile, IndicatorDefinition> idsMap = new HashMap<IFile, IndicatorDefinition>();

    private UDIResourceFileHelper() {
        super();
    }

    public static UDIResourceFileHelper getInstance() {
        if (instance == null) {
            instance = new UDIResourceFileHelper();
        }
        return instance;
    }

    public IndicatorDefinition findUDI(IFile file) {
        if (checkFile(file)) {
            IndicatorDefinition id = idsMap.get(file);
            if (id == null) {
                id = retireUDI(getFileResource(file));
            }

            idsMap.put(file, id);

            return id;
        }

        return null;
    }

    private IndicatorDefinition retireUDI(Resource fileResource) {
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

    public Collection<IndicatorDefinition> getAllUDIs(IFolder patternFodler) {

        try {
            searchAllUDIs(patternFodler);
        } catch (CoreException e) {
            log.error(e, e);
        }

        return idsMap.values();
    }

    public Collection<IndicatorDefinition> getAllUDIs() {
        IFolder udiFolder = ResourceManager.getLibrariesFolder().getFolder("Indicators");
        return getAllUDIs(udiFolder);
    }

    public IFile getUDIFile(IndicatorDefinition id, IFolder[] folders) {
        IFile file = null;

        try {
            for (int i = 0; i < folders.length; i++) {
                searchAllUDIs(folders[i]);
            }
        } catch (CoreException e) {
            log.error(e, e);
        }

        Set<IFile> keySet = idsMap.keySet();
        for (IFile file2 : keySet) {
            IndicatorDefinition id2 = idsMap.get(file2);
            Expression e = id.getSqlGenericExpression().get(0);
            Expression e2 = id2.getSqlGenericExpression().get(0);
            String et = e.getLanguage();
            String et2 = e2.getLanguage();
            if (id2.getName().equals(id.getName())) {
                boolean b = et == null && et2 == null;
                b = b || (et != null && et.equals(et2));
                if (b) {
                    file = file2;
                }
            }
        }
        return file;
    }

    private void searchAllUDIs(IFolder folder) throws CoreException {
        for (IResource resource : folder.members()) {
            if (resource.getType() == IResource.FOLDER) {
                searchAllUDIs(folder.getFolder(resource.getName()));
                continue;
            }
            IFile file = (IFile) resource;
            if (checkFile(file)) {
                findUDI(file);
            }
        }
    }

    public ReturnCode save(IndicatorDefinition indicator) {
        IndicatorDefinitionWriter writer = ElementWriterFactory.getInstance().createIndicatorDefinitionWriter();
        ReturnCode saved = writer.save(indicator);
        return saved;
    }

    @Override
    public void remove(IFile file) {
        super.remove(file);
        idsMap.remove(file);
    }

    @Override
    public void clear() {
        super.clear();
        idsMap.clear();
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
        if (idsMap == null || idsMap.isEmpty()) {
            getAllUDIs();
        }

        Iterator<IFile> iterator = idsMap.keySet().iterator();
        while (iterator.hasNext()) {
            IFile next = iterator.next();

            if (ResourceHelper.areSame(element, idsMap.get(next))) {
                return next;
            }
        }
        return null;
    }
}
