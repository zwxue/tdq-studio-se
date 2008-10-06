/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.columnset.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.talend.dataquality.indicators.columnset.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ColumnsetFactoryImpl extends EFactoryImpl implements ColumnsetFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ColumnsetFactory init() {
        try {
            ColumnsetFactory theColumnsetFactory = (ColumnsetFactory)EPackage.Registry.INSTANCE.getEFactory("http://dataquality.indicators.columnset"); 
            if (theColumnsetFactory != null) {
                return theColumnsetFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new ColumnsetFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ColumnsetFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case ColumnsetPackage.VALUE_MATCHING_INDICATOR: return createValueMatchingIndicator();
            case ColumnsetPackage.ROW_MATCHING_INDICATOR: return createRowMatchingIndicator();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ValueMatchingIndicator createValueMatchingIndicator() {
        ValueMatchingIndicatorImpl valueMatchingIndicator = new ValueMatchingIndicatorImpl();
        return valueMatchingIndicator;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public RowMatchingIndicator createRowMatchingIndicator() {
        RowMatchingIndicatorImpl rowMatchingIndicator = new RowMatchingIndicatorImpl();
        return rowMatchingIndicator;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ColumnsetPackage getColumnsetPackage() {
        return (ColumnsetPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static ColumnsetPackage getPackage() {
        return ColumnsetPackage.eINSTANCE;
    }

} //ColumnsetFactoryImpl
