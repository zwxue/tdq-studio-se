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
package org.talend.dataprofiler.core.ui.editor.analysis.drilldown;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.sqlexplorer.dataset.DataSet;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TableHelper;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.ui.editor.preview.model.MenuItemEntity;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.analysis.AnalyzedDataSet;
import org.talend.dataquality.analysis.impl.AnalyzedDataSetImpl;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.indicators.preview.table.ChartDataEntity;
import org.talend.dq.indicators.preview.table.PatternChartDataEntity;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class DrillDownEditorInput implements IEditorInput {

    private Analysis analysis;

    private Indicator currIndicator;

    private MenuItemEntity menuItemEntity;

    private ChartDataEntity dataEntity;

    private String[] columnHeader = null;

    private String[][] columnValue = null;

    public static final int MENU_VALUE_TYPE = 1;

    public static final int MENU_VALID_TYPE = 2;

    public static final int MENU_INVALID_TYPE = 3;

    public DrillDownEditorInput() {

    }

    public DrillDownEditorInput(Analysis analysis, ChartDataEntity dataEntity, MenuItemEntity menuItemEntity) {
        this.analysis = analysis;
        this.currIndicator = dataEntity.getIndicator();
        this.menuItemEntity = menuItemEntity;
        this.dataEntity = dataEntity;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#exists()
     */
    public boolean exists() {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return currIndicator.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getPersistable()
     */
    public IPersistableElement getPersistable() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IEditorInput#getToolTipText()
     */
    public String getToolTipText() {
        return analysis.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        // TODO Auto-generated method stub
        return null;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    public Indicator getCurrIndicator() {
        return currIndicator;
    }

    public void setCurrIndicator(Indicator currIndicator) {
        this.currIndicator = currIndicator;
    }

    public String getMenuType() {
        return this.menuItemEntity.getLabel();
    }

    public String getSelectValue() {
        return this.dataEntity.getLabel();
    }

    public String getComputeValue() {
        if (judgeMenuType(this.getMenuType(), MENU_INVALID_TYPE)) {
            return ((PatternChartDataEntity) this.dataEntity).getNumNoMatch();
        } else if (judgeMenuType(this.getMenuType(), MENU_VALID_TYPE)) {
            return ((PatternChartDataEntity) this.dataEntity).getNumMatch();
        }
        return this.dataEntity.getValue();
    }

    public static boolean judgeMenuType(String menuStr, int menuType) {
        if (menuStr == null)
            return false;
        switch (menuType) {
        case MENU_VALUE_TYPE:
            return menuStr.toLowerCase().indexOf("values") > -1;
        case MENU_VALID_TYPE:
            return menuStr.toLowerCase().indexOf("valid") > -1;
        case MENU_INVALID_TYPE:
            return menuStr.toLowerCase().indexOf("invalid") > -1;
        default:
            return false;
        }
    }

    public DataSet getDataSet() {
        List<TdColumn> columnElementList = filterAdaptColumnHeader();
        columnHeader = new String[columnElementList.size()];
        int headerIndex = 0;
        for (TdColumn columnElement : columnElementList) {
            columnHeader[headerIndex++] = columnElement.getName();
        }
        List<Object[]> newColumnElementList = filterAdaptDataList();
        if (newColumnElementList.size() <= 0) {
            columnValue = new String[0][0];
            return new DataSet(columnHeader, columnValue);
        }
        columnValue = new String[newColumnElementList.size()][newColumnElementList.get(0).length];
        int rowIndex = 0;
        for (Object[] tableRow : newColumnElementList) {
            int columnIndex = 0;
            for (Object tableValue : tableRow) {
                columnValue[rowIndex][columnIndex++] = tableValue == null ? "<null>" : tableValue.toString();
            }
            rowIndex++;
        }
        return new DataSet(columnHeader, columnValue);
        // return null;
    }

    public List<Object[]> filterAdaptDataList() {
        // get columnValue
        List<Object[]> newColumnElementList = new ArrayList<Object[]>();
        AnalyzedDataSet analysisDataSet = this.getAnalysis().getResults().getIndicToRowMap().get(currIndicator);
        if (analysisDataSet.getData() != null && analysisDataSet.getData().size() > 0) {
            newColumnElementList.addAll(getDesignatedData());
        } else if (analysisDataSet.getFrequencyData() != null && analysisDataSet.getFrequencyData().size() > 0) {
            String selectValue = this.getSelectValue();
            newColumnElementList.addAll(analysisDataSet.getFrequencyData().get(selectValue));
        } else if (analysisDataSet.getPatternData() != null && analysisDataSet.getPatternData().size() > 0) {
            if (DrillDownEditorInput.judgeMenuType(getMenuType(), DrillDownEditorInput.MENU_INVALID_TYPE)) {
                newColumnElementList.addAll((List<Object[]>) analysisDataSet.getPatternData().get(
                        AnalyzedDataSetImpl.INVALID_VALUE));
            } else if (DrillDownEditorInput.judgeMenuType(getMenuType(), DrillDownEditorInput.MENU_VALID_TYPE)) {
                newColumnElementList.addAll((List<Object[]>) analysisDataSet.getPatternData()
                        .get(AnalyzedDataSetImpl.VALID_VALUE));
            }
        }
        return newColumnElementList;
    }

    private List<Object[]> getDesignatedData() {
        AnalyzedDataSet analysisDataSet = this.getAnalysis().getResults().getIndicToRowMap().get(currIndicator);
        List<Object[]> dataList = analysisDataSet.getData();

        List<Object[]> returnDataList = new ArrayList<Object[]>();
        if (analysisDataSet.getData() == null || analysisDataSet.getData().size() < 0) {
            return returnDataList;
        }
        if (DrillDownEditorInput.judgeMenuType(this.getMenuType(), DrillDownEditorInput.MENU_VALUE_TYPE)) {
            List<TdColumn> columnElementList = TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(currIndicator
                    .getAnalyzedElement().eContainer()));
            int offset = columnElementList.indexOf(currIndicator.getAnalyzedElement());
            for (Object[] obj : dataList) {
                Object[] newObj = new Object[1];
                newObj[0] = obj[offset];
                returnDataList.add(newObj);
            }
        } else {
            returnDataList = dataList;
        }
        return returnDataList;
    }

    public List<TdColumn> filterAdaptColumnHeader() {
        // get columnHeader
        Indicator indicator = this.getCurrIndicator();
        String menuType = this.getMenuType();
        List<TdColumn> columnElementList = null;
        if (DrillDownEditorInput.judgeMenuType(menuType, DrillDownEditorInput.MENU_VALUE_TYPE)) {
            columnElementList = new ArrayList<TdColumn>();
            columnElementList.add((TdColumn) indicator.getAnalyzedElement());
        } else {
            columnElementList = TableHelper.getColumns(SwitchHelpers.TABLE_SWITCH.doSwitch(indicator.getAnalyzedElement()
                    .eContainer()));
        }
        return columnElementList;
    }

    public boolean isDataSpill() {
        return analysis.getResults().getIndicToRowMap().get(currIndicator).getDataCount() < Double.parseDouble(getComputeValue());
    }

}
