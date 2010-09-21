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
package org.talend.dataprofiler.core.ui.views.provider;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataprofiler.core.helper.FolderNodeHelper;
import org.talend.dataprofiler.core.model.nodes.foldernode.AnaElementFolderNode;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.analysis.category.CategoryHandler;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.nodes.foldernode.IFolderNode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class DQRepositoryViewContentProvider extends AdapterFactoryContentProvider {

    private static Logger log = Logger.getLogger(DQRepositoryViewContentProvider.class);

    /**
     * @param adapterFactory
     */
    public DQRepositoryViewContentProvider() {
        super(MNComposedAdapterFactory.getAdapterFactory());
    }

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IFile) {
            IFile file = (IFile) parentElement;
            if (FactoriesUtil.isAnalysisFile(file.getFileExtension())) {
                Analysis analysis = (Analysis) AnaResourceFileHelper.getInstance().findAnalysis(file);
                EList<ModelElement> analysedElements = analysis.getContext().getAnalysedElements();
                AnaElementFolderNode folderNode = new AnaElementFolderNode(analysedElements);
                return new Object[] { folderNode };
            }
            // parentElement = PrvResourceFileHelper.getInstance().getFileResource(file);
        } else if (parentElement instanceof IRepositoryViewObject) {
            // MOD mzhao feature 2010-08-12 14891: use same repository API with TOS to persistent metadata
            IRepositoryViewObject conn = (IRepositoryViewObject) parentElement;
            // Currently we only care about connection Item.
            Item connItem = conn.getProperty().getItem();
            if (connItem instanceof ConnectionItem) {
                return ((ConnectionItem) connItem).getConnection().getDataPackage().toArray();
            }
        } else if (parentElement instanceof IFolderNode) {
            IFolderNode folerNode = (IFolderNode) parentElement;
            folerNode.loadChildren();
            if (folerNode.getChildren() == null) {
                return new Object[0];
            }
            if (folerNode.getChildrenType() == IFolderNode.MODELELEMENT_TYPE) {
                return ComparatorsFactory.sort(folerNode.getChildren(), ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
            } else {
                return ComparatorsFactory.sort(folerNode.getChildren(), ComparatorsFactory.FILE_RESOURCE_COMPARATOR_ID);
            }
        } else if (parentElement instanceof EObject) {
            EObject eParent = (EObject) parentElement;

            if (SwitchHelpers.CATALOG_SWITCH.doSwitch(eParent) != null) {
                if (CatalogHelper.getSchemas(SwitchHelpers.CATALOG_SWITCH.doSwitch(eParent)).size() > 0) {
                    return ComparatorsFactory.sort(super.getChildren(eParent), ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
                } else {
                    return FolderNodeHelper.getFolderNodes(eParent);
                }

            } else if (SwitchHelpers.XMLSCHEMA_SWITCH.doSwitch(eParent) != null) {
                // MOD mzhao feature 10238 xml documents.
                return DqRepositoryViewService.getXMLElements((TdXmlSchema) eParent).toArray();
            } else if (SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(eParent) != null) {
                // MOD mzhao xml elements
                return DqRepositoryViewService.getXMLElements((TdXmlElementType) eParent).toArray();

            } else {
                return FolderNodeHelper.getFolderNodes(eParent);
            }
        } else if (parentElement instanceof IndicatorCategory) {
            IndicatorCategory category = (IndicatorCategory) parentElement;
            Map<IndicatorCategory, List<IndicatorDefinition>> categoriesIDMaps = CategoryHandler.getCategoriesIDMaps();
            List<IndicatorDefinition> list = categoriesIDMaps.get(category);
            if (list != null) {
                return list.toArray();
            }

        }
        return ComparatorsFactory.sort(super.getChildren(parentElement), ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
    }

    @Override
    public Object[] getElements(Object object) {
        return this.getChildren(object);
    }

    public Object getParent(Object element) {
        if (element instanceof IFile) {
            return ((IResource) element).getParent();
        }
        // MOD make the subNode of connection can find IRepositoryViewObject on the DQView instead of the file of
        // connection.
        Object returnObj = super.getParent(element);
        // if (returnObj instanceof CwmResource) {
        // for (EObject conn : ((CwmResource) returnObj).getContents()) {
        // if (conn instanceof Connection) {
        // IRepositoryViewObject repObjec = ProxyRepositoryViewObject.getRepositoryViewObject((Connection) conn);
        // if (repObjec == null) {
        // break;
        // }
        // return repObjec;
        // }
        // }
        // } else if (returnObj == null && element instanceof PackageImpl && ((PackageImpl)
        // element).getDataManager().size() > 0) {
        // for (EObject conn : ((PackageImpl) element).getDataManager()) {
        // if (conn instanceof Connection) {
        // IRepositoryViewObject repObjec = ProxyRepositoryViewObject.getRepositoryViewObject((Connection) conn);
        //                    
        // if (repObjec == null) {
        // break;
        // }
        // return repObjec;
        // }
        // }
        // }
        return returnObj;
    }

    public boolean hasChildren(Object element) {
        if (element instanceof EObject) {
            EObject eobject = (EObject) element;
            if (SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(eobject) != null) {
                return DqRepositoryViewService.hasChildren((TdXmlElementType) element);
            }

            return checkLeaf(eobject);
        }

        return true;
    }

    /**
     * DOC bZhou Comment method "checkLeaf".
     * 
     * @param eobject
     * @return
     */
    private boolean checkLeaf(EObject eobject) {
        return !(eobject instanceof TdColumn || eobject instanceof IndicatorDefinition || eobject instanceof RegularExpression);
    }
}
