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
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.Information;
import org.talend.core.model.properties.InformationLevel;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.core.model.properties.User;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.analysis.informationvisualization.RenderedObject;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public abstract class AElementPersistance implements IElementPersistence, IElementSerialize {

    private static Logger log = Logger.getLogger(AElementPersistance.class);

    protected EMFSharedResources util = EMFSharedResources.getInstance();

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.IElementPersistence#create(orgomg.cwm.objectmodel.core.ModelElement,
     * org.eclipse.core.resources.IFolder)
     */
    public TypedReturnCode<IFile> create(ModelElement element, IFolder folder) {
        TypedReturnCode<IFile> trc = new TypedReturnCode<IFile>();

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
                ReturnCode rc = save(element, file);
                trc.setReturnCode(rc.getMessage(), rc.isOk(), file);

                // trc.setReturnCode("Can't create resource file, file is existed.", false);
            } else {
                ReturnCode rc = save(element, file);
                trc.setReturnCode(rc.getMessage(), rc.isOk(), file);
            }
        }

        return trc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.IElementPersistence#save(orgomg.cwm.objectmodel.core.ModelElement,
     * org.eclipse.core.resources.IFile)
     */
    public ReturnCode save(ModelElement element, IFile file) {
        ReturnCode rc = new ReturnCode();

        if (!check(file)) {
            rc.setReturnCode("Failed to save pattern, the extent file name is wrong.", false);
        } else {

            String filePath = file.getFullPath().toString();
            if (!util.addEObjectToResourceSet(filePath, element)) {
                rc.setReturnCode("Failed to save pattern: " + util.getLastErrorMessage(), false);
            } else {
                if (element instanceof RenderedObject) {
                    ((RenderedObject) element).setFileName(filePath);
                }

                rc = save(element);
            }
        }

        return rc;
    }

    /**
     * 
     * DOC mzhao bug:9012.
     * 
     * @param element
     * @param file
     * @return
     */
    public ReturnCode saveWithoutProperty(ModelElement element, IFile file) {
        ReturnCode rc = new ReturnCode();

        if (!check(file)) {
            rc.setReturnCode("Failed to element, the extent file name is wrong.", false);
        } else {
            String filePath = file.getFullPath().toString();
            if (!util.addEObjectToResourceSet(filePath, element)) {
                rc.setReturnCode("Failed to save pattern: " + util.getLastErrorMessage(), false);
            } else {
                addResourceContent(element);
                addDependencies(element);
                rc.setOk(util.saveResource(element.eResource()));
                if (rc.isOk()) {
                    rc.setMessage("save " + element.getName() + " is OK!");
                } else {
                    rc.setMessage(util.getLastErrorMessage());
                }
                if (rc.isOk() && element instanceof RenderedObject) {
                    ((RenderedObject) element).setFileName(file.getFullPath().toString());
                }
            }
        }
        return rc;
    }

    /**
     * DOC bZhou Comment method "savePerperties".
     * 
     * @param element
     */
    public void savePerperties(ModelElement element) {
        Resource resource = element.eResource();
        String fileName = resource.getURI().lastSegment();

        Property property = initProperty(element);
        TDQItem item = initItem(element, property);
        item.setFilename(fileName);

        URI uri = element.eResource().getURI();
        serialize(property, uri);

        String propertyPath = property.eResource().getURI().toPlatformString(true);
        MetadataHelper.setPropertyPath(propertyPath, element);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.IElementPersistence#save(orgomg.cwm.objectmodel.core.ModelElement)
     */
    public ReturnCode save(ModelElement element) {
        ReturnCode rc = new ReturnCode();

        addDependencies(element);

        addResourceContent(element);

        savePerperties(element);

        rc.setOk(util.saveResource(element.eResource()));

        if (rc.isOk()) {
            rc.setMessage("save " + element.getName() + " is OK!");
            ProxyRepositoryManager.getInstance().save();
        } else {
            rc.setMessage(util.getLastErrorMessage());
        }

        return rc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.IElementSerialize#initProperty(orgomg.cwm.objectmodel.core.ModelElement)
     */
    public Property initProperty(ModelElement element) {
        Property property = PropertiesFactory.eINSTANCE.createProperty();

        String author = MetadataHelper.getAuthor(element);
        String purpose = MetadataHelper.getPurpose(element);
        String description = MetadataHelper.getDescription(element);
        String version = MetadataHelper.getVersion(element);
        String status = MetadataHelper.getDevStatus(element);

        User user = ReponsitoryContextBridge.getUser();
        if (user != null) {
            user.setLogin(author);
            property.setAuthor(user);
        }

        property.setId(EcoreUtil.generateUUID());
        property.setLabel(element.getName());
        property.setPurpose(purpose);
        property.setDescription(description);
        property.setStatusCode(status);
        property.setVersion(version);
        property.setCreationDate(new Date());

        computePropertyMaxInformationLevel(property);

        return property;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.IElementSerialize#initItem(orgomg.cwm.objectmodel.core.ModelElement,
     * org.talend.core.model.properties.Property, java.lang.String)
     */
    public TDQItem initItem(ModelElement element, Property property) {
        TDQItem item = PropertiesFactory.eINSTANCE.createTDQItem();

        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        itemState.setDeleted(false);
        itemState.setPath("");
        item.setState(itemState);
        item.setProperty(property);
        return item;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.IElementSerialize#serialize(org.talend.core.model.properties.Property,
     * org.eclipse.emf.common.util.URI)
     */
    public ReturnCode serialize(Property property, URI uri) {
        ReturnCode rc = new ReturnCode();

        URI propertiesURI = uri.trimFileExtension().appendFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
        Resource propertyResource = util.createResource(propertiesURI);

        propertyResource.getContents().add(property);
        propertyResource.getContents().add(property.getItem());
        propertyResource.getContents().add(property.getItem().getState());

        rc.setOk(util.saveResource(propertyResource));

        return rc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.IElementPersistence#check(org.eclipse.core.resources.IFile)
     */
    public boolean check(IFile file) {
        return getFileExtension().equalsIgnoreCase(file.getFileExtension());
    }

    /**
     * DOC bZhou Comment method "computePropertyMaxInformationLevel".
     * 
     * @param property
     */
    protected void computePropertyMaxInformationLevel(Property property) {
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
