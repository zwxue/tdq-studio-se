// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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

import org.apache.log4j.Logger;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.properties.Item;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import org.talend.dq.helper.ProxyRepositoryViewObject;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableViewFilterWizard extends AbstractWizard {

    protected static Logger log = Logger.getLogger(TableViewFilterWizard.class);

    private TableViewFilterWizardPage tableViewFilterWizardPage;

    private Package packageObj;

    private Connection tdDataProvider;

    private String oldTableFilter, oldViewFilter;

    public String getOldTableFilter() {
        return oldTableFilter;
    }

    public void setOldTableFilter(String oldTableFilter) {
        this.oldTableFilter = oldTableFilter;
    }

    public String getOldViewFilter() {
        return oldViewFilter;
    }

    public void setOldViewFilter(String oldViewFilter) {
        this.oldViewFilter = oldViewFilter;
    }

    public Package getPackageObj() {
        return packageObj;
    }

    public void setPackageObj(Package packageObj) {
        this.packageObj = packageObj;
    }

    public Connection getTdDataProvider() {
        return tdDataProvider;
    }

    public void setTdDataProvider(Connection tdDataProvider) {
        this.tdDataProvider = tdDataProvider;
    }

    public TableViewFilterWizard() {
        super();
    }

    public TableViewFilterWizard(Package packageObj) {
        super();
        initAction(packageObj);
    }

    @Override
    protected String getEditorName() {
        return DefaultMessagesImpl.getString("TableViewFilterWizard.Table/ViewFilter"); //$NON-NLS-1$
    }

    @Override
    public boolean performFinish() {
        boolean needSave = false;

        String tableFilter = this.tableViewFilterWizardPage.getTableFilterText().getText();
        if (!this.getOldTableFilter().equals(tableFilter)) {
            ColumnSetHelper.setTableFilter(tableFilter, packageObj);
            needSave = true;
        }

        String viewFilter = this.tableViewFilterWizardPage.getViewFilterText().getText();
        if (!this.getOldViewFilter().equals(viewFilter)) {
            ColumnSetHelper.setViewFilter(viewFilter, packageObj);
            needSave = true;
        }

        if (needSave) {
            return ProxyRepositoryViewObject.save(tdDataProvider).isOk();
        }
        return true;
    }

    @Override
    protected ConnectionParameter getParameter() {
        return null;
    }

    @Override
    protected ResourceFileMap getResourceFileMap() {
        return null;
    }

    @Override
    public void addPages() {
        setWindowTitle(DefaultMessagesImpl.getString("TableViewFilterWizard.tableViewFilter")); //$NON-NLS-1$
        setDefaultPageImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));

        tableViewFilterWizardPage = new TableViewFilterWizardPage(this);
        tableViewFilterWizardPage.setTitle(DefaultMessagesImpl.getString("TableViewFilterWizard.tableViewFilter")); //$NON-NLS-1$
        tableViewFilterWizardPage.setDescription(DefaultMessagesImpl.getString("TableViewFilterWizard.tableViewFilterDesc")); //$NON-NLS-1$

        addPage(tableViewFilterWizardPage);
    }

    public TypedReturnCode<Object> createAndSaveCWMFile(ModelElement cwmElement) {
        return null;
    }

    public ModelElement initCWMResourceBuilder() {
        return this.tdDataProvider;
    }

    private void initAction(Package packageObj) {
        this.packageObj = packageObj;
        this.tdDataProvider = ConnectionHelper.getTdDataProvider(this.packageObj);
        this.oldTableFilter = ColumnSetHelper.getTableFilter(packageObj);
        this.oldViewFilter = ColumnSetHelper.getViewFilter(packageObj);
    }

    @Override
    public void openEditor(Item item) {
        // TODO Auto-generated method stub

    }
}
