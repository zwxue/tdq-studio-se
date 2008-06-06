/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.reports;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.EReference;
import orgomg.cwmx.analysis.informationreporting.InformationreportingPackage;

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
 * @see org.talend.dataquality.reports.ReportsFactory
 * @model kind="package"
 * @generated
 */
public interface ReportsPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "reports";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://dataquality.reports";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "dataquality.reports";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ReportsPackage eINSTANCE = org.talend.dataquality.reports.impl.ReportsPackageImpl.init();

    /**
     * The meta object id for the '{@link org.talend.dataquality.reports.impl.TdReportImpl <em>Td Report</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.reports.impl.TdReportImpl
     * @see org.talend.dataquality.reports.impl.ReportsPackageImpl#getTdReport()
     * @generated
     */
    int TD_REPORT = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__NAME = InformationreportingPackage.REPORT__NAME;

    /**
     * The feature id for the '<em><b>Visibility</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__VISIBILITY = InformationreportingPackage.REPORT__VISIBILITY;

    /**
     * The feature id for the '<em><b>Client Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__CLIENT_DEPENDENCY = InformationreportingPackage.REPORT__CLIENT_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Supplier Dependency</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__SUPPLIER_DEPENDENCY = InformationreportingPackage.REPORT__SUPPLIER_DEPENDENCY;

    /**
     * The feature id for the '<em><b>Constraint</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__CONSTRAINT = InformationreportingPackage.REPORT__CONSTRAINT;

    /**
     * The feature id for the '<em><b>Namespace</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__NAMESPACE = InformationreportingPackage.REPORT__NAMESPACE;

    /**
     * The feature id for the '<em><b>Importer</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__IMPORTER = InformationreportingPackage.REPORT__IMPORTER;

    /**
     * The feature id for the '<em><b>Stereotype</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__STEREOTYPE = InformationreportingPackage.REPORT__STEREOTYPE;

    /**
     * The feature id for the '<em><b>Tagged Value</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__TAGGED_VALUE = InformationreportingPackage.REPORT__TAGGED_VALUE;

    /**
     * The feature id for the '<em><b>Document</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__DOCUMENT = InformationreportingPackage.REPORT__DOCUMENT;

    /**
     * The feature id for the '<em><b>Description</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__DESCRIPTION = InformationreportingPackage.REPORT__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Responsible Party</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__RESPONSIBLE_PARTY = InformationreportingPackage.REPORT__RESPONSIBLE_PARTY;

    /**
     * The feature id for the '<em><b>Element Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__ELEMENT_NODE = InformationreportingPackage.REPORT__ELEMENT_NODE;

    /**
     * The feature id for the '<em><b>Set</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__SET = InformationreportingPackage.REPORT__SET;

    /**
     * The feature id for the '<em><b>Rendered Object</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__RENDERED_OBJECT = InformationreportingPackage.REPORT__RENDERED_OBJECT;

    /**
     * The feature id for the '<em><b>Vocabulary Element</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__VOCABULARY_ELEMENT = InformationreportingPackage.REPORT__VOCABULARY_ELEMENT;

    /**
     * The feature id for the '<em><b>Measurement</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__MEASUREMENT = InformationreportingPackage.REPORT__MEASUREMENT;

    /**
     * The feature id for the '<em><b>Change Request</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__CHANGE_REQUEST = InformationreportingPackage.REPORT__CHANGE_REQUEST;

    /**
     * The feature id for the '<em><b>Dasdl Property</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__DASDL_PROPERTY = InformationreportingPackage.REPORT__DASDL_PROPERTY;

    /**
     * The feature id for the '<em><b>Owned Element</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__OWNED_ELEMENT = InformationreportingPackage.REPORT__OWNED_ELEMENT;

    /**
     * The feature id for the '<em><b>Is Abstract</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__IS_ABSTRACT = InformationreportingPackage.REPORT__IS_ABSTRACT;

    /**
     * The feature id for the '<em><b>Feature</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__FEATURE = InformationreportingPackage.REPORT__FEATURE;

    /**
     * The feature id for the '<em><b>Structural Feature</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__STRUCTURAL_FEATURE = InformationreportingPackage.REPORT__STRUCTURAL_FEATURE;

    /**
     * The feature id for the '<em><b>Parameter</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__PARAMETER = InformationreportingPackage.REPORT__PARAMETER;

    /**
     * The feature id for the '<em><b>Generalization</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__GENERALIZATION = InformationreportingPackage.REPORT__GENERALIZATION;

    /**
     * The feature id for the '<em><b>Specialization</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__SPECIALIZATION = InformationreportingPackage.REPORT__SPECIALIZATION;

    /**
     * The feature id for the '<em><b>Instance</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__INSTANCE = InformationreportingPackage.REPORT__INSTANCE;

    /**
     * The feature id for the '<em><b>Alias</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__ALIAS = InformationreportingPackage.REPORT__ALIAS;

    /**
     * The feature id for the '<em><b>Expression Node</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__EXPRESSION_NODE = InformationreportingPackage.REPORT__EXPRESSION_NODE;

    /**
     * The feature id for the '<em><b>Mapping From</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__MAPPING_FROM = InformationreportingPackage.REPORT__MAPPING_FROM;

    /**
     * The feature id for the '<em><b>Mapping To</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__MAPPING_TO = InformationreportingPackage.REPORT__MAPPING_TO;

    /**
     * The feature id for the '<em><b>Classifier Map</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__CLASSIFIER_MAP = InformationreportingPackage.REPORT__CLASSIFIER_MAP;

    /**
     * The feature id for the '<em><b>Cf Map</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__CF_MAP = InformationreportingPackage.REPORT__CF_MAP;

    /**
     * The feature id for the '<em><b>Domain</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__DOMAIN = InformationreportingPackage.REPORT__DOMAIN;

    /**
     * The feature id for the '<em><b>Simple Dimension</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__SIMPLE_DIMENSION = InformationreportingPackage.REPORT__SIMPLE_DIMENSION;

    /**
     * The feature id for the '<em><b>Formula</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__FORMULA = InformationreportingPackage.REPORT__FORMULA;

    /**
     * The feature id for the '<em><b>Action</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__ACTION = InformationreportingPackage.REPORT__ACTION;

    /**
     * The feature id for the '<em><b>File Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__FILE_NAME = InformationreportingPackage.REPORT__FILE_NAME;

    /**
     * The feature id for the '<em><b>Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__TYPE = InformationreportingPackage.REPORT__TYPE;

    /**
     * The feature id for the '<em><b>Url</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__URL = InformationreportingPackage.REPORT__URL;

    /**
     * The feature id for the '<em><b>Composite</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__COMPOSITE = InformationreportingPackage.REPORT__COMPOSITE;

    /**
     * The feature id for the '<em><b>Component</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__COMPONENT = InformationreportingPackage.REPORT__COMPONENT;

    /**
     * The feature id for the '<em><b>Default Rendering</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__DEFAULT_RENDERING = InformationreportingPackage.REPORT__DEFAULT_RENDERING;

    /**
     * The feature id for the '<em><b>Model Element</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__MODEL_ELEMENT = InformationreportingPackage.REPORT__MODEL_ELEMENT;

    /**
     * The feature id for the '<em><b>Neighbor</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__NEIGHBOR = InformationreportingPackage.REPORT__NEIGHBOR;

    /**
     * The feature id for the '<em><b>Referencing Neighbor</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__REFERENCING_NEIGHBOR = InformationreportingPackage.REPORT__REFERENCING_NEIGHBOR;

    /**
     * The feature id for the '<em><b>Presentation Params</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__PRESENTATION_PARAMS = InformationreportingPackage.REPORT_FEATURE_COUNT + 0;

    /**
     * The feature id for the '<em><b>Creation Date</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT__CREATION_DATE = InformationreportingPackage.REPORT_FEATURE_COUNT + 1;

    /**
     * The number of structural features of the '<em>Td Report</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int TD_REPORT_FEATURE_COUNT = InformationreportingPackage.REPORT_FEATURE_COUNT + 2;


    /**
     * The meta object id for the '{@link org.talend.dataquality.reports.impl.PresentationParameterImpl <em>Presentation Parameter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.talend.dataquality.reports.impl.PresentationParameterImpl
     * @see org.talend.dataquality.reports.impl.ReportsPackageImpl#getPresentationParameter()
     * @generated
     */
    int PRESENTATION_PARAMETER = 1;

    /**
     * The feature id for the '<em><b>Plot Type</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PRESENTATION_PARAMETER__PLOT_TYPE = 0;

    /**
     * The feature id for the '<em><b>Indicator</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PRESENTATION_PARAMETER__INDICATOR = 1;

    /**
     * The number of structural features of the '<em>Presentation Parameter</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int PRESENTATION_PARAMETER_FEATURE_COUNT = 2;


    /**
     * Returns the meta object for class '{@link org.talend.dataquality.reports.TdReport <em>Td Report</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Td Report</em>'.
     * @see org.talend.dataquality.reports.TdReport
     * @generated
     */
    EClass getTdReport();

    /**
     * Returns the meta object for the containment reference list '{@link org.talend.dataquality.reports.TdReport#getPresentationParams <em>Presentation Params</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Presentation Params</em>'.
     * @see org.talend.dataquality.reports.TdReport#getPresentationParams()
     * @see #getTdReport()
     * @generated
     */
    EReference getTdReport_PresentationParams();

    /**
     * Returns the meta object for the attribute '{@link org.talend.dataquality.reports.TdReport#getCreationDate <em>Creation Date</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Creation Date</em>'.
     * @see org.talend.dataquality.reports.TdReport#getCreationDate()
     * @see #getTdReport()
     * @generated
     */
    EAttribute getTdReport_CreationDate();

    /**
     * Returns the meta object for class '{@link org.talend.dataquality.reports.PresentationParameter <em>Presentation Parameter</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Presentation Parameter</em>'.
     * @see org.talend.dataquality.reports.PresentationParameter
     * @generated
     */
    EClass getPresentationParameter();

    /**
     * Returns the meta object for the attribute '{@link org.talend.dataquality.reports.PresentationParameter#getPlotType <em>Plot Type</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Plot Type</em>'.
     * @see org.talend.dataquality.reports.PresentationParameter#getPlotType()
     * @see #getPresentationParameter()
     * @generated
     */
    EAttribute getPresentationParameter_PlotType();

    /**
     * Returns the meta object for the reference '{@link org.talend.dataquality.reports.PresentationParameter#getIndicator <em>Indicator</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Indicator</em>'.
     * @see org.talend.dataquality.reports.PresentationParameter#getIndicator()
     * @see #getPresentationParameter()
     * @generated
     */
    EReference getPresentationParameter_Indicator();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ReportsFactory getReportsFactory();

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
         * The meta object literal for the '{@link org.talend.dataquality.reports.impl.TdReportImpl <em>Td Report</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.reports.impl.TdReportImpl
         * @see org.talend.dataquality.reports.impl.ReportsPackageImpl#getTdReport()
         * @generated
         */
        EClass TD_REPORT = eINSTANCE.getTdReport();
        /**
         * The meta object literal for the '<em><b>Presentation Params</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference TD_REPORT__PRESENTATION_PARAMS = eINSTANCE.getTdReport_PresentationParams();
        /**
         * The meta object literal for the '<em><b>Creation Date</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute TD_REPORT__CREATION_DATE = eINSTANCE.getTdReport_CreationDate();
        /**
         * The meta object literal for the '{@link org.talend.dataquality.reports.impl.PresentationParameterImpl <em>Presentation Parameter</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.talend.dataquality.reports.impl.PresentationParameterImpl
         * @see org.talend.dataquality.reports.impl.ReportsPackageImpl#getPresentationParameter()
         * @generated
         */
        EClass PRESENTATION_PARAMETER = eINSTANCE.getPresentationParameter();
        /**
         * The meta object literal for the '<em><b>Plot Type</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute PRESENTATION_PARAMETER__PLOT_TYPE = eINSTANCE.getPresentationParameter_PlotType();
        /**
         * The meta object literal for the '<em><b>Indicator</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference PRESENTATION_PARAMETER__INDICATOR = eINSTANCE.getPresentationParameter_Indicator();

    }

} //ReportsPackage
