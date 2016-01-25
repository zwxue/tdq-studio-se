// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.general.Project;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ColumnSetHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.i18n.internal.DefaultMessagesImpl;
import org.talend.dataprofiler.core.ui.wizard.AbstractWizard;
import org.talend.dataprofiler.core.ui.wizard.database.MetaDataFilterWizardPage.FilterType;
import org.talend.dq.analysis.parameters.ConnectionParameter;
import org.talend.dq.helper.resourcehelper.ResourceFileMap;
import org.talend.repository.ProjectManager;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.NamedColumnSet;

/**
 * DOC xqliu class global comment. Detailled comment
 */
public class MetaDataFilterWizard extends AbstractWizard {

    protected static Logger log = Logger.getLogger(MetaDataFilterWizard.class);

    private MetaDataFilterWizardPage metaDataFilterWizardPage;

    private NamedColumnSet namedColumnSet;

    private Package packageObj;

    private Connection tdDataProvider;

    private String oldColumnFilter;

    private DatabaseConnection databaseConnection;

    private FilterType filterType;

    public MetaDataFilterWizard() {
        super();
    }

    public MetaDataFilterWizard(NamedColumnSet namedColumnSet) {
        this();
        initAction(namedColumnSet);
    }

    /**
     * DOC klliu ColumnFilterWizard constructor comment.
     * 
     * @param databaseConnection
     */
    public MetaDataFilterWizard(NamedColumnSet namedColumnSet, FilterType filterType) {
        this();
        initAction(namedColumnSet);
        this.filterType = filterType;
    }

    public MetaDataFilterWizard(DatabaseConnection databaseConnection, FilterType filterType) {
        this.setDatabaseConnection(databaseConnection);
        this.filterType = filterType;
    }

    public NamedColumnSet getNamedColumnSet() {
        return namedColumnSet;
    }

    public void setNamedColumnSet(NamedColumnSet namedColumnSet) {
        this.namedColumnSet = namedColumnSet;
    }

    public Connection getTdDataProvider() {
        return tdDataProvider;
    }

    public void setTdDataProvider(Connection tdDataProvider) {
        this.tdDataProvider = tdDataProvider;
    }

    @Override
    protected String getEditorName() {
        return DefaultMessagesImpl.getString("MetaDataFilterWizard.ColumnFilter"); //$NON-NLS-1$
    }

    @Override
    protected ConnectionParameter getParameter() {
        return null;
    }

    @Override
    protected ResourceFileMap getResourceFileMap() {
        return null;
    }

    public TypedReturnCode<Object> createAndSaveCWMFile(ModelElement cwmElement) {
        return null;
    }

    public ModelElement initCWMResourceBuilder() {
        return this.tdDataProvider;
    }

    @Override
    public boolean performFinish() {
        String metaDataFilter = this.metaDataFilterWizardPage.getMetadataFilterText().getText();
        switch (filterType) {
        case PACKAGE_FILTER:
            return isSavePackageFilter(metaDataFilter);
        case COLUMN_FILTER:
            return isSaveColumnFilter(metaDataFilter);
        default:
            break;
        }
        return false;

    }

    private boolean isSaveColumnFilter(String metaDataFilter) {
        if (!this.getOldColumnFilter().equals(metaDataFilter)) {
            // ColumnHelper.setColumnFilter(columnFilter, namedColumnSet);
            // return ElementWriterFactory.getInstance().createDataProviderWriter().save(tdDataProvider).isOk();
            Property property = org.talend.dq.helper.PropertyHelper.getProperty(tdDataProvider);
            DatabaseConnectionItem connectionItem = (DatabaseConnectionItem) property.getItem();
            ColumnHelper.setColumnFilter(metaDataFilter, namedColumnSet);
            Project currentProject = ProjectManager.getInstance().getCurrentProject();
            try {
                ProxyRepositoryFactory.getInstance().save(currentProject, connectionItem);
            } catch (PersistenceException e) {
                return false;
            }
        }
        return true;
    }

    private boolean isSavePackageFilter(String metaDataFilter) {
        if (!this.getOldPackageFilter().equals(metaDataFilter)) {
            Property property = org.talend.dq.helper.PropertyHelper.getProperty(databaseConnection);
            DatabaseConnectionItem connectionItem = (DatabaseConnectionItem) property.getItem();
            ConnectionHelper.setPackageFilter(databaseConnection, metaDataFilter);
            Project currentProject = ProjectManager.getInstance().getCurrentProject();
            try {
                ProxyRepositoryFactory.getInstance().save(currentProject, connectionItem);
            } catch (PersistenceException e) {
                return false;
            }
        }
        return true;

    }

    /**
     * This method is used for getting the filter of package.
     * 
     * @return
     */
    public String getOldPackageFilter() {

        return ConnectionHelper.getPackageFilter(getDatabaseConnection());
    }

    @Override
    public void addPages() {
        switch (filterType) {
        case PACKAGE_FILTER:
            initPackageFilterDescription();
            break;
        case COLUMN_FILTER:
            initColumnFilterDescription();
        default:
            break;
        }
        addPage(metaDataFilterWizardPage);
    }

    private void initColumnFilterDescription() {
        setWindowTitle(DefaultMessagesImpl.getString("MetaDataFilterWizard.columnFilter")); //$NON-NLS-1$
        setDefaultPageImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));
        this.metaDataFilterWizardPage = new MetaDataFilterWizardPage(this);
        this.metaDataFilterWizardPage.setTitle(DefaultMessagesImpl.getString("MetaDataFilterWizard.columnFilter")); //$NON-NLS-1$
        this.metaDataFilterWizardPage.setDescription(DefaultMessagesImpl.getString("MetaDataFilterWizard.columnFilterDesc")); //$NON-NLS-1$
        metaDataFilterWizardPage.setFilterType(FilterType.COLUMN_FILTER);
    }

    private void initPackageFilterDescription() {
        setWindowTitle(DefaultMessagesImpl.getString("MetaDataFilterWizard.PackageFilter")); //$NON-NLS-1$
        setDefaultPageImageDescriptor(ImageLib.getImageDescriptor(ImageLib.REFRESH_IMAGE));
        this.metaDataFilterWizardPage = new MetaDataFilterWizardPage(this);
        this.metaDataFilterWizardPage.setTitle(DefaultMessagesImpl.getString("MetaDataFilterWizard.PackageFilter")); //$NON-NLS-1$
        this.metaDataFilterWizardPage.setDescription(DefaultMessagesImpl.getString("MetaDataFilterWizard.PackageFilterDesc")); //$NON-NLS-1$
        metaDataFilterWizardPage.setFilterType(FilterType.PACKAGE_FILTER);
    }

    private void initAction(NamedColumnSet namedColumnSet) {
        this.namedColumnSet = namedColumnSet;
        this.packageObj = ColumnSetHelper.getParentCatalogOrSchema(this.namedColumnSet);
        this.tdDataProvider = ConnectionHelper.getTdDataProvider(this.packageObj);
        this.oldColumnFilter = ColumnHelper.getColumnFilter(namedColumnSet);
    }

    @Override
    public void openEditor(Item item) {
    }

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

    public DatabaseConnection getDatabaseConnection() {
        return this.databaseConnection;
    }

    public void setDatabaseConnection(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }
}
