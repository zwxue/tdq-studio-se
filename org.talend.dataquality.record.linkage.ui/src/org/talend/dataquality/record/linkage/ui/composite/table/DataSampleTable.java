// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
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
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.painter.ColumnGroupHeaderTextPainter;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.config.DefaultColumnHeaderStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.painter.cell.BackgroundImagePainter;
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.talend.dataquality.record.linkage.ui.composite.ListObjectDataProvider;
import org.talend.dataquality.record.linkage.ui.section.DefaultMatchColumnConstant;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * when the property(displayed column) name of the table is changed, need to call initTableProperty first. if only the
 * data of the table changed, need only to call createTableControl. TODO: refresh the table with new data(column not
 * changed), and no need to create the table for every refresh; or give all the data to the table at one time?
 */
public class DataSampleTable {

    private BodyLayerStack bodyLayer;

    private String[] propertyNames;

    private Map<String, String> propertyToLabels;

    private NatTable natTable;

    private boolean isMdm = false;

    private boolean isDelimitedFile = false;

    public DataSampleTable(boolean isFile, boolean isMDM) {
        this.isMdm = isMDM;
        this.isDelimitedFile = isFile;
    }

    // public Control createSelectedTableByConnection(Composite parentContainer, DataManager connection, Object[]
    // columns) {
    // if (columns == null || columns.length < 1) {
    // return null;
    // }
    // // get the data from the columns
    // String sqlStatement = ConnectionManager.createSqlStatement((ModelElement[]) columns);
    // // create the NatTable
    // try {
    // return createTable(parentContainer, (ModelElement[]) columns,
    // ConnectionManager.executeQuery(connection, sqlStatement, columns.length));
    // } catch (SQLException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // return null;
    // }

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

        if (listOfData.size() < 1) {
            listOfData.add(getEmptyRow());
        }
        return createTableControl(parentContainer, listOfData);

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
        String[] columnsName = new String[columns.length + DefaultMatchColumnConstant.COLUMN_COUNT];

        int i = 0;
        for (ModelElement column : columns) {
            columnsName[i++] = column.getName();
        }
        columnsName[i++] = DefaultMatchColumnConstant.GID;
        columnsName[i++] = DefaultMatchColumnConstant.GRP_SIZE;
        columnsName[i++] = DefaultMatchColumnConstant.SCORE;
        columnsName[i++] = DefaultMatchColumnConstant.GRP_QUALITY;
        columnsName[i++] = DefaultMatchColumnConstant.ATTRIBUTE_SCORES;
        columnsName[i++] = DefaultMatchColumnConstant.BLOCK_KEY;
        return columnsName;
    }

    /**
     * before create the table control, need to init the property name and name to label map
     * 
     * @param propertyNames
     * @param propertyToLabels : <property name, display label>
     */
    public void initTableProperty(String[] propertyNames, Map<String, String> propertyToLabels) {
        this.propertyNames = propertyNames;
        this.propertyToLabels = propertyToLabels;

    }

    public void changeColumnHeaderLabelColor(String columnName, Color color) {
        Style cellStyle = new Style();
        cellStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, color);

        // unregister the old one
        natTable.getConfigRegistry().unregisterConfigAttribute(CellConfigAttributes.CELL_STYLE, DisplayMode.NORMAL, columnName);

        natTable.getConfigRegistry().registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
                columnName);

        // TextPainter textPainter = new TextPainter();
        //
        // CellPainterDecorator painterDecorator = new CellPainterDecorator(
        // new TextPainter(),CellEdgeEnum.TOP,new TextPainter(),false);
        //
        // natTable.getConfigRegistry().registerConfigAttribute(
        // CellConfigAttributes.CELL_PAINTER,
        // painterDecorator,
        // DisplayMode.NORMAL, columnName);
        natTable.configure();

        // ILayerCell layerCell = natTable.getCellByPosition(2, 2);
        // TextPainter cellPainter = new TextPainter();
        // Rectangle cellBounds = layerCell.getBounds();
        // Image image = new Image(natTable.getDisplay(), cellBounds.width, cellBounds.height);
        //
        // GC gc = new GC(image);
        // gc.setForeground(GUIHelper.COLOR_BLUE);
        // if (cellPainter != null) {
        // cellPainter.paintCell(layerCell, gc, cellBounds,
        // natTable.getConfigRegistry());
        // }
        // gc.dispose();

    }

    /**
     * When the column is the user selected one, return its name; when the column is the default additional one, return
     * null
     * 
     * @param position
     * @return
     */
    public String getUserColumnNameByPosition(int position) {
        if (position > propertyNames.length - DefaultMatchColumnConstant.COLUMN_COUNT) {
            return null;
        }
        return propertyNames[position - 1];
    }

    public void refresh() {
        natTable.forceFocus();
    }

    /**
     * create the NatTable according to the property, and list of data to display
     * 
     * @param parent
     * @param data
     * @return
     */
    public NatTable createTableControl(Composite parent, List<Object[]> data) {
        IDataProvider bodyDataProvider = setupBodyDataProvider(data);

        DefaultColumnHeaderDataProvider colHeaderDataProvider = new DefaultColumnHeaderDataProvider(propertyNames,
                propertyToLabels);

        DefaultRowHeaderDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);

        bodyLayer = new BodyLayerStack(bodyDataProvider);
        ColumnHeaderLayerStack columnHeaderLayer = new ColumnHeaderLayerStack(colHeaderDataProvider);

        RowHeaderLayerStack rowHeaderLayer = new RowHeaderLayerStack(rowHeaderDataProvider);

        DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(colHeaderDataProvider, rowHeaderDataProvider);
        CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);

        GridLayer gridLayer = new GridLayer(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

        // add control for each column header
        // ColumnOverrideLabelAccumulator columnLabelAccumulator = new ColumnOverrideLabelAccumulator(bodyLayer);
        // columnHeaderLayer.setConfigLabelAccumulator(columnLabelAccumulator);
        // registerColumnLabels(columnLabelAccumulator);

        // DummyBodyDataProvider bodyDataProvider = new DummyBodyDataProvider(500, 1000000);
        // SelectionLayer selectionLayer = new SelectionLayer(new DataLayer(bodyDataProvider));
        // ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
        //
        // ILayer columnHeaderLayer = new ColumnHeaderLayer(new DataLayer(new
        // DummyColumnHeaderDataProvider(bodyDataProvider)),
        // viewportLayer, selectionLayer);

        // CompositeLayer compositeLayer = new CompositeLayer(1, 2);
        // compositeLayer.setChildLayer("COLUMN_HEADER", columnHeaderLayer, 0, 0);
        // compositeLayer.setChildLayer("BODY", viewportLayer, 0, 1);

        if (natTable != null) {
            natTable.dispose();
        }
        natTable = new NatTable(parent, gridLayer);

        // no use for change color now
        // try {
        // natTable.addConfiguration(new StyledColumnHeaderConfiguration());
        // } catch (java.lang.IllegalStateException e) {
        // // no need to operate
        // }

        natTable.getConfigRegistry().registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
                IEditableRule.NEVER_EDITABLE, DisplayMode.EDIT, "ODD_BODY");
        natTable.getConfigRegistry().registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE,
                IEditableRule.NEVER_EDITABLE, DisplayMode.EDIT, "EVEN_BODY");

        return natTable;
    }

    /**
     * DOC yyin Comment method "registerColumnLabels".
     * 
     * @param columnLabelAccumulator
     */
    private void registerColumnLabels(ColumnOverrideLabelAccumulator columnLabelAccumulator) {
        int i = 0;
        for (String column : this.propertyNames) {
            columnLabelAccumulator.registerColumnOverrides(i++, new String[] { column });
        }

    }

    private IDataProvider setupBodyDataProvider(List<Object[]> data) {
        IColumnPropertyAccessor columnPropertyAccessor = new ReflectiveColumnPropertyAccessor(propertyNames);
        return new ListObjectDataProvider(data, columnPropertyAccessor);
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
            if (columnPosition < propertyNames.length + 1) {
                return new LabelStack(propertyNames[columnPosition] + columnPosition, propertyNames[columnPosition]);
            }
            return super.getConfigLabelsByPosition(columnPosition, rowPosition);
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

        public void configureRegistry(IConfigRegistry configRegistry) {
            super.configureRegistry(configRegistry);
            addNormalModeStyling(configRegistry);
            addSelectedModeStyling(configRegistry);
        }

        private void addSelectedModeStyling(IConfigRegistry configRegistry) {
            Image selectedBgImage = new Image(Display.getDefault(), getClass().getResourceAsStream(
                    "selected_column_header_bg.png"));

            TextPainter txtPainter = new TextPainter(false, false);
            ICellPainter selectedCellPainter = new BackgroundImagePainter(txtPainter, selectedBgImage, GUIHelper.COLOR_BLUE);

            ColumnGroupHeaderTextPainter selectedHeaderPainter = new ColumnGroupHeaderTextPainter(new ColumnGroupModel(),
                    selectedCellPainter);

            configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, selectedHeaderPainter, "SELECT",
                    "COLUMN_HEADER");
        }

        private void addNormalModeStyling(IConfigRegistry configRegistry) {
            Image bgImage = new Image(Display.getDefault(), getClass().getResourceAsStream("column_header_bg.png"));

            TextPainter txtPainter = new TextPainter(false, false);
            ICellPainter bgImagePainter = new BackgroundImagePainter(txtPainter, bgImage, GUIHelper.getColor(192, 192, 192));
            SortableHeaderTextPainter headerPainter = new SortableHeaderTextPainter(bgImagePainter, false, true);

            configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, headerPainter, "NORMAL", "COLUMN_HEADER");
            configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, headerPainter, "NORMAL", "CORNER");
        }
    }
}
