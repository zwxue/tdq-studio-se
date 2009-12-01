/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.cwm.xml;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * @see org.talend.cwm.xml.XmlPackage
 * @generated
 */
public interface XmlFactory extends EFactory {

    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    XmlFactory eINSTANCE = org.talend.cwm.xml.impl.XmlFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Td XML Element</em>'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return a new object of class '<em>Td XML Element</em>'.
     * @generated
     */
    TdXMLElement createTdXMLElement();

    /**
     * Returns a new object of class '<em>Td XML Content</em>'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return a new object of class '<em>Td XML Content</em>'.
     * @generated
     */
    TdXMLContent createTdXMLContent();

    /**
     * Returns a new object of class '<em>Td XML Document</em>'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return a new object of class '<em>Td XML Document</em>'.
     * @generated
     */
    TdXMLDocument createTdXMLDocument();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    XmlPackage getXmlPackage();

} // XmlFactory
