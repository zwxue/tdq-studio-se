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

import org.eclipse.jface.wizard.Wizard;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.utils.FormEnum;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.BinsDesignerParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.DataThresholdsParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.ExpectedValueParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.IndicatorThresholdsParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.NumbericNominalParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TextLengthParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TextParameter;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TimeSlicesParameter;
import org.talend.dataquality.domain.Domain;
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

    private Indicator indicator;

    private IndicatorUnit indicatorUnit;

    private Map<FormEnum, AbstractIndicatorParameter> paramMap = new HashMap<FormEnum, AbstractIndicatorParameter>();

    /**
     * DOC zqin IndicatorOptionsWizard constructor comment.
     */
    public IndicatorOptionsWizard(IndicatorUnit indicatorUnit) {
        setWindowTitle("Indicator");

        this.indicatorUnit = indicatorUnit;
        this.indicator = indicatorUnit.getIndicator();

        initWizard();
    }

    private void initWizard() {

        if (!AbstractIndicatorForm.isParametersEmpty()) {
            AbstractIndicatorForm.emptyParameterList();
        }

        IndicatorParameters indicatorParam = indicator.getParameters();
        if (indicatorParam != null) {

            int topN = indicatorParam.getTopN();

            NumbericNominalParameter numbericNominalParameter = new NumbericNominalParameter();
            numbericNominalParameter.setNumberOfShown(topN);

            BinsDesignerParameter binsParam = new BinsDesignerParameter();
            binsParam.setNumOfShown(topN);

            TimeSlicesParameter timeParam = new TimeSlicesParameter();
            timeParam.setNumOfShown(topN);

            paramMap.put(FormEnum.NumbericNominalForm, numbericNominalParameter);

            if (indicatorParam.getTextParameter() != null) {

                TextParameter textParam = new TextParameter();
                textParam.setIngoreCase(indicatorParam.getTextParameter().isIgnoreCase());
                textParam.setNumOfShown(topN);

                TextLengthParameter textLengthParam = new TextLengthParameter();
                textLengthParam.setUseBlank(indicatorParam.getTextParameter().isUseBlank());
                textLengthParam.setUseNull(indicatorParam.getTextParameter().isUseNulls());

                paramMap.put(FormEnum.TextParametersForm, textParam);
                paramMap.put(FormEnum.TextLengthForm, textLengthParam);
                paramMap.put(FormEnum.FreqTextLengthForm, textLengthParam);
            }

            if (IndicatorHelper.getDataThreshold(indicator) != null) {

                DataThresholdsParameter dataParam = new DataThresholdsParameter();
                dataParam.setMinThreshold(IndicatorHelper.getDataThreshold(indicator)[0]);
                dataParam.setMaxThreshold(IndicatorHelper.getDataThreshold(indicator)[1]);

                paramMap.put(FormEnum.DataThresholdsForm, dataParam);
            }

            if (IndicatorHelper.getIndicatorThreshold(indicator) != null) {
                IndicatorThresholdsParameter indicatorThresholdsParam = new IndicatorThresholdsParameter();
                indicatorThresholdsParam.setMinThreshold(IndicatorHelper.getIndicatorThreshold(indicator)[0]);
                indicatorThresholdsParam.setMaxThreshold(IndicatorHelper.getIndicatorThreshold(indicator)[1]);

                paramMap.put(FormEnum.IndicatorThresholdsForm, indicatorThresholdsParam);
            }

            if (IndicatorHelper.getExpectedValue(indicator) != null) {
                ExpectedValueParameter expectedParam = new ExpectedValueParameter();
                expectedParam.setExpectedValue(IndicatorHelper.getExpectedValue(indicator));
                paramMap.put(FormEnum.ExpectedValueForm, expectedParam);
            }

            Domain domain = indicatorParam.getBins();
            if (domain != null) {

                binsParam.setDomain(domain);
                binsParam.setMaxValue(DomainHelper.getMaxBinValue(domain));
                binsParam.setMinValue(DomainHelper.getMinBinValue(domain));
                binsParam.setNumOfBins(DomainHelper.getNumberOfBins(domain));
            }
            paramMap.put(FormEnum.BinsDesignerForm, binsParam);

            if (indicatorParam.getDateParameters() != null) {
                timeParam.setDataUnit(indicatorParam.getDateParameters().getDateAggregationType().getLiteral());
            }
            paramMap.put(FormEnum.TimeSlicesForm, timeParam);
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

            IndicatorParameters parameters = indicator.getParameters();

            if (parameters == null) {
                isDirty = true;
                parameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
                indicator.setParameters(parameters);
            }

            DateParameters dateParameters = parameters.getDateParameters();
            TextParameters textParameters = parameters.getTextParameter();

            for (AbstractIndicatorParameter formParam : AbstractIndicatorForm.getParameters()) {

                if (!ParamCompareFactory.compare(parameters, formParam)) {
                    isDirty = true;

                    switch (formParam.getFormEnum()) {
                    case BinsDesignerForm:
                        BinsDesignerParameter tempParam = (BinsDesignerParameter) formParam;
                        int numOfShown = tempParam.getNumOfShown();
                        Domain domain = tempParam.getUserDomian();

                        if (domain.getRanges().size() == 0) {
                            parameters.setBins(null);
                        } else {
                            parameters.setBins(domain);
                        }

                        parameters.setTopN(numOfShown);
                        break;
                    case TextParametersForm:
                        if (textParameters == null) {
                            textParameters = IndicatorsFactory.eINSTANCE.createTextParameters();
                            parameters.setTextParameter(textParameters);
                        }
                        TextParameter textParam = (TextParameter) formParam;
                        textParameters.setIgnoreCase(textParam.isIngoreCase());
                        parameters.setTopN(textParam.getNumOfShown());
                        break;
                    case TextLengthForm:
                    case FreqTextLengthForm:
                        if (textParameters == null) {
                            textParameters = IndicatorsFactory.eINSTANCE.createTextParameters();
                            parameters.setTextParameter(textParameters);
                        }
                        TextLengthParameter lengthParam = (TextLengthParameter) formParam;
                        textParameters.setUseBlank(lengthParam.isUseBlank());
                        textParameters.setUseNulls(lengthParam.isUseNull());
                        break;
                    case DataThresholdsForm:
                        DataThresholdsParameter dataParam = (DataThresholdsParameter) formParam;
                        String min1 = dataParam.getMinThreshold();
                        String max1 = dataParam.getMaxThreshold();

                        if ("".equals(min1) && "".equals(max1)) {
                            parameters.setDataValidDomain(null);
                            IndicatorHelper.propagateDataThresholdsInChildren(indicator);
                        } else {
                            IndicatorHelper.setDataThreshold(indicator, min1, max1);
                            IndicatorHelper.propagateDataThresholdsInChildren(indicator);
                        }

                        break;
                    case IndicatorThresholdsForm:
                        IndicatorThresholdsParameter indiParam = (IndicatorThresholdsParameter) formParam;
                        String min2 = indiParam.getMinThreshold();
                        String max2 = indiParam.getMaxThreshold();

                        if ("".equals(min2) && "".equals(max2)) {
                            parameters.setIndicatorValidDomain(null);
                        } else {
                            IndicatorHelper.setIndicatorThreshold(parameters, min2, max2);
                        }

                        break;
                    case TimeSlicesForm:
                        TimeSlicesParameter timeParam = (TimeSlicesParameter) formParam;

                        DateGrain dateGrain = DateGrain.get(timeParam.getDataUnit());
                        dateParameters.setDateAggregationType(dateGrain);
                        parameters.setTopN(timeParam.getNumOfShown());
                        break;
                    case NumbericNominalForm:
                        NumbericNominalParameter numbParam = (NumbericNominalParameter) formParam;
                        parameters.setTopN(numbParam.getNumberOfShown());
                        break;
                    case ExpectedValueForm:
                        ExpectedValueParameter expectedParam = (ExpectedValueParameter) formParam;
                        IndicatorHelper.setIndicatorExpectedValue(parameters, expectedParam.getExpectedValue());
                        break;
                    default:

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
