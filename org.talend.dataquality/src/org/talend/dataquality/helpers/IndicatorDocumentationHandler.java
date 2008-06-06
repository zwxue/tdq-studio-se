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
package org.talend.dataquality.helpers;

import java.net.URL;

import org.talend.dataquality.indicators.IndicatorsPackage;

/**
 * @author scorreia
 * 
 * This class gives access to the description and purpose of each indicator. The aim is to be able to get the
 * documentation without having to instantiate the indicators.
 * @deprecated this class is replaced by the IndicatorDefinition instances.
 */
public class IndicatorDocumentationHandler {

    private static final String DEFAULT_STRING = "";

    /**
     * Method "getLongDescription".
     * 
     * @param indicatorClassifierId the id of the indicator (should be one of the IndicatorsPackage constants)
     * @return the long description of the indicator represented by the given id or the empty string if not found.
     * @deprecated use TaggedValueHelper.getDescription(def) instead
     */
    public static String getLongDescription(int indicatorClassifierId) {
        switch (indicatorClassifierId) {
        case IndicatorsPackage.ROW_COUNT_INDICATOR:
            return "counts the number of rows";
        case IndicatorsPackage.NULL_COUNT_INDICATOR:
            return "counts the number of missing (null) instances";
        case IndicatorsPackage.DISTINCT_COUNT_INDICATOR:
            return "counts the number of distinct instances";
        case IndicatorsPackage.UNIQUE_COUNT_INDICATOR:
            return "counts the number of unique instances (i.e. instances that are not duplicated)";
        case IndicatorsPackage.DUPLICATE_COUNT_INDICATOR:
            return "counts the number of duplicates. this number and the number of unique data equals the number of rows";
        case IndicatorsPackage.BLANK_COUNT_INDICATOR:
            return "counts the number of blank instances. A blank is a non null textual data that contains only white spaces (or nothing)";
        case IndicatorsPackage.MIN_LENGTH_INDICATOR:
            return "computes the minimal length of a text field. Parameters can be set to avoid counting blank data or null data.";
        case IndicatorsPackage.MAX_LENGTH_INDICATOR:
            return "computes the maximal length of a text field. Parameters can be set to avoid counting blank data or null data.";
        case IndicatorsPackage.AVERAGE_LENGTH_INDICATOR:
            return "computes the average length of the field. Parameters can be set to avoid counting blank data or null data.";
        case IndicatorsPackage.MODE_INDICATOR:
            return "computes the mode (most probable value). For numerical data or continuous data, bins can be set in the parameters of this indicator. It is different from the average and from the median. Generalization of it is an histogram. It is good for addressing categorical attributes.";
        case IndicatorsPackage.FREQUENCY_INDICATOR:
            return "for each distinct instance, counts the number of instances.";
        case IndicatorsPackage.BOX_INDICATOR:
            return "computes the average, the median and the extent of the data";
        case IndicatorsPackage.MEAN_INDICATOR:
            return "computes the mean (average) of the instances";
        case IndicatorsPackage.MEDIAN_INDICATOR:
            return "computes the median. The median is the value separating the higher half of a sample, a population, or a probability distribution, from the lower half. ";
        case IndicatorsPackage.IQR_INDICATOR:
            return "the number separating the higher half of a sample, a population, or a probability distribution, from the lower half. It gives the spread of the main half data and is more stable than the range.";
        case IndicatorsPackage.RANGE_INDICATOR:
            return "difference between max and min, gives the extent of the data";
        default:
            break;
        }
        return DEFAULT_STRING;
    }

    /**
     * Method "getPurpose".
     * 
     * @param indicatorClassifierId the id of the indicator (should be one of the IndicatorsPackage constants)
     * @return the purpose of the indicator represented by the given id or the empty string if not found.
     * @deprecated use Indicator#getPurpose() instead
     */
    public static String getPurpose(int indicatorClassifierId) {
        switch (indicatorClassifierId) {
        case IndicatorsPackage.ROW_COUNT_INDICATOR:
            return "Evaluate the number of rows";
        case IndicatorsPackage.NULL_COUNT_INDICATOR:
            return "Evaluate the number of missing data";
        case IndicatorsPackage.DISTINCT_COUNT_INDICATOR:
            return "evaluate the number of distinct data";
        case IndicatorsPackage.UNIQUE_COUNT_INDICATOR:
            return "evaluate the number of unique data";
        case IndicatorsPackage.DUPLICATE_COUNT_INDICATOR:
            return "evaluate the number of duplicated data";
        case IndicatorsPackage.BLANK_COUNT_INDICATOR:
            return "evaluate the number of blank data";
        case IndicatorsPackage.MIN_LENGTH_INDICATOR:
            return "evaluate the length of the smallest textual data";
        case IndicatorsPackage.MAX_LENGTH_INDICATOR:
            return "evaluate the length of the longest textual data";
        case IndicatorsPackage.AVERAGE_LENGTH_INDICATOR:
            return "evaluate the average length of the textual data";
        case IndicatorsPackage.MODE_INDICATOR:
            return "evaluate the most frequent value";
        case IndicatorsPackage.FREQUENCY_INDICATOR:
            return "evaluate the count of each distinct value";
        case IndicatorsPackage.BOX_INDICATOR:
            return "gives a simple overview of the distribution of the data";
        case IndicatorsPackage.MEAN_INDICATOR:
            return "evaluate the mean value of the data";
        case IndicatorsPackage.MEDIAN_INDICATOR:
            return "evaluate the median of the data";
        case IndicatorsPackage.IQR_INDICATOR:
            return "measure the dispersion of the main half of the data";
        case IndicatorsPackage.RANGE_INDICATOR:
            return "measure the dispersion of data";
        default:
            break;
        }
        return DEFAULT_STRING;
    }

    /**
     * Method "getDefinitionURL".
     * 
     * @param indicatorClassifierId the id of the indicator (should be one of the IndicatorsPackage constants)
     * @return an URL with the definition of the indicator (generally a Wikipedia url) or null if no definition has been
     * set.
     */
    public static URL getDefinitionURL(int indicatorClassifierId) {
        switch (indicatorClassifierId) {

        case IndicatorsPackage.ROW_COUNT_INDICATOR:
        case IndicatorsPackage.NULL_COUNT_INDICATOR:
        case IndicatorsPackage.DISTINCT_COUNT_INDICATOR:
        case IndicatorsPackage.UNIQUE_COUNT_INDICATOR:
        case IndicatorsPackage.DUPLICATE_COUNT_INDICATOR:
        case IndicatorsPackage.BLANK_COUNT_INDICATOR:
        case IndicatorsPackage.MIN_LENGTH_INDICATOR:
        case IndicatorsPackage.MAX_LENGTH_INDICATOR:
        case IndicatorsPackage.AVERAGE_LENGTH_INDICATOR:
        case IndicatorsPackage.MODE_INDICATOR:
        case IndicatorsPackage.FREQUENCY_INDICATOR:
        case IndicatorsPackage.BOX_INDICATOR:
        case IndicatorsPackage.MEAN_INDICATOR:
        case IndicatorsPackage.MEDIAN_INDICATOR:
        case IndicatorsPackage.IQR_INDICATOR:
        case IndicatorsPackage.RANGE_INDICATOR:

            break;

        default:
            break;
        }

        // URL url = new URL(spec);
        // TODO scorreia implement me when needed
        return null;
    }

    /**
     * Method "getName".
     * 
     * @param indicatorClassifierId the id of the indicator (should be one of the IndicatorsPackage constants)
     * @return the name of the indicator.
     * @deprecated use Indicator#getName() instead
     */
    public static String getName(int indicatorClassifierId) {
        switch (indicatorClassifierId) {

        case IndicatorsPackage.ROW_COUNT_INDICATOR:
            return "Row count";
        case IndicatorsPackage.NULL_COUNT_INDICATOR:
            return "Null count";
        case IndicatorsPackage.DISTINCT_COUNT_INDICATOR:
            return "Distinct count";
        case IndicatorsPackage.UNIQUE_COUNT_INDICATOR:
            return "Unique count";
        case IndicatorsPackage.DUPLICATE_COUNT_INDICATOR:
            return "Duplicate count";
        case IndicatorsPackage.BLANK_COUNT_INDICATOR:
            return "Blank count";
        case IndicatorsPackage.MIN_LENGTH_INDICATOR:
            return "Minimal length";
        case IndicatorsPackage.MAX_LENGTH_INDICATOR:
            return "Maximal length";
        case IndicatorsPackage.AVERAGE_LENGTH_INDICATOR:
            return "Average length";
        case IndicatorsPackage.MODE_INDICATOR:
            return "Mode";
        case IndicatorsPackage.FREQUENCY_INDICATOR:
            return "Frequency table";
        case IndicatorsPackage.BOX_INDICATOR:
            return "Summary statistics";
        case IndicatorsPackage.MEAN_INDICATOR:
            return "Mean";
        case IndicatorsPackage.MEDIAN_INDICATOR:
            return "Median";
        case IndicatorsPackage.IQR_INDICATOR:
            return "Inter quartile range";
        case IndicatorsPackage.RANGE_INDICATOR:
            return "Range";
        default:
            break;
        }
        return DEFAULT_STRING;
    }
}
