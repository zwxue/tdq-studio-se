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

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.manager.DQStructureMessage;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.UDIResourceFileHelper;
import org.talend.resource.ResourceManager;
import org.talend.top.repository.ImplementationHelper;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * @author rli
 * 
 */
public class ResourceViewLabelProvider extends WorkbenchLabelProvider implements ICommonLabelProvider {

    private static Logger log = Logger.getLogger(ResourceViewLabelProvider.class);

    public void init(ICommonContentExtensionSite aConfig) {
    }

    protected ImageDescriptor decorateImage(ImageDescriptor input, Object element) {
        ImageDescriptor image = super.decorateImage(input, element);

        if (element instanceof IFile) {
            IFile file = (IFile) element;
            String fileExtension = file.getFileExtension();
            if (FactoriesUtil.isPatternFile(fileExtension)) {
                image = ImageLib.getImageDescriptor(ImageLib.PATTERN_REG);

                Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(file);
                if (pattern != null) {
                    if (!TaggedValueHelper.getValidStatus(pattern)) {
                        image = ImageLib.createInvalidIcon(ImageLib.PATTERN_REG);
                    }
                }
            } else if (FactoriesUtil.isReportFile(fileExtension)) {
                image = ImageLib.getImageDescriptor(ImageLib.REPORT_OBJECT);
            } else if (FactoriesUtil.isUDIFile(fileExtension)) {
                image = ImageLib.getImageDescriptor(ImageLib.IND_DEFINITION);

                IndicatorDefinition udi = UDIResourceFileHelper.getInstance().findUDI(file);
                if (udi != null) {
                    boolean validStatus = TaggedValueHelper.getValidStatus(udi) | UDIHelper.isUDIValid(udi);

                    if (!validStatus) {
                        image = ImageLib.createInvalidIcon(ImageLib.IND_DEFINITION);
                    }
                }
            }

            if (FactoriesUtil.isEmfFile(fileExtension)) {
                Property property = ModelElementFileFactory.getProperty(file);
                if (property != null) {
                    Item item = property.getItem();
                    Boolean lockByOthers = ImplementationHelper.getRepositoryManager().isLockByOthers(item);
                    Boolean lockByUserOwn = ImplementationHelper.getRepositoryManager().isLockByUserOwn(item);
                    if (lockByOthers || lockByUserOwn) {
                        log.info(property.getLabel() + " is locked");
                        image = ImageLib.createLockedIcon(image);
                    }
                }
            }

        } else if (element instanceof IFolder) {
            IFolder folder = (IFolder) element;
            if (ResourceManager.isMetadataFolder(folder)) {
                image = ImageLib.getImageDescriptor(ImageLib.METADATA);
            } else if (ResourceManager.isLibrariesFolder(folder)) {
                image = ImageLib.getImageDescriptor(ImageLib.LIBRARIES);
            } else if (ResourceManager.isDataProfilingFolder(folder)) {
                image = ImageLib.getImageDescriptor(ImageLib.DATA_PROFILING);
            } else if (ResourceManager.isConnectionFolder(folder)) {
                image = ImageLib.getImageDescriptor(ImageLib.CONNECTION);
            } else if (ResourceManager.isExchangeFolder(folder)) {
                image = ImageLib.getImageDescriptor(ImageLib.EXCHANGE);
            } else if (ResourceManager.isMdmConnectionFolder(folder)) {
                image = ImageLib.getImageDescriptor(ImageLib.CONNECTION);
            }
        }
        return image;
    }

    public String getDescription(Object anElement) {

        if (anElement instanceof IResource) {
            return ((IResource) anElement).getFullPath().makeRelative().toString();
        }
        return null;
    }

    public void restoreState(IMemento aMemento) {

    }

    public void saveState(IMemento aMemento) {
    }

    protected String decorateText(String input, Object element) {
        if (element instanceof IFile) {
            IFile file = (IFile) element;
            if (log.isDebugEnabled()) {
                log.debug("Loading file " + file.getLocation());
            }

            ModelElement mElement = ModelElementFileFactory.getModelElement(file);

            if (mElement != null) {
                return DqRepositoryViewService.buildElementName(mElement);
            }
        }
        input = DQStructureMessage.getString(super.decorateText(input, element));

        if (element instanceof IFolder) {
            if (input.startsWith(DQStructureManager.PREFIX_TDQ)) {
                input = input.replaceFirst(DQStructureManager.PREFIX_TDQ, ""); //$NON-NLS-1$
            }

            IFolder folder = (IFolder) element;
            if (ResourceManager.isAnalysisFolder(folder)) {
                input += "(" + getFileCount(folder, new String[] { "ana" }) + ")";
            } else if (ResourceManager.isReportsFolder(folder)) {
                input += "(" + getFileCount(folder, new String[] { "rep" }) + ")";
            }
        }

        return super.decorateText(input, element);
    }

    private int getFileCount(IFolder parent, String[] filterExtensions) {
        int i = 0;
        List<String> extensions = Arrays.asList(filterExtensions);
        try {
            IResource[] members = parent.members();
            for (IResource resource : members) {
                if (resource instanceof IFile) {
                    if (extensions.contains(((IFile) resource).getFileExtension()))
                        i++;
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return i;
    }

}
