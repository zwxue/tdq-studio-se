/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.definition.userdefine.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.talend.dataquality.indicators.definition.userdefine.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class UserdefineFactoryImpl extends EFactoryImpl implements UserdefineFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static UserdefineFactory init() {
        try {
            UserdefineFactory theUserdefineFactory = (UserdefineFactory)EPackage.Registry.INSTANCE.getEFactory("http://dataquality.indicators.definition.userdefine"); 
            if (theUserdefineFactory != null) {
                return theUserdefineFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new UserdefineFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public UserdefineFactoryImpl() {
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
            case UserdefinePackage.UD_INDICATOR_DEFINITION: return createUDIndicatorDefinition();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public UDIndicatorDefinition createUDIndicatorDefinition() {
        UDIndicatorDefinitionImpl udIndicatorDefinition = new UDIndicatorDefinitionImpl();
        return udIndicatorDefinition;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public UserdefinePackage getUserdefinePackage() {
        return (UserdefinePackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static UserdefinePackage getPackage() {
        return UserdefinePackage.eINSTANCE;
    }

} //UserdefineFactoryImpl
