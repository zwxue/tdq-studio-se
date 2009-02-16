// ============================================================================
//
// Copyright (C) 2006-2009 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.indicator.forms.impl;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.editor.analysis.AnalysisEditor;
import org.talend.dataprofiler.core.ui.editor.analysis.ColumnMasterDetailsPage;
import org.talend.dataprofiler.core.ui.utils.CheckValueUtils;
import org.talend.dataprofiler.core.ui.utils.DateTimeDialog;
import org.talend.dataprofiler.core.ui.utils.UIMessages;
import org.talend.dataprofiler.core.ui.wizard.indicator.forms.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.wizard.indicator.forms.FormEnum;
import org.talend.dataquality.domain.Domain;
import org.talend.dataquality.domain.RangeRestriction;
import org.talend.dataquality.helpers.AnalysisHelper;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dq.nodes.indicator.type.IndicatorEnum;
import org.talend.utils.sql.Java2SqlType;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class IndicatorThresholdsForm extends AbstractIndicatorForm {

    protected Text pLowerText, pHigherText;

    protected Text lowerText, higherText;

    private Button lowerBTN, higherBTN;

    private Button lowerDelBTN, higherDelBTN;

    private static final double MIN = 0;

    private static final double MAX = 100;

    private static final String VALUE_THRESHOLD = "Value Threshold"; //$NON-NLS-1$

    private static final String PERCENTAGE_THRESHOLD = "Percentage Threshold"; //$NON-NLS-1$

    private boolean isContainRowCount;

    private boolean isRangeForDate;

    private boolean isDatetime;

    private boolean isOptionForRowCount;

    public IndicatorThresholdsForm(Composite parent, int style, IndicatorParameters parameters) {
        super(parent, style, parameters);

        Indicator currentIndicator = (Indicator) parameters.eContainer();
        int sqltype = ((TdColumn) currentIndicator.getAnalyzedElement()).getJavaType();
        IndicatorEnum currentIndicatorType = IndicatorEnum.findIndicatorEnum(currentIndicator.eClass());
        isRangeForDate = Java2SqlType.isDateInSQL(sqltype) && currentIndicatorType.isAChildOf(IndicatorEnum.RangeIndicatorEnum);

        if (isRangeForDate) {
            isDatetime = Java2SqlType.isDateTimeSQL(sqltype);
        }

        isOptionForRowCount = currentIndicatorType == IndicatorEnum.RowCountIndicatorEnum;

        setupForm();
    }

    @Override
    protected void addFields() {
        int colsForLayout = 2;

        if (isRangeForDate) {
            colsForLayout = 4;
        }

        Group group = new Group(this, SWT.NONE);
        group.setLayout(new GridLayout(colsForLayout, false));
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.setThresholds")); //$NON-NLS-1$

        GridData gdText = new GridData(GridData.FILL_HORIZONTAL);

        Label lowerLabel = new Label(group, SWT.NONE);
        lowerLabel.setText(DefaultMessagesImpl.getString("DataThresholdsForm.lowerThreshold")); //$NON-NLS-1$
        lowerText = new Text(group, SWT.BORDER);
        lowerText.setLayoutData(gdText);
        if (isRangeForDate) {
            lowerBTN = new Button(group, SWT.PUSH);
            lowerBTN.setText("..."); //$NON-NLS-1$
            lowerDelBTN = new Button(group, SWT.PUSH);
            lowerDelBTN.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));

            lowerText.setEditable(false);
            lowerText.setEnabled(false);
        }

        Label higherLabel = new Label(group, SWT.NONE);
        higherLabel.setText(DefaultMessagesImpl.getString("DataThresholdsForm.higherThreshold")); //$NON-NLS-1$
        higherText = new Text(group, SWT.BORDER);
        higherText.setLayoutData(gdText);
        if (isRangeForDate) {
            higherBTN = new Button(group, SWT.PUSH);
            higherBTN.setText("..."); //$NON-NLS-1$
            higherDelBTN = new Button(group, SWT.PUSH);
            higherDelBTN.setImage(ImageLib.getImage(ImageLib.DELETE_ACTION));

            higherText.setEditable(false);
            higherText.setEnabled(false);
        }

        if (!isOptionForRowCount && !isRangeForDate) {
            Group pGroup = new Group(this, SWT.NONE);
            pGroup.setLayout(new GridLayout(2, false));
            pGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            pGroup.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.setPersentThresholds")); //$NON-NLS-1$

            Label pLower = new Label(pGroup, SWT.NONE);
            pLower.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.lowerThreshold")); //$NON-NLS-1$
            pLowerText = new Text(pGroup, SWT.BORDER);
            pLowerText.setLayoutData(gdText);

            Label pHigher = new Label(pGroup, SWT.NONE);
            pHigher.setText(DefaultMessagesImpl.getString("IndicatorThresholdsForm.higherThreshold")); //$NON-NLS-1$
            pHigherText = new Text(pGroup, SWT.BORDER);
            pHigherText.setLayoutData(gdText);

            setPercentUIEnable();
        }
    }

    private void setPercentUIEnable() {
        IEditorPart editor = CorePlugin.getDefault().getCurrentActiveEditor();
        ColumnMasterDetailsPage masterPage = null;
        AnalysisEditor anaEditor = null;
        if (editor != null) {
            anaEditor = (AnalysisEditor) editor;
            if (anaEditor.getMasterPage() != null) {
                masterPage = (ColumnMasterDetailsPage) anaEditor.getMasterPage();
            }
        }

        if (masterPage != null) {
            isContainRowCount = AnalysisHelper.containsRowCount(masterPage.getAnalysisHandler().getAnalysis());
        }

        pLowerText.setEnabled(isContainRowCount);
        pHigherText.setEnabled(isContainRowCount);
    }

    @Override
    public FormEnum getFormEnum() {
        return FormEnum.IndicatorThresholdsForm;
    }

    @Override
    public boolean performFinish() {
        boolean isMinEmpty = CheckValueUtils.isEmpty(lowerText.getText());
        boolean isMaxEmpty = CheckValueUtils.isEmpty(higherText.getText());
        if (isContainRowCount) {
            boolean isPerMinEmpty = CheckValueUtils.isEmpty(pLowerText.getText());
            boolean isPerMaxEmpty = CheckValueUtils.isEmpty(pHigherText.getText());

            if (isMinEmpty && isMaxEmpty && isPerMinEmpty && isPerMaxEmpty) {
                parameters.setIndicatorValidDomain(null);
            } else {
                if (isMinEmpty && isMaxEmpty) {
                    removeRange(VALUE_THRESHOLD);
                } else {
                    IndicatorHelper.setIndicatorThreshold(parameters, lowerText.getText(), higherText.getText());
                }

                if (isPerMinEmpty && isPerMaxEmpty) {
                    removeRange(PERCENTAGE_THRESHOLD);
                } else {
                    IndicatorHelper.setIndicatorThresholdInPercent(parameters, pLowerText.getText(), pHigherText.getText());
                }
            }

        } else {
            if (isMinEmpty && isMaxEmpty) {
                parameters.setIndicatorValidDomain(null);
            } else {
                IndicatorHelper.setIndicatorThreshold(parameters, lowerText.getText(), higherText.getText());
            }
        }

        return true;
    }

    private void removeRange(String rangeName) {
        Domain validDomain = parameters.getIndicatorValidDomain();
        if (validDomain != null) {
            Iterator<RangeRestriction> it = validDomain.getRanges().iterator();
            while (it.hasNext()) {
                if (rangeName.equals(it.next().getName())) {
                    it.remove();
                }
            }
        }
    }

    @Override
    protected void adaptFormToReadOnly() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void addFieldsListeners() {
        lowerText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String min = lowerText.getText();
                String max = higherText.getText();

                if (!CheckValueUtils.isEmpty(min)) {
                    if (isRangeForDate) {
                        if (!CheckValueUtils.isDateValue(min)) {
                            updateStatus(IStatus.ERROR, MSG_ONLY_DATE);
                        } else {
                            updateStatus(IStatus.OK, MSG_OK);
                        }
                    } else if (!CheckValueUtils.isNumberWithNegativeValue(min)) {
                        updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
                    } else if (!CheckValueUtils.isEmpty(max) && CheckValueUtils.isAoverB(min, max)) {
                        updateStatus(IStatus.ERROR, UIMessages.MSG_LOWER_LESS_HIGHER);
                    } else {
                        updateStatus(IStatus.OK, MSG_OK);
                    }
                } else {
                    updateStatus(IStatus.OK, UIMessages.MSG_INDICATOR_WIZARD);
                }
            }

        });

        higherText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                String min = lowerText.getText();
                String max = higherText.getText();

                if (!CheckValueUtils.isEmpty(max)) {
                    if (isRangeForDate) {
                        if (!CheckValueUtils.isDateValue(max)) {
                            updateStatus(IStatus.ERROR, MSG_ONLY_DATE);
                        } else {
                            updateStatus(IStatus.OK, MSG_OK);
                        }
                    } else if (!CheckValueUtils.isNumberWithNegativeValue(max)) {
                        updateStatus(IStatus.ERROR, MSG_ONLY_NUMBER);
                    } else if (!CheckValueUtils.isEmpty(min) && CheckValueUtils.isAoverB(min, max)) {
                        updateStatus(IStatus.ERROR, UIMessages.MSG_LOWER_LESS_HIGHER);
                    } else {
                        updateStatus(IStatus.OK, MSG_OK);
                    }
                } else {
                    updateStatus(IStatus.OK, UIMessages.MSG_INDICATOR_WIZARD);
                }
            }

        });

        if (isContainRowCount) {
            pLowerText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    String pmin = pLowerText.getText();
                    String pmax = pHigherText.getText();

                    if (!CheckValueUtils.isEmpty(pmin)) {
                        if (!CheckValueUtils.isRealNumberValue(pmin)) {
                            updateStatus(IStatus.ERROR, MSG_ONLY_REAL_NUMBER);
                        } else if (CheckValueUtils.isOutRange(MIN, MAX, pmin)) {
                            updateStatus(IStatus.ERROR, UIMessages.MSG_INDICATOR_VALUE_OUT_OF_RANGE);
                        } else if (!CheckValueUtils.isEmpty(pmax) && CheckValueUtils.isAoverB(pmin, pmax)) {
                            updateStatus(IStatus.ERROR, UIMessages.MSG_LOWER_LESS_HIGHER);
                        } else {
                            updateStatus(IStatus.OK, MSG_OK);
                        }
                    } else {
                        updateStatus(IStatus.OK, UIMessages.MSG_INDICATOR_WIZARD);
                    }
                }
            });

            pHigherText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    String pmin = pLowerText.getText();
                    String pmax = pHigherText.getText();

                    if (!CheckValueUtils.isEmpty(pmax)) {
                        if (!CheckValueUtils.isRealNumberValue(pmax)) {
                            updateStatus(IStatus.ERROR, MSG_ONLY_REAL_NUMBER);
                        } else if (CheckValueUtils.isOutRange(MIN, MAX, pmax)) {
                            updateStatus(IStatus.ERROR, UIMessages.MSG_INDICATOR_VALUE_OUT_OF_RANGE);
                        } else if (!CheckValueUtils.isEmpty(pmin) && CheckValueUtils.isAoverB(pmin, pmax)) {
                            updateStatus(IStatus.ERROR, UIMessages.MSG_LOWER_LESS_HIGHER);
                        } else {
                            updateStatus(IStatus.OK, MSG_OK);
                        }
                    } else {
                        updateStatus(IStatus.OK, UIMessages.MSG_INDICATOR_WIZARD);
                    }

                }
            });
        }
    }

    @Override
    protected void addUtilsButtonListeners() {
        if (isRangeForDate) {

            lowerBTN.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    DateTimeDialog dialog = new DateTimeDialog(null, isDatetime);
                    if (Window.OK == dialog.open()) {
                        lowerText.setText(dialog.getSelectDate());
                    }
                }
            });

            higherBTN.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    DateTimeDialog dialog = new DateTimeDialog(null, isDatetime);
                    if (Window.OK == dialog.open()) {
                        higherText.setText(dialog.getSelectDate());
                    }
                }
            });

            lowerDelBTN.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    lowerText.setText(""); //$NON-NLS-1$
                }
            });

            higherDelBTN.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    higherText.setText(""); //$NON-NLS-1$
                }
            });
        }
    }

    @Override
    protected boolean checkFieldsValue() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void initialize() {
        String[] indicatorThreshold = IndicatorHelper.getIndicatorThreshold(parameters);
        if (indicatorThreshold != null) {
            lowerText.setText(indicatorThreshold[0]);
            higherText.setText(indicatorThreshold[1]);
        }
        String[] indicatorPersentThreshold = IndicatorHelper.getIndicatorThresholdInPercent(parameters);
        if (indicatorPersentThreshold != null && isContainRowCount) {
            pLowerText.setText(indicatorPersentThreshold[0]);
            pHigherText.setText(indicatorPersentThreshold[1]);
        }
    }
}
