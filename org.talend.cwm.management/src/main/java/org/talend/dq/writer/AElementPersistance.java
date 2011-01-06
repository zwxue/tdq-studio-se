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
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.bridge.ReponsitoryContextBridge;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.metadata.builder.database.DqRepositoryViewService;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.Information;
import org.talend.core.model.properties.InformationLevel;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.MDMConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.TDQItem;
import org.talend.core.model.properties.User;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.PropertiesFactory;
import org.talend.dataquality.properties.TDQAnalysisItem;
import org.talend.dataquality.properties.TDQBusinessRuleItem;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dataquality.properties.TDQJrxmlItem;
import org.talend.dataquality.properties.TDQPatternItem;
import org.talend.dataquality.properties.TDQReportItem;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.helper.ListUtils;
import org.talend.dq.helper.ModelElementIdentifier;
import org.talend.dq.helper.PropertyHelper;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.ReturnCode;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.analysis.informationvisualization.RenderedObject;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwmx.analysis.informationreporting.Report;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public abstract class AElementPersistance {

    private static Logger log = Logger.getLogger(AElementPersistance.class);

    protected EMFSharedResources util = EMFSharedResources.getInstance();

    /**
     * Persist an element in the specified folder, the file name is created logically by the name of this element.
     * 
     * @param element
     * @param folder
     * @return
     */
    public TypedReturnCode<Object> create(ModelElement element, IFolder folder) {
        TypedReturnCode<Object> trc = new TypedReturnCode<Object>();

        if (getFileExtension() == null) {
            trc.setMessage("File extension is null.");
            log.error("Get file extension error");
        } else {

            IPath itemPath = folder.getFullPath();
            // ProxyRepositoryFactory.getInstance().getRepositoryFactoryFromProvider()\
            // int segmentCount = itemPath.segmentCount();
            Property property = initProperty(element);
            Item item = property.getItem();
            try {
                // if (item instanceof TDQBusinessRuleItem) {
                // ProxyRepositoryFactory.getInstance().create(item, itemPath.removeFirstSegments(segmentCount - 1));
                // } else if (item instanceof ConnectionItem) {
                // ProxyRepositoryFactory.getInstance().create(item, Path.EMPTY);
                // } else {
                // ProxyRepositoryFactory.getInstance().create(item, itemPath.removeFirstSegments(segmentCount - 2));
                // }
                ProxyRepositoryFactory.getInstance().create(item, getRelativePath(item, itemPath));
                trc.setObject(item);
                trc.setOk(Boolean.TRUE);
            } catch (PersistenceException e) {
                trc.setMessage(e.getMessage());
                trc.setOk(Boolean.FALSE);
                log.error(e, e);

                String fname = createLogicalFileName(element, getFileExtension());
                IFile file = folder.getFile(fname);

                if (file.exists()) {
                    // MOD yyi 2009-10-15 Feature: 9524
                    String oriName = element.getName();
                    element.setName(element.getName() + DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
                    fname = createLogicalFileName(element, getFileExtension());
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
        }

        return trc;
    }

    /**
     * DOC xqliu Comment method "getRelativePath".
     * 
     * @param item
     * @param itemPath
     * @return
     */
    private IPath getRelativePath(Item item, IPath itemPath) {
        if (item instanceof TDQAnalysisItem) {
            return itemPath.makeRelativeTo(ResourceManager.getAnalysisFolder().getFullPath());
        } else if (item instanceof TDQReportItem) {
            return itemPath.makeRelativeTo(ResourceManager.getReportsFolder().getFullPath());
        } else if (item instanceof TDQJrxmlItem) {
            return itemPath.makeRelativeTo(ResourceManager.getJRXMLFolder().getFullPath());
        } else if (item instanceof TDQPatternItem) {
            return itemPath.makeRelativeTo(ResourceManager.getPatternFolder().getFullPath());
        } else if (item instanceof TDQPatternItem) {
            return itemPath.makeRelativeTo(ResourceManager.getPatternFolder().getFullPath());
        } else if (item instanceof TDQBusinessRuleItem) {
            return itemPath.makeRelativeTo(ResourceManager.getRulesSQLFolder().getFullPath());
        } else if (item instanceof DatabaseConnectionItem) {
            return itemPath.makeRelativeTo(ResourceManager.getTDQConnectionFolder().getFullPath());
        } else if (item instanceof MDMConnectionItem) {
            return itemPath.makeRelativeTo(ResourceManager.getMDMConnectionFolder().getFullPath());
        }
        return Path.EMPTY;
    }

    /**
     * DOC bZhou Comment method "createLogicalFileNmae".
     * 
     * @param element
     * @param extension
     * @return
     */
    public static String createLogicalFileName(ModelElement element, String extension) {
        return DqRepositoryViewService.createTechnicalName(element.getName()) + "_" + MetadataHelper.getVersion(element)
                + org.talend.dataquality.PluginConstant.DOT_STRING + extension;
    }

    /**
     * DOC bZhou Comment method "create".
     * 
     * Persist the element into the specified file.
     * 
     * @param element
     * @param file
     * @return
     */
    public ReturnCode create(ModelElement element, IFile file) {
        ReturnCode rc = new ReturnCode();
        if (!check(file)) {
            rc.setReturnCode("Failed to save! the extent file name is wrong.", false);
        } else {
            rc = create(element, file.getFullPath(), true);
        }
        return rc;
    }

    /**
     * DOC bZhou Comment method "create".
     * 
     * Persist the element into the specified path.
     * 
     * @param element
     * @param file
     * @param withProperty
     * @return
     */
    public ReturnCode create(ModelElement element, IPath itemPath, boolean withProperty) {
        ReturnCode rc = new ReturnCode();

        if (!util.addEObjectToResourceSet(itemPath.toString(), element)) {
            rc.setReturnCode("Failed to save: " + util.getLastErrorMessage(), false);
        } else {
            if (element instanceof RenderedObject) {
                ((RenderedObject) element).setFileName(itemPath.toString());
            }

            if (withProperty) {
                createProperty(element);
            }
            rc = save(element, withProperty);

        }

        return rc;
    }

    /**
     * DOC bZhou Comment method "createProperty".
     * 
     * Create and save a property from model element resource.
     * 
     * @param modelElement
     * @return
     */
    public Property createProperty(ModelElement modelElement) {
        Resource eResource = modelElement.eResource();
        if (eResource == null) {
            log.error("Can't create property: no resouce assigned to this model element!");
            return null;
        }

        Property property = initProperty(modelElement);

        User user = ReponsitoryContextBridge.getUser();
        if (user != null) {
            property.setAuthor(user);
        }

        URI propURI = eResource.getURI().trimFileExtension().appendFileExtension(FactoriesUtil.PROPERTIES_EXTENSION);

        saveProperty(property, propURI);

        return property;
    }

    /**
     * DOC bZhou Comment method "initProperty".
     * 
     * Initialized a new property.
     * 
     * @param modelElement
     * @return
     */
    public Property initProperty(ModelElement modelElement) {
        Property property = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createProperty();

        setPropertyMetadata(modelElement, property);

        property.setCreationDate(new Date());

        Item item = createItem(modelElement);
        property.setItem(item);
        item.setProperty(property);
        return property;
    }

    private void setPropertyMetadata(ModelElement modelElement, Property property) {

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

        List<Information> informations = ListUtils.castList(Information.class, property.getInformations());
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
     * Save a property.
     * 
     * @param property
     * @return
     */
    public ReturnCode saveProperty(Property property) {
        ReturnCode rc = new ReturnCode();

        Resource propertyResource = property.eResource();
        propertyResource.getContents().add(property.getItem());
        propertyResource.getContents().add(property.getItem().getState());

        rc.setOk(util.saveResource(propertyResource));

        if (!rc.isOk()) {
            rc.setMessage(util.getLastErrorMessage());
        }

        return rc;
    }

    /**
     * DOC bZhou Comment method "saveProperty".
     * 
     * @param property
     * @param uri
     * @return
     */
    public ReturnCode saveProperty(Property property, URI uri) {
        ReturnCode rc = new ReturnCode();

        Resource propResource = util.createResource(uri);
        propResource.getContents().add(property);

        rc = saveProperty(property);

        return rc;
    }

    /**
     * DOC bZhou Comment method "save".
     * 
     * Save a model element and update the related property by default.
     * 
     * @param element
     * @return
     */
    public ReturnCode save(ModelElement element) {
        return save(element, true);
    }

    /**
     * DOC bZhou Comment method "save".
     * 
     * Save a model element and update the related property.
     * 
     * @param element
     * @param withProperty
     * @return
     */
    public ReturnCode save(ModelElement element, boolean withProperty) {
        ReturnCode rc = new ReturnCode();

        addDependencies(element);

        addResourceContent(element);

        rc.setOk(util.saveResource(element.eResource()));

        if (withProperty) {
            updateProperty(element);
        }

        if (rc.isOk()) {
            rc.setMessage("save " + element.getName() + " is OK!");
            notifyResourceChanges();
        } else {
            rc.setMessage(util.getLastErrorMessage());
        }

        return rc;
    }

    public ReturnCode save(ModelElement element, boolean... withProperty) {
        // MOD Use super method to create model element without property.
        if (withProperty != null && withProperty.length > 0 && !withProperty[0]) {
            return saveWithoutProperty(element, withProperty);
        }
        ReturnCode rc = new ReturnCode();
        Item item = PropertyHelper.getProperty(element).getItem();
        try {
            ProxyRepositoryFactory.getInstance().save(item);
        } catch (PersistenceException e) {
            log.error(e, e);
            rc.setOk(Boolean.FALSE);
            rc.setMessage(e.getMessage());
        }
        return rc;
    }

    protected abstract void notifyResourceChanges();

    /**
     * DOC bZhou Comment method "updateProperty".
     * 
     * Use model element's attribute to update the related property.
     * 
     * @param element
     */
    public void updateProperty(ModelElement element) {
        Property property = PropertyHelper.getProperty(element);
        if (property != null) {
            setPropertyMetadata(element, property);

            property.setModificationDate(new Date());

            saveProperty(property);
        }
    }

    /**
     * DOC bZhou Comment method "createItem".
     * 
     * @param element
     * @return
     */
    public Item createItem(ModelElement element) {
        Item item = null;
        // MOD mzhao feature 13114, 2010-05-19 distinguish tdq items.
        if (ModelElementIdentifier.isAnalysis(element)) {
            item = PropertiesFactory.eINSTANCE.createTDQAnalysisItem();
            ((TDQAnalysisItem) item).setAnalysis((Analysis) element);
        } else if (ModelElementIdentifier.isDQRule(element)) {
            item = PropertiesFactory.eINSTANCE.createTDQBusinessRuleItem();
            ((TDQBusinessRuleItem) item).setDqrule((DQRule) element);
        } else if (ModelElementIdentifier.isDataProvider(element)) {
            if (element instanceof DatabaseConnection) {
                item = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createDatabaseConnectionItem();
                ((ConnectionItem) item).setConnection((DatabaseConnection) element);
            } else if (element instanceof MDMConnection) {
                item = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createMDMConnectionItem();
                ((ConnectionItem) item).setConnection((MDMConnection) element);
            }
            ((ConnectionItem) item).setConnection((Connection) element);
        } else if (ModelElementIdentifier.isID(element)) {
            item = PropertiesFactory.eINSTANCE.createTDQIndicatorDefinitionItem();
            ((TDQIndicatorDefinitionItem) item).setIndicatorDefinition((IndicatorDefinition) element);
        } else if (ModelElementIdentifier.isPattern(element)) {
            item = PropertiesFactory.eINSTANCE.createTDQPatternItem();
            ((TDQPatternItem) item).setPattern((Pattern) element);
        } else if (ModelElementIdentifier.isReport(element)) {
            item = PropertiesFactory.eINSTANCE.createTDQReportItem();
            ((TDQReportItem) item).setReport((Report) element);
        } else {
            item = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createTDQItem();
        }

        ItemState itemState = org.talend.core.model.properties.PropertiesFactory.eINSTANCE.createItemState();
        itemState.setDeleted(false);
        item.setState(itemState);

        Resource eResource = element.eResource();
        if (eResource != null) {
            URI uri = eResource.getURI();

            if (item instanceof TDQItem) {
                ((TDQItem) item).setFilename(URI.decode(uri.lastSegment()));
            }

            if (uri.isPlatform()) {
                IPath elementPath = new Path(uri.toPlatformString(true)).removeLastSegments(1);
                IPath typedPath = ResourceManager.getRootProject().getFullPath().append(PropertyHelper.getItemTypedPath(item));

                IPath statePath = elementPath.makeRelativeTo(typedPath);
                itemState.setPath(statePath.toString());
            }
        }

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

    public ReturnCode saveWithoutProperty(ModelElement element, boolean... withProperty) {
        ReturnCode rc = new ReturnCode();

        addDependencies(element);

        addResourceContent(element);

        rc.setOk(util.saveResource(element.eResource()));

        updateProperty(element);

        if (rc.isOk()) {
            rc.setMessage("save " + element.getName() + " is OK!");
            notifyResourceChanges();
        } else {
            rc.setMessage(util.getLastErrorMessage());
        }

        return rc;
    }

    public abstract ReturnCode save(Item item);
}
