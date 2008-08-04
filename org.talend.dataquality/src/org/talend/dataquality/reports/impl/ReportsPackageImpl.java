/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.reports.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.talend.dataquality.analysis.AnalysisPackage;

import org.talend.dataquality.analysis.category.CategoryPackage;
import org.talend.dataquality.analysis.category.impl.CategoryPackageImpl;
import org.talend.dataquality.analysis.impl.AnalysisPackageImpl;

import org.talend.dataquality.domain.DomainPackage;

import org.talend.dataquality.domain.impl.DomainPackageImpl;

import org.talend.dataquality.domain.pattern.PatternPackage;

import org.talend.dataquality.domain.pattern.impl.PatternPackageImpl;

import org.talend.dataquality.domain.sql.SQLPackage;
import org.talend.dataquality.domain.sql.impl.SQLPackageImpl;
import org.talend.dataquality.expressions.impl.ExpressionsPackageImpl;

import org.talend.dataquality.indicators.IndicatorsPackage;

import org.talend.dataquality.indicators.definition.DefinitionPackage;
import org.talend.dataquality.indicators.definition.impl.DefinitionPackageImpl;
import org.talend.dataquality.indicators.impl.IndicatorsPackageImpl;

import org.talend.dataquality.indicators.schema.SchemaPackage;
import org.talend.dataquality.indicators.schema.impl.SchemaPackageImpl;
import org.talend.dataquality.indicators.sql.IndicatorSqlPackage;
import org.talend.dataquality.indicators.sql.impl.IndicatorSqlPackageImpl;
import org.talend.dataquality.reports.AnalysisMap;
import org.talend.dataquality.reports.PresentationParameter;
import org.talend.dataquality.reports.ReportsFactory;
import org.talend.dataquality.reports.ReportsPackage;
import org.talend.dataquality.reports.TdReport;

import orgomg.cwm.analysis.businessnomenclature.BusinessnomenclaturePackage;

import orgomg.cwm.analysis.datamining.DataminingPackage;

import orgomg.cwm.analysis.informationvisualization.InformationvisualizationPackage;

import orgomg.cwm.analysis.olap.OlapPackage;

import orgomg.cwm.analysis.transformation.TransformationPackage;

import orgomg.cwm.foundation.businessinformation.BusinessinformationPackage;

import orgomg.cwm.foundation.datatypes.DatatypesPackage;

import orgomg.cwm.foundation.expressions.ExpressionsPackage;

import orgomg.cwm.foundation.keysindexes.KeysindexesPackage;

import orgomg.cwm.foundation.softwaredeployment.SoftwaredeploymentPackage;

import orgomg.cwm.foundation.typemapping.TypemappingPackage;

import orgomg.cwm.management.warehouseoperation.WarehouseoperationPackage;

import orgomg.cwm.management.warehouseprocess.WarehouseprocessPackage;

import orgomg.cwm.objectmodel.behavioral.BehavioralPackage;

import orgomg.cwm.objectmodel.core.CorePackage;

import orgomg.cwm.objectmodel.instance.InstancePackage;

import orgomg.cwm.objectmodel.relationships.RelationshipsPackage;

import orgomg.cwm.resource.multidimensional.MultidimensionalPackage;

import orgomg.cwm.resource.record.RecordPackage;

import orgomg.cwm.resource.relational.RelationalPackage;

import orgomg.cwm.resource.xml.XmlPackage;

import orgomg.cwmmip.CwmmipPackage;

import orgomg.cwmx.analysis.informationreporting.InformationreportingPackage;

import orgomg.cwmx.analysis.informationset.InformationsetPackage;

import orgomg.cwmx.foundation.er.ErPackage;

import orgomg.cwmx.resource.coboldata.CoboldataPackage;

import orgomg.cwmx.resource.dmsii.DmsiiPackage;

import orgomg.cwmx.resource.essbase.EssbasePackage;

import orgomg.cwmx.resource.express.ExpressPackage;

import orgomg.cwmx.resource.imsdatabase.ImsdatabasePackage;

import orgomg.mof.model.ModelPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ReportsPackageImpl extends EPackageImpl implements ReportsPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass tdReportEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass presentationParameterEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass analysisMapEClass = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.talend.dataquality.reports.ReportsPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private ReportsPackageImpl() {
        super(eNS_URI, ReportsFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this
     * model, and for any others upon which it depends.  Simple
     * dependencies are satisfied by calling this method on all
     * dependent packages before doing anything else.  This method drives
     * initialization for interdependent packages directly, in parallel
     * with this package, itself.
     * <p>Of this package and its interdependencies, all packages which
     * have not yet been registered by their URI values are first created
     * and registered.  The packages are then initialized in two steps:
     * meta-model objects for all of the packages are created before any
     * are initialized, since one package's meta-model objects may refer to
     * those of another.
     * <p>Invocation of this method will not affect any packages that have
     * already been initialized.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static ReportsPackage init() {
        if (isInited) return (ReportsPackage)EPackage.Registry.INSTANCE.getEPackage(ReportsPackage.eNS_URI);

        // Obtain or create and register package
        ReportsPackageImpl theReportsPackage = (ReportsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ReportsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ReportsPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        CorePackage.eINSTANCE.eClass();
        BehavioralPackage.eINSTANCE.eClass();
        RelationshipsPackage.eINSTANCE.eClass();
        InstancePackage.eINSTANCE.eClass();
        BusinessinformationPackage.eINSTANCE.eClass();
        DatatypesPackage.eINSTANCE.eClass();
        ExpressionsPackage.eINSTANCE.eClass();
        KeysindexesPackage.eINSTANCE.eClass();
        SoftwaredeploymentPackage.eINSTANCE.eClass();
        TypemappingPackage.eINSTANCE.eClass();
        RelationalPackage.eINSTANCE.eClass();
        RecordPackage.eINSTANCE.eClass();
        MultidimensionalPackage.eINSTANCE.eClass();
        XmlPackage.eINSTANCE.eClass();
        TransformationPackage.eINSTANCE.eClass();
        OlapPackage.eINSTANCE.eClass();
        DataminingPackage.eINSTANCE.eClass();
        InformationvisualizationPackage.eINSTANCE.eClass();
        BusinessnomenclaturePackage.eINSTANCE.eClass();
        WarehouseprocessPackage.eINSTANCE.eClass();
        WarehouseoperationPackage.eINSTANCE.eClass();
        ErPackage.eINSTANCE.eClass();
        CoboldataPackage.eINSTANCE.eClass();
        DmsiiPackage.eINSTANCE.eClass();
        ImsdatabasePackage.eINSTANCE.eClass();
        EssbasePackage.eINSTANCE.eClass();
        ExpressPackage.eINSTANCE.eClass();
        InformationsetPackage.eINSTANCE.eClass();
        InformationreportingPackage.eINSTANCE.eClass();
        CwmmipPackage.eINSTANCE.eClass();
        ModelPackage.eINSTANCE.eClass();

        // Obtain or create and register interdependencies
        AnalysisPackageImpl theAnalysisPackage = (AnalysisPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(AnalysisPackage.eNS_URI) instanceof AnalysisPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(AnalysisPackage.eNS_URI) : AnalysisPackage.eINSTANCE);
        CategoryPackageImpl theCategoryPackage = (CategoryPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(CategoryPackage.eNS_URI) instanceof CategoryPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(CategoryPackage.eNS_URI) : CategoryPackage.eINSTANCE);
        IndicatorsPackageImpl theIndicatorsPackage = (IndicatorsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(IndicatorsPackage.eNS_URI) instanceof IndicatorsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(IndicatorsPackage.eNS_URI) : IndicatorsPackage.eINSTANCE);
        SchemaPackageImpl theSchemaPackage = (SchemaPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(SchemaPackage.eNS_URI) instanceof SchemaPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(SchemaPackage.eNS_URI) : SchemaPackage.eINSTANCE);
        DefinitionPackageImpl theDefinitionPackage = (DefinitionPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DefinitionPackage.eNS_URI) instanceof DefinitionPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DefinitionPackage.eNS_URI) : DefinitionPackage.eINSTANCE);
        IndicatorSqlPackageImpl theIndicatorSqlPackage = (IndicatorSqlPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(IndicatorSqlPackage.eNS_URI) instanceof IndicatorSqlPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(IndicatorSqlPackage.eNS_URI) : IndicatorSqlPackage.eINSTANCE);
        ExpressionsPackageImpl theExpressionsPackage_1 = (ExpressionsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(org.talend.dataquality.expressions.ExpressionsPackage.eNS_URI) instanceof ExpressionsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(org.talend.dataquality.expressions.ExpressionsPackage.eNS_URI) : org.talend.dataquality.expressions.ExpressionsPackage.eINSTANCE);
        DomainPackageImpl theDomainPackage = (DomainPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DomainPackage.eNS_URI) instanceof DomainPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DomainPackage.eNS_URI) : DomainPackage.eINSTANCE);
        PatternPackageImpl thePatternPackage = (PatternPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(PatternPackage.eNS_URI) instanceof PatternPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(PatternPackage.eNS_URI) : PatternPackage.eINSTANCE);
        SQLPackageImpl theSQLPackage = (SQLPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(SQLPackage.eNS_URI) instanceof SQLPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(SQLPackage.eNS_URI) : SQLPackage.eINSTANCE);

        // Create package meta-data objects
        theReportsPackage.createPackageContents();
        theAnalysisPackage.createPackageContents();
        theCategoryPackage.createPackageContents();
        theIndicatorsPackage.createPackageContents();
        theSchemaPackage.createPackageContents();
        theDefinitionPackage.createPackageContents();
        theIndicatorSqlPackage.createPackageContents();
        theExpressionsPackage_1.createPackageContents();
        theDomainPackage.createPackageContents();
        thePatternPackage.createPackageContents();
        theSQLPackage.createPackageContents();

        // Initialize created meta-data
        theReportsPackage.initializePackageContents();
        theAnalysisPackage.initializePackageContents();
        theCategoryPackage.initializePackageContents();
        theIndicatorsPackage.initializePackageContents();
        theSchemaPackage.initializePackageContents();
        theDefinitionPackage.initializePackageContents();
        theIndicatorSqlPackage.initializePackageContents();
        theExpressionsPackage_1.initializePackageContents();
        theDomainPackage.initializePackageContents();
        thePatternPackage.initializePackageContents();
        theSQLPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theReportsPackage.freeze();

        return theReportsPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getTdReport() {
        return tdReportEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTdReport_PresentationParams() {
        return (EReference)tdReportEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTdReport_CreationDate() {
        return (EAttribute)tdReportEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTdReport_LastExecutionDate() {
        return (EAttribute)tdReportEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTdReport_AnalysisMap() {
        return (EReference)tdReportEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTdReport_InputJrxml() {
        return (EAttribute)tdReportEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTdReport_OutputReportFolder() {
        return (EAttribute)tdReportEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTdReport_ReportType() {
        return (EAttribute)tdReportEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getPresentationParameter() {
        return presentationParameterEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getPresentationParameter_PlotType() {
        return (EAttribute)presentationParameterEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getPresentationParameter_Indicator() {
        return (EReference)presentationParameterEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getAnalysisMap() {
        return analysisMapEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getAnalysisMap_Analysis() {
        return (EReference)analysisMapEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getAnalysisMap_MustRefresh() {
        return (EAttribute)analysisMapEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ReportsFactory getReportsFactory() {
        return (ReportsFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        tdReportEClass = createEClass(TD_REPORT);
        createEReference(tdReportEClass, TD_REPORT__PRESENTATION_PARAMS);
        createEAttribute(tdReportEClass, TD_REPORT__CREATION_DATE);
        createEAttribute(tdReportEClass, TD_REPORT__LAST_EXECUTION_DATE);
        createEReference(tdReportEClass, TD_REPORT__ANALYSIS_MAP);
        createEAttribute(tdReportEClass, TD_REPORT__INPUT_JRXML);
        createEAttribute(tdReportEClass, TD_REPORT__OUTPUT_REPORT_FOLDER);
        createEAttribute(tdReportEClass, TD_REPORT__REPORT_TYPE);

        presentationParameterEClass = createEClass(PRESENTATION_PARAMETER);
        createEAttribute(presentationParameterEClass, PRESENTATION_PARAMETER__PLOT_TYPE);
        createEReference(presentationParameterEClass, PRESENTATION_PARAMETER__INDICATOR);

        analysisMapEClass = createEClass(ANALYSIS_MAP);
        createEReference(analysisMapEClass, ANALYSIS_MAP__ANALYSIS);
        createEAttribute(analysisMapEClass, ANALYSIS_MAP__MUST_REFRESH);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        InformationreportingPackage theInformationreportingPackage = (InformationreportingPackage)EPackage.Registry.INSTANCE.getEPackage(InformationreportingPackage.eNS_URI);
        AnalysisPackage theAnalysisPackage = (AnalysisPackage)EPackage.Registry.INSTANCE.getEPackage(AnalysisPackage.eNS_URI);
        CorePackage theCorePackage = (CorePackage)EPackage.Registry.INSTANCE.getEPackage(CorePackage.eNS_URI);
        IndicatorsPackage theIndicatorsPackage = (IndicatorsPackage)EPackage.Registry.INSTANCE.getEPackage(IndicatorsPackage.eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        tdReportEClass.getESuperTypes().add(theInformationreportingPackage.getReport());

        // Initialize classes and features; add operations and parameters
        initEClass(tdReportEClass, TdReport.class, "TdReport", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTdReport_PresentationParams(), this.getPresentationParameter(), null, "presentationParams", null, 0, -1, TdReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTdReport_CreationDate(), ecorePackage.getEDate(), "creationDate", null, 0, 1, TdReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTdReport_LastExecutionDate(), ecorePackage.getEDate(), "lastExecutionDate", null, 0, 1, TdReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTdReport_AnalysisMap(), this.getAnalysisMap(), null, "analysisMap", null, 0, -1, TdReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTdReport_InputJrxml(), ecorePackage.getEString(), "inputJrxml", null, 0, 1, TdReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTdReport_OutputReportFolder(), ecorePackage.getEString(), "outputReportFolder", null, 0, 1, TdReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTdReport_ReportType(), ecorePackage.getEString(), "reportType", null, 0, 1, TdReport.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        EOperation op = addEOperation(tdReportEClass, ecorePackage.getEBoolean(), "addAnalysis", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, theAnalysisPackage.getAnalysis(), "analysis", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(tdReportEClass, ecorePackage.getEBoolean(), "removeAnalysis", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, theAnalysisPackage.getAnalysis(), "analysis", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(tdReportEClass, ecorePackage.getEBoolean(), "setMustRefresh", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, theAnalysisPackage.getAnalysis(), "analysis", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, theCorePackage.getBoolean(), "mustRefresh", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(tdReportEClass, ecorePackage.getEBoolean(), "addAnalysis", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, theAnalysisPackage.getAnalysis(), "analysis", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, theCorePackage.getBoolean(), "mustRefresh", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(tdReportEClass, ecorePackage.getEBoolean(), "mustRefresh", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, theAnalysisPackage.getAnalysis(), "analysis", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(presentationParameterEClass, PresentationParameter.class, "PresentationParameter", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getPresentationParameter_PlotType(), ecorePackage.getEString(), "plotType", null, 0, 1, PresentationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getPresentationParameter_Indicator(), theIndicatorsPackage.getIndicator(), null, "indicator", null, 0, 1, PresentationParameter.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(analysisMapEClass, AnalysisMap.class, "AnalysisMap", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getAnalysisMap_Analysis(), theAnalysisPackage.getAnalysis(), null, "analysis", null, 0, 1, AnalysisMap.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getAnalysisMap_MustRefresh(), ecorePackage.getEBoolean(), "mustRefresh", null, 0, 1, AnalysisMap.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Create resource
        createResource(eNS_URI);
    }

} //ReportsPackageImpl
