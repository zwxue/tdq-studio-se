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
package org.talend.dataprofiler.core.ui.wizard.database;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.objectmodel.core.TaggedValue;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class TableViewFilterWizard extends AbstractWizard {

    protected static Logger log = Logger.getLogger(TableViewFilterWizard.class);

    private TableViewFilterWizardPage tableViewFilterWizardPage;

    private Package packageObj;

    private TdDataProvider tdDataProvider;

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

    public TdDataProvider getTdDataProvider() {
        return tdDataProvider;
    }

    public void setTdDataProvider(TdDataProvider tdDataProvider) {
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
        return "Table/View Filter";
    }

    @Override
    public boolean performFinish() {
        EList<TaggedValue> tvs = this.packageObj.getTaggedValue();

        String tableFilter = this.tableViewFilterWizardPage.getTableFilterText().getText();
        if (!this.getOldTableFilter().equals(tableFilter)) {
            clearOwnedTables(this.packageObj);
            clearTaggedValue(tvs, TaggedValueHelper.TABLE_FILTER);
            addTaggedValue(tvs, TaggedValueHelper.TABLE_FILTER, tableFilter);

        }

        String viewFilter = this.tableViewFilterWizardPage.getViewFilterText().getText();
        if (!this.getOldViewFilter().equals(viewFilter)) {
            clearOwnedViews(this.packageObj);
            clearTaggedValue(tvs, TaggedValueHelper.VIEW_FILTER);
            addTaggedValue(tvs, TaggedValueHelper.VIEW_FILTER, viewFilter);
        }

        return PrvResourceFileHelper.getInstance().save(tdDataProvider).isOk();
    }

    private void clearOwnedViews(Package pckg) {
        EList<ModelElement> mes = pckg.getOwnedElement();
        int size = mes.size();
        for (int i = 0; i < size; ++i) {
            ModelElement me = mes.get(i);
            if (me instanceof TdView) {
                mes.remove(me);
                i--;
                size--;
            }
        }
    }

    private void clearOwnedTables(Package pckg) {
        EList<ModelElement> mes = pckg.getOwnedElement();
        int size = mes.size();
        for (int i = 0; i < size; ++i) {
            ModelElement me = mes.get(i);
            if (me instanceof TdTable) {
                mes.remove(me);
                i--;
                size--;
            }
        }
    }

    private void addTaggedValue(EList<TaggedValue> taggedValues, String tag, String value) {
        if (value != null && !"".equals(value)) {
            taggedValues.add(TaggedValueHelper.createTaggedValue(tag, value));
        }
    }

    private void clearTaggedValue(EList<TaggedValue> taggedValues, String tag) {
        int size = taggedValues.size();
        for (int i = 0; i < size; i++) {
            TaggedValue tv = taggedValues.get(i);
            if (tv.getTag().equals(tag)) {
                taggedValues.remove(tv);
                i--;
                size--;
            }
        }
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

    public TypedReturnCode<IFile> createAndSaveCWMFile(ModelElement cwmElement) {
        return null;
    }

    public ModelElement initCWMResourceBuilder() {
        return this.tdDataProvider;
    }

    private void initAction(Package packageObj) {
        this.packageObj = packageObj;
        this.tdDataProvider = DataProviderHelper.getTdDataProvider(this.packageObj);
        this.oldTableFilter = TaggedValueHelper.getValue(TaggedValueHelper.TABLE_FILTER, this.packageObj.getTaggedValue()) == null ? ""
                : TaggedValueHelper.getValue(TaggedValueHelper.TABLE_FILTER, this.packageObj.getTaggedValue());
        this.oldViewFilter = TaggedValueHelper.getValue(TaggedValueHelper.VIEW_FILTER, this.packageObj.getTaggedValue()) == null ? ""
                : TaggedValueHelper.getValue(TaggedValueHelper.VIEW_FILTER, this.packageObj.getTaggedValue());
    }
}
