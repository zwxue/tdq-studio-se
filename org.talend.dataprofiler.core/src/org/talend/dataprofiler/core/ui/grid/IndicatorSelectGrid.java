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
package org.talend.dataprofiler.core.ui.grid;

import org.eclipse.nebula.widgets.grid.Grid;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.helper.ModelElementIndicatorHelper;
import org.talend.dataprofiler.core.model.ModelElementIndicator;
import org.talend.dq.nodes.DBColumnRepNode;
import org.talend.dq.nodes.indicator.IIndicatorNode;
import org.talend.dq.nodes.indicator.IndicatorTreeModelBuilder;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.repository.model.IRepositoryNode;

/**
 * this customized nebula grid is used for indicator selection.
 */
public class IndicatorSelectGrid extends Grid {

    private ModelElementIndicator[] _modelElementIndicators;

    private IndicatorSelectDialog2 _dialog;

    static final Color gray = new Color(Display.getCurrent(), 240, 240, 240);

    static final Color yellow = new Color(Display.getCurrent(), 255, 255, 40);

    static final Color lightYellow = new Color(Display.getCurrent(), 255, 255, 200);

    static final Color blue = new Color(Display.getCurrent(), 51, 204, 255);

    static final Color lightBlue = new Color(Display.getCurrent(), 200, 220, 240);

    static final Image tickImage = ImageLib.getImage(ImageLib.TICK_IMAGE);

    static final Image indImage = ImageLib.getImage(ImageLib.IND_DEFINITION);

    static final Image pkImage = ImageLib.getImage(ImageLib.PK_ICON);

    static final int COLUMN_WIDTH = 50;

    /**
     * IndicatorSelectionGrid constructor.
     * 
     * @param parent
     * @param style
     * @param modelElementIndicators
     * @param modelElementIndicators
     */
    public IndicatorSelectGrid(IndicatorSelectDialog2 dialog, Composite parent, int style,
            ModelElementIndicator[] modelElementIndicators) {
        super(parent, style);
        _dialog = dialog;
        _modelElementIndicators = modelElementIndicators;
        addExtraListeners();
        initializeGrid();
    }

    private void initializeGrid() {

        // first column is for indicator labels, it is hided from the cells but shown as row header.
        GridColumn indicatorLabelColumn = new GridColumn(this, SWT.NONE);
        indicatorLabelColumn.setTree(true);
        indicatorLabelColumn.setWidth(200);
        indicatorLabelColumn.setText("Indicators");
        getColumn(0).setVisible(false); // hide the label column, but it is actually visible in the fixed column

        // select all column
        GridColumn rowSelectCol = new GridColumn(this, SWT.CHECK);
        rowSelectCol.setHeaderRenderer(new TdColumnHeaderRenderer());
        rowSelectCol.setCellRenderer(new TdCellRenderer());
        rowSelectCol.setText("Select All");
        rowSelectCol.setWidth(COLUMN_WIDTH);
        rowSelectCol.setWordWrap(true);
        rowSelectCol.setResizeable(false);
        rowSelectCol.setCellSelectionEnabled(true);
        rowSelectCol.setVisible(false); // hide the row select column, it is also visible in the fixed column.

        // database columns
        for (int i = 0; i < _modelElementIndicators.length; i++) {
            GridColumn newCol = new GridColumn(this, SWT.CHECK);
            newCol.setHeaderRenderer(new TdColumnHeaderRenderer());
            newCol.setCellRenderer(new TdCellRenderer());
            newCol.setText(ModelElementIndicatorHelper.getModelElementDisplayName(_modelElementIndicators[i]));
            newCol.setWidth(COLUMN_WIDTH);
            newCol.setData(_modelElementIndicators[i]);
            newCol.setMoveable(true);
            newCol.setResizeable(false);
            IRepositoryNode repNode = _modelElementIndicators[i].getModelElementRepositoryNode();
            if (repNode instanceof DBColumnRepNode && ((DBColumnRepNode) repNode).isKey()) {
                newCol.setImage(pkImage);
            }
        }
        recalculateHeader();

        // initialize grid contents
        IIndicatorNode[] branchNodes = IndicatorTreeModelBuilder.buildIndicatorCategory();
        for (int i = 0; i < branchNodes.length; i++) {
            // indicator category row
            IIndicatorNode indicatorNode = branchNodes[i];

            GridItem item = new GridItem(this, SWT.NONE);
            item.setText(indicatorNode.getLabel());
            item.setData(indicatorNode);
            createChildNodes(null, item, indicatorNode);
            processNodeSelection(null, item);
        }

        // show fixed column header
        setHeaderVisible(true);
        setTopLeftRenderer(new TdTopLeftRenderer());
        // setCellHeaderSelectionBackground(IndicatorSelectGrid.standardYellow);

        setEmptyColumnHeaderRenderer(new TdEmptyColumnHeaderRenderer());
        setEmptyRowHeaderRenderer(new TdEmptyCellRenderer());
        setEmptyCellRenderer(new TdEmptyCellRenderer());

        // show fixed row header
        TdRowHeaderRenderer rowHeaderRenderer = new TdRowHeaderRenderer();
        setRowHeaderRenderer(rowHeaderRenderer);
        rowHeaderRenderer.setTree(true);
        rowHeaderRenderer.setWordWrap(false);
        setRowHeaderVisible(true);

        setLinesVisible(true);
        setColumnScrolling(true);
        setSelectionEnabled(false);
        setCellSelectionEnabled(false);

        setRowsResizeable(false);
        setItemHeight(25);
        setLineColor(IndicatorSelectGrid.lightBlue);
        setFocusRenderer(null);

        for (GridItem gridItem : getItems()) {
            gridItem.setBackground(0, IndicatorSelectGrid.gray);
        }
    }

    /**
     * DOC root Comment method "addExtraListener".
     */
    private void addExtraListeners() {

        addMouseTrackListener(new MouseTrackListener() {

            public void mouseEnter(MouseEvent e) {
            }

            public void mouseExit(MouseEvent e) {
                if (e != null) {
                    return;
                }
                for (int j = 1; j < getColumns().length; j++) {
                    getColumn(j).getHeaderRenderer().setSelected(false);
                }

            }

            public void mouseHover(MouseEvent e) {
            }

        });

        addMouseMoveListener(new MouseMoveListener() {

            public void mouseMove(MouseEvent e) {
                onMouseMove(e);
            }
        });

        addMouseListener(new MouseListener() {

            public void mouseDoubleClick(MouseEvent e) {
            }

            public void mouseDown(MouseEvent e) {
                onMouseDown(e);
            }

            public void mouseUp(MouseEvent e) {

            }

        });
    }

    private void onMouseDown(MouseEvent e) {
        Point cell = getCell(new Point(e.x, e.y));
        if (cell != null) {
            boolean checked = getItem(cell.y).getChecked(cell.x);
            tickCell(cell, checked);
            GridItem parent = getItem(cell.y);
            while (parent.getParentItem() != null) {
                parent = parent.getParentItem();
            }
            processNodeSelection(null, parent);
        } else {
            GridItem item = getItem(new Point(e.x, e.y));
            if (e.button == 1 && item != null) {
                if (overRowSelect(item, new Point(e.x, e.y))) {
                    boolean rowChecked = item.getChecked(1);
                    tickCell(new Point(1, getIndexOfItem(item)), !rowChecked);
                    GridItem parent = item;
                    while (parent.getParentItem() != null) {
                        parent = parent.getParentItem();
                    }
                    processNodeSelection(null, parent);
                    if (item.getParentItem() == null && rowChecked) {
                        item.setExpanded(false);
                    }
                } else {
                    if (item.hasChildren() && item.getParentItem() == null) {
                        item.setExpanded(!item.isExpanded());
                    }
                    TdRowHeaderRenderer renderer = ((TdRowHeaderRenderer) getRowHeaderRenderer());
                    e.x = renderer.getSize().x - 1; // Move into row select cell
                    onMouseMove(e);
                    _dialog.updateIndicatorInfo(item);
                }
            }
        }
    }

    private void onMouseMove(MouseEvent e) {
        Point cell = getCell(new Point(e.x, e.y));
        if (cell != null && cell.x != 0) { // any cell except the row select cells
            GridVisibleRange range = getVisibleRange();
            for (GridItem item : range.getItems()) {
                int i = indexOf(item);
                // set background for
                if (i == cell.y) {
                    item.setBackground(0, yellow);
                    item.setBackground(1, lightYellow);
                } else {
                    item.setBackground(0, gray);
                    item.setBackground(1, getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
                    if (item.getCheckable(1)) {
                        item.setBackground(yellow);
                    }
                }

                for (GridColumn column : range.getColumns()) {
                    int j = indexOf(column);
                    if (i == cell.y && j == cell.x) {
                        item.setBackground(j, yellow);
                    } else if (i == cell.y && j < cell.x || j == cell.x && i < cell.y) {
                        item.setBackground(j, lightYellow);
                    } else {
                        item.setBackground(j, null);
                    }
                }
            }

            for (int j = 0; j < getColumnCount(); j++) {
                getColumn(j).getHeaderRenderer().setSelected(j == cell.x);
            }

        } else { // row select cells
            GridItem currentItem = getItem(new Point(e.x, e.y));
            if (currentItem != null) {
                if (overRowSelect(currentItem, new Point(e.x, e.y))) { // handle hover event of row select cell
                    GridVisibleRange range = getVisibleRange();
                    for (GridItem item : range.getItems()) {
                        int i = indexOf(item);
                        if (item.getCheckable(0)) {
                            if (i == indexOf(currentItem)) {
                                item.setBackground(0, yellow);
                                item.setBackground(1, yellow);
                            } else {
                                item.setBackground(0, gray);
                                item.setBackground(1, getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
                            }
                        }

                        for (GridColumn column : range.getColumns()) {
                            int j = indexOf(column);
                            if (i == indexOf(currentItem)) {
                                item.setBackground(j, lightYellow);
                            } else {
                                item.setBackground(j, getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
                            }
                        }
                    }
                    for (int j = 0; j < getColumnCount(); j++) {
                        getColumn(j).getHeaderRenderer().setSelected(false);
                    }
                    return;
                }
            }
        }
    }

    private boolean overRowSelect(GridItem item, Point point) {

        point = new Point(point.x, point.y);
        point.y -= item.getBounds(0).y;

        int x = getRowHeaderWidth();
        if (point.x > x - 50 && point.x < x) {
            if (point.y > 0 && point.y < getItemHeight()) {
                return true;
            }
        }

        return false;
    }

    private void tickCell(Point cell, boolean tick) {
        if (!getItem(cell.y).getCheckable(cell.x)) {
            return;
        }

        if (cell.x == 1) {
            for (int i = 2; i < getColumnCount(); i++) {
                tickCell(new Point(i, cell.y), tick);
            }
        }

        getItem(cell.y).setChecked(cell.x, tick);

        IIndicatorNode indicatorNode = (IIndicatorNode) getItem(cell.y).getData();
        IndicatorEnum indicatorEnum = indicatorNode.getIndicatorEnum();
        ModelElementIndicator meIndicator = (ModelElementIndicator) getColumn(cell.x).getData();
        if (meIndicator != null) {
            if (tick) {
                if (indicatorEnum != null) {
                    meIndicator.addTempIndicatorEnum(indicatorEnum);
                }
            } else {
                meIndicator.removeTempIndicatorEnum(indicatorEnum);
            }
        }
        // select the entire indicator category
        if (getItem(cell.y).hasChildren()) {
            for (GridItem child : getItem(cell.y).getItems()) {
                tickCell(new Point(cell.x, indexOf(child)), tick);
            }
        }
    }

    /**
     * recursively create tree nodes and checked the existing indicators.
     * 
     * @param grid
     * @param currentItem
     * @param indicatorNode
     */
    void createChildNodes(GridItem parentItem, GridItem currentItem, IIndicatorNode indicatorNode) {

        Boolean hasCheckableInColumn[] = new Boolean[getColumnCount()];

        for (int j = 1; j < getColumnCount(); j++) {
            hasCheckableInColumn[j] = false;
        }

        for (int i = 0; i < indicatorNode.getChildren().length; i++) {
            IIndicatorNode childNode = indicatorNode.getChildren()[i];
            GridItem childItem = new GridItem(currentItem, SWT.NONE);
            childItem.setImage(indImage);

            if (childNode.hasChildren()) {
                createChildNodes(currentItem, childItem, childNode);
            }

            childItem.setText(childNode.getLabel());
            childItem.setData(childNode);
            if (parentItem == null) {
                childItem.setExpanded(true);
            }

            boolean hasCheckableInRow = false;

            for (int j = 2; j < getColumnCount(); j++) { // ignore indicator label column and row select column

                IndicatorEnum indicatorEnum = childNode.getIndicatorEnum();

                // DB columns
                ModelElementIndicator meIndicator = null;
                if (j - 2 < _modelElementIndicators.length) {
                    meIndicator = _modelElementIndicators[j - 2];
                } else {
                    meIndicator = _modelElementIndicators[0];
                }

                // Enable/disable the check box
                boolean isMatch = _dialog.isMatchCurrentIndicator(meIndicator, childNode);
                childItem.setCheckable(j, isMatch);

                if (isMatch) {
                    hasCheckableInRow = true;
                    hasCheckableInColumn[j] = true;
                    // Check the box if it is already selected
                    if (meIndicator != null && meIndicator.tempContains(indicatorEnum)) {
                        childItem.setChecked(j, true);
                    }
                }
            }

            childItem.setCheckable(1, hasCheckableInRow);
        }

        boolean entireCategoryCheckable = false;
        for (int j = 2; j < getColumnCount(); j++) {
            if (hasCheckableInColumn[j]) {
                entireCategoryCheckable = true;
            } else {
                currentItem.setCheckable(j, false);
            }
        }
        currentItem.setCheckable(1, entireCategoryCheckable);
    }

    /**
     * recursively check if a entire row/column is selected/
     * 
     * @param grid
     * @param parentItem
     * @param currentItem
     */
    void processNodeSelection(GridItem parentItem, GridItem currentItem) {
        if (currentItem.hasChildren()) {
            // declare and initialize variables
            Boolean allCheckedInColumn[] = new Boolean[getColumnCount()];
            Boolean hasCheckedInColumn[] = new Boolean[getColumnCount()];
            for (int j = 1; j < getColumnCount(); j++) {
                allCheckedInColumn[j] = true;
                hasCheckedInColumn[j] = false;
            }

            for (int i = 0; i < currentItem.getItemCount(); i++) {
                GridItem childItem = currentItem.getItem(i);
                // process the children of current item, this must be done before handling the current item
                processNodeSelection(currentItem, childItem);

                boolean allCheckedInRow = true;
                boolean hasCheckedInRow = false;
                boolean expanded = false;

                for (int j = 2; j < getColumnCount(); j++) {
                    if (childItem.getChecked(j)) {
                        hasCheckedInRow = true;
                        hasCheckedInColumn[j] = true;
                        expanded = true;
                    } else {
                        if (childItem.getCheckable(j)) {
                            allCheckedInRow = false;
                            allCheckedInColumn[j] = false;
                        }
                    }
                }
                childItem.setChecked(1, hasCheckedInRow && allCheckedInRow);

                if (expanded) {
                    currentItem.setExpanded(true);
                    if (parentItem != null) {
                        parentItem.setExpanded(true);
                    }
                }

            }

            // process the selections of indicator category row
            boolean entireCategoryChecked = true;
            for (int j = 2; j < getColumnCount(); j++) {
                if (currentItem.getCheckable(j)) {
                    if (allCheckedInColumn[j]) {
                        currentItem.setChecked(j, true);
                    } else {
                        currentItem.setChecked(j, false);
                        entireCategoryChecked = false;
                    }
                }
            }
            if (currentItem.getCheckable(1)) {
                currentItem.setChecked(1, entireCategoryChecked);
            }
        }
    }

    /**
     * select/deselect all enabled indicators when Ctrl+Shift+[A|N] is down.
     * 
     * @param select
     */
    public void setAllIndicators(boolean select) {
        for (int i = 0; i < getItemCount(); i++) {
            for (int j = 1; j < getColumnCount(); j++) {
                if (getItem(i).getParentItem() == null) {
                    tickCell(new Point(j, i), select);
                    getItem(i).setExpanded(select);
                }
            }
        }
    }
}
