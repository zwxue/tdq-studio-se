// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.talend.commons.exception.BusinessException;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.helper.ResourceHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.migration.impl.UpdateUDIIndicatorsWithNewModelTask;
import org.talend.dataprofiler.core.ui.utils.UDIUtils;
import org.talend.dataquality.helpers.IndicatorCategoryHelper;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.userdefine.UDIndicatorDefinition;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.indicators.definitions.DefinitionHandler;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class UDIHandle extends EMFResourceHandle {

    Logger log = Logger.getLogger(UDIHandle.class);

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

        // reload is needed after duplicate an indicator.
        DefinitionHandler.getInstance().reloadIndicatorsDefinitions();

        // update the udi model to new model, not use the migration task
        if (!isUserDefCategory) {
            File ifileToFile = WorkspaceUtils.ifileToFile(duplicatedFile);
            Map<String, String> initIndicatorReplaceMap = UpdateUDIIndicatorsWithNewModelTask.initIndicatorReplaceMap();
            if (FilesUtils.migrateFile(ifileToFile, initIndicatorReplaceMap, log)) {
                try {
                    for (IRepositoryViewObject viewObject : ProxyRepositoryFactory.getInstance().getAll(
                            ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS)) {
                        ProxyRepositoryFactory.getInstance().reload(viewObject.getProperty());
                    }
                } catch (PersistenceException e) {
                    log.error(e);
                }
                ResourceService.refreshStructure();

                // add templates for udi only.
                List<IndicatorDefinition> indiDefinitions = DefinitionHandler.getInstance().getUserDefinedIndicatorDefinitions();
                for (IndicatorDefinition indiDefinition : indiDefinitions) {
                    if (indiDefinition instanceof UDIndicatorDefinition) {
                        String name = indiDefinition.getLabel() != null ? indiDefinition.getLabel() : indiDefinition.getName();
                        if (name != null && name.equals(newLabel)) {
                            UDIndicatorDefinition udi = (UDIndicatorDefinition) indiDefinition;
                            udi = UDIUtils.createDefaultDrillDownList(udi);
                            ElementWriterFactory.getInstance().createIndicatorDefinitionWriter().save(udi);
                            break;
                        }
                    }
                }
            }
        }

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
