/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.schema.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.talend.dataquality.indicators.schema.ConnectionIndicator;
import org.talend.dataquality.indicators.schema.SchemaIndicator;
import org.talend.dataquality.indicators.schema.SchemaPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Connection Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.schema.impl.ConnectionIndicatorImpl#getSchemaIndicators <em>Schema Indicators</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ConnectionIndicatorImpl extends SchemaIndicatorImpl implements ConnectionIndicator {

    /**
     * The cached value of the '{@link #getSchemaIndicators() <em>Schema Indicators</em>}' containment reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getSchemaIndicators()
     * @generated
     * @ordered
     */
    protected EList<SchemaIndicator> schemaIndicators;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected ConnectionIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return SchemaPackage.Literals.CONNECTION_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EList<SchemaIndicator> getSchemaIndicators() {
        if (schemaIndicators == null) {
            schemaIndicators = new EObjectContainmentEList<SchemaIndicator>(SchemaIndicator.class, this, SchemaPackage.CONNECTION_INDICATOR__SCHEMA_INDICATORS);
        }
        return schemaIndicators;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public boolean addSchemaIndicator(SchemaIndicator schemaIndicator) {
        return this.getSchemaIndicators().add(schemaIndicator);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case SchemaPackage.CONNECTION_INDICATOR__SCHEMA_INDICATORS:
                return ((InternalEList<?>)getSchemaIndicators()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case SchemaPackage.CONNECTION_INDICATOR__SCHEMA_INDICATORS:
                return getSchemaIndicators();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case SchemaPackage.CONNECTION_INDICATOR__SCHEMA_INDICATORS:
                getSchemaIndicators().clear();
                getSchemaIndicators().addAll((Collection<? extends SchemaIndicator>)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case SchemaPackage.CONNECTION_INDICATOR__SCHEMA_INDICATORS:
                getSchemaIndicators().clear();
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case SchemaPackage.CONNECTION_INDICATOR__SCHEMA_INDICATORS:
                return schemaIndicators != null && !schemaIndicators.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} // ConnectionIndicatorImpl
