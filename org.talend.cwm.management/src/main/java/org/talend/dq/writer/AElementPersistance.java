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
package org.talend.dq.writer;

import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Information;
import org.talend.core.model.properties.InformationLevel;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.Property;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.properties.PropertiesFactory;
import org.talend.dq.helper.ModelElementIdentifier;
import org.talend.dq.helper.PropertyHelper;
import org.talend.resource.ResourceManager;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.analysis.informationvisualization.RenderedObject;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public abstract class AElementPersistance {

    private static Logger log = Logger.getLogger(AElementPersistance.class);

    protected EMFSharedResources util = EMFSharedResources.getInstance();

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.IElementPersistence#create(orgomg.cwm.objectmodel.core.ModelElement,
     * org.eclipse.core.resources.IFolder)
     */
    public TypedReturnCode<Object> create(ModelElement element, IFolder folder) {
        TypedReturnCode<Object> trc = new TypedReturnCode<Object>();

        if (getFileExtension() == null) {
            trc.setMessage("File extension is null.");
            log.error("Get file extension error");
        } else {

            String fname = DqRepositoryViewService.createLogicalFileName(element, getFileExtension());
            IFile file = folder.getFile(fname);

            if (file.exists()) {
                // MOD yyi 2009-10-15 Feature: 9524
                String oriName = element.getName();
                element.setName(element.getName() + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
                fname = DqRepositoryViewService.createLogicalFileName(element, getFileExtension());
                file = folder.getFile(fname);

                element.setName(oriName);
                ReturnCode rc = create(element, file);
                trc.setReturnCode(rc.getMessage(), rc.isOk(), file);

                // trc.setReturnCode("Can't create resource file, file is existed.", false);
            } else {
                ReturnCode rc = create(element, file);
                trc.setReturnCode(rc.getMessage(), rc.isOk(), file);
            }
        }

        return trc;
    }

    /**
     * DOC bZhou Comment method "create".
     * 
     * @param element
     * @param file
     * @return
     */
    public ReturnCode create(ModelElement element, IFile file) {
        return create(element, file, true);
    }

    /**
     * DOC bZhou Comment method "create".
     * 
     * @param element
     * @param file
     * @param withProperty
     * @return
     */
    public ReturnCode create(ModelElement element, IFile file, boolean withProperty) {
        ReturnCode rc = new ReturnCode();

        if (!check(file)) {
            rc.setReturnCode("Failed to save! the extent file name is wrong.", false);
        } else {

            IPath itemPath = file.getFullPath();
            IPath propPath = itemPath.removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

            if (withProperty) {
                createProperty(element, propPath);
            }

            if (!util.addEObjectToResourceSet(itemPath.toString(), element)) {
                rc.setReturnCode("Failed to save: " + util.getLastErrorMessage(), false);
            } else {
                if (element instanceof RenderedObject) {
                    ((RenderedObject) element).setFileName(itemPath.toString());
                }

                rc = save(element);
            }
        }

        return rc;
    }

    /**
     * DOC bZhou Comment method "createProperty".
     * 
     * @param modelElement
     * @param propPath
     * @return
     */
    public Property createProperty(ModelElement modelElement, IPath propPath) {
        Property property = createProperty(modelElement);

        util.addEObjectToResourceSet(propPath.toString(), property);
        property.getItem().getState().setPath(computePath(property));
        saveProperty(property);

        return property;
    }

    /**
     * DOC bZhou Comment method "createProperty".
     * 
     * @param modelElement
     * @return
     */
    public Property createProperty(ModelElement modelElement) {
        Property property = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createProperty();
        property.setAuthor(ReponsitoryContextBridge.getUser());

        String purpose = MetadataHelper.getPurpose(modelElement);
        String description = MetadataHelper.getDescription(modelElement);
        String version = MetadataHelper.getVersion(modelElement);
        String status = MetadataHelper.getDevStatus(modelElement);

        property.setId(EcoreUtil.generateUUID());
        property.setLabel(modelElement.getName());
        property.setPurpose(purpose);
        property.setDescription(description);
        property.setStatusCode(status);
        property.setVersion(version);
        property.setCreationDate(new Date());

        computePropertyMaxInformationLevel(property);

        Item item = createItem(modelElement);
        property.setItem(item);
        item.setProperty(property);

        return property;
    }

    private String computePath(Property property) {
        Resource eResource = property.eResource();
        if (eResource != null) {
            IPath propPath, typedPath;

            URI propURI = eResource.getURI();
            if (propURI.isPlatform()) {
                propPath = new Path(propURI.toPlatformString(true)).removeLastSegments(1);
                typedPath = ResourceManager.getRootProject().getFullPath().append(PropertyHelper.getItemTypedPath(property));

                IPath itemPath = propPath.makeRelativeTo(typedPath);

                return itemPath.toString();
            }
        }

        return "";
    }

    private void computePropertyMaxInformationLevel(Property property) {
        EList<Information> informations = property.getInformations();
        InformationLevel maxLevel = null;
        for (Information information : informations) {
            int value = information.getLevel().getValue();
            if (maxLevel == null || value > maxLevel.getValue()) {
                maxLevel = information.getLevel();
            }
        }
        property.setMaxInformationLevel(maxLevel);
    }

    /**
     * DOC bZhou Comment method "saveProperty".
     * 
     * @param property
     * @return
     */
    public ReturnCode saveProperty(Property property) {
        ReturnCode rc = new ReturnCode();

        property.setModificationDate(new Date());

        Resource propertyResource = property.eResource();
        propertyResource.getContents().add(property);
        propertyResource.getContents().add(property.getItem());
        propertyResource.getContents().add(property.getItem().getState());

        rc.setOk(util.saveResource(propertyResource));

        return rc;
    }

    /**
     * DOC bZhou Comment method "save".
     * 
     * @param element
     * @return
     */
    public ReturnCode save(ModelElement element) {
        ReturnCode rc = new ReturnCode();

        addDependencies(element);

        addResourceContent(element);

        rc.setOk(util.saveResource(element.eResource()));

        if (rc.isOk()) {
            rc.setMessage("save " + element.getName() + " is OK!");
            ProxyRepositoryManager.getInstance().save();
        } else {
            rc.setMessage(util.getLastErrorMessage());
        }

        return rc;
    }

    public Item createItem(ModelElement element) {
        Item item = null;
        // MOD mzhao feature 13114, 2010-05-19 distinguish tdq items.
        if (ModelElementIdentifier.isAnalysis(element)) {
            item = PropertiesFactory.eINSTANCE.createTDQAnalysisItem();
        } else if (ModelElementIdentifier.isDQRule(element)) {
            item = PropertiesFactory.eINSTANCE.createTDQBusinessRuleItem();
        } else if (ModelElementIdentifier.isDataProvider(element)) {
            if (element instanceof DatabaseConnection) {
                item = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createDatabaseConnectionItem();
            } else if (element instanceof MDMConnection) {
                item = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createMDMConnectionItem();
            }
            ((ConnectionItem) item).setConnection((Connection) element);
        } else if (ModelElementIdentifier.isID(element)) {
            item = PropertiesFactory.eINSTANCE.createTDQIndicatorDefinitionItem();
        } else if (ModelElementIdentifier.isPattern(element)) {
            item = PropertiesFactory.eINSTANCE.createTDQPatternItem();
        } else if (ModelElementIdentifier.isReport(element)) {
            item = PropertiesFactory.eINSTANCE.createTDQReportItem();
        } else {
            item = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createTDQItem();
        }

        ItemState itemState = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createItemState();
        itemState.setDeleted(false);
        item.setState(itemState);

        return item;
    }

    /**
     * DOC bZhou Comment method "check".
     * 
     * @param file
     * @return
     */
    protected boolean check(IFile file) {
        return getFileExtension().equalsIgnoreCase(file.getFileExtension());
    }

    /**
     * DOC bZhou Comment method "addDependencies".
     * 
     * @param element
     */
    protected abstract void addDependencies(ModelElement element);

    /**
     * DOC bZhou Comment method "addResourceContent".
     * 
     * @param element
     * @return
     */
    protected abstract void addResourceContent(ModelElement element);

    /**
     * DOC bZhou Comment method "getFileExtension".
     * 
     * @return
     */
    protected abstract String getFileExtension();

}
