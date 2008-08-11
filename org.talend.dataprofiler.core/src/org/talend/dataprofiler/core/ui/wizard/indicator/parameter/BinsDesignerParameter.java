// ============================================================================
//
// Copyright (C) 2006-2007 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.indicator.parameter;

import java.util.List;

import org.talend.dataprofiler.core.model.nodes.indicator.option.SliceEntity;
import org.talend.dataprofiler.core.ui.utils.FormEnum;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.helpers.DomainHelper;

/**
 * DOC zqin class global comment. Detailled comment <br/>
 * 
 * $Id: talend.epf 1 2006-09-29 17:06:40Z zqin $
 * 
 */
public class BinsDesignerParameter extends AbstractIndicatorParameter {

    private double maxValue;

    private double minValue;

    private int numOfBins;

    private int numOfShown;

    private Object binsData;

    /**
     * Getter for maxValue.
     * 
     * @return the maxValue
     */
    public double getMaxValue() {
        return this.maxValue;
    }

    /**
     * Sets the maxValue.
     * 
     * @param maxValue the maxValue to set
     */
    public void setMaxValue(double maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Getter for minValue.
     * 
     * @return the minValue
     */
    public double getMinValue() {
        return this.minValue;
    }

    /**
     * Sets the minValue.
     * 
     * @param minValue the minValue to set
     */
    public void setMinValue(double minValue) {
        this.minValue = minValue;
    }

    /**
     * Getter for numOfBins.
     * 
     * @return the numOfBins
     */
    public int getNumOfBins() {
        return this.numOfBins;
    }

    /**
     * Sets the numOfBins.
     * 
     * @param numOfBins the numOfBins to set
     */
    public void setNumOfBins(int numOfBins) {
        this.numOfBins = numOfBins;
    }

    public int getNumOfShown() {
        return numOfShown;
    }

    public void setNumOfShown(int numOfShown) {
        this.numOfShown = numOfShown;
    }

    @Override
    public FormEnum getFormEnum() {

        return FormEnum.BinsDesignerForm;
    }

    public Object getBinsData() {
        return binsData;
    }

    public void setBinsData(Object binsData) {
        this.binsData = binsData;
    }

    @SuppressWarnings("unchecked")
    public Domain getUserDomian() {
        if (getBinsData() != null) {
            List<SliceEntity> tableData = (List<SliceEntity>) getBinsData();

            Domain domain = DomainHelper.createDomain("test");
            for (SliceEntity entity : tableData) {
                double min = Double.parseDouble(entity.getLowValue());
                double max = Double.parseDouble(entity.getHighValue());
                RangeRestriction rangeRestriction = DomainHelper.createRealRangeRestriction(min, max);
                domain.getRanges().add(rangeRestriction);
            }

            return domain;
        }

        return null;
    }
}
