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
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.BlankCountIndicator;
import org.talend.dataquality.indicators.BoxIndicator;
import org.talend.dataquality.indicators.CountsIndicator;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.IQRIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
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
import org.talend.utils.sql.Java2SqlType;

/**
 * This class can store the all the Indicators of one TdColumn, and provide the method to access all indicator.
 * 
 */
public class ColumnIndicator {

    private final List<IndicatorEnum> countsEnumChildren = Arrays.asList(IndicatorEnum.CountsIndicatorEnum.getChildren());

    private TdColumn tdColumn;

    private List<IndicatorEnum> tempIndicatorEnums = new ArrayList<IndicatorEnum>();

    // the indicatorEnums number equal plat IndicatorEnum(not hierrachy) numbers.
    private List<IndicatorEnum> flatIndicatorEnumList = new ArrayList<IndicatorEnum>();

    private Map<IndicatorEnum, IndicatorUnit> plainIndicatorUnitMap = new HashMap<IndicatorEnum, IndicatorUnit>();

    private IndicatorUnit[] plainIndicatorUnits;

    private List<IndicatorUnit> specialIndicatorUnitList;

    public ColumnIndicator(TdColumn tdColumn) {
        this.tdColumn = tdColumn;
    }

    public boolean contains(IndicatorEnum indicatorEnum) {
        return this.flatIndicatorEnumList.contains(indicatorEnum);
    }

    /**
     * If the Indicator is a plain indicator, will remove the indicator value from field 'flatIndicatorEnumList' and
     * 'indicatorUnitMap', contains the parent and children of indicatorTypeMapping.getType();Else, will remove it from
     * specialIndicaortUnitList.
     * 
     * @param indicatorUnit
     */
    public void removeIndicatorUnit(IndicatorUnit indicatorUnit) {
        IndicatorEnum indicatorEnum = indicatorUnit.getType();
        if (IndicatorEnum.isPlainIndicatorEnum(indicatorEnum)) {
            removePlainIndicatorUnit(indicatorEnum);
        } else {
            removeSpecialIndicatorUnit(indicatorUnit);
        }
    }

    /**
     * Remove the specialIndicatorUnit from specialIndicatorList, if there exist more than one InicatorIndicator which
     * has same IndicatorEnumn type, the type of IndicatorUnit will be not removed from flatIndicatorEnumList.
     * 
     * @param indicatorUnit
     */
    private void removeSpecialIndicatorUnit(IndicatorUnit indicatorUnit) {
        IndicatorEnum indicatorEnumn = indicatorUnit.getType();
        this.specialIndicatorUnitList.remove(indicatorUnit);
        for (IndicatorUnit unit : specialIndicatorUnitList) {
            if (unit.getType() == indicatorEnumn) {
                return;
            } else {
                continue;
            }
        }
        this.flatIndicatorEnumList.remove(indicatorEnumn);

    }

    private void removePlainIndicatorUnit(IndicatorEnum indicatorEnum) {

        this.flatIndicatorEnumList.remove(indicatorEnum);
        plainIndicatorUnitMap.remove(indicatorEnum);
        this.removeChildrenEnumMap(indicatorEnum);
        this.removeParentEnumMap(indicatorEnum);
        this.processCategoryIndicator();
    }

    private void removeChildrenEnumMap(IndicatorEnum indicatorEnum) {
        if (indicatorEnum.hasChildren()) {
            for (IndicatorEnum childEnum : indicatorEnum.getChildren()) {
                this.flatIndicatorEnumList.remove(childEnum);
                plainIndicatorUnitMap.remove(childEnum);
                this.removeChildrenEnumMap(childEnum);
            }
        }
    }

    private void removeParentEnumMap(IndicatorEnum indicatorEnum) {
        if (indicatorEnum.hasParent()) {
            this.flatIndicatorEnumList.remove(indicatorEnum.getParent());
            plainIndicatorUnitMap.remove(indicatorEnum.getParent());
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
        if (plainIndicatorUnits == null && (this.specialIndicatorUnitList == null || this.specialIndicatorUnitList.size() == 0)) {
            return new IndicatorUnit[0];
        }
        List<IndicatorUnit> unitList = new ArrayList<IndicatorUnit>();
        if (plainIndicatorUnits != null && plainIndicatorUnits.length > 0) {
            for (IndicatorUnit unit : plainIndicatorUnits) {
                unitList.add(unit);
            }
        }
        if (this.specialIndicatorUnitList != null && this.specialIndicatorUnitList.size() > 0) {
            unitList.addAll(specialIndicatorUnitList);
        }
        return unitList.toArray(new IndicatorUnit[unitList.size()]);
    }

    public void setIndicators(Indicator[] indicators) {
        clear();
        for (Indicator oneIndicator : indicators) {
            IndicatorEnum findIndicatorEnum = IndicatorEnum.findIndicatorEnum(oneIndicator.eClass());
            if (IndicatorEnum.isPlainIndicatorEnum(findIndicatorEnum)) {
                this.flatIndicatorEnumList.add(findIndicatorEnum);
                fillCategoryIndicators(findIndicatorEnum, oneIndicator);
            } else {
                this.addSpecialIndicator(findIndicatorEnum, oneIndicator);
            }
        }
        processCategoryIndicator();

    }

    /**
     * Fill the plain indicator value to the corresponding indicator's property, and fill the value to field
     * 'flatIndicatorEnumList' and 'plainIndicatorUnitMap'.
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
            this.plainIndicatorUnitMap.put(IndicatorEnum.CountsIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.CountsIndicatorEnum, countsIndicator));

            // add indicatorUnit to indicatorUnitMap
            this.plainIndicatorUnitMap.put(IndicatorEnum.BlankCountIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.BlankCountIndicatorEnum, countsIndicator.getBlankCountIndicator()));
            this.plainIndicatorUnitMap.put(IndicatorEnum.DistinctCountIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.DistinctCountIndicatorEnum, countsIndicator.getDistinctCountIndicator()));
            this.plainIndicatorUnitMap.put(IndicatorEnum.DuplicateCountIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.DuplicateCountIndicatorEnum, countsIndicator.getDuplicateCountIndicator()));
            this.plainIndicatorUnitMap.put(IndicatorEnum.RowCountIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.RowCountIndicatorEnum, countsIndicator.getRowCountIndicator()));
            this.plainIndicatorUnitMap.put(IndicatorEnum.NullCountIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.NullCountIndicatorEnum, countsIndicator.getNullCountIndicator()));
            this.plainIndicatorUnitMap.put(IndicatorEnum.UniqueIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.UniqueIndicatorEnum, countsIndicator.getUniqueCountIndicator()));
            break;
        case TextIndicatorEnum:
            TextIndicator textIndicator = (TextIndicator) indicator;
            this.plainIndicatorUnitMap.put(IndicatorEnum.TextIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.TextIndicatorEnum, textIndicator));

            // add indicatorUnit to indicatorUnitMap
            this.plainIndicatorUnitMap.put(IndicatorEnum.MinLengthIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.MinLengthIndicatorEnum, textIndicator.getMinLengthIndicator()));
            this.plainIndicatorUnitMap.put(IndicatorEnum.MaxLengthIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.MaxLengthIndicatorEnum, textIndicator.getMaxLengthIndicator()));
            this.plainIndicatorUnitMap.put(IndicatorEnum.AverageLengthIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.AverageLengthIndicatorEnum, textIndicator.getAverageLengthIndicator()));
            break;
        case BoxIIndicatorEnum:
            BoxIndicator boxtIndicator = (BoxIndicator) indicator;
            this.plainIndicatorUnitMap.put(IndicatorEnum.BoxIIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.BoxIIndicatorEnum, boxtIndicator));

            this.plainIndicatorUnitMap.put(IndicatorEnum.MeanIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.MeanIndicatorEnum, boxtIndicator.getMeanIndicator()));
            this.plainIndicatorUnitMap.put(IndicatorEnum.MedianIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.MedianIndicatorEnum, boxtIndicator.getMedianIndicator()));
            this.fillCategoryIndicators(IndicatorEnum.RangeIndicatorEnum, boxtIndicator.getRangeIndicator());
            this.fillCategoryIndicators(IndicatorEnum.IQRIndicatorEnum, boxtIndicator.getIQR());
            break;
        case IQRIndicatorEnum:
            IQRIndicator iqrIndicator = (IQRIndicator) indicator;
            this.plainIndicatorUnitMap.put(IndicatorEnum.IQRIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.IQRIndicatorEnum, iqrIndicator));

            this.plainIndicatorUnitMap.put(IndicatorEnum.LowerQuartileIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.LowerQuartileIndicatorEnum, iqrIndicator.getLowerValue()));
            this.plainIndicatorUnitMap.put(IndicatorEnum.UpperQuartileIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.UpperQuartileIndicatorEnum, iqrIndicator.getUpperValue()));
            break;
        case RangeIndicatorEnum:
            RangeIndicator rangeIndicator = (RangeIndicator) indicator;
            this.plainIndicatorUnitMap.put(IndicatorEnum.RangeIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.RangeIndicatorEnum, rangeIndicator));

            this.plainIndicatorUnitMap.put(IndicatorEnum.MaxValueIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.MaxValueIndicatorEnum, rangeIndicator.getUpperValue()));
            this.plainIndicatorUnitMap.put(IndicatorEnum.MinValueIndicatorEnum, createPlainIndicatorUnit(
                    IndicatorEnum.MinValueIndicatorEnum, rangeIndicator.getLowerValue()));
            break;
        default:
            this.plainIndicatorUnitMap.put(indicatorEnum, createPlainIndicatorUnit(indicatorEnum, indicator));
            break;
        }

    }

    /**
     * DOC rli Comment method "clear".
     */
    private void clear() {
        flatIndicatorEnumList.clear();
        plainIndicatorUnitMap.clear();
        if (specialIndicatorUnitList != null) {
            specialIndicatorUnitList.clear();
        }
    }

    public IndicatorUnit addSpecialIndicator(IndicatorEnum indicatorEnum, Indicator indicator) {
        return createSpecialIndicatorUnit(indicatorEnum, indicator);
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
        plainIndicatorUnits = createCategoryIndicatorUnits(categoryEnumList.toArray(new IndicatorEnum[categoryEnumList.size()]));
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
            if (!IndicatorEnum.isPlainIndicatorEnum(categoryEnum)) {
                continue;
            }
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
    private IndicatorUnit getIndicatorUnit(IndicatorEnum indicatorEnum) {
        IndicatorUnit indicatorUnit = this.plainIndicatorUnitMap.get(indicatorEnum);
        if (indicatorUnit == null) {
            indicatorUnit = createPlainIndicatorUnit(indicatorEnum, null);
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
    private IndicatorUnit createPlainIndicatorUnit(IndicatorEnum indicatorEnum, Indicator indicator) {
        if (indicator == null) {
            IndicatorsFactory factory = IndicatorsFactory.eINSTANCE;
            indicator = (Indicator) factory.create(indicatorEnum.getIndicatorType());

            // for 4225, the frequency indicator need be initialized
            int sqlType = this.tdColumn.getJavaType();
            if (indicatorEnum == IndicatorEnum.FrequencyIndicatorEnum && Java2SqlType.isDateInSQL(sqlType)) {
                IndicatorParameters parameters = indicator.getParameters();
                if (parameters == null) {
                    parameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
                    indicator.setParameters(parameters);
                }
                DateParameters dateParameters = parameters.getDateParameters();
                if (dateParameters == null) {
                    dateParameters = IndicatorsFactory.eINSTANCE.createDateParameters();
                    parameters.setDateParameters(dateParameters);
                }

                // MOD scorreia 2008-06-19 default is already set in the model
                // dateParameters.setDateAggregationType(DateGrain.YEAR);
            }
        }
        IndicatorUnit indicatorUnit = new IndicatorUnit(indicatorEnum, indicator, this);
        this.plainIndicatorUnitMap.put(indicatorEnum, indicatorUnit);
        return indicatorUnit;

    }

    private IndicatorUnit createSpecialIndicatorUnit(IndicatorEnum indicatorEnum, Indicator indicator) {
        if (indicator == null) {
            IndicatorsFactory factory = IndicatorsFactory.eINSTANCE;
            indicator = (Indicator) factory.create(indicatorEnum.getIndicatorType());
        }
        if (!flatIndicatorEnumList.contains(indicatorEnum)) {
            this.flatIndicatorEnumList.add(indicatorEnum);
        }
        if (this.specialIndicatorUnitList == null) {
            this.specialIndicatorUnitList = new ArrayList<IndicatorUnit>();
        }
        IndicatorUnit indicatorUnit = new IndicatorUnit(indicatorEnum, indicator, this);
        specialIndicatorUnitList.add(indicatorUnit);
        return indicatorUnit;
    }

    private void listCopy(List<IndicatorEnum> dest, List<IndicatorEnum> src) {
        dest.clear();
        for (IndicatorEnum indicatorEnum : src) {
            dest.add(indicatorEnum);
        }
    }
}
