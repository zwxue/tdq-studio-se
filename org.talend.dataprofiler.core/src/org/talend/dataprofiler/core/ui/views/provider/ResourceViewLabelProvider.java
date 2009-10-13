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

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.AnaResourceFileHelper;
import org.talend.dq.helper.resourcehelper.DQRuleResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PatternResourceFileHelper;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.RepResourceFileHelper;
import org.talend.dq.helper.resourcehelper.UDIResourceFileHelper;
import org.talend.resource.ResourceManager;
import org.talend.utils.sugars.TypedReturnCode;
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
        if (element instanceof IFile) {
            IFile file = (IFile) element;
            if (FactoriesUtil.PATTERN.equalsIgnoreCase(file.getFileExtension())) {
                Pattern findPattern = PatternResourceFileHelper.getInstance().findPattern(file);
                ImageDescriptor imageDescriptor = ImageLib.getImageDescriptor(ImageLib.PATTERN_REG);
                if (findPattern != null) {
                    boolean validStatus = TaggedValueHelper.getValidStatus(findPattern);
                    if (!validStatus) {
                        ImageDescriptor warnImg = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                                ISharedImages.IMG_OBJS_WARN_TSK);
                        DecorationOverlayIcon icon = new DecorationOverlayIcon(imageDescriptor.createImage(), warnImg,
                                IDecoration.BOTTOM_RIGHT);
                        imageDescriptor = icon;
                    }
                }
                return imageDescriptor;
            } else if (FactoriesUtil.REP.equalsIgnoreCase(file.getFileExtension())) {
                return ImageLib.getImageDescriptor(ImageLib.REPORT_OBJECT);
            } else if (FactoriesUtil.UDI.equalsIgnoreCase(file.getFileExtension())) {
                // MOD yyi 2009-09-15, add warning icon for imported indicators
                // Feature 8866
                IndicatorDefinition findUDI = UDIResourceFileHelper.getInstance().findUDI(file);
                ImageDescriptor imageDescriptor = ImageLib.getImageDescriptor(ImageLib.IND_DEFINITION);
                if (findUDI != null) {
                    boolean validStatus = TaggedValueHelper.getValidStatus(findUDI) | UDIHelper.isUDIValid(findUDI);
                    if (!validStatus) {
                        ImageDescriptor warnImg = ImageLib.getImageDescriptor(ImageLib.WARN_OVR);
                        PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_WARN_TSK);
                        DecorationOverlayIcon icon = new DecorationOverlayIcon(imageDescriptor.createImage(), warnImg,
                                IDecoration.BOTTOM_RIGHT);
                        imageDescriptor = icon;
                    }
                }
                return imageDescriptor;
            }
        }
        // MOD mzhao 2009-03-20,Move tdq/top top folders into one project.
        // Feature 6066.
        if (element instanceof IFolder) {
            String folderName = ((IFolder) element).getName();
            if (ResourceManager.METADATA_FOLDER_NAME.equals(folderName)) {
                return ImageLib.getImageDescriptor(ImageLib.METADATA);
            } else if (ResourceManager.LIBRARIES_FOLDER_NAME.equals(folderName)) {
                return ImageLib.getImageDescriptor(ImageLib.LIBRARIES);
            } else if (ResourceManager.DATA_PROFILING_FOLDER_NAME.equals(folderName)) {
                return ImageLib.getImageDescriptor(ImageLib.DATA_PROFILING);
            } else if (org.talend.dataquality.PluginConstant.DB_CONNECTIONS.equals(folderName)) {
                return ImageLib.getImageDescriptor(ImageLib.CONNECTION);
            } else if (DQStructureManager.EXCHANGE.equals(folderName)) {
                return ImageLib.getImageDescriptor(ImageLib.EXCHANGE);
            }
        }
        return super.decorateImage(input, element);
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

            ModelElement mElement = null;
            if (input.endsWith(FactoriesUtil.PROV)) {
                TypedReturnCode<TdDataProvider> rc = PrvResourceFileHelper.getInstance().findProvider(file);
                if (rc.isOk()) {
                    mElement = rc.getObject();
                } else {
                    log.error(rc.getMessage());
                }
            } else if (input.endsWith(FactoriesUtil.ANA)) {
                mElement = AnaResourceFileHelper.getInstance().findAnalysis(file);
            } else if (input.endsWith(FactoriesUtil.PATTERN)) {
                mElement = PatternResourceFileHelper.getInstance().findPattern(file);
            } else if (input.endsWith(FactoriesUtil.REP)) {
                mElement = RepResourceFileHelper.getInstance().findReport(file);
            } else if (input.endsWith(FactoriesUtil.DQRULE)) {
                mElement = DQRuleResourceFileHelper.getInstance().findWhereRule(file);
            } else if (input.endsWith(FactoriesUtil.UDI)) {
                mElement = UDIResourceFileHelper.getInstance().findUDI(file);
            }

            if (mElement != null) {
                return DqRepositoryViewService.buildElementName(mElement);
            }
        }

        if (element instanceof IFolder && input.startsWith(DQStructureManager.PREFIX_TDQ)) {
            input = input.replaceFirst(DQStructureManager.PREFIX_TDQ, ""); //$NON-NLS-1$
        }

        return super.decorateText(input, element);
    }
}
