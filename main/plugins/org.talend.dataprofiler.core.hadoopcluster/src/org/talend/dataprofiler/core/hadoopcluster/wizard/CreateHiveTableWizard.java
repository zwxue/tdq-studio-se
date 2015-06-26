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
package org.talend.dataprofiler.core.hadoopcluster.wizard;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.eclipse.ui.IWorkbench;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.ITDQRepositoryService;
import org.talend.core.model.metadata.IMetadataConnection;
import org.talend.core.model.metadata.builder.ConvertionHelper;
import org.talend.core.model.metadata.builder.connection.MetadataColumn;
import org.talend.core.model.metadata.builder.connection.MetadataTable;
import org.talend.core.model.metadata.types.TypesManager;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.dataprofiler.core.hadoopcluster.i18n.DefaultMessagesImpl;
import org.talend.designer.hdfsbrowse.model.EHadoopFileTypes;
import org.talend.designer.hdfsbrowse.model.IHDFSNode;
import org.talend.metadata.managment.connection.manager.HiveConnectionManager;
import org.talend.metadata.managment.hive.handler.HiveConnectionHandler;
import org.talend.repository.hdfs.ui.HDFSFileSelectorWizardPage;
import org.talend.repository.hdfs.ui.HDFSSchemaWizard;
import org.talend.repository.model.IRepositoryNode;
import org.talend.repository.model.RepositoryNode;
import org.talend.repository.model.hdfs.HDFSConnection;
import org.talend.repository.model.hdfs.HDFSConnectionItem;

/**
 * created by yyin on 2015年4月28日 Detailled comment
 *
 */
public class CreateHiveTableWizard extends HDFSSchemaWizard {

    private HDFSFileSelectorWizardPage step1Page;

    private CreateHiveTableStep2page step2page;

    private CreateHiveTableStep3Page step3Page;

    private RepositoryNode currentNode;

    private String mappingId;

    private DatabaseConnectionItem hiveConnectionItem = null;

    private String tableName;

    public CreateHiveTableWizard(IWorkbench workbench, RepositoryNode repositoryNode, String[] existingNames) {
        super(workbench, true, repositoryNode.getObject(), null, existingNames, false);
        currentNode = repositoryNode;
    }

    @Override
    public void addPages() {
        step1Page = new CreateHiveTableStep1Page(connectionItem, isRepositoryObjectEditable(), this.getTempHDFSConnection());
        step1Page.setTitle(DefaultMessagesImpl.getString(
                "HDFSSchemaWizardPage.titleCreate", connectionItem.getProperty().getLabel())); //$NON-NLS-1$
        step1Page.setDescription(DefaultMessagesImpl.getString("CreateHiveTableStep2page.descriptionCreate")); //$NON-NLS-1$
        addPage(step1Page);

        step2page = new CreateHiveTableStep2page(connectionItem, getTempHDFSConnection());
        step2page.setTitle(DefaultMessagesImpl.getString("CreateHiveTableStep2page.titleCreate", connectionItem.getProperty() //$NON-NLS-1$
                .getLabel()));
        step2page.setDescription(DefaultMessagesImpl.getString("CreateHiveTableStep2page.descriptionCreate")); //$NON-NLS-1$
        addPage(step2page);

        // add step 3 page:
        step3Page = new CreateHiveTableStep3Page(currentNode);
        step3Page.setTitle(DefaultMessagesImpl.getString("CreateHiveTableStep3Page.title")); //$NON-NLS-1$
        step3Page.setDescription(DefaultMessagesImpl.getString("CreateHiveTableStep3Page.description")); //$NON-NLS-1$
        step3Page.setPageComplete(true);
        addPage(step3Page);

        step2page.setPageComplete(false);
    }

    @Override
    public boolean performFinish() {
        if (step1Page.getSelectedFile() == null) {
            step3Page.setErrorMessage(DefaultMessagesImpl.getString("CreateHiveTableStep3Page.nofileselected")); //$NON-NLS-1$
            return false;
        }

        IRepositoryNode selectedHive = step3Page.getSelectedHive();
        // to open the wizard: create hive
        ITDQRepositoryService tdqRepService = null;
        if (GlobalServiceRegister.getDefault().isServiceRegistered(ITDQRepositoryService.class)) {
            tdqRepService = (ITDQRepositoryService) GlobalServiceRegister.getDefault().getService(ITDQRepositoryService.class);
        }

        if (selectedHive == null && tdqRepService != null) {
            if (!tdqRepService.createHive(currentNode.getParent().getParent())) {
                return false;
            }
        } else {
            hiveConnectionItem = (DatabaseConnectionItem) selectedHive.getObject().getProperty().getItem();
        }
        // execute the DDL
        IMetadataConnection metadataConnection = ConvertionHelper.convert(hiveConnectionItem.getConnection());
        mappingId = metadataConnection.getMapping();
        HiveConnectionHandler hiveConnHandler = HiveConnectionManager.getInstance().createHandler(metadataConnection);

        String createTableSql = getCreateTableSql();
        if (createTableSql == null) {
            return false;
        }
        java.sql.Statement stmt = null;

        try {
            java.sql.Connection hiveConnection = hiveConnHandler.createHiveConnection();
            if (hiveConnection != null) {
                stmt = hiveConnection.createStatement();
                stmt.execute(createTableSql);
            }
            if (tdqRepService != null) {
                tdqRepService.reloadTableList(hiveConnectionItem);
            }
            this.tableName = step3Page.getTableName();
            return true;
        } catch (java.sql.SQLException e) {
            showErrorOnPage(e);
        } catch (ClassNotFoundException e) {
            showErrorOnPage(e);
        } catch (InstantiationException e) {
            showErrorOnPage(e);
        } catch (IllegalAccessException e) {
            showErrorOnPage(e);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error(e, e);
                }
            }
        }

        return false;
    }

    /**
     * DOC yyin Comment method "showErrorOnPage".
     * 
     * @param e
     */
    private void showErrorOnPage(Exception e) {
        log.error(e, e);
        String errorMessage = e.getMessage();
        if (e.getCause() != null) {
            errorMessage = e.getCause().getMessage();
        }
        step3Page.setErrorMessage(errorMessage);
    }

    /**
     * "Create external table <filename>(<schema>)  row format delimited fields terminated by '\n' stored as textfile location '<path to hdfs file>'"
     * 
     * @param metadataTable
     * 
     * @return
     */
    private String getCreateTableSql() {
        StringBuilder createTableSQL = new StringBuilder();

        IHDFSNode selectedFile = this.step1Page.getSelectedFile();

        createTableSQL.append("CREATE EXTERNAL TABLE "); //$NON-NLS-1$
        createTableSQL.append(step3Page.getTableName());
        createTableSQL.append(" ("); //$NON-NLS-1$

        try {
            final Set<MetadataTable> tables = ConnectionHelper.getTables(getTempHDFSConnection());
            for (MetadataTable t : tables) {
                if ((metadataTable != null && t.getLabel().equals(metadataTable.getLabel())) || metadataTable == null) {
                    this.metadataTable = t;
                    break;
                }
            }
            List<MetadataColumn> metadataColumns = metadataTable.getColumns();
            // add columns
            if (metadataColumns != null && metadataColumns.size() > 0) {
                for (MetadataColumn column : metadataColumns) {
                    // use the connection's mapping id and column's talend type, can get its db type
                    String dbTypeFromTalendType = TypesManager.getDBTypeFromTalendType(mappingId, column.getTalendType());
                    createTableSQL.append(column.getLabel());
                    createTableSQL.append(" "); //$NON-NLS-1$
                    createTableSQL.append(dbTypeFromTalendType);
                    createTableSQL.append(","); //$NON-NLS-1$
                }
            } else {
                step3Page.setErrorMessage("No columns existed in this table."); //$NON-NLS-1$
                return null;
            }
            createTableSQL.deleteCharAt(createTableSQL.length() - 1);
            createTableSQL.append(")"); //$NON-NLS-1$
        } catch (Exception e) {
            showErrorOnPage(e);
        }

        HDFSConnectionItem HDFSConnectionItem = (HDFSConnectionItem) currentNode.getObject().getProperty().getItem();
        HDFSConnection connection = (HDFSConnection) HDFSConnectionItem.getConnection();
        String fieldSep = connection.getFieldSeparator();
        String lineSep = connection.getRowSeparator();
        createTableSQL.append(" row format " //$NON-NLS-1$
                + " delimited fields terminated by " + fieldSep //$NON-NLS-1$
                + " lines terminated by " + lineSep + " stored as textfile"); //$NON-NLS-1$ //$NON-NLS-2$

        createTableSQL.append(" LOCATION '"); //$NON-NLS-1$
        createTableSQL.append(getlocation(selectedFile));
        createTableSQL.append("'"); //$NON-NLS-1$

        return createTableSQL.toString();
    }

    /**
     * get the location of selected file ( end with a folder , but not a file name)
     * 
     * @param selectedFile
     * @return like "/tmp/test"
     */
    private Object getlocation(IHDFSNode selectedFile) {
        String folderPath = selectedFile.getPath();
        EHadoopFileTypes type = selectedFile.getType();
        if (EHadoopFileTypes.FILE.equals(type)) {
            folderPath = selectedFile.getParent().getPath();
        }
        return folderPath;
    }

    /**
     * check the table name . if it contains "-", change it to"_"
     * 
     * @param selectedFile
     * @return the corrected table name
     */
    private String checkTableName(IHDFSNode selectedFile) {
        EHadoopFileTypes type = selectedFile.getType();
        // when the selected is a folder, use the name by: getValue
        String name = selectedFile.getValue();
        // when the selected is a file, use the name by: getTable.getName
        if (EHadoopFileTypes.FILE.equals(type)) {
            name = selectedFile.getTable().getName();
        }
        if (name != null && name.indexOf("-") > 0) { //$NON-NLS-1$
            name = name.replaceAll("-", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return name;
    }

    protected String getDefaultTableName() {
        return checkTableName(step1Page.getSelectedFile());
    }

    public DatabaseConnectionItem getHiveConnection() {
        return hiveConnectionItem;
    }

    public String getCreatedTableName() {
        return this.tableName;
    }
}
