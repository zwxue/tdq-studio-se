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

import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.core.model.ColumnIndicator;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.utils.AbstractForm;
import org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.utils.FormFactory;
import org.talend.dataprofiler.core.ui.utils.FormEnum;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;
import org.talend.dataprofiler.help.HelpPlugin;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class DynamicIndicatorOptionsPage extends WizardPage {

    private IndicatorUnit indicator;

    private ColumnIndicator parentColumn;

    private TabFolder tabFolder;

    private Map<FormEnum, AbstractIndicatorParameter> paramMap;

    private AbstractIndicatorForm.ICheckListener listener;

    /**
     * DOC zqin DynamicIndicatorOptionsPage constructor comment.
     * 
     * @param pageName
     */
    public DynamicIndicatorOptionsPage(IndicatorUnit indicator, Map<FormEnum, AbstractIndicatorParameter> paramMap) {
        super("Indicator settings");

        this.indicator = indicator;
        this.paramMap = paramMap;
        this.parentColumn = indicator.getParentColumn();
        setTitle("Indicator settings");
        setMessage("In this wizard, parameters for the given indicator can be set");

        this.listener = new AbstractForm.ICheckListener() {

            public void checkPerformed(AbstractForm source) {
                if (source.isStatusOnError()) {
                    DynamicIndicatorOptionsPage.this.setPageComplete(false);
                    setErrorMessage(source.getStatus());
                } else {
                    DynamicIndicatorOptionsPage.this.setPageComplete(true);
                    setErrorMessage(null);
                    setMessage(source.getStatus(), source.getStatusLevel());
                }
            }

        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout());

        tabFolder = new TabFolder(container, SWT.FLAT);
        tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

        DataminingType dataminingType = MetadataHelper.getDataminingType(parentColumn.getTdColumn());
        if (dataminingType == null) {
            dataminingType = MetadataHelper.getDefaultDataminingType(parentColumn.getTdColumn().getJavaType());
        }
        int sqlType = parentColumn.getTdColumn().getJavaType();

        FormEnum[] forms = getForms(dataminingType, sqlType);
        if (forms != null) {
            setControl(createView(FormFactory.createForm(tabFolder, listener, forms, paramMap)));
        }

        if (getControl() != null) {
            try {
                PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(),
                        HelpPlugin.PLUGIN_ID + HelpPlugin.INDICATOR_OPTION_HELP_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * DOC zqin Comment method "getForms".
     * 
     * @param dataminingType
     * @param sqlType
     */
    private FormEnum[] getForms(DataminingType dataminingType, int sqlType) {
        FormEnum[] forms = null;

        switch (indicator.getType()) {

        case DistinctCountIndicatorEnum:
        case UniqueIndicatorEnum:
        case DuplicateCountIndicatorEnum:

            if (Java2SqlType.isTextInSQL(sqlType)) {
                forms = new FormEnum[] { FormEnum.TextParametersForm, FormEnum.IndicatorThresholdsForm };
            }

            break;
        case MinLengthIndicatorEnum:
        case MaxLengthIndicatorEnum:
        case AverageLengthIndicatorEnum:

            forms = new FormEnum[] { FormEnum.TextLengthForm, FormEnum.IndicatorThresholdsForm };

            break;
        case FrequencyIndicatorEnum:
            if (dataminingType == DataminingType.INTERVAL) {
                if (Java2SqlType.isNumbericInSQL(sqlType)) {

                    forms = new FormEnum[] { FormEnum.FreqBinsDesignerForm };
                }

                if (Java2SqlType.isDateInSQL(sqlType)) {

                    forms = new FormEnum[] { FormEnum.FreqTimeSliceForm };
                }
            } else if (Java2SqlType.isTextInSQL(sqlType)) {

                forms = new FormEnum[] { FormEnum.FreqTextParametersForm, FormEnum.TextLengthForm };
            }

            break;
        case ModeIndicatorEnum:
            if (dataminingType == DataminingType.INTERVAL) {
                if (Java2SqlType.isNumbericInSQL(sqlType)) {

                    forms = new FormEnum[] { FormEnum.BinsDesignerForm };
                }
            } else if (Java2SqlType.isTextInSQL(sqlType)) {

                forms = new FormEnum[] { FormEnum.TextParametersForm };
            }

            break;
        case BoxIIndicatorEnum:
            forms = new FormEnum[] { FormEnum.DataThresholdsForm };

            break;
        case MeanIndicatorEnum:
        case MedianIndicatorEnum:
        case LowerQuartileIndicatorEnum:
        case UpperQuartileIndicatorEnum:
        case MinValueIndicatorEnum:
        case MaxValueIndicatorEnum:
            forms = new FormEnum[] { FormEnum.IndicatorThresholdsForm };

            break;

        case RegexpMatchingIndicatorEnum:
        case SqlPatternMatchingIndicatorEnum:
            forms = new FormEnum[] { FormEnum.IndicatorThresholdsForm };

            break;

        default:

        }

        return forms;
    }

    private Composite createView(AbstractIndicatorForm... forms) {
        try {
            for (AbstractIndicatorForm iForm : forms) {
                TabItem item = new TabItem(tabFolder, SWT.NONE);
                item.setText(iForm.getFormName());
                item.setControl(iForm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tabFolder;
    }

    @Override
    public void setErrorMessage(String newMessage) {
        super.setErrorMessage(newMessage);

        if (isCurrentPage()) {
            getContainer().updateMessage();
        }
    }

}
