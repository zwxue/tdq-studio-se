// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import org.talend.commons.exception.BusinessException;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataquality.helpers.IndicatorCategoryHelper;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
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
    public IFile duplicate(String newLabel) throws BusinessException {
        IFile duplicatedFile = super.duplicate(newLabel);
        IndicatorDefinition definition = (IndicatorDefinition) ModelElementFileFactory.getModelElement(duplicatedFile);

        // MOD klliu 2010-09-25 bug 15530 when duplicate the system indicator ,the definition must be reset the
        // category and the label name
        boolean isUserDefCategory = IndicatorCategoryHelper.isUserDefCategory(UDIHelper.getUDICategory(definition));
        if (!isUserDefCategory) {
            IndicatorCategory category = null;
            String indDefUuid = ResourceHelper.getUUID(this.getModelElement());
            if (PluginConstant.PATTERN_FREQUENCY_TABLE_ID.equals(indDefUuid)
                    || PluginConstant.DATE_PATTERN_FREQUENCY_TABLE_ID.equals(indDefUuid)
                    || PluginConstant.PATTERN_LOW_FREQUENCY_TABLE_ID.equals(indDefUuid)) {
                category = DefinitionHandler.getInstance().getUserDefinedFrequencyIndicatorCategory();
            } else {
                category = DefinitionHandler.getInstance().getUserDefinedCountIndicatorCategory();
            }

            if (category != null) {
                UDIHelper.setUDICategory(definition, category);
            }
        }

        TaggedValueHelper.setValidStatus(true, definition);
        definition.setLabel(definition.getName());
        IndicatorResourceFileHelper.getInstance().save(definition);
        // MOD klliu duplicate successfully, refresh the duplicate session
        DefinitionHandler.reload();
        return duplicatedFile;
    }

    @Override
    public ReturnCode validDuplicated() {
        ReturnCode returnCode = new ReturnCode();

        String indDefUuid = ResourceHelper.getUUID(this.getModelElement());

        if (PluginConstant.UNIQUE_COUNT_ID.equals(indDefUuid) || PluginConstant.BLANK_COUNT_ID.equals(indDefUuid)
                || PluginConstant.DEFAULT_VALUE_COUNT_ID.equals(indDefUuid) || PluginConstant.ROW_COUNT_ID.equals(indDefUuid)
                || PluginConstant.DUPLICATE_COUNT_ID.equals(indDefUuid) || PluginConstant.NULL_COUNT_ID.equals(indDefUuid)
                || PluginConstant.PATTERN_FREQUENCY_TABLE_ID.equals(indDefUuid)
                || PluginConstant.DATE_PATTERN_FREQUENCY_TABLE_ID.equals(indDefUuid)
                || PluginConstant.PATTERN_LOW_FREQUENCY_TABLE_ID.equals(indDefUuid)) {
            returnCode.setOk(true);
        } else {
            returnCode.setOk(false);
            returnCode.setMessage(DefaultMessagesImpl.getString("UDIHandle.TypeDuplicated", getProperty().getDisplayName()));//$NON-NLS-1$
        }

        return returnCode;
    }
}
