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
package org.talend.dataquality.record.linkage.ui.composite.table;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.event.ColumnHeaderSelectionEvent;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.painter.ColumnGroupHeaderTextPainter;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultColumnHeaderStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.painter.SortableHeaderTextPainter;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.talend.dataprofiler.common.ui.editor.preview.chart.utils.MatchRuleColorRegistry;
import org.talend.dataquality.record.linkage.ui.composite.ListObjectDataProvider;
import org.talend.dataquality.record.linkage.utils.MatchAnalysisConstant;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * when the property(displayed column) name of the table is changed, need to call initTableProperty first. if only the
 * data of the table changed, need only to call createTableControl. TODO: refresh the table with new data(column not
 * changed), and no need to create the table for every refresh; or give all the data to the table at one time?
 */
public class DataSampleTable {

    private BodyLayerStack bodyLayer;

    @SuppressWarnings("rawtypes")
    private ListObjectDataProvider bodyDataProvider;

    private String[] propertyNames;

    private Map<String, String> propertyToLabels;

    private NatTable natTable;

    public static final String MATCH_EKY = "MATCH"; //$NON-NLS-1$

    public static final String BLOCK_EKY = "BLOCK"; //$NON-NLS-1$

    public static final Color COLOR_BLACK = Display.getDefault().getSystemColor(SWT.COLOR_BLACK);

    public static final Color COLOR_RED = Display.getDefault().getSystemColor(SWT.COLOR_RED);

    public static final Color COLOR_GREEN = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);

    protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);

    private String currentSelectedColumn = null;

    private int groupSizeIndex;

    private final static Color[] COLOR_LIST = MatchRuleColorRegistry.getColorsForSwt();

    public DataSampleTable() {
    }

    /**
     * create the nattable every time when the user select some columns
     * 
     * @param columns
     * @param listOfData
     */
    public Control createTable(Composite parentContainer, ModelElement[] columns, List<Object[]> listOfData) {
        String[] columnsName = createColumnLabel(columns);

        Map<String, String> columnToLabelMap = new HashMap<String, String>();
        for (String label : columnsName) {
            columnToLabelMap.put(label, label);
        }
        initTableProperty(columnsName, columnToLabelMap);

        // initial the data if it is empty
        if (listOfData == null) {
            listOfData = new ArrayList<Object[]>();
        }
        if (listOfData.size() < 1) {
            listOfData.add(getEmptyRow());
        }

        return createTableControl(parentContainer, listOfData);

    }

    private void addCustomSelectionBehaviour() {
        natTable.addLayerListener(new ILayerListener() {

            public void handleLayerEvent(ILayerEvent event) {
                if ((event instanceof ColumnHeaderSelectionEvent)) {
                    ColumnHeaderSelectionEvent columnEvent = (ColumnHeaderSelectionEvent) event;
                    Collection<Range> ranges = columnEvent.getColumnPositionRanges();
                    if (ranges.size() > 0) {
                        Range range = ranges.iterator().next();
                        handleColumnSelectionChange(range.start);
                    }
                }
            }
        });
    }

    private void handleColumnSelectionChange(int index) {
        currentSelectedColumn = this.getUserColumnNameByPosition(index);
        listeners.firePropertyChange(MatchAnalysisConstant.DATA_SAMPLE_TABLE_COLUMN_SELECTION, true, false);

    }

    public String getCurrentSelectedColumn() {
        return this.currentSelectedColumn;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.addPropertyChangeListener(listener);
    }

    /**
     * when the data is empty, the column can not response the click event, so we need to add an empty row to it.
     * 
     * @return
     */
    private Object[] getEmptyRow() {
        Object[] emptyRow = new Object[propertyNames.length];

        for (int i = 0; i < propertyNames.length; i++) {
            emptyRow[i] = StringUtils.EMPTY;
        }
        return emptyRow;
    }

    private String[] createColumnLabel(ModelElement[] columns) {
        int columnCount = MatchAnalysisConstant.DEFAULT_COLUMN_COUNT;
        if (columns != null) {
            columnCount = columns.length + columnCount;
        }
        String[] columnsName = new String[columnCount];

        int i = 0;
        if (columns != null) {
            for (ModelElement column : columns) {
                columnsName[i++] = column.getName();
            }
        }
        columnsName[i++] = MatchAnalysisConstant.BLOCK_KEY;
        columnsName[i++] = MatchAnalysisConstant.GID;
        // record the index of the GRP_SIZE
        groupSizeIndex = i;
        columnsName[i++] = MatchAnalysisConstant.GRP_SIZE;
        columnsName[i++] = MatchAnalysisConstant.MASTER;
        columnsName[i++] = MatchAnalysisConstant.SCORE;
        columnsName[i++] = MatchAnalysisConstant.GRP_QUALITY;
        columnsName[i++] = MatchAnalysisConstant.ATTRIBUTE_SCORES;
        return columnsName;
    }

    /**
     * before create the table control, need to init the property name and name to label map
     * 
     * @param proNames
     * @param proToLabels : <property name, display label>
     */
    public void initTableProperty(String[] proNames, Map<String, String> proToLabels) {
        this.propertyNames = proNames;
        this.propertyToLabels = proToLabels;

    }

    // record the columns which is used as block keys
    private List<String> markedAsBlockKey = null;

    // record the columns which is used as match keys
    private List<String> markedAsMatchKey = null;

    public void changeColumnHeaderLabelColor(String columnName, Color color, String keyName) {
        updateMarkedKeys(columnName, color, keyName);

        Style cellStyle = new Style();
        cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, color);

        natTable.getConfigRegistry().registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
                columnName);

        natTable.configure();

    }

    /**
     * update Marked Keys
     * 
     * @param columnName
     * @param color
     * @param keyName
     */
    private void updateMarkedKeys(String columnName, Color color, String keyName) {
        if (this.markedAsBlockKey == null) {
            markedAsBlockKey = new ArrayList<String>();
        }
        if (this.markedAsMatchKey == null) {
            markedAsMatchKey = new ArrayList<String>();
        }
        if (GUIHelper.COLOR_BLACK.equals(color)) {
            removeMarkedKey(columnName, keyName);
        } else if (GUIHelper.COLOR_GREEN.equals(color)) {
            markedAsBlockKey.add(columnName);
        } else if (GUIHelper.COLOR_RED.equals(color)) {
            this.markedAsMatchKey.add(columnName);
        }

    }

    /**
     * remove Marked Key".
     * 
     * @param columnName
     * @param keyName
     */
    private void removeMarkedKey(String columnName, String keyName) {
        if (DataSampleTable.MATCH_EKY.equals(keyName)) {
            this.markedAsMatchKey.remove(columnName);
        } else if (DataSampleTable.BLOCK_EKY.endsWith(keyName)) {
            this.markedAsBlockKey.remove(keyName);
        }

    }

    /**
     * When the column is the user selected one, return its name; when the column is the default additional one, return
     * null
     * 
     * @param position
     * @return
     */
    public String getUserColumnNameByPosition(int position) {
        if (position > propertyNames.length - MatchAnalysisConstant.DEFAULT_COLUMN_COUNT) {
            return null;
        }
        return propertyNames[position - 1];
    }

    public void refresh() {
        natTable.refresh();
    }

    /**
     * create the NatTable according to the property, and list of data to display
     * 
     * @param parent
     * @param data
     * @return
     */
    public NatTable createTableControl(Composite parent, List<Object[]> data) {
        bodyDataProvider = setupBodyDataProvider(data);
        bodyLayer = new BodyLayerStack(bodyDataProvider);

        DefaultColumnHeaderDataProvider colHeaderDataProvider = new DefaultColumnHeaderDataProvider(propertyNames,
                propertyToLabels);
        ColumnHeaderLayerStack columnHeaderLayer = new ColumnHeaderLayerStack(colHeaderDataProvider);

        DefaultRowHeaderDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
        RowHeaderLayerStack rowHeaderLayer = new RowHeaderLayerStack(rowHeaderDataProvider);

        DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(colHeaderDataProvider, rowHeaderDataProvider);
        CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);

        GridLayer gridLayer = new GridLayer(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

        if (natTable != null) {
            clearTable();
        }
        natTable = new NatTable(parent, gridLayer, false);
        natTable.addConfiguration(new DefaultNatTableStyleConfiguration() {

            {// use the own painter to paint the group color
                cellPainter = new RowBackgroundGroupPainter(new TextPainter(false, false, false));
            }
        });
        natTable.configure();

        natTable.getConfigRegistry().registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
                IEditableRule.NEVER_EDITABLE, DisplayMode.EDIT, "ODD_BODY"); //$NON-NLS-1$
        natTable.getConfigRegistry().registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
                IEditableRule.NEVER_EDITABLE, DisplayMode.EDIT, "EVEN_BODY"); //$NON-NLS-1$

        natTable.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

        // add the listener for the column header selection
        addCustomSelectionBehaviour();

        return natTable;
    }

    private void clearTable() {
        natTable.dispose();
        if (this.markedAsBlockKey != null) {
            this.markedAsBlockKey.clear();
        }
        if (this.markedAsMatchKey != null) {
            this.markedAsMatchKey.clear();
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private ListObjectDataProvider setupBodyDataProvider(List<Object[]> data) {
        IColumnPropertyAccessor columnPropertyAccessor = new ReflectiveColumnPropertyAccessor(propertyNames);
        return new ListObjectDataProvider(data, columnPropertyAccessor);
    }

    // for different data group, use different background color
    private class RowBackgroundGroupPainter extends BackgroundPainter {// GradientBackgroundPainter {

        // <groupid, related color>
        private Map<String, Integer> rowOfGIDWithColor = null;

        private String previousGID = null;

        public RowBackgroundGroupPainter(ICellPainter painter) {
            super(painter);
            rowOfGIDWithColor = new HashMap<String, Integer>();
        }

        @Override
        public void paintCell(ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
            super.paintCell(cell, gc, bounds, configRegistry);

            // when the GID changed, draw a line
            String currentGID = getGID(cell);
            if (currentGID != null) {
                // only draw a line when the group with same size neighbour with each other
                if (!StringUtils.equals(previousGID, currentGID) && isEqualGroupSize(previousGID, currentGID)) {
                    gc.setLineWidth(gc.getLineWidth() * 2);
                    gc.setLineStyle(SWT.LINE_DOT);
                    gc.setForeground(GUIHelper.COLOR_BLUE);
                    gc.drawLine(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y);
                }

                previousGID = currentGID;
            }
        }

        protected Color getBackgroundColour(ILayerCell cell, IConfigRegistry configRegistry) {
            int grpSizeValue = getGrpSize(cell);
            if (grpSizeValue == 0) {// default color when no
                return GUIHelper.COLOR_WHITE;
            }
            return COLOR_LIST[grpSizeValue % COLOR_LIST.length];
        }

        /**
         * first: if the row of the cell is a master row, return its group size second: if the row of the cell is not a
         * master one, return the previous one
         * 
         * @param cell
         * @return
         */
        private int getGrpSize(ILayerCell cell) {
            Object[] rowObject = (Object[]) bodyDataProvider.getRowObject(cell.getRowIndex());
            // if the row record contains the group size info, continue
            if (rowObject != null && rowObject.length > groupSizeIndex) {
                // find the group size from the map first, GID index = grp_size_index-1
                Integer groupSize = rowOfGIDWithColor.get(rowObject[groupSizeIndex - 1]);

                if (groupSize != null) {
                    return groupSize;
                }
                try {
                    // if the group id has no related group size, get it
                    groupSize = Integer.valueOf((String) ((Object[]) rowObject)[groupSizeIndex]);
                } catch (java.lang.NumberFormatException nfe) {
                    // no need to handle--when no column given
                    return 0;
                }
                // if the current row is a master row, record its group size
                if (Boolean.valueOf((String) rowObject[groupSizeIndex + 1])) {
                    // put the group size of this group id into the map
                    rowOfGIDWithColor.put((String) rowObject[groupSizeIndex - 1], groupSize);
                    return groupSize;
                } else {
                    // if the current row is not a master one,get its group size by its group id
                    return rowOfGIDWithColor.get(rowObject[groupSizeIndex - 1]);
                }
            } else {
                return 0;
            }
        }

        // get the group id of the cell if any
        private String getGID(ILayerCell cell) {
            Object[] rowObject = (Object[]) bodyDataProvider.getRowObject(cell.getRowIndex());
            // if the row record contains the group size info, continue
            if (rowObject != null && rowObject.length > groupSizeIndex) {
                return (String) rowObject[groupSizeIndex - 1];
            } else {
                return null;
            }
        }

        /**
         * check if the group size of the two group is equal or not.
         * 
         * @param previousGID2
         * @param currentGID
         * @return
         */
        private boolean isEqualGroupSize(String groupId1, String groupId2) {
            Integer size1 = rowOfGIDWithColor.get(groupId1);
            Integer size2 = rowOfGIDWithColor.get(groupId2);
            if (size1 != null && size2 != null && size1 == size2) {
                return true;
            }
            return false;
        }

    }

    class BodyLayerStack extends AbstractLayerTransform {

        private SelectionLayer selectionLayer;

        public BodyLayerStack(IDataProvider dataProvider) {
            DataLayer bodyDataLayer = new DataLayer(dataProvider);
            ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(bodyDataLayer);
            ColumnHideShowLayer columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
            this.selectionLayer = new SelectionLayer(columnHideShowLayer);

            ViewportLayer viewportLayer = new ViewportLayer(this.selectionLayer);
            setUnderlyingLayer(viewportLayer);
        }

        public SelectionLayer getSelectionLayer() {
            return this.selectionLayer;
        }
    }

    class ColumnHeaderLayerStack extends AbstractLayerTransform {

        public ColumnHeaderLayerStack(IDataProvider dataProvider) {
            DataLayer dataLayer = new DataLayer(dataProvider);
            ColumnHeaderLayer colHeaderLayer = new ColumnHeaderLayer(dataLayer, bodyLayer, bodyLayer.getSelectionLayer());

            setUnderlyingLayer(colHeaderLayer);
            dataLayer.setColumnPercentageSizing(true);
        }

        @Override
        public LabelStack getConfigLabelsByPosition(int columnPosition, int rowPosition) {
            // use columnPosition to get its property name, if the name is selected, return new LabelStack, else
            // return super
            String currentColumnName = getCurrentColumnName(columnPosition, rowPosition);
            // if the current column is used as key, return its labelstack.(the color of it will keep)
            if (isColumnMarkedAsKeys(currentColumnName)) {
                if (columnPosition < propertyNames.length + 1) {
                    return new LabelStack(currentColumnName);// + columnPosition);// ,
                                                             // propertyNames[columnPosition]);
                }
            }
            return super.getConfigLabelsByPosition(columnPosition, rowPosition);
        }

        /**
         * get Current Column Name.
         * 
         * @param columnPosition
         * @return
         */
        private String getCurrentColumnName(int columnPosition, int rowPosition) {
            ILayerCell cell = this.getCellByPosition(columnPosition, rowPosition);
            Object dataValue = cell.getDataValue();
            return String.valueOf(dataValue);
        }
    }

    class RowHeaderLayerStack extends AbstractLayerTransform {

        public RowHeaderLayerStack(IDataProvider dataProvider) {
            DataLayer dataLayer = new DataLayer(dataProvider, 50, 20);
            RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(dataLayer, bodyLayer, bodyLayer.getSelectionLayer());
            setUnderlyingLayer(rowHeaderLayer);
        }
    }

    class StyledColumnHeaderConfiguration extends DefaultColumnHeaderStyleConfiguration {

        public StyledColumnHeaderConfiguration() {
        }

        @Override
        public void configureRegistry(IConfigRegistry configRegistry) {
            super.configureRegistry(configRegistry);
            addNormalModeStyling(configRegistry);
            addSelectedModeStyling(configRegistry);
        }

        private void addSelectedModeStyling(IConfigRegistry configRegistry) {
            Image selectedBgImage = new Image(Display.getDefault(), getClass().getResourceAsStream(
                    "selected_column_header_bg.png")); //$NON-NLS-1$

            TextPainter txtPainter = new TextPainter(false, false);
            ICellPainter selectedCellPainter = new BackgroundImagePainter(txtPainter, selectedBgImage, GUIHelper.COLOR_BLUE);

            ColumnGroupHeaderTextPainter selectedHeaderPainter = new ColumnGroupHeaderTextPainter(new ColumnGroupModel(),
                    selectedCellPainter);

            configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, selectedHeaderPainter, "SELECT", //$NON-NLS-1$
                    "COLUMN_HEADER"); //$NON-NLS-1$
        }

        private void addNormalModeStyling(IConfigRegistry configRegistry) {
            Image bgImage = new Image(Display.getDefault(), getClass().getResourceAsStream("column_header_bg.png")); //$NON-NLS-1$

            TextPainter txtPainter = new TextPainter(false, false);
            ICellPainter bgImagePainter = new BackgroundImagePainter(txtPainter, bgImage, GUIHelper.getColor(192, 192, 192));
            SortableHeaderTextPainter headerPainter = new SortableHeaderTextPainter(bgImagePainter, false, true);

            configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, headerPainter, "NORMAL", "COLUMN_HEADER"); //$NON-NLS-1$ //$NON-NLS-2$
            configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, headerPainter, "NORMAL", "CORNER"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * check if the current column is marked as block/match keys.
     * 
     * @param columnPosition
     * @return
     */
    public boolean isColumnMarkedAsKeys(String column) {
        boolean isMarked = false;
        if (this.markedAsBlockKey != null && this.markedAsBlockKey.size() > 0) {
            if (this.markedAsBlockKey.contains(column)) {
                isMarked = true;
            }
        }
        if (this.markedAsMatchKey != null && this.markedAsMatchKey.size() > 0) {
            if (this.markedAsMatchKey.contains(column)) {
                isMarked = true;
            }
        }
        return isMarked;
    }
}
