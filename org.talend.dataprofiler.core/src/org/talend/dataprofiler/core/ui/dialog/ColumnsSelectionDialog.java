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
package org.talend.dataprofiler.core.ui.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.MDMConnectionItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.ModelElementHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.XmlElementHelper;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.xml.TdXmlElementType;
import org.talend.cwm.xml.TdXmlSchema;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.exception.MessageBoxExceptionHandler;
import org.talend.dataprofiler.core.helper.FolderNodeHelper;
import org.talend.dataprofiler.core.model.nodes.foldernode.NamedColumnSetFolderNode;
import org.talend.dataprofiler.core.ui.dialog.provider.DBTablesViewLabelProvider;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.filters.DQFolderFliter;
import org.talend.dataprofiler.core.ui.filters.EMFObjFilter;
import org.talend.dataprofiler.core.ui.filters.TDQEEConnectionFolderFilter;
import org.talend.dataprofiler.core.ui.utils.ComparatorsFactory;
import org.talend.dataprofiler.core.ui.views.provider.DQRepositoryViewContentProvider;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.dq.nodes.foldernode.IFolderNode;
import org.talend.resource.ResourceManager;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Catalog;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * @author Select the special columns from this dialog.
 * 
 */
public class ColumnsSelectionDialog extends TwoPartCheckSelectionDialog {

    private static Logger log = Logger.getLogger(ColumnsSelectionDialog.class);

    private Map<ModelElementKey, ModelElementCheckedMap> modelElementCheckedMap;

    private List<ModelElement> currentCheckedModelElement = new ArrayList<ModelElement>();

    private IFolder metadataFolder = ResourceManager.getMetadataFolder();

    public ColumnsSelectionDialog(AbstractAnalysisMetadataPage metadataFormPage, Shell parent, String title,
            List<? extends ModelElement> modelElementList, String message) {
        super(metadataFormPage, parent, message);
        modelElementCheckedMap = new HashMap<ModelElementKey, ModelElementCheckedMap>();
        initCheckedModelElement(modelElementList);

        addFilter(new EMFObjFilter());
        addFilter(new DQFolderFliter(true));
        addFilter(new TDQEEConnectionFolderFilter());
        setInput(metadataFolder);
        setTitle(title);
    }

    @Override
    /**
     * DOC mzhao bug 9240 mzhao 2009-11-05
     */
    protected void unfoldToCheckedElements() {
        Iterator<ModelElementKey> it = modelElementCheckedMap.keySet().iterator();
        while (it.hasNext()) {
            ModelElementKey mek = it.next();
            getTreeViewer().expandToLevel(mek.getModelElement(), 1);
            StructuredSelection structSel = new StructuredSelection(mek.getModelElement());
            getTreeViewer().setSelection(structSel);
        }
    }

    private void initCheckedModelElement(List<? extends ModelElement> modelElementList) {
        List<ModelElement> containerList = new ArrayList<ModelElement>();
        for (int i = 0; i < modelElementList.size(); i++) {
            ModelElement modelElement = modelElementList.get(i);
            ModelElement container = ModelElementHelper.getContainer(modelElement);
            if (!containerList.contains(container)) {
                containerList.add(container);
            }
            ModelElementKey modelElementKey = new ModelElementKeyImpl(container);
            ModelElementCheckedMap meCheckedMap = modelElementCheckedMap.get(modelElementKey);
            if (meCheckedMap == null) {
                meCheckedMap = new ModelElementCheckedMapImpl();
                this.modelElementCheckedMap.put(modelElementKey, meCheckedMap);
            }
            meCheckedMap.putModelElementChecked(modelElement, Boolean.TRUE);
        }
        this.setInitialElementSelections(containerList);
    }

    protected void initProvider() {
        fLabelProvider = new DBTablesViewLabelProvider();
        fContentProvider = new DBTreeViewContentProvider();
        sLabelProvider = new ModelElementLabelProvider();
        sContentProvider = new ModelElementContentProvider();
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataprofiler.core.ui.dialog.TwoPartCheckSelectionDialog# addCheckedListener()
     */
    protected void addCheckedListener() {

        // When user checks a checkbox in the tree, check all its children
        getTreeViewer().addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                ColumnSelectionViewer columnViewer = (ColumnSelectionViewer) event.getSource();
                TreePath treePath = new TreePath(new Object[] { event.getElement() });
                columnViewer.setSelection(new TreeSelection(treePath));

                getTreeViewer().setSubtreeChecked(event.getElement(), event.getChecked());
                setOutput(event.getElement());
                if (event.getElement() instanceof ColumnSet) {
                    handleColumnsChecked((ColumnSet) event.getElement(), event.getChecked());
                } else if (event.getElement() instanceof TdXmlSchema) {
                    handleXmlDocumentChecked((TdXmlSchema) event.getElement(), event.getChecked());
                } else if (event.getElement() instanceof TdXmlElementType) {
                    handleXmlElementsChecked((TdXmlElementType) event.getElement(), event.getChecked());
                }
            }
        });

        getTableViewer().addCheckStateListener(new ICheckStateListener() {

            public void checkStateChanged(CheckStateChangedEvent event) {
                if (event.getElement() instanceof TdColumn) {
                    handleColumnChecked((TdColumn) event.getElement(), event.getChecked());
                } else if (event.getElement() instanceof TdXmlElementType) {
                    handleXmlElementChecked((TdXmlElementType) event.getElement(), event.getChecked());
                }
            }
        });
    }

    /**
     * DOC xqliu Comment method "getCheckedModelElements".
     * 
     * @param modelElement
     * @return
     */
    private ModelElement[] getCheckedModelElements(ModelElement modelElement) {
        boolean isColumnSet = modelElement instanceof ColumnSet;
        boolean isTdXmlSchema = modelElement instanceof TdXmlSchema;
        boolean isTdXmlElementType = modelElement instanceof TdXmlElementType;

        ModelElementKey mek = new ModelElementKeyImpl(modelElement);
        ModelElementCheckedMap meCheckMap = modelElementCheckedMap.get(mek);
        if (meCheckMap == null) {
            Boolean allCheckFlag = this.getTreeViewer().getChecked(modelElement);
            this.getTableViewer().setAllChecked(allCheckFlag);
            meCheckMap = new ModelElementCheckedMapImpl();
            ModelElement[] modelElements = null;
            if (isColumnSet) {
                modelElements = EObjectHelper.getColumns((ColumnSet) modelElement);
            } else if (isTdXmlSchema) {
                List<TdXmlElementType> children = XmlElementHelper.getLeafNode(DqRepositoryViewService
                        .getXMLElements((TdXmlSchema) modelElement));
                modelElements = children.toArray(new ModelElement[children.size()]);
            } else if (isTdXmlElementType) {
                List<TdXmlElementType> children = XmlElementHelper.getLeafNode(DqRepositoryViewService
                        .getXMLElements((TdXmlElementType) modelElement));
                modelElements = children.toArray(new ModelElement[children.size()]);
            }
            meCheckMap.putAllChecked(modelElements, allCheckFlag);
            modelElementCheckedMap.put(mek, meCheckMap);
            return allCheckFlag ? modelElements : null;
        } else {
            if (isColumnSet) {
                return meCheckMap.getCheckedModelElements(ColumnSetHelper.getColumns((ColumnSet) modelElement));
            } else if (isTdXmlSchema) {
                List<TdXmlElementType> children = XmlElementHelper.getLeafNode(DqRepositoryViewService
                        .getXMLElements((TdXmlSchema) modelElement));
                return meCheckMap.getCheckedModelElements(children);
            } else if (isTdXmlElementType) {
                List<TdXmlElementType> children = XmlElementHelper.getLeafNode(DqRepositoryViewService
                        .getXMLElements((TdXmlElementType) modelElement));
                return meCheckMap.getCheckedModelElements(children);
            }
            return null;
        }
    }

    private void handleColumnChecked(TdColumn column, Boolean checkedFlag) {
        ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(column);
        if (checkedFlag) {
            getTreeViewer().setChecked(columnSetOwner, true);
        }
        ModelElementCheckedMap columnCheckMap = modelElementCheckedMap.get(new ModelElementKeyImpl(columnSetOwner));
        if (columnCheckMap != null) {
            columnCheckMap.putModelElementChecked(column, checkedFlag);
        }
    }

    /**
     * handle Column(TdXmlElementType) checked.
     * 
     * @param xmlElement
     * @param checkedFlag
     */
    private void handleXmlElementChecked(TdXmlElementType xmlElement, Boolean checkedFlag) {
        ModelElement parentElement = XmlElementHelper.getParentElement(xmlElement);
        if (checkedFlag) {
            getTreeViewer().setChecked(parentElement, true);
        }
        ModelElementCheckedMap columnCheckMap = modelElementCheckedMap.get(new ModelElementKeyImpl(parentElement));
        if (columnCheckMap != null) {
            columnCheckMap.putModelElementChecked(xmlElement, checkedFlag);
        }
    }

    private void handleColumnsChecked(ColumnSet columnSet, Boolean checkedFlag) {
        ModelElementKey key = new ModelElementKeyImpl(columnSet);
        ModelElementCheckedMap columnCheckMap = modelElementCheckedMap.get(key);
        if (columnCheckMap != null) {
            columnCheckMap.clear();
            columnCheckMap.putAllChecked(EObjectHelper.getColumns(columnSet), checkedFlag);
        } else {
            columnCheckMap = new ModelElementCheckedMapImpl();
            columnCheckMap.putAllChecked(EObjectHelper.getColumns(columnSet), checkedFlag);
            modelElementCheckedMap.put(key, columnCheckMap);
        }
        getTableViewer().setAllChecked(checkedFlag);
    }

    /**
     * handle catalog/table(TdXmlSchema) checked.
     * 
     * @param xmlDocument
     * @param checkedFlag
     */
    private void handleXmlDocumentChecked(TdXmlSchema xmlDocument, Boolean checkedFlag) {
        ModelElementKey key = new ModelElementKeyImpl(xmlDocument);
        ModelElementCheckedMap columnCheckMap = modelElementCheckedMap.get(key);
        if (columnCheckMap != null) {
            columnCheckMap.clear();
            List<TdXmlElementType> xmlElements = XmlElementHelper
                    .getLeafNode(DqRepositoryViewService.getXMLElements(xmlDocument));
            columnCheckMap.putAllChecked(xmlElements.toArray(new ModelElement[xmlElements.size()]), checkedFlag);
        } else {
            columnCheckMap = new ModelElementCheckedMapImpl();
            List<TdXmlElementType> xmlElements = XmlElementHelper
                    .getLeafNode(DqRepositoryViewService.getXMLElements(xmlDocument));
            columnCheckMap.putAllChecked(xmlElements.toArray(new ModelElement[xmlElements.size()]), checkedFlag);
            modelElementCheckedMap.put(key, columnCheckMap);
        }
        getTableViewer().setAllChecked(checkedFlag);
    }

    /**
     * handle table(TdXmlElementType) checked.
     * 
     * @param xmlElement
     * @param checkedFlag
     */
    private void handleXmlElementsChecked(TdXmlElementType xmlElement, Boolean checkedFlag) {
        ModelElementKey key = new ModelElementKeyImpl(xmlElement);
        ModelElementCheckedMap columnCheckMap = modelElementCheckedMap.get(key);
        if (columnCheckMap != null) {
            columnCheckMap.clear();
            List<TdXmlElementType> xmlElements = XmlElementHelper.getLeafNode(DqRepositoryViewService.getXMLElements(xmlElement));
            columnCheckMap.putAllChecked(xmlElements.toArray(new ModelElement[xmlElements.size()]), checkedFlag);
        } else {
            columnCheckMap = new ModelElementCheckedMapImpl();
            List<TdXmlElementType> xmlElements = XmlElementHelper.getLeafNode(DqRepositoryViewService.getXMLElements(xmlElement));
            columnCheckMap.putAllChecked(xmlElements.toArray(new ModelElement[xmlElements.size()]), checkedFlag);
            modelElementCheckedMap.put(key, columnCheckMap);
        }
        getTableViewer().setAllChecked(checkedFlag);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.talend.dataprofiler.core.ui.dialog.TwoPartCheckSelectionDialog#
     * addSelectionButtonListener(org.eclipse.swt .widgets.Button, org.eclipse.swt.widgets.Button)
     */
    protected void addSelectionButtonListener(Button selectButton, Button deselectButton) {
        SelectionListener listener = new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                Object[] viewerElements = fContentProvider.getElements(getTreeViewer().getInput());
                if (fContainerMode) {
                    getTreeViewer().setCheckedElements(viewerElements);
                } else {
                    for (int i = 0; i < viewerElements.length; i++) {
                        getTreeViewer().setSubtreeChecked(viewerElements[i], true);
                    }
                }
                modelElementCheckedMap.clear();
                if (getTableViewer().getInput() != null) {
                    if (getTableViewer().getInput() instanceof ColumnSet) {
                        handleColumnsChecked((ColumnSet) getTableViewer().getInput(), true);
                    } else if (getTableViewer().getInput() instanceof TdXmlSchema) {
                        handleXmlDocumentChecked((TdXmlSchema) getTableViewer().getInput(), true);
                    } else if (getTableViewer().getInput() instanceof TdXmlElementType) {
                        handleXmlElementsChecked((TdXmlElementType) getTableViewer().getInput(), true);
                    }
                }
                updateOKStatus();
            }
        };
        selectButton.addSelectionListener(listener);

        listener = new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                getTreeViewer().setCheckedElements(new Object[0]);
                modelElementCheckedMap.clear();
                if (getTableViewer().getInput() != null) {
                    if (getTableViewer().getInput() instanceof ColumnSet) {
                        handleColumnsChecked((ColumnSet) getTableViewer().getInput(), false);
                    } else if (getTableViewer().getInput() instanceof TdXmlSchema) {
                        handleXmlDocumentChecked((TdXmlSchema) getTableViewer().getInput(), false);
                    } else if (getTableViewer().getInput() instanceof TdXmlElementType) {
                        handleXmlElementsChecked((TdXmlElementType) getTableViewer().getInput(), false);
                    }
                }
                updateOKStatus();
            }
        };
        deselectButton.addSelectionListener(listener);
    }

    public void selectionChanged(SelectionChangedEvent event) {
        Object selectedObj = ((IStructuredSelection) event.getSelection()).getFirstElement();
        if (selectedObj instanceof ColumnSet || selectedObj instanceof TdXmlSchema
                || (selectedObj instanceof TdXmlElementType && !XmlElementHelper.isLeafNode((TdXmlElementType) selectedObj))) {
            this.setOutput(selectedObj);
            ModelElement[] modelElements = getCheckedModelElements((ModelElement) selectedObj);
            if (modelElements != null) {
                this.getTableViewer().setCheckedElements(modelElements);
            }
        }
    }

    /**
     * DOC xqliu ColumnsSelectionDialog class global comment. Detailled comment
     */
    interface ModelElementKey {

        public int hashCode();

        public boolean equals(Object obj);

        public ModelElement getModelElement();

        public ModelElement getParentModelElement();
    }

    /**
     * DOC xqliu ColumnsSelectionDialog class global comment. Detailled comment
     */
    class ModelElementKeyImpl implements ModelElementKey {

        private static final int KEY_PRIME = 128;

        private ModelElement modelElement;

        private ModelElement parentModelElement;

        public ModelElementKeyImpl(ModelElement mElement) {
            modelElement = mElement;
            parentModelElement = getParentModelElement(mElement);
        }

        /**
         * DOC xqliu Comment method "getParentModelElement".
         * 
         * @param mElement
         * @return null if the mElement is the top element
         */
        private ModelElement getParentModelElement(ModelElement mElement) {
            if (mElement instanceof ColumnSet) {
                return EObjectHelper.getParent((ColumnSet) mElement);
            } else if (mElement instanceof TdXmlElementType) {
                return XmlElementHelper.getParentElement((TdXmlElementType) mElement);
            }
            return null;
        }

        public ModelElement getModelElement() {
            return modelElement;
        }

        public ModelElement getParentModelElement() {
            return parentModelElement;
        }

        public int hashCode() {
            return KEY_PRIME + (getModelElement().getName().hashCode())
                    + (getParentModelElement() == null ? 0 : new ModelElementKeyImpl(getParentModelElement()).hashCode());
        }

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
            final ModelElementKey other = (ModelElementKey) obj;

            if (getModelElement() == null) {
                if (other.getModelElement() != null) {
                    return false;
                }
            } else if (!getModelElement().equals(other.getModelElement())) {
                return false;
            }

            if (this.getParentModelElement() == null) {
                if (other.getParentModelElement() != null) {
                    return false;
                }
            } else if (!getParentModelElement().equals(other.getParentModelElement())) {
                return false;
            }
            return true;
        }
    }

    /**
     * @author xqliu
     */
    interface ModelElementCheckedMap {

        public void putModelElementChecked(ModelElement modelElement, Boolean isChecked);

        public Boolean getModelElementChecked(ModelElement modelElement);

        public void putAllChecked(ModelElement[] modelElement, Boolean isChecked);

        public ModelElement[] getCheckedModelElements(List<? extends ModelElement> modelElementList);

        public List<ModelElement> getCheckedModelElementList(ModelElement modelElement);

        public void clear();
    }

    /**
     * DOC xqliu ColumnsSelectionDialog class global comment. Detailled comment
     */
    class ModelElementCheckedMapImpl implements ModelElementCheckedMap {

        Map<String, Boolean> modelElementNameMap = new HashMap<String, Boolean>();

        public void clear() {
            modelElementNameMap.clear();
        }

        public List<ModelElement> getCheckedModelElementList(ModelElement modelElement) {
            List<ModelElement> checkedModelElements = new ArrayList<ModelElement>();
            List<? extends ModelElement> modelElementList = new ArrayList<ModelElement>();

            if (modelElement instanceof ColumnSet) {
                modelElementList = ColumnSetHelper.getColumns((ColumnSet) modelElement);
            } else if ((modelElement instanceof TdXmlElementType)) {
                modelElementList = XmlElementHelper.getLeafNode(DqRepositoryViewService
                        .getXMLElements((TdXmlElementType) modelElement));
            } else if ((modelElement instanceof TdXmlSchema)) {
                modelElementList = XmlElementHelper.getLeafNode(DqRepositoryViewService
                        .getXMLElements((TdXmlSchema) modelElement));
            }

            for (ModelElement mElement : modelElementList) {
                if (modelElementNameMap.containsKey(mElement.getName()) && modelElementNameMap.get(mElement.getName())) {
                    checkedModelElements.add(mElement);
                }
            }

            return checkedModelElements;
        }

        public ModelElement[] getCheckedModelElements(List<? extends ModelElement> modelElementList) {
            List<ModelElement> checkedModelElements = new ArrayList<ModelElement>();
            for (ModelElement modelElement : modelElementList) {
                if (modelElementNameMap.containsKey(modelElement.getName()) && modelElementNameMap.get(modelElement.getName())) {
                    checkedModelElements.add(modelElement);
                }
            }
            return checkedModelElements.toArray(new ModelElement[checkedModelElements.size()]);
        }

        public Boolean getModelElementChecked(ModelElement modelElement) {
            return modelElementNameMap.get(modelElement.getName());
        }

        public void putAllChecked(ModelElement[] modelElement, Boolean isChecked) {
            for (int i = 0; i < modelElement.length; i++) {
                modelElementNameMap.put(modelElement[i].getName(), isChecked);
            }
        }

        public void putModelElementChecked(ModelElement modelElement, Boolean isChecked) {
            modelElementNameMap.put(modelElement.getName(), isChecked);
        }

    }

    protected void computeResult() {
        setResult(getAllCheckedModelElements());
    }

    private List<ModelElement> getAllCheckedModelElements() {
        Object[] checkedNodes = this.getTreeViewer().getCheckedElements();
        List<ModelElement> meList = new ArrayList<ModelElement>();
        for (int i = 0; i < checkedNodes.length; i++) {
            if (!(checkedNodes[i] instanceof ColumnSet || checkedNodes[i] instanceof TdXmlElementType || checkedNodes[i] instanceof TdXmlSchema)) {
                continue;
            }
            ModelElementKey mek = new ModelElementKeyImpl((ModelElement) checkedNodes[i]);
            if (modelElementCheckedMap.containsKey(mek)) {
                ModelElementCheckedMap columnMap = modelElementCheckedMap.get(mek);
                meList.addAll(columnMap.getCheckedModelElementList((ModelElement) checkedNodes[i]));
            } else {
                if (checkedNodes[i] instanceof ColumnSet) {
                    meList.addAll(ColumnSetHelper.getColumns((ColumnSet) checkedNodes[i]));
                } else if ((checkedNodes[i] instanceof TdXmlElementType)) {
                    meList.addAll(XmlElementHelper.getLeafNode(DqRepositoryViewService
                            .getXMLElements((TdXmlElementType) checkedNodes[i])));
                } else if ((checkedNodes[i] instanceof TdXmlSchema)) {
                    meList.addAll(XmlElementHelper.getLeafNode(DqRepositoryViewService
                            .getXMLElements((TdXmlSchema) checkedNodes[i])));
                }
            }
        }
        return meList;
    }

    protected void okPressed() {
        super.okPressed();
        this.modelElementCheckedMap = null;
        this.currentCheckedModelElement = null;
    }

    /**
     * @author rli
     * 
     */
    class ModelElementLabelProvider extends LabelProvider {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
         */
        public Image getImage(Object element) {
            if (element instanceof TdXmlElementType) {
                return ImageLib.getImage(ImageLib.XML_ELEMENT_DOC);
            }
            return ImageLib.getImage(ImageLib.TD_COLUMN);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
         */
        public String getText(Object element) {
            if (element instanceof TdColumn) {
                TdColumn columnObj = (TdColumn) element;
                return columnObj.getName() + PluginConstant.SPACE_STRING + PluginConstant.PARENTHESIS_LEFT
                        + columnObj.getSqlDataType().getName() + PluginConstant.PARENTHESIS_RIGHT;
            } else if (element instanceof TdXmlElementType) {
                TdXmlElementType xmlElement = (TdXmlElementType) element;
                return xmlElement.getName() + PluginConstant.SPACE_STRING + PluginConstant.PARENTHESIS_LEFT
                        + xmlElement.getJavaType() + PluginConstant.PARENTHESIS_RIGHT;
            }
            return "";
        }

    }

    /**
     * @author rli
     */
    class ModelElementContentProvider implements IStructuredContentProvider {

        public Object[] getElements(Object inputElement) {
            if (!(inputElement instanceof ColumnSet || inputElement instanceof TdXmlSchema || (inputElement instanceof TdXmlElementType && !XmlElementHelper
                    .isLeafNode((TdXmlElementType) inputElement)))) {
                return new Object[0];
            }
            EObject eObj = (EObject) inputElement;
            ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(eObj);
            if (columnSet != null) {
                if (columnSet instanceof MetadataTable)
                    ConnectionUtils.retrieveColumn((MetadataTable) columnSet);
                TdColumn[] columns = EObjectHelper.getColumns(columnSet);
                if (columns.length <= 0) {
                    Package parentCatalogOrSchema = ColumnSetHelper.getParentCatalogOrSchema(columnSet);
                    if (parentCatalogOrSchema == null) {
                        return null;
                    }
                    Connection conn = DataProviderHelper.getTdDataProvider(parentCatalogOrSchema);
                    if (conn == null) {
                        return null;
                    }
                    try {
                        List<TdColumn> columnList = DqRepositoryViewService.getColumns(conn, columnSet, null, true);
                        columns = columnList.toArray(new TdColumn[columnList.size()]);
                        // store tables in catalog
                        // MOD scorreia 2009-01-29 columns are stored in the
                        // table
                        // ColumnSetHelper.addColumns(columnSet,
                        // columnList);
                    } catch (TalendException e) {
                        MessageBoxExceptionHandler.process(e);
                    }
                    ProxyRepositoryViewObject.fetchAllDBRepositoryViewObjects(Boolean.TRUE, Boolean.TRUE);
                    ProxyRepositoryViewObject.save(conn);
                }
                return sort(columns, ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
            } else {
                TdXmlSchema xmlDocument = SwitchHelpers.XMLSCHEMA_SWITCH.doSwitch(eObj);
                if (xmlDocument != null) {
                    List<ModelElement> xmlElements = DqRepositoryViewService.getXMLElements(xmlDocument);
                    List<TdXmlElementType> leafNodes = XmlElementHelper.getLeafNode(xmlElements);
                    return sort(leafNodes.toArray(), ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
                } else {
                    TdXmlElementType xmlElement = SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(eObj);
                    if (xmlElement != null) {
                        List<TdXmlElementType> xmlElements = DqRepositoryViewService.getXMLElements(xmlElement);
                        List<TdXmlElementType> leafNodes = XmlElementHelper.getLeafNode(xmlElements);
                        return sort(leafNodes.toArray(), ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
                    }
                }
            }
            return null;
        }

        /**
         * Sort the parameter objects, and return the sorted array.
         * 
         * @param objects
         * @param comparatorId the comparator id has been defined in the {@link ComparatorsFactory};
         * @return
         */
        @SuppressWarnings("unchecked")
        protected Object[] sort(Object[] objects, int comparatorId) {
            if (objects == null || objects.length <= 1) {
                return objects;
            }
            Arrays.sort(objects, ComparatorsFactory.buildComparator(comparatorId));
            return objects;
        }

        public void dispose() {
        }

        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }

    /**
     * DOC zqin ColumnsSelectionDialog class global comment. Detailled comment
     */
    class DBTreeViewContentProvider extends DQRepositoryViewContentProvider {

        /**
         * @param adapterFactory
         */
        public DBTreeViewContentProvider() {
            super();
        }

        @SuppressWarnings("unchecked")
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof IContainer) {
                IContainer container = (IContainer) parentElement;
                IResource[] members = null;
                try {
                    members = container.members();
                } catch (CoreException e) {
                    log.error("Can't get the children of container:" + container.getLocation());
                }

                if (ResourceManager.getConnectionFolder().equals(container)) {
                    return ProxyRepositoryViewObject.fetchAllDBRepositoryViewObjects(true, true).toArray();
                } else if (ResourceManager.getMDMConnectionFolder().equals(container)) {
                    return ProxyRepositoryViewObject.fetchAllMDMRepositoryViewObjects(true, true).toArray();
                }

                // ComparatorsFactory.sort(members, ComparatorsFactory.FILEMODEL_COMPARATOR_ID);

                return members;
            } else if (parentElement instanceof NamedColumnSet) {
                return null;
            } else if (parentElement instanceof NamedColumnSetFolderNode) {
                NamedColumnSetFolderNode folerNode = (NamedColumnSetFolderNode) parentElement;
                folerNode.loadChildren();
                Object[] children = folerNode.getChildren();
                if (children != null && children.length > 0) {
                    if (!(children[0] instanceof ColumnSet)) {
                        return children;
                    }
                    for (int i = 0; i < children.length; i++) {
                        ColumnSet columnSet = (ColumnSet) children[i];
                        ModelElementKey key = new ModelElementKeyImpl(columnSet);
                        if (modelElementCheckedMap.containsKey(key)) {
                            currentCheckedModelElement.add(columnSet);
                        }
                    }
                }
                return ComparatorsFactory.sort(children, ComparatorsFactory.MODELELEMENT_COMPARATOR_ID);
            } else if (parentElement instanceof TdXmlSchema || parentElement instanceof TdXmlElementType) {
                boolean isXmlDocument = parentElement instanceof TdXmlSchema;
                List<? extends ModelElement> modelElements = isXmlDocument ? DqRepositoryViewService
                        .getXMLElements((TdXmlSchema) parentElement) : DqRepositoryViewService
                        .getXMLElements((TdXmlElementType) parentElement);
                Object[] children = XmlElementHelper.clearLeafNode(modelElements).toArray();
                if (children != null && children.length > 0) {
                    if (!(children[0] instanceof TdXmlElementType)) {
                        return children;
                    }
                    for (int i = 0; i < children.length; i++) {
                        TdXmlElementType xmlElement = (TdXmlElementType) children[i];
                        ModelElementKey key = new ModelElementKeyImpl(xmlElement);
                        if (modelElementCheckedMap.containsKey(key)) {
                            currentCheckedModelElement.add(xmlElement);
                        }
                    }
                }
                return children.length == 0 ? null : children;
            }
            return super.getChildren(parentElement);
        }

        public Object getParent(Object element) {
            if (element instanceof EObject) {
                // MOD xqliu 2010-02-02 bug 11198
                if (element instanceof TdXmlSchema) {
                    Connection conn = DataProviderHelper.getTdDataProvider((TdXmlSchema) element);
                    return ProxyRepositoryViewObject.getRepositoryViewObject(conn);
                } else if (element instanceof TdXmlElementType) {
                    return XmlElementHelper.getParentElement((TdXmlElementType) element);
                }
                // ~
                EObject eObj = (EObject) element;
                ColumnSet columnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(eObj);
                if (columnSet != null) {
                    IFolderNode folderNode = FolderNodeHelper.getFolderNode(EObjectHelper.getParent((ColumnSet) element),
                            columnSet);
                    return folderNode;
                }

                Package packageValue = SwitchHelpers.PACKAGE_SWITCH.doSwitch(eObj);
                Catalog parentCatalog = CatalogHelper.getParentCatalog(packageValue);
                if (parentCatalog != null) {
                    return parentCatalog;
                }

                if (packageValue != null) {
                    Connection tdDataProvider = DataProviderHelper.getTdDataProvider(packageValue);
                    return ProxyRepositoryViewObject.getRepositoryViewObject(tdDataProvider);
                }
            } else if (element instanceof IFolderNode) {
                return ((IFolderNode) element).getParent();
            } else if (element instanceof IResource) {
                return ((IResource) element).getParent();
            } else if (element instanceof IRepositoryViewObject) {
                // Add this if by qiongli bug 14891,2010-9-20
                IRepositoryViewObject conn = (IRepositoryViewObject) element;
                Item connItem = conn.getProperty().getItem();
                if (connItem instanceof MDMConnectionItem) {
                    return ResourceManager.getMDMConnectionFolder();
                } else if (connItem instanceof ConnectionItem) {
                    return ResourceManager.getConnectionFolder();
                }
            }
            return super.getParent(element);
        }

        public boolean hasChildren(Object element) {
            // MOD xqliu 2010-02-02 bug 11198
            if (element instanceof TdView || element instanceof TdTable) {
                return false;
            }
            return superHasChildren(element); // ???
            // ~
        }

        /**
         * If element if TdXmlElementType, the super method hasChildren() return wrong result, so add use this method to
         * get the right result. xqliu 2010-02-04
         * 
         * @param element
         * @return
         */
        private boolean superHasChildren(Object element) {
            boolean hasChildren = super.hasChildren(element);
            if (element instanceof EObject) {
                EObject eobject = (EObject) element;
                if (SwitchHelpers.XMLELEMENTTYPE_SWITCH.doSwitch(eobject) != null) {
                    hasChildren = !hasChildren;
                }
            }

            return hasChildren;
        }

    }
}
