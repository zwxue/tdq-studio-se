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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.compare.diff.metamodel.AddModelElement;
import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.RemoveModelElement;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.api.MatchOptions;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.helper.PrvResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.util.RelationalSwitch;

/**
 * DOC rli class global comment. Detailled comment
 */
public class DataProviderComparisonLevel extends AbstractPartComparisonLevel {

    private RelationalSwitch<Package> packageSwitch;

    public DataProviderComparisonLevel(IFile selectedObj) {
        super(selectedObj);
    }

    protected void initSwitchValue() {
        super.initSwitchValue();
        packageSwitch = new RelationalSwitch<Package>() {

            public Package casePackage(Package object) {
                return object;
            }
        };
    }

    protected void handleRemoveElement(TdDataProvider oldDataProvider, RemoveModelElement removeElement) {
        Package removePackage = packageSwitch.doSwitch(removeElement.getLeftElement());
        if (removePackage == null) {
            return;
        }
        popRemoveElementConfirm(oldDataProvider);
        oldDataProvider.getDataPackage().remove(removePackage);
    }

    protected void handleAddElement(TdDataProvider oldDataProvider, AddModelElement addElement) {
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

    public void reloadCurrentLevelElement() {
        super.reloadCurrentLevelElement();
        reloadDataProviderFile(tempConnectionFile, (IFile) selectedObj);
    }

    private boolean reloadDataProviderFile(IFile tempConnectionFile, IFile selectedFile) {
        if (!selectedFile.getFileExtension().equalsIgnoreCase(FactoriesUtil.PROV)) {
            return false;
        }
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
        MatchModel match = null;
        try {
            match = MatchService.doMatch(oldDataProvider, returnProvider.getObject(), options);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        final DiffModel diff = DiffService.doDiff(match, false);
        EList<DiffElement> ownedElements = diff.getOwnedElements();
        for (DiffElement de : ownedElements) {
            EList<DiffElement> subDiffElements = de.getSubDiffElements();
            for (DiffElement difElement : subDiffElements) {
                handleDiffPackageElement(oldDataProvider, difElement);
            }
        }

        boolean ok = DqRepositoryViewService.saveDataProviderResource(oldDataProvider, (IFolder) tempConnectionFile.getParent(),
                selectedFile);
        PrvResourceFileHelper.getInstance().remove(selectedFile);
        PrvResourceFileHelper.getInstance().register(selectedFile, oldDataProvider.eResource());
        return ok;
    }

}
