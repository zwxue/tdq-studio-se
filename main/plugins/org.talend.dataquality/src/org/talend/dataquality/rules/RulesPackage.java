/**
 * // ============================================================================
 * //
 * // Copyright (C) 2006-2016 Talend Inc. - www.talend.com
 * //
 * // This source code is available under agreement available at
 * // %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * //
 * // You should have received a copy of the agreement
 * // along with this program; if not, write to Talend SA
 * // 9 rue Pages 92150 Suresnes, France
 * //
 * // ============================================================================
 * 
 *
 * $Id$
 */
package org.talend.dataquality.rules;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.talend.dataquality.indicators.definition.DefinitionPackage;

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
 * @see org.talend.dataquality.rules.RulesFactory
 * @model kind="package"
 * @generated
 */
public interface RulesPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "rules";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://dataquality.rules";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "dataquality.rules";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    RulesPackage eINSTANCE = org.talend.dataquality.rules.impl.RulesPackageImpl.init();

    /**
     * The meta object id for the '{@link org.talend.dataquality.rules.impl.DQRuleImpl <em>DQ Rule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.rules.impl.DQRuleImpl
     * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getDQRule()
     * @generated
     */
    int DQ_RULE = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__NAME = DefinitionPackage.INDICATOR_DEFINITION__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__VISIBILITY = DefinitionPackage.INDICATOR_DEFINITION__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__CLIENT_DEPENDENCY = DefinitionPackage.INDICATOR_DEFINITION__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__SUPPLIER_DEPENDENCY = DefinitionPackage.INDICATOR_DEFINITION__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__CONSTRAINT = DefinitionPackage.INDICATOR_DEFINITION__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__NAMESPACE = DefinitionPackage.INDICATOR_DEFINITION__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__IMPORTER = DefinitionPackage.INDICATOR_DEFINITION__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__STEREOTYPE = DefinitionPackage.INDICATOR_DEFINITION__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__TAGGED_VALUE = DefinitionPackage.INDICATOR_DEFINITION__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__DOCUMENT = DefinitionPackage.INDICATOR_DEFINITION__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__DESCRIPTION = DefinitionPackage.INDICATOR_DEFINITION__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__RESPONSIBLE_PARTY = DefinitionPackage.INDICATOR_DEFINITION__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__ELEMENT_NODE = DefinitionPackage.INDICATOR_DEFINITION__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__SET = DefinitionPackage.INDICATOR_DEFINITION__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__RENDERED_OBJECT = DefinitionPackage.INDICATOR_DEFINITION__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__VOCABULARY_ELEMENT = DefinitionPackage.INDICATOR_DEFINITION__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__MEASUREMENT = DefinitionPackage.INDICATOR_DEFINITION__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__CHANGE_REQUEST = DefinitionPackage.INDICATOR_DEFINITION__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__DASDL_PROPERTY = DefinitionPackage.INDICATOR_DEFINITION__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Categories</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__CATEGORIES = DefinitionPackage.INDICATOR_DEFINITION__CATEGORIES;

    /**
     * The feature id for the '<em><b>Aggregated Definitions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__AGGREGATED_DEFINITIONS = DefinitionPackage.INDICATOR_DEFINITION__AGGREGATED_DEFINITIONS;

    /**
     * The feature id for the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__LABEL = DefinitionPackage.INDICATOR_DEFINITION__LABEL;

    /**
     * The feature id for the '<em><b>Sub Categories</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__SUB_CATEGORIES = DefinitionPackage.INDICATOR_DEFINITION__SUB_CATEGORIES;

    /**
     * The feature id for the '<em><b>Sql Generic Expression</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__SQL_GENERIC_EXPRESSION = DefinitionPackage.INDICATOR_DEFINITION__SQL_GENERIC_EXPRESSION;

    /**
     * The feature id for the '<em><b>Aggregate1arg Functions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__AGGREGATE1ARG_FUNCTIONS = DefinitionPackage.INDICATOR_DEFINITION__AGGREGATE1ARG_FUNCTIONS;

    /**
     * The feature id for the '<em><b>Date1arg Functions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__DATE1ARG_FUNCTIONS = DefinitionPackage.INDICATOR_DEFINITION__DATE1ARG_FUNCTIONS;

    /**
     * The feature id for the '<em><b>Characters Mapping</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__CHARACTERS_MAPPING = DefinitionPackage.INDICATOR_DEFINITION__CHARACTERS_MAPPING;

    /**
     * The feature id for the '<em><b>Indicator Definition Parameter</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int DQ_RULE__INDICATOR_DEFINITION_PARAMETER = DefinitionPackage.INDICATOR_DEFINITION__INDICATOR_DEFINITION_PARAMETER;

				/**
     * The feature id for the '<em><b>Criticality Level</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__CRITICALITY_LEVEL = DefinitionPackage.INDICATOR_DEFINITION_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Elements</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE__ELEMENTS = DefinitionPackage.INDICATOR_DEFINITION_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>DQ Rule</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DQ_RULE_FEATURE_COUNT = DefinitionPackage.INDICATOR_DEFINITION_FEATURE_COUNT + 2;

    /**
     * The meta object id for the '{@link org.talend.dataquality.rules.impl.SpecifiedDQRuleImpl <em>Specified DQ Rule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.rules.impl.SpecifiedDQRuleImpl
     * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getSpecifiedDQRule()
     * @generated
     */
    int SPECIFIED_DQ_RULE = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__NAME = DQ_RULE__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__VISIBILITY = DQ_RULE__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__CLIENT_DEPENDENCY = DQ_RULE__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__SUPPLIER_DEPENDENCY = DQ_RULE__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__CONSTRAINT = DQ_RULE__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__NAMESPACE = DQ_RULE__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__IMPORTER = DQ_RULE__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__STEREOTYPE = DQ_RULE__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__TAGGED_VALUE = DQ_RULE__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__DOCUMENT = DQ_RULE__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__DESCRIPTION = DQ_RULE__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__RESPONSIBLE_PARTY = DQ_RULE__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__ELEMENT_NODE = DQ_RULE__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__SET = DQ_RULE__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__RENDERED_OBJECT = DQ_RULE__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__VOCABULARY_ELEMENT = DQ_RULE__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__MEASUREMENT = DQ_RULE__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__CHANGE_REQUEST = DQ_RULE__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__DASDL_PROPERTY = DQ_RULE__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Categories</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__CATEGORIES = DQ_RULE__CATEGORIES;

    /**
     * The feature id for the '<em><b>Aggregated Definitions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__AGGREGATED_DEFINITIONS = DQ_RULE__AGGREGATED_DEFINITIONS;

    /**
     * The feature id for the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__LABEL = DQ_RULE__LABEL;

    /**
     * The feature id for the '<em><b>Sub Categories</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__SUB_CATEGORIES = DQ_RULE__SUB_CATEGORIES;

    /**
     * The feature id for the '<em><b>Sql Generic Expression</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__SQL_GENERIC_EXPRESSION = DQ_RULE__SQL_GENERIC_EXPRESSION;

    /**
     * The feature id for the '<em><b>Aggregate1arg Functions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__AGGREGATE1ARG_FUNCTIONS = DQ_RULE__AGGREGATE1ARG_FUNCTIONS;

    /**
     * The feature id for the '<em><b>Date1arg Functions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__DATE1ARG_FUNCTIONS = DQ_RULE__DATE1ARG_FUNCTIONS;

    /**
     * The feature id for the '<em><b>Characters Mapping</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__CHARACTERS_MAPPING = DQ_RULE__CHARACTERS_MAPPING;

    /**
     * The feature id for the '<em><b>Indicator Definition Parameter</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int SPECIFIED_DQ_RULE__INDICATOR_DEFINITION_PARAMETER = DQ_RULE__INDICATOR_DEFINITION_PARAMETER;

				/**
     * The feature id for the '<em><b>Criticality Level</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__CRITICALITY_LEVEL = DQ_RULE__CRITICALITY_LEVEL;

    /**
     * The feature id for the '<em><b>Elements</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE__ELEMENTS = DQ_RULE__ELEMENTS;

    /**
     * The number of structural features of the '<em>Specified DQ Rule</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SPECIFIED_DQ_RULE_FEATURE_COUNT = DQ_RULE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.talend.dataquality.rules.impl.InferredDQRuleImpl <em>Inferred DQ Rule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.rules.impl.InferredDQRuleImpl
     * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getInferredDQRule()
     * @generated
     */
    int INFERRED_DQ_RULE = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__NAME = DQ_RULE__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__VISIBILITY = DQ_RULE__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__CLIENT_DEPENDENCY = DQ_RULE__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__SUPPLIER_DEPENDENCY = DQ_RULE__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__CONSTRAINT = DQ_RULE__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__NAMESPACE = DQ_RULE__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__IMPORTER = DQ_RULE__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__STEREOTYPE = DQ_RULE__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__TAGGED_VALUE = DQ_RULE__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__DOCUMENT = DQ_RULE__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__DESCRIPTION = DQ_RULE__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__RESPONSIBLE_PARTY = DQ_RULE__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__ELEMENT_NODE = DQ_RULE__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__SET = DQ_RULE__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__RENDERED_OBJECT = DQ_RULE__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__VOCABULARY_ELEMENT = DQ_RULE__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__MEASUREMENT = DQ_RULE__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__CHANGE_REQUEST = DQ_RULE__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__DASDL_PROPERTY = DQ_RULE__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Categories</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__CATEGORIES = DQ_RULE__CATEGORIES;

    /**
     * The feature id for the '<em><b>Aggregated Definitions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__AGGREGATED_DEFINITIONS = DQ_RULE__AGGREGATED_DEFINITIONS;

    /**
     * The feature id for the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__LABEL = DQ_RULE__LABEL;

    /**
     * The feature id for the '<em><b>Sub Categories</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__SUB_CATEGORIES = DQ_RULE__SUB_CATEGORIES;

    /**
     * The feature id for the '<em><b>Sql Generic Expression</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__SQL_GENERIC_EXPRESSION = DQ_RULE__SQL_GENERIC_EXPRESSION;

    /**
     * The feature id for the '<em><b>Aggregate1arg Functions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__AGGREGATE1ARG_FUNCTIONS = DQ_RULE__AGGREGATE1ARG_FUNCTIONS;

    /**
     * The feature id for the '<em><b>Date1arg Functions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__DATE1ARG_FUNCTIONS = DQ_RULE__DATE1ARG_FUNCTIONS;

    /**
     * The feature id for the '<em><b>Characters Mapping</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__CHARACTERS_MAPPING = DQ_RULE__CHARACTERS_MAPPING;

    /**
     * The feature id for the '<em><b>Indicator Definition Parameter</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int INFERRED_DQ_RULE__INDICATOR_DEFINITION_PARAMETER = DQ_RULE__INDICATOR_DEFINITION_PARAMETER;

				/**
     * The feature id for the '<em><b>Criticality Level</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__CRITICALITY_LEVEL = DQ_RULE__CRITICALITY_LEVEL;

    /**
     * The feature id for the '<em><b>Elements</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE__ELEMENTS = DQ_RULE__ELEMENTS;

    /**
     * The number of structural features of the '<em>Inferred DQ Rule</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int INFERRED_DQ_RULE_FEATURE_COUNT = DQ_RULE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.talend.dataquality.rules.impl.WhereRuleImpl <em>Where Rule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.rules.impl.WhereRuleImpl
     * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getWhereRule()
     * @generated
     */
    int WHERE_RULE = 3;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__NAME = SPECIFIED_DQ_RULE__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__VISIBILITY = SPECIFIED_DQ_RULE__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__CLIENT_DEPENDENCY = SPECIFIED_DQ_RULE__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__SUPPLIER_DEPENDENCY = SPECIFIED_DQ_RULE__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__CONSTRAINT = SPECIFIED_DQ_RULE__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__NAMESPACE = SPECIFIED_DQ_RULE__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__IMPORTER = SPECIFIED_DQ_RULE__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__STEREOTYPE = SPECIFIED_DQ_RULE__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__TAGGED_VALUE = SPECIFIED_DQ_RULE__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__DOCUMENT = SPECIFIED_DQ_RULE__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__DESCRIPTION = SPECIFIED_DQ_RULE__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__RESPONSIBLE_PARTY = SPECIFIED_DQ_RULE__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__ELEMENT_NODE = SPECIFIED_DQ_RULE__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__SET = SPECIFIED_DQ_RULE__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__RENDERED_OBJECT = SPECIFIED_DQ_RULE__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__VOCABULARY_ELEMENT = SPECIFIED_DQ_RULE__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__MEASUREMENT = SPECIFIED_DQ_RULE__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__CHANGE_REQUEST = SPECIFIED_DQ_RULE__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__DASDL_PROPERTY = SPECIFIED_DQ_RULE__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Categories</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__CATEGORIES = SPECIFIED_DQ_RULE__CATEGORIES;

    /**
     * The feature id for the '<em><b>Aggregated Definitions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__AGGREGATED_DEFINITIONS = SPECIFIED_DQ_RULE__AGGREGATED_DEFINITIONS;

    /**
     * The feature id for the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__LABEL = SPECIFIED_DQ_RULE__LABEL;

    /**
     * The feature id for the '<em><b>Sub Categories</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__SUB_CATEGORIES = SPECIFIED_DQ_RULE__SUB_CATEGORIES;

    /**
     * The feature id for the '<em><b>Sql Generic Expression</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__SQL_GENERIC_EXPRESSION = SPECIFIED_DQ_RULE__SQL_GENERIC_EXPRESSION;

    /**
     * The feature id for the '<em><b>Aggregate1arg Functions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__AGGREGATE1ARG_FUNCTIONS = SPECIFIED_DQ_RULE__AGGREGATE1ARG_FUNCTIONS;

    /**
     * The feature id for the '<em><b>Date1arg Functions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__DATE1ARG_FUNCTIONS = SPECIFIED_DQ_RULE__DATE1ARG_FUNCTIONS;

    /**
     * The feature id for the '<em><b>Characters Mapping</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__CHARACTERS_MAPPING = SPECIFIED_DQ_RULE__CHARACTERS_MAPPING;

    /**
     * The feature id for the '<em><b>Indicator Definition Parameter</b></em>' containment reference list.
     * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
	int WHERE_RULE__INDICATOR_DEFINITION_PARAMETER = SPECIFIED_DQ_RULE__INDICATOR_DEFINITION_PARAMETER;

				/**
     * The feature id for the '<em><b>Criticality Level</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__CRITICALITY_LEVEL = SPECIFIED_DQ_RULE__CRITICALITY_LEVEL;

    /**
     * The feature id for the '<em><b>Elements</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__ELEMENTS = SPECIFIED_DQ_RULE__ELEMENTS;

    /**
     * The feature id for the '<em><b>Where Expression</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__WHERE_EXPRESSION = SPECIFIED_DQ_RULE_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Join Expression</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__JOIN_EXPRESSION = SPECIFIED_DQ_RULE_FEATURE_COUNT + 1;

    /**
     * The feature id for the '<em><b>Joins</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE__JOINS = SPECIFIED_DQ_RULE_FEATURE_COUNT + 2;

    /**
     * The number of structural features of the '<em>Where Rule</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int WHERE_RULE_FEATURE_COUNT = SPECIFIED_DQ_RULE_FEATURE_COUNT + 3;


    /**
     * The meta object id for the '{@link org.talend.dataquality.rules.impl.JoinElementImpl <em>Join Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.rules.impl.JoinElementImpl
     * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getJoinElement()
     * @generated
     */
    int JOIN_ELEMENT = 4;

    /**
     * The feature id for the '<em><b>Col A</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_ELEMENT__COL_A = 0;

    /**
     * The feature id for the '<em><b>Col B</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_ELEMENT__COL_B = 1;

    /**
     * The feature id for the '<em><b>Operator</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_ELEMENT__OPERATOR = 2;

    /**
     * The feature id for the '<em><b>Table Alias A</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_ELEMENT__TABLE_ALIAS_A = 3;

    /**
     * The feature id for the '<em><b>Table Alias B</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_ELEMENT__TABLE_ALIAS_B = 4;

    /**
     * The feature id for the '<em><b>Column Alias A</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_ELEMENT__COLUMN_ALIAS_A = 5;

    /**
     * The feature id for the '<em><b>Column Alias B</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_ELEMENT__COLUMN_ALIAS_B = 6;

    /**
     * The number of structural features of the '<em>Join Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int JOIN_ELEMENT_FEATURE_COUNT = 7;


    /**
     * The meta object id for the '{@link org.talend.dataquality.rules.impl.ParserRuleImpl <em>Parser Rule</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.rules.impl.ParserRuleImpl
     * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getParserRule()
     * @generated
     */
    int PARSER_RULE = 5;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__NAME = DQ_RULE__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__VISIBILITY = DQ_RULE__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__CLIENT_DEPENDENCY = DQ_RULE__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__SUPPLIER_DEPENDENCY = DQ_RULE__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__CONSTRAINT = DQ_RULE__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__NAMESPACE = DQ_RULE__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__IMPORTER = DQ_RULE__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__STEREOTYPE = DQ_RULE__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__TAGGED_VALUE = DQ_RULE__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__DOCUMENT = DQ_RULE__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__DESCRIPTION = DQ_RULE__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__RESPONSIBLE_PARTY = DQ_RULE__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__ELEMENT_NODE = DQ_RULE__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__SET = DQ_RULE__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__RENDERED_OBJECT = DQ_RULE__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__VOCABULARY_ELEMENT = DQ_RULE__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__MEASUREMENT = DQ_RULE__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__CHANGE_REQUEST = DQ_RULE__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__DASDL_PROPERTY = DQ_RULE__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Categories</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__CATEGORIES = DQ_RULE__CATEGORIES;

    /**
     * The feature id for the '<em><b>Aggregated Definitions</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__AGGREGATED_DEFINITIONS = DQ_RULE__AGGREGATED_DEFINITIONS;

    /**
     * The feature id for the '<em><b>Label</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__LABEL = DQ_RULE__LABEL;

    /**
     * The feature id for the '<em><b>Sub Categories</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__SUB_CATEGORIES = DQ_RULE__SUB_CATEGORIES;

    /**
     * The feature id for the '<em><b>Sql Generic Expression</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__SQL_GENERIC_EXPRESSION = DQ_RULE__SQL_GENERIC_EXPRESSION;

    /**
     * The feature id for the '<em><b>Aggregate1arg Functions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__AGGREGATE1ARG_FUNCTIONS = DQ_RULE__AGGREGATE1ARG_FUNCTIONS;

    /**
     * The feature id for the '<em><b>Date1arg Functions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__DATE1ARG_FUNCTIONS = DQ_RULE__DATE1ARG_FUNCTIONS;

    /**
     * The feature id for the '<em><b>Characters Mapping</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__CHARACTERS_MAPPING = DQ_RULE__CHARACTERS_MAPPING;

    /**
     * The feature id for the '<em><b>Indicator Definition Parameter</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__INDICATOR_DEFINITION_PARAMETER = DQ_RULE__INDICATOR_DEFINITION_PARAMETER;

    /**
     * The feature id for the '<em><b>Criticality Level</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__CRITICALITY_LEVEL = DQ_RULE__CRITICALITY_LEVEL;

    /**
     * The feature id for the '<em><b>Elements</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE__ELEMENTS = DQ_RULE__ELEMENTS;

    /**
     * The number of structural features of the '<em>Parser Rule</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PARSER_RULE_FEATURE_COUNT = DQ_RULE_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '<em>Td Expression List</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.util.List
     * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getTdExpressionList()
     * @generated
     */
    int TD_EXPRESSION_LIST = 6;


    /**
     * Returns the meta object for class '{@link org.talend.dataquality.rules.DQRule <em>DQ Rule</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>DQ Rule</em>'.
     * @see org.talend.dataquality.rules.DQRule
     * @generated
     */
    EClass getDQRule();

    /**
     * Returns the meta object for the attribute '{@link org.talend.dataquality.rules.DQRule#getCriticalityLevel <em>Criticality Level</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Criticality Level</em>'.
     * @see org.talend.dataquality.rules.DQRule#getCriticalityLevel()
     * @see #getDQRule()
     * @generated
     */
    EAttribute getDQRule_CriticalityLevel();

    /**
     * Returns the meta object for the reference list '{@link org.talend.dataquality.rules.DQRule#getElements <em>Elements</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Elements</em>'.
     * @see org.talend.dataquality.rules.DQRule#getElements()
     * @see #getDQRule()
     * @generated
     */
    EReference getDQRule_Elements();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.rules.SpecifiedDQRule <em>Specified DQ Rule</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Specified DQ Rule</em>'.
     * @see org.talend.dataquality.rules.SpecifiedDQRule
     * @generated
     */
    EClass getSpecifiedDQRule();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.rules.InferredDQRule <em>Inferred DQ Rule</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Inferred DQ Rule</em>'.
     * @see org.talend.dataquality.rules.InferredDQRule
     * @generated
     */
    EClass getInferredDQRule();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.rules.WhereRule <em>Where Rule</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Where Rule</em>'.
     * @see org.talend.dataquality.rules.WhereRule
     * @generated
     */
    EClass getWhereRule();

    /**
     * Returns the meta object for the attribute '{@link org.talend.dataquality.rules.WhereRule#getWhereExpression <em>Where Expression</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Where Expression</em>'.
     * @see org.talend.dataquality.rules.WhereRule#getWhereExpression()
     * @see #getWhereRule()
     * @generated
     */
    EAttribute getWhereRule_WhereExpression();

    /**
     * Returns the meta object for the attribute '{@link org.talend.dataquality.rules.WhereRule#getJoinExpression <em>Join Expression</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Join Expression</em>'.
     * @see org.talend.dataquality.rules.WhereRule#getJoinExpression()
     * @see #getWhereRule()
     * @generated
     */
    EAttribute getWhereRule_JoinExpression();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.dataquality.rules.WhereRule#getJoins <em>Joins</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Joins</em>'.
     * @see org.talend.dataquality.rules.WhereRule#getJoins()
     * @see #getWhereRule()
     * @generated
     */
    EReference getWhereRule_Joins();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.rules.JoinElement <em>Join Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Join Element</em>'.
     * @see org.talend.dataquality.rules.JoinElement
     * @generated
     */
    EClass getJoinElement();

    /**
     * Returns the meta object for the reference '{@link org.talend.dataquality.rules.JoinElement#getColA <em>Col A</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Col A</em>'.
     * @see org.talend.dataquality.rules.JoinElement#getColA()
     * @see #getJoinElement()
     * @generated
     */
    EReference getJoinElement_ColA();

    /**
     * Returns the meta object for the reference '{@link org.talend.dataquality.rules.JoinElement#getColB <em>Col B</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Col B</em>'.
     * @see org.talend.dataquality.rules.JoinElement#getColB()
     * @see #getJoinElement()
     * @generated
     */
    EReference getJoinElement_ColB();

    /**
     * Returns the meta object for the attribute '{@link org.talend.dataquality.rules.JoinElement#getOperator <em>Operator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Operator</em>'.
     * @see org.talend.dataquality.rules.JoinElement#getOperator()
     * @see #getJoinElement()
     * @generated
     */
    EAttribute getJoinElement_Operator();

    /**
     * Returns the meta object for the attribute '{@link org.talend.dataquality.rules.JoinElement#getTableAliasA <em>Table Alias A</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Table Alias A</em>'.
     * @see org.talend.dataquality.rules.JoinElement#getTableAliasA()
     * @see #getJoinElement()
     * @generated
     */
    EAttribute getJoinElement_TableAliasA();

    /**
     * Returns the meta object for the attribute '{@link org.talend.dataquality.rules.JoinElement#getTableAliasB <em>Table Alias B</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Table Alias B</em>'.
     * @see org.talend.dataquality.rules.JoinElement#getTableAliasB()
     * @see #getJoinElement()
     * @generated
     */
    EAttribute getJoinElement_TableAliasB();

    /**
     * Returns the meta object for the attribute '{@link org.talend.dataquality.rules.JoinElement#getColumnAliasA <em>Column Alias A</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Column Alias A</em>'.
     * @see org.talend.dataquality.rules.JoinElement#getColumnAliasA()
     * @see #getJoinElement()
     * @generated
     */
    EAttribute getJoinElement_ColumnAliasA();

    /**
     * Returns the meta object for the attribute '{@link org.talend.dataquality.rules.JoinElement#getColumnAliasB <em>Column Alias B</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Column Alias B</em>'.
     * @see org.talend.dataquality.rules.JoinElement#getColumnAliasB()
     * @see #getJoinElement()
     * @generated
     */
    EAttribute getJoinElement_ColumnAliasB();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.rules.ParserRule <em>Parser Rule</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Parser Rule</em>'.
     * @see org.talend.dataquality.rules.ParserRule
     * @generated
     */
    EClass getParserRule();

    /**
     * Returns the meta object for data type '{@link java.util.List <em>Td Expression List</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>Td Expression List</em>'.
     * @see java.util.List
     * @model instanceClass="java.util.List<org.talend.cwm.relational.TdExpression>"
     * @generated
     */
    EDataType getTdExpressionList();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    RulesFactory getRulesFactory();

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
         * The meta object literal for the '{@link org.talend.dataquality.rules.impl.DQRuleImpl <em>DQ Rule</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.rules.impl.DQRuleImpl
         * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getDQRule()
         * @generated
         */
        EClass DQ_RULE = eINSTANCE.getDQRule();

        /**
         * The meta object literal for the '<em><b>Criticality Level</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute DQ_RULE__CRITICALITY_LEVEL = eINSTANCE.getDQRule_CriticalityLevel();

        /**
         * The meta object literal for the '<em><b>Elements</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference DQ_RULE__ELEMENTS = eINSTANCE.getDQRule_Elements();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.rules.impl.SpecifiedDQRuleImpl <em>Specified DQ Rule</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.rules.impl.SpecifiedDQRuleImpl
         * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getSpecifiedDQRule()
         * @generated
         */
        EClass SPECIFIED_DQ_RULE = eINSTANCE.getSpecifiedDQRule();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.rules.impl.InferredDQRuleImpl <em>Inferred DQ Rule</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.rules.impl.InferredDQRuleImpl
         * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getInferredDQRule()
         * @generated
         */
        EClass INFERRED_DQ_RULE = eINSTANCE.getInferredDQRule();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.rules.impl.WhereRuleImpl <em>Where Rule</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.rules.impl.WhereRuleImpl
         * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getWhereRule()
         * @generated
         */
        EClass WHERE_RULE = eINSTANCE.getWhereRule();

        /**
         * The meta object literal for the '<em><b>Where Expression</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WHERE_RULE__WHERE_EXPRESSION = eINSTANCE.getWhereRule_WhereExpression();

        /**
         * The meta object literal for the '<em><b>Join Expression</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute WHERE_RULE__JOIN_EXPRESSION = eINSTANCE.getWhereRule_JoinExpression();

        /**
         * The meta object literal for the '<em><b>Joins</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference WHERE_RULE__JOINS = eINSTANCE.getWhereRule_Joins();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.rules.impl.JoinElementImpl <em>Join Element</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.rules.impl.JoinElementImpl
         * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getJoinElement()
         * @generated
         */
        EClass JOIN_ELEMENT = eINSTANCE.getJoinElement();

        /**
         * The meta object literal for the '<em><b>Col A</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference JOIN_ELEMENT__COL_A = eINSTANCE.getJoinElement_ColA();

        /**
         * The meta object literal for the '<em><b>Col B</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference JOIN_ELEMENT__COL_B = eINSTANCE.getJoinElement_ColB();

        /**
         * The meta object literal for the '<em><b>Operator</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute JOIN_ELEMENT__OPERATOR = eINSTANCE.getJoinElement_Operator();

        /**
         * The meta object literal for the '<em><b>Table Alias A</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute JOIN_ELEMENT__TABLE_ALIAS_A = eINSTANCE.getJoinElement_TableAliasA();

        /**
         * The meta object literal for the '<em><b>Table Alias B</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute JOIN_ELEMENT__TABLE_ALIAS_B = eINSTANCE.getJoinElement_TableAliasB();

        /**
         * The meta object literal for the '<em><b>Column Alias A</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute JOIN_ELEMENT__COLUMN_ALIAS_A = eINSTANCE.getJoinElement_ColumnAliasA();

        /**
         * The meta object literal for the '<em><b>Column Alias B</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute JOIN_ELEMENT__COLUMN_ALIAS_B = eINSTANCE.getJoinElement_ColumnAliasB();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.rules.impl.ParserRuleImpl <em>Parser Rule</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.rules.impl.ParserRuleImpl
         * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getParserRule()
         * @generated
         */
        EClass PARSER_RULE = eINSTANCE.getParserRule();

        /**
         * The meta object literal for the '<em>Td Expression List</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.util.List
         * @see org.talend.dataquality.rules.impl.RulesPackageImpl#getTdExpressionList()
         * @generated
         */
        EDataType TD_EXPRESSION_LIST = eINSTANCE.getTdExpressionList();

    }

} //RulesPackage
