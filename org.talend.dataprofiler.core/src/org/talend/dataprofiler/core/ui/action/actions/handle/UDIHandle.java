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
package org.talend.dataprofiler.core.ui.action.actions.handle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class UDIHandle extends EMFResourceHandle {

    /**
     * DOC bZhou DuplicateUDIHandle constructor comment.
     */
    UDIHandle(Property propety) {
        super(propety);
    }

    UDIHandle(IRepositoryNode node) {
        super(node);
    }
    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.action.actions.duplicate.DuplicateEMFResourceHandle#extractFolder(orgomg.cwm.
     * objectmodel.core.ModelElement)
     */
    @Override
    protected IFolder extractFolder(ModelElement oldObject) {
        return ResourceManager.getUDIFolder();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.actions.handle.EMFResourceHandle#duplicate(java.lang.String)
     */
    @Override
    public IFile duplicate(String newLabel) {
        IFile duplicatedFile = super.duplicate(newLabel);
        IndicatorDefinition definition = (IndicatorDefinition) ModelElementFileFactory.getModelElement(duplicatedFile);
        // MOD klliu 2010-09-25 bug 15530 when duplicate the system indicator ,the definition must be reset the category
        // and the label
        // name
        UDIHelper.setUDICategory(definition, DefinitionHandler.getInstance().getUserDefinedCountIndicatorCategory());
        TaggedValueHelper.setValidStatus(true, definition);
        definition.setLabel(definition.getName());
        IndicatorResourceFileHelper.getInstance().save(definition);
        // MOD klliu duplicate successfully, refresh the duplicate session
        DefinitionHandler.reload();
        return duplicatedFile;
    }

    @Override
    public ReturnCode validDuplicated() {
        ReturnCode returnCode = new ReturnCode(true);
        String label = getProperty().getLabel();
        if (label.equals(PluginConstant.UNIQUE_COUNT) || label.equals(PluginConstant.BLANK_COUNT)
                || label.equals(PluginConstant.DEFAULT_VALUE_COUNT) || label.equals(PluginConstant.ROW_COUNT)
                || label.equals(PluginConstant.DUPLICATE_COUNT) || label.equals(PluginConstant.NULL_COUNT)
                || label.equals(PluginConstant.PATTERN_FREQUENCY_TABLE)
                || label.equals(PluginConstant.DATE_PATTERN_FREQUENCY_TABLE)
                || label.equals(PluginConstant.PATTERN_LOW_FREQUENCY_TABLE)) {
            return returnCode;
        }
        returnCode.setMessage("This '" + label + "' of Indicator's type could not be duplicated!");//$NON-NLS-1$

        returnCode.setOk(false);
        return returnCode;
    }
}
