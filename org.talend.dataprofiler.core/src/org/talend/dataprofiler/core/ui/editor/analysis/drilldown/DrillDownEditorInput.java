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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.talend.dataprofiler.core.ui.editor.preview.model.MenuItemEntity;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dq.indicators.preview.table.ChartDataEntity;

/**
 * DOC zshen class global comment. Detailled comment
 */
public class DrillDownEditorInput implements IEditorInput {

    private Analysis analysis;

    private Indicator currIndicator;

    private MenuItemEntity menuItemEntity;

    private ChartDataEntity dataEntity;

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
        return this.dataEntity.getValue();
    }
}
