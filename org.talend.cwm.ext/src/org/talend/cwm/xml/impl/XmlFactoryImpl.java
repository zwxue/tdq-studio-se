/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.cwm.xml.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.talend.cwm.xml.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class XmlFactoryImpl extends EFactoryImpl implements XmlFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static XmlFactory init() {
        try {
            XmlFactory theXmlFactory = (XmlFactory)EPackage.Registry.INSTANCE.getEFactory("http:///org/talend/cwm/resource.xml"); 
            if (theXmlFactory != null) {
                return theXmlFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new XmlFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlFactoryImpl() {
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
            case XmlPackage.TD_XML_ELEMENT: return createTdXMLElement();
            case XmlPackage.TD_XML_CONTENT: return createTdXMLContent();
            case XmlPackage.TD_XML_DOCUMENT: return createTdXMLDocument();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TdXMLElement createTdXMLElement() {
        TdXMLElementImpl tdXMLElement = new TdXMLElementImpl();
        return tdXMLElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TdXMLContent createTdXMLContent() {
        TdXMLContentImpl tdXMLContent = new TdXMLContentImpl();
        return tdXMLContent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TdXMLDocument createTdXMLDocument() {
        TdXMLDocumentImpl tdXMLDocument = new TdXMLDocumentImpl();
        return tdXMLDocument;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public XmlPackage getXmlPackage() {
        return (XmlPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static XmlPackage getPackage() {
        return XmlPackage.eINSTANCE;
    }

} //XmlFactoryImpl
