// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.cwm.management.handler;

import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.core.repository.constants.FileConstants;
import org.talend.repository.items.importexport.handlers.imports.ImportRepTypeHandler;
import org.talend.repository.items.importexport.handlers.model.ItemRecord;
import org.talend.repository.items.importexport.manager.ResourcesManager;

public class TOPImportHandler extends ImportRepTypeHandler {

    public TOPImportHandler() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.repository.items.importexport.handlers.imports.ImportBasicHandler#computeItemRecord(org.talend.repository
     * .items.importexport.manager.ResourcesManager, org.eclipse.core.runtime.IPath)
     */
    @Override
    public ItemRecord computeItemRecord(ResourcesManager resManager, IPath path) {
        ItemRecord itemRecord = new ItemRecord(path);

        // Load the DQ specific resource such as patterns and rules.
        // Load resource only
        super.loadResource(resManager, itemRecord);

        IPath propertyPath = path.removeFileExtension().addFileExtension(FileConstants.PROPERTIES_EXTENSION);
        ResourceSet resSet = itemRecord.getResourceSet();
        itemRecord = new ItemRecord(propertyPath); 
        itemRecord.setResourceSet(resSet);

        // Load property resource.
        super.computeProperty(resManager, itemRecord);
        EcoreUtil.resolveAll(itemRecord.getResourceSet());

        return itemRecord;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.repository.items.importexport.handlers.imports.ImportBasicHandler#computeProperty(org.talend.repository
     * .items.importexport.manager.ResourcesManager, org.talend.repository.items.importexport.handlers.model.ItemRecord)
     */
    @Override
    protected void computeProperty(ResourcesManager manager, ItemRecord itemRecord) {
        // Empty implementation
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.repository.items.importexport.handlers.imports.ImportBasicHandler#isValidFile(org.eclipse.core.runtime
     * .IPath)
     */
    @Override
    protected boolean isValidFile(IPath path) {
        String[] exts = getResourceNeededExtensions();
        for (String ext : exts) {
            if (path.lastSegment().endsWith(ext)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.repository.items.importexport.handlers.imports.ImportBasicHandler#getResourceNeededExtensions()
     */
    @Override
    protected String[] getResourceNeededExtensions() {
        return new String[] { FileConstants.ITEM_EXTENSION, FileConstants.ANA_EXTENSION, FileConstants.REP_EXTENSION,
                FileConstants.PAT_EXTENSION, FileConstants.RULE_EXTENSION, FileConstants.DEF_EXTENSION };
    }
}
