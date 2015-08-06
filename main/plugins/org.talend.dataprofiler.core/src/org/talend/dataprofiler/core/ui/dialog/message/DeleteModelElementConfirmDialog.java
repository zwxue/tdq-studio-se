// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
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
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.MDMConnection;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.pattern.Pattern;
import org.talend.dataquality.indicators.definition.IndicatorDefinition;
import org.talend.dataquality.reports.TdReport;
import org.talend.dataquality.rules.DQRule;
import org.talend.dq.factory.ModelElementFileFactory;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.helper.ProxyRepositoryManager;
import org.talend.dq.helper.RepositoryNodeHelper;
import org.talend.repository.model.IRepositoryNode;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public class DeleteModelElementConfirmDialog {

    private static final String REQUIRES = "requires"; //$NON-NLS-1$

    private static LabelProvider fLabelProvider;

    private static List<ImpactNode> impactNodes = new ArrayList<ImpactNode>();

    /**
     * DOC rli DeleteModelElementConfirmDialog class global comment. Detailled comment
     */
    static class ImpactNode {

        List<Object> children = new ArrayList<Object>();

        private ModelElement nodeElement = null;

        private IRepositoryNode node = null;

        /**
         * Getter for node.
         * 
         * @return the node
         */
        public IRepositoryNode getNode() {
            return this.node;
        }

        /**
         * Sets the node.
         * 
         * @param node the node to set
         */
        public void setNode(IRepositoryNode node) {
            this.node = node;
        }

        public ImpactNode(ModelElement modelElement) {
            this.nodeElement = modelElement;
        }

        /**
         * DOC yyin ImpactNode constructor comment.
         * 
         * @param node
         */
        public ImpactNode(IRepositoryNode node) {
            this.node = node;
        }

        public void addRequireModelElement(Object object) {
            if (!children.contains(object)) {
                this.children.add(object);
            }
        }

        public Object[] getChildren() {
            return children.toArray(new Object[children.size()]);
        }

        @Override
        public String toString() {
            if (this.nodeElement != null) {
                return nodeElement.getName();
            } else {
                return this.node.getObject().getLabel();
            }
        }

        /**
         * Getter for nodeElement.
         * 
         * @return the nodeElement
         */
        public ModelElement getNodeElement() {
            return nodeElement;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((nodeElement == null) ? 0 : nodeElement.hashCode());
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ImpactNode other = (ImpactNode) obj;
            if (nodeElement == null) {
                if (other.nodeElement != null) {
                    return false;
                }
            } else if (nodeElement.getName() == other.nodeElement.getName()) {
                return true;
            } else if (!nodeElement.getName().equals(other.nodeElement.getName())) {
                return false;
            }
            return true;
        }

    }

    static void addDenpendencyElements(ModelElement[] children) {
        for (ModelElement element : children) {
            EList<Dependency> supplierDependencies = element.getSupplierDependency();
            ImpactNode impactNode;
            for (Dependency dependency : supplierDependencies) {
                EList<ModelElement> clients = dependency.getClient();
                for (ModelElement client : clients) {
                    // ~MOD mzhao feature 7488 2009-08-21.
                    // if (DependenciesHandler.isTDQElementProperties(client)) {
                    // continue;
                    // }
                    // ~
                    if (!client.eIsProxy()) {
                        impactNode = new ImpactNode(client);
                        int index = impactNodes.indexOf(impactNode);
                        if (index == -1) {
                            impactNode.addRequireModelElement(element);
                            impactNodes.add(impactNode);
                        } else {
                            ImpactNode existNode = impactNodes.get(index);
                            existNode.addRequireModelElement(element);
                        }
                    }
                }
            }
        }
    }

    static ImpactNode[] getImpactNodes() {
        return impactNodes.toArray(new ImpactNode[impactNodes.size()]);
    }

    static void clear() {
        impactNodes.clear();
    }

    /**
     * DOC rli RequirsResourceConfirmDialog class global comment. Detailled comment
     */
    protected static class DialogContentProvider implements ITreeContentProvider {

        ImpactNode[] treeNode;

        DialogContentProvider(ImpactNode[] treeNode) {
            this.treeNode = treeNode;
        }

        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof ImpactNode) {
                return ((ImpactNode) parentElement).getChildren();
            }
            return null;
        }

        public Object getParent(Object element) {
            return null;
        }

        public boolean hasChildren(Object element) {
            return element instanceof ImpactNode;
        }

        public Object[] getElements(Object inputElement) {
            return treeNode;
        }

        public void dispose() {
            // do nothing here ???
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // do nothing here ???
        }
    }

    /**
     * DOC bZhou Comment method "showDialog".
     * 
     * @param parentShell
     * @param file
     * @param dependencyElements
     * @param dialogMessage
     * @return
     */
    public static boolean showDialog(Shell parentShell, Object obj, ModelElement[] dependencyElements, String dialogMessage,
            boolean isNeedCheckbox) {
        // MOD qiongli 2011-3-1 feature 17588.return boolean,add 'dialog.setNeedCheckbox(true)'
        for (ModelElement element : dependencyElements) {
            ImpactNode node = new ImpactNode(element);
            if (!impactNodes.contains(node)) {
                node.addRequireModelElement(obj);
                impactNodes.add(node);
            }
        }

        ImpactNode[] impactElements = getImpactNodes();
        boolean isChecked = false;
        if (impactElements.length > 0) {
            if (lockedByOthers(impactElements)) {
                MessageUI.openError(DefaultMessagesImpl.getString("DQDeleteAction.lockedByOthers")); //$NON-NLS-1$
            } else {
                TreeMessageInfoDialog dialog = new TreeMessageInfoDialog(
                        parentShell,
                        DefaultMessagesImpl.getString("DeleteModelElementConfirmDialog.confirmResourceDelete"), null, dialogMessage, //$NON-NLS-1$
                        MessageDialog.WARNING, new String[] { IDialogConstants.OK_LABEL }, 1);
                dialog.setNeedCheckbox(isNeedCheckbox);
                dialog.setContentProvider(new DialogContentProvider(impactElements));
                dialog.setInput(new Object());
                clear();
                // MOD qiongli 2012-1-6 if don't click OK button,should return false;
                int result = dialog.open();
                isChecked = dialog.isChecked() && (result == Window.OK);
            }
        }

        return isChecked;
    }

    /**
     * DOC xqliu Comment method "lockedByOthers".
     * 
     * @param impactElements
     * @return
     */
    private static boolean lockedByOthers(ImpactNode[] impactElements) {
        for (ImpactNode node : impactElements) {
            if (lockedByOthers(node)) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOC xqliu Comment method "lockedByOthers".
     * 
     * @param node
     * @return
     */
    private static boolean lockedByOthers(ImpactNode node) {
        boolean result = false;
        ModelElement nodeElement = node.getNodeElement();
        if (nodeElement != null) {
            Property property = PropertyHelper.getProperty(nodeElement);
            if (property != null) {
                result = ProxyRepositoryManager.getInstance().isLockByOthers(property.getItem());
            }
        }
        return result;
    }

    /**
     * 
     * DOC qiongli Comment method "showDialog".
     * 
     * @param parentShell
     * @param repositoryNodes
     * @param dependencyElements
     * @param dialogMessage
     * @return
     */
    public static int showDialog(Shell parentShell, List<IRepositoryNode> repositoryNodes, String dialogMessage) {
        for (IRepositoryNode repNode : repositoryNodes) {
            List<ModelElement> dependencies = new ArrayList<ModelElement>();
            Object object = null;
            if (repNode.getObjectType() == ERepositoryObjectType.METADATA_CON_TABLE
                    || repNode.getObjectType() == ERepositoryObjectType.METADATA_CON_VIEW) {
                dependencies = EObjectHelper.getFirstDependency(repNode);
                object = RepositoryNodeHelper.getMetadataElement(repNode);
            } else {
                dependencies = EObjectHelper.getDependencyClients(repNode);
                object = RepositoryNodeHelper.getModelElementFromRepositoryNode(repNode);
            }
            for (ModelElement element : dependencies) {
                ImpactNode node = new ImpactNode(element);
                if (!impactNodes.contains(node)) {
                    node.addRequireModelElement(object);
                    impactNodes.add(node);
                } else if (!Arrays.asList(node.getChildren()).contains(object)) {
                    int index = impactNodes.indexOf(node);
                    if (index != -1) {
                        impactNodes.get(index).addRequireModelElement(object);
                    }
                }
            }
        }
        ImpactNode[] impactElements = getImpactNodes();
        if (impactElements.length > 0) {
            TreeMessageInfoDialog dialog = new TreeMessageInfoDialog(parentShell,
                    DefaultMessagesImpl.getString("DeleteModelElementConfirmDialog.confirmResourceDelete"), null, dialogMessage, //$NON-NLS-1$
                    MessageDialog.WARNING, new String[] { IDialogConstants.OK_LABEL }, 1);
            dialog.setContentProvider(new DialogContentProvider(impactElements));
            dialog.setInput(new Object());
            clear();
            return dialog.open();
        }

        return Window.CANCEL;
    }

    /**
     * DOC bzhou Comment method "showConfirmDialog".
     * 
     * @param parentShell
     * @param obj
     * @param dependencyElements
     * @param dialogMessage
     * @return
     */
    public static int showConfirmDialog(Shell parentShell, Object obj, ModelElement[] dependencyElements, String dialogTitle,
            String dialogMessage) {
        for (ModelElement element : dependencyElements) {
            ImpactNode node = new ImpactNode(element);
            if (!impactNodes.contains(node)) {
                node.addRequireModelElement(obj);
                impactNodes.add(node);
            }
        }

        ImpactNode[] impactElements = getImpactNodes();
        if (impactElements.length > 0) {
            TreeMessageInfoDialog dialog = new TreeMessageInfoDialog(parentShell, dialogTitle, null, dialogMessage,
                    MessageDialog.WARNING, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
            dialog.setContentProvider(new DialogContentProvider(impactElements));
            dialog.setInput(new Object());
            clear();
            return dialog.open();
        }

        return Window.OK;
    }

    /**
     * DOC rli Comment method "showDialog".
     * 
     * @param parentShell
     * @param modelElements
     * @param dialogMessage
     * @return
     */
    public static int showDialog(Shell parentShell, ModelElement[] modelElements, String dialogMessage) {
        addDenpendencyElements(modelElements);
        ImpactNode[] impactElements = getImpactNodes();
        if (impactElements.length > 0) {
            TreeMessageInfoDialog dialog = new TreeMessageInfoDialog(parentShell,
                    DefaultMessagesImpl.getString("DeleteModelElementConfirmDialog.confirmResourceDelete"), null, dialogMessage, //$NON-NLS-1$
                    MessageDialog.WARNING, new String[] { IDialogConstants.OK_LABEL }, 1);
            dialog.setContentProvider(new DialogContentProvider(impactElements));
            dialog.setInput(new Object());
            clear();
            dialog.open();
            return Window.CANCEL;
        } else {
            return popConfirmDialog(modelElements);
        }
    }

    /**
     * 
     * MOD mzhao 2009-03-26, Impact existing analyses when copy remote structure from remote to local on comparison
     * editor.
     * 
     * @param parentShell
     * @param modelElements
     * @param dialogMessage
     * @return
     */
    public static int showElementImpactDialog(Shell parentShell, ModelElement[] modelElements, String dialogTitle,
            String dialogMessage) {
        addDenpendencyElements(modelElements);
        ImpactNode[] impactElements = getImpactNodes();
        if (impactElements.length > 0) {
            TreeMessageInfoDialog dialog = new TreeMessageInfoDialog(parentShell, dialogTitle, null, dialogMessage,
                    MessageDialog.WARNING, new String[] { IDialogConstants.OK_LABEL }, 0);
            dialog.setContentProvider(new DialogContentProvider(impactElements));
            dialog.setInput(new Object());
            clear();
            int result = dialog.open();
            return result;
        } else {
            return Window.OK;
        }
    }

    public static int showElementImpactConfirmDialog(Shell parentShell, ModelElement[] modelElements, String dialogTitle,
            String dialogMessage) {
        addDenpendencyElements(modelElements);
        ImpactNode[] impactElements = getImpactNodes();
        if (impactElements.length > 0) {
            TreeMessageInfoDialog dialog = new TreeMessageInfoDialog(parentShell, dialogTitle, null, dialogMessage,
                    MessageDialog.WARNING, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
            dialog.setContentProvider(new DialogContentProvider(impactElements));
            dialog.setInput(new Object());
            clear();
            int result = dialog.open();
            return result;
        } else {
            return Window.OK;
        }
    }

    private static int popConfirmDialog(ModelElement[] modelElements) {
        MessageDialog messageDialog;
        if (modelElements.length > 1) {
            messageDialog = new MessageDialog(
                    null,
                    DefaultMessagesImpl.getString("DeleteModelElementConfirmDialog.confirmResourceDelete"), null, //$NON-NLS-1$
                    DefaultMessagesImpl.getString("DeleteModelElementConfirmDialog.deleleTheseResources"), MessageDialog.WARNING, new String[] { //$NON-NLS-1$
                    IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);

        } else {
            messageDialog = new MessageDialog(
                    null,
                    DefaultMessagesImpl.getString("DeleteModelElementConfirmDialog.confirmResourcesDelete"), null, DefaultMessagesImpl.getString("DeleteModelElementConfirmDialog.areYouDelele", modelElements[0].getName()) //$NON-NLS-1$ //$NON-NLS-2$ 
                    , MessageDialog.WARNING, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);
        }
        return messageDialog.open();
    }

    // private static void removeReportComponent(ImpactNode[] impactNodes) {
    // ReportsSwitch<TdReport> mySwitch = new ReportsSwitch<TdReport>() {
    //
    // public TdReport caseTdReport(TdReport object) {
    // return object;
    // }
    // };
    // TdReport report = null;
    // for (ImpactNode node : impactNodes) {
    // report = mySwitch.doSwitch(node.getNodeElement());
    // if (report != null && node.getChildren().length > 0) {
    // List<Analysis> anaList = new ArrayList<Analysis>();
    // for (ModelElement element : node.getChildren()) {
    // anaList.add((Analysis) element);
    // }
    // ReportHelper.removeAnalyses(report, anaList);
    // // remove dependencies
    // DependenciesHandler.getInstance().removeDependenciesBetweenModels(report, anaList);
    // }
    // }
    // }

    protected static LabelProvider getLabelProvider() {
        if (fLabelProvider == null) {
            fLabelProvider = new LabelProvider() {

                @Override
                public String getText(Object obj) {
                    if (obj instanceof ImpactNode) {
                        return ((ImpactNode) obj).toString();
                    } else if (obj instanceof IFile) {
                        IFile file = (IFile) obj;
                        ModelElement modelElement = ModelElementFileFactory.getModelElement(file);
                        // MOD msjian TDQ-5909: modify to displayName
                        String name = modelElement != null ? PropertyHelper.getProperty(modelElement).getDisplayName() : file
                                .getName();
                        return REQUIRES + PluginConstant.SPACE_STRING + "<<" + name + ">>";//$NON-NLS-1$ //$NON-NLS-2$
                    }
                    return REQUIRES + PluginConstant.SPACE_STRING
                            + "<<" + PropertyHelper.getProperty((ModelElement) obj).getDisplayName() + ">>"; //$NON-NLS-1$ //$NON-NLS-2$
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
                    }
                    if (modelElement == null) {
                        return super.getImage(obj);
                    }
                    if (modelElement instanceof Analysis) {
                        return ImageLib.getImage(ImageLib.ANALYSIS_OBJECT);
                    }
                    if (modelElement instanceof TdReport) {
                        return ImageLib.getImage(ImageLib.REPORT_OBJECT);
                    }
                    if (modelElement instanceof DatabaseConnection) {
                        return ImageLib.getImage(ImageLib.CONNECTION);
                    }
                    if (modelElement instanceof MDMConnection) {
                        return ImageLib.getImage(ImageLib.MDM_CONNECTION);
                    }
                    if (modelElement instanceof DelimitedFileConnection) {
                        return ImageLib.getImage(ImageLib.FILE_DELIMITED);
                    }
                    if (modelElement instanceof Pattern) {
                        return ImageLib.getImage(ImageLib.PATTERN_REG);
                    }
                    if (modelElement instanceof IndicatorDefinition) {
                        return ImageLib.getImage(ImageLib.IND_DEFINITION);
                    }
                    if (modelElement instanceof DQRule) {
                        return ImageLib.getImage(ImageLib.DQ_RULE);
                    }

                    return super.getImage(obj);
                }
            };
        }
        return fLabelProvider;
    }

}
