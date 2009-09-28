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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.model.nodes.foldernode.IndicatorFolderNode;
import org.talend.dataprofiler.core.ui.action.provider.NewSourcePatternActionProvider;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataprofiler.ecos.jobs.ComponentSearcher;
import org.talend.dataprofiler.ecos.model.IEcosCategory;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.domain.pattern.PatternComponent;
import org.talend.dataquality.domain.pattern.RegularExpression;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.resource.ResourceManager;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ResourceViewContentProvider extends WorkbenchContentProvider {

    private static Logger log = Logger.getLogger(ResourceViewContentProvider.class);

    private List<IContainer> needSortContainers;

    /**
     * DOC rli ResourceViewContentProvider constructor comment.
     */
    public ResourceViewContentProvider() {
        super();
        needSortContainers = new ArrayList<IContainer>();
        needSortContainers.add(ResourceManager.getDataProfilingFolder().getFolder(DQStructureManager.ANALYSIS));
        needSortContainers.add(ResourceManager.getDataProfilingFolder().getFolder(DQStructureManager.REPORTS));
        needSortContainers.add(ResourceManager.getMetadataFolder().getFolder(PluginConstant.DB_CONNECTIONS));
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
                }
            } catch (CoreException e) {
                log.error(e);
            }
            return folders.toArray();
        } else if (element instanceof IFile) {
            IFile file = (IFile) element;
            if (file.getName().endsWith(NewSourcePatternActionProvider.EXTENSION_PATTERN)) {
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
            //
            IFolder folder = (IFolder) element;
            if (folder.getName().equals(DQStructureManager.EXCHANGE)) {
                // Mod gyichao 2009-07-07, feature 8109
                return ComponentSearcher.getAvailableCategory(CorePlugin.getDefault().getProductVersion().toString()).toArray();

            } else if (folder.getName().equals(DQStructureManager.INDICATORS)) {
                // MOD xqliu 2009-07-27 bug 7810
                return getIndicatorsChildren(folder);
            }
        } else if (element instanceof IEcosCategory) {
            return ((IEcosCategory) element).getComponent().toArray();
        } else if (element instanceof IndicatorCategory) {
            return getIndicatorsChildren((IndicatorCategory) element);
        }
        if (needSortContainers.contains(element)) {
            return sort(super.getChildren(element));
        }
        return super.getChildren(element);
    }

    /**
     * DOC xqliu Comment method "getIndicatorsChildren".
     * 
     * @param category
     * @return
     */
    private Object[] getIndicatorsChildren(IndicatorCategory category) {
        List<IndicatorDefinition> indicatorDefinitionList = IndicatorFolderNode.getIndicatorDefinitionList(category);
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
        list.add(new IndicatorFolderNode("System"));
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
        return super.hasChildren(element);
    }

}
