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
package org.talend.cwm.compare.factory.comparisonlevel;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.factory.IComparisonLevel;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.TaggedValueHelper;
import org.talend.cwm.management.api.ConnectionService;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.dataprofiler.core.PluginConstant;
import org.talend.dataprofiler.core.ui.dialog.message.DeleteModelElementConfirmDialog;
import org.talend.dq.analysis.parameters.DBConnectionParameter;
import org.talend.dq.analysis.parameters.IParameterConstant;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC rli class global comment. Detailled comment
 */
public abstract class AbstractPartComparisonLevel implements IComparisonLevel {

    private static Logger log = Logger.getLogger(AbstractPartComparisonLevel.class);

    private DiffSwitch<AddModelElement> addModelSwitch;

    private DiffSwitch<RemoveModelElement> removeModelSwitch;

    private boolean removeElementConfirm = false;

    protected Object selectedObj;

    protected TdDataProvider oldDataProvider;

    // protected IFile tempConnectionFile;

    protected TdDataProvider tempReloadProvider;

    public AbstractPartComparisonLevel(Object selectedObj) {
        this.selectedObj = selectedObj;
        initSwitchValue();
    }

    protected void initSwitchValue() {

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
    }

    public void reloadCurrentLevelElement() {
        if (!isValid()) {
            return;
        }
        DQStructureComparer.deleteCopiedResourceFile();
        IFile tempConnectionFile = DQStructureComparer.createTempConnectionFile();
        oldDataProvider = findDataProvider();
        if (oldDataProvider == null) {
            return;
        }
        TypedReturnCode<TdDataProvider> returnProvider = getRefreshedDataProvider(oldDataProvider);
        if (!returnProvider.isOk()) {
            log.error(returnProvider.getMessage());
            return;
        }
        tempReloadProvider = returnProvider.getObject();
        DqRepositoryViewService.saveDataProviderResource(tempReloadProvider, (IFolder) tempConnectionFile.getParent(),
                tempConnectionFile);
        EObject savedReloadObject = getSavedReloadObject();

        if (compareWithReloadObject(savedReloadObject)) {
            saveReloadResult();
        }
    }

    protected abstract EObject getSavedReloadObject();

    protected void saveReloadResult() {
        DqRepositoryViewService.saveOpenDataProvider(this.oldDataProvider);
    }

    protected abstract TdDataProvider findDataProvider();

    protected boolean isValid() {
        return true;
    }

    /**
     * 
     * 
     * @param oldDataProvider
     */
    protected boolean compareWithReloadObject(EObject reloadedObj) {

        // add option for ignoring some elements
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(MatchOptions.OPTION_IGNORE_XMI_ID, true);
        MatchModel match = null;
        try {
            match = MatchService.doMatch(oldDataProvider, reloadedObj, options);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        final DiffModel diff = DiffService.doDiff(match);
        EList<DiffElement> ownedElements = diff.getOwnedElements();
        for (DiffElement de : ownedElements) {
            EList<DiffElement> subDiffElements = de.getSubDiffElements();
            for (DiffElement difElement : subDiffElements) {
                handleDiffPackageElement(difElement);
            }
        }
        return true;
    }

    protected TypedReturnCode<TdDataProvider> getRefreshedDataProvider(TdDataProvider oldDataProvider) {
        TypedReturnCode<TdProviderConnection> tdProviderConnection = DataProviderHelper.getTdProviderConnection(oldDataProvider);
        String urlString = tdProviderConnection.getObject().getConnectionString();
        String driverClassName = tdProviderConnection.getObject().getDriverClassName();
        Properties properties = new Properties();
        properties.setProperty(PluginConstant.USER_PROPERTY, TaggedValueHelper.getValue(PluginConstant.USER_PROPERTY,
                tdProviderConnection.getObject()));
        properties.setProperty(PluginConstant.PASSWORD_PROPERTY, TaggedValueHelper.getValue(PluginConstant.PASSWORD_PROPERTY,
                tdProviderConnection.getObject()));
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

    protected void handleDiffPackageElement(DiffElement difElement) {
        AddModelElement addElement = addModelSwitch.doSwitch(difElement);
        if (addElement != null) {
            handleAddElement(addElement);
            return;
        }
        RemoveModelElement removeElement = removeModelSwitch.doSwitch(difElement);
        if (removeElement != null) {
            handleRemoveElement(removeElement);
        }
    }

    protected abstract void handleRemoveElement(RemoveModelElement removeElement);

    protected abstract void handleAddElement(AddModelElement addElement);

    protected void popRemoveElementConfirm() {
        if (!removeElementConfirm) {
            final Display display = PlatformUI.getWorkbench().getDisplay();
            final TdDataProvider provider = oldDataProvider;
            display.asyncExec(new Runnable() {

                public void run() {

                    DeleteModelElementConfirmDialog.showElementImpactDialog(new Shell(display), new ModelElement[] { provider },
                            "The following analyses will be impacted:");
                }
            });
            removeElementConfirm = true;
        }
    }

}
