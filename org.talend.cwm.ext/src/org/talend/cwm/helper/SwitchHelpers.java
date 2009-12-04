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
package org.talend.cwm.helper;

import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdColumn;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.relational.TdTable;
import org.talend.cwm.relational.TdView;
import org.talend.cwm.relational.util.RelationalSwitch;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.cwm.softwaredeployment.util.SoftwaredeploymentSwitch;
import org.talend.cwm.xml.TdXMLDocument;
import org.talend.cwm.xml.TdXMLElement;
import org.talend.cwm.xml.util.XmlSwitch;
import orgomg.cwm.foundation.softwaredeployment.Component;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.ColumnSet;
import orgomg.cwm.resource.relational.ForeignKey;
import orgomg.cwm.resource.relational.NamedColumnSet;
import orgomg.cwm.resource.relational.PrimaryKey;

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

    public static final orgomg.cwm.resource.relational.util.RelationalSwitch<PrimaryKey> PRIMARY_KEY_SWITCH = new orgomg.cwm.resource.relational.util.RelationalSwitch<PrimaryKey>() {

        /*
         * (non-Javadoc)
         * 
         * @see
         * orgomg.cwm.resource.relational.util.RelationalSwitch#casePrimaryKey(orgomg.cwm.resource.relational.PrimaryKey
         * )
         */
        @Override
        public PrimaryKey casePrimaryKey(PrimaryKey object) {
            return object;
        }

    };

    public static final orgomg.cwm.resource.relational.util.RelationalSwitch<ForeignKey> FOREIGN_KEY_SWITCH = new orgomg.cwm.resource.relational.util.RelationalSwitch<ForeignKey>() {

        /*
         * (non-Javadoc)
         * 
         * @see
         * orgomg.cwm.resource.relational.util.RelationalSwitch#caseForeignKey(orgomg.cwm.resource.relational.ForeignKey
         * )
         */
        @Override
        public ForeignKey caseForeignKey(ForeignKey object) {
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

    public static final XmlSwitch<TdXMLDocument> XMLDOCUMENT_SWITCH = new XmlSwitch<TdXMLDocument>() {

        @Override
        public TdXMLDocument caseTdXMLDocument(TdXMLDocument object) {
            return object;
        }

    };

    public static final XmlSwitch<TdXMLElement> XMLELEMENT_SWITCH = new XmlSwitch<TdXMLElement>() {
        @Override
        public TdXMLElement caseTdXMLElement(TdXMLElement object) {
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

    public static final RelationalSwitch<NamedColumnSet> NAMED_COLUMN_SET_SWITCH = new RelationalSwitch<NamedColumnSet>() {

        @Override
        public NamedColumnSet caseNamedColumnSet(NamedColumnSet object) {
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

    public static final SoftwaredeploymentSwitch<TdSoftwareSystem> TDSOFTWARE_SYSTEM_SWITCH = new SoftwaredeploymentSwitch<TdSoftwareSystem>() {

        /*
         * (non-Javadoc)
         * 
         * @seeorg.talend.cwm.softwaredeployment.util.SoftwaredeploymentSwitch#caseTdSoftwareSystem(org.talend.cwm.
         * softwaredeployment.TdSoftwareSystem)
         */
        @Override
        public TdSoftwareSystem caseTdSoftwareSystem(TdSoftwareSystem object) {
            return object;
        }

    };

    public static final orgomg.cwm.foundation.softwaredeployment.util.SoftwaredeploymentSwitch<Component> COMPONENT_SWITCH = new orgomg.cwm.foundation.softwaredeployment.util.SoftwaredeploymentSwitch<Component>() {

        /*
         * (non-Javadoc)
         * 
         * @see
         * orgomg.cwm.foundation.softwaredeployment.util.SoftwaredeploymentSwitch#caseComponent(orgomg.cwm.foundation
         * .softwaredeployment.Component)
         */
        @Override
        public Component caseComponent(Component object) {
            return object;
        }

    };

}
