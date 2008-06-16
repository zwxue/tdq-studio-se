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
package org.talend.dataprofiler.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.model.nodes.indicator.tpye.IndicatorEnum;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.BlankCountIndicator;
import org.talend.dataquality.indicators.BoxIndicator;
import org.talend.dataquality.indicators.CountsIndicator;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.IQRIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.LowerQuartileIndicator;
import org.talend.dataquality.indicators.MaxLengthIndicator;
import org.talend.dataquality.indicators.MaxValueIndicator;
import org.talend.dataquality.indicators.MeanIndicator;
import org.talend.dataquality.indicators.MedianIndicator;
import org.talend.dataquality.indicators.MinLengthIndicator;
import org.talend.dataquality.indicators.MinValueIndicator;
import org.talend.dataquality.indicators.NullCountIndicator;
import org.talend.dataquality.indicators.RangeIndicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.TextIndicator;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.UpperQuartileIndicator;

/**
 * This class can store the all the Indicators of one TdColumn, and provide the method to access all indicator.
 * 
 */
public class ColumnIndicator {

    private TdColumn tdColumn;

    private List<IndicatorEnum> tempIndicatorEnums = new ArrayList<IndicatorEnum>();

    // the indicatorEnums number equal plat IndicatorEnum(not hierrachy) numbers.
    private List<IndicatorEnum> flatIndicatorEnumList = new ArrayList<IndicatorEnum>();

    private Map<IndicatorEnum, IndicatorUnit> indicatorUnitMap = new HashMap<IndicatorEnum, IndicatorUnit>();

    private IndicatorUnit[] currentindicatorUnits;

    private final List<IndicatorEnum> countsEnumChildren = Arrays.asList(IndicatorEnum.CountsIndicatorEnum.getChildren());

    // static{
    // for(IndicatorEnum.CountsIndicatorEnum.getChildren()){
    //            
    // }
    // }

    public ColumnIndicator(TdColumn tdColumn) {
        this.tdColumn = tdColumn;
    }

    public boolean contains(IndicatorEnum indicatorEnum) {
        return this.flatIndicatorEnumList.contains(indicatorEnum);
    }

    /**
     * Remove the indicator value from field 'flatIndicatorEnumList' and 'indicatorUnitMap', contains the parent and
     * children of indicatorTypeMapping.getType().
     * 
     * @param indicatorUnit
     */
    public void removeIndicatorUnit(IndicatorUnit indicatorUnit) {
        IndicatorEnum indicatorEnum = indicatorUnit.getType();
        this.flatIndicatorEnumList.remove(indicatorEnum);
        indicatorUnitMap.remove(indicatorEnum);
        this.removeChildrenEnumMap(indicatorEnum);
        this.removeParentEnumMap(indicatorEnum);
        this.processCategoryIndicator();
    }

    private void removeChildrenEnumMap(IndicatorEnum indicatorEnum) {
        if (indicatorEnum.hasChildren()) {
            for (IndicatorEnum childEnum : indicatorEnum.getChildren()) {
                this.flatIndicatorEnumList.remove(childEnum);
                indicatorUnitMap.remove(childEnum);
                this.removeChildrenEnumMap(childEnum);
            }
        }
    }

    private void removeParentEnumMap(IndicatorEnum indicatorEnum) {
        if (indicatorEnum.hasParent()) {
            this.flatIndicatorEnumList.remove(indicatorEnum.getParent());
            indicatorUnitMap.remove(indicatorEnum.getParent());
            this.removeParentEnumMap(indicatorEnum.getParent());
        }
    }

    public boolean hasIndicators() {
        return !(this.flatIndicatorEnumList.size() == 0 || this.flatIndicatorEnumList.size() == 0);
    }

    /**
     * @return the tdColumn
     */
    public TdColumn getTdColumn() {
        return tdColumn;
    }

    public Indicator[] getIndicators() {
        List<Indicator> indicatorList = new ArrayList<Indicator>();
        IndicatorUnit[] indicatorUnits = this.getIndicatorUnits();

        for (IndicatorUnit indicatorUnit : indicatorUnits) {
            indicatorList.add(indicatorUnit.getIndicator());
        }
        return indicatorList.toArray(new Indicator[indicatorList.size()]);
    }

    public IndicatorUnit[] getIndicatorUnits() {
        if (currentindicatorUnits == null) {
            return new IndicatorUnit[0];
        }
        return currentindicatorUnits;
    }

    public void setIndicators(Indicator[] indicators) {
        clear();
        for (Indicator oneIndicator : indicators) {
            IndicatorEnum findIndicatorEnum = IndicatorEnum.findIndicatorEnum(oneIndicator.eClass());
            this.flatIndicatorEnumList.add(findIndicatorEnum);
            fillCategoryIndicators(findIndicatorEnum, oneIndicator);
        }
        processCategoryIndicator();

    }

    /**
     * Fill the indicator value to the corresponding indicator's property, and fill the value to field
     * 'flatIndicatorEnumList' and 'indicatorUnitMap'.
     * 
     * @param indicatorEnum
     * @param indicator
     */
    private void fillCategoryIndicators(IndicatorEnum indicatorEnum, Indicator indicator) {

        // add indicatorEnum to flatIndicatorEnumList
        if (indicatorEnum.hasChildren()) {
            for (IndicatorEnum indEnum : indicatorEnum.getChildren()) {
                if (this.flatIndicatorEnumList.contains(indEnum)) {
                    continue;
                }
                this.flatIndicatorEnumList.add(indEnum);
            }
        }
        switch (indicatorEnum) {
        case CountsIndicatorEnum:
            CountsIndicator countsIndicator = (CountsIndicator) indicator;
            this.indicatorUnitMap.put(IndicatorEnum.CountsIndicatorEnum, createIndicatorUnit(IndicatorEnum.CountsIndicatorEnum,
                    countsIndicator));

            // add indicatorUnit to indicatorUnitMap
            this.indicatorUnitMap.put(IndicatorEnum.BlankCountIndicatorEnum, createIndicatorUnit(
                    IndicatorEnum.BlankCountIndicatorEnum, countsIndicator.getBlankCountIndicator()));
            this.indicatorUnitMap.put(IndicatorEnum.DistinctCountIndicatorEnum, createIndicatorUnit(
                    IndicatorEnum.DistinctCountIndicatorEnum, countsIndicator.getDistinctCountIndicator()));
            this.indicatorUnitMap.put(IndicatorEnum.DuplicateCountIndicatorEnum, createIndicatorUnit(
                    IndicatorEnum.DuplicateCountIndicatorEnum, countsIndicator.getDuplicateCountIndicator()));
            this.indicatorUnitMap.put(IndicatorEnum.RowCountIndicatorEnum, createIndicatorUnit(
                    IndicatorEnum.RowCountIndicatorEnum, countsIndicator.getRowCountIndicator()));
            this.indicatorUnitMap.put(IndicatorEnum.NullCountIndicatorEnum, createIndicatorUnit(
                    IndicatorEnum.NullCountIndicatorEnum, countsIndicator.getNullCountIndicator()));
            this.indicatorUnitMap.put(IndicatorEnum.UniqueIndicatorEnum, createIndicatorUnit(IndicatorEnum.UniqueIndicatorEnum,
                    countsIndicator.getUniqueCountIndicator()));
            break;
        case TextIndicatorEnum:
            TextIndicator textIndicator = (TextIndicator) indicator;
            this.indicatorUnitMap.put(IndicatorEnum.TextIndicatorEnum, createIndicatorUnit(IndicatorEnum.TextIndicatorEnum,
                    textIndicator));

            // add indicatorUnit to indicatorUnitMap
            this.indicatorUnitMap.put(IndicatorEnum.MinLengthIndicatorEnum, createIndicatorUnit(
                    IndicatorEnum.MinLengthIndicatorEnum, textIndicator.getMinLengthIndicator()));
            this.indicatorUnitMap.put(IndicatorEnum.MaxLengthIndicatorEnum, createIndicatorUnit(
                    IndicatorEnum.MaxLengthIndicatorEnum, textIndicator.getMaxLengthIndicator()));
            this.indicatorUnitMap.put(IndicatorEnum.AverageLengthIndicatorEnum, createIndicatorUnit(
                    IndicatorEnum.AverageLengthIndicatorEnum, textIndicator.getAverageLengthIndicator()));
            break;
        case BoxIIndicatorEnum:
            BoxIndicator boxtIndicator = (BoxIndicator) indicator;
            this.indicatorUnitMap.put(IndicatorEnum.BoxIIndicatorEnum, createIndicatorUnit(IndicatorEnum.BoxIIndicatorEnum,
                    boxtIndicator));

            this.indicatorUnitMap.put(IndicatorEnum.MeanIndicatorEnum, createIndicatorUnit(IndicatorEnum.MeanIndicatorEnum,
                    boxtIndicator.getMeanIndicator()));
            this.indicatorUnitMap.put(IndicatorEnum.MedianIndicatorEnum, createIndicatorUnit(IndicatorEnum.MedianIndicatorEnum,
                    boxtIndicator.getMedianIndicator()));
            this.fillCategoryIndicators(IndicatorEnum.RangeIndicatorEnum, boxtIndicator.getRangeIndicator());
            this.fillCategoryIndicators(IndicatorEnum.IQRIndicatorEnum, boxtIndicator.getIQR());
            break;
        case IQRIndicatorEnum:
            IQRIndicator iqrIndicator = (IQRIndicator) indicator;
            this.indicatorUnitMap.put(IndicatorEnum.IQRIndicatorEnum, createIndicatorUnit(IndicatorEnum.IQRIndicatorEnum,
                    iqrIndicator));

            this.indicatorUnitMap.put(IndicatorEnum.LowerQuartileIndicatorEnum, createIndicatorUnit(
                    IndicatorEnum.LowerQuartileIndicatorEnum, iqrIndicator.getLowerValue()));
            this.indicatorUnitMap.put(IndicatorEnum.UpperQuartileIndicatorEnum, createIndicatorUnit(
                    IndicatorEnum.UpperQuartileIndicatorEnum, iqrIndicator.getUpperValue()));
            break;
        case RangeIndicatorEnum:
            RangeIndicator rangeIndicator = (RangeIndicator) indicator;
            this.indicatorUnitMap.put(IndicatorEnum.RangeIndicatorEnum, createIndicatorUnit(IndicatorEnum.RangeIndicatorEnum,
                    rangeIndicator));

            this.indicatorUnitMap.put(IndicatorEnum.MaxValueIndicatorEnum, createIndicatorUnit(
                    IndicatorEnum.MaxValueIndicatorEnum, rangeIndicator.getUpperValue()));
            this.indicatorUnitMap.put(IndicatorEnum.MinValueIndicatorEnum, createIndicatorUnit(
                    IndicatorEnum.MinValueIndicatorEnum, rangeIndicator.getLowerValue()));
            break;
        default:
            this.indicatorUnitMap.put(indicatorEnum, createIndicatorUnit(indicatorEnum, indicator));
            break;
        }

    }

    /**
     * DOC rli Comment method "clear".
     */
    private void clear() {
        flatIndicatorEnumList.clear();
        indicatorUnitMap.clear();
    }

    /**
     * Getter for detaminingType.
     * 
     * @return the detaminingType
     */
    public DataminingType getDataminingType() {
        DataminingType type = MetadataHelper.getDataminingType(tdColumn);

        if (type == null) {
            return MetadataHelper.getDefaultDataminingType(tdColumn.getJavaType());
        }

        return type;
    }

    /**
     * Sets the detaminingType.
     * 
     * @param detaminingType the detaminingType to set
     */
    public void setDataminingType(DataminingType dataminingType) {
        MetadataHelper.setDataminingType(dataminingType, tdColumn);
    }

    public void addIndicator(IndicatorEnum indicatorEnum, Indicator indicator) {
        this.flatIndicatorEnumList.add(indicatorEnum);
        this.createIndicatorUnit(indicatorEnum, indicator);
    }

    public void addPatternIndicator(IndicatorEnum indicatorEnum, Indicator indicator) {
        this.flatIndicatorEnumList.add(indicatorEnum);
        IndicatorUnit createIndicatorUnit = this.createIndicatorUnit(indicatorEnum, indicator);
        int size = 0;
        if (currentindicatorUnits != null) {
            size = currentindicatorUnits.length;
        }
        IndicatorUnit[] newUnits = new IndicatorUnit[size + 1];
        if (currentindicatorUnits != null) {
            System.arraycopy(currentindicatorUnits, 0, newUnits, 0, size);
        }
        newUnits[size] = createIndicatorUnit;
        this.currentindicatorUnits = newUnits;
    }

    public void addTempIndicatorEnum(IndicatorEnum indicatorEnum) {
        if (!tempIndicatorEnums.contains(indicatorEnum)) {
            tempIndicatorEnums.add(indicatorEnum);
        }
    }

    public void removeTempIndicatorEnum(IndicatorEnum indicatorEnum) {
        tempIndicatorEnums.remove(indicatorEnum);
    }

    public void copyOldIndicatorEnum() {
        listCopy(tempIndicatorEnums, flatIndicatorEnumList);
    }

    /**
     * Store the tempory indicator to flatIndicatorEnumList.
     */
    public void storeTempIndicator() {
        for (IndicatorEnum indEnum : tempIndicatorEnums) {
            if (!flatIndicatorEnumList.contains(indEnum)) {
                this.flatIndicatorEnumList.add(indEnum);
            }
        }
        // remove the deleted IndicatorEnum from current indicatorsEnums;
        Iterator<IndicatorEnum> iterator = flatIndicatorEnumList.iterator();
        while (iterator.hasNext()) {
            IndicatorEnum next = iterator.next();
            if (!tempIndicatorEnums.contains(next)) {
                iterator.remove();
            }
        }

        processCategoryIndicator();

        // clear tempIndicatorEnums
        tempIndicatorEnums.clear();
    }

    /**
     * Handle the category IndicatorEnum.
     */
    private void processCategoryIndicator() {
        List<IndicatorEnum> categoryEnumList = new ArrayList<IndicatorEnum>();
        listCopy(categoryEnumList, flatIndicatorEnumList);
        Iterator<IndicatorEnum> iterator = flatIndicatorEnumList.iterator();
        List<IndicatorEnum> currentCountsChildren = new ArrayList<IndicatorEnum>();
        while (iterator.hasNext()) {
            IndicatorEnum indEnum = iterator.next();
            if (countsEnumChildren.contains(indEnum)) {
                currentCountsChildren.add(indEnum);
                continue;
            }
            if ((indEnum != IndicatorEnum.CountsIndicatorEnum) && indEnum.hasChildren()) {
                for (IndicatorEnum childrenEnum : indEnum.getChildren()) {
                    categoryEnumList.remove(childrenEnum);
                }
            }

        }
        if (currentCountsChildren.size() == countsEnumChildren.size()
                && flatIndicatorEnumList.contains(IndicatorEnum.CountsIndicatorEnum)) {
            categoryEnumList.removeAll(currentCountsChildren);
        } else {
            categoryEnumList.remove(IndicatorEnum.CountsIndicatorEnum);
        }
        currentindicatorUnits = createCategoryIndicatorUnits(categoryEnumList.toArray(new IndicatorEnum[categoryEnumList.size()]));
    }

    /**
     * create indicatorMappings according category indicatorEnum.
     * 
     * @param categoryEnums
     * @return
     */
    private IndicatorUnit[] createCategoryIndicatorUnits(IndicatorEnum[] categoryEnums) {
        List<IndicatorUnit> indicatorUnitList = new ArrayList<IndicatorUnit>();
        IndicatorUnit indicatorUnit;
        for (IndicatorEnum categoryEnum : categoryEnums) {
            indicatorUnit = getIndicatorUnit(categoryEnum);
            switch (categoryEnum) {
            case CountsIndicatorEnum:
                CountsIndicator countsIndicator = (CountsIndicator) indicatorUnit.getIndicator();
                countsIndicator.setBlankCountIndicator((BlankCountIndicator) getIndicatorUnit(
                        IndicatorEnum.BlankCountIndicatorEnum).getIndicator());
                countsIndicator.setDistinctCountIndicator((DistinctCountIndicator) getIndicatorUnit(
                        IndicatorEnum.DistinctCountIndicatorEnum).getIndicator());
                countsIndicator.setDuplicateCountIndicator((DuplicateCountIndicator) getIndicatorUnit(
                        IndicatorEnum.DuplicateCountIndicatorEnum).getIndicator());
                countsIndicator.setRowCountIndicator((RowCountIndicator) getIndicatorUnit(IndicatorEnum.RowCountIndicatorEnum)
                        .getIndicator());
                countsIndicator.setNullCountIndicator((NullCountIndicator) getIndicatorUnit(IndicatorEnum.NullCountIndicatorEnum)
                        .getIndicator());
                countsIndicator
                        .setUniqueCountIndicator((UniqueCountIndicator) getIndicatorUnit(IndicatorEnum.UniqueIndicatorEnum)
                                .getIndicator());
                indicatorUnit.setChildren(createCategoryIndicatorUnits(IndicatorEnum.CountsIndicatorEnum.getChildren()));
                indicatorUnitList.add(indicatorUnit);
                break;

            case TextIndicatorEnum:
                TextIndicator textIndicator = (TextIndicator) indicatorUnit.getIndicator();
                textIndicator.setMinLengthIndicator((MinLengthIndicator) getIndicatorUnit(IndicatorEnum.MinLengthIndicatorEnum)
                        .getIndicator());
                textIndicator.setMaxLengthIndicator((MaxLengthIndicator) getIndicatorUnit(IndicatorEnum.MaxLengthIndicatorEnum)
                        .getIndicator());
                textIndicator.setAverageLengthIndicator((AverageLengthIndicator) getIndicatorUnit(
                        IndicatorEnum.AverageLengthIndicatorEnum).getIndicator());
                indicatorUnit.setChildren(createCategoryIndicatorUnits(IndicatorEnum.TextIndicatorEnum.getChildren()));
                indicatorUnitList.add(indicatorUnit);
                break;
            case BoxIIndicatorEnum:
                BoxIndicator boxtIndicator = (BoxIndicator) indicatorUnit.getIndicator();
                boxtIndicator.setRangeIndicator((RangeIndicator) getIndicatorUnit(IndicatorEnum.RangeIndicatorEnum)
                        .getIndicator());
                boxtIndicator.setIQR((IQRIndicator) getIndicatorUnit(IndicatorEnum.IQRIndicatorEnum).getIndicator());
                boxtIndicator.setMeanIndicator((MeanIndicator) getIndicatorUnit(IndicatorEnum.MeanIndicatorEnum).getIndicator());
                boxtIndicator.setMedianIndicator((MedianIndicator) getIndicatorUnit(IndicatorEnum.MedianIndicatorEnum)
                        .getIndicator());
                indicatorUnit.setChildren(createCategoryIndicatorUnits(IndicatorEnum.BoxIIndicatorEnum.getChildren()));
                indicatorUnitList.add(indicatorUnit);
                break;
            case IQRIndicatorEnum:
                IQRIndicator iqrIndicator = (IQRIndicator) indicatorUnit.getIndicator();
                iqrIndicator.setLowerValue((LowerQuartileIndicator) getIndicatorUnit(IndicatorEnum.LowerQuartileIndicatorEnum)
                        .getIndicator());
                iqrIndicator.setUpperValue((UpperQuartileIndicator) getIndicatorUnit(IndicatorEnum.UpperQuartileIndicatorEnum)
                        .getIndicator());
                indicatorUnit.setChildren(createCategoryIndicatorUnits(IndicatorEnum.IQRIndicatorEnum.getChildren()));
                indicatorUnitList.add(indicatorUnit);
                break;
            case RangeIndicatorEnum:
                RangeIndicator rangeIndicator = (RangeIndicator) indicatorUnit.getIndicator();
                rangeIndicator.setLowerValue((MinValueIndicator) getIndicatorUnit(IndicatorEnum.MinValueIndicatorEnum)
                        .getIndicator());
                rangeIndicator.setUpperValue((MaxValueIndicator) getIndicatorUnit(IndicatorEnum.MaxValueIndicatorEnum)
                        .getIndicator());
                indicatorUnit.setChildren(createCategoryIndicatorUnits(IndicatorEnum.RangeIndicatorEnum.getChildren()));
                indicatorUnitList.add(indicatorUnit);
                break;
            default:
                indicatorUnitList.add(indicatorUnit);
                break;
            }

        }
        return indicatorUnitList.toArray(new IndicatorUnit[indicatorUnitList.size()]);
    }

    /**
     * This method will get IndicatorUnit from indicatorUnitMap, if can't get exist object, it will be create a new
     * IndicatorUnit.
     * 
     * @param indicatorEnum
     * @return
     */
    public IndicatorUnit getIndicatorUnit(IndicatorEnum indicatorEnum) {
        IndicatorUnit indicatorUnit = this.indicatorUnitMap.get(indicatorEnum);
        if (indicatorUnit == null) {
            indicatorUnit = createIndicatorUnit(indicatorEnum, null);
        }
        return indicatorUnit;
    }

    /**
     * Create a new IndicatorUnit according to indicatorEnum and indicator, if the parameter indicator is null, will
     * create a new indicator .
     * 
     * @param indicatorEnum
     * @param indicator
     * @return
     */
    private IndicatorUnit createIndicatorUnit(IndicatorEnum indicatorEnum, Indicator indicator) {
        if (indicator == null) {
            IndicatorsFactory factory = IndicatorsFactory.eINSTANCE;
            indicator = (Indicator) factory.create(indicatorEnum.getIndicatorType());
        }
        IndicatorUnit indicatorUnit = new IndicatorUnit(indicatorEnum, indicator, this);
        this.indicatorUnitMap.put(indicatorEnum, indicatorUnit);
        return indicatorUnit;

    }

    private void listCopy(List<IndicatorEnum> dest, List<IndicatorEnum> src) {
        dest.clear();
        for (IndicatorEnum indicatorEnum : src) {
            dest.add(indicatorEnum);
        }
    }
}
