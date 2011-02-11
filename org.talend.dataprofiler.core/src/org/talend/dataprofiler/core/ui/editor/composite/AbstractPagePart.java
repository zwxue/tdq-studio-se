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
package org.talend.dataprofiler.core.ui.editor.composite;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.talend.core.model.metadata.MetadataColumnRepositoryObject;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.ConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.ModelElementIndicatorHelper;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.model.TableIndicator;
import org.talend.dataprofiler.core.ui.action.actions.ChangeConnectionAction;
import org.talend.dataprofiler.core.ui.editor.analysis.AbstractAnalysisMetadataPage;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dq.helper.EObjectHelper;
import org.talend.dq.helper.PropertyHelper;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.utils.sugars.ReturnCode;
import orgomg.cwm.foundation.softwaredeployment.DataManager;

import common.Logger;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractPagePart {

    private static Logger log = Logger.getLogger(AbstractPagePart.class);

    private boolean isDirty;

    protected PropertyChangeSupport propertyChangeSupport;

    private SelectionListener selectionListener;

    public AbstractPagePart() {
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public void setDirty(boolean dirty) {
        if (isDirty != dirty) {
            this.isDirty = dirty;
            propertyChangeSupport.firePropertyChange(PluginConstant.ISDIRTY_PROPERTY, null, Boolean.valueOf(dirty));
        }
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    /**
     * ADD mzhao 2009-05-05 bug:6587.
     */
    protected void updateBindConnection(AbstractAnalysisMetadataPage masterPage, ModelElementIndicator[] indicators, Tree tree) {
        if (indicators != null && indicators.length != 0) {
            // MOD mzhao 2010-07-24, avoid a NPE, feature 13221
            DataManager connection = masterPage.getAnalysis().getContext()
                    .getConnection();
            Connection tdProvider = null;
            if (connection != null) {
                tdProvider = SwitchHelpers.CONNECTION_SWITCH.doSwitch(connection);
            }

            if (tdProvider == null) {
                tdProvider = ModelElementIndicatorHelper.getTdDataProvider(indicators[0]);
            }
            setConnectionState(masterPage, tdProvider);
        }
    }

    /**
     * 
     * ADD mzhao 2009-05-05 bug:6587.
     */
    protected void updateBindConnection(AbstractAnalysisMetadataPage masterPage, TableIndicator[] indicators, Tree tree) {
        // MOD mzhao 2009-06-09 feature 5887
        // if (!isAnalyzedColumnsEmpty(tree)) {
        // List<TdDataProvider> providerList = new
        // ArrayList<TdDataProvider>();
        Connection tdProvider = null;
        if (indicators != null && indicators.length != 0) {
            tdProvider = ConnectionHelper.getDataProvider(SwitchHelpers.COLUMN_SET_SWITCH
                    .doSwitch(indicators[0].getColumnSet()));

            setConnectionState(masterPage, tdProvider);
        }
        // }
    }

    /**
     * 
     * ADD mzhao 2009-05-05 bug:6587.
     */
    protected void updateBindConnection(AbstractAnalysisMetadataPage masterPage, Tree tree) {
        if (!isAnalyzedColumnsEmpty(tree)) {
            DataManager dataManager = masterPage.getAnalysis().getContext().getConnection();
            if (dataManager == null) {
                if (tree.getData() instanceof AnalysisColumnNominalIntervalTreeViewer) {
                    AnalysisColumnNominalIntervalTreeViewer treeViewer = (AnalysisColumnNominalIntervalTreeViewer) tree.getData();
                    RepositoryNode repositoryNode = treeViewer.getColumnSetMultiValueList().get(0);
                    dataManager = ((ConnectionItem) repositoryNode.getObject().getProperty().getItem()).getConnection();
                    // dataManager = ConnectionHelper.getTdDataProvider(repositoryNode);
                } else if (tree.getData() instanceof AnalysisColumnSetTreeViewer) {
                    AnalysisColumnSetTreeViewer treeViewer = (AnalysisColumnSetTreeViewer) tree.getData();
                    IRepositoryNode reposNode = treeViewer.getColumnSetMultiValueList().get(0);
                    dataManager = ((ConnectionItem) reposNode.getObject().getProperty().getItem()).getConnection();
                }
            }
            setConnectionState(masterPage, dataManager);
        }
    }

    /**
     * 
     * ADD mzhao 2009-05-05 bug:6587.
     */
    protected void updateBindConnection(AbstractAnalysisMetadataPage masterPage, List<TableViewer> tableViewerPosStack) {
        boolean isEmpty1 = tableViewerPosStack.get(0) == null || tableViewerPosStack.get(0).getInput() == null
                || ((List<TdColumn>) tableViewerPosStack.get(0).getInput()).size() == 0;
        boolean isEmpty2 = tableViewerPosStack.get(1) == null || tableViewerPosStack.get(1).getInput() == null
                || ((List<TdColumn>) tableViewerPosStack.get(1).getInput()).size() == 0;
        if (isEmpty1 && isEmpty2) {
            return;
        } else {

            TableViewer columnsElementViewer = null;
            if (!isEmpty1) {
                columnsElementViewer = tableViewerPosStack.get(0);
            } else {
                columnsElementViewer = tableViewerPosStack.get(1);
            }
            Connection tdProvider = null;
            Object input = columnsElementViewer.getInput();

            List<DBColumnRepNode> columnSet = (List<DBColumnRepNode>) input;
            if (columnSet != null && columnSet.size() != 0) {
                TdColumn column = (TdColumn) ((MetadataColumnRepositoryObject) columnSet.get(0).getObject()).getTdColumn();

                if (column != null && column.eIsProxy()) {
                    column = (TdColumn) EObjectHelper.resolveObject(column);
                }
                tdProvider = ConnectionHelper.getTdDataProvider(column);
                setConnectionState(masterPage, tdProvider);
            }
        }
    }

    private void setConnectionState(final AbstractAnalysisMetadataPage masterPage, final DataManager dataManager) {
        if (dataManager != null) {

            Object value = masterPage.getConnCombo().getData(dataManager.getName());
            // MOD qiongli 2011-1-7 delimitedFile connection dosen't use 'dataManager.getName()'.
            if (SwitchHelpers.DELIMITEDFILECONNECTION_SWITCH.doSwitch(dataManager) != null) {
                Property prop = PropertyHelper.getProperty((Connection) dataManager);
                value = masterPage.getConnCombo().getData(prop.getLabel());
            }
            Integer index = 0;
            if (value != null && value instanceof Integer) {
                index = (Integer) value;
            }
            masterPage.getConnCombo().select(index);
            // MOD mzhao 2009-06-09 feature 5887
            if (selectionListener == null) {
                selectionListener = new SelectionListener() {

                    private int prevSelect = masterPage.getConnCombo().getSelectionIndex();

                    private Connection dataProvider = (Connection) dataManager;

                    public void widgetDefaultSelected(SelectionEvent e) {
                    }

                    public void widgetSelected(SelectionEvent e) {
                        dataProvider = callChangeConnectionAction(masterPage, prevSelect, dataProvider);
                        prevSelect = masterPage.getConnCombo().getSelectionIndex();
                    }

                };
                masterPage.getConnCombo().addSelectionListener(selectionListener);
            }
        } else {
            masterPage.getConnCombo().setText("unknown connection");
        }

        // MOD mzhao 2009-06-09 feature 5887
        // masterPage.getConnCombo().setEnabled(false);
    }

    // MOD mzhao 2009-06-09 feature 5887
    private Connection callChangeConnectionAction(AbstractAnalysisMetadataPage masterPage, final int oldSelect,
            Connection tdProvider) {
        ChangeConnectionAction changeConnAction = new ChangeConnectionAction(masterPage, tdProvider);
        changeConnAction.run();
        ReturnCode ret = changeConnAction.getStatus();
        if (ret.isOk()) {
            IRunnableWithProgress op = new IRunnableWithProgress() {

                public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                    Display.getDefault().asyncExec(new Runnable() {

                        public void run() {
                            updateModelViewer();
                        }

                    });

                }

            };

            try {
                ProgressUI.popProgressDialog(op);
            } catch (InvocationTargetException e) {
                log.error(e, e);
            } catch (InterruptedException e) {
                log.error(e, e);
            }

            // The newest dataprovider now would be the old one for next
            // time connection changes.
            Object connObject = masterPage.getConnCombo().getData(masterPage.getConnCombo().getSelectionIndex() + ""); //$NON-NLS-1$
            if (connObject instanceof RepositoryNode) {
                tdProvider = ((ConnectionItem) ((RepositoryNode) connObject).getObject().getProperty().getItem()).getConnection();
            } else if (connObject instanceof Connection) {
                tdProvider = (Connection) connObject;
            }
            // MOD mzhao bug 12766, 2010-04-22 save the editor automatically.
            masterPage.doSave(new NullProgressMonitor());
            //
        } else {
            cancelSelection(masterPage, oldSelect);
        }
        return tdProvider;
    }

    private void cancelSelection(AbstractAnalysisMetadataPage masterPage, final int oldSelect) {
        masterPage.getConnCombo().removeSelectionListener(selectionListener);
        masterPage.getConnCombo().select(oldSelect);
        masterPage.getConnCombo().addSelectionListener(selectionListener);
    }

    public abstract void updateModelViewer();

    /**
     * 
     * ADD mzhao 2009-05-05 bug:6587
     */
    private boolean isAnalyzedColumnsEmpty(Tree tree) {
        boolean isEmpty = false;
        if (tree == null || tree.getItemCount() == 0) {
            isEmpty = true;
        }
        return isEmpty;
    }
}
