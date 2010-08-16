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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.commons.exception.PersistenceException;
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.recycle.DQRecycleBinNode;
import org.talend.dataprofiler.core.recycle.LogicalDeleteFileHandle;
import org.talend.dataprofiler.core.recycle.impl.RecycleBin;
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
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.repository.model.ProxyRepositoryFactory;
import org.talend.resource.EResourceConstant;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ResourceViewContentProvider extends WorkbenchContentProvider {

    private static Logger log = Logger.getLogger(ResourceViewContentProvider.class);

    private List<IContainer> needSortContainers;

    private boolean timeoutFlag = true;

    private List<Object> recyBinElements = new ArrayList<Object>();

    private RecycleBin recycleBin = new RecycleBin();

    /**
     * DOC rli ResourceViewContentProvider constructor comment.
     */
    public ResourceViewContentProvider() {
        super();
        needSortContainers = new ArrayList<IContainer>();
        needSortContainers.add(ResourceManager.getAnalysisFolder());
        needSortContainers.add(ResourceManager.getReportsFolder());
        needSortContainers.add(ResourceManager.getConnectionFolder());
        needSortContainers.add(ResourceManager.getMDMConnectionFolder());
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
            folders.add(recycleBin);
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
            } else if (ResourceManager.isConnectionFolder(folder)) {
                // MOD mzhao 2010-08-11 feature 14891: use same repository API with TOS to persistent metadata
                return getMetadataConnectionChildren(folder);
            }

            return getChildrenExceptRecBin(element);// FIXME Why call this method by default, qiongli?
        } else if (element instanceof IEcosCategory) {
            return ((IEcosCategory) element).getComponent().toArray();
        } else if (element instanceof IndicatorCategory) {
            return getIndicatorsChildren((IndicatorCategory) element);
        } else if (element instanceof RecycleBin) {// MOD qiongli feature 9486
            return recyBinElements.toArray();
        } else if (element instanceof DQRecycleBinNode) {
            DQRecycleBinNode rbn = (DQRecycleBinNode) element;
            return getRecBinNodes(rbn).toArray();
        }
        if (needSortContainers.contains(element)) {
            return getChildrenExceptRecBin(element);
        }
        // ~
        return super.getChildren(element);
    }

    private Object[] getMetadataConnectionChildren(IFolder folder) {
        List<IFile> connFiles = new ArrayList<IFile>();
        // List<ConnectionItem> connItem=null;
        // if(ResourceManager.isMdmConnectionFolder(folder)){
        // connItem=ProxyRepositoryFactory.getInstance().getMetadataConnectionsItem();
        // }else{
        // connItem=ProxyRepositoryFactory.getInstance().
        // }

        try {
            for (ConnectionItem connItem : ProxyRepositoryFactory.getInstance().getMetadataConnectionsItem()) {
                if (connItem instanceof ConnectionItem) {
                    connFiles.add(WorkspaceUtils.getModelElementResource(connItem.getConnection()));
                }
            }
        } catch (PersistenceException e) {
            log.error(e, e);
        }
        return connFiles.toArray();
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
    @SuppressWarnings("unchecked")
    protected Object[] sort(Object[] elements) {
        if (elements == null) {
            return elements;
        }
        List<IResource> list = new ArrayList<IResource>();
        for (Object element : elements) {
            list.add((IResource) element);
            // if (element instanceof IFile) {
            // list.add((IFile) element);
            // } else {
            // log.error("The elemnt:" + ((IFolder) element).getFullPath() +
            // " can't display on the workspace!");
            // }
        }

        Collections.sort(list, ComparatorsFactory.buildComparator(ComparatorsFactory.FILEMODEL_COMPARATOR_ID));
        return list.toArray();
    }

    @Override
    public boolean hasChildren(Object element) {

        if (element instanceof IEcosCategory) {
            return true;
        }
        // MOD qiongli feature 9486
        if (element instanceof IFolder) {
            List<Object> obsLs = Arrays.asList(super.getChildren(element));
            if (obsLs.size() == 1) {
                Object obj = (Object) obsLs.get(0);
                if (obj instanceof IFolder && ((IFolder) obj).getName().equals(PluginConstant.SVN_SUFFIX))
                    return false;
            }
        } else if (element instanceof RecycleBin) {
            return getRecycleBinChildren().size() > 0 ? true : false;
        } else if (element instanceof DQRecycleBinNode) {
            DQRecycleBinNode rbn = (DQRecycleBinNode) element;
            Object obj = rbn.getObject();
            if (obj instanceof IFolder) {
                try {
                    if (((IFolder) obj).members().length > 0)
                        return true;
                } catch (CoreException e) {
                    log.error(e);
                }
            }
            return false;
        }
        return super.hasChildren(element);
    }

    /**
     * DOC qiongli Comment method "getIndicatorChildren".
     */
    private Object[] getChildrenExceptRecBin(Object element) {
        Object[] obs = super.getChildren(element);
        List<String[]> delElements = LogicalDeleteFileHandle.getDelLs();
        if (delElements.size() == 0)
            return obs;
        List<Object> abstractLs = Arrays.asList(obs);
        List<Object> ls = new ArrayList<Object>(abstractLs);
        removeDelElement(ls, delElements);
        return ls.toArray();
    }

    private void removeDelElement(List<Object> list, List<String[]> delElements) {
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
                for (String[] es : delElements) {
                    if (es.length > 1 && folder.getFullPath().toOSString().equals(es[1])) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }

    /**
     * @return get the logical deleted elements
     */
    private List<Object> getRecycleBinChildren() {
        List<String[]> delElements = LogicalDeleteFileHandle.getDelLs();
        List<Object> ls = new ArrayList<Object>();
        HashSet<String> set = new HashSet<String>();
        String fType = null;
        String pathStr = null;
        for (String[] es : delElements) {
            if (es.length < 2)
                continue;
            fType = es[0];
            pathStr = es[1];
            IPath iPath = new Path(pathStr);
            if (fType.equals("File")) {
                IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(iPath);
                if (file.exists()) {
                    IFolder parent = (IFolder) file.getParent();
                    if (ResourceService.isReadOnlyFolder(parent)) {
                        DQRecycleBinNode rbn = new DQRecycleBinNode();
                        rbn.setObject(file);
                        set.add(file.getFullPath().toOSString());
                        ls.add(rbn);
                    } else {
                        addParent(ls, parent, file, set);
                    }
                }
            } else if (fType.equals("Folder")) {
                IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(iPath);
                if (folder.exists()) {
                    IFolder parent = (IFolder) folder.getParent();
                    addParent(ls, parent, folder, set);
                }
            }
        }
        recyBinElements.clear();
        recyBinElements.addAll(ls);
        return recyBinElements;
    }

    /**
     * @param fList
     * @param parent
     * @param child
     * @param hSet make parent folder add to 'fList'
     */
    private void addParent(List<Object> fList, IFolder parent, Object child, HashSet<String> hSet) {
        IFolder currentFolder = parent;
        DQRecycleBinNode rbn = null;
        if (!ResourceService.isReadOnlyFolder(currentFolder)) {
            parent = (IFolder) parent.getParent();
            addParent(fList, parent, currentFolder, hSet);
        } else {
            String childPath = ((IResource) child).getFullPath().toOSString();
            if (!hSet.contains(childPath)) {// make sure the same path added once
                rbn = new DQRecycleBinNode();
                rbn.setObject(child);
                hSet.add(((IResource) child).getFullPath().toOSString());
                fList.add(rbn);
            }
            return;
        }
    }

    /**
     * 
     * @param rbn
     * @return
     */
    private List<Object> getRecBinNodes(DQRecycleBinNode rbn) {

        List<Object> ls = new ArrayList<Object>();
        Object obj = rbn.getObject();
        if (obj instanceof IFolder) {
            IFolder folder = (IFolder) obj;
            String fPath = folder.getFullPath().toOSString();
            ls = LogicalDeleteFileHandle.getChildFromTXT(fPath);
            // MOD qiongli 2010-8-5,bug 14697.add all empty subfolders when has not deleted element in TXT
            try {
                if (ls.size() == 0) {
                    DQRecycleBinNode rbnChild = null;
                    for (IResource member : folder.members()) {
                        if (member.getType() == IResource.FOLDER && ((IFolder) member).members().length == 0) {
                            rbnChild = new DQRecycleBinNode();
                            rbnChild.setObject((IFolder) member);
                            ls.add(rbnChild);
                        }
                    }
                }
            } catch (CoreException e) {
                log.error(e);
            }
            if (ls.size() != 0)
                rbn.setDeletedChildren(ls);
        }
        return ls;
    }

    public RecycleBin getRecycleBin() {
        return recycleBin;
    }
}
