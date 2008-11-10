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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;
import org.talend.cwm.softwaredeployment.SoftwaredeploymentFactory;
import org.talend.cwm.softwaredeployment.TdDataProvider;
import org.talend.cwm.softwaredeployment.TdProviderConnection;
import org.talend.cwm.softwaredeployment.TdSoftwareSystem;
import org.talend.utils.sugars.TypedReturnCode;
import orgomg.cwm.foundation.softwaredeployment.Component;
import orgomg.cwm.foundation.softwaredeployment.DataProvider;
import orgomg.cwm.foundation.softwaredeployment.ProviderConnection;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Namespace;
import orgomg.cwm.objectmodel.core.Package;
import orgomg.cwm.resource.relational.Column;
import orgomg.cwm.resource.relational.ColumnSet;

/**
 * @author scorreia
 * 
 * Utility class for data provider handling.
 */
public final class DataProviderHelper {

    private DataProviderHelper() {
    }

    /**
     * Method "createTdDataProvider" creates a data provider with the given name.
     * 
     * @param name the name of the data provider (could be null)
     * @return the created data provider.
     */
    public static TdDataProvider createTdDataProvider(String name) {
        TdDataProvider provider = SoftwaredeploymentFactory.eINSTANCE.createTdDataProvider();
        provider.setName(name);
        return provider;
    }

    /**
     * Method "setTechnicalName".
     * 
     * @param dataProvider the data provider
     * @param technicalName the technical name of the given data provider
     * @return true if the technical name was not set before.
     */
    public static boolean setTechnicalName(TdDataProvider dataProvider, String technicalName) {
        return TaggedValueHelper.setTechnicalName(dataProvider, technicalName);
    }

    /**
     * Method "setIdentifierQuoteString" sets a comment on the given element.
     * 
     * @param identifierQuoteString the quote to set
     * @param dataProvider the data provider
     * @return true if the value was not set before.
     */
    public static boolean setIdentifierQuoteString(String identifierQuoteString, TdDataProvider dataProvider) {
        return TaggedValueHelper.setIdentifierQuoteString(identifierQuoteString, dataProvider);
    }

    /**
     * Method "getIdentifierQuoteString".
     * 
     * @param dataProvider
     * @return the identifier quote string
     */
    public static String getIdentifierQuoteString(TdDataProvider dataProvider) {
        return TaggedValueHelper.getIdentifierQuoteString(dataProvider);
    }

    /**
     * Method "getTdDataProvider" returns the data provider when the catalog (or schema) is associated to only one data
     * provider. It returns null if there is no data provider or more than one data provider.
     * 
     * @param catalog the catalog or schema
     * @return the associated data provider or null
     */
    public static TdDataProvider getTdDataProvider(Package catalog) {
        Collection<TdDataProvider> tdDataProviders = DataProviderHelper.getTdDataProviders(catalog.getDataManager());
        if ((tdDataProviders.isEmpty()) || (tdDataProviders.size() > 1)) {
            // check whether given object is a schema contained in a catalog
            Namespace cat = catalog.getNamespace();
            if (cat != null) {
                TdCatalog realCatalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(cat);
                if (realCatalog != null) {
                    return getTdDataProvider(realCatalog);
                }
            }
            return null;
        }
        // else
        return tdDataProviders.iterator().next();
    }

    /**
     * Method "getTdDataProvider".
     * 
     * @param column
     * @return the data provider or null
     */
    public static TdDataProvider getTdDataProvider(Column column) {
        ColumnSet columnSetOwner = ColumnHelper.getColumnSetOwner(column);
        if (columnSetOwner == null) {
            return null;
        }
        return getDataProvider(columnSetOwner);
    }

    /**
     * Method "getDataProvider".
     * 
     * @param columnSetOwner
     * @return the data provider or null
     */
    public static TdDataProvider getDataProvider(ColumnSet columnSetOwner) {
        Namespace schemaOrCatalog = columnSetOwner.getNamespace();
        if (schemaOrCatalog == null) {
            return null;
        }
        TdSchema schema = SwitchHelpers.SCHEMA_SWITCH.doSwitch(schemaOrCatalog);
        if (schema != null) {
            return getTdDataProvider(schema);
        }
        // else
        TdCatalog catalog = SwitchHelpers.CATALOG_SWITCH.doSwitch(schemaOrCatalog);
        if (catalog != null) {
            return getTdDataProvider(catalog);
        }
        // else
        return null;
    }

    /**
     * Method "getTdDataProviders".
     * 
     * @param objects a collection of objects
     * @return the subset of objects containing only the TdDataProviders.
     */
    public static Collection<TdDataProvider> getTdDataProviders(Collection<? extends EObject> objects) {
        Collection<TdDataProvider> list = new ArrayList<TdDataProvider>();
        getTdDataProvider(objects, list);
        return list;
    }

    /**
     * Method "getTdDataProvider" adds the TdDataProviders found in the objects collection into the resultingCollection.
     * 
     * @param objects collection in which to search for TdDataProviders (must not be null)
     * @param resultingCollection the collection in which the TdDataProviders are added (must not be null).
     * @return true if resulting collection is not empty.
     */
    public static boolean getTdDataProvider(Collection<? extends EObject> objects, Collection<TdDataProvider> resultingCollection) {
        assert objects != null;
        assert resultingCollection != null;
        for (EObject object : objects) {
            TdDataProvider dataProv = SwitchHelpers.TDDATAPROVIDER_SWITCH.doSwitch(object);
            if (dataProv != null) {
                resultingCollection.add(dataProv);
            }
        }
        return !resultingCollection.isEmpty();
    }

    public static boolean addCatalogs(Collection<TdCatalog> catalogs, TdDataProvider dataProvider) {
        return addPackages(catalogs, dataProvider);
    }

    public static boolean addCatalog(TdCatalog catalog, TdDataProvider dataProvider) {
        return addPackage(catalog, dataProvider);
    }

    public static boolean addSchemas(Collection<TdSchema> schemas, TdDataProvider dataProvider) {
        return addPackages(schemas, dataProvider);
    }

    public static boolean addSchema(TdSchema schema, TdDataProvider dataProvider) {
        return addPackage(schema, dataProvider);
    }

    public static TdSoftwareSystem getSoftwareSystem(TdDataProvider dataProvider) {
        final Component component = dataProvider.getComponent();
        if (component != null) {
            final Namespace namespace = component.getNamespace();
            if (namespace != null) {
                final TdSoftwareSystem softwareSystem = SwitchHelpers.TDSOFTWARE_SYSTEM_SWITCH.doSwitch(namespace);
                return softwareSystem;
            }
        }
        return null;
    }

    /**
     * Method "setSoftwareSystem".
     * 
     * @param dataProvider
     * @param softwareSystem
     * @return true if the link between the data provider and the software system is set
     */
    public static boolean setSoftwareSystem(TdDataProvider dataProvider, TdSoftwareSystem softwareSystem) {
        final EList<ModelElement> ownedElements = softwareSystem.getOwnedElement();
        for (ModelElement modelElement : ownedElements) {
            if (modelElement != null) {
            Component component = SwitchHelpers.COMPONENT_SWITCH.doSwitch(modelElement);
            if (component != null) {
                    dataProvider.setComponent(component);
                    return true;
            }
            }
        }
        return false;
    }

    private static boolean addPackages(Collection<? extends Package> packages, TdDataProvider dataProvider) {
        boolean added = false;
        if ((dataProvider != null) && (packages != null)) {
            added = dataProvider.getDataPackage().addAll(packages);
        }
        return added;
    }

    private static boolean addPackage(Package pack, TdDataProvider dataProvider) {
        boolean added = false;
        if ((dataProvider != null) && (pack != null)) {
            added = dataProvider.getDataPackage().add(pack);
        }
        return added;
    }

    /**
     * Method "addProviderConnection" adds the connection provider to the data provider.
     * 
     * @param providerConnection the provider connection
     * @param dataProvider the data provider
     * @return true if added
     */
    public static boolean addProviderConnection(TdProviderConnection providerConnection, TdDataProvider dataProvider) {
        boolean added = false;
        if ((dataProvider != null) && (providerConnection != null)) {
            // this relation is a reference to a contained object.
            added = dataProvider.getResourceConnection().add(providerConnection);
            // this relation is a reference to a non contained object.
            // dataProvider.getClientConnection().add(providerConnection);
        }
        return added;
    }

    /**
     * Method "getTdProviderConnections".
     * 
     * @param dataProvider must not be null
     * @return the list of provider connections.
     */
    public static List<TdProviderConnection> getTdProviderConnections(DataProvider dataProvider) {
        assert dataProvider != null;
        List<TdProviderConnection> tdProvConnections = new ArrayList<TdProviderConnection>();
        EList<ProviderConnection> resourceConnections = dataProvider.getResourceConnection();
        for (ProviderConnection providerConnection : resourceConnections) {
            TdProviderConnection tdProvConn = SwitchHelpers.TDPROVIDER_CONNECTION_SWITCH.doSwitch(providerConnection);
            if (tdProvConn != null) {
                tdProvConnections.add(tdProvConn);
            }
        }
        return tdProvConnections;
    }

    /**
     * Method "getTdProviderConnection".
     * 
     * @param dataProvider the data provider that contains connections.
     * @return the returned code is true only when there is only one provider connection in the list. When there is no
     * provider connection, the returned code is false, the object is null and an error message is set. When there are
     * several provider connections, the returned code is false, an error message is set, but the first connection is
     * returned by the method {@link TypedReturnCode#getObject()}.
     */
    public static TypedReturnCode<TdProviderConnection> getTdProviderConnection(DataProvider dataProvider) {
        assert dataProvider != null;
        TypedReturnCode<TdProviderConnection> rc = new TypedReturnCode<TdProviderConnection>(true);
        List<TdProviderConnection> resourceConnections = getTdProviderConnections(dataProvider);
        int nbConnections = resourceConnections.size();
        if (nbConnections == 0) {
            rc.setReturnCode("No connection found in Data provider " + dataProvider.getName(), false);
        } else {
            TdProviderConnection tdProvConn = resourceConnections.get(0);
            if (nbConnections > 1) {
                rc.setReturnCode("Warning: several connection found for this data provider " + dataProvider.getName()
                        + ". Returning the first one only.", false, tdProvConn);
            }
            // else got only one provider connection
            rc.setObject(tdProvConn);
        }
        return rc;
    }

    /**
     * Method "getTdCatalogs".
     * 
     * @param dataProvider the data provider
     * @return the catalogs contained in the data provider
     */
    public static List<TdCatalog> getTdCatalogs(DataProvider dataProvider) {
        return CatalogHelper.getTdCatalogs(dataProvider.getDataPackage());
    }

    /**
     * Method "getTdSchema".
     * 
     * @param dataProvider the data provider
     * @return the schemas contained in the data provider
     */
    public static List<TdSchema> getTdSchema(DataProvider dataProvider) {
        return SchemaHelper.getSchemas(dataProvider.getDataPackage());
    }
}
