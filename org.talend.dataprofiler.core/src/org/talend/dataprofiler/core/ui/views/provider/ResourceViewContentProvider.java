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

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.utils.io.FilesUtils;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.recycle.DQRecycleBinNode;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dataprofiler.core.recycle.impl.RecycleBin;
import org.talend.dataprofiler.core.recycle.impl.RecycleBinManager;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataprofiler.ecos.jobs.ComponentSearcher;
import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.analysis.category.CategoryHandler;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ResourceViewContentProvider extends WorkbenchContentProvider {

    private static Logger log = Logger.getLogger(ResourceViewContentProvider.class);


    private boolean timeoutFlag = true;

    /**
     * DOC rli ResourceViewContentProvider constructor comment.
     */
    public ResourceViewContentProvider() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.ui.internal.navigator.resources.workbench.
     * ResourceExtensionContentProvider#getChildren(java.lang.Object)
     */
    @Override
    public Object[] getChildren(Object element) {
        if (element instanceof IWorkspaceRoot) {
            Object currentOpenProject = null;
            for (Object child : super.getChildren(element)) {
                if (child instanceof IProject) {
                    if (((IProject) child).getName().equals(ResourceManager.getRootProjectName())) {
                        currentOpenProject = child;
                        break;
                    }
                }
            }
            List<Object> folders = new ArrayList<Object>();
            try {
                Object[] rootFolders = new Object[0];
                rootFolders = ((IProject) currentOpenProject).members(false);
                for (Object folder : rootFolders) {
                    if (folder instanceof IFolder && ((IFolder) folder).getName().startsWith(DQStructureManager.PREFIX_TDQ)) {
                        // ~ MOD mzhao 2009-04-13, Move reporting db folder into
                        // project folder.
                        if (((IFolder) folder).getName().trim().equals("TDQ_reporting_db")) { //$NON-NLS-1$
                            continue;
                        }
                        // ~
                        folders.add(folder);
                    }
                    // MOD mzhao for metadata folder
                    if (folder instanceof IFolder && ((IFolder) folder).getName().equals(EResourceConstant.METADATA.getPath())) {
                        folders.add(folder);
                    }
                }
            } catch (CoreException e) {
                log.error(e);
            }
            // MOD qiongli feature 9486
            folders.add(RecycleBinManager.getInstance().getRecycleBin());
            return folders.toArray();
        } else if (element instanceof IFile) {
            IFile file = (IFile) element;
            if (FactoriesUtil.isPatternFile(file.getFileExtension())) {
                // MOD mzhao 2009-04-20,Bug 6349.
                Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(file);
                RegularExpression[] regularExp = new RegularExpression[pattern.getComponents().size()];
                int peIdx = 0;
                for (PatternComponent patCom : pattern.getComponents()) {
                    regularExp[peIdx] = (RegularExpression) patCom;
                    peIdx++;
                }
                return regularExp;
            }
        } else if (element instanceof IFolder) {
            IFolder folder = (IFolder) element;
            if (ResourceManager.isExchangeFolder(folder)) {

                try {
                    if (timeoutFlag) {
                        String version = CorePlugin.getDefault().getProductVersion().toString();
                        return ComponentSearcher.getAvailableCategory(version).toArray();
                    } else {
                        return new String[] { "Connection failed: time out" };
                    }
                } catch (SocketTimeoutException e) {
                    timeoutFlag = false;
                    return new String[] { "Connection failed:" + e.getMessage() };

                } catch (Exception e) {
                    timeoutFlag = false;
                    return new String[] { e.getMessage() };
                }

            } else if (ResourceManager.isIndicatorFolder(folder)) {
                // MOD xqliu 2009-07-27 bug 7810
                return getIndicatorsChildren(folder);
            } else if (ResourceManager.getConnectionFolder().getFullPath().isPrefixOf(folder.getFullPath())) {
                // MOD mzhao 2010-08-11 feature 14891: use same repository API with TOS to persistent metadata
                // MOD qiongli 2010-9-3 bug 14891
                // MOD zshen 2010-9-07 bug 14891
                // List<IRepositoryViewObject> conList = DQDBConnectionReposViewObjDelegator.getInstance()
                // .fetchRepositoryViewObjectsWithFolder(Boolean.FALSE);

                List<Object> returnList = new ArrayList<Object>();
                if (ResourcesPlugin.getWorkspace().isTreeLocked()) {
                    return returnList.toArray();
                }
                IPath path = folder.getFullPath().makeRelativeTo(ResourceManager.getConnectionFolder().getFullPath());
                List<IRepositoryViewObject> conList = ProxyRepositoryViewObject.fetchRepositoryViewObjectsByFolder(true,
                        ERepositoryObjectType.METADATA_CONNECTIONS, path, true);
                returnList.addAll(getConnectionChildren(conList));
                for (Object folderResource : getChildrenExceptRecBin(folder)) {
                    if (folderResource instanceof IResource && ((IResource) folderResource).getType() == IResource.FOLDER) {
                        returnList.add(folderResource);
                    }
                }
                // List<IRepositoryViewObject> conList = DQDBConnectionReposViewObjDelegator.getInstance()
                // .fetchRepositoryViewObjects(Boolean.TRUE);
                // ProxyRepositoryFactory.getInstance().getFolderItem(
                // ProxyRepositoryFactory.getInstance().getRepositoryContext().getProject(),
                // ERepositoryObjectType.METADATA_CONNECTIONS, Path.EMPTY);
                // ProxyRepositoryFactory.getInstance().getFolderItem(
                // ProxyRepositoryFactory.getInstance().getRepositoryContext().getProject(),
                // ERepositoryObjectType.METADATA_CONNECTIONS, new Path("aa"));
                // try {
                // ProxyRepositoryFactory.getInstance().getAll(ERepositoryObjectType.METADATA_CONNECTIONS);
                // } catch (PersistenceException e) {
                // // TODO Auto-generated catch block
                // e.printStackTrace();
                // }
                return sort(returnList.toArray());

                // List<IRepositoryViewObject> conList =
                // ProxyRepositoryViewObject.fetchAllDBRepositoryViewObjects(Boolean.TRUE);

                // return getConnectionChildren(conList).toArray();

            } else if (ResourceManager.getMDMConnectionFolder().getFullPath().isPrefixOf(folder.getFullPath())) {
                // MOD zshen 2010-08-30 feature 14891: use same repository API with TOS to persistent metadata
                // MOD qiongli 2010-9-3 bug 14891
                // List<IRepositoryViewObject> conList = DQMDMConnectionReposViewObjDelegator.getInstance()
                // .fetchRepositoryViewObjectsWithFolder(Boolean.FALSE);
                List<Object> returnList = new ArrayList<Object>();
                if (ResourcesPlugin.getWorkspace().isTreeLocked()) {
                    return returnList.toArray();
                }
                IPath path = folder.getFullPath().makeRelativeTo(ResourceManager.getMDMConnectionFolder().getFullPath());
                List<IRepositoryViewObject> conList = ProxyRepositoryViewObject.fetchRepositoryViewObjectsByFolder(true,
                        ERepositoryObjectType.METADATA_MDMCONNECTION, path, true);
                returnList.addAll(getConnectionChildren(conList));
                for (Object folderResource : getChildrenExceptRecBin(folder)) {
                    if (folderResource instanceof IResource && ((IResource) folderResource).getType() == IResource.FOLDER) {
                        returnList.add(folderResource);
                    }
                }
                return sort(returnList.toArray());
                // List<IRepositoryViewObject> conList = DQMDMConnectionReposViewObjDelegator.getInstance()
                // .fetchRepositoryViewObjects(Boolean.TRUE);

                // List<IRepositoryViewObject> conList =
                // ProxyRepositoryViewObject.fetchAllMDMRepositoryViewObjects(Boolean.FALSE);

                // return getConnectionChildren(conList).toArray();

            }

            return getChildrenExceptRecBin(element);// FIXME Why call this method by default, qiongli?
        } else if (element instanceof IEcosCategory) {
            return ((IEcosCategory) element).getComponent().toArray();
        } else if (element instanceof IndicatorCategory) {
            return getIndicatorsChildren((IndicatorCategory) element);
        } else if (element instanceof RecycleBin) {// MOD qiongli feature 9486
            RecycleBin bin = (RecycleBin) element;
            return sort(bin.getChildren());
        } else if (element instanceof DQRecycleBinNode) {
            DQRecycleBinNode rbn = (DQRecycleBinNode) element;
            return sort(getRecBinNodes(rbn).toArray());
        }
        // ~
        return super.getChildren(element);
    }

    /**
     * DOC xqliu Comment method "getIndicatorsChildren".
     * 
     * @param category
     * @return
     */
    private Object[] getIndicatorsChildren(IndicatorCategory category) {
        List<IndicatorDefinition> indicatorDefinitionList = CategoryHandler.getIndicatorDefinitionList(category);
        if (indicatorDefinitionList == null) {
            indicatorDefinitionList = new ArrayList<IndicatorDefinition>();
        }
        return indicatorDefinitionList.toArray();
    }

    /**
     * DOC xqliu Comment method "getIndicatorChildren".
     * 
     * @return
     */
    private Object[] getIndicatorsChildren(IFolder folder) {
        List<Object> list = new ArrayList<Object>();
        // MOD mzhao feature 13676, split system indicators. 2010-07-08
        // list.add(new IndicatorFolderNode("System"));
        try {
            list.addAll(Arrays.asList(folder.members()));
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return list.toArray();
    }

    /**
     * Sort the parameter objects, and return the sorted array.
     * 
     * @param elements
     * @return
     */
    protected Object[] sort(Object[] elements) {
        if (elements == null || elements.length == 0) {
            return elements;
        }
        // MOD qiongli 2010-12-1 bug 15700.sort all Object
        // MOD qiongli 2010-12-21 bug 17738
        if (elements[0] instanceof ModelElement) {
            return ComparatorsFactory.sort(elements, ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
        } else if (elements[0] instanceof IRepositoryViewObject) {
            return ComparatorsFactory.sort(elements, ComparatorsFactory.IREPOSITORYVIEWOBJECT_COMPARATOR_ID);
        } else if (elements[0] instanceof DQRecycleBinNode) {
            return ComparatorsFactory.sort(elements, ComparatorsFactory.DQRECYCLYBIN_COMPARATOR_ID);
        }
        return ComparatorsFactory.sort(elements, ComparatorsFactory.FILEMODEL_COMPARATOR_ID);
    }

    @Override
    public boolean hasChildren(Object element) {

        if (element instanceof IEcosCategory) {
            return true;
        }
        // MOD qiongli feature 9486
        if (element instanceof IFolder) {
            // MOD yyi 2010-09-30 15271: svn project can't load exchange nodes
            if (ResourceManager.isExchangeFolder((IFolder) element)) {
                return true;
            }
            // ~15271
            List<Object> obsLs = Arrays.asList(super.getChildren(element));
            if (obsLs.size() == 1) {
                Object obj = (Object) obsLs.get(0);
                if (obj instanceof IFolder && ((IFolder) obj).getName().equals(PluginConstant.SVN_SUFFIX))
                    return false;
            }
        } else if (element instanceof DQRecycleBinNode) {
            DQRecycleBinNode rbn = (DQRecycleBinNode) element;
            Object obj = rbn.getObject();
            if (obj instanceof IFolder) {
                try {
                    return ((IFolder) obj).members().length > 0;
                } catch (CoreException e) {
                    log.error(e);
                }
            }
            return false;
        }
        return super.hasChildren(element);
    }

    /**
     * DOC qiongli Comment method "getIndicatorChildren".Get all Children which have not been logical deleted.
     */
    private Object[] getChildrenExceptRecBin(Object element) {
        IFolder folder = (IFolder) element;
        Object[] obs = super.getChildren(folder);
        HashSet<Property> delElements = LogicalDeleteFileHandle.getDelPropertyLs();
        if (delElements.size() == 0)
            return sort(obs);
        List<Object> abstractLs = Arrays.asList(obs);
        List<Object> ls = new ArrayList<Object>(abstractLs);
        try {
            removeDelElement(ls);
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        return sort(ls.toArray());
    }

    private void removeDelElement(List<Object> list) throws Exception {
        Iterator<Object> iterator = list.iterator();
        Object object;
        IFile propFile;
        Property property;
        while (iterator.hasNext()) {
            object = iterator.next();
            if (object instanceof IFile) {
                IFile ifile = (IFile) object;
                propFile = ResourcesPlugin.getWorkspace().getRoot().getFile(
                        ifile.getFullPath().removeFileExtension().addFileExtension(FactoriesUtil.PROPERTIES_EXTENSION));
                if (propFile.exists()) {
                    property = PropertyHelper.getProperty(propFile);
                    if (property.getItem().getState().isDeleted()) {
                        iterator.remove();
                    }
                }
            } else if (object instanceof IFolder) {
                IFolder folder = (IFolder) object;
                if (ResourceService.isReadOnlyFolder(folder) || FilesUtils.isSVNFolder(folder))
                    continue;
                if (LogicalDeleteFileHandle.isAllChildrenDeleted(folder)) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * 
     * @param rbn
     * @return
     */
    private List<Object> getRecBinNodes(DQRecycleBinNode rbn) {

        List<Object> ls = rbn.getDeletedChildren();
        if (ls != null) {
            return ls;
        }
        ls = new ArrayList<Object>();
        Object obj = rbn.getObject();
        if (obj instanceof IFolder) {
            IFolder folder = (IFolder) obj;
            String fPath = folder.getFullPath().toOSString();
            ls = LogicalDeleteFileHandle.getLogicalDelNodes(fPath);
            if (ls.size() != 0)
                rbn.setDeletedChildren(ls);
        }
        return ls;
    }

    /**
     * 
     * DOC QiongLi Comment method "getConnectionChildren".
     * 
     * @param ls
     * @return
     */
    private List<IRepositoryViewObject> getConnectionChildren(List<IRepositoryViewObject> ls) {
        Iterator<IRepositoryViewObject> iterator = ls.iterator();
        while (iterator.hasNext()) {
            IRepositoryViewObject conn = iterator.next();
            Item connItem = conn.getProperty().getItem();
            if (connItem.getState().isDeleted()) {
                iterator.remove();
            }
        }
        return ls;
    }
}
