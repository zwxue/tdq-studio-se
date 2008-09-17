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
package org.talend.cwm.helper;

import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.relational.util.RelationalSwitch;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.cwm.softwaredeployment.util.SoftwaredeploymentSwitch;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * @author scorreia
 * 
 * This class gives easy access to the correctly typed elements.
 */
public final class SwitchHelpers {

    private SwitchHelpers() {
    }

    public static final RelationalSwitch<Package> PACKAGE_SWITCH = new RelationalSwitch<Package>() {

        @Override
        public Package casePackage(Package object) {
            return object;
        }
    };

    public static final RelationalSwitch<TdCatalog> CATALOG_SWITCH = new RelationalSwitch<TdCatalog>() {

        @Override
        public TdCatalog caseTdCatalog(TdCatalog object) {
            return object;
        }
    };

    public static final RelationalSwitch<TdSchema> SCHEMA_SWITCH = new RelationalSwitch<TdSchema>() {

        @Override
        public TdSchema caseTdSchema(TdSchema object) {
            return object;
        }

    };

    public static final RelationalSwitch<TdTable> TABLE_SWITCH = new RelationalSwitch<TdTable>() {

        @Override
        public TdTable caseTdTable(TdTable object) {
            return object;
        }

    };

    public static final RelationalSwitch<TdView> VIEW_SWITCH = new RelationalSwitch<TdView>() {

        @Override
        public TdView caseTdView(TdView object) {
            return object;
        }

    };

    public static final RelationalSwitch<TdColumn> COLUMN_SWITCH = new RelationalSwitch<TdColumn>() {

        @Override
        public TdColumn caseTdColumn(TdColumn object) {
            return object;
        }

    };

    public static final RelationalSwitch<ColumnSet> COLUMN_SET_SWITCH = new RelationalSwitch<ColumnSet>() {

        @Override
        public ColumnSet caseColumnSet(ColumnSet object) {
            return object;
        }

    };

    public static final SoftwaredeploymentSwitch<TdDataProvider> TDDATAPROVIDER_SWITCH = new SoftwaredeploymentSwitch<TdDataProvider>() {

        @Override
        public TdDataProvider caseTdDataProvider(TdDataProvider object) {
            return object;
        }

    };

    public static final SoftwaredeploymentSwitch<TdProviderConnection> TDPROVIDER_CONNECTION_SWITCH = new SoftwaredeploymentSwitch<TdProviderConnection>() {

        @Override
        public TdProviderConnection caseTdProviderConnection(TdProviderConnection object) {
            return object;
        }

    };
}
