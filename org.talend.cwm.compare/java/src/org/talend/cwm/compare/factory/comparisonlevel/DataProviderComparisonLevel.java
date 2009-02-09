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
package org.talend.cwm.compare.factory.comparisonlevel;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.compare.diff.metamodel.AddModelElement;
import org.eclipse.emf.compare.diff.metamodel.RemoveModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.talend.commons.emf.EMFSharedResources;
import org.talend.commons.emf.FactoriesUtil;
import org.talend.cwm.compare.DQStructureComparer;
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dq.helper.resourcehelper.PrvResourceFileHelper;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.objectmodel.core.Package;

/**
 * DOC rli class global comment. Detailled comment
 */
public class DataProviderComparisonLevel extends AbstractComparisonLevel {

	public DataProviderComparisonLevel(Object selectedObj) {
		super(selectedObj);
	}

	protected void handleRemoveElement(RemoveModelElement removeElement) {
		Package removePackage = packageSwitch.doSwitch(removeElement
				.getLeftElement());
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
			TdSchema schema = SwitchHelpers.SCHEMA_SWITCH
					.doSwitch(rightElement);
			if (schema != null) {
				DataProviderHelper.addSchema(schema, oldDataProvider);
			}
		}
		return;
	}

	protected boolean isValid() {
		return ((IFile) selectedObj).getFileExtension().equalsIgnoreCase(
				FactoriesUtil.PROV);
	}

	@Override
	protected TdDataProvider findDataProvider() {
		TypedReturnCode<TdDataProvider> returnVlaue = PrvResourceFileHelper
				.getInstance().findProvider((IFile) selectedObj);
		return returnVlaue.getObject();
	}

	@Override
	protected void saveReloadResult() {
		DqRepositoryViewService.saveOpenDataProvider(oldDataProvider, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.talend.cwm.compare.factory.comparisonlevel.AbstractComparisonLevel
	 * #getSavedReloadObject()
	 */
	protected EObject getSavedReloadObject() throws ReloadCompareException {
		return this.tempReloadProvider;
	}

	@Override
	protected Resource getLeftResource() throws ReloadCompareException {
		DQStructureComparer.clearSubNode(copyedDataProvider);
		List<TdCatalog> tdCatalogs = DataProviderHelper
				.getTdCatalogs(copyedDataProvider);
		// URI uri =
		// URI.createPlatformResourceURI(copyedFile.getFullPath().toString(),
		// false);
		Resource leftResource = null;
		leftResource = copyedDataProvider.eResource();
		// if (tdCatalogs.size() != 0) {
		// leftResource =
		// EMFSharedResources.getInstance().getResource(copyedDataProvider
		// .eResource().getURI(),
		// true);
		// if (leftResource == null) {
		// throw new
		// ReloadCompareException("No factory has been found for URI: " +
		// copyedDataProvider.eResource().getURI());
		// }
		leftResource.getContents().clear();
		for (TdCatalog catalog : tdCatalogs) {
			catalog.getDataManager().clear();
			leftResource.getContents().add(catalog);
		}
		// }
		EMFSharedResources.getInstance().saveResource(leftResource);
		return leftResource;
	}

	@Override
	protected Resource getRightResource() throws ReloadCompareException {
		List<TdCatalog> tdCatalogs = DataProviderHelper
				.getTdCatalogs(tempReloadProvider);
		// URI uri = tempReloadProvider.eResource().getURI();
		Resource reloadResource = null;
		reloadResource = tempReloadProvider.eResource();
		// if (tdCatalogs.size() != 0) {
		// reloadResource = EMFSharedResources.getInstance().getResource(uri,
		// true);
		// if (reloadResource == null) {
		// throw new
		// ReloadCompareException("No factory has been found for URI: " + uri);
		// }
		reloadResource.getContents().clear();
		for (TdCatalog catalog : tdCatalogs) {
			catalog.getDataManager().clear();
			reloadResource.getContents().add(catalog);
		}
		// }
		EMFSharedResources.getInstance().saveResource(reloadResource);
		return reloadResource;
	}

}
