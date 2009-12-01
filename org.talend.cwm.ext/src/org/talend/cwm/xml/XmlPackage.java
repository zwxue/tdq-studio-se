/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.cwm.xml;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.talend.cwm.xml.XmlFactory
 * @model kind="package"
 * @generated
 */
public interface XmlPackage extends EPackage {

    /**
     * The package name.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "xml";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http:///org/talend/cwm/resource.xml";

    /**
     * The package namespace name.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "org.talend.cwm.xml";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    XmlPackage eINSTANCE = org.talend.cwm.xml.impl.XmlPackageImpl.init();

    /**
     * The meta object id for the '{@link org.talend.cwm.xml.impl.TdXMLElementImpl <em>Td XML Element</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.talend.cwm.xml.impl.TdXMLElementImpl
     * @see org.talend.cwm.xml.impl.XmlPackageImpl#getTdXMLElement()
     * @generated
     */
    int TD_XML_ELEMENT = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__NAME = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__VISIBILITY = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__CLIENT_DEPENDENCY = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__SUPPLIER_DEPENDENCY = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__CONSTRAINT = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__NAMESPACE = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__IMPORTER = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__STEREOTYPE = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__TAGGED_VALUE = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__DOCUMENT = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__DESCRIPTION = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__RESPONSIBLE_PARTY = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__ELEMENT_NODE = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__SET = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__RENDERED_OBJECT = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__VOCABULARY_ELEMENT = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__MEASUREMENT = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__CHANGE_REQUEST = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__DASDL_PROPERTY = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Value Slot</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__VALUE_SLOT = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__VALUE_SLOT;

    /**
     * The feature id for the '<em><b>Classifier</b></em>' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__CLASSIFIER = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__CLASSIFIER;

    /**
     * The feature id for the '<em><b>Slot</b></em>' containment reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__SLOT = orgomg.cwm.resource.xml.XmlPackage.ELEMENT__SLOT;

    /**
     * The feature id for the '<em><b>Xsd Element Declaration</b></em>' reference.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__XSD_ELEMENT_DECLARATION = orgomg.cwm.resource.xml.XmlPackage.ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Owned Document</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__OWNED_DOCUMENT = orgomg.cwm.resource.xml.XmlPackage.ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Java Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__JAVA_TYPE = orgomg.cwm.resource.xml.XmlPackage.ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Xml Content</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT__XML_CONTENT = orgomg.cwm.resource.xml.XmlPackage.ELEMENT_FEATURE_COUNT + 3;

    /**
     * The number of structural features of the '<em>Td XML Element</em>' class.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_ELEMENT_FEATURE_COUNT = orgomg.cwm.resource.xml.XmlPackage.ELEMENT_FEATURE_COUNT + 4;

    /**
     * The meta object id for the '{@link org.talend.cwm.xml.impl.TdXMLContentImpl <em>Td XML Content</em>}' class. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.talend.cwm.xml.impl.TdXMLContentImpl
     * @see org.talend.cwm.xml.impl.XmlPackageImpl#getTdXMLContent()
     * @generated
     */
    int TD_XML_CONTENT = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__NAME = orgomg.cwm.resource.xml.XmlPackage.CONTENT__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__VISIBILITY = orgomg.cwm.resource.xml.XmlPackage.CONTENT__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__CLIENT_DEPENDENCY = orgomg.cwm.resource.xml.XmlPackage.CONTENT__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__SUPPLIER_DEPENDENCY = orgomg.cwm.resource.xml.XmlPackage.CONTENT__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__CONSTRAINT = orgomg.cwm.resource.xml.XmlPackage.CONTENT__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__NAMESPACE = orgomg.cwm.resource.xml.XmlPackage.CONTENT__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__IMPORTER = orgomg.cwm.resource.xml.XmlPackage.CONTENT__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__STEREOTYPE = orgomg.cwm.resource.xml.XmlPackage.CONTENT__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__TAGGED_VALUE = orgomg.cwm.resource.xml.XmlPackage.CONTENT__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__DOCUMENT = orgomg.cwm.resource.xml.XmlPackage.CONTENT__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__DESCRIPTION = orgomg.cwm.resource.xml.XmlPackage.CONTENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__RESPONSIBLE_PARTY = orgomg.cwm.resource.xml.XmlPackage.CONTENT__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__ELEMENT_NODE = orgomg.cwm.resource.xml.XmlPackage.CONTENT__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__SET = orgomg.cwm.resource.xml.XmlPackage.CONTENT__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__RENDERED_OBJECT = orgomg.cwm.resource.xml.XmlPackage.CONTENT__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__VOCABULARY_ELEMENT = orgomg.cwm.resource.xml.XmlPackage.CONTENT__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__MEASUREMENT = orgomg.cwm.resource.xml.XmlPackage.CONTENT__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__CHANGE_REQUEST = orgomg.cwm.resource.xml.XmlPackage.CONTENT__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__DASDL_PROPERTY = orgomg.cwm.resource.xml.XmlPackage.CONTENT__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__TYPE = orgomg.cwm.resource.xml.XmlPackage.CONTENT__TYPE;

    /**
     * The feature id for the '<em><b>Occurrence</b></em>' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__OCCURRENCE = orgomg.cwm.resource.xml.XmlPackage.CONTENT__OCCURRENCE;

    /**
     * The feature id for the '<em><b>Element Type</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__ELEMENT_TYPE = orgomg.cwm.resource.xml.XmlPackage.CONTENT__ELEMENT_TYPE;

    /**
     * The feature id for the '<em><b>Owned Element Type</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__OWNED_ELEMENT_TYPE = orgomg.cwm.resource.xml.XmlPackage.CONTENT__OWNED_ELEMENT_TYPE;

    /**
     * The feature id for the '<em><b>Xml Elements</b></em>' containment reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT__XML_ELEMENTS = orgomg.cwm.resource.xml.XmlPackage.CONTENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Td XML Content</em>' class.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_CONTENT_FEATURE_COUNT = orgomg.cwm.resource.xml.XmlPackage.CONTENT_FEATURE_COUNT + 1;

    /**
     * The meta object id for the '{@link org.talend.cwm.xml.impl.TdXMLDocumentImpl <em>Td XML Document</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.talend.cwm.xml.impl.TdXMLDocumentImpl
     * @see org.talend.cwm.xml.impl.XmlPackageImpl#getTdXMLDocument()
     * @generated
     */
    int TD_XML_DOCUMENT = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__NAME = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__VISIBILITY = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__CLIENT_DEPENDENCY = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__SUPPLIER_DEPENDENCY = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__CONSTRAINT = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__NAMESPACE = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__IMPORTER = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__STEREOTYPE = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__TAGGED_VALUE = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__DOCUMENT = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__DESCRIPTION = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__RESPONSIBLE_PARTY = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__ELEMENT_NODE = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__SET = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__RENDERED_OBJECT = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__VOCABULARY_ELEMENT = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__MEASUREMENT = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__CHANGE_REQUEST = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__DASDL_PROPERTY = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Owned Element</b></em>' containment reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__OWNED_ELEMENT = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__OWNED_ELEMENT;

    /**
     * The feature id for the '<em><b>Imported Element</b></em>' reference list.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__IMPORTED_ELEMENT = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__IMPORTED_ELEMENT;

    /**
     * The feature id for the '<em><b>Data Manager</b></em>' reference list. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__DATA_MANAGER = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT__DATA_MANAGER;

    /**
     * The feature id for the '<em><b>Xsd File Path</b></em>' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT__XSD_FILE_PATH = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Td XML Document</em>' class.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_XML_DOCUMENT_FEATURE_COUNT = orgomg.cwm.resource.xml.XmlPackage.DOCUMENT_FEATURE_COUNT + 1;

    /**
     * Returns the meta object for class '{@link org.talend.cwm.xml.TdXMLElement <em>Td XML Element</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Td XML Element</em>'.
     * @see org.talend.cwm.xml.TdXMLElement
     * @generated
     */
    EClass getTdXMLElement();

    /**
     * Returns the meta object for the reference '{@link org.talend.cwm.xml.TdXMLElement#getXsdElementDeclaration <em>Xsd Element Declaration</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Xsd Element Declaration</em>'.
     * @see org.talend.cwm.xml.TdXMLElement#getXsdElementDeclaration()
     * @see #getTdXMLElement()
     * @generated
     */
    EReference getTdXMLElement_XsdElementDeclaration();

    /**
     * Returns the meta object for the reference '{@link org.talend.cwm.xml.TdXMLElement#getOwnedDocument <em>Owned Document</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Owned Document</em>'.
     * @see org.talend.cwm.xml.TdXMLElement#getOwnedDocument()
     * @see #getTdXMLElement()
     * @generated
     */
    EReference getTdXMLElement_OwnedDocument();

    /**
     * Returns the meta object for the attribute '{@link org.talend.cwm.xml.TdXMLElement#getJavaType <em>Java Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Java Type</em>'.
     * @see org.talend.cwm.xml.TdXMLElement#getJavaType()
     * @see #getTdXMLElement()
     * @generated
     */
    EAttribute getTdXMLElement_JavaType();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.cwm.xml.TdXMLElement#getXmlContent <em>Xml Content</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Xml Content</em>'.
     * @see org.talend.cwm.xml.TdXMLElement#getXmlContent()
     * @see #getTdXMLElement()
     * @generated
     */
    EReference getTdXMLElement_XmlContent();

    /**
     * Returns the meta object for class '{@link org.talend.cwm.xml.TdXMLContent <em>Td XML Content</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Td XML Content</em>'.
     * @see org.talend.cwm.xml.TdXMLContent
     * @generated
     */
    EClass getTdXMLContent();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.cwm.xml.TdXMLContent#getXmlElements <em>Xml Elements</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Xml Elements</em>'.
     * @see org.talend.cwm.xml.TdXMLContent#getXmlElements()
     * @see #getTdXMLContent()
     * @generated
     */
    EReference getTdXMLContent_XmlElements();

    /**
     * Returns the meta object for class '{@link org.talend.cwm.xml.TdXMLDocument <em>Td XML Document</em>}'. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @return the meta object for class '<em>Td XML Document</em>'.
     * @see org.talend.cwm.xml.TdXMLDocument
     * @generated
     */
    EClass getTdXMLDocument();

    /**
     * Returns the meta object for the attribute '{@link org.talend.cwm.xml.TdXMLDocument#getXsdFilePath <em>Xsd File Path</em>}'.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Xsd File Path</em>'.
     * @see org.talend.cwm.xml.TdXMLDocument#getXsdFilePath()
     * @see #getTdXMLDocument()
     * @generated
     */
    EAttribute getTdXMLDocument_XsdFilePath();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    XmlFactory getXmlFactory();

    /**
     * <!-- begin-user-doc --> Defines literals for the meta objects that represent
     * <ul>
     * <li>each class,</li>
     * <li>each feature of each class,</li>
     * <li>each enum,</li>
     * <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {

        /**
         * The meta object literal for the '{@link org.talend.cwm.xml.impl.TdXMLElementImpl <em>Td XML Element</em>}' class.
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * @see org.talend.cwm.xml.impl.TdXMLElementImpl
         * @see org.talend.cwm.xml.impl.XmlPackageImpl#getTdXMLElement()
         * @generated
         */
        EClass TD_XML_ELEMENT = eINSTANCE.getTdXMLElement();

        /**
         * The meta object literal for the '<em><b>Xsd Element Declaration</b></em>' reference feature. <!--
         * begin-user-doc --> <!-- end-user-doc -->
         * 
         * @generated
         */
        EReference TD_XML_ELEMENT__XSD_ELEMENT_DECLARATION = eINSTANCE.getTdXMLElement_XsdElementDeclaration();

        /**
         * The meta object literal for the '<em><b>Owned Document</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TD_XML_ELEMENT__OWNED_DOCUMENT = eINSTANCE.getTdXMLElement_OwnedDocument();

        /**
         * The meta object literal for the '<em><b>Java Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TD_XML_ELEMENT__JAVA_TYPE = eINSTANCE.getTdXMLElement_JavaType();

        /**
         * The meta object literal for the '<em><b>Xml Content</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TD_XML_ELEMENT__XML_CONTENT = eINSTANCE.getTdXMLElement_XmlContent();

        /**
         * The meta object literal for the '{@link org.talend.cwm.xml.impl.TdXMLContentImpl <em>Td XML Content</em>}' class.
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * @see org.talend.cwm.xml.impl.TdXMLContentImpl
         * @see org.talend.cwm.xml.impl.XmlPackageImpl#getTdXMLContent()
         * @generated
         */
        EClass TD_XML_CONTENT = eINSTANCE.getTdXMLContent();

        /**
         * The meta object literal for the '<em><b>Xml Elements</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TD_XML_CONTENT__XML_ELEMENTS = eINSTANCE.getTdXMLContent_XmlElements();

        /**
         * The meta object literal for the '{@link org.talend.cwm.xml.impl.TdXMLDocumentImpl <em>Td XML Document</em>}' class.
         * <!-- begin-user-doc --> <!-- end-user-doc -->
         * @see org.talend.cwm.xml.impl.TdXMLDocumentImpl
         * @see org.talend.cwm.xml.impl.XmlPackageImpl#getTdXMLDocument()
         * @generated
         */
        EClass TD_XML_DOCUMENT = eINSTANCE.getTdXMLDocument();

        /**
         * The meta object literal for the '<em><b>Xsd File Path</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TD_XML_DOCUMENT__XSD_FILE_PATH = eINSTANCE.getTdXMLDocument_XsdFilePath();

    }

} // XmlPackage
