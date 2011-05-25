// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ITreeContentProvider;
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
import org.talend.commons.utils.WorkspaceUtils;
import org.talend.core.model.metadata.builder.database.PluginConstant;
import org.talend.core.model.properties.Item;
import org.talend.cwm.dependencies.DependenciesHandler;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.dialog.JavaUdiJarSelectDialog;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.filters.DQFolderFliter;
import org.talend.dataprofiler.core.ui.filters.FolderObjFilter;
import org.talend.dataprofiler.core.ui.filters.RecycleBinFilter;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.JavaUDIIndicatorParameter;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.definition.IndicatorCategory;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.indicators.definition.IndicatorDefinitionParameter;
import org.talend.dataquality.indicators.sql.JavaUserDefIndicator;
import org.talend.dataquality.properties.TDQIndicatorDefinitionItem;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.dq.helper.UDIHelper;
import org.talend.dq.helper.resourcehelper.IndicatorResourceFileHelper;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.repository.model.IRepositoryNode;
import org.talend.resource.ResourceManager;
import org.talend.resource.ResourceService;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public final class UDIUtils {

    public static final String JAREXTENSIONG = "jar"; //$NON-NLS-1$

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

        IndicatorDefinition udid = IndicatorResourceFileHelper.getInstance().findIndDefinition(pfile);

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

        IndicatorParameters parameters = udi.getParameters();
        if (parameters == null) {
            parameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
            udi.setParameters(parameters);
        }
        Domain indicatorValidDomain = parameters.getIndicatorValidDomain();
        if (indicatorValidDomain == null) {
            indicatorValidDomain = DomainHelper.createDomain("JAVA_UDI_PARAMETERS"); //$NON-NLS-1$
            parameters.setIndicatorValidDomain(indicatorValidDomain);
        }
        List<IndicatorDefinitionParameter> indicatorDefs = udid.getIndicatorDefinitionParameter();
        for (IndicatorDefinitionParameter idp : indicatorDefs) {
            JavaUDIIndicatorParameter judip = DomainHelper.createJavaUDIIndicatorParameter(idp.getKey(), idp.getValue());
            indicatorValidDomain.getJavaUDIIndicatorParameter().add(judip);
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
        dialog.addFilter(new RecycleBinFilter());
        dialog.addFilter(new FolderObjFilter());
        dialog.setInput(udiProject);
        dialog.setValidator(new ISelectionStatusValidator() {

            public IStatus validate(Object[] selection) {
                IStatus status = Status.OK_STATUS;
                for (Object udi : selection) {
                    if (udi instanceof IFile) {
                        IFile file = (IFile) udi;
                        if (FactoriesUtil.DEFINITION.equals(file.getFileExtension())) {
                            IndicatorDefinition findUdi = IndicatorResourceFileHelper.getInstance().findIndDefinition(file);
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

    /**
     * 
     * zshen Comment method "createUdiJarCheckedTreeSelectionDialog".
     * 
     * @param udiJarProject
     * @return
     */
    public static JavaUdiJarSelectDialog createUdiJarCheckedTreeSelectionDialog(IFolder udiJarProject, String[] selectionPath) {

        JavaUdiJarSelectDialog dialog = new JavaUdiJarSelectDialog(null, new UdiLabelProvider(), new UdiJarContentProvider());
        // dialog.addFilter(new RecycleBinFilter());
        // dialog.addFilter(new FolderObjFilter());
        dialog.setInput(udiJarProject);
        dialog.setCheckedElements(selectionPath);
        dialog.setValidator(new ISelectionStatusValidator() {

            public IStatus validate(Object[] selection) {
                IStatus status = Status.OK_STATUS;
                for (Object udi : selection) {
                    if (udi instanceof IFile) {
                        IFile file = (IFile) udi;
                        if (FactoriesUtil.DEFINITION.equals(file.getFileExtension())) {
                            IndicatorDefinition findUdi = IndicatorResourceFileHelper.getInstance().findIndDefinition(file);
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
        // dialog.addFilter(new DQFolderFliter(true));
        dialog.addFilter(new ViewerFilter() {

            @Override
            public boolean select(Viewer viewer, Object parentElement, Object element) {
                if (element instanceof File) {
                    File file = (File) element;
                    if (JAREXTENSIONG.equals(new Path(file.getName()).getFileExtension())) {
                        return true;
                    }
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

    /**
     * 
     * zshen Comment method "getLibJarFileList".
     * 
     * @return
     */
    public static List<IFile> getLibJarFileList() {
        List<IFile> fileList = new ArrayList<IFile>();

        List<File> libJarFileList = getLibJarFileList(ResourceManager.getUDIJarFolder().getLocation().toFile());

        for (File jarFile : libJarFileList) {
            IFile ifile = WorkspaceUtils.fileToIFile(jarFile);
            if (ifile.exists()) {
                fileList.add(ifile);
            }
        }

        return fileList;
    }

    public static List<File> getLibJarFileList(File udiJarFolder) {
        List<File> fileList = new ArrayList<File>();

        if (udiJarFolder.isDirectory()) {

            for (File jarFile : udiJarFolder.listFiles()) {
                if (jarFile.isFile() && jarFile.getName().endsWith(JAREXTENSIONG)) {
                    fileList.add(jarFile);
                }
            }
        }

        return fileList;
    }

    /**
     * 
     * zshen Comment method "getContainJarFile".
     * 
     * @param jarPathStr
     * @return
     */
    public static List<IFile> getContainJarFile(String jarPathStr) {
        List<IFile> fileList = new ArrayList<IFile>();

        for (String containJarName : jarPathStr.split("\\|\\|")) { //$NON-NLS-1$
            for (IFile libJarFile : getLibJarFileList()) {
                if (libJarFile.getName().equalsIgnoreCase(containJarName)) {
                    fileList.add(libJarFile);
                    break;
                }
            }
        }
        return fileList;
    }

    public static ReturnCode checkUDIDependency(File delFile) {
        ReturnCode result = new ReturnCode(true);
        IPath filePath = new Path(delFile.getPath());
        if (!ResourceManager.getUDIJarFolder().getLocation().isPrefixOf(filePath)) {
            // filePath.makeRelativeTo(ResourceManager.getRootFolderLocation()))) {
            return result;
        }
        // for (IndicatorDefinition file : IndicatorResourceFileHelper.getInstance().getAllUDIs()) {
        // EMFSharedResources.getInstance().reloadResource(file.eResource().getURI());
        // }
        // IndicatorResourceFileHelper.getInstance().clear();

        for (IRepositoryNode indiDefNode : RepositoryNodeHelper.getUdisRepositoryNodes(true)) {
            IndicatorDefinition indiDef = null;
            Item item = indiDefNode.getObject().getProperty().getItem();
            if (item instanceof TDQIndicatorDefinitionItem) {
                indiDef = ((TDQIndicatorDefinitionItem) item).getIndicatorDefinition();
            } else {
                continue;
            }
            TaggedValue tv = TaggedValueHelper.getTaggedValue(PluginConstant.JAR_FILE_PATH, indiDef.getTaggedValue());
            if (tv == null) {
                continue;
            }
            String[] strArray = tv.getValue().split("\\|\\|"); //$NON-NLS-1$
            int index = Arrays.binarySearch(strArray, filePath.lastSegment());
            if (index >= 0) {
                result.setMessage("The jar file(" + strArray[index] + ") has in use by UDI for " + indiDef.getName());//$NON-NLS-1$ //$NON-NLS-2$
                result.setOk(false);
                return result;
            }
        }
        return result;
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
            IndicatorDefinition findUdi = IndicatorResourceFileHelper.getInstance().findIndDefinition((IFile) element);
            boolean validStatus = TaggedValueHelper.getValidStatus(findUdi);
            ImageDescriptor imageDescriptor = ImageLib.getImageDescriptor(ImageLib.IND_DEFINITION);
            if (!validStatus) {
                ImageDescriptor warnImg = PlatformUI.getWorkbench().getSharedImages()
                        .getImageDescriptor(ISharedImages.IMG_OBJS_WARN_TSK);
                DecorationOverlayIcon icon = new DecorationOverlayIcon(imageDescriptor.createImage(), warnImg,
                        IDecoration.BOTTOM_RIGHT);
                imageDescriptor = icon;
            }
            return imageDescriptor.createImage();
        }

        if (element instanceof File) {
            return ImageLib.getImage(ImageLib.JAR_FILE);
        }

        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof IFile) {
            IFile file = (IFile) element;
            IndicatorDefinition udi = IndicatorResourceFileHelper.getInstance().findIndDefinition(file);
            if (udi != null) {
                return udi.getName();
            }
        }

        if (element instanceof IFolder) {
            return ((IFolder) element).getName();
        }

        if (element instanceof File) {
            File file = (File) element;
            return file.getName();
        }

        return ""; //$NON-NLS-1$
    }

}

/**
 * 
 * @author zshen
 * 
 */
class UdiJarContentProvider implements ITreeContentProvider {

    /*
     * (non-Jsdoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object,
     * java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {

        return getChildren(inputElement);
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof IFolder) {
            return Arrays.asList(((IFolder) parentElement).getLocation().toFile().listFiles()).toArray();
        }
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
     */
    public Object getParent(Object element) {
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
     */
    public boolean hasChildren(Object element) {
        return false;
    }
}
