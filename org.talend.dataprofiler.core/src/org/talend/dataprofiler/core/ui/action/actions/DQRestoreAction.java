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

import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dataprofiler.core.recycle.SelectedResources;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.writer.EMFSharedResources;
import org.talend.top.repository.ProxyRepositoryManager;

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
        // MOD qiongli 2010-10-9,bug 15674
        SelectedResources selectedResources = new SelectedResources();
        Property[] selectedProps = selectedResources.getSelectedArrayForDelForever();
        try {

            for (Property property : selectedProps) {
				if (property.eIsProxy()) {
					property = (Property) EObjectHelper.resolveObject(property);
				}
                Item item = property.getItem();
                if (item instanceof ConnectionItem) {
                    // MOD qiongli 2010-10-14,bug 15587.this property is no longer from file.
                    property.getItem().getState().setDeleted(false);
                    ProxyRepositoryFactory.getInstance().save(property);
                } else {
                    property.getItem().getState().setDeleted(false);
                    Resource propertyResource = property.eResource();
                    if (!EMFSharedResources.getInstance().saveResource(propertyResource))
                        return;
                }
                LogicalDeleteFileHandle.refreshDelPropertys(0, property);
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
