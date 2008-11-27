/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.columnset.impl;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
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
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsCompareIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetFactory;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.columnset.CountAvgNullIndicator;
import org.talend.dataquality.indicators.columnset.MinMaxDateIndicator;
import org.talend.dataquality.indicators.columnset.RowMatchingIndicator;
import org.talend.dataquality.indicators.columnset.ValueMatchingIndicator;
import org.talend.dataquality.indicators.definition.DefinitionPackage;
import org.talend.dataquality.indicators.definition.impl.DefinitionPackageImpl;
import org.talend.dataquality.indicators.impl.IndicatorsPackageImpl;
import org.talend.dataquality.indicators.schema.SchemaPackage;
import org.talend.dataquality.indicators.schema.impl.SchemaPackageImpl;
import org.talend.dataquality.indicators.sql.IndicatorSqlPackage;
import org.talend.dataquality.indicators.sql.impl.IndicatorSqlPackageImpl;
import org.talend.dataquality.reports.ReportsPackage;
import org.talend.dataquality.reports.impl.ReportsPackageImpl;
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
public class ColumnsetPackageImpl extends EPackageImpl implements ColumnsetPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass columnsCompareIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass valueMatchingIndicatorEClass = null;
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass rowMatchingIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass columnSetMultiValueIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass countAvgNullIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass minMaxDateIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType listObjectEDataType = null;

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
     * @see org.talend.dataquality.indicators.columnset.ColumnsetPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private ColumnsetPackageImpl() {
        super(eNS_URI, ColumnsetFactory.eINSTANCE);
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
    public static ColumnsetPackage init() {
        if (isInited) return (ColumnsetPackage)EPackage.Registry.INSTANCE.getEPackage(ColumnsetPackage.eNS_URI);

        // Obtain or create and register package
        ColumnsetPackageImpl theColumnsetPackage = (ColumnsetPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ColumnsetPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ColumnsetPackageImpl());

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
        ReportsPackageImpl theReportsPackage = (ReportsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ReportsPackage.eNS_URI) instanceof ReportsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ReportsPackage.eNS_URI) : ReportsPackage.eINSTANCE);
        IndicatorsPackageImpl theIndicatorsPackage = (IndicatorsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(IndicatorsPackage.eNS_URI) instanceof IndicatorsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(IndicatorsPackage.eNS_URI) : IndicatorsPackage.eINSTANCE);
        SchemaPackageImpl theSchemaPackage = (SchemaPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(SchemaPackage.eNS_URI) instanceof SchemaPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(SchemaPackage.eNS_URI) : SchemaPackage.eINSTANCE);
        DefinitionPackageImpl theDefinitionPackage = (DefinitionPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DefinitionPackage.eNS_URI) instanceof DefinitionPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DefinitionPackage.eNS_URI) : DefinitionPackage.eINSTANCE);
        IndicatorSqlPackageImpl theIndicatorSqlPackage = (IndicatorSqlPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(IndicatorSqlPackage.eNS_URI) instanceof IndicatorSqlPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(IndicatorSqlPackage.eNS_URI) : IndicatorSqlPackage.eINSTANCE);
        ExpressionsPackageImpl theExpressionsPackage_1 = (ExpressionsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(org.talend.dataquality.expressions.ExpressionsPackage.eNS_URI) instanceof ExpressionsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(org.talend.dataquality.expressions.ExpressionsPackage.eNS_URI) : org.talend.dataquality.expressions.ExpressionsPackage.eINSTANCE);
        DomainPackageImpl theDomainPackage = (DomainPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DomainPackage.eNS_URI) instanceof DomainPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DomainPackage.eNS_URI) : DomainPackage.eINSTANCE);
        PatternPackageImpl thePatternPackage = (PatternPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(PatternPackage.eNS_URI) instanceof PatternPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(PatternPackage.eNS_URI) : PatternPackage.eINSTANCE);
        SQLPackageImpl theSQLPackage = (SQLPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(SQLPackage.eNS_URI) instanceof SQLPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(SQLPackage.eNS_URI) : SQLPackage.eINSTANCE);

        // Create package meta-data objects
        theColumnsetPackage.createPackageContents();
        theAnalysisPackage.createPackageContents();
        theCategoryPackage.createPackageContents();
        theReportsPackage.createPackageContents();
        theIndicatorsPackage.createPackageContents();
        theSchemaPackage.createPackageContents();
        theDefinitionPackage.createPackageContents();
        theIndicatorSqlPackage.createPackageContents();
        theExpressionsPackage_1.createPackageContents();
        theDomainPackage.createPackageContents();
        thePatternPackage.createPackageContents();
        theSQLPackage.createPackageContents();

        // Initialize created meta-data
        theColumnsetPackage.initializePackageContents();
        theAnalysisPackage.initializePackageContents();
        theCategoryPackage.initializePackageContents();
        theReportsPackage.initializePackageContents();
        theIndicatorsPackage.initializePackageContents();
        theSchemaPackage.initializePackageContents();
        theDefinitionPackage.initializePackageContents();
        theIndicatorSqlPackage.initializePackageContents();
        theExpressionsPackage_1.initializePackageContents();
        theDomainPackage.initializePackageContents();
        thePatternPackage.initializePackageContents();
        theSQLPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theColumnsetPackage.freeze();

        return theColumnsetPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getColumnsCompareIndicator() {
        return columnsCompareIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumnsCompareIndicator_ColumnSetA() {
        return (EReference)columnsCompareIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumnsCompareIndicator_ColumnSetB() {
        return (EReference)columnsCompareIndicatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getValueMatchingIndicator() {
        return valueMatchingIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getRowMatchingIndicator() {
        return rowMatchingIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getColumnSetMultiValueIndicator() {
        return columnSetMultiValueIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumnSetMultiValueIndicator_AnalyzedColumns() {
        return (EReference)columnSetMultiValueIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumnSetMultiValueIndicator_ListRows() {
        return (EAttribute)columnSetMultiValueIndicatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumnSetMultiValueIndicator_NumericFunctions() {
        return (EAttribute)columnSetMultiValueIndicatorEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumnSetMultiValueIndicator_NominalColumns() {
        return (EReference)columnSetMultiValueIndicatorEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumnSetMultiValueIndicator_NumericColumns() {
        return (EReference)columnSetMultiValueIndicatorEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumnSetMultiValueIndicator_ColumnHeaders() {
        return (EAttribute)columnSetMultiValueIndicatorEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getColumnSetMultiValueIndicator_DateFunctions() {
        return (EAttribute)columnSetMultiValueIndicatorEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getColumnSetMultiValueIndicator_DateColumns() {
        return (EReference)columnSetMultiValueIndicatorEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getCountAvgNullIndicator() {
        return countAvgNullIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getMinMaxDateIndicator() {
        return minMaxDateIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getListObject() {
        return listObjectEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ColumnsetFactory getColumnsetFactory() {
        return (ColumnsetFactory)getEFactoryInstance();
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
        columnsCompareIndicatorEClass = createEClass(COLUMNS_COMPARE_INDICATOR);
        createEReference(columnsCompareIndicatorEClass, COLUMNS_COMPARE_INDICATOR__COLUMN_SET_A);
        createEReference(columnsCompareIndicatorEClass, COLUMNS_COMPARE_INDICATOR__COLUMN_SET_B);

        valueMatchingIndicatorEClass = createEClass(VALUE_MATCHING_INDICATOR);

        rowMatchingIndicatorEClass = createEClass(ROW_MATCHING_INDICATOR);

        columnSetMultiValueIndicatorEClass = createEClass(COLUMN_SET_MULTI_VALUE_INDICATOR);
        createEReference(columnSetMultiValueIndicatorEClass, COLUMN_SET_MULTI_VALUE_INDICATOR__ANALYZED_COLUMNS);
        createEAttribute(columnSetMultiValueIndicatorEClass, COLUMN_SET_MULTI_VALUE_INDICATOR__LIST_ROWS);
        createEAttribute(columnSetMultiValueIndicatorEClass, COLUMN_SET_MULTI_VALUE_INDICATOR__NUMERIC_FUNCTIONS);
        createEReference(columnSetMultiValueIndicatorEClass, COLUMN_SET_MULTI_VALUE_INDICATOR__NOMINAL_COLUMNS);
        createEReference(columnSetMultiValueIndicatorEClass, COLUMN_SET_MULTI_VALUE_INDICATOR__NUMERIC_COLUMNS);
        createEAttribute(columnSetMultiValueIndicatorEClass, COLUMN_SET_MULTI_VALUE_INDICATOR__COLUMN_HEADERS);
        createEAttribute(columnSetMultiValueIndicatorEClass, COLUMN_SET_MULTI_VALUE_INDICATOR__DATE_FUNCTIONS);
        createEReference(columnSetMultiValueIndicatorEClass, COLUMN_SET_MULTI_VALUE_INDICATOR__DATE_COLUMNS);

        countAvgNullIndicatorEClass = createEClass(COUNT_AVG_NULL_INDICATOR);

        minMaxDateIndicatorEClass = createEClass(MIN_MAX_DATE_INDICATOR);

        // Create data types
        listObjectEDataType = createEDataType(LIST_OBJECT);
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
        IndicatorsPackage theIndicatorsPackage = (IndicatorsPackage)EPackage.Registry.INSTANCE.getEPackage(IndicatorsPackage.eNS_URI);
        RelationalPackage theRelationalPackage = (RelationalPackage)EPackage.Registry.INSTANCE.getEPackage(RelationalPackage.eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        columnsCompareIndicatorEClass.getESuperTypes().add(theIndicatorsPackage.getMatchingIndicator());
        valueMatchingIndicatorEClass.getESuperTypes().add(this.getColumnsCompareIndicator());
        rowMatchingIndicatorEClass.getESuperTypes().add(this.getColumnsCompareIndicator());
        columnSetMultiValueIndicatorEClass.getESuperTypes().add(theIndicatorsPackage.getIndicator());
        countAvgNullIndicatorEClass.getESuperTypes().add(this.getColumnSetMultiValueIndicator());
        minMaxDateIndicatorEClass.getESuperTypes().add(this.getColumnSetMultiValueIndicator());

        // Initialize classes and features; add operations and parameters
        initEClass(columnsCompareIndicatorEClass, ColumnsCompareIndicator.class, "ColumnsCompareIndicator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getColumnsCompareIndicator_ColumnSetA(), theRelationalPackage.getColumn(), null, "columnSetA", null, 0, -1, ColumnsCompareIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getColumnsCompareIndicator_ColumnSetB(), theRelationalPackage.getColumn(), null, "columnSetB", null, 0, -1, ColumnsCompareIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(valueMatchingIndicatorEClass, ValueMatchingIndicator.class, "ValueMatchingIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(rowMatchingIndicatorEClass, RowMatchingIndicator.class, "RowMatchingIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(columnSetMultiValueIndicatorEClass, ColumnSetMultiValueIndicator.class, "ColumnSetMultiValueIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getColumnSetMultiValueIndicator_AnalyzedColumns(), theRelationalPackage.getColumn(), null, "analyzedColumns", null, 0, -1, ColumnSetMultiValueIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getColumnSetMultiValueIndicator_ListRows(), theIndicatorsPackage.getObjectArray(), "listRows", null, 0, 1, ColumnSetMultiValueIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getColumnSetMultiValueIndicator_NumericFunctions(), ecorePackage.getEString(), "numericFunctions", null, 0, -1, ColumnSetMultiValueIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getColumnSetMultiValueIndicator_NominalColumns(), theRelationalPackage.getColumn(), null, "nominalColumns", null, 0, -1, ColumnSetMultiValueIndicator.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getColumnSetMultiValueIndicator_NumericColumns(), theRelationalPackage.getColumn(), null, "numericColumns", null, 0, -1, ColumnSetMultiValueIndicator.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getColumnSetMultiValueIndicator_ColumnHeaders(), ecorePackage.getEString(), "columnHeaders", null, 0, -1, ColumnSetMultiValueIndicator.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getColumnSetMultiValueIndicator_DateFunctions(), ecorePackage.getEString(), "dateFunctions", null, 0, -1, ColumnSetMultiValueIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getColumnSetMultiValueIndicator_DateColumns(), theRelationalPackage.getColumn(), null, "dateColumns", null, 0, -1, ColumnSetMultiValueIndicator.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(countAvgNullIndicatorEClass, CountAvgNullIndicator.class, "CountAvgNullIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(minMaxDateIndicatorEClass, MinMaxDateIndicator.class, "MinMaxDateIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        // Initialize data types
        initEDataType(listObjectEDataType, List.class, "ListObject", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.List<Object>");
    }

} //ColumnsetPackageImpl
