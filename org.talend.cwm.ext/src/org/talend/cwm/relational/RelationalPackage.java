/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.cwm.relational;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

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
 * @see org.talend.cwm.relational.RelationalFactory
 * @model kind="package"
 * @generated
 */
public interface RelationalPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "relational";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http:///org/talend/cwm/resource.relational";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "relational";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    RelationalPackage eINSTANCE = org.talend.cwm.relational.impl.RelationalPackageImpl.init();

    /**
     * The meta object id for the '{@link org.talend.cwm.relational.impl.TdTableImpl <em>Td Table</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.cwm.relational.impl.TdTableImpl
     * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdTable()
     * @generated
     */
    int TD_TABLE = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__NAME = orgomg.cwm.resource.relational.RelationalPackage.TABLE__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__VISIBILITY = orgomg.cwm.resource.relational.RelationalPackage.TABLE__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__CLIENT_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.TABLE__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__SUPPLIER_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.TABLE__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__CONSTRAINT = orgomg.cwm.resource.relational.RelationalPackage.TABLE__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__NAMESPACE = orgomg.cwm.resource.relational.RelationalPackage.TABLE__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__IMPORTER = orgomg.cwm.resource.relational.RelationalPackage.TABLE__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__STEREOTYPE = orgomg.cwm.resource.relational.RelationalPackage.TABLE__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__TAGGED_VALUE = orgomg.cwm.resource.relational.RelationalPackage.TABLE__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__DOCUMENT = orgomg.cwm.resource.relational.RelationalPackage.TABLE__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__DESCRIPTION = orgomg.cwm.resource.relational.RelationalPackage.TABLE__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__RESPONSIBLE_PARTY = orgomg.cwm.resource.relational.RelationalPackage.TABLE__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__ELEMENT_NODE = orgomg.cwm.resource.relational.RelationalPackage.TABLE__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__SET = orgomg.cwm.resource.relational.RelationalPackage.TABLE__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__RENDERED_OBJECT = orgomg.cwm.resource.relational.RelationalPackage.TABLE__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__VOCABULARY_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.TABLE__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__MEASUREMENT = orgomg.cwm.resource.relational.RelationalPackage.TABLE__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__CHANGE_REQUEST = orgomg.cwm.resource.relational.RelationalPackage.TABLE__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__DASDL_PROPERTY = orgomg.cwm.resource.relational.RelationalPackage.TABLE__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Owned Element</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__OWNED_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.TABLE__OWNED_ELEMENT;

    /**
     * The feature id for the '<em><b>Is Abstract</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__IS_ABSTRACT = orgomg.cwm.resource.relational.RelationalPackage.TABLE__IS_ABSTRACT;

    /**
     * The feature id for the '<em><b>Feature</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__FEATURE = orgomg.cwm.resource.relational.RelationalPackage.TABLE__FEATURE;

    /**
     * The feature id for the '<em><b>Structural Feature</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__STRUCTURAL_FEATURE = orgomg.cwm.resource.relational.RelationalPackage.TABLE__STRUCTURAL_FEATURE;

    /**
     * The feature id for the '<em><b>Parameter</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__PARAMETER = orgomg.cwm.resource.relational.RelationalPackage.TABLE__PARAMETER;

    /**
     * The feature id for the '<em><b>Generalization</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__GENERALIZATION = orgomg.cwm.resource.relational.RelationalPackage.TABLE__GENERALIZATION;

    /**
     * The feature id for the '<em><b>Specialization</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__SPECIALIZATION = orgomg.cwm.resource.relational.RelationalPackage.TABLE__SPECIALIZATION;

    /**
     * The feature id for the '<em><b>Instance</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__INSTANCE = orgomg.cwm.resource.relational.RelationalPackage.TABLE__INSTANCE;

    /**
     * The feature id for the '<em><b>Alias</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__ALIAS = orgomg.cwm.resource.relational.RelationalPackage.TABLE__ALIAS;

    /**
     * The feature id for the '<em><b>Expression Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__EXPRESSION_NODE = orgomg.cwm.resource.relational.RelationalPackage.TABLE__EXPRESSION_NODE;

    /**
     * The feature id for the '<em><b>Mapping From</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__MAPPING_FROM = orgomg.cwm.resource.relational.RelationalPackage.TABLE__MAPPING_FROM;

    /**
     * The feature id for the '<em><b>Mapping To</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__MAPPING_TO = orgomg.cwm.resource.relational.RelationalPackage.TABLE__MAPPING_TO;

    /**
     * The feature id for the '<em><b>Classifier Map</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__CLASSIFIER_MAP = orgomg.cwm.resource.relational.RelationalPackage.TABLE__CLASSIFIER_MAP;

    /**
     * The feature id for the '<em><b>Cf Map</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__CF_MAP = orgomg.cwm.resource.relational.RelationalPackage.TABLE__CF_MAP;

    /**
     * The feature id for the '<em><b>Domain</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__DOMAIN = orgomg.cwm.resource.relational.RelationalPackage.TABLE__DOMAIN;

    /**
     * The feature id for the '<em><b>Simple Dimension</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__SIMPLE_DIMENSION = orgomg.cwm.resource.relational.RelationalPackage.TABLE__SIMPLE_DIMENSION;

    /**
     * The feature id for the '<em><b>Index</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__INDEX = orgomg.cwm.resource.relational.RelationalPackage.TABLE__INDEX;

    /**
     * The feature id for the '<em><b>Using Trigger</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__USING_TRIGGER = orgomg.cwm.resource.relational.RelationalPackage.TABLE__USING_TRIGGER;

    /**
     * The feature id for the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__TYPE = orgomg.cwm.resource.relational.RelationalPackage.TABLE__TYPE;

    /**
     * The feature id for the '<em><b>Option Scope Column</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__OPTION_SCOPE_COLUMN = orgomg.cwm.resource.relational.RelationalPackage.TABLE__OPTION_SCOPE_COLUMN;

    /**
     * The feature id for the '<em><b>Is Temporary</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__IS_TEMPORARY = orgomg.cwm.resource.relational.RelationalPackage.TABLE__IS_TEMPORARY;

    /**
     * The feature id for the '<em><b>Temporary Scope</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__TEMPORARY_SCOPE = orgomg.cwm.resource.relational.RelationalPackage.TABLE__TEMPORARY_SCOPE;

    /**
     * The feature id for the '<em><b>Is System</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__IS_SYSTEM = orgomg.cwm.resource.relational.RelationalPackage.TABLE__IS_SYSTEM;

    /**
     * The feature id for the '<em><b>Trigger</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE__TRIGGER = orgomg.cwm.resource.relational.RelationalPackage.TABLE__TRIGGER;

    /**
     * The number of structural features of the '<em>Td Table</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TABLE_FEATURE_COUNT = orgomg.cwm.resource.relational.RelationalPackage.TABLE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.talend.cwm.relational.impl.TdViewImpl <em>Td View</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.cwm.relational.impl.TdViewImpl
     * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdView()
     * @generated
     */
    int TD_VIEW = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__NAME = orgomg.cwm.resource.relational.RelationalPackage.VIEW__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__VISIBILITY = orgomg.cwm.resource.relational.RelationalPackage.VIEW__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__CLIENT_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.VIEW__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__SUPPLIER_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.VIEW__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__CONSTRAINT = orgomg.cwm.resource.relational.RelationalPackage.VIEW__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__NAMESPACE = orgomg.cwm.resource.relational.RelationalPackage.VIEW__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__IMPORTER = orgomg.cwm.resource.relational.RelationalPackage.VIEW__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__STEREOTYPE = orgomg.cwm.resource.relational.RelationalPackage.VIEW__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__TAGGED_VALUE = orgomg.cwm.resource.relational.RelationalPackage.VIEW__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__DOCUMENT = orgomg.cwm.resource.relational.RelationalPackage.VIEW__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__DESCRIPTION = orgomg.cwm.resource.relational.RelationalPackage.VIEW__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__RESPONSIBLE_PARTY = orgomg.cwm.resource.relational.RelationalPackage.VIEW__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__ELEMENT_NODE = orgomg.cwm.resource.relational.RelationalPackage.VIEW__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__SET = orgomg.cwm.resource.relational.RelationalPackage.VIEW__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__RENDERED_OBJECT = orgomg.cwm.resource.relational.RelationalPackage.VIEW__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__VOCABULARY_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.VIEW__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__MEASUREMENT = orgomg.cwm.resource.relational.RelationalPackage.VIEW__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__CHANGE_REQUEST = orgomg.cwm.resource.relational.RelationalPackage.VIEW__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__DASDL_PROPERTY = orgomg.cwm.resource.relational.RelationalPackage.VIEW__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Owned Element</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__OWNED_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.VIEW__OWNED_ELEMENT;

    /**
     * The feature id for the '<em><b>Is Abstract</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__IS_ABSTRACT = orgomg.cwm.resource.relational.RelationalPackage.VIEW__IS_ABSTRACT;

    /**
     * The feature id for the '<em><b>Feature</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__FEATURE = orgomg.cwm.resource.relational.RelationalPackage.VIEW__FEATURE;

    /**
     * The feature id for the '<em><b>Structural Feature</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__STRUCTURAL_FEATURE = orgomg.cwm.resource.relational.RelationalPackage.VIEW__STRUCTURAL_FEATURE;

    /**
     * The feature id for the '<em><b>Parameter</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__PARAMETER = orgomg.cwm.resource.relational.RelationalPackage.VIEW__PARAMETER;

    /**
     * The feature id for the '<em><b>Generalization</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__GENERALIZATION = orgomg.cwm.resource.relational.RelationalPackage.VIEW__GENERALIZATION;

    /**
     * The feature id for the '<em><b>Specialization</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__SPECIALIZATION = orgomg.cwm.resource.relational.RelationalPackage.VIEW__SPECIALIZATION;

    /**
     * The feature id for the '<em><b>Instance</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__INSTANCE = orgomg.cwm.resource.relational.RelationalPackage.VIEW__INSTANCE;

    /**
     * The feature id for the '<em><b>Alias</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__ALIAS = orgomg.cwm.resource.relational.RelationalPackage.VIEW__ALIAS;

    /**
     * The feature id for the '<em><b>Expression Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__EXPRESSION_NODE = orgomg.cwm.resource.relational.RelationalPackage.VIEW__EXPRESSION_NODE;

    /**
     * The feature id for the '<em><b>Mapping From</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__MAPPING_FROM = orgomg.cwm.resource.relational.RelationalPackage.VIEW__MAPPING_FROM;

    /**
     * The feature id for the '<em><b>Mapping To</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__MAPPING_TO = orgomg.cwm.resource.relational.RelationalPackage.VIEW__MAPPING_TO;

    /**
     * The feature id for the '<em><b>Classifier Map</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__CLASSIFIER_MAP = orgomg.cwm.resource.relational.RelationalPackage.VIEW__CLASSIFIER_MAP;

    /**
     * The feature id for the '<em><b>Cf Map</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__CF_MAP = orgomg.cwm.resource.relational.RelationalPackage.VIEW__CF_MAP;

    /**
     * The feature id for the '<em><b>Domain</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__DOMAIN = orgomg.cwm.resource.relational.RelationalPackage.VIEW__DOMAIN;

    /**
     * The feature id for the '<em><b>Simple Dimension</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__SIMPLE_DIMENSION = orgomg.cwm.resource.relational.RelationalPackage.VIEW__SIMPLE_DIMENSION;

    /**
     * The feature id for the '<em><b>Index</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__INDEX = orgomg.cwm.resource.relational.RelationalPackage.VIEW__INDEX;

    /**
     * The feature id for the '<em><b>Using Trigger</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__USING_TRIGGER = orgomg.cwm.resource.relational.RelationalPackage.VIEW__USING_TRIGGER;

    /**
     * The feature id for the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__TYPE = orgomg.cwm.resource.relational.RelationalPackage.VIEW__TYPE;

    /**
     * The feature id for the '<em><b>Option Scope Column</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__OPTION_SCOPE_COLUMN = orgomg.cwm.resource.relational.RelationalPackage.VIEW__OPTION_SCOPE_COLUMN;

    /**
     * The feature id for the '<em><b>Is Read Only</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__IS_READ_ONLY = orgomg.cwm.resource.relational.RelationalPackage.VIEW__IS_READ_ONLY;

    /**
     * The feature id for the '<em><b>Check Option</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__CHECK_OPTION = orgomg.cwm.resource.relational.RelationalPackage.VIEW__CHECK_OPTION;

    /**
     * The feature id for the '<em><b>Query Expression</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW__QUERY_EXPRESSION = orgomg.cwm.resource.relational.RelationalPackage.VIEW__QUERY_EXPRESSION;

    /**
     * The number of structural features of the '<em>Td View</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_VIEW_FEATURE_COUNT = orgomg.cwm.resource.relational.RelationalPackage.VIEW_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.talend.cwm.relational.impl.TdCatalogImpl <em>Td Catalog</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.cwm.relational.impl.TdCatalogImpl
     * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdCatalog()
     * @generated
     */
    int TD_CATALOG = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__NAME = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__VISIBILITY = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__CLIENT_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__SUPPLIER_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__CONSTRAINT = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__NAMESPACE = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__IMPORTER = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__STEREOTYPE = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__TAGGED_VALUE = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__DOCUMENT = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__DESCRIPTION = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__RESPONSIBLE_PARTY = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__ELEMENT_NODE = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__SET = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__RENDERED_OBJECT = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__VOCABULARY_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__MEASUREMENT = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__CHANGE_REQUEST = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__DASDL_PROPERTY = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Owned Element</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__OWNED_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__OWNED_ELEMENT;

    /**
     * The feature id for the '<em><b>Imported Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__IMPORTED_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__IMPORTED_ELEMENT;

    /**
     * The feature id for the '<em><b>Data Manager</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__DATA_MANAGER = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__DATA_MANAGER;

    /**
     * The feature id for the '<em><b>Default Character Set Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__DEFAULT_CHARACTER_SET_NAME = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__DEFAULT_CHARACTER_SET_NAME;

    /**
     * The feature id for the '<em><b>Default Collation Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG__DEFAULT_COLLATION_NAME = orgomg.cwm.resource.relational.RelationalPackage.CATALOG__DEFAULT_COLLATION_NAME;

    /**
     * The number of structural features of the '<em>Td Catalog</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_CATALOG_FEATURE_COUNT = orgomg.cwm.resource.relational.RelationalPackage.CATALOG_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.talend.cwm.relational.impl.TdSchemaImpl <em>Td Schema</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.cwm.relational.impl.TdSchemaImpl
     * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdSchema()
     * @generated
     */
    int TD_SCHEMA = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__NAME = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__VISIBILITY = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__CLIENT_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__SUPPLIER_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__CONSTRAINT = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__NAMESPACE = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__IMPORTER = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__STEREOTYPE = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__TAGGED_VALUE = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__DOCUMENT = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__DESCRIPTION = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__RESPONSIBLE_PARTY = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__ELEMENT_NODE = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__SET = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__RENDERED_OBJECT = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__VOCABULARY_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__MEASUREMENT = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__CHANGE_REQUEST = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__DASDL_PROPERTY = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Owned Element</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__OWNED_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__OWNED_ELEMENT;

    /**
     * The feature id for the '<em><b>Imported Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__IMPORTED_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__IMPORTED_ELEMENT;

    /**
     * The feature id for the '<em><b>Data Manager</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA__DATA_MANAGER = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA__DATA_MANAGER;

    /**
     * The number of structural features of the '<em>Td Schema</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SCHEMA_FEATURE_COUNT = orgomg.cwm.resource.relational.RelationalPackage.SCHEMA_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.talend.cwm.relational.impl.TdColumnImpl <em>Td Column</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.cwm.relational.impl.TdColumnImpl
     * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdColumn()
     * @generated
     */
    int TD_COLUMN = 4;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__NAME = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__VISIBILITY = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__CLIENT_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__SUPPLIER_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__CONSTRAINT = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__NAMESPACE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__IMPORTER = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__STEREOTYPE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__TAGGED_VALUE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__DOCUMENT = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__DESCRIPTION = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__RESPONSIBLE_PARTY = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__ELEMENT_NODE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__SET = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__RENDERED_OBJECT = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__VOCABULARY_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__MEASUREMENT = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__CHANGE_REQUEST = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__DASDL_PROPERTY = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Owner Scope</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__OWNER_SCOPE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__OWNER_SCOPE;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__OWNER = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__OWNER;

    /**
     * The feature id for the '<em><b>Feature Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__FEATURE_NODE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__FEATURE_NODE;

    /**
     * The feature id for the '<em><b>Feature Map</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__FEATURE_MAP = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__FEATURE_MAP;

    /**
     * The feature id for the '<em><b>Cf Map</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__CF_MAP = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__CF_MAP;

    /**
     * The feature id for the '<em><b>Changeability</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__CHANGEABILITY = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__CHANGEABILITY;

    /**
     * The feature id for the '<em><b>Multiplicity</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__MULTIPLICITY = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__MULTIPLICITY;

    /**
     * The feature id for the '<em><b>Ordering</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__ORDERING = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__ORDERING;

    /**
     * The feature id for the '<em><b>Target Scope</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__TARGET_SCOPE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__TARGET_SCOPE;

    /**
     * The feature id for the '<em><b>Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__TYPE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__TYPE;

    /**
     * The feature id for the '<em><b>Slot</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__SLOT = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__SLOT;

    /**
     * The feature id for the '<em><b>Discriminated Union</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__DISCRIMINATED_UNION = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__DISCRIMINATED_UNION;

    /**
     * The feature id for the '<em><b>Indexed Feature</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__INDEXED_FEATURE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__INDEXED_FEATURE;

    /**
     * The feature id for the '<em><b>Key Relationship</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__KEY_RELATIONSHIP = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__KEY_RELATIONSHIP;

    /**
     * The feature id for the '<em><b>Unique Key</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__UNIQUE_KEY = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__UNIQUE_KEY;

    /**
     * The feature id for the '<em><b>Data Item</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__DATA_ITEM = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__DATA_ITEM;

    /**
     * The feature id for the '<em><b>Remap</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__REMAP = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__REMAP;

    /**
     * The feature id for the '<em><b>Initial Value</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__INITIAL_VALUE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__INITIAL_VALUE;

    /**
     * The feature id for the '<em><b>Precision</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__PRECISION = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__PRECISION;

    /**
     * The feature id for the '<em><b>Scale</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__SCALE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__SCALE;

    /**
     * The feature id for the '<em><b>Is Nullable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__IS_NULLABLE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__IS_NULLABLE;

    /**
     * The feature id for the '<em><b>Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__LENGTH = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__LENGTH;

    /**
     * The feature id for the '<em><b>Collation Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__COLLATION_NAME = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__COLLATION_NAME;

    /**
     * The feature id for the '<em><b>Character Set Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__CHARACTER_SET_NAME = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__CHARACTER_SET_NAME;

    /**
     * The feature id for the '<em><b>Referenced Table Type</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__REFERENCED_TABLE_TYPE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__REFERENCED_TABLE_TYPE;

    /**
     * The feature id for the '<em><b>Option Scope Column Set</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__OPTION_SCOPE_COLUMN_SET = orgomg.cwm.resource.relational.RelationalPackage.COLUMN__OPTION_SCOPE_COLUMN_SET;

    /**
     * The feature id for the '<em><b>Java Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__JAVA_TYPE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Sql Data Type</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN__SQL_DATA_TYPE = orgomg.cwm.resource.relational.RelationalPackage.COLUMN_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Td Column</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_COLUMN_FEATURE_COUNT = orgomg.cwm.resource.relational.RelationalPackage.COLUMN_FEATURE_COUNT + 2;


    /**
     * The meta object id for the '{@link org.talend.cwm.relational.impl.TdSqlDataTypeImpl <em>Td Sql Data Type</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.cwm.relational.impl.TdSqlDataTypeImpl
     * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdSqlDataType()
     * @generated
     */
    int TD_SQL_DATA_TYPE = 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__NAME = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__VISIBILITY = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__CLIENT_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__SUPPLIER_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__CONSTRAINT = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__NAMESPACE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__IMPORTER = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__STEREOTYPE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__TAGGED_VALUE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__DOCUMENT = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__DESCRIPTION = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__RESPONSIBLE_PARTY = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__ELEMENT_NODE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__SET = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__RENDERED_OBJECT = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__VOCABULARY_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__MEASUREMENT = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__CHANGE_REQUEST = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__DASDL_PROPERTY = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Owned Element</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__OWNED_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__OWNED_ELEMENT;

    /**
     * The feature id for the '<em><b>Is Abstract</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__IS_ABSTRACT = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__IS_ABSTRACT;

    /**
     * The feature id for the '<em><b>Feature</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__FEATURE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__FEATURE;

    /**
     * The feature id for the '<em><b>Structural Feature</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__STRUCTURAL_FEATURE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__STRUCTURAL_FEATURE;

    /**
     * The feature id for the '<em><b>Parameter</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__PARAMETER = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__PARAMETER;

    /**
     * The feature id for the '<em><b>Generalization</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__GENERALIZATION = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__GENERALIZATION;

    /**
     * The feature id for the '<em><b>Specialization</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__SPECIALIZATION = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__SPECIALIZATION;

    /**
     * The feature id for the '<em><b>Instance</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__INSTANCE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__INSTANCE;

    /**
     * The feature id for the '<em><b>Alias</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__ALIAS = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__ALIAS;

    /**
     * The feature id for the '<em><b>Expression Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__EXPRESSION_NODE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__EXPRESSION_NODE;

    /**
     * The feature id for the '<em><b>Mapping From</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__MAPPING_FROM = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__MAPPING_FROM;

    /**
     * The feature id for the '<em><b>Mapping To</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__MAPPING_TO = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__MAPPING_TO;

    /**
     * The feature id for the '<em><b>Classifier Map</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__CLASSIFIER_MAP = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__CLASSIFIER_MAP;

    /**
     * The feature id for the '<em><b>Cf Map</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__CF_MAP = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__CF_MAP;

    /**
     * The feature id for the '<em><b>Domain</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__DOMAIN = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__DOMAIN;

    /**
     * The feature id for the '<em><b>Simple Dimension</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__SIMPLE_DIMENSION = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__SIMPLE_DIMENSION;

    /**
     * The feature id for the '<em><b>Type Number</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__TYPE_NUMBER = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__TYPE_NUMBER;

    /**
     * The feature id for the '<em><b>Character Maximum Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__CHARACTER_MAXIMUM_LENGTH = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__CHARACTER_MAXIMUM_LENGTH;

    /**
     * The feature id for the '<em><b>Character Octet Length</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__CHARACTER_OCTET_LENGTH = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__CHARACTER_OCTET_LENGTH;

    /**
     * The feature id for the '<em><b>Numeric Precision</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__NUMERIC_PRECISION = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__NUMERIC_PRECISION;

    /**
     * The feature id for the '<em><b>Numeric Precision Radix</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__NUMERIC_PRECISION_RADIX = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__NUMERIC_PRECISION_RADIX;

    /**
     * The feature id for the '<em><b>Numeric Scale</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__NUMERIC_SCALE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__NUMERIC_SCALE;

    /**
     * The feature id for the '<em><b>Date Time Precision</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__DATE_TIME_PRECISION = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__DATE_TIME_PRECISION;

    /**
     * The feature id for the '<em><b>Sql Distinct Type</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__SQL_DISTINCT_TYPE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE__SQL_DISTINCT_TYPE;

    /**
     * The feature id for the '<em><b>Java Data Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__JAVA_DATA_TYPE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Nullable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__NULLABLE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Unsigned Attribute</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__UNSIGNED_ATTRIBUTE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE_FEATURE_COUNT + 2;

    /**
     * The feature id for the '<em><b>Case Sensitive</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__CASE_SENSITIVE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE_FEATURE_COUNT + 3;

    /**
     * The feature id for the '<em><b>Auto Increment</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__AUTO_INCREMENT = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE_FEATURE_COUNT + 4;

    /**
     * The feature id for the '<em><b>Local Type Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__LOCAL_TYPE_NAME = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE_FEATURE_COUNT + 5;

    /**
     * The feature id for the '<em><b>Searchable</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE__SEARCHABLE = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE_FEATURE_COUNT + 6;

    /**
     * The number of structural features of the '<em>Td Sql Data Type</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_SQL_DATA_TYPE_FEATURE_COUNT = orgomg.cwm.resource.relational.RelationalPackage.SQL_SIMPLE_TYPE_FEATURE_COUNT + 7;


    /**
     * The meta object id for the '{@link org.talend.cwm.relational.impl.TdTriggerImpl <em>Td Trigger</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.cwm.relational.impl.TdTriggerImpl
     * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdTrigger()
     * @generated
     */
    int TD_TRIGGER = 6;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__NAME = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__VISIBILITY = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__CLIENT_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__SUPPLIER_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__CONSTRAINT = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__NAMESPACE = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__IMPORTER = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__STEREOTYPE = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__TAGGED_VALUE = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__DOCUMENT = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__DESCRIPTION = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__RESPONSIBLE_PARTY = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__ELEMENT_NODE = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__SET = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__RENDERED_OBJECT = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__VOCABULARY_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__MEASUREMENT = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__CHANGE_REQUEST = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__DASDL_PROPERTY = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Event Manipulation</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__EVENT_MANIPULATION = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__EVENT_MANIPULATION;

    /**
     * The feature id for the '<em><b>Action Condition</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__ACTION_CONDITION = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__ACTION_CONDITION;

    /**
     * The feature id for the '<em><b>Action Statement</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__ACTION_STATEMENT = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__ACTION_STATEMENT;

    /**
     * The feature id for the '<em><b>Action Orientation</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__ACTION_ORIENTATION = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__ACTION_ORIENTATION;

    /**
     * The feature id for the '<em><b>Condition Timing</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__CONDITION_TIMING = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__CONDITION_TIMING;

    /**
     * The feature id for the '<em><b>Condition Reference New Table</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__CONDITION_REFERENCE_NEW_TABLE = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__CONDITION_REFERENCE_NEW_TABLE;

    /**
     * The feature id for the '<em><b>Condition Reference Old Table</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__CONDITION_REFERENCE_OLD_TABLE = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__CONDITION_REFERENCE_OLD_TABLE;

    /**
     * The feature id for the '<em><b>Used Column Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__USED_COLUMN_SET = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__USED_COLUMN_SET;

    /**
     * The feature id for the '<em><b>Table</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER__TABLE = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER__TABLE;

    /**
     * The number of structural features of the '<em>Td Trigger</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_TRIGGER_FEATURE_COUNT = orgomg.cwm.resource.relational.RelationalPackage.TRIGGER_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.talend.cwm.relational.impl.TdProcedureImpl <em>Td Procedure</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.cwm.relational.impl.TdProcedureImpl
     * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdProcedure()
     * @generated
     */
    int TD_PROCEDURE = 7;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__NAME = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__VISIBILITY = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__CLIENT_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__SUPPLIER_DEPENDENCY = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__CONSTRAINT = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__NAMESPACE = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__IMPORTER = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__STEREOTYPE = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__TAGGED_VALUE = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__DOCUMENT = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__DESCRIPTION = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__RESPONSIBLE_PARTY = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__ELEMENT_NODE = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__SET = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__RENDERED_OBJECT = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__VOCABULARY_ELEMENT = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__MEASUREMENT = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__CHANGE_REQUEST = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__DASDL_PROPERTY = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Owner Scope</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__OWNER_SCOPE = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__OWNER_SCOPE;

    /**
     * The feature id for the '<em><b>Owner</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__OWNER = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__OWNER;

    /**
     * The feature id for the '<em><b>Feature Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__FEATURE_NODE = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__FEATURE_NODE;

    /**
     * The feature id for the '<em><b>Feature Map</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__FEATURE_MAP = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__FEATURE_MAP;

    /**
     * The feature id for the '<em><b>Cf Map</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__CF_MAP = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__CF_MAP;

    /**
     * The feature id for the '<em><b>Is Query</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__IS_QUERY = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__IS_QUERY;

    /**
     * The feature id for the '<em><b>Parameter</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__PARAMETER = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__PARAMETER;

    /**
     * The feature id for the '<em><b>Body</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__BODY = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__BODY;

    /**
     * The feature id for the '<em><b>Specification</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__SPECIFICATION = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__SPECIFICATION;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE__TYPE = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE__TYPE;

    /**
     * The number of structural features of the '<em>Td Procedure</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_PROCEDURE_FEATURE_COUNT = orgomg.cwm.resource.relational.RelationalPackage.PROCEDURE_FEATURE_COUNT + 0;


    /**
     * Returns the meta object for class '{@link org.talend.cwm.relational.TdTable <em>Td Table</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Td Table</em>'.
     * @see org.talend.cwm.relational.TdTable
     * @generated
     */
    EClass getTdTable();

    /**
     * Returns the meta object for class '{@link org.talend.cwm.relational.TdView <em>Td View</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Td View</em>'.
     * @see org.talend.cwm.relational.TdView
     * @generated
     */
    EClass getTdView();

    /**
     * Returns the meta object for class '{@link org.talend.cwm.relational.TdCatalog <em>Td Catalog</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Td Catalog</em>'.
     * @see org.talend.cwm.relational.TdCatalog
     * @generated
     */
    EClass getTdCatalog();

    /**
     * Returns the meta object for class '{@link org.talend.cwm.relational.TdSchema <em>Td Schema</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Td Schema</em>'.
     * @see org.talend.cwm.relational.TdSchema
     * @generated
     */
    EClass getTdSchema();

    /**
     * Returns the meta object for class '{@link org.talend.cwm.relational.TdColumn <em>Td Column</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Td Column</em>'.
     * @see org.talend.cwm.relational.TdColumn
     * @generated
     */
    EClass getTdColumn();

    /**
     * Returns the meta object for the attribute '{@link org.talend.cwm.relational.TdColumn#getJavaType <em>Java Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Java Type</em>'.
     * @see org.talend.cwm.relational.TdColumn#getJavaType()
     * @see #getTdColumn()
     * @generated
     */
    EAttribute getTdColumn_JavaType();

    /**
     * Returns the meta object for the containment reference '{@link org.talend.cwm.relational.TdColumn#getSqlDataType <em>Sql Data Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Sql Data Type</em>'.
     * @see org.talend.cwm.relational.TdColumn#getSqlDataType()
     * @see #getTdColumn()
     * @generated
     */
    EReference getTdColumn_SqlDataType();

    /**
     * Returns the meta object for class '{@link org.talend.cwm.relational.TdSqlDataType <em>Td Sql Data Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Td Sql Data Type</em>'.
     * @see org.talend.cwm.relational.TdSqlDataType
     * @generated
     */
    EClass getTdSqlDataType();

    /**
     * Returns the meta object for the attribute '{@link org.talend.cwm.relational.TdSqlDataType#getJavaDataType <em>Java Data Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Java Data Type</em>'.
     * @see org.talend.cwm.relational.TdSqlDataType#getJavaDataType()
     * @see #getTdSqlDataType()
     * @generated
     */
    EAttribute getTdSqlDataType_JavaDataType();

    /**
     * Returns the meta object for the attribute '{@link org.talend.cwm.relational.TdSqlDataType#getNullable <em>Nullable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Nullable</em>'.
     * @see org.talend.cwm.relational.TdSqlDataType#getNullable()
     * @see #getTdSqlDataType()
     * @generated
     */
    EAttribute getTdSqlDataType_Nullable();

    /**
     * Returns the meta object for the attribute '{@link org.talend.cwm.relational.TdSqlDataType#isUnsignedAttribute <em>Unsigned Attribute</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Unsigned Attribute</em>'.
     * @see org.talend.cwm.relational.TdSqlDataType#isUnsignedAttribute()
     * @see #getTdSqlDataType()
     * @generated
     */
    EAttribute getTdSqlDataType_UnsignedAttribute();

    /**
     * Returns the meta object for the attribute '{@link org.talend.cwm.relational.TdSqlDataType#isCaseSensitive <em>Case Sensitive</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Case Sensitive</em>'.
     * @see org.talend.cwm.relational.TdSqlDataType#isCaseSensitive()
     * @see #getTdSqlDataType()
     * @generated
     */
    EAttribute getTdSqlDataType_CaseSensitive();

    /**
     * Returns the meta object for the attribute '{@link org.talend.cwm.relational.TdSqlDataType#isAutoIncrement <em>Auto Increment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Auto Increment</em>'.
     * @see org.talend.cwm.relational.TdSqlDataType#isAutoIncrement()
     * @see #getTdSqlDataType()
     * @generated
     */
    EAttribute getTdSqlDataType_AutoIncrement();

    /**
     * Returns the meta object for the attribute '{@link org.talend.cwm.relational.TdSqlDataType#getLocalTypeName <em>Local Type Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Local Type Name</em>'.
     * @see org.talend.cwm.relational.TdSqlDataType#getLocalTypeName()
     * @see #getTdSqlDataType()
     * @generated
     */
    EAttribute getTdSqlDataType_LocalTypeName();

    /**
     * Returns the meta object for the attribute '{@link org.talend.cwm.relational.TdSqlDataType#getSearchable <em>Searchable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Searchable</em>'.
     * @see org.talend.cwm.relational.TdSqlDataType#getSearchable()
     * @see #getTdSqlDataType()
     * @generated
     */
    EAttribute getTdSqlDataType_Searchable();

    /**
     * Returns the meta object for class '{@link org.talend.cwm.relational.TdTrigger <em>Td Trigger</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Td Trigger</em>'.
     * @see org.talend.cwm.relational.TdTrigger
     * @generated
     */
    EClass getTdTrigger();

    /**
     * Returns the meta object for class '{@link org.talend.cwm.relational.TdProcedure <em>Td Procedure</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Td Procedure</em>'.
     * @see org.talend.cwm.relational.TdProcedure
     * @generated
     */
    EClass getTdProcedure();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    RelationalFactory getRelationalFactory();

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
         * The meta object literal for the '{@link org.talend.cwm.relational.impl.TdTableImpl <em>Td Table</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.cwm.relational.impl.TdTableImpl
         * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdTable()
         * @generated
         */
        EClass TD_TABLE = eINSTANCE.getTdTable();

        /**
         * The meta object literal for the '{@link org.talend.cwm.relational.impl.TdViewImpl <em>Td View</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.cwm.relational.impl.TdViewImpl
         * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdView()
         * @generated
         */
        EClass TD_VIEW = eINSTANCE.getTdView();

        /**
         * The meta object literal for the '{@link org.talend.cwm.relational.impl.TdCatalogImpl <em>Td Catalog</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.cwm.relational.impl.TdCatalogImpl
         * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdCatalog()
         * @generated
         */
        EClass TD_CATALOG = eINSTANCE.getTdCatalog();

        /**
         * The meta object literal for the '{@link org.talend.cwm.relational.impl.TdSchemaImpl <em>Td Schema</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.cwm.relational.impl.TdSchemaImpl
         * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdSchema()
         * @generated
         */
        EClass TD_SCHEMA = eINSTANCE.getTdSchema();

        /**
         * The meta object literal for the '{@link org.talend.cwm.relational.impl.TdColumnImpl <em>Td Column</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.cwm.relational.impl.TdColumnImpl
         * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdColumn()
         * @generated
         */
        EClass TD_COLUMN = eINSTANCE.getTdColumn();

        /**
         * The meta object literal for the '<em><b>Java Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TD_COLUMN__JAVA_TYPE = eINSTANCE.getTdColumn_JavaType();

        /**
         * The meta object literal for the '<em><b>Sql Data Type</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TD_COLUMN__SQL_DATA_TYPE = eINSTANCE.getTdColumn_SqlDataType();

        /**
         * The meta object literal for the '{@link org.talend.cwm.relational.impl.TdSqlDataTypeImpl <em>Td Sql Data Type</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.cwm.relational.impl.TdSqlDataTypeImpl
         * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdSqlDataType()
         * @generated
         */
        EClass TD_SQL_DATA_TYPE = eINSTANCE.getTdSqlDataType();

        /**
         * The meta object literal for the '<em><b>Java Data Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TD_SQL_DATA_TYPE__JAVA_DATA_TYPE = eINSTANCE.getTdSqlDataType_JavaDataType();

        /**
         * The meta object literal for the '<em><b>Nullable</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TD_SQL_DATA_TYPE__NULLABLE = eINSTANCE.getTdSqlDataType_Nullable();

        /**
         * The meta object literal for the '<em><b>Unsigned Attribute</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TD_SQL_DATA_TYPE__UNSIGNED_ATTRIBUTE = eINSTANCE.getTdSqlDataType_UnsignedAttribute();

        /**
         * The meta object literal for the '<em><b>Case Sensitive</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TD_SQL_DATA_TYPE__CASE_SENSITIVE = eINSTANCE.getTdSqlDataType_CaseSensitive();

        /**
         * The meta object literal for the '<em><b>Auto Increment</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TD_SQL_DATA_TYPE__AUTO_INCREMENT = eINSTANCE.getTdSqlDataType_AutoIncrement();

        /**
         * The meta object literal for the '<em><b>Local Type Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TD_SQL_DATA_TYPE__LOCAL_TYPE_NAME = eINSTANCE.getTdSqlDataType_LocalTypeName();

        /**
         * The meta object literal for the '<em><b>Searchable</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TD_SQL_DATA_TYPE__SEARCHABLE = eINSTANCE.getTdSqlDataType_Searchable();

        /**
         * The meta object literal for the '{@link org.talend.cwm.relational.impl.TdTriggerImpl <em>Td Trigger</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.cwm.relational.impl.TdTriggerImpl
         * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdTrigger()
         * @generated
         */
        EClass TD_TRIGGER = eINSTANCE.getTdTrigger();

        /**
         * The meta object literal for the '{@link org.talend.cwm.relational.impl.TdProcedureImpl <em>Td Procedure</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.cwm.relational.impl.TdProcedureImpl
         * @see org.talend.cwm.relational.impl.RelationalPackageImpl#getTdProcedure()
         * @generated
         */
        EClass TD_PROCEDURE = eINSTANCE.getTdProcedure();

    }

} //RelationalPackage
