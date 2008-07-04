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
package org.talend.dataprofiler.core.ui.wizard.indicator;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.wizard.Wizard;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.utils.FormEnum;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.BinsDesignerParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.DataThresholdsParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TextLengthParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TextParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TimeSlicesParameter;
import org.talend.dataquality.analysis.Analysis;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.domain.RealNumberValue;
import org.talend.dataquality.domain.TextValue;
import org.talend.dataquality.helpers.DomainHelper;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.TextParameters;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class IndicatorOptionsWizard extends Wizard {

    private boolean isDirty;

    private Analysis analysis;

    private IndicatorUnit indicatorUnit;

    private Indicator indicator;

    private Map<String, AbstractIndicatorParameter> paramMap;

    /**
     * DOC zqin IndicatorOptionsWizard constructor comment.
     */
    public IndicatorOptionsWizard(IndicatorUnit indicatorUnit, Analysis analysis) {
        setWindowTitle("Indicator");

        this.indicatorUnit = indicatorUnit;
        this.analysis = analysis;
        this.indicator = indicatorUnit.getIndicator();

        initWizard();
    }

    private void initWizard() {

        if (!AbstractIndicatorForm.getTheParameter().isEmpty()) {
            AbstractIndicatorForm.emptyParameterList();
        }

        IndicatorParameters indicatorParam = indicatorUnit.getIndicator().getParameters();
        if (indicatorParam != null) {

            paramMap = new HashMap<String, AbstractIndicatorParameter>();

            TextParameters textParameters = indicatorParam.getTextParameter();

            if (textParameters != null) {

                TextParameter textParam = new TextParameter();
                textParam.setIngoreCase(textParameters.isIgnoreCase());
                textParam.setNumOfShown(indicatorParam.getTopN());

                TextLengthParameter textLengthParam = new TextLengthParameter();
                textLengthParam.setUseBlank(textParameters.isUseBlank());
                textLengthParam.setUseNull(textParameters.isUseNulls());

                paramMap.put(FormEnum.TextParametersForm.getFormName(), textParam);
                paramMap.put(FormEnum.TextLengthForm.getFormName(), textLengthParam);
            }

            if (IndicatorHelper.getDataThreshold(indicator) != null) {

                DataThresholdsParameter dataParam = new DataThresholdsParameter();
                dataParam.setMinThreshold(IndicatorHelper.getDataThreshold(indicator)[0]);
                dataParam.setMaxThreshold(IndicatorHelper.getDataThreshold(indicator)[1]);

                paramMap.put(FormEnum.DataThresholdsForm.getFormName(), dataParam);
            }

            if (indicatorParam.getBins() != null) {

                BinsDesignerParameter binsParam = new BinsDesignerParameter();
                binsParam.setMaxValue(DomainHelper.getMaxBinValue(indicatorParam.getBins()));
                binsParam.setMinValue(DomainHelper.getMinBinValue(indicatorParam.getBins()));
                binsParam.setNumOfBins(DomainHelper.getNumberOfBins(indicatorParam.getBins()));
                binsParam.setNumOfShown(indicatorParam.getTopN());

                paramMap.put(FormEnum.BinsDesignerForm.getFormName(), binsParam);
            }
            if (indicatorParam.getDateParameters() != null) {
                TimeSlicesParameter timeParam = new TimeSlicesParameter();
                timeParam.setDataUnit(indicatorParam.getDateParameters().getDateAggregationType().getLiteral());
                timeParam.setNumOfShown(indicatorParam.getTopN());

                paramMap.put(FormEnum.TimeSlicesForm.getFormName(), timeParam);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#performFinish()
     */
    @Override
    public boolean performFinish() {

        try {

            IndicatorParameters paramters = indicatorUnit.getIndicator().getParameters();

            if (paramters == null) {
                isDirty = true;
                paramters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
                indicatorUnit.getIndicator().setParameters(paramters);
            }

            TextParameters textParameters = paramters.getTextParameter();

            if (textParameters == null) {
                isDirty = true;
                textParameters = IndicatorsFactory.eINSTANCE.createTextParameters();
                paramters.setTextParameter(textParameters);
            }

            for (AbstractIndicatorParameter parameter : AbstractIndicatorForm.getTheParameter()) {

                if (parameter instanceof BinsDesignerParameter) {

                    BinsDesignerParameter tempParam = (BinsDesignerParameter) parameter;
                    int numOfBin = tempParam.getNumOfBins();
                    int numOfShown = tempParam.getNumOfShown();
                    double min = tempParam.getMinValue();
                    double max = tempParam.getMaxValue();
                    Domain domain = DomainHelper.createContiguousClosedBinsIntoDomain("test", numOfBin, min, max);
                    // MOD scorreia 2008-05-27 adding to EMF resource not needed because the relation is now a
                    // containment relation.
                    // Resource resource = analysis.eResource();
                    // resource.getContents().add(domain);
                    Domain bins = paramters.getBins();
                    boolean same = true;
                    if (bins != null) {
                        EList<RangeRestriction> ranges = bins.getRanges();
                        EList<RangeRestriction> ranges2 = domain.getRanges();
                        if (ranges.size() != ranges2.size()) {
                            same = false;
                        }
                        for (int i = 0; i < ranges2.size() && same; i++) {
                            RangeRestriction d2 = ranges2.get(i);
                            RangeRestriction d1 = ranges.get(i);
                            double v1 = ((RealNumberValue) d1.getLowerValue()).getValue();
                            double v2 = ((RealNumberValue) d2.getLowerValue()).getValue();
                            if (v1 != v2) {
                                same = false;
                                break;
                            }
                            v1 = ((RealNumberValue) d1.getUpperValue()).getValue();
                            v2 = ((RealNumberValue) d2.getUpperValue()).getValue();
                            if (v1 != v2) {
                                same = false;
                                break;
                            }
                        }
                    } else if (!domain.getRanges().isEmpty()) {
                        same = false;
                    }

                    if (paramters.getTopN() != tempParam.getNumOfShown()) {
                        same = false;
                    }

                    if (!same) {
                        isDirty = true;
                        paramters.setBins(domain);
                        paramters.setTopN(numOfShown);
                    }
                }

                if (parameter instanceof TextParameter) {

                    TextParameter tempParam = (TextParameter) parameter;
                    int numOfShown = paramters.getTopN();
                    // PTODO qzhang for bug 3491.
                    if (textParameters.isIgnoreCase() != tempParam.isIngoreCase() || numOfShown != tempParam.getNumOfShown()) {
                        isDirty = true;
                        textParameters.setIgnoreCase(tempParam.isIngoreCase());
                        paramters.setTopN(tempParam.getNumOfShown());
                    }
                }

                if (parameter instanceof TextLengthParameter) {

                    TextLengthParameter tempParam = (TextLengthParameter) parameter;
                    // PTODO qzhang for bug 3491.
                    if (textParameters.isUseBlank() != tempParam.isUseBlank()) {
                        isDirty = true;
                        textParameters.setUseBlank(tempParam.isUseBlank());
                    }
                    // PTODO qzhang for bug 3491.
                    if (textParameters.isUseNulls() != tempParam.isUseNull()) {
                        isDirty = true;
                        textParameters.setUseNulls(tempParam.isUseNull());
                    }
                }

                if (parameter instanceof DataThresholdsParameter) {
                    DataThresholdsParameter tempParam = (DataThresholdsParameter) parameter;
                    String min = tempParam.getMinThreshold();
                    String max = tempParam.getMaxThreshold();
                    // PTODO qzhang for bug 3491.
                    isDirty = indicatorUnit.getIndicator().getParameters() == null;
                    if (!isDirty) {
                        Domain validDomain = paramters.getDataValidDomain();
                        isDirty = validDomain == null;
                        if (!isDirty) {
                            int size = validDomain.getRanges().size();
                            isDirty = size != 1;
                            if (!isDirty) {
                                RangeRestriction rr = validDomain.getRanges().get(0);
                                TextValue lv = (TextValue) rr.getLowerValue();
                                TextValue uv = (TextValue) rr.getUpperValue();
                                if (!min.equals(lv.getValue())) {
                                    isDirty = true;
                                }
                                if (!max.equals(uv.getValue())) {
                                    isDirty = true;
                                }
                            }
                        }
                    }
                    if (isDirty) {
                        IndicatorHelper.setDataThreshold(indicatorUnit.getIndicator(), min, max);
                    }
                }

                if (parameter instanceof TimeSlicesParameter) {
                    DateParameters dateParameters = paramters.getDateParameters();
                    if (dateParameters == null) {
                        dateParameters = IndicatorsFactory.eINSTANCE.createDateParameters();
                        paramters.setDateParameters(dateParameters);
                        isDirty = true;
                    }
                    TimeSlicesParameter tempParam = (TimeSlicesParameter) parameter;
                    DateGrain dateGrain = DateGrain.get(tempParam.getDataUnit());
                    int numOfShown = paramters.getTopN();
                    // PTODO qzhang for bug 3491.
                    if (dateGrain.compareTo(dateParameters.getDateAggregationType()) != 0
                            || numOfShown != tempParam.getNumOfShown()) {
                        isDirty = true;
                        numOfShown = tempParam.getNumOfShown();
                        dateParameters.setDateAggregationType(dateGrain);
                        paramters.setTopN(numOfShown);
                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.wizard.Wizard#addPages()
     */
    @Override
    public void addPages() {

        DynamicIndicatorOptionsPage indicatorPage = new DynamicIndicatorOptionsPage(indicatorUnit, paramMap);

        addPage(indicatorPage);
    }

    /**
     * Getter for isDirty.
     * 
     * @return the isDirty
     */
    public boolean isDirty() {
        return this.isDirty;
    }

}
