// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.dataprofiler.core.ui.wizard.database;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizardPage;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class MetaDataFilterWizardPage extends AbstractWizardPage {

    private static final String MSG_FILTER_VALID = DefaultMessagesImpl.getString("TableViewColumnFilterWizardPage.filterValid"); //$NON-NLS-1$

    private static final String MSG_FILTER_INVALID = DefaultMessagesImpl
            .getString("TableViewColumnFilterWizardPage.filterInvalid"); //$NON-NLS-1$

    private MetaDataFilterWizard parent;

    private Text metaDataFilterText;

    private FilterType filterType;

    public Text getMetadataFilterText() {
        return metaDataFilterText;
    }

    public void setMetadataFilterText(Text metaDataFilterText) {
        this.metaDataFilterText = metaDataFilterText;
    }

    public MetaDataFilterWizardPage() {
        super();
    }

    public MetaDataFilterWizardPage(MetaDataFilterWizard parent) {
        this();
        this.parent = parent;
    }

    public void createControl(Composite parent) {
        Composite comp = new Composite(parent, SWT.NONE);
        GridLayout grid = new GridLayout(2, false);
        comp.setLayout(grid);

        GridData gd = new GridData();
        gd.widthHint = 280;
        gd.heightHint = 15;

        Label l1 = new Label(comp, SWT.NONE);
        l1.setText(DefaultMessagesImpl.getString("MetaDataFilterWizardPage.conn")); //$NON-NLS-1$

        String temp = "";//$NON-NLS-1$
        switch (getFilterType()) {
        case PACKAGE_FILTER:
            initPackageFilterComposite(comp, gd);
            temp = this.parent.getOldPackageFilter();
            break;
        case COLUMN_FILTER:
            initColumnFilterComposite(comp, gd);
            temp = this.parent.getOldColumnFilter();
        default:
            break;
        }
        metaDataFilterText = new Text(comp, SWT.BORDER);
        temp = temp == null ? "" : temp; //$NON-NLS-1$
        metaDataFilterText.setText(temp);
        metaDataFilterText.setLayoutData(gd);

        addFieldsListeners();
        this.setControl(comp);
    }

    private void initPackageFilterComposite(Composite comp, GridData gd) {
        Label t11 = new Label(comp, SWT.BORDER);
        t11.setText(this.parent.getDatabaseConnection().getName());
        t11.setLayoutData(gd);
        Label label1 = new Label(comp, SWT.NONE);
        label1.setText(DefaultMessagesImpl.getString("MetaDataFilterWizard.PackageFilter")); //$NON-NLS-1$
    }

    private void initColumnFilterComposite(Composite comp, GridData gd) {
        Label t11 = new Label(comp, SWT.BORDER);
        t11.setText(this.parent.getTdDataProvider().getName());
        t11.setLayoutData(gd);

        Label l2 = new Label(comp, SWT.NONE);
        l2.setText(DefaultMessagesImpl.getString("MetaDataFilterWizardPage.catalog")); //$NON-NLS-1$

        Label t22 = new Label(comp, SWT.BORDER);
        t22.setText(this.parent.getPackageObj().getName());
        t22.setLayoutData(gd);

        Label l3 = new Label(comp, SWT.NONE);
        l3.setText(DefaultMessagesImpl.getString("MetaDataFilterWizardPage.table")); //$NON-NLS-1$

        Label t33 = new Label(comp, SWT.BORDER);
        t33.setText(this.parent.getNamedColumnSet().getName());
        t33.setLayoutData(gd);
        Label label1 = new Label(comp, SWT.NONE);
        label1.setText(DefaultMessagesImpl.getString("MetaDataFilterWizardPage.columnFilter")); //$NON-NLS-1$
    }

    @Override
    public boolean checkFieldsValue() {
        String metadataFilter = this.metaDataFilterText.getText();
        if (metadataFilter.indexOf("\\") > -1 || metadataFilter.indexOf("/") > -1) { //$NON-NLS-1$ //$NON-NLS-2$
            return false;
        }
        return true;
    }

    private void addFieldsListeners() {
        metaDataFilterText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                if (checkFieldsValue()) {
                    updateStatus(IStatus.OK, MSG_FILTER_VALID);
                } else {
                    updateStatus(IStatus.ERROR, MSG_FILTER_INVALID);
                }
            }
        });
    }

    /**
     * 
     * This method is uesd for identifying the kind of Meta Data.
     */
    public enum FilterType {
        PACKAGE_FILTER,
        COLUMN_FILTER;
    }

    public FilterType getFilterType() {
        return this.filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }
}
