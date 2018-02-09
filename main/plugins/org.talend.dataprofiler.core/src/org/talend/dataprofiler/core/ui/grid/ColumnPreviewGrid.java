// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.grid;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.grid.GridCellRenderer;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.nebula.widgets.grid.TalendGridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.talend.core.model.metadata.builder.connection.DelimitedFileConnection;
import org.talend.core.model.metadata.builder.connection.Escape;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.database.JavaSqlFactory;
import org.talend.cwm.db.connection.ConnectionUtils;
import org.talend.cwm.db.connection.talendResultSet.DatabaseResultSet;
import org.talend.cwm.db.connection.talendResultSet.FileCSVResultSet;
import org.talend.cwm.db.connection.talendResultSet.FileDelimitedResultSet;
import org.talend.cwm.db.connection.talendResultSet.ITalendResultSet;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.dataprofiler.core.helper.ModelElementIndicatorHelper;
import org.talend.dataprofiler.core.helper.ResultSetHelper;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.model.DelimitedFileIndicator;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dataprofiler.core.ui.grid.utils.Observerable;
import org.talend.dataprofiler.core.ui.grid.utils.TDQObserver;
import org.talend.dataprofiler.core.ui.grid.utils.events.ObserverEvent;
import org.talend.dataprofiler.core.ui.grid.utils.events.ObserverEventEnum;
import org.talend.dataquality.PluginConstant;
import org.talend.dq.helper.AnalysisExecutorHelper;
import org.talend.dq.helper.FileUtils;
import org.talend.fileprocess.FileInputDelimited;
import orgomg.cwm.objectmodel.core.ModelElement;

import com.talend.csv.CSVReader;

/**
 * The table which is used be preivew data
 * 
 */
public class ColumnPreviewGrid extends AbstractIndicatorSelectGrid implements TDQObserver<ObserverEvent>,
        Observerable<ObserverEvent> {

    private final String COLUMN_RESULT_KEY = "columnResult"; //$NON-NLS-1$

    private List<TDQObserver<ObserverEvent>> observers = null;

    private Logger log = Logger.getLogger(ColumnPreviewGrid.class);

    /**
     * ColumnPreviewGrid constructor comment.
     * 
     * @param dialog
     * @param parent
     * @param style
     * @param modelElementIndicators
     */
    public ColumnPreviewGrid(IndicatorSelectDialog dialog, Composite parent, int style,
            ModelElementIndicator[] modelElementIndicators, int limit) {
        super(dialog, parent, style, modelElementIndicators, limit);
        initializeGrid();
        addVscrollBarListener();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.grid.AbstractIndicatorSelectGrid#createTableContent()
     */
    @Override
    protected void createTableContent() {
        try {
            TalendGridItem PreviewItem = new TalendGridItem(this, SWT.NONE);
            for (int i = 0; i < this.getColumnCount(); i++) {
                PreviewItem.setCheckable(i, false);
            }
            PreviewItem.setText(DefaultMessagesImpl.getString("ColumnPreviewGrid.PreviewItemLabel")); //$NON-NLS-1$
            processNodePrivew(null, PreviewItem);
            PreviewItem.setExpanded(true);
        } catch (SQLException e) {

        }
    }

    /**
     * DOC talend Comment method "processNodePrivew".
     * 
     * @param object
     * @param previewItem
     * @throws SQLException
     */
    private void processNodePrivew(Object object, GridItem parentItem) throws SQLException {
        if (!checkSameTable()) {
            return;
        }
        try {
            for (int j = 0; j < limitNumber; j++) {
                TalendGridItem currentItem = new TalendGridItem(parentItem, SWT.NONE);
                currentItem.setCheckable(0, false);
                currentItem.setCheckable(1, false);
                for (int i = 2; i < this.getColumnCount(); i++) {
                    GridColumn column = getColumn(i);
                    currentItem.setCheckable(i, false);
                    String columnValue = getColumnValue(column, currentItem);
                    if (columnValue == null) {
                        this.remove(this.indexOf(currentItem));
                        return;
                    }
                    int minWith = columnValue.length() * 7 > 100 ? 100 : columnValue.length() * 7;
                    column.setWidth(COLUMN_WIDTH < minWith ? minWith : COLUMN_WIDTH);
                    currentItem.setText(i, columnValue);
                }
            }
        } finally {
            ITalendResultSet rs = (ITalendResultSet) this.getData(COLUMN_RESULT_KEY);
            ModelElementIndicator modelElementIndicator = (ModelElementIndicator) getColumn(2).getData();
            closeConnection(rs, modelElementIndicator);
        }
    }

    /**
     * DOC talend Comment method "checkSameTable".
     * 
     * @return
     */
    private boolean checkSameTable() {
        List<MetadataColumn> columnsList = ModelElementIndicatorHelper.getColumns(_modelElementIndicators);
        return ColumnHelper.checkSameTable(columnsList.toArray(new ModelElement[columnsList.size()]));
    }

    /**
     * DOC talend Comment method "getColumnValue".
     * 
     * @param column
     * @param currentItem
     * @return
     * @throws SQLException
     */
    private String getColumnValue(GridColumn column, GridItem currentItem) throws SQLException {
        ITalendResultSet rs = (ITalendResultSet) this.getData(COLUMN_RESULT_KEY);
        ModelElementIndicator modelElementIndicator = (ModelElementIndicator) column.getData();

        if (rs == null) {
            rs = getResultSet(modelElementIndicator);
            this.setData(COLUMN_RESULT_KEY, rs);
        }
        // row index
        int indexOfRow = this.indexOf(currentItem);
        if (rs.absolute(indexOfRow)) {
            String columnName = modelElementIndicator.getElementName();
            Object columnValue = rs.getObject(columnName);
            return columnValue == null ? PluginConstant.NULL_STRING : columnValue.toString();
        }

        return null;
    }

    /**
     * DOC msjian Comment method "closeConnection".
     * 
     * @param rs
     * @param modelElementIndicator
     * @throws SQLException
     */
    private void closeConnection(ITalendResultSet rs, ModelElementIndicator modelElementIndicator) throws SQLException {
        if (rs != null) {
            if (rs instanceof DatabaseResultSet) {
                ColumnIndicator columnIndicator = ModelElementIndicatorHelper.switchColumnIndicator(modelElementIndicator);
                org.talend.core.model.metadata.builder.connection.Connection tdDataProvider = ModelElementIndicatorHelper
                        .getTdDataProvider(columnIndicator);
                if (ConnectionUtils.isSqlite(tdDataProvider)) {
                    Connection connection = ((DatabaseResultSet) rs).getRs().getStatement().getConnection();
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                }
            }
        }
    }

    /**
     * DOC talend Comment method "getResultSet".
     * 
     * @param tdDataProvider
     * @param tdColumn
     * @return
     * @throws SQLException
     */
    private ITalendResultSet getResultSet(ModelElementIndicator modelElementIndicator) throws SQLException {
        ColumnIndicator columnIndicator = ModelElementIndicatorHelper.switchColumnIndicator(modelElementIndicator);
        if (columnIndicator != null) {
            return getResultSet(columnIndicator);
        }
        DelimitedFileIndicator delimitedFileIndicator = ModelElementIndicatorHelper
                .switchDelimitedFileIndicator(modelElementIndicator);
        if (delimitedFileIndicator != null) {
            return getResultSet(delimitedFileIndicator);
        }
        return null;
    }

    /**
     * DOC talend Comment method "getResultSet".
     * 
     * @param tdDataProvider
     * @param tdColumn
     * @return
     * @throws SQLException
     */
    private ITalendResultSet getResultSet(ColumnIndicator columnIndicator) throws SQLException {
        ResultSet rs = ResultSetHelper.getResultSet(columnIndicator, _dialog.getWhereExpression(), limitNumber);
        return new DatabaseResultSet(rs);
    }

    /**
     * DOC talend Comment method "getResultSet".
     * 
     * @param tdDataProvider
     * @param tdColumn
     * @returnDelimitedFileConnectionImpl
     * @throws SQLException
     */
    private ITalendResultSet getResultSet(DelimitedFileIndicator modelElementIndicator) throws SQLException {
        DelimitedFileConnection delimitedFileconnection = (DelimitedFileConnection) ModelElementIndicatorHelper
                .getTdDataProvider(modelElementIndicator);

        // need to find the analysed element position , and only get these analysed column's values.
        List<String> columnLabels = ModelElementIndicatorHelper.getColumnNameList(modelElementIndicator);
        if (Escape.CSV.equals(delimitedFileconnection.getEscapeType())) {
            CSVReader csvReader = null;
            try {
                int headValue = JavaSqlFactory.getHeadValue(delimitedFileconnection);
                csvReader = FileUtils.createCsvReader(delimitedFileconnection);
                FileUtils.initializeCsvReader(delimitedFileconnection, csvReader);
                for (int i = 0; i < headValue; i++) {
                    csvReader.readHeaders();
                }
                return new FileCSVResultSet(csvReader, columnLabels);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                if (csvReader != null) {
                    try {
                        csvReader.close();
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        } else {
            FileInputDelimited fileInputDelimited = null;
            try {
                fileInputDelimited = AnalysisExecutorHelper.createFileInputDelimited(delimitedFileconnection);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            return new FileDelimitedResultSet(fileInputDelimited, columnLabels);
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.grid.utils.TalendObserver#Update(org.talend.dataprofiler.core.ui.grid.utils.
     * Observerable)
     */
    public void update(ObserverEvent observer) {
        switch (observer.getEventType()) {
        case ItemHeaderWidth:
            Object width = observer.getData(ObserverEvent.ITEM_HEADER_WIDTH);
            if (width == null) {
                return;
            }
            this.setItemHeaderWidth(Integer.parseInt(width.toString()));
            break;
        case HSrcollMove:
            Object selection = observer.getData(ObserverEvent.HORIZONTAL_SCROLLBAR_MOVE);
            if (selection == null) {
                return;
            }
            this.getHorizontalBar().setSelection(Integer.parseInt(selection.toString()));
            redraw(getClientArea().x, getClientArea().y, getClientArea().width, getClientArea().height, false);
            break;
        case VSrcollVisible:
            Object show = observer.getData(ObserverEvent.VERTICAL_SRCOLL_VISABLE);

            if (show == null || getVerticalBar() == null) {
                return;
            }
            if (!getVerticalBar().isVisible() && Boolean.parseBoolean(show.toString())) {
                // make current table bounds change to small
                GridData previewGridData = (GridData) this.getLayoutData();
                previewGridData.widthHint = this.getBounds().width - 50 - getVerticalBar().getSize().x;
                previewGridData.minimumWidth = this.getBounds().width - 50 - getVerticalBar().getSize().x;
                previewGridData.horizontalAlignment = SWT.BEGINNING;
                this.getParent().layout();
            }
            if (!getVerticalBar().isVisible() && !Boolean.parseBoolean(show.toString())) {
                // make current table bounds change to big
                GridData previewGridData = (GridData) this.getLayoutData();
                if (previewGridData.horizontalAlignment == SWT.FILL) {
                    return;
                }
                previewGridData.minimumWidth = 650;
                previewGridData.horizontalAlignment = SWT.FILL;
                notifyVerticalBarShown(false);
                this.getParent().layout();
            }

            break;
        case ColumnHighlight:
            Object data = observer.getData(ObserverEvent.COLUMN_HIGH_LIGHT);
            handleColumnHighlight(((Integer) data));
            break;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.grid.AbstractIndicatorSelectGrid#getCellRenderer()
     */
    @Override
    protected GridCellRenderer getCellRenderer() {
        return new AbstractTdCellRenderer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.grid.AbstractIndicatorSelectGrid#redrawTable()
     */
    @Override
    protected void redrawTable() {
        GridData previewGridData = (GridData) this.getLayoutData();
        if (previewGridData.minimumHeight == this.getHeaderHeight() + this.getItemHeight() * 2) {
            previewGridData.minimumHeight = this.getHeaderHeight() + this.getItemHeight() * 10;
            previewGridData.heightHint = this.getHeaderHeight() + this.getItemHeight() * 10;
        } else {
            previewGridData.minimumHeight = this.getHeaderHeight() + this.getItemHeight() * 2;
            previewGridData.heightHint = this.getHeaderHeight() + this.getItemHeight() * 2;
        }
        this.getParent().layout();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.grid.utils.Observerable#addObserver(org.talend.dataprofiler.core.ui.grid.utils
     * .TDQObserver)
     */
    public boolean addObserver(TDQObserver<ObserverEvent> observer) {
        initObserverable();
        return observers.add(observer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.grid.utils.Observerable#removeObserver(org.talend.dataprofiler.core.ui.grid.utils
     * .TDQObserver)
     */
    public boolean removeObserver(TDQObserver<ObserverEvent> observer) {
        if (observers == null) {
            return false;
        }
        return observers.remove(observer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.grid.utils.Observerable#clearObserver()
     */
    public void clearObserver() {
        if (observers == null) {
            return;
        }
        observers.clear();

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.grid.utils.Observerable#notifyObservers()
     */
    public void notifyObservers() {
        // no need to implement

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.grid.utils.Observerable#initObserverable()
     */
    public void initObserverable() {
        if (observers == null) {
            observers = new ArrayList<TDQObserver<ObserverEvent>>();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataprofiler.core.ui.grid.AbstractIndicatorSelectGrid#notifyObservers(org.eclipse.nebula.widgets.grid
     * .GridColumn)
     */
    @Override
    protected void notifyObservers(Event event) {
        if (observers == null) {
            return;
        }
        GridColumn sourceColumn = null;
        if (GridColumn.class.isInstance(event.item)) {
            sourceColumn = (GridColumn) event.item;
        } else {
            return;
        }

        for (TDQObserver<ObserverEvent> observer : observers) {
            ObserverEvent observerEvent = null;
            if (SWT.Resize == event.type) {
                observerEvent = new ObserverEvent(ObserverEventEnum.ColumnResize);
                observerEvent.putData(ObserverEvent.COLUMN_HEADER_RESIZE, sourceColumn);
            } else if (SWT.Move == event.type) {
                observerEvent = new ObserverEvent(ObserverEventEnum.MoveColumn);
                observerEvent.putData(ObserverEvent.COLUMN_HEADER_MOVE, this.getColumnOrder());
            } else {
                continue;
            }
            observer.update(observerEvent);
        }

    }

    /**
     * DOC talend Comment method "notifyVerticalBarVisible".
     * 
     * @param observer
     */
    @Override
    protected void notifyVerticalBarShown(boolean show) {
        if (observers == null) {
            return;
        }
        for (TDQObserver<ObserverEvent> observer : observers) {
            ObserverEvent observerEvent = new ObserverEvent(ObserverEventEnum.VSrcollVisible);
            observerEvent.putData(ObserverEvent.VERTICAL_SRCOLL_VISABLE, show);
            observer.update(observerEvent);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.grid.AbstractIndicatorSelectGrid#notifyHscrollSelectionChange()
     */
    @Override
    protected void notifyHscrollSelectionChange() {
        if (observers == null) {
            return;
        }
        for (TDQObserver<ObserverEvent> observer : observers) {
            ObserverEvent observerEvent = new ObserverEvent(ObserverEventEnum.HSrcollMove);
            observerEvent.putData(ObserverEvent.HORIZONTAL_SCROLLBAR_MOVE, this.getHorizontalBar().getSelection());
            observer.update(observerEvent);
        }
    }

    @Override
    protected void notifyhandleColumnHighlight(MouseEvent e) {
        if (observers == null) {
            return;
        }
        for (TDQObserver<ObserverEvent> observer : observers) {
            ObserverEvent observerEvent = new ObserverEvent(ObserverEventEnum.ColumnHighlight);
            GridColumn currentColumn = this.getColumn(new Point(e.x, e.y));
            if (currentColumn != null) {
                observerEvent.putData(ObserverEvent.COLUMN_HIGH_LIGHT, this.indexOf(currentColumn));
                observer.update(observerEvent);
            }
        }
    }

    /**
     * Check whether the where clause is valid
     * 
     * @return true if the where clause is valid else return false
     */
    public boolean checkWhereClause() {
        // no column be selected case
        if (this.getColumnCount() <= 2) {
            return true;
        }

        GridColumn column = getColumn(2);
        ModelElementIndicator modelElementIndicator = (ModelElementIndicator) column.getData();
        ITalendResultSet rs = null;
        try {
            rs = getResultSet(modelElementIndicator);
        } catch (SQLException e) {
            MessageDialog.openWarning(null, DefaultMessagesImpl.getString("ColumnPreviewGrid.InValidWhereClause"), //$NON-NLS-1$
                    e.getMessage());
            return false;
        } finally {
            try {
                closeConnection(rs, modelElementIndicator);
            } catch (SQLException e) {
                log.error(e, e);
                return false;
            }
        }
        return true;
    }

    public int[] getColumnsWidth() {
        int[] result = new int[this.getColumnCount()];
        for (int index = 0; index < this.getColumnCount(); index++) {
            result[index] = this.getColumn(index).getWidth();
        }
        return result;
    }

    @Override
    protected int getPreferWidth(int index) {
        return COLUMN_WIDTH;
    }

}
