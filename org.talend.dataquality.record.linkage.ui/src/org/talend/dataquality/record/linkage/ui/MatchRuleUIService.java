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
package org.talend.dataquality.record.linkage.ui;

import java.util.List;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabFolderRenderer;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.talend.core.service.IMatchRuleUIService;
import org.talend.dataquality.record.linkage.ui.composite.chart.MatchRuleDataChart;
import org.talend.dataquality.record.linkage.ui.composite.table.MatchRuleDataTable;
import org.talend.dataquality.record.linkage.ui.composite.utils.ImageLib;
import org.talend.dataquality.record.linkage.ui.section.MatchRuleCTabFolderRenderer;


public class MatchRuleUIService implements IMatchRuleUIService {

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.service.IMatchRuleUIService#getImageByName(java.lang.String)
     */
    @Override
    public Image getImageByName(String imageName) {
        return ImageLib.getImage(imageName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.service.IMatchRuleUIService#createMatchRuleCTabFolderRenderer()
     */
    @Override
    public CTabFolderRenderer createMatchRuleCTabFolderRenderer(CTabFolder configurationFolder) {
        return new MatchRuleCTabFolderRenderer(configurationFolder);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.service.IMatchRuleUIService#getAddImage()
     */
    @Override
    public Image getAddImage() {
        return getImageByName(ImageLib.ADD_ACTION);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.service.IMatchRuleUIService#getMatchRuleTable()
     */
    @Override
    public Composite createMatchRuleTable(SashForm parent, List<String> columnNames) {
        return new MatchRuleDataTable(parent, columnNames.toArray(new String[0]));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.service.IMatchRuleUIService#createMatchRuleDataChart(org.eclipse.swt.widgets.Composite,
     * java.util.List, java.util.List)
     */
    @Override
    public Composite createMatchRuleDataChart(Composite parent, List<String[]> viewData, List<String> columnNames) {
        return new MatchRuleDataChart(parent, viewData, columnNames.toArray(new String[0]));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.service.IMatchRuleUIService#setNeedDisplayCount(int)
     */
    @Override
    public void setNeedDisplayCount(int count, Composite com) {
        if (com instanceof MatchRuleDataChart) {
            ((MatchRuleDataChart) com).setTimes(count);
        } else if (com instanceof MatchRuleDataTable) {
            ((MatchRuleDataTable) com).setDisGroupSize(count);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.service.IMatchRuleUIService#refreshMatchRuleChart(java.util.List,
     * org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void refreshMatchRuleChart(List<String[]> viewData, Composite com) {
        if (com instanceof MatchRuleDataChart) {
            ((MatchRuleDataChart) com).refresh(viewData);
        } else if (com instanceof MatchRuleDataTable) {
            ((MatchRuleDataTable) com).refresh(viewData);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.core.service.IMatchRuleUIService#layoutMatchRuleChart()
     */
    @Override
    public void layoutMatchRuleChart(Composite chart) {
        if (chart instanceof MatchRuleDataChart) {
            ((MatchRuleDataChart) chart).layout();
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.core.service.IMatchRuleUIService#disposeMatchRuleChart(org.eclipse.swt.widgets.Composite)
     */
    @Override
    public void disposeMatchRuleChart(Composite chart) {
        if (chart instanceof MatchRuleDataChart) {
            ((MatchRuleDataChart) chart).dispose();
        }
    }

}
