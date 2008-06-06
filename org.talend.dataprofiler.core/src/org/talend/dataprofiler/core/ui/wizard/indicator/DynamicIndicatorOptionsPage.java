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
import org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;
import org.talend.dataprofiler.help.HelpPlugin;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class DynamicIndicatorOptionsPage extends WizardPage {

    private IndicatorUnit indicator;

    private ColumnIndicator parentColumn;

    private TabFolder tabFolder;

    private Map<String, AbstractIndicatorParameter> paramMap;

    /**
     * DOC zqin DynamicIndicatorOptionsPage constructor comment.
     * 
     * @param pageName
     */
    public DynamicIndicatorOptionsPage(IndicatorUnit indicator, Map<String, AbstractIndicatorParameter> paramMap) {
        super("Indicator settings");

        this.indicator = indicator;
        this.paramMap = paramMap;
        this.parentColumn = indicator.getParentColumn();
        setTitle("Indicator settings");
        setMessage("In this wizard, parameters for the given indicator can be set");

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
        String url = "";
        if (indicator != null) {

            int sqlType = parentColumn.getTdColumn().getJavaType();
            // System.out.println(sqlType);

            switch (indicator.getType()) {

            case DistinctCountIndicatorEnum:
            case UniqueIndicatorEnum:
            case DuplicateCountIndicatorEnum:

                if (Java2SqlType.isTextInSQL(sqlType)) {

                    setControl(createView(new TextParametersForm(tabFolder, SWT.NONE)));
                }
                break;
            case MinLengthIndicatorEnum:
            case MaxLengthIndicatorEnum:
            case AverageLengthIndicatorEnum:

                setControl(createView(new TextLengthForm(tabFolder, SWT.NONE)));

                break;
            case FrequencyIndicatorEnum:
                if (parentColumn.getDataminingType() == DataminingType.INTERVAL) {
                    if (Java2SqlType.isNumbericInSQL(sqlType)) {

                        setControl(createView(new BinsDesignerForm(tabFolder, SWT.NONE)));
                    }

                    if (Java2SqlType.isDateInSQL(sqlType)) {

                        setControl(createView(new TimeSlicesForm(tabFolder, SWT.NONE)));
                    }
                } else if (Java2SqlType.isTextInSQL(sqlType)) {

                    setControl(createView(new TextParametersForm(tabFolder, SWT.NONE)));
                }

                break;
            case ModeIndicatorEnum:
                if (parentColumn.getDataminingType() == DataminingType.INTERVAL) {
                    if (Java2SqlType.isNumbericInSQL(sqlType)) {

                        setControl(createView(new BinsDesignerForm(tabFolder, SWT.NONE)));
                    }
                } else if (Java2SqlType.isTextInSQL(sqlType)) {

                    setControl(createView(new TextParametersForm(tabFolder, SWT.NONE)));
                }

                break;
            case BoxIIndicatorEnum:

                setControl(createView(new DataThresholdsForm(tabFolder, SWT.NONE)));
                break;
            default:

            }
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

    private Composite createView(AbstractIndicatorForm... forms) {
        try {
            for (AbstractIndicatorForm form : forms) {
                TabItem item = new TabItem(tabFolder, SWT.NONE);
                item.setText(form.getFormName());
                item.setControl(form);

                if (paramMap == null) {
                    form.injectTheParameter(null);
                } else {
                    form.injectTheParameter(paramMap.get(form.getFormName()));
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tabFolder;
    }

}
