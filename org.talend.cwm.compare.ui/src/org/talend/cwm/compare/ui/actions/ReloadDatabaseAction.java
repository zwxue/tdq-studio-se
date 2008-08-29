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
package org.talend.cwm.compare.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.AddModelElement;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.RemoveModelElement;
import org.eclipse.emf.compare.diff.metamodel.util.DiffSwitch;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.api.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.ui.ImageLib;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.CorePlugin;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.helper.PrvResourceFileHelper;
import org.talend.dataprofiler.core.model.nodes.foldernode.AbstractDatabaseFolderNode;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dataprofiler.core.ui.progress.ProgressUI;
import org.talend.dataprofiler.core.ui.views.DQRespositoryView;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.analysis.parameters.IParameterConstant;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.util.RelationalSwitch;

/**
 * DOC rli class global comment. Detailled comment
 */
public class ReloadDatabaseAction extends Action {

    private Object selectedNode;

    private boolean removeElementConfirm = false;

    private DiffSwitch<AddModelElement> addModelSwitch;

    private DiffSwitch<RemoveModelElement> removeModelSwitch;

    private RelationalSwitch<Package> packageSwitch;

    public ReloadDatabaseAction(Object selectedNode) {
        super("Reload from database");
        this.selectedNode = selectedNode;
        setImageDescriptor(ImageLib.getImageDescriptor(ImageLib.UPDATE_IMAGE));
        initValue();
    }

    private void initValue() {

        addModelSwitch = new DiffSwitch<AddModelElement>() {

            public AddModelElement caseAddModelElement(AddModelElement object) {
                return object;
            }
        };
        removeModelSwitch = new DiffSwitch<RemoveModelElement>() {

            public RemoveModelElement caseRemoveModelElement(RemoveModelElement object) {
                return object;
            }
        };
        packageSwitch = new RelationalSwitch<Package>() {

            public Package casePackage(Package object) {
                return object;
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.action.Action#run()
     */
    @Override
    public void run() {
        final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
        IRunnableWithProgress op = new IRunnableWithProgress() {

            public void run(IProgressMonitor monitor) throws InvocationTargetException {
                DQStructureComparer.deleteCopiedResourceFile();
                IFile tempConnectionFile = DQStructureComparer.createTempConnectionFile();
                if (selectedNode instanceof AbstractDatabaseFolderNode) {
                    AbstractDatabaseFolderNode dbFolderNode = (AbstractDatabaseFolderNode) selectedNode;

                } else if (selectedNode instanceof IFile) {
                    IFile selectedFile = (IFile) selectedNode;
                    if (selectedFile.getFileExtension().equalsIgnoreCase(FactoriesUtil.PROV)) {
                        boolean ok = reloadDataProviderFile(tempConnectionFile, selectedFile);
                    }
                }
            }
        };
        try {
            ProgressUI.popProgressDialog(op, shell);
            ((DQRespositoryView) CorePlugin.getDefault().findView(DQRespositoryView.ID)).getCommonViewer().refresh();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private boolean reloadDataProviderFile(IFile tempConnectionFile, IFile selectedFile) {
        TypedReturnCode<TdDataProvider> returnVlaue = PrvResourceFileHelper.getInstance().getTdProvider(selectedFile);
        TdDataProvider oldDataProvider = returnVlaue.getObject();
        TypedReturnCode<TdDataProvider> returnProvider = getRefreshedDataProvider(oldDataProvider);
        if (returnProvider.isOk()) {
            DqRepositoryViewService.saveDataProviderResource(returnProvider.getObject(),
                    (IFolder) tempConnectionFile.getParent(), tempConnectionFile);

        }
        // add option for ignoring some elements
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(MatchOptions.OPTION_IGNORE_XMI_ID, true);
        // options.put(MatchOptions.OPTION_SEARCH_WINDOW, 500);
        MatchModel match = null;
        try {
            match = MatchService.doMatch(oldDataProvider, returnProvider.getObject(), options);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        final DiffModel diff = DiffService.doDiff(match, false);

        // EList<UnMatchElement> unMatchedElements = match.getUnMatchedElements();
        // for (Object object : unMatchedElements) {
        // UnMatchElement unMatched = (UnMatchElement) object;
        // ModelElement modelElt = (ModelElement) unMatched.getElement();
        // System.out.println("Unmatched elt= " + modelElt.getName());
        // }
        EList<DiffElement> ownedElements = diff.getOwnedElements();
        for (DiffElement de : ownedElements) {
            EList<DiffElement> subDiffElements = de.getSubDiffElements();
            for (DiffElement difElement : subDiffElements) {
                handleDiffPackage(oldDataProvider, difElement);
            }
        }

        boolean ok = DqRepositoryViewService.saveDataProviderResource(oldDataProvider, (IFolder) tempConnectionFile.getParent(),
                selectedFile);

        PrvResourceFileHelper.getInstance().remove(selectedFile);
        PrvResourceFileHelper.getInstance().register(selectedFile, oldDataProvider.eResource());
        return ok;
    }

    private void handleDiffPackage(TdDataProvider oldDataProvider, DiffElement difElement) {
        AddModelElement addElement = addModelSwitch.doSwitch(difElement);
        if (addElement != null) {
            EObject rightElement = addElement.getRightElement();
            TdCatalog catalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(rightElement);
            if (catalog != null) {
                DataProviderHelper.addCatalog(catalog, oldDataProvider);
            } else {
                TdSchema schema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(rightElement);
                if (schema != null) {
                    DataProviderHelper.addSchema(schema, oldDataProvider);
                }
            }
            return;
        }
        RemoveModelElement removeElement = removeModelSwitch.doSwitch(difElement);
        if (removeElement != null) {
            Package removePackage = packageSwitch.doSwitch(removeElement.getLeftElement());
            if (removePackage == null) {
                return;
            }
            if (!removeElementConfirm) {
                DeleteModelElementConfirmDialog.showElementImpactDialog(null, new ModelElement[] { oldDataProvider },
                        "The following analyses is impacted:");
                removeElementConfirm = true;
            }
            oldDataProvider.getDataPackage().remove(removePackage);
        }
    }

    private TypedReturnCode<TdDataProvider> getRefreshedDataProvider(TdDataProvider oldDataProvider) {
        TypedReturnCode<TdProviderConnection> tdProviderConnection = DataProviderHelper.getTdProviderConnection(oldDataProvider);
        String urlString = tdProviderConnection.getObject().getConnectionString();
        String driverClassName = tdProviderConnection.getObject().getDriverClassName();
        Properties properties = new Properties();
        properties.setProperty(PluginConstant.USER_PROPERTY, TaggedValueHelper.getValue(PluginConstant.USER_PROPERTY,
                tdProviderConnection.getObject()));
        properties.setProperty(PluginConstant.PASSWORD_PROPERTY, TaggedValueHelper.getValue(PluginConstant.PASSWORD_PROPERTY,
                tdProviderConnection.getObject()));
        // DBConnect dbConnect = new DBConnect(urlString, driverClassName, properties);
        DBConnectionParameter connectionParameters = new DBConnectionParameter();
        String connectionName = oldDataProvider.getName();
        HashMap<String, String> paramMetadate = new HashMap<String, String>();
        paramMetadate.put(IParameterConstant.ANALYSIS_AUTHOR, TaggedValueHelper.getAuthor(oldDataProvider));
        paramMetadate.put(IParameterConstant.ANALYSIS_DESCRIPTION, TaggedValueHelper.getDescription(oldDataProvider));
        paramMetadate.put(IParameterConstant.ANALYSIS_PURPOSE, TaggedValueHelper.getPurpose(oldDataProvider));
        paramMetadate.put(IParameterConstant.ANALYSIS_STATUS, TaggedValueHelper.getDevStatus(oldDataProvider).getLiteral());
        paramMetadate.put(IParameterConstant.ANALYSIS_NAME, connectionName);
        connectionParameters.setMetadate(paramMetadate);
        connectionParameters.setJdbcUrl(urlString);
        connectionParameters.setDriverClassName(driverClassName);
        connectionParameters.setParameters(properties);
        TypedReturnCode<TdDataProvider> returnProvider = ConnectionService.createConnection(connectionParameters);
        return returnProvider;
    }
}
