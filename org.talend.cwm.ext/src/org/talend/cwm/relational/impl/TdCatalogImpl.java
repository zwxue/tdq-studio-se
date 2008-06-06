/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.cwm.relational.impl;

import org.eclipse.emf.ecore.EClass;

import org.talend.cwm.relational.RelationalPackage;
import org.talend.cwm.relational.TdCatalog;
import org.talend.cwm.relational.TdSchema;

import orgomg.cwm.resource.relational.impl.CatalogImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Td Catalog</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class TdCatalogImpl extends CatalogImpl implements TdCatalog {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected TdCatalogImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return RelationalPackage.Literals.TD_CATALOG;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public boolean addSchema(TdSchema schema) {
        return this.getOwnedElement().add(schema);
    }

} // TdCatalogImpl
