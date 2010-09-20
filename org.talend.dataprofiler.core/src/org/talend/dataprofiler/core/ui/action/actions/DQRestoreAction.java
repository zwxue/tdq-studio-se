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
package org.talend.dataprofiler.core.ui.action.actions;

import java.io.File;

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dataprofiler.core.recycle.SelectedResources;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.dq.writer.impl.ElementWriterFactory;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.resource.ResourceManager;
import org.talend.top.repository.ProxyRepositoryManager;
import org.talend.utils.sugars.ReturnCode;

/**
 * @author qiongli Restore recycle bin element
 */
public class DQRestoreAction extends Action {

    private static Logger log = Logger.getLogger(DQRestoreAction.class);

    /**
	 * 
	 */
    public DQRestoreAction() {
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.ADD_ACTION));
        this.setText(DefaultMessagesImpl.getString("DQRestoreAction.restore"));
    }

    @Override
    public void run() {

        SelectedResources selectedResources = new SelectedResources();
        IFile[] selectedFiles = selectedResources.getSelectedResourcesArrayForDelForever();
        try {
            String fileString = ResourceManager.getLibrariesFolder().getLocation().toOSString() + "/.logicalDelete.txt";
            File f = new Path(fileString).toFile();
            for (IFile file : selectedFiles) {
                Property property = PropertyHelper.getProperty(file);
                if (file.getFileExtension().equals(FactoriesUtil.PROPERTIES_EXTENSION)) {
                    IRepositoryViewObject repViewObj = ProxyRepositoryViewObject.getRepositoryViewObjectByProperty(property);
                    Property propertyTMP = repViewObj.getProperty();
                    propertyTMP.getItem().getState().setDeleted(false);
                    ProxyRepositoryFactory.getInstance().save(propertyTMP);
                    EMFSharedResources.getInstance().unloadResource(property.eResource().getURI().toString());

                    ReturnCode rc = ElementWriterFactory.getInstance().createDataProviderWriter().saveProperty(property);
                    if (!rc.isOk())
                        return;
                } else {
                    property.getItem().getState().setDeleted(false);
                    Resource propertyResource = property.eResource();
                    if (!EMFSharedResources.getInstance().saveResource(propertyResource))
                        return;
                }

                if (f.exists()) {
                    LogicalDeleteFileHandle.replaceInFile(LogicalDeleteFileHandle.fileType + file.getFullPath().toOSString(),
                            PluginConstant.EMPTY_STRING);
                    LogicalDeleteFileHandle.removeAllParent(file);
                }

                // Add yyi 2010-09-15 14549: hide connections in SQL Explorer when a connection is moved to the trash
                // bin
                if (property.getItem() instanceof ConnectionItem) {
                    SQLExplorerPlugin.getDefault().getAliasManager().modelChanged();
                }
            }
            // MOD qiongli bug 14697,delete some codes which replace folder path in txt
        } catch (Exception exc) {
            log.error(exc, exc);
        }
        ProxyRepositoryManager.getInstance().save();

        CorePlugin.getDefault().refreshDQView();

        CorePlugin.getDefault().refreshWorkSpace();

    }

}
