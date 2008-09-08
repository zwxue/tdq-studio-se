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
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
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
import org.talend.cwm.compare.exception.ReloadCompareException;
import org.talend.cwm.exception.TalendException;
import org.talend.cwm.helper.CatalogHelper;
import org.talend.cwm.helper.DataProviderHelper;
import org.talend.cwm.helper.PackageHelper;
import org.talend.cwm.helper.SchemaHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.management.api.DqRepositoryViewService;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.dataprofiler.core.exception.ExceptionHandler;
import org.talend.dataprofiler.core.helper.PrvResourceFileHelper;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.Schema;

/**
 * DOC rli class global comment. Detailled comment
 */
public class CatalogSchemaComparisonLevel extends AbstractComparisonLevel {

    public CatalogSchemaComparisonLevel(Object selectedObj) {
        super(selectedObj);
    }

    @Override
    protected TdDataProvider findDataProvider() {
        TdDataProvider provider = DataProviderHelper.getTdDataProvider((Package) selectedObj);
        IFile file = PrvResourceFileHelper.getInstance().findCorrespondingFile(provider);
        TdDataProvider synchronizedProvider = PrvResourceFileHelper.getInstance().readFromFile(file).getObject();
        // re-assign the value of synchronizedProvider to selectedObj.
        List<TdCatalog> tdCatalogs = DataProviderHelper.getTdCatalogs(synchronizedProvider);
        for (TdCatalog catalog : tdCatalogs) {
            if (((Package) selectedObj).getName().equals(catalog.getName())) {
                selectedObj = catalog;
            }
        }
        return synchronizedProvider;
    }

    @Override
    protected boolean compareWithReloadObject(EObject reloadedObj) {
        Package catalogSchemaObj = (Package) reloadedObj;
        try {
            TdCatalog catalogObj = SwitchHelpers.CATALOG_SWITCH.doSwitch(catalogSchemaObj);
            if (catalogObj != null) {
                List<TdTable> tables = DqRepositoryViewService.getTables(tempReloadProvider, catalogObj, null, true);
                CatalogHelper.addTables(tables, catalogObj);
                List<TdView> views = DqRepositoryViewService.getViews(tempReloadProvider, catalogObj, null, true);
                CatalogHelper.addViews(views, catalogObj);
            } else {
                List<TdTable> tables = DqRepositoryViewService.getTables(tempReloadProvider, (Schema) catalogSchemaObj, null,
                        true);
                SchemaHelper.addTables(tables, (Schema) catalogSchemaObj);
                List<TdView> views = DqRepositoryViewService.getViews(tempReloadProvider, (Schema) catalogSchemaObj, null, true);
                SchemaHelper.addViews(views, (Schema) catalogSchemaObj);
            }
        } catch (TalendException e1) {
            e1.printStackTrace();
            ExceptionHandler.process(e1);
        }

        // add option for ignoring some elements
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(MatchOptions.OPTION_IGNORE_XMI_ID, true);
        MatchModel match = null;
        try {
            match = MatchService.doContentMatch((Package) selectedObj, catalogSchemaObj, options);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        final DiffModel diff = DiffService.doDiff(match, false);
        EList<DiffElement> ownedElements = diff.getOwnedElements();
        for (DiffElement de : ownedElements) {
            handleSubDiffElement(de);
        }

        // test(match);
        return true;
    }

    /**
     * DOC rli Comment method "ddd".
     * 
     * @param match
     */
    // private void test(MatchModel match) {
    // EList<UnMatchElement> unMatchedElements = match.getUnMatchedElements();
    // for (Object object : unMatchedElements) {
    // UnMatchElement unMatched = (UnMatchElement) object;
    // ModelElement modelElt = (ModelElement) unMatched.getElement();
    // System.out.println("Unmatched elt= " + modelElt.getName());
    // }
    // }
    private void handleSubDiffElement(DiffElement de) {
        if (de.getSubDiffElements().size() > 0) {
            EList<DiffElement> subDiffElements = de.getSubDiffElements();
            for (DiffElement difElement : subDiffElements) {
                handleSubDiffElement(difElement);
            }

        } else {
            handleDiffPackageElement(de);
        }
    }

    @Override
    protected void handleAddElement(AddModelElement addElement) {
        EObject rightElement = addElement.getRightElement();
        ColumnSet columnSetSwitch = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(rightElement);
        if (columnSetSwitch != null) {
            TdCatalog catalog = (TdCatalog) selectedObj;
            PackageHelper.addColumnSet(columnSetSwitch, catalog);
        }
    }

    @Override
    protected void handleRemoveElement(RemoveModelElement removeElement) {
        ColumnSet removeColumnSet = SwitchHelpers.COLUMN_SET_SWITCH.doSwitch(removeElement.getLeftElement());
        if (removeColumnSet == null) {
            return;
        }
        popRemoveElementConfirm();
        PackageHelper.removeColumnSet(removeColumnSet, (TdCatalog) selectedObj);
    }

    @Override
    protected EObject getSavedReloadObject() throws ReloadCompareException {
        Package selectedPackage = (Package) selectedObj;
        List<TdCatalog> tdCatalogs = DataProviderHelper.getTdCatalogs(this.tempReloadProvider);
        for (TdCatalog catalog : tdCatalogs) {
            if (selectedPackage.getName().equals(catalog.getName())) {
                return catalog;
            }
        }
        List<TdSchema> tdSchemas = DataProviderHelper.getTdSchema(this.tempReloadProvider);
        for (TdSchema schema : tdSchemas) {
            if (selectedPackage.getName().equals(schema.getName())) {
                return schema;
            }
        }
        throw new ReloadCompareException("Can't find out the corresponding reload catalog/schema node for current selected node:"
                + selectedPackage.getName());
    }

}
