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

import org.eclipse.emf.common.util.EList;
import orgomg.cwm.objectmodel.core.CoreFactory;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.ModelElement;

/**
 * DOC scorreia class global comment. Detailled comment
 */
public class ModelElementHelper {

    /**
     * As specified in CWM document at p. 67, the dependency kind can be of two types "Usage" or "Abstraction", but can
     * also be of other types.
     */
    public static final String USAGE = "Usage";

    /**
     * Method "createUsageDependencyOn".
     * 
     * Example Analysis depends on the exitence of a DataProvider. This method must be called
     * createUsageDependencyOn(Analysis, DataProvider). The created dependency is added to the DataProvider in its
     * "client dependencies" and to the Analysis in its "supplier dependencies". See OMG CWM spec paragraph 4.3.2.7.
     * 
     * @param kind the kind of dependency
     * @param clientElement the element that requires the requiredElement
     * @param supplierElement the required element
     * @return the Dependency of clientElement on requiredElement
     */
    public static Dependency createDependencyOn(String kind, ModelElement clientElement, ModelElement supplierElement) {
        Dependency dependency = CoreFactory.eINSTANCE.createDependency();
        dependency.setKind(kind);
        dependency.getClient().add(clientElement);
        dependency.getSupplier().add(supplierElement);
        return dependency;
    }

    /**
     * Method "removeSupplierDependencies". The given element (supplier) is an element required by other elements (the
     * clients). This method gets all the dependencies that link the supplier to the clients. Then for each client, the
     * dependency toward the supplier is removed. And finally the list of dependencies that link the supplier to its
     * clients is suppressed.
     * 
     * @param supplierElement an element that is required by other elements
     * @return
     */
    public static boolean removeSupplierDependencies(ModelElement supplierElement) {
        boolean ok = true;
        EList<Dependency> supplierDependencies = supplierElement.getSupplierDependency();
        for (Dependency dependency : supplierDependencies) {
            // first remove each dependency object from the clients elements that depend on the supplier
            dependency.getClient().clear();
        }
        supplierDependencies.clear();
        return ok;
    }

    /**
     * Method "getDependencyBetween" the dependency that relates the supplier to the client. This method looks into the
     * list of dependencies of both the supplier and the client.
     * 
     * @param kind the kind of dependency looked for (could be null)
     * @param clientElement
     * @param supplierElement
     * @return the dependency that relates the supplier to the client or null if none is found.
     */
    public static Dependency getDependencyBetween(String kind, ModelElement clientElement, ModelElement supplierElement) {
        EList<Dependency> supplierDependencies = supplierElement.getSupplierDependency();
        for (Dependency dependency : supplierDependencies) {
            String depKind = dependency.getKind();
            // 2008-04-28 scorreia instance of clientElement can be different from the client element inside the
            // list of clients of the dependency => we should not use "contains" here, but rather the object id
            if (ResourceHelper.resolveObject(dependency.getClient(), clientElement) != null) {
                if (kind == null) {
                    if (depKind == null) { // both null
                        return dependency;
                    } else {
                        continue; // not both are null
                    }
                }
                // else kind is not null and can be compared
                if (kind.compareTo(dependency.getKind()) == 0) {
                    return dependency;
                }
            }
        }
        // not found in the supplier, look into the client
        EList<Dependency> clientDependencies = clientElement.getClientDependency();
        for (Dependency dependency : clientDependencies) {
            String depKind = dependency.getKind();
            // 2008-04-28 scorreia instance of clientElement can be different from the client element inside the
            // list of clients of the dependency => we should not use "contains" here, but rather the object id
            if (ResourceHelper.resolveObject(dependency.getSupplier(), supplierElement) != null) {
                if (kind == null) {
                    if (depKind == null) { // both null
                        return dependency;
                    } else {
                        continue; // not both are null
                    }
                }
                // else kind is not null and can be compared
                if (kind.compareTo(dependency.getKind()) == 0) {
                    return dependency;
                }
            }
        }
        return null;
    }

}
