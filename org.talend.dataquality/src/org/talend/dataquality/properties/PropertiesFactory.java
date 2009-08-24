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
     * Returns a new object of class '<em>ITDQ Property</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>ITDQ Property</em>'.
     * @generated
     */
    ITDQProperty createITDQProperty();

    /**
     * Returns a new object of class '<em>ITDQ Item</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>ITDQ Item</em>'.
     * @generated
     */
    ITDQItem createITDQItem();

    /**
     * Returns a new object of class '<em>ITDQ User</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>ITDQ User</em>'.
     * @generated
     */
    ITDQUser createITDQUser();

    /**
     * Returns a new object of class '<em>ITDQ Item State</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>ITDQ Item State</em>'.
     * @generated
     */
    ITDQItemState createITDQItemState();

    /**
     * Returns a new object of class '<em>IMock Model Element</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>IMock Model Element</em>'.
     * @generated
     */
    IMockModelElement createIMockModelElement();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    PropertiesPackage getPropertiesPackage();

} //PropertiesFactory
