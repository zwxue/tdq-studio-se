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
import org.talend.dataquality.expressions.TdExpression;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.util.DefinitionSwitch;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.dq.writer.impl.IndicatorDefinitionWriter;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.Expression;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC xqliu class global comment. Detailled comment MOD mzhao 13676,split system indicators. 2010-07-08
 */
public class IndicatorResourceFileHelper extends ResourceFileMap {

    private static Logger log = Logger.getLogger(IndicatorResourceFileHelper.class);

    private static IndicatorResourceFileHelper instance;

    private Map<IFile, IndicatorDefinition> idsMap = new HashMap<IFile, IndicatorDefinition>();

    private IndicatorResourceFileHelper() {
        super();
    }

    public static IndicatorResourceFileHelper getInstance() {
        if (instance == null) {
            instance = new IndicatorResourceFileHelper();
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

    /**
     * 
     * DOC mzhao Get all indicators (depend on the folder which is SYSTEM or USER DEFINE).
     * 
     * @param IndicatorFodler
     * @return
     */
    public Collection<IndicatorDefinition> getAllIndicators(IFolder IndicatorFodler) {

        try {
            searchAllIndicators(IndicatorFodler);
        } catch (CoreException e) {
            log.error(e, e);
        }

        return idsMap.values();
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

    public IFile getIndicatorFile(IndicatorDefinition id, IFolder[] folders) {
        IFile file = null;

        try {
            for (int i = 0; i < folders.length; i++) {
                searchAllIndicators(folders[i]);
            }
        } catch (CoreException e) {
            log.error(e, e);
        }

        Set<IFile> keySet = idsMap.keySet();
        for (IFile file2 : keySet) {
            IndicatorDefinition id2 = idsMap.get(file2);
            //MOD qiongli bug 13994, if contains java language indicator,handle it as follows
			EList<TdExpression> ls = id.getSqlGenericExpression();
			EList<TdExpression> ls2 = id2.getSqlGenericExpression();
			if (ls.size() == 0) {
				if (ResourceHelper.areSame(id, id2)) {
					file = file2;
					break;
				} else {
					continue;
				}
			}
			if (ls2.size() == 0)
				continue;
            //~
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

    private void searchAllIndicators(IFolder folder) throws CoreException {
        for (IResource resource : folder.members()) {
            if (resource.getType() == IResource.FOLDER) {
                searchAllIndicators(folder.getFolder(resource.getName()));
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
