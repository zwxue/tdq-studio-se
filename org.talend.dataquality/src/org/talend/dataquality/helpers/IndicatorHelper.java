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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.talend.dataquality.analysis.AnalysisResult;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;

/**
 * @author scorreia
 * 
 * Helper class for handling indicator attributes.
 */
public final class IndicatorHelper {

    private static Logger log = Logger.getLogger(IndicatorHelper.class);

    private IndicatorHelper() {
    }

    public static void setDataThreshold(Indicator indicator, String min, String max) {
        IndicatorParameters parameters = indicator.getParameters();
        if (parameters == null) {
            parameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
            indicator.setParameters(parameters);
        }
        setDataThreshold(parameters, min, max);
    }

    public static void setDataThreshold(IndicatorParameters parameters, String min, String max) {
        assert parameters != null;
        Domain validDomain = parameters.getDataValidDomain();
        if (validDomain == null) {
            validDomain = DomainHelper.createDomain("Data threshold");
            parameters.setDataValidDomain(validDomain);
        }
        // remove previous ranges
        assert validDomain.getRanges().size() < 2;
        validDomain.getRanges().clear();
        RangeRestriction rangeRestriction = DomainHelper.createStringRangeRestriction(min, max);
        validDomain.getRanges().add(rangeRestriction);
    }

    public static String[] getDataThreshold(Indicator indicator) {
        IndicatorParameters parameters = indicator.getParameters();
        if (parameters == null) {
            return new String[] { null, null };
        }
        return getDataThreshold(parameters);
    }

    public static void setIndicatorThreshold(IndicatorParameters parameters, String min, String max) {
        assert parameters != null;
        Domain validDomain = parameters.getIndicatorValidDomain();
        if (validDomain == null) {
            validDomain = DomainHelper.createDomain("Indicator threshold");
            parameters.setIndicatorValidDomain(validDomain);
        }
        // remove previous ranges
        assert validDomain.getRanges().size() < 2;
        validDomain.getRanges().clear();
        RangeRestriction rangeRestriction = DomainHelper.createStringRangeRestriction(min, max);
        validDomain.getRanges().add(rangeRestriction);
    }

    public static String[] getIndicatorThreshold(Indicator indicator) {
        IndicatorParameters parameters = indicator.getParameters();
        if (parameters == null) {
            return new String[] { null, null };
        }
        return getIndicatorThreshold(parameters);
    }

    public static String[] getIndicatorThreshold(IndicatorParameters parameters) {
        Domain validDomain = parameters.getIndicatorValidDomain();
        if (validDomain == null) {
            return null;
        }
        EList<RangeRestriction> ranges = validDomain.getRanges();
        if (ranges.size() != 1) {
            log.warn("Indicator threshold contain too many ranges (or no range): " + ranges.size() + " range(s).");
            return null;
        }
        RangeRestriction rangeRestriction = ranges.get(0);
        if (rangeRestriction == null) {
            return new String[] { null, null };
        }
        return new String[] { DomainHelper.getMinValue(rangeRestriction), DomainHelper.getMaxValue(rangeRestriction) };
    }

    /**
     * Method "getDataThreshold".
     * 
     * @param parameters
     * @return an array with two elements. returns null when no threshold has been found. One element of the array can
     * be null but not both. In this case, it means that there is only one defined threshold, either the upper or the
     * lower threshold.
     */
    public static String[] getDataThreshold(IndicatorParameters parameters) {
        Domain validDomain = parameters.getDataValidDomain();
        if (validDomain == null) {
            return null;
        }
        EList<RangeRestriction> ranges = validDomain.getRanges();
        if (ranges.size() != 1) {
            log.warn("Data threshold contain too many ranges (or no range): " + ranges.size() + " range(s).");
            return null;
        }
        RangeRestriction rangeRestriction = ranges.get(0);
        if (rangeRestriction == null) {
            return new String[] { null, null };
        }
        return new String[] { DomainHelper.getMinValue(rangeRestriction), DomainHelper.getMaxValue(rangeRestriction) };
    }

    /**
     * Method "getIndicatorLeaves" returns the leaf indicators when the given indicator is a composite indicator or the
     * given indicator.
     * 
     * @param indicator the indicator
     * @return the leaf indicators
     */
    public static List<Indicator> getIndicatorLeaves(Indicator indicator) {
        List<Indicator> leafIndicators = new ArrayList<Indicator>();
        if (indicator instanceof CompositeIndicator) {
            CompositeIndicator compositeIndicator = (CompositeIndicator) indicator;
            for (Indicator ind : compositeIndicator.getChildIndicators()) {
                leafIndicators.addAll(getIndicatorLeaves(ind));
            }
        } else {
            leafIndicators.add(indicator);
        }
        return leafIndicators;
    }

    /**
     * Method "getIndicatorLeaves".
     * 
     * @param result
     * @return all the leaf indicators
     */
    public static List<Indicator> getIndicatorLeaves(AnalysisResult result) {
        List<Indicator> leafIndicators = new ArrayList<Indicator>();
        EList<Indicator> indicators = result.getIndicators();
        for (Indicator indicator : indicators) {
            leafIndicators.addAll(getIndicatorLeaves(indicator));
        }
        return leafIndicators;
    }

}
