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
package org.talend.dataprofiler.core.ui.views.provider;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.xml.TdXMLDocument;
import org.talend.cwm.xml.TdXMLElement;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.dataprofiler.ecos.model.IEcosComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.nodes.foldernode.AbstractFolderNode;
import org.talend.dq.nodes.foldernode.IFolderNode;

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
        } else if (element instanceof TdDataProvider) {
            return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
        } else if (element instanceof TdColumn) {
            if (ColumnHelper.isPrimaryKey((TdColumn) element)) {
                // get the icon for primary key
                return ImageLib.getImage(ImageLib.PK_COLUMN);
            }
        } else if (element instanceof IEcosComponent) {
            return ImageLib.getImage(ImageLib.EXCHANGE);
        } else if (element instanceof IEcosCategory) {
            return ImageLib.getImage(ImageLib.EXCHANGE);
        } else if (element instanceof IndicatorDefinition) {
            return ImageLib.getImage(ImageLib.IND_DEFINITION);
        } else if (element instanceof TdView) {
            return ImageLib.getImage(ImageLib.VIEW);
        } else if (element instanceof TdXMLDocument) {
            return ImageLib.getImage(ImageLib.XML_DOC);
        } else if (element instanceof TdXMLElement) {
            return ImageLib.getImage(ImageLib.XML_ELEMENT_DOC);
        }
        return super.getImage(element);
    }

    public String getText(Object element) {

        if (element instanceof AbstractFolderNode) {
            if (((IFolderNode) element).getChildren() != null) {
                return ((IFolderNode) element).getName() + "(" + ((IFolderNode) element).getChildren().length + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            }

            return ((IFolderNode) element).getName();
        } else if (element instanceof IEcosComponent) {
            return ((IEcosComponent) element).getName();
        } else if (element instanceof IEcosCategory) {
            return ((IEcosCategory) element).getName();
        } else if (element instanceof IndicatorDefinition) {
            return ((IndicatorDefinition) element).getName();
        } else if (element instanceof IndicatorCategory) {
            return ((IndicatorCategory) element).getName();
        }

        // PTODO qzhang fixed bug 4176: Display expressions as children of the
        // patterns
        if (element instanceof RegularExpression) {
            RegularExpression regExp = (RegularExpression) element;
            return regExp.getExpression().getLanguage();
        } else if (element instanceof TdDataProvider) {
            return ((TdDataProvider) element).getName();
        }

        // MOD mzhao feature 10238
        if (element instanceof TdXMLDocument) {
            return ((TdXMLDocument) element).getName();
        } else if (element instanceof TdXMLElement) {
            String elemLabe = ((TdXMLElement) element).getName();
            String elementType = ((TdXMLElement) element).getJavaType();
            if (elementType != null && !StringUtils.isEmpty(elementType)) {
                elemLabe += " (" + elementType + ")";
            }
            return elemLabe;
        }
        return super.getText(element);
    }
}
