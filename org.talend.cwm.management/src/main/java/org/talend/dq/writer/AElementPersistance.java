// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.EmfHelper;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Information;
import org.talend.core.model.properties.InformationLevel;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dq.helper.resourcehelper.ResourceFilenameHelper;
import org.talend.dq.helper.resourcehelper.ResourceFilenameHelper.FileName;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.analysis.informationvisualization.RenderedObject;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public abstract class AElementPersistance implements IElementPersistence, IElementSerialize {

    private static Logger log = Logger.getLogger(AElementPersistance.class);

    protected EMFSharedResources util = EMFSharedResources.getInstance();

    protected static ResourceSet resourceSet = new ResourceSetImpl();

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

            String fname = DqRepositoryViewService.createLogicalFileNmae(element, getFileExtension());
            IFile file = folder.getFile(fname);

            if (file.exists()) {
                trc.setReturnCode("Can't create resource file, file is existed.", false);
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
                savePerperties(element, file);
                rc = save(element);
                if (rc.isOk() && element instanceof RenderedObject) {
                    ((RenderedObject) element).setFileName(file.getFullPath().toString());
                }

            }
        }

        return rc;
    }

    public ReturnCode savePerperties(ModelElement element, IFile file) {
        ReturnCode rc = new ReturnCode();
        // Create properties.
        Property property = initProperty(element);
        TDQItem item = initItem(element, property, file.getName());
        IPath parentPath = file.getParent().getFullPath();
       
        // Add tagged value.
        TaggedValue taggedValue = TaggedValueHelper.createTaggedValue(TaggedValueHelper.TDQ_ELEMENT_FILE,  element.eResource().getURI()
                .toPlatformString(true));
        serialize(item, parentPath);
        
        property.eResource().getContents().add(taggedValue);
        try {
            EmfHelper.saveResource(property.eResource());
        } catch (PersistenceException e) {
            rc.setMessage(e.getMessage());
            log.error(e.getMessage(), e);
        }
        // Add tagged value.
        TaggedValueHelper.setTaggedValue(element, TaggedValueHelper.PROPERTY_FILE, property.eResource().getURI()
                .toPlatformString(true));        
         
        return rc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.IElementPersistence#save(orgomg.cwm.objectmodel.core.ModelElement)
     */
    public ReturnCode save(ModelElement element) {
        ReturnCode rc = new ReturnCode();

        addResourceContent(element);

        addDependencies(element);

        rc.setOk(util.saveResource(element.eResource()));

        if (rc.isOk()) {
            rc.setMessage("save " + element.getName() + " is OK!");
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

        // String author = MetadataHelper.getAuthor(element);
        String purpose = MetadataHelper.getPurpose(element);
        String description = MetadataHelper.getDescription(element);
        String version = MetadataHelper.getVersion(element);
        String status = MetadataHelper.getDevStatus(element).getLiteral();

        property.setId(EcoreUtil.generateUUID());

        // User user = PropertiesFactory.eINSTANCE.createTdqUser();
        // user.setLogin(author);
        // property.setAuthor(user);

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
    public TDQItem initItem(ModelElement element, Property property, String fileName) {
        TDQItem item = PropertiesFactory.eINSTANCE.createTDQItem();

        // ItemState itemState = PropertiesFactory.eINSTANCE.createITDQItemState();
        // itemState.setDeleted(false);
        // itemState.setPath("");
        // item.setState(itemState);

        item.setFilename(fileName);
        item.setProperty(property);

        return item;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.IElementSerialize#serialize(org.talend.core.model.properties.Item,
     * org.eclipse.core.runtime.IPath)
     */
    public ReturnCode serialize(Item item, IPath parentPath) {
        ReturnCode rc = new ReturnCode();

        FileName fileName = ResourceFilenameHelper.create(item.getProperty());
        IPath resourcePath = ResourceFilenameHelper.getExpectedFilePath(fileName, parentPath, FactoriesUtil.ITEM_EXTENSION);
        URI itemURI = URI.createPlatformResourceURI(resourcePath.toString(), false);
        // Resource itemResource = resourceSet.createResource(itemURI);
        // itemResource.getContents().add(item);

        URI propertiesURI = itemURI.trimFileExtension().appendFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);
        Resource propertyResource = resourceSet.createResource(propertiesURI);
        propertyResource.getContents().add(item.getProperty());
        propertyResource.getContents().add(item);

        try {
            // EmfHelper.saveResource(itemResource);
            EmfHelper.saveResource(propertyResource);
            rc.setOk(true);
        } catch (PersistenceException e) {
            rc.setMessage(e.getMessage());
            log.error(e.getMessage(), e);
        }

        return rc;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dq.writer.IElementPersistence#check(org.eclipse.core.resources.IFile)
     */
    public boolean check(IFile file) {
        return file.getFileExtension().equalsIgnoreCase(getFileExtension());
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
