/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.properties;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import orgomg.cwm.objectmodel.core.CorePackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.talend.dataquality.properties.PropertiesFactory
 * @model kind="package"
 * @generated
 */
public interface PropertiesPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "properties";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://dataquality.properties";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "dataquality.properties";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    PropertiesPackage eINSTANCE = org.talend.dataquality.properties.impl.PropertiesPackageImpl.init();

    /**
     * The meta object id for the '{@link org.talend.dataquality.properties.impl.TdqPropertyImpl <em>Tdq Property</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.properties.impl.TdqPropertyImpl
     * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTdqProperty()
     * @generated
     */
    int TDQ_PROPERTY = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__NAME = CorePackage.MODEL_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__VISIBILITY = CorePackage.MODEL_ELEMENT__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__CLIENT_DEPENDENCY = CorePackage.MODEL_ELEMENT__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__SUPPLIER_DEPENDENCY = CorePackage.MODEL_ELEMENT__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__CONSTRAINT = CorePackage.MODEL_ELEMENT__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__NAMESPACE = CorePackage.MODEL_ELEMENT__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__IMPORTER = CorePackage.MODEL_ELEMENT__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__STEREOTYPE = CorePackage.MODEL_ELEMENT__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__TAGGED_VALUE = CorePackage.MODEL_ELEMENT__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__DOCUMENT = CorePackage.MODEL_ELEMENT__DOCUMENT;

    /**
     * The feature id for the '<em><b>Descriptions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__DESCRIPTIONS = CorePackage.MODEL_ELEMENT__DESCRIPTIONS;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__RESPONSIBLE_PARTY = CorePackage.MODEL_ELEMENT__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__ELEMENT_NODE = CorePackage.MODEL_ELEMENT__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__SET = CorePackage.MODEL_ELEMENT__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__RENDERED_OBJECT = CorePackage.MODEL_ELEMENT__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__VOCABULARY_ELEMENT = CorePackage.MODEL_ELEMENT__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__MEASUREMENT = CorePackage.MODEL_ELEMENT__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__CHANGE_REQUEST = CorePackage.MODEL_ELEMENT__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__DASDL_PROPERTY = CorePackage.MODEL_ELEMENT__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__ID = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__LABEL = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Purpose</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__PURPOSE = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__DESCRIPTION = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Creation Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__CREATION_DATE = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Modification Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__MODIFICATION_DATE = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__VERSION = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Status Code</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__STATUS_CODE = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Item</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__ITEM = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Author</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__AUTHOR = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Informations</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__INFORMATIONS = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 10;

    /**
     * The feature id for the '<em><b>Max Information Level</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY__MAX_INFORMATION_LEVEL = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 11;

    /**
     * The number of structural features of the '<em>Tdq Property</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_PROPERTY_FEATURE_COUNT = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 12;

    /**
     * The meta object id for the '{@link org.talend.dataquality.properties.impl.TdqItemImpl <em>Tdq Item</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.properties.impl.TdqItemImpl
     * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTdqItem()
     * @generated
     */
    int TDQ_ITEM = 1;

    /**
     * The feature id for the '<em><b>Property</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__PROPERTY = org.talend.core.model.properties.PropertiesPackage.ITEM__PROPERTY;

    /**
     * The feature id for the '<em><b>State</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__STATE = org.talend.core.model.properties.PropertiesPackage.ITEM__STATE;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__NAME = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__VISIBILITY = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__CLIENT_DEPENDENCY = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__SUPPLIER_DEPENDENCY = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__CONSTRAINT = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__NAMESPACE = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__IMPORTER = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__STEREOTYPE = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__TAGGED_VALUE = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__DOCUMENT = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Descriptions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__DESCRIPTIONS = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 10;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__RESPONSIBLE_PARTY = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 11;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__ELEMENT_NODE = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 12;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__SET = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 13;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__RENDERED_OBJECT = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 14;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__VOCABULARY_ELEMENT = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 15;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__MEASUREMENT = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 16;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__CHANGE_REQUEST = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 17;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__DASDL_PROPERTY = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 18;

    /**
     * The feature id for the '<em><b>Filename</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM__FILENAME = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 19;

    /**
     * The number of structural features of the '<em>Tdq Item</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_FEATURE_COUNT = org.talend.core.model.properties.PropertiesPackage.ITEM_FEATURE_COUNT + 20;


    /**
     * The meta object id for the '{@link org.talend.dataquality.properties.impl.TdqUserImpl <em>Tdq User</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.properties.impl.TdqUserImpl
     * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTdqUser()
     * @generated
     */
    int TDQ_USER = 2;

    /**
     * The feature id for the '<em><b>Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__ID = org.talend.core.model.properties.PropertiesPackage.USER__ID;

    /**
     * The feature id for the '<em><b>Login</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__LOGIN = org.talend.core.model.properties.PropertiesPackage.USER__LOGIN;

    /**
     * The feature id for the '<em><b>Password</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__PASSWORD = org.talend.core.model.properties.PropertiesPackage.USER__PASSWORD;

    /**
     * The feature id for the '<em><b>First Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__FIRST_NAME = org.talend.core.model.properties.PropertiesPackage.USER__FIRST_NAME;

    /**
     * The feature id for the '<em><b>Last Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__LAST_NAME = org.talend.core.model.properties.PropertiesPackage.USER__LAST_NAME;

    /**
     * The feature id for the '<em><b>Creation Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__CREATION_DATE = org.talend.core.model.properties.PropertiesPackage.USER__CREATION_DATE;

    /**
     * The feature id for the '<em><b>Delete Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__DELETE_DATE = org.talend.core.model.properties.PropertiesPackage.USER__DELETE_DATE;

    /**
     * The feature id for the '<em><b>Deleted</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__DELETED = org.talend.core.model.properties.PropertiesPackage.USER__DELETED;

    /**
     * The feature id for the '<em><b>Allowed To Modify Components</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__ALLOWED_TO_MODIFY_COMPONENTS = org.talend.core.model.properties.PropertiesPackage.USER__ALLOWED_TO_MODIFY_COMPONENTS;

    /**
     * The feature id for the '<em><b>Comment</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__COMMENT = org.talend.core.model.properties.PropertiesPackage.USER__COMMENT;

    /**
     * The feature id for the '<em><b>Role</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__ROLE = org.talend.core.model.properties.PropertiesPackage.USER__ROLE;

    /**
     * The feature id for the '<em><b>Project Authorization</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__PROJECT_AUTHORIZATION = org.talend.core.model.properties.PropertiesPackage.USER__PROJECT_AUTHORIZATION;

    /**
     * The feature id for the '<em><b>Module Authorization</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__MODULE_AUTHORIZATION = org.talend.core.model.properties.PropertiesPackage.USER__MODULE_AUTHORIZATION;

    /**
     * The feature id for the '<em><b>Preferred Dashboard Connection</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__PREFERRED_DASHBOARD_CONNECTION = org.talend.core.model.properties.PropertiesPackage.USER__PREFERRED_DASHBOARD_CONNECTION;

    /**
     * The feature id for the '<em><b>Last Admin Connection Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__LAST_ADMIN_CONNECTION_DATE = org.talend.core.model.properties.PropertiesPackage.USER__LAST_ADMIN_CONNECTION_DATE;

    /**
     * The feature id for the '<em><b>Last Studio Connection Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__LAST_STUDIO_CONNECTION_DATE = org.talend.core.model.properties.PropertiesPackage.USER__LAST_STUDIO_CONNECTION_DATE;

    /**
     * The feature id for the '<em><b>First Admin Connection Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__FIRST_ADMIN_CONNECTION_DATE = org.talend.core.model.properties.PropertiesPackage.USER__FIRST_ADMIN_CONNECTION_DATE;

    /**
     * The feature id for the '<em><b>First Studio Connection Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__FIRST_STUDIO_CONNECTION_DATE = org.talend.core.model.properties.PropertiesPackage.USER__FIRST_STUDIO_CONNECTION_DATE;

    /**
     * The feature id for the '<em><b>Admin Connexion Number</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__ADMIN_CONNEXION_NUMBER = org.talend.core.model.properties.PropertiesPackage.USER__ADMIN_CONNEXION_NUMBER;

    /**
     * The feature id for the '<em><b>Studio Connexion Number</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__STUDIO_CONNEXION_NUMBER = org.talend.core.model.properties.PropertiesPackage.USER__STUDIO_CONNEXION_NUMBER;

    /**
     * The feature id for the '<em><b>Authentication Info</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__AUTHENTICATION_INFO = org.talend.core.model.properties.PropertiesPackage.USER__AUTHENTICATION_INFO;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__NAME = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__VISIBILITY = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__CLIENT_DEPENDENCY = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__SUPPLIER_DEPENDENCY = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__CONSTRAINT = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__NAMESPACE = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__IMPORTER = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__STEREOTYPE = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__TAGGED_VALUE = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__DOCUMENT = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Descriptions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__DESCRIPTIONS = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 10;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__RESPONSIBLE_PARTY = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 11;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__ELEMENT_NODE = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 12;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__SET = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 13;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__RENDERED_OBJECT = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 14;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__VOCABULARY_ELEMENT = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 15;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__MEASUREMENT = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 16;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__CHANGE_REQUEST = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 17;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER__DASDL_PROPERTY = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 18;

    /**
     * The number of structural features of the '<em>Tdq User</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_USER_FEATURE_COUNT = org.talend.core.model.properties.PropertiesPackage.USER_FEATURE_COUNT + 19;

    /**
     * The meta object id for the '{@link org.talend.dataquality.properties.impl.TdqItemStateImpl <em>Tdq Item State</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.properties.impl.TdqItemStateImpl
     * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTdqItemState()
     * @generated
     */
    int TDQ_ITEM_STATE = 3;

    /**
     * The feature id for the '<em><b>Path</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__PATH = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__PATH;

    /**
     * The feature id for the '<em><b>Deleted</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__DELETED = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__DELETED;

    /**
     * The feature id for the '<em><b>Locked</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__LOCKED = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__LOCKED;

    /**
     * The feature id for the '<em><b>Locker</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__LOCKER = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__LOCKER;

    /**
     * The feature id for the '<em><b>Lock Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__LOCK_DATE = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__LOCK_DATE;

    /**
     * The feature id for the '<em><b>Commit Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__COMMIT_DATE = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__COMMIT_DATE;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__NAME = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__VISIBILITY = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__CLIENT_DEPENDENCY = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__SUPPLIER_DEPENDENCY = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__CONSTRAINT = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__NAMESPACE = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__IMPORTER = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 6;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__STEREOTYPE = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 7;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__TAGGED_VALUE = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 8;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__DOCUMENT = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 9;

    /**
     * The feature id for the '<em><b>Descriptions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__DESCRIPTIONS = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 10;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__RESPONSIBLE_PARTY = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 11;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__ELEMENT_NODE = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 12;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__SET = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 13;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__RENDERED_OBJECT = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 14;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__VOCABULARY_ELEMENT = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 15;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__MEASUREMENT = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 16;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__CHANGE_REQUEST = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 17;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE__DASDL_PROPERTY = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 18;

    /**
     * The number of structural features of the '<em>Tdq Item State</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TDQ_ITEM_STATE_FEATURE_COUNT = org.talend.core.model.properties.PropertiesPackage.ITEM_STATE_FEATURE_COUNT + 19;


    /**
     * The meta object id for the '{@link org.talend.dataquality.properties.impl.MockModelElementImpl <em>Mock Model Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.properties.impl.MockModelElementImpl
     * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getMockModelElement()
     * @generated
     */
    int MOCK_MODEL_ELEMENT = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__NAME = CorePackage.MODEL_ELEMENT__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__VISIBILITY = CorePackage.MODEL_ELEMENT__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__CLIENT_DEPENDENCY = CorePackage.MODEL_ELEMENT__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__SUPPLIER_DEPENDENCY = CorePackage.MODEL_ELEMENT__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__CONSTRAINT = CorePackage.MODEL_ELEMENT__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__NAMESPACE = CorePackage.MODEL_ELEMENT__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__IMPORTER = CorePackage.MODEL_ELEMENT__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__STEREOTYPE = CorePackage.MODEL_ELEMENT__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__TAGGED_VALUE = CorePackage.MODEL_ELEMENT__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__DOCUMENT = CorePackage.MODEL_ELEMENT__DOCUMENT;

    /**
     * The feature id for the '<em><b>Descriptions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__DESCRIPTIONS = CorePackage.MODEL_ELEMENT__DESCRIPTIONS;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__RESPONSIBLE_PARTY = CorePackage.MODEL_ELEMENT__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__ELEMENT_NODE = CorePackage.MODEL_ELEMENT__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__SET = CorePackage.MODEL_ELEMENT__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__RENDERED_OBJECT = CorePackage.MODEL_ELEMENT__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__VOCABULARY_ELEMENT = CorePackage.MODEL_ELEMENT__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__MEASUREMENT = CorePackage.MODEL_ELEMENT__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__CHANGE_REQUEST = CorePackage.MODEL_ELEMENT__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT__DASDL_PROPERTY = CorePackage.MODEL_ELEMENT__DASDL_PROPERTY;

    /**
     * The number of structural features of the '<em>Mock Model Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MOCK_MODEL_ELEMENT_FEATURE_COUNT = CorePackage.MODEL_ELEMENT_FEATURE_COUNT + 0;


    /**
     * Returns the meta object for class '{@link org.talend.dataquality.properties.TdqProperty <em>Tdq Property</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Tdq Property</em>'.
     * @see org.talend.dataquality.properties.TdqProperty
     * @generated
     */
    EClass getTdqProperty();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.properties.TdqItem <em>Tdq Item</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Tdq Item</em>'.
     * @see org.talend.dataquality.properties.TdqItem
     * @generated
     */
    EClass getTdqItem();

    /**
     * Returns the meta object for the attribute '{@link org.talend.dataquality.properties.TdqItem#getFilename <em>Filename</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Filename</em>'.
     * @see org.talend.dataquality.properties.TdqItem#getFilename()
     * @see #getTdqItem()
     * @generated
     */
    EAttribute getTdqItem_Filename();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.properties.TdqUser <em>Tdq User</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Tdq User</em>'.
     * @see org.talend.dataquality.properties.TdqUser
     * @generated
     */
    EClass getTdqUser();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.properties.TdqItemState <em>Tdq Item State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Tdq Item State</em>'.
     * @see org.talend.dataquality.properties.TdqItemState
     * @generated
     */
    EClass getTdqItemState();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.properties.MockModelElement <em>Mock Model Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Mock Model Element</em>'.
     * @see org.talend.dataquality.properties.MockModelElement
     * @generated
     */
    EClass getMockModelElement();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    PropertiesFactory getPropertiesFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.talend.dataquality.properties.impl.TdqPropertyImpl <em>Tdq Property</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.properties.impl.TdqPropertyImpl
         * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTdqProperty()
         * @generated
         */
        EClass TDQ_PROPERTY = eINSTANCE.getTdqProperty();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.properties.impl.TdqItemImpl <em>Tdq Item</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.properties.impl.TdqItemImpl
         * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTdqItem()
         * @generated
         */
        EClass TDQ_ITEM = eINSTANCE.getTdqItem();

        /**
         * The meta object literal for the '<em><b>Filename</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TDQ_ITEM__FILENAME = eINSTANCE.getTdqItem_Filename();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.properties.impl.TdqUserImpl <em>Tdq User</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.properties.impl.TdqUserImpl
         * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTdqUser()
         * @generated
         */
        EClass TDQ_USER = eINSTANCE.getTdqUser();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.properties.impl.TdqItemStateImpl <em>Tdq Item State</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.properties.impl.TdqItemStateImpl
         * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getTdqItemState()
         * @generated
         */
        EClass TDQ_ITEM_STATE = eINSTANCE.getTdqItemState();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.properties.impl.MockModelElementImpl <em>Mock Model Element</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.properties.impl.MockModelElementImpl
         * @see org.talend.dataquality.properties.impl.PropertiesPackageImpl#getMockModelElement()
         * @generated
         */
        EClass MOCK_MODEL_ELEMENT = eINSTANCE.getMockModelElement();

    }

} //PropertiesPackage
