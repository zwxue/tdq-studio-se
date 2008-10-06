/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.columnset;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.talend.dataquality.indicators.IndicatorsPackage;

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
 * @see org.talend.dataquality.indicators.columnset.ColumnsetFactory
 * @model kind="package"
 * @generated
 */
public interface ColumnsetPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "columnset";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://dataquality.indicators.columnset";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "dataquality.indicators.columnset";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ColumnsetPackage eINSTANCE = org.talend.dataquality.indicators.columnset.impl.ColumnsetPackageImpl.init();

    /**
     * The meta object id for the '{@link org.talend.dataquality.indicators.columnset.impl.ColumnsCompareIndicatorImpl <em>Columns Compare Indicator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.indicators.columnset.impl.ColumnsCompareIndicatorImpl
     * @see org.talend.dataquality.indicators.columnset.impl.ColumnsetPackageImpl#getColumnsCompareIndicator()
     * @generated
     */
    int COLUMNS_COMPARE_INDICATOR = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__NAME = IndicatorsPackage.MATCHING_INDICATOR__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__VISIBILITY = IndicatorsPackage.MATCHING_INDICATOR__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__CLIENT_DEPENDENCY = IndicatorsPackage.MATCHING_INDICATOR__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__SUPPLIER_DEPENDENCY = IndicatorsPackage.MATCHING_INDICATOR__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__CONSTRAINT = IndicatorsPackage.MATCHING_INDICATOR__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__NAMESPACE = IndicatorsPackage.MATCHING_INDICATOR__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__IMPORTER = IndicatorsPackage.MATCHING_INDICATOR__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__STEREOTYPE = IndicatorsPackage.MATCHING_INDICATOR__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__TAGGED_VALUE = IndicatorsPackage.MATCHING_INDICATOR__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__DOCUMENT = IndicatorsPackage.MATCHING_INDICATOR__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__DESCRIPTION = IndicatorsPackage.MATCHING_INDICATOR__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__RESPONSIBLE_PARTY = IndicatorsPackage.MATCHING_INDICATOR__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__ELEMENT_NODE = IndicatorsPackage.MATCHING_INDICATOR__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__SET = IndicatorsPackage.MATCHING_INDICATOR__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__RENDERED_OBJECT = IndicatorsPackage.MATCHING_INDICATOR__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__VOCABULARY_ELEMENT = IndicatorsPackage.MATCHING_INDICATOR__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__MEASUREMENT = IndicatorsPackage.MATCHING_INDICATOR__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__CHANGE_REQUEST = IndicatorsPackage.MATCHING_INDICATOR__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__DASDL_PROPERTY = IndicatorsPackage.MATCHING_INDICATOR__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__COUNT = IndicatorsPackage.MATCHING_INDICATOR__COUNT;

    /**
     * The feature id for the '<em><b>Null Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__NULL_COUNT = IndicatorsPackage.MATCHING_INDICATOR__NULL_COUNT;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__PARAMETERS = IndicatorsPackage.MATCHING_INDICATOR__PARAMETERS;

    /**
     * The feature id for the '<em><b>Analyzed Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__ANALYZED_ELEMENT = IndicatorsPackage.MATCHING_INDICATOR__ANALYZED_ELEMENT;

    /**
     * The feature id for the '<em><b>Datamining Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__DATAMINING_TYPE = IndicatorsPackage.MATCHING_INDICATOR__DATAMINING_TYPE;

    /**
     * The feature id for the '<em><b>Indicator Definition</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__INDICATOR_DEFINITION = IndicatorsPackage.MATCHING_INDICATOR__INDICATOR_DEFINITION;

    /**
     * The feature id for the '<em><b>Instantiated Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__INSTANTIATED_EXPRESSIONS = IndicatorsPackage.MATCHING_INDICATOR__INSTANTIATED_EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Matching Value Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__MATCHING_VALUE_COUNT = IndicatorsPackage.MATCHING_INDICATOR__MATCHING_VALUE_COUNT;

    /**
     * The feature id for the '<em><b>Not Matching Value Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__NOT_MATCHING_VALUE_COUNT = IndicatorsPackage.MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT;

    /**
     * The feature id for the '<em><b>Column Set A</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__COLUMN_SET_A = IndicatorsPackage.MATCHING_INDICATOR_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Column Set B</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR__COLUMN_SET_B = IndicatorsPackage.MATCHING_INDICATOR_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Columns Compare Indicator</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int COLUMNS_COMPARE_INDICATOR_FEATURE_COUNT = IndicatorsPackage.MATCHING_INDICATOR_FEATURE_COUNT + 2;


    /**
     * The meta object id for the '{@link org.talend.dataquality.indicators.columnset.impl.ValueMatchingIndicatorImpl <em>Value Matching Indicator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.indicators.columnset.impl.ValueMatchingIndicatorImpl
     * @see org.talend.dataquality.indicators.columnset.impl.ColumnsetPackageImpl#getValueMatchingIndicator()
     * @generated
     */
    int VALUE_MATCHING_INDICATOR = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__NAME = COLUMNS_COMPARE_INDICATOR__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__VISIBILITY = COLUMNS_COMPARE_INDICATOR__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__CLIENT_DEPENDENCY = COLUMNS_COMPARE_INDICATOR__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__SUPPLIER_DEPENDENCY = COLUMNS_COMPARE_INDICATOR__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__CONSTRAINT = COLUMNS_COMPARE_INDICATOR__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__NAMESPACE = COLUMNS_COMPARE_INDICATOR__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__IMPORTER = COLUMNS_COMPARE_INDICATOR__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__STEREOTYPE = COLUMNS_COMPARE_INDICATOR__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__TAGGED_VALUE = COLUMNS_COMPARE_INDICATOR__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__DOCUMENT = COLUMNS_COMPARE_INDICATOR__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__DESCRIPTION = COLUMNS_COMPARE_INDICATOR__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__RESPONSIBLE_PARTY = COLUMNS_COMPARE_INDICATOR__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__ELEMENT_NODE = COLUMNS_COMPARE_INDICATOR__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__SET = COLUMNS_COMPARE_INDICATOR__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__RENDERED_OBJECT = COLUMNS_COMPARE_INDICATOR__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__VOCABULARY_ELEMENT = COLUMNS_COMPARE_INDICATOR__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__MEASUREMENT = COLUMNS_COMPARE_INDICATOR__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__CHANGE_REQUEST = COLUMNS_COMPARE_INDICATOR__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__DASDL_PROPERTY = COLUMNS_COMPARE_INDICATOR__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__COUNT = COLUMNS_COMPARE_INDICATOR__COUNT;

    /**
     * The feature id for the '<em><b>Null Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__NULL_COUNT = COLUMNS_COMPARE_INDICATOR__NULL_COUNT;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__PARAMETERS = COLUMNS_COMPARE_INDICATOR__PARAMETERS;

    /**
     * The feature id for the '<em><b>Analyzed Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__ANALYZED_ELEMENT = COLUMNS_COMPARE_INDICATOR__ANALYZED_ELEMENT;

    /**
     * The feature id for the '<em><b>Datamining Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__DATAMINING_TYPE = COLUMNS_COMPARE_INDICATOR__DATAMINING_TYPE;

    /**
     * The feature id for the '<em><b>Indicator Definition</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__INDICATOR_DEFINITION = COLUMNS_COMPARE_INDICATOR__INDICATOR_DEFINITION;

    /**
     * The feature id for the '<em><b>Instantiated Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__INSTANTIATED_EXPRESSIONS = COLUMNS_COMPARE_INDICATOR__INSTANTIATED_EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Matching Value Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__MATCHING_VALUE_COUNT = COLUMNS_COMPARE_INDICATOR__MATCHING_VALUE_COUNT;

    /**
     * The feature id for the '<em><b>Not Matching Value Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT = COLUMNS_COMPARE_INDICATOR__NOT_MATCHING_VALUE_COUNT;

    /**
     * The feature id for the '<em><b>Column Set A</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__COLUMN_SET_A = COLUMNS_COMPARE_INDICATOR__COLUMN_SET_A;

    /**
     * The feature id for the '<em><b>Column Set B</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR__COLUMN_SET_B = COLUMNS_COMPARE_INDICATOR__COLUMN_SET_B;

    /**
     * The number of structural features of the '<em>Value Matching Indicator</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int VALUE_MATCHING_INDICATOR_FEATURE_COUNT = COLUMNS_COMPARE_INDICATOR_FEATURE_COUNT + 0;

    /**
     * The meta object id for the '{@link org.talend.dataquality.indicators.columnset.impl.RowMatchingIndicatorImpl <em>Row Matching Indicator</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.indicators.columnset.impl.RowMatchingIndicatorImpl
     * @see org.talend.dataquality.indicators.columnset.impl.ColumnsetPackageImpl#getRowMatchingIndicator()
     * @generated
     */
    int ROW_MATCHING_INDICATOR = 2;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__NAME = COLUMNS_COMPARE_INDICATOR__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__VISIBILITY = COLUMNS_COMPARE_INDICATOR__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__CLIENT_DEPENDENCY = COLUMNS_COMPARE_INDICATOR__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__SUPPLIER_DEPENDENCY = COLUMNS_COMPARE_INDICATOR__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__CONSTRAINT = COLUMNS_COMPARE_INDICATOR__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__NAMESPACE = COLUMNS_COMPARE_INDICATOR__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__IMPORTER = COLUMNS_COMPARE_INDICATOR__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__STEREOTYPE = COLUMNS_COMPARE_INDICATOR__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__TAGGED_VALUE = COLUMNS_COMPARE_INDICATOR__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__DOCUMENT = COLUMNS_COMPARE_INDICATOR__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__DESCRIPTION = COLUMNS_COMPARE_INDICATOR__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__RESPONSIBLE_PARTY = COLUMNS_COMPARE_INDICATOR__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__ELEMENT_NODE = COLUMNS_COMPARE_INDICATOR__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__SET = COLUMNS_COMPARE_INDICATOR__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__RENDERED_OBJECT = COLUMNS_COMPARE_INDICATOR__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__VOCABULARY_ELEMENT = COLUMNS_COMPARE_INDICATOR__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__MEASUREMENT = COLUMNS_COMPARE_INDICATOR__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__CHANGE_REQUEST = COLUMNS_COMPARE_INDICATOR__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__DASDL_PROPERTY = COLUMNS_COMPARE_INDICATOR__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__COUNT = COLUMNS_COMPARE_INDICATOR__COUNT;

    /**
     * The feature id for the '<em><b>Null Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__NULL_COUNT = COLUMNS_COMPARE_INDICATOR__NULL_COUNT;

    /**
     * The feature id for the '<em><b>Parameters</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__PARAMETERS = COLUMNS_COMPARE_INDICATOR__PARAMETERS;

    /**
     * The feature id for the '<em><b>Analyzed Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__ANALYZED_ELEMENT = COLUMNS_COMPARE_INDICATOR__ANALYZED_ELEMENT;

    /**
     * The feature id for the '<em><b>Datamining Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__DATAMINING_TYPE = COLUMNS_COMPARE_INDICATOR__DATAMINING_TYPE;

    /**
     * The feature id for the '<em><b>Indicator Definition</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__INDICATOR_DEFINITION = COLUMNS_COMPARE_INDICATOR__INDICATOR_DEFINITION;

    /**
     * The feature id for the '<em><b>Instantiated Expressions</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__INSTANTIATED_EXPRESSIONS = COLUMNS_COMPARE_INDICATOR__INSTANTIATED_EXPRESSIONS;

    /**
     * The feature id for the '<em><b>Matching Value Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__MATCHING_VALUE_COUNT = COLUMNS_COMPARE_INDICATOR__MATCHING_VALUE_COUNT;

    /**
     * The feature id for the '<em><b>Not Matching Value Count</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT = COLUMNS_COMPARE_INDICATOR__NOT_MATCHING_VALUE_COUNT;

    /**
     * The feature id for the '<em><b>Column Set A</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__COLUMN_SET_A = COLUMNS_COMPARE_INDICATOR__COLUMN_SET_A;

    /**
     * The feature id for the '<em><b>Column Set B</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR__COLUMN_SET_B = COLUMNS_COMPARE_INDICATOR__COLUMN_SET_B;

    /**
     * The number of structural features of the '<em>Row Matching Indicator</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROW_MATCHING_INDICATOR_FEATURE_COUNT = COLUMNS_COMPARE_INDICATOR_FEATURE_COUNT + 0;


    /**
     * Returns the meta object for class '{@link org.talend.dataquality.indicators.columnset.ColumnsCompareIndicator <em>Columns Compare Indicator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Columns Compare Indicator</em>'.
     * @see org.talend.dataquality.indicators.columnset.ColumnsCompareIndicator
     * @generated
     */
    EClass getColumnsCompareIndicator();

    /**
     * Returns the meta object for the reference list '{@link org.talend.dataquality.indicators.columnset.ColumnsCompareIndicator#getColumnSetA <em>Column Set A</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Column Set A</em>'.
     * @see org.talend.dataquality.indicators.columnset.ColumnsCompareIndicator#getColumnSetA()
     * @see #getColumnsCompareIndicator()
     * @generated
     */
    EReference getColumnsCompareIndicator_ColumnSetA();

    /**
     * Returns the meta object for the reference list '{@link org.talend.dataquality.indicators.columnset.ColumnsCompareIndicator#getColumnSetB <em>Column Set B</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Column Set B</em>'.
     * @see org.talend.dataquality.indicators.columnset.ColumnsCompareIndicator#getColumnSetB()
     * @see #getColumnsCompareIndicator()
     * @generated
     */
    EReference getColumnsCompareIndicator_ColumnSetB();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.indicators.columnset.ValueMatchingIndicator <em>Value Matching Indicator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Value Matching Indicator</em>'.
     * @see org.talend.dataquality.indicators.columnset.ValueMatchingIndicator
     * @generated
     */
    EClass getValueMatchingIndicator();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.indicators.columnset.RowMatchingIndicator <em>Row Matching Indicator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Row Matching Indicator</em>'.
     * @see org.talend.dataquality.indicators.columnset.RowMatchingIndicator
     * @generated
     */
    EClass getRowMatchingIndicator();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ColumnsetFactory getColumnsetFactory();

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
         * The meta object literal for the '{@link org.talend.dataquality.indicators.columnset.impl.ColumnsCompareIndicatorImpl <em>Columns Compare Indicator</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.indicators.columnset.impl.ColumnsCompareIndicatorImpl
         * @see org.talend.dataquality.indicators.columnset.impl.ColumnsetPackageImpl#getColumnsCompareIndicator()
         * @generated
         */
        EClass COLUMNS_COMPARE_INDICATOR = eINSTANCE.getColumnsCompareIndicator();

        /**
         * The meta object literal for the '<em><b>Column Set A</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COLUMNS_COMPARE_INDICATOR__COLUMN_SET_A = eINSTANCE.getColumnsCompareIndicator_ColumnSetA();

        /**
         * The meta object literal for the '<em><b>Column Set B</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference COLUMNS_COMPARE_INDICATOR__COLUMN_SET_B = eINSTANCE.getColumnsCompareIndicator_ColumnSetB();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.indicators.columnset.impl.ValueMatchingIndicatorImpl <em>Value Matching Indicator</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.indicators.columnset.impl.ValueMatchingIndicatorImpl
         * @see org.talend.dataquality.indicators.columnset.impl.ColumnsetPackageImpl#getValueMatchingIndicator()
         * @generated
         */
        EClass VALUE_MATCHING_INDICATOR = eINSTANCE.getValueMatchingIndicator();

        /**
         * The meta object literal for the '{@link org.talend.dataquality.indicators.columnset.impl.RowMatchingIndicatorImpl <em>Row Matching Indicator</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.indicators.columnset.impl.RowMatchingIndicatorImpl
         * @see org.talend.dataquality.indicators.columnset.impl.ColumnsetPackageImpl#getRowMatchingIndicator()
         * @generated
         */
        EClass ROW_MATCHING_INDICATOR = eINSTANCE.getRowMatchingIndicator();

    }

} //ColumnsetPackage
