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
package org.talend.dataprofiler.core.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.filters.DQFolderFliter;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.sql.JavaUserDefIndicator;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.UDIResourceFileHelper;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class UDIUtils {

    private UDIUtils() {
    }

    public static IndicatorCategory getUDICategory(IndicatorUnit unit) {
        return UDIHelper.getUDICategory(unit.getIndicator().getIndicatorDefinition());
    }

    public static String getUDICategoryLabel(IndicatorUnit unit) {
        IndicatorCategory ic = getUDICategory(unit);
        if (ic != null) {
            return ic.getLabel();
        }
        return null;
    }

    public static IndicatorUnit[] createIndicatorUnit(IFile pfile, ModelElementIndicator meIndicator, Analysis analysis)
            throws Throwable {
        List<IndicatorUnit> addIndicatorUnits = new ArrayList<IndicatorUnit>();

        IndicatorDefinition udid = UDIResourceFileHelper.getInstance().findUDI(pfile);
        IndicatorCategory ic = UDIHelper.getUDICategory(udid);

        // can't add the same user defined indicator
        for (Indicator indicator : meIndicator.getIndicators()) {
            if (udid.getName().equals(indicator.getName())) {
                MessageUI.openWarning(DefaultMessagesImpl.getString("UDIUtils.UDISelected") //$NON-NLS-1$
                        + udid.getName());
                return null;
            }
        }

        Indicator udi = UDIFactory.createUserDefIndicator(udid);
        udi.setIndicatorDefinition(udid);
        // MOD mzhao feature 11128, Handle Java User Defined Indicator.
        Indicator judi = UDIHelper.adaptToJavaUDI(udi);
        if (judi != null) {
            ((JavaUserDefIndicator) judi).setExecuteEngine(analysis.getParameters().getExecutionLanguage());
            udi = judi;
        }
        IndicatorEnum indicatorType = IndicatorEnum.findIndicatorEnum(udi.eClass());

        // MOD xqliu 2009-10-09 bug 9304
        // create user defined matching indicator
        // if (DefinitionHandler.getInstance().getUserDefinedMatchIndicatorCategory().equals(ic)) {
        // IFolder libProject = ResourceManager.getLibrariesFolder();
        // CheckedTreeSelectionDialog dialog = PatternUtilities.createPatternCheckedTreeSelectionDialog(libProject);
        // if (dialog.open() == Window.OK) {
        // for (Object obj : dialog.getResult()) {
        // if (obj instanceof IFile) {
        // IFile file = (IFile) obj;
        // IndicatorUnit addIndicatorUnit = PatternUtilities.createIndicatorUnit(file, columnIndicator, analysis,
        // udid);
        // if (addIndicatorUnit == null) {
        // Pattern pattern = PatternResourceFileHelper.getInstance().findPattern(file);
        //                            MessageUI.openError(DefaultMessagesImpl.getString("UDIUtils.PatternSelected") //$NON-NLS-1$
        // + pattern.getName());
        // } else {
        // addIndicatorUnits.add(addIndicatorUnit);
        // }
        // }
        // }
        // }
        // } else {
        addIndicatorUnits.add(meIndicator.addSpecialIndicator(indicatorType, udi));
        // }
        // ~

        DependenciesHandler.getInstance().setUsageDependencyOn(analysis, udid);
        return addIndicatorUnits.toArray(new IndicatorUnit[addIndicatorUnits.size()]);
    }

    /**
     * DOC xqliu Comment method "createUdiCheckedTreeSelectionDialog".
     * 
     * @param udiProject
     * @return
     */
    public static CheckedTreeSelectionDialog createUdiCheckedTreeSelectionDialog(IFolder udiProject) {
        CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(null, new UdiLabelProvider(),
                new WorkbenchContentProvider());
        dialog.setInput(udiProject);
        dialog.setValidator(new ISelectionStatusValidator() {

            public IStatus validate(Object[] selection) {
                IStatus status = Status.OK_STATUS;
                for (Object udi : selection) {
                    if (udi instanceof IFile) {
                        IFile file = (IFile) udi;
                        if (FactoriesUtil.DEFINITION.equals(file.getFileExtension())) {
                            IndicatorDefinition findUdi = UDIResourceFileHelper.getInstance().findUDI(file);
                            boolean validStatus = TaggedValueHelper.getValidStatus(findUdi);
                            if (!validStatus) {
                                status = new Status(IStatus.ERROR, CorePlugin.PLUGIN_ID, DefaultMessagesImpl
                                        .getString("AnalysisColumnTreeViewer.chooseValidUdis")); //$NON-NLS-1$
                            }
                        }
                    }
                }
                return status;
            }

        });
        dialog.addFilter(new DQFolderFliter(true));
        dialog.addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof IFile) {
                    IFile file = (IFile) element;
                    if (FactoriesUtil.DEFINITION.equals(file.getFileExtension())) {
                        return true;
                    }
                } else if (element instanceof IFolder) {
                    IFolder folder = (IFolder) element;
                    return ResourceService.isSubFolder(ResourceManager.getUDIFolder(), folder);
                }
                return false;
            }
        });
        dialog.setContainerMode(true);
        dialog.setTitle(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.udiSelector")); //$NON-NLS-1$
        dialog.setMessage(DefaultMessagesImpl.getString("AnalysisColumnTreeViewer.udis")); //$NON-NLS-1$
        dialog.setSize(80, 30);
        return dialog;
    }
}

/**
 * DOC xqliu class global comment. Detailled comment
 */
class UdiLabelProvider extends LabelProvider {

    @Override
    public Image getImage(Object element) {
        if (element instanceof IFolder) {
            return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
        }

        if (element instanceof IFile) {
            IndicatorDefinition findUdi = UDIResourceFileHelper.getInstance().findUDI((IFile) element);
            boolean validStatus = TaggedValueHelper.getValidStatus(findUdi);
            ImageDescriptor imageDescriptor = ImageLib.getImageDescriptor(ImageLib.IND_DEFINITION);
            if (!validStatus) {
                ImageDescriptor warnImg = PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(
                        ISharedImages.IMG_OBJS_WARN_TSK);
                DecorationOverlayIcon icon = new DecorationOverlayIcon(imageDescriptor.createImage(), warnImg,
                        IDecoration.BOTTOM_RIGHT);
                imageDescriptor = icon;
            }
            return imageDescriptor.createImage();
        }

        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof IFile) {
            IFile file = (IFile) element;
            IndicatorDefinition udi = UDIResourceFileHelper.getInstance().findUDI(file);
            if (udi != null) {
                return udi.getName();
            }
        }

        if (element instanceof IFolder) {
            return ((IFolder) element).getName();
        }

        return ""; //$NON-NLS-1$
    }
}
