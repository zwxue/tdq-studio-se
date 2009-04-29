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
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
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
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class ColumnFilterWizard extends AbstractWizard {

    protected static Logger log = Logger.getLogger(ColumnFilterWizard.class);

    private ColumnFilterWizardPage columnFilterWizardPage;

    private NamedColumnSet namedColumnSet;

    private Package packageObj;

    private TdDataProvider tdDataProvider;

    private String oldColumnFilter;

    public String getOldColumnFilter() {
        return oldColumnFilter;
    }

    public void setOldColumnFilter(String oldColumnFilter) {
        this.oldColumnFilter = oldColumnFilter;
    }

    public Package getPackageObj() {
        return packageObj;
    }

    public void setPackageObj(Package packageObj) {
        this.packageObj = packageObj;
    }

    public ColumnFilterWizard() {
        super();
    }

    public ColumnFilterWizard(NamedColumnSet namedColumnSet) {
        this();
        initAction(namedColumnSet);
    }

    public NamedColumnSet getNamedColumnSet() {
        return namedColumnSet;
    }

    public void setNamedColumnSet(NamedColumnSet namedColumnSet) {
        this.namedColumnSet = namedColumnSet;
    }

    public TdDataProvider getTdDataProvider() {
        return tdDataProvider;
    }

    public void setTdDataProvider(TdDataProvider tdDataProvider) {
        this.tdDataProvider = tdDataProvider;
    }

    @Override
    protected String getEditorName() {
        return "Column Filter";
    }

    @Override
    protected ConnectionParameter getParameter() {
        return null;
    }

    @Override
    protected ResourceFileMap getResourceFileMap() {
        return null;
    }

    public TypedReturnCode<IFile> createAndSaveCWMFile(ModelElement cwmElement) {
        return null;
    }

    public ModelElement initCWMResourceBuilder() {
        return this.tdDataProvider;
    }

    @Override
    public boolean performFinish() {
        EList<TaggedValue> tvs = this.namedColumnSet.getTaggedValue();

        String columnFilter = this.columnFilterWizardPage.getColumnFilterText().getText();
        if (!this.getOldColumnFilter().equals(columnFilter)) {
            this.namedColumnSet.getFeature().clear();
            this.namedColumnSet.getOwnedElement().clear();
            clearTaggedValue(tvs, TaggedValueHelper.COLUMN_FILTER);
            addTaggedValue(tvs, TaggedValueHelper.COLUMN_FILTER, columnFilter);
            return PrvResourceFileHelper.getInstance().save(tdDataProvider).isOk();
        }

        return true;
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
    public void addPages() {
        setWindowTitle(DefaultMessagesImpl.getString("ColumnFilterWizard.columnFilter")); //$NON-NLS-1$
        setDefaultPageImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));

        this.columnFilterWizardPage = new ColumnFilterWizardPage(this);
        this.columnFilterWizardPage.setTitle(DefaultMessagesImpl.getString("ColumnFilterWizard.columnFilter")); //$NON-NLS-1$
        this.columnFilterWizardPage.setDescription(DefaultMessagesImpl.getString("ColumnFilterWizard.columnFilterDesc")); //$NON-NLS-1$

        addPage(columnFilterWizardPage);
    }

    private void initAction(NamedColumnSet namedColumnSet) {
        this.namedColumnSet = namedColumnSet;
        this.packageObj = ColumnSetHelper.getParentCatalogOrSchema(this.namedColumnSet);
        this.tdDataProvider = DataProviderHelper.getTdDataProvider(this.packageObj);
        this.oldColumnFilter = TaggedValueHelper.getValue(TaggedValueHelper.COLUMN_FILTER, this.namedColumnSet.getTaggedValue()) == null ? ""
                : TaggedValueHelper.getValue(TaggedValueHelper.COLUMN_FILTER, this.namedColumnSet.getTaggedValue());
    }
}
