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
package org.talend.dataprofiler.core.ui.action.actions.predefined;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction;
import org.talend.dataprofiler.core.ui.utils.AbstractForm;
import org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.utils.FormEnum;
import org.talend.dataprofiler.core.ui.utils.OpeningHelpWizardDialog;
import org.talend.dataprofiler.core.ui.wizard.analysis.WizardFactory;
import org.talend.dataprofiler.core.ui.wizard.analysis.column.ColumnWizard;
import org.talend.dataprofiler.core.ui.wizard.indicator.FreqTimeSliceForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.TimeSlicesParameter;
import org.talend.dataquality.analysis.AnalysisType;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dq.analysis.parameters.AnalysisParameter;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class CreateDateAnalysisAction extends AbstractPredefinedAnalysisAction {

    private TimeSlicesParameter patameter;

    public CreateDateAnalysisAction() {
        super("Time analysis", null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction#getPredefinedColumnIndicator()
     */
    @Override
    protected ColumnIndicator[] getPredefinedColumnIndicator() {

        IndicatorEnum[] allwedEnumes = new IndicatorEnum[2];
        allwedEnumes[0] = IndicatorEnum.CountsIndicatorEnum;
        allwedEnumes[1] = IndicatorEnum.FrequencyIndicatorEnum;

        ColumnIndicator[] returnColumnIndicator = composePredefinedColumnIndicator(allwedEnumes);

        if (patameter != null) {
            for (ColumnIndicator columnIndicator : returnColumnIndicator) {
                for (Indicator indicator : columnIndicator.getIndicators()) {
                    if (indicator instanceof FrequencyIndicator) {
                        IndicatorParameters indicatorParameters = indicator.getParameters();
                        if (indicatorParameters == null) {
                            indicatorParameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
                            indicator.setParameters(indicatorParameters);
                        }

                        indicatorParameters.setTopN(patameter.getNumOfShown());

                        DateParameters dateParameters = indicatorParameters.getDateParameters();
                        if (dateParameters == null) {
                            dateParameters = IndicatorsFactory.eINSTANCE.createDateParameters();
                            indicatorParameters.setDateParameters(dateParameters);
                        }

                        dateParameters.setDateAggregationType(DateGrain.get(patameter.getDataUnit()));
                    }
                }
            }
        }

        return returnColumnIndicator;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction#isAllowed()
     */
    @Override
    protected boolean isAllowed() {
        for (TdColumn column : getColumns()) {
            if (!Java2SqlType.isDateInSQL(column.getJavaType())) {
                return false;
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataprofiler.core.ui.action.AbstractPredefinedAnalysisAction#preDo()
     */
    @Override
    protected boolean preDo() {

        return true;
    }

    /**
     * DOC zqin CreateDateAnalysisAction class global comment. Detailled comment
     */
    public class TimeSliceOptionPage extends WizardPage {

        private AbstractIndicatorForm.ICheckListener listener;

        public TimeSliceOptionPage() {
            super("Creaete new analysis");
            setTitle("New Analysis");
            setDescription("add option to all frequency indicator.");

            patameter = new TimeSlicesParameter();
            patameter.setDataUnit(DateGrain.YEAR.getLiteral());

            this.listener = new AbstractForm.ICheckListener() {

                public void checkPerformed(AbstractForm source) {
                    if (source.isStatusOnError()) {
                        TimeSliceOptionPage.this.setPageComplete(false);
                        setErrorMessage(source.getStatus());
                    } else {
                        TimeSliceOptionPage.this.setPageComplete(true);
                        setErrorMessage(null);
                        setMessage(source.getStatus(), source.getStatusLevel());
                    }
                }

            };
        }

        public void createControl(Composite parent) {

            Composite comp = new Composite(parent, SWT.NONE);
            comp.setLayout(new GridLayout());
            comp.setLayoutData(new GridData(GridData.FILL_BOTH));

            FreqTimeSliceForm timeSliceForm = new FreqTimeSliceForm(comp, SWT.NONE, patameter);
            timeSliceForm.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            timeSliceForm.setListener(listener);

            setControl(comp);
        }

    }

    @Override
    protected WizardDialog getPredefinedDialog() {

        ColumnWizard wizard = (ColumnWizard) WizardFactory.createAnalysisWizard(AnalysisType.MULTIPLE_COLUMN,
                new AnalysisParameter());
        wizard.setForcePreviousAndNextButtons(true);
        TimeSliceOptionPage page = new TimeSliceOptionPage();
        wizard.setExtenalPages(new WizardPage[] { page });

        return new OpeningHelpWizardDialog(null, wizard, FormEnum.TimeSlicesForm.getHelpHref(), page);
    }

}
