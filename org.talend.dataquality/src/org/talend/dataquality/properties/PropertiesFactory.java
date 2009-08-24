/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.properties;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.talend.dataquality.properties.PropertiesPackage
 * @generated
 */
public interface PropertiesFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    PropertiesFactory eINSTANCE = org.talend.dataquality.properties.impl.PropertiesFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Tdq Property</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Tdq Property</em>'.
     * @generated
     */
    TdqProperty createTdqProperty();

    /**
     * Returns a new object of class '<em>Tdq Item</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Tdq Item</em>'.
     * @generated
     */
    TdqItem createTdqItem();

    /**
     * Returns a new object of class '<em>Tdq User</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Tdq User</em>'.
     * @generated
     */
    TdqUser createTdqUser();

    /**
     * Returns a new object of class '<em>Tdq Item State</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Tdq Item State</em>'.
     * @generated
     */
    TdqItemState createTdqItemState();

    /**
     * Returns a new object of class '<em>Mock Model Element</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Mock Model Element</em>'.
     * @generated
     */
    MockModelElement createMockModelElement();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    PropertiesPackage getPropertiesPackage();

} //PropertiesFactory
