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
package org.talend.dataprofiler.core.ui.action.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.CorePlugin;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.core.model.properties.DatabaseConnectionItem;
import org.talend.core.model.properties.Property;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryObject;
import org.talend.cwm.constants.DevelopmentStatus;
import org.talend.cwm.dburl.SupportDBUrlStore;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.FolderProvider;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.ImageLib;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.manager.DQStructureManager;
import org.talend.dataprofiler.core.ui.utils.MessageUI;
import org.talend.dataprofiler.core.ui.utils.connection.DataStringConnection;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.connection.DataProviderBuilder;
import org.talend.dq.connection.DataProviderWriter;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.repository.model.IProxyRepositoryFactory;
import org.talend.utils.sugars.TypedReturnCode;

/**
 * DOC bZhou class global comment. Detailled comment
 */
public class ImportConnecionFromTOSAction extends Action {

    private static Logger log = Logger.getLogger(ImportConnecionFromTOSAction.class);

    private IFolder storeFolder = ResourcesPlugin.getWorkspace().getRoot().getProject(
            org.talend.dataquality.PluginConstant.getRootProjectName()).getFolder(PluginConstant.METADATA_PROJECTNAME).getFolder(
            DQStructureManager.DB_CONNECTIONS);

    /**
     * DOC bZhou ImportConnecionFromTOSAction constructor comment.
     */
    public ImportConnecionFromTOSAction() {
        super("Import connection from TOS");

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        // get all connection from TOS
        // show them in dialog
        // check already exist or not
        // store valid connection to file.

        List<DatabaseConnectionItem> allTOSConnections = getAllTOSConnections();

        initializedConnections(allTOSConnections);

        List<DatabaseConnectionItem> selectedTOSConnectiones = new ArrayList<DatabaseConnectionItem>();

        showConnectionsInDialog(allTOSConnections, selectedTOSConnectiones);

        if (!selectedTOSConnectiones.isEmpty()) {
            List<DatabaseConnectionItem> validConnections = pickOutDifferentConnections(selectedTOSConnectiones,
                    getAllTDPConnectrions());

            if (!validConnections.isEmpty()) {
                importConnectionToTDQ(validConnections);
            } else {
                MessageUI.openWarning("This connection had existed!");
            }
        }
    }

    /**
     * DOC bZhou Comment method "initializedConnections".
     * 
     * @param allTOSConnections
     */
    private void initializedConnections(List<DatabaseConnectionItem> allTOSConnections) {
        for (DatabaseConnectionItem connectionItem : allTOSConnections) {
            DatabaseConnection conn = (DatabaseConnection) connectionItem.getConnection();

            if (conn.getServerName() == null) {
                conn.setServerName("localhost");
            }

            if (conn.getAdditionalParams() != null) {
                conn.setAdditionalParams("zeroDateTimeBehavior=convertToNull&noDatetimeStringSync=true");
            }

            if (conn.getDriverClass() == null) {
                conn.setDriverClass(SupportDBUrlStore.getInstance().getDBUrlType(conn.getDatabaseType()).getDbDriver());
            }

            DataStringConnection stringConn = new DataStringConnection();
            String urlConnectionStr = stringConn.getUrlConnectionStr(conn);
            conn.setURL(urlConnectionStr);
        }
    }

    /**
     * DOC bZhou Comment method "importConnectionToTDQ".
     * 
     * @param validConnections
     */
    private void importConnectionToTDQ(List<DatabaseConnectionItem> validConnections) {
        for (DatabaseConnectionItem connectionItem : validConnections) {

            DBConnectionParameter parameter = initParameter(connectionItem);

            DataProviderBuilder dpBuilder = new DataProviderBuilder();
            boolean dpInitialized = dpBuilder.initializeDataProvider(parameter);

            if (dpInitialized) {
                // save
                TdDataProvider dataProvider = (TdDataProvider) dpBuilder.getDataProvider();

                TaggedValueHelper.setDevStatus(dataProvider, DevelopmentStatus.get(parameter.getStatus()));
                TaggedValueHelper.setAuthor(dataProvider, parameter.getAuthor());
                TaggedValueHelper.setPurpose(parameter.getPurpose(), dataProvider);
                TaggedValueHelper.setDescription(parameter.getDescription(), dataProvider);

                TypedReturnCode<IFile> save = DataProviderWriter.getInstance().createDataProviderFile(dataProvider, storeFolder);

                if (save.isOk()) {
                    log.info("import connection [" + parameter.getName() + "] from TOS sucessfully.");
                }

                org.talend.dataprofiler.core.CorePlugin.getDefault().refreshDQView();
            }
        }
    }

    /**
     * DOC bZhou Comment method "initParameter".
     * 
     * @param connectionItem
     * @return
     */
    private DBConnectionParameter initParameter(DatabaseConnectionItem connectionItem) {
        DatabaseConnection connection = (DatabaseConnection) connectionItem.getConnection();
        Property properties = connectionItem.getProperty();

        DBConnectionParameter parameter = new DBConnectionParameter();

        // set meatadata parameter
        parameter.setName(properties.getLabel());
        parameter.setAuthor(properties.getAuthor().getLogin());
        parameter.setDescription(properties.getDescription());
        parameter.setPurpose(properties.getPurpose());
        parameter.setStatus(properties.getPurpose());
        parameter.setVersion(properties.getVersion());

        // set connection parameter
        parameter.setDriverClassName(connection.getDriverClass());
        parameter.setJdbcUrl(connection.getURL());

        FolderProvider folderProvider = new FolderProvider();
        folderProvider.setFolderResource(storeFolder);
        parameter.setFolderProvider(folderProvider);

        Properties property = new Properties();
        property.setProperty("user", connection.getUsername());
        property.setProperty("password", connection.getPassword());
        parameter.setParameters(property);
        return parameter;
    }

    /**
     * DOC bZhou Comment method "showConnectionsInDialog".
     * 
     * @param allTOSConnectionItems
     */
    private void showConnectionsInDialog(List<DatabaseConnectionItem> allTOSConnectionItems,
            List<DatabaseConnectionItem> selectedTOSConnectiones) {
        CheckedTreeSelectionDialog dialog = new CheckedTreeSelectionDialog(null, new TOSConnectionsLabelProvider(),
                new TOSConnectionsContentProvider());
        dialog.setInput(allTOSConnectionItems);
        dialog.setContainerMode(true);
        dialog.setTitle("TOS Connectiones");

        if (dialog.open() == Window.OK) {
            Object[] results = dialog.getResult();
            if (results.length != 0) {
                for (Object obj : results) {
                    DatabaseConnectionItem connectionItem = (DatabaseConnectionItem) obj;
                    selectedTOSConnectiones.add(connectionItem);
                }
            }
        }
    }

    /**
     * DOC bZhou Comment method "getAllTOSConnections".
     * 
     * @return
     */
    private List<DatabaseConnectionItem> getAllTOSConnections() {
        List<DatabaseConnectionItem> connectionItems = new ArrayList<DatabaseConnectionItem>();

        IProxyRepositoryFactory proxyRepositoryFactory = CorePlugin.getDefault().getProxyRepositoryFactory();
        try {
            List<IRepositoryObject> allTOSConnections = proxyRepositoryFactory.getAll(ERepositoryObjectType.METADATA_CONNECTIONS);
            if (!allTOSConnections.isEmpty()) {
                for (IRepositoryObject repositoryObject : allTOSConnections) {
                    DatabaseConnectionItem item = (DatabaseConnectionItem) repositoryObject.getProperty().getItem();
                    connectionItems.add(item);
                }
            }
        } catch (PersistenceException e) {
            e.printStackTrace();
        }

        return connectionItems;
    }

    private List<TdDataProvider> getAllTDPConnectrions() {
        List<TdDataProvider> dataProviders = PrvResourceFileHelper.getInstance().getAllDataProviders(storeFolder);

        return dataProviders;
    }

    private List<DatabaseConnectionItem> pickOutDifferentConnections(List<DatabaseConnectionItem> selectedTOSConnectiones,
            List<TdDataProvider> tdpDataProviders) {

        List<DatabaseConnectionItem> validConnections = new ArrayList<DatabaseConnectionItem>();

        for (DatabaseConnectionItem connectionItem : selectedTOSConnectiones) {
            boolean isSame = false;
            for (TdDataProvider dataProvider : tdpDataProviders) {

                DatabaseConnection connection = (DatabaseConnection) connectionItem.getConnection();

                // String host = TaggedValueHelper.getValue(TaggedValueHelper.HOST, dataProvider);
                // String port = TaggedValueHelper.getValue(TaggedValueHelper.PORT, dataProvider);
                // String user = TaggedValueHelper.getValue(TaggedValueHelper.USER, dataProvider);
                // if (connection.getServerName().equals(host) && connection.getPort().equals(port)
                // && connection.getUsername().equals(user)) {
                // selectedTOSConnectiones.remove(connection);
                // }

                String connectionString = DataProviderHelper.getTdProviderConnection(dataProvider).getObject()
                        .getConnectionString();

                if (connection.getURL().equalsIgnoreCase(connectionString)) {
                    isSame = true;
                }
            }

            if (!isSame) {
                validConnections.add(connectionItem);
            }
        }

        return validConnections;
    }

    /**
     * DOC bZhou ImportConnecionFromTOSAction class global comment. Detailled comment
     */
    class TOSConnectionsLabelProvider extends LabelProvider {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
         */
        @Override
        public String getText(Object element) {
            if (element instanceof DatabaseConnectionItem) {
                return ((DatabaseConnectionItem) element).getProperty().getLabel();
            }

            return ((IFolder) element).getName();
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.LabelProvider#getImage(java.lang.Object)
         */
        @Override
        public Image getImage(Object element) {
            if (element instanceof DatabaseConnectionItem) {
                return ImageLib.getImage(ImageLib.TD_DATAPROVIDER);
            }

            return ImageLib.getImage(ImageLib.FOLDERNODE_IMAGE);
        }
    }

    /**
     * DOC bZhou ImportConnecionFromTOSAction class global comment. Detailled comment
     */
    class TOSConnectionsContentProvider implements ITreeContentProvider {

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
         */
        public Object[] getChildren(Object parentElement) {

            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
         */
        public Object getParent(Object element) {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
         */
        public boolean hasChildren(Object element) {
            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
         */
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                return ((List) inputElement).toArray();
            }

            return new Object[0];
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#dispose()
         */
        public void dispose() {

        }

        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
         * java.lang.Object, java.lang.Object)
         */
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

    }
}
