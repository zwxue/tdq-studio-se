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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.PlatformUI;
import org.talend.dataprofiler.core.ui.editor.preview.IndicatorUnit;
import org.talend.dataprofiler.core.ui.utils.AbstractForm;
import org.talend.dataprofiler.core.ui.utils.AbstractIndicatorForm;
import org.talend.dataprofiler.core.ui.utils.FormEnum;
import org.talend.dataprofiler.core.ui.utils.FormFactory;
import org.talend.dataprofiler.core.ui.wizard.indicator.parameter.AbstractIndicatorParameter;
import org.talend.dataprofiler.help.HelpPlugin;

/**
 * DOC zqin class global comment. Detailled comment
 */
public class DynamicIndicatorOptionsPage extends WizardPage {

    private IndicatorUnit indicatorUnit;

    private TabFolder tabFolder;

    private Map<FormEnum, AbstractIndicatorParameter> paramMap;

    private AbstractIndicatorForm.ICheckListener listener;

    /**
     * DOC zqin DynamicIndicatorOptionsPage constructor comment.
     * 
     * @param pageName
     */
    public DynamicIndicatorOptionsPage(IndicatorUnit indicatorUnit, Map<FormEnum, AbstractIndicatorParameter> paramMap) {
        super("Indicator settings");

        this.indicatorUnit = indicatorUnit;
        this.paramMap = paramMap;

        setPageComplete(false);
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
        tabFolder.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                Object formObject = e.item.getData();
                if (formObject != null) {
                    AbstractIndicatorForm form = (AbstractIndicatorForm) formObject;
                    form.showHelp();
                }
            }

        });

        FormEnum[] forms = FormEnum.getForms(this.indicatorUnit);
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

    private Composite createView(AbstractIndicatorForm... forms) {
        try {
            for (AbstractIndicatorForm iForm : forms) {
                TabItem item = new TabItem(tabFolder, SWT.NONE);
                item.setData(iForm);
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
