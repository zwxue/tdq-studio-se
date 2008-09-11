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

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.compare.diff.metamodel.AddModelElement;
import org.eclipse.emf.compare.diff.metamodel.RemoveModelElement;
import org.eclipse.emf.ecore.EObject;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.util.RelationalSwitch;

/**
 * DOC rli class global comment. Detailled comment
 */
public class DataProviderComparisonLevel extends AbstractComparisonLevel {

    private RelationalSwitch<Package> packageSwitch;

    public DataProviderComparisonLevel(Object selectedObj) {
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

    protected void handleRemoveElement(RemoveModelElement removeElement) {
        Package removePackage = packageSwitch.doSwitch(removeElement.getLeftElement());
        if (removePackage == null) {
            return;
        }
        popRemoveElementConfirm();
        oldDataProvider.getDataPackage().remove(removePackage);
        oldDataProvider.eResource().getContents().remove(removePackage);
    }

    protected void handleAddElement(AddModelElement addElement) {
        EObject rightElement = addElement.getRightElement();
        TdCatalog catalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(rightElement);
        if (catalog != null) {
            DataProviderHelper.addCatalog(catalog, oldDataProvider);
            this.tempReloadProvider.getDataPackage().remove(catalog);
        } else {
            TdSchema schema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(rightElement);
            if (schema != null) {
                DataProviderHelper.addSchema(schema, oldDataProvider);
            }
        }
        return;
    }

    protected boolean isValid() {
        return ((IFile) selectedObj).getFileExtension().equalsIgnoreCase(FactoriesUtil.PROV);
    }

    @Override
    protected TdDataProvider findDataProvider() {
        TypedReturnCode<TdDataProvider> returnVlaue = PrvResourceFileHelper.getInstance().findProvider((IFile) selectedObj);
        return returnVlaue.getObject();
    }

    @Override
    protected void saveReloadResult() {
        DqRepositoryViewService.saveOpenDataProvider(oldDataProvider, true);
        // IFile selectedFile = (IFile) selectedObj;
        // DqRepositoryViewService.saveDataProviderAndStructure(oldDataProvider, true);
        // PrvResourceFileHelper.getInstance().remove(selectedFile);
        // PrvResourceFileHelper.getInstance().register(selectedFile, oldDataProvider.eResource());
    }

    @Override
    protected EObject getSavedReloadObject() {
        return this.tempReloadProvider;
    }

}
