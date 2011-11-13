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
package org.talend.dataprofiler.core.migration.impl;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.commons.emf.EMFUtil;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.core.model.properties.PropertiesPackage;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.migration.AbstractWorksapceUpdateTask;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.properties.impl.TDQIndicatorDefinitionItemImpl;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.utils.io.FilesUtils;
import orgomg.cwm.objectmodel.core.TaggedValue;

import common.Logger;

/**
 * 
 * DOC zshen class global comment. Detailled comment
 */
public class UpdateJUDITask extends AbstractWorksapceUpdateTask {

    Logger log = Logger.getLogger(UpdateJUDITask.class);

    private ResourceSet resourceSet;

    public UpdateJUDITask() {
        resourceSet = new ResourceSetImpl();
    }

    public Date getOrder() {
        return createDate(2011, 11, 11);
    }

    public MigrationTaskType getMigrationTaskType() {
        return MigrationTaskType.FILE;
    }

    @Override
    protected boolean doExecute() throws Exception {

        ResourceManager.getUDIFolder();
        IPath udiJarPath = this.getWorkspacePath().append(EResourceConstant.USER_DEFINED_INDICATORS_LIB.getPath());
        File udiJarfile = new File(udiJarPath.toOSString());
        if (!udiJarfile.exists()) {
            udiJarfile.mkdir();
        }
        if (udiJarfile.exists()) {
            // find all udi and check jarfile
            File parentFile = udiJarfile.getParentFile();
            if (parentFile.isDirectory()) {
                handleUDIFile(parentFile.listFiles());
            }
        }

        return false;
    }

    /**
     * DOC zshen Comment method "handleUDIFile".
     * 
     * @param listFiles
     */
    private void handleUDIFile(File[] listFiles) {
        if (listFiles == null)
            return;
        for (File udiFile : listFiles) {
            if (udiFile == null) {
                continue;
            }
            if (udiFile.isDirectory()) {
                handleUDIFile(udiFile.listFiles());
            } else {
                // there only use old path to find jar. After that we can try to open a dialog to let user provider it.
                if (!udiFile.getName().endsWith(FactoriesUtil.PROPERTIES_EXTENSION)) {
                    continue;
                }
                Resource propResource = getResource(udiFile.getAbsolutePath());

                Property newProperty = (Property) EcoreUtil.getObjectByType(propResource.getContents(),
                        PropertiesPackage.eINSTANCE.getProperty());
                try {
                    // ProxyRepositoryFactory.getInstance().getAll(ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS);
                    newProperty = ProxyRepositoryFactory.getInstance().getLastVersion(newProperty.getId()).getProperty();
                } catch (PersistenceException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                IndicatorDefinition uDIindicatorDefinition = ((TDQIndicatorDefinitionItemImpl) newProperty.getItem())
                        .getIndicatorDefinition();

                EList<TaggedValue> taggedValues = uDIindicatorDefinition.getTaggedValue();
                String userJavaClassName = null;
                String jarPath = null;
                for (TaggedValue tv : taggedValues) {
                    if (tv.getTag().equals(PluginConstant.CLASS_NAME_TEXT)) {
                        userJavaClassName = tv.getValue();
                        continue;
                    }
                    if (tv.getTag().equals(PluginConstant.JAR_FILE_PATH)) {
                        jarPath = tv.getValue();
                        tv.setValue(new Path(jarPath).lastSegment());
                        ((TDQIndicatorDefinitionItemImpl) newProperty.getItem()).setIndicatorDefinition(uDIindicatorDefinition);
                        Resource itemResource = getResource(new Path(udiFile.getAbsolutePath()).removeFileExtension()
                                .addFileExtension(FactoriesUtil.DEFINITION).toOSString());
                        EMFUtil.saveResource(itemResource);
                        newProperty.setItem(newProperty.getItem());
                        // String propertyPath = String.valueOf(Path.SEPARATOR)
                        // + (new
                        // Path(destPropFile.getPath()).makeRelativeTo(this.getWorkspacePath().removeLastSegments(1)).toString());
                        // MetadataHelper.setPropertyPath(propertyPath, connectionItem.getConnection());
                        newProperty.getItem().setProperty(newProperty);

                        propResource.getContents().clear();

                        propResource.getContents().add(newProperty);
                        propResource.getContents().add(newProperty.getItem());
                        propResource.getContents().add(newProperty.getItem().getState());
                        EMFUtil.saveResource(propResource);

                        // FolderItem folderItem = ProxyRepositoryFactory.getInstance().getFolderItem(
                        // ProjectManager.getInstance().getCurrentProject(),
                        // ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS, new Path(""));
                        //
                        // folderItem.getChildren().clear();
                        //

                        // //
                        // ElementWriterFactory.getInstance().createIndicatorDefinitionWriter().save(newProperty.getItem());
                        //
                        // try {
                        // ProxyRepositoryFactory.getInstance().getLastVersion(folderItem.getProperty().getId(), null,
                        // ERepositoryObjectType.TDQ_USERDEFINE_INDICATORS);
                        // } catch (PersistenceException e) {
                        // log.error(e, e);
                        // }
                       
                        // for (Object obj : folderItem.getChildren()) {
                        // ((TDQIndicatorDefinitionItem) obj).getIndicatorDefinition();
                        // }

                        // IndicatorResourceFileHelper.getInstance().save(uDIindicatorDefinition);
                    }
                }
                if (jarPath != null) {
                    File jarPathFile = new File(jarPath);
                    if (jarPathFile.exists()) {
                        try {
                            FilesUtils.copyFile(jarPathFile,
                                    ResourceManager.getUDIJarFolder().getLocation().append(jarPathFile.getName()).toFile());
                        } catch (IOException e) {
                            log.error(e, e);
                        }
                    }
                }

            }

        }

    }

    private Resource getResource(String filePath) {
        URI uri = URI.createFileURI(filePath);
        Resource resource = resourceSet.getResource(uri, true);
        return resource;
    }

}
