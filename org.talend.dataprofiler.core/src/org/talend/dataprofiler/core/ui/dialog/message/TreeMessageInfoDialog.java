// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.dialog.message;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryViewObject;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog.ImpactNode;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.reports.TdReport;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ProxyRepositoryManager;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public class TreeMessageInfoDialog extends MessageDialog {

    private ITreeContentProvider contentProvider;

    private ILabelProvider labelProvider;

    private Object input;

    private Button checkButton;

    private boolean isChecked = false;

    private boolean needCheckbox = false;

    private List<Image> imagesNeedDisposedList = new ArrayList<Image>();

    public TreeMessageInfoDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage,
            int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
        super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels, defaultIndex);
    }

    @Override
    protected Control createCustomArea(Composite parent) {
        // MOD qiongli 2011-2-15,feature 17588.
        Composite mainComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.numColumns = 1;
        mainComposite.setLayout(layout);
        if (isNeedCheckbox()) {
            Composite checkComp = new Composite(mainComposite, SWT.NONE);
            GridLayout layout1 = new GridLayout();
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            layout.numColumns = 1;
            checkComp.setLayout(layout1);
            checkButton = new Button(checkComp, SWT.CHECK);
            checkButton.setText(DefaultMessagesImpl.getString("DQDeleteAction.deleteAllDependency"));//$NON-NLS-1$
            checkButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    isChecked = checkButton.getSelection();
                }

            });
        }
        TreeViewer viewer = new TreeViewer(mainComposite, SWT.BORDER);
        GridData gd = new GridData();
        gd.heightHint = 150;
        gd.widthHint = 450;
        viewer.getTree().setLayoutData(gd);
        viewer.setContentProvider(contentProvider);
        viewer.setLabelProvider(getLabelProvider());
        viewer.setInput(input);
        applyDialogFont(viewer.getControl());
        viewer.expandAll();
        return viewer.getControl();
    }

    public void setContentProvider(ITreeContentProvider provider) {
        contentProvider = provider;
    }

    public void setInput(Object input) {
        this.input = input;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public boolean isNeedCheckbox() {
        return this.needCheckbox;
    }

    public void setNeedCheckbox(boolean needCheckbox) {
        this.needCheckbox = needCheckbox;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.Dialog#close()
     */
    @Override
    public boolean close() {
        boolean status = super.close();
        // Displose images
        disposeImages();
        return status;
    }

    protected ILabelProvider getLabelProvider() {
        if (labelProvider == null) {
            labelProvider = new LabelProvider() {

                @Override
                public String getText(Object obj) {
                    if (obj == null) {
                        return ""; //$NON-NLS-1$
                    }
                    if (obj instanceof ImpactNode) {
                        return ((ImpactNode) obj).toString();
                    } else if (obj instanceof IFile) {
                        IFile file = (IFile) obj;
                        ModelElement modelElement = ModelElementFileFactory.getModelElement(file);
                        // MOD msjian TDQ-5909: modify to displayName
                        String name = modelElement != null ? PropertyHelper.getProperty(modelElement).getDisplayName() : file
                                .getName();
                        return name;
                        //return REQUIRES + PluginConstant.SPACE_STRING + "<<" + name + ">>";//$NON-NLS-1$ //$NON-NLS-2$
                    } else if (obj instanceof RepositoryViewObject) {// Added 20130226 TDQ-6899 show the name for Jrxml
                                                                     // object (which has no related ModelElement)
                        return ((IRepositoryViewObject) obj).getLabel();
                    }// ~

                    Property property = PropertyHelper.getProperty((ModelElement) obj);
                    return property == null ? ((ModelElement) obj).getName() : property.getDisplayName();
                    //REQUIRES + PluginConstant.SPACE_STRING+ "<<" + PropertyHelper.getProperty((ModelElement) obj).getDisplayName() + ">>"; //$NON-NLS-1$ //$NON-NLS-2$
                    // TDQ-5909~
                }

                @Override
                public Image getImage(Object obj) {
                    ModelElement modelElement = null;
                    if (obj instanceof ModelElement) {
                        modelElement = (ModelElement) obj;
                    } else if (obj instanceof ImpactNode) {
                        modelElement = ((ImpactNode) obj).getNodeElement();
                    } else if (obj instanceof IFile) {
                        modelElement = ModelElementFileFactory.getModelElement((IFile) obj);
                    } else if (obj instanceof RepositoryViewObject) {
                        // Added 20130226 TDQ-6899 show the name for Jrxml object (which has no related ModelElement)
                        return ImageLib.getImage(ImageLib.XML_DOC);
                    }
                    // ~
                    if (modelElement == null) {
                        if (((ImpactNode) obj).getNode() != null) {
                            return ImageLib.getImage(ImageLib.XML_DOC);
                        }
                        return super.getImage(obj);
                    }

                    Image modelElementImage = null;
                    if (modelElement instanceof Analysis) {
                        modelElementImage = ImageLib.getImage(ImageLib.ANALYSIS_OBJECT);
                    }
                    if (modelElement instanceof TdReport) {
                        modelElementImage = ImageLib.getImage(ImageLib.REPORT_OBJECT);
                    }
                    if (modelElement instanceof DatabaseConnection) {
                        modelElementImage = ImageLib.getImage(ImageLib.CONNECTION);
                    }
                    if (modelElement instanceof MDMConnection) {
                        modelElementImage = ImageLib.getImage(ImageLib.MDM_CONNECTION);
                    }
                    if (modelElement instanceof DelimitedFileConnection) {
                        modelElementImage = ImageLib.getImage(ImageLib.FILE_DELIMITED);
                    }
                    if (modelElement instanceof Pattern) {
                        modelElementImage = ImageLib.getImage(ImageLib.PATTERN_REG);
                    }
                    if (modelElement instanceof IndicatorDefinition) {
                        modelElementImage = ImageLib.getImage(ImageLib.IND_DEFINITION);
                    }
                    if (modelElement instanceof DQRule) {
                        modelElementImage = ImageLib.getImage(ImageLib.DQ_RULE);
                    }

                    // add lock icon on the image
                    if (modelElementImage != null) {
                        if (modelElement != null) {
                            Property property = PropertyHelper.getProperty(modelElement);
                            if (property != null) {
                                Item item = property.getItem();
                                if (item != null) {
                                    if (ProxyRepositoryManager.getInstance().isLockByUserOwn(item)) {
                                        modelElementImage = ImageLib.createLockedByOwnIcon(modelElementImage).createImage();
                                        imagesNeedDisposedList.add(modelElementImage);
                                    } else if (ProxyRepositoryManager.getInstance().isLockByOthers(item)) {
                                        modelElementImage = ImageLib.createLockedByOtherIcon(modelElementImage).createImage();
                                        imagesNeedDisposedList.add(modelElementImage);
                                    }
                                }
                            }
                        }
                        return modelElementImage;
                    }

                    return super.getImage(obj);
                }
            };
        }
        return labelProvider;
    }

    /**
     * DOC xqliu Comment method "disposeImages".
     */
    public void disposeImages() {
        if (!imagesNeedDisposedList.isEmpty()) {
            for (Image im : imagesNeedDisposedList) {
                im.dispose();
            }
            imagesNeedDisposedList.clear();
        }
    }
}
