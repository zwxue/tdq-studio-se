// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.views.provider;

import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.model.nodes.foldernode.ColumnFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.IFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.TableFolderNode;
import org.talend.dataprofiler.core.model.nodes.foldernode.ViewFolderNode;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.RegularExpression;

import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * @author rli
 * 
 */
public class DQRepositoryViewLabelProvider extends AdapterFactoryLabelProvider {

    public DQRepositoryViewLabelProvider() {
        super(MNComposedAdapterFactory.getAdapterFactory());
    }

    public Image getImage(Object element) {
        if (element instanceof IFolderNode) {
            return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
        }
        return super.getImage(element);
    }

    public String getText(Object element) {

        if (element instanceof TableFolderNode) {
            TableFolderNode node = (TableFolderNode) element;

            Package catalogOrSchema = PackageHelper.getCatalogOrSchema(node.getParent());
            return ((IFolderNode) element).getName() + "(" + PackageHelper.getTables(catalogOrSchema).size() + ")";
        }

        if (element instanceof ViewFolderNode) {
            ViewFolderNode node = (ViewFolderNode) element;

            Package catalogOrSchema = PackageHelper.getCatalogOrSchema(node.getParent());
            return ((IFolderNode) element).getName() + "(" + PackageHelper.getViews(catalogOrSchema).size() + ")";
        }

        if (element instanceof ColumnFolderNode) {
            ColumnFolderNode node = (ColumnFolderNode) element;

            ColumnSet table = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(node.getParent());
            return ((IFolderNode) element).getName() + "(" + ColumnSetHelper.getColumns(table).size() + ")";
        }

        // PTODO qzhang fixed bug 4176: Display expressions as children of the patterns
        if (element instanceof Pattern) {
            Pattern pattern = (Pattern) element;
            RegularExpression patternComponent = (RegularExpression) pattern.getComponents().get(0);
            return patternComponent.getExpression().getBody();
        }
        return super.getText(element);
    }
}
