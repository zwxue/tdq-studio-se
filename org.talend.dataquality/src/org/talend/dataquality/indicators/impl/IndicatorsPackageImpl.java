/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
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
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.BlankCountIndicator;
import org.talend.dataquality.indicators.BoxIndicator;
import org.talend.dataquality.indicators.CompositeIndicator;
import org.talend.dataquality.indicators.CountsIndicator;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.DateGrain;
import org.talend.dataquality.indicators.DateParameters;
import org.talend.dataquality.indicators.DistinctCountIndicator;
import org.talend.dataquality.indicators.DuplicateCountIndicator;
import org.talend.dataquality.indicators.EnumStatistics;
import org.talend.dataquality.indicators.FrequencyIndicator;
import org.talend.dataquality.indicators.IQRIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorValueType;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.LengthIndicator;
import org.talend.dataquality.indicators.LowFrequencyIndicator;
import org.talend.dataquality.indicators.LowerQuartileIndicator;
import org.talend.dataquality.indicators.MatchingAlgorithm;
import org.talend.dataquality.indicators.MatchingIndicator;
import org.talend.dataquality.indicators.MaxLengthIndicator;
import org.talend.dataquality.indicators.MaxValueIndicator;
import org.talend.dataquality.indicators.MeanIndicator;
import org.talend.dataquality.indicators.MedianIndicator;
import org.talend.dataquality.indicators.MinLengthIndicator;
import org.talend.dataquality.indicators.MinValueIndicator;
import org.talend.dataquality.indicators.ModeIndicator;
import org.talend.dataquality.indicators.NullCountIndicator;
import org.talend.dataquality.indicators.PatternFreqIndicator;
import org.talend.dataquality.indicators.PatternLowFreqIndicator;
import org.talend.dataquality.indicators.PatternMatchingIndicator;
import org.talend.dataquality.indicators.RangeIndicator;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;
import org.talend.dataquality.indicators.RowCountIndicator;
import org.talend.dataquality.indicators.SqlPatternMatchingIndicator;
import org.talend.dataquality.indicators.SumIndicator;
import org.talend.dataquality.indicators.TextIndicator;
import org.talend.dataquality.indicators.TextParameters;
import org.talend.dataquality.indicators.UniqueCountIndicator;
import org.talend.dataquality.indicators.UpperQuartileIndicator;
import org.talend.dataquality.indicators.ValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.columnset.impl.ColumnsetPackageImpl;
import org.talend.dataquality.indicators.definition.DefinitionPackage;
import org.talend.dataquality.indicators.definition.impl.DefinitionPackageImpl;
import org.talend.dataquality.indicators.schema.SchemaPackage;
import org.talend.dataquality.indicators.schema.impl.SchemaPackageImpl;
import org.talend.dataquality.indicators.sql.IndicatorSqlPackage;
import org.talend.dataquality.indicators.sql.impl.IndicatorSqlPackageImpl;
import org.talend.dataquality.reports.ReportsPackage;
import org.talend.dataquality.reports.impl.ReportsPackageImpl;
import org.talend.dataquality.rules.RulesPackage;
import org.talend.dataquality.rules.impl.RulesPackageImpl;
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
 * <!-- begin-user-doc --> An implementation of the model <b>Package</b>. <!-- end-user-doc -->
 * @generated
 */
public class IndicatorsPackageImpl extends EPackageImpl implements IndicatorsPackage {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass indicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass rowCountIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass meanIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass sumIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass compositeIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass rangeIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass boxIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass frequencyIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass blankCountIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass indicatorParametersEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass medianIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass valueIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass minValueIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass maxValueIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass modeIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass nullCountIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass distinctCountIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass uniqueCountIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass duplicateCountIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass iqrIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass textIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass minLengthIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass maxLengthIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass averageLengthIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass lengthIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass textParametersEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass lowerQuartileIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass upperQuartileIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass countsIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass dateParametersEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass patternMatchingIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass sqlPatternMatchingIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass regexpMatchingIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass matchingIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass lowFrequencyIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass patternFreqIndicatorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass patternLowFreqIndicatorEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EEnum enumStatisticsEEnum = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EEnum dataminingTypeEEnum = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EEnum dateGrainEEnum = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EEnum matchingAlgorithmEEnum = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EEnum indicatorValueTypeEEnum = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType javaSetEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType javaHashMapEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType javaTreeMapEDataType = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EDataType objectArrayEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package package URI value.
     * <p>
     * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
     * performs initialization of the package, or returns the registered package, if one already exists. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.talend.dataquality.indicators.IndicatorsPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private IndicatorsPackageImpl() {
        super(eNS_URI, IndicatorsFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
     * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static IndicatorsPackage init() {
        if (isInited) return (IndicatorsPackage)EPackage.Registry.INSTANCE.getEPackage(IndicatorsPackage.eNS_URI);

        // Obtain or create and register package
        IndicatorsPackageImpl theIndicatorsPackage = (IndicatorsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof IndicatorsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new IndicatorsPackageImpl());

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
        SchemaPackageImpl theSchemaPackage = (SchemaPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(SchemaPackage.eNS_URI) instanceof SchemaPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(SchemaPackage.eNS_URI) : SchemaPackage.eINSTANCE);
        DefinitionPackageImpl theDefinitionPackage = (DefinitionPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DefinitionPackage.eNS_URI) instanceof DefinitionPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DefinitionPackage.eNS_URI) : DefinitionPackage.eINSTANCE);
        IndicatorSqlPackageImpl theIndicatorSqlPackage = (IndicatorSqlPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(IndicatorSqlPackage.eNS_URI) instanceof IndicatorSqlPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(IndicatorSqlPackage.eNS_URI) : IndicatorSqlPackage.eINSTANCE);
        ColumnsetPackageImpl theColumnsetPackage = (ColumnsetPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ColumnsetPackage.eNS_URI) instanceof ColumnsetPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ColumnsetPackage.eNS_URI) : ColumnsetPackage.eINSTANCE);
        ExpressionsPackageImpl theExpressionsPackage_1 = (ExpressionsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(org.talend.dataquality.expressions.ExpressionsPackage.eNS_URI) instanceof ExpressionsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(org.talend.dataquality.expressions.ExpressionsPackage.eNS_URI) : org.talend.dataquality.expressions.ExpressionsPackage.eINSTANCE);
        DomainPackageImpl theDomainPackage = (DomainPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(DomainPackage.eNS_URI) instanceof DomainPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(DomainPackage.eNS_URI) : DomainPackage.eINSTANCE);
        PatternPackageImpl thePatternPackage = (PatternPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(PatternPackage.eNS_URI) instanceof PatternPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(PatternPackage.eNS_URI) : PatternPackage.eINSTANCE);
        SQLPackageImpl theSQLPackage = (SQLPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(SQLPackage.eNS_URI) instanceof SQLPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(SQLPackage.eNS_URI) : SQLPackage.eINSTANCE);
        RulesPackageImpl theRulesPackage = (RulesPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(RulesPackage.eNS_URI) instanceof RulesPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(RulesPackage.eNS_URI) : RulesPackage.eINSTANCE);

        // Create package meta-data objects
        theIndicatorsPackage.createPackageContents();
        theAnalysisPackage.createPackageContents();
        theCategoryPackage.createPackageContents();
        theReportsPackage.createPackageContents();
        theSchemaPackage.createPackageContents();
        theDefinitionPackage.createPackageContents();
        theIndicatorSqlPackage.createPackageContents();
        theColumnsetPackage.createPackageContents();
        theExpressionsPackage_1.createPackageContents();
        theDomainPackage.createPackageContents();
        thePatternPackage.createPackageContents();
        theSQLPackage.createPackageContents();
        theRulesPackage.createPackageContents();

        // Initialize created meta-data
        theIndicatorsPackage.initializePackageContents();
        theAnalysisPackage.initializePackageContents();
        theCategoryPackage.initializePackageContents();
        theReportsPackage.initializePackageContents();
        theSchemaPackage.initializePackageContents();
        theDefinitionPackage.initializePackageContents();
        theIndicatorSqlPackage.initializePackageContents();
        theColumnsetPackage.initializePackageContents();
        theExpressionsPackage_1.initializePackageContents();
        theDomainPackage.initializePackageContents();
        thePatternPackage.initializePackageContents();
        theSQLPackage.initializePackageContents();
        theRulesPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theIndicatorsPackage.freeze();

        return theIndicatorsPackage;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getIndicator() {
        return indicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getIndicator_Count() {
        return (EAttribute)indicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getIndicator_NullCount() {
        return (EAttribute)indicatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getIndicator_Parameters() {
        return (EReference)indicatorEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getIndicator_AnalyzedElement() {
        return (EReference)indicatorEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getIndicator_DataminingType() {
        return (EAttribute)indicatorEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getIndicator_IndicatorDefinition() {
        return (EReference)indicatorEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getIndicator_InstantiatedExpressions() {
        return (EReference)indicatorEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getRowCountIndicator() {
        return rowCountIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getMeanIndicator() {
        return meanIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getSumIndicator() {
        return sumIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getSumIndicator_SumStr() {
        return (EAttribute)sumIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getSumIndicator_Datatype() {
        return (EAttribute)sumIndicatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getCompositeIndicator() {
        return compositeIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getRangeIndicator() {
        return rangeIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getRangeIndicator_LowerValue() {
        return (EReference)rangeIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getRangeIndicator_UpperValue() {
        return (EReference)rangeIndicatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRangeIndicator_Datatype() {
        return (EAttribute)rangeIndicatorEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getRangeIndicator_Range() {
        return (EAttribute)rangeIndicatorEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getBoxIndicator() {
        return boxIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getBoxIndicator_IQR() {
        return (EReference)boxIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getBoxIndicator_RangeIndicator() {
        return (EReference)boxIndicatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getBoxIndicator_MeanIndicator() {
        return (EReference)boxIndicatorEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getBoxIndicator_MedianIndicator() {
        return (EReference)boxIndicatorEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getFrequencyIndicator() {
        return frequencyIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFrequencyIndicator_UniqueValues() {
        return (EAttribute)frequencyIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFrequencyIndicator_DistinctValueCount() {
        return (EAttribute)frequencyIndicatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFrequencyIndicator_UniqueValueCount() {
        return (EAttribute)frequencyIndicatorEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFrequencyIndicator_DuplicateValueCount() {
        return (EAttribute)frequencyIndicatorEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getFrequencyIndicator_ValueToFreq() {
        return (EAttribute)frequencyIndicatorEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getBlankCountIndicator() {
        return blankCountIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getBlankCountIndicator_BlankCount() {
        return (EAttribute)blankCountIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getIndicatorParameters() {
        return indicatorParametersEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getIndicatorParameters_IndicatorValidDomain() {
        return (EReference)indicatorParametersEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getIndicatorParameters_DataValidDomain() {
        return (EReference)indicatorParametersEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getIndicatorParameters_Bins() {
        return (EReference)indicatorParametersEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getIndicatorParameters_TextParameter() {
        return (EReference)indicatorParametersEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getIndicatorParameters_DateParameters() {
        return (EReference)indicatorParametersEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getIndicatorParameters_TopN() {
        return (EAttribute)indicatorParametersEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getMedianIndicator() {
        return medianIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMedianIndicator_Median() {
        return (EAttribute)medianIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMedianIndicator_FrequenceTable() {
        return (EAttribute)medianIndicatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMedianIndicator_DateMedian() {
        return (EAttribute)medianIndicatorEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getValueIndicator() {
        return valueIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getValueIndicator_Value() {
        return (EAttribute)valueIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getValueIndicator_Datatype() {
        return (EAttribute)valueIndicatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getMinValueIndicator() {
        return minValueIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getMaxValueIndicator() {
        return maxValueIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getModeIndicator() {
        return modeIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModeIndicator_Mode() {
        return (EAttribute)modeIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getNullCountIndicator() {
        return nullCountIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getDistinctCountIndicator() {
        return distinctCountIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDistinctCountIndicator_DistinctValueCount() {
        return (EAttribute)distinctCountIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getUniqueCountIndicator() {
        return uniqueCountIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getUniqueCountIndicator_UniqueValueCount() {
        return (EAttribute)uniqueCountIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getDuplicateCountIndicator() {
        return duplicateCountIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDuplicateCountIndicator_DuplicateValueCount() {
        return (EAttribute)duplicateCountIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getIQRIndicator() {
        return iqrIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getTextIndicator() {
        return textIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTextIndicator_AverageLengthIndicator() {
        return (EReference)textIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTextIndicator_MaxLengthIndicator() {
        return (EReference)textIndicatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTextIndicator_MinLengthIndicator() {
        return (EReference)textIndicatorEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getMinLengthIndicator() {
        return minLengthIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getMaxLengthIndicator() {
        return maxLengthIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getAverageLengthIndicator() {
        return averageLengthIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getAverageLengthIndicator_SumLength() {
        return (EAttribute)averageLengthIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getLengthIndicator() {
        return lengthIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getLengthIndicator_Length() {
        return (EAttribute)lengthIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getTextParameters() {
        return textParametersEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTextParameters_UseBlank() {
        return (EAttribute)textParametersEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTextParameters_MatchingAlgorithm() {
        return (EAttribute)textParametersEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTextParameters_IgnoreCase() {
        return (EAttribute)textParametersEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTextParameters_UseNulls() {
        return (EAttribute)textParametersEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getLowerQuartileIndicator() {
        return lowerQuartileIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getUpperQuartileIndicator() {
        return upperQuartileIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getCountsIndicator() {
        return countsIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCountsIndicator_BlankCountIndicator() {
        return (EReference)countsIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCountsIndicator_RowCountIndicator() {
        return (EReference)countsIndicatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCountsIndicator_NullCountIndicator() {
        return (EReference)countsIndicatorEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCountsIndicator_UniqueCountIndicator() {
        return (EReference)countsIndicatorEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCountsIndicator_DistinctCountIndicator() {
        return (EReference)countsIndicatorEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getCountsIndicator_DuplicateCountIndicator() {
        return (EReference)countsIndicatorEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getDateParameters() {
        return dateParametersEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getDateParameters_DateAggregationType() {
        return (EAttribute)dateParametersEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getPatternMatchingIndicator() {
        return patternMatchingIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getSqlPatternMatchingIndicator() {
        return sqlPatternMatchingIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getRegexpMatchingIndicator() {
        return regexpMatchingIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getMatchingIndicator() {
        return matchingIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMatchingIndicator_MatchingValueCount() {
        return (EAttribute)matchingIndicatorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getMatchingIndicator_NotMatchingValueCount() {
        return (EAttribute)matchingIndicatorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getLowFrequencyIndicator() {
        return lowFrequencyIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getPatternFreqIndicator() {
        return patternFreqIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getPatternLowFreqIndicator() {
        return patternLowFreqIndicatorEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EEnum getEnumStatistics() {
        return enumStatisticsEEnum;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EEnum getDataminingType() {
        return dataminingTypeEEnum;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EEnum getDateGrain() {
        return dateGrainEEnum;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EEnum getMatchingAlgorithm() {
        return matchingAlgorithmEEnum;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EEnum getIndicatorValueType() {
        return indicatorValueTypeEEnum;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getJavaSet() {
        return javaSetEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getJavaHashMap() {
        return javaHashMapEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getJavaTreeMap() {
        return javaTreeMapEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EDataType getObjectArray() {
        return objectArrayEDataType;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public IndicatorsFactory getIndicatorsFactory() {
        return (IndicatorsFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        indicatorEClass = createEClass(INDICATOR);
        createEAttribute(indicatorEClass, INDICATOR__COUNT);
        createEAttribute(indicatorEClass, INDICATOR__NULL_COUNT);
        createEReference(indicatorEClass, INDICATOR__PARAMETERS);
        createEReference(indicatorEClass, INDICATOR__ANALYZED_ELEMENT);
        createEAttribute(indicatorEClass, INDICATOR__DATAMINING_TYPE);
        createEReference(indicatorEClass, INDICATOR__INDICATOR_DEFINITION);
        createEReference(indicatorEClass, INDICATOR__INSTANTIATED_EXPRESSIONS);

        rowCountIndicatorEClass = createEClass(ROW_COUNT_INDICATOR);

        meanIndicatorEClass = createEClass(MEAN_INDICATOR);

        sumIndicatorEClass = createEClass(SUM_INDICATOR);
        createEAttribute(sumIndicatorEClass, SUM_INDICATOR__SUM_STR);
        createEAttribute(sumIndicatorEClass, SUM_INDICATOR__DATATYPE);

        compositeIndicatorEClass = createEClass(COMPOSITE_INDICATOR);

        rangeIndicatorEClass = createEClass(RANGE_INDICATOR);
        createEReference(rangeIndicatorEClass, RANGE_INDICATOR__LOWER_VALUE);
        createEReference(rangeIndicatorEClass, RANGE_INDICATOR__UPPER_VALUE);
        createEAttribute(rangeIndicatorEClass, RANGE_INDICATOR__DATATYPE);
        createEAttribute(rangeIndicatorEClass, RANGE_INDICATOR__RANGE);

        boxIndicatorEClass = createEClass(BOX_INDICATOR);
        createEReference(boxIndicatorEClass, BOX_INDICATOR__IQR);
        createEReference(boxIndicatorEClass, BOX_INDICATOR__RANGE_INDICATOR);
        createEReference(boxIndicatorEClass, BOX_INDICATOR__MEAN_INDICATOR);
        createEReference(boxIndicatorEClass, BOX_INDICATOR__MEDIAN_INDICATOR);

        frequencyIndicatorEClass = createEClass(FREQUENCY_INDICATOR);
        createEAttribute(frequencyIndicatorEClass, FREQUENCY_INDICATOR__UNIQUE_VALUES);
        createEAttribute(frequencyIndicatorEClass, FREQUENCY_INDICATOR__DISTINCT_VALUE_COUNT);
        createEAttribute(frequencyIndicatorEClass, FREQUENCY_INDICATOR__UNIQUE_VALUE_COUNT);
        createEAttribute(frequencyIndicatorEClass, FREQUENCY_INDICATOR__DUPLICATE_VALUE_COUNT);
        createEAttribute(frequencyIndicatorEClass, FREQUENCY_INDICATOR__VALUE_TO_FREQ);

        blankCountIndicatorEClass = createEClass(BLANK_COUNT_INDICATOR);
        createEAttribute(blankCountIndicatorEClass, BLANK_COUNT_INDICATOR__BLANK_COUNT);

        indicatorParametersEClass = createEClass(INDICATOR_PARAMETERS);
        createEReference(indicatorParametersEClass, INDICATOR_PARAMETERS__INDICATOR_VALID_DOMAIN);
        createEReference(indicatorParametersEClass, INDICATOR_PARAMETERS__DATA_VALID_DOMAIN);
        createEReference(indicatorParametersEClass, INDICATOR_PARAMETERS__BINS);
        createEReference(indicatorParametersEClass, INDICATOR_PARAMETERS__TEXT_PARAMETER);
        createEReference(indicatorParametersEClass, INDICATOR_PARAMETERS__DATE_PARAMETERS);
        createEAttribute(indicatorParametersEClass, INDICATOR_PARAMETERS__TOP_N);

        medianIndicatorEClass = createEClass(MEDIAN_INDICATOR);
        createEAttribute(medianIndicatorEClass, MEDIAN_INDICATOR__MEDIAN);
        createEAttribute(medianIndicatorEClass, MEDIAN_INDICATOR__FREQUENCE_TABLE);
        createEAttribute(medianIndicatorEClass, MEDIAN_INDICATOR__DATE_MEDIAN);

        valueIndicatorEClass = createEClass(VALUE_INDICATOR);
        createEAttribute(valueIndicatorEClass, VALUE_INDICATOR__VALUE);
        createEAttribute(valueIndicatorEClass, VALUE_INDICATOR__DATATYPE);

        minValueIndicatorEClass = createEClass(MIN_VALUE_INDICATOR);

        maxValueIndicatorEClass = createEClass(MAX_VALUE_INDICATOR);

        modeIndicatorEClass = createEClass(MODE_INDICATOR);
        createEAttribute(modeIndicatorEClass, MODE_INDICATOR__MODE);

        nullCountIndicatorEClass = createEClass(NULL_COUNT_INDICATOR);

        distinctCountIndicatorEClass = createEClass(DISTINCT_COUNT_INDICATOR);
        createEAttribute(distinctCountIndicatorEClass, DISTINCT_COUNT_INDICATOR__DISTINCT_VALUE_COUNT);

        uniqueCountIndicatorEClass = createEClass(UNIQUE_COUNT_INDICATOR);
        createEAttribute(uniqueCountIndicatorEClass, UNIQUE_COUNT_INDICATOR__UNIQUE_VALUE_COUNT);

        duplicateCountIndicatorEClass = createEClass(DUPLICATE_COUNT_INDICATOR);
        createEAttribute(duplicateCountIndicatorEClass, DUPLICATE_COUNT_INDICATOR__DUPLICATE_VALUE_COUNT);

        iqrIndicatorEClass = createEClass(IQR_INDICATOR);

        textIndicatorEClass = createEClass(TEXT_INDICATOR);
        createEReference(textIndicatorEClass, TEXT_INDICATOR__AVERAGE_LENGTH_INDICATOR);
        createEReference(textIndicatorEClass, TEXT_INDICATOR__MAX_LENGTH_INDICATOR);
        createEReference(textIndicatorEClass, TEXT_INDICATOR__MIN_LENGTH_INDICATOR);

        minLengthIndicatorEClass = createEClass(MIN_LENGTH_INDICATOR);

        maxLengthIndicatorEClass = createEClass(MAX_LENGTH_INDICATOR);

        averageLengthIndicatorEClass = createEClass(AVERAGE_LENGTH_INDICATOR);
        createEAttribute(averageLengthIndicatorEClass, AVERAGE_LENGTH_INDICATOR__SUM_LENGTH);

        lengthIndicatorEClass = createEClass(LENGTH_INDICATOR);
        createEAttribute(lengthIndicatorEClass, LENGTH_INDICATOR__LENGTH);

        textParametersEClass = createEClass(TEXT_PARAMETERS);
        createEAttribute(textParametersEClass, TEXT_PARAMETERS__USE_BLANK);
        createEAttribute(textParametersEClass, TEXT_PARAMETERS__MATCHING_ALGORITHM);
        createEAttribute(textParametersEClass, TEXT_PARAMETERS__IGNORE_CASE);
        createEAttribute(textParametersEClass, TEXT_PARAMETERS__USE_NULLS);

        lowerQuartileIndicatorEClass = createEClass(LOWER_QUARTILE_INDICATOR);

        upperQuartileIndicatorEClass = createEClass(UPPER_QUARTILE_INDICATOR);

        countsIndicatorEClass = createEClass(COUNTS_INDICATOR);
        createEReference(countsIndicatorEClass, COUNTS_INDICATOR__BLANK_COUNT_INDICATOR);
        createEReference(countsIndicatorEClass, COUNTS_INDICATOR__ROW_COUNT_INDICATOR);
        createEReference(countsIndicatorEClass, COUNTS_INDICATOR__NULL_COUNT_INDICATOR);
        createEReference(countsIndicatorEClass, COUNTS_INDICATOR__UNIQUE_COUNT_INDICATOR);
        createEReference(countsIndicatorEClass, COUNTS_INDICATOR__DISTINCT_COUNT_INDICATOR);
        createEReference(countsIndicatorEClass, COUNTS_INDICATOR__DUPLICATE_COUNT_INDICATOR);

        dateParametersEClass = createEClass(DATE_PARAMETERS);
        createEAttribute(dateParametersEClass, DATE_PARAMETERS__DATE_AGGREGATION_TYPE);

        patternMatchingIndicatorEClass = createEClass(PATTERN_MATCHING_INDICATOR);

        sqlPatternMatchingIndicatorEClass = createEClass(SQL_PATTERN_MATCHING_INDICATOR);

        regexpMatchingIndicatorEClass = createEClass(REGEXP_MATCHING_INDICATOR);

        matchingIndicatorEClass = createEClass(MATCHING_INDICATOR);
        createEAttribute(matchingIndicatorEClass, MATCHING_INDICATOR__MATCHING_VALUE_COUNT);
        createEAttribute(matchingIndicatorEClass, MATCHING_INDICATOR__NOT_MATCHING_VALUE_COUNT);

        lowFrequencyIndicatorEClass = createEClass(LOW_FREQUENCY_INDICATOR);

        patternFreqIndicatorEClass = createEClass(PATTERN_FREQ_INDICATOR);

        patternLowFreqIndicatorEClass = createEClass(PATTERN_LOW_FREQ_INDICATOR);

        // Create enums
        enumStatisticsEEnum = createEEnum(ENUM_STATISTICS);
        dataminingTypeEEnum = createEEnum(DATAMINING_TYPE);
        dateGrainEEnum = createEEnum(DATE_GRAIN);
        matchingAlgorithmEEnum = createEEnum(MATCHING_ALGORITHM);
        indicatorValueTypeEEnum = createEEnum(INDICATOR_VALUE_TYPE);

        // Create data types
        javaSetEDataType = createEDataType(JAVA_SET);
        javaHashMapEDataType = createEDataType(JAVA_HASH_MAP);
        javaTreeMapEDataType = createEDataType(JAVA_TREE_MAP);
        objectArrayEDataType = createEDataType(OBJECT_ARRAY);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
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
        SchemaPackage theSchemaPackage = (SchemaPackage)EPackage.Registry.INSTANCE.getEPackage(SchemaPackage.eNS_URI);
        DefinitionPackage theDefinitionPackage = (DefinitionPackage)EPackage.Registry.INSTANCE.getEPackage(DefinitionPackage.eNS_URI);
        IndicatorSqlPackage theIndicatorSqlPackage = (IndicatorSqlPackage)EPackage.Registry.INSTANCE.getEPackage(IndicatorSqlPackage.eNS_URI);
        ColumnsetPackage theColumnsetPackage = (ColumnsetPackage)EPackage.Registry.INSTANCE.getEPackage(ColumnsetPackage.eNS_URI);
        CorePackage theCorePackage = (CorePackage)EPackage.Registry.INSTANCE.getEPackage(CorePackage.eNS_URI);
        DomainPackage theDomainPackage = (DomainPackage)EPackage.Registry.INSTANCE.getEPackage(DomainPackage.eNS_URI);

        // Add subpackages
        getESubpackages().add(theSchemaPackage);
        getESubpackages().add(theDefinitionPackage);
        getESubpackages().add(theIndicatorSqlPackage);
        getESubpackages().add(theColumnsetPackage);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        indicatorEClass.getESuperTypes().add(theCorePackage.getModelElement());
        rowCountIndicatorEClass.getESuperTypes().add(this.getIndicator());
        meanIndicatorEClass.getESuperTypes().add(this.getSumIndicator());
        sumIndicatorEClass.getESuperTypes().add(this.getIndicator());
        compositeIndicatorEClass.getESuperTypes().add(this.getIndicator());
        rangeIndicatorEClass.getESuperTypes().add(this.getCompositeIndicator());
        boxIndicatorEClass.getESuperTypes().add(this.getCompositeIndicator());
        frequencyIndicatorEClass.getESuperTypes().add(this.getIndicator());
        blankCountIndicatorEClass.getESuperTypes().add(this.getIndicator());
        medianIndicatorEClass.getESuperTypes().add(this.getIndicator());
        valueIndicatorEClass.getESuperTypes().add(this.getIndicator());
        minValueIndicatorEClass.getESuperTypes().add(this.getValueIndicator());
        maxValueIndicatorEClass.getESuperTypes().add(this.getValueIndicator());
        modeIndicatorEClass.getESuperTypes().add(this.getIndicator());
        nullCountIndicatorEClass.getESuperTypes().add(this.getIndicator());
        distinctCountIndicatorEClass.getESuperTypes().add(this.getIndicator());
        uniqueCountIndicatorEClass.getESuperTypes().add(this.getIndicator());
        duplicateCountIndicatorEClass.getESuperTypes().add(this.getIndicator());
        iqrIndicatorEClass.getESuperTypes().add(this.getRangeIndicator());
        textIndicatorEClass.getESuperTypes().add(this.getCompositeIndicator());
        minLengthIndicatorEClass.getESuperTypes().add(this.getLengthIndicator());
        maxLengthIndicatorEClass.getESuperTypes().add(this.getLengthIndicator());
        averageLengthIndicatorEClass.getESuperTypes().add(this.getLengthIndicator());
        lengthIndicatorEClass.getESuperTypes().add(this.getIndicator());
        lowerQuartileIndicatorEClass.getESuperTypes().add(this.getMinValueIndicator());
        upperQuartileIndicatorEClass.getESuperTypes().add(this.getMaxValueIndicator());
        countsIndicatorEClass.getESuperTypes().add(this.getCompositeIndicator());
        patternMatchingIndicatorEClass.getESuperTypes().add(this.getMatchingIndicator());
        sqlPatternMatchingIndicatorEClass.getESuperTypes().add(this.getPatternMatchingIndicator());
        regexpMatchingIndicatorEClass.getESuperTypes().add(this.getPatternMatchingIndicator());
        matchingIndicatorEClass.getESuperTypes().add(this.getIndicator());
        lowFrequencyIndicatorEClass.getESuperTypes().add(this.getFrequencyIndicator());
        patternFreqIndicatorEClass.getESuperTypes().add(this.getFrequencyIndicator());
        patternLowFreqIndicatorEClass.getESuperTypes().add(this.getLowFrequencyIndicator());

        // Initialize classes and features; add operations and parameters
        initEClass(indicatorEClass, Indicator.class, "Indicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getIndicator_Count(), ecorePackage.getELongObject(), "count", "0", 0, 1, Indicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getIndicator_NullCount(), ecorePackage.getELongObject(), "nullCount", "0", 0, 1, Indicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getIndicator_Parameters(), this.getIndicatorParameters(), null, "parameters", null, 0, 1, Indicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getIndicator_AnalyzedElement(), theCorePackage.getModelElement(), null, "analyzedElement", null, 0, 1, Indicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getIndicator_DataminingType(), this.getDataminingType(), "dataminingType", null, 0, 1, Indicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getIndicator_IndicatorDefinition(), theDefinitionPackage.getIndicatorDefinition(), null, "indicatorDefinition", null, 0, 1, Indicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getIndicator_InstantiatedExpressions(), theCorePackage.getExpression(), null, "instantiatedExpressions", null, 0, -1, Indicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        EOperation op = addEOperation(indicatorEClass, ecorePackage.getEBoolean(), "handle", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, ecorePackage.getEJavaObject(), "data", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(indicatorEClass, ecorePackage.getEBoolean(), "reset", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(indicatorEClass, ecorePackage.getEString(), "getPurpose", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(indicatorEClass, ecorePackage.getEString(), "getLongDescription", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(indicatorEClass, ecorePackage.getEBoolean(), "prepare", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(indicatorEClass, ecorePackage.getEBoolean(), "finalizeComputation", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(indicatorEClass, ecorePackage.getEBoolean(), "storeSqlResults", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getObjectArray(), "objects", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(indicatorEClass, theCorePackage.getExpression(), "getInstantiatedExpressions", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, ecorePackage.getEString(), "language", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(indicatorEClass, ecorePackage.getEBoolean(), "setInstantiatedExpression", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, theCorePackage.getExpression(), "expression", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(indicatorEClass, ecorePackage.getELongObject(), "getIntegerValue", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(indicatorEClass, ecorePackage.getEDoubleObject(), "getRealValue", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(indicatorEClass, this.getIndicatorValueType(), "getValueType", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(indicatorEClass, ecorePackage.getEString(), "getInstanceValue", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(rowCountIndicatorEClass, RowCountIndicator.class, "RowCountIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(meanIndicatorEClass, MeanIndicator.class, "MeanIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        addEOperation(meanIndicatorEClass, ecorePackage.getEDoubleObject(), "getMean", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(meanIndicatorEClass, ecorePackage.getEDoubleObject(), "getMeanWithNulls", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, ecorePackage.getEDouble(), "valueForNull", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(sumIndicatorEClass, SumIndicator.class, "SumIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getSumIndicator_SumStr(), ecorePackage.getEString(), "sumStr", null, 0, 1, SumIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getSumIndicator_Datatype(), ecorePackage.getEInt(), "datatype", null, 0, 1, SumIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(compositeIndicatorEClass, CompositeIndicator.class, "CompositeIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        addEOperation(compositeIndicatorEClass, this.getIndicator(), "getChildIndicators", 0, -1, IS_UNIQUE, IS_ORDERED);

        addEOperation(compositeIndicatorEClass, this.getIndicator(), "getLeafIndicators", 0, -1, IS_UNIQUE, IS_ORDERED);

        addEOperation(compositeIndicatorEClass, this.getIndicator(), "getAllChildIndicators", 0, -1, IS_UNIQUE, IS_ORDERED);

        initEClass(rangeIndicatorEClass, RangeIndicator.class, "RangeIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getRangeIndicator_LowerValue(), this.getMinValueIndicator(), null, "lowerValue", null, 0, 1, RangeIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getRangeIndicator_UpperValue(), this.getMaxValueIndicator(), null, "upperValue", null, 0, 1, RangeIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getRangeIndicator_Datatype(), ecorePackage.getEInt(), "datatype", null, 0, 1, RangeIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getRangeIndicator_Range(), ecorePackage.getEString(), "range", null, 0, 1, RangeIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(boxIndicatorEClass, BoxIndicator.class, "BoxIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getBoxIndicator_IQR(), this.getIQRIndicator(), null, "IQR", null, 0, 1, BoxIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getBoxIndicator_RangeIndicator(), this.getRangeIndicator(), null, "rangeIndicator", null, 0, 1, BoxIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getBoxIndicator_MeanIndicator(), this.getMeanIndicator(), null, "meanIndicator", null, 0, 1, BoxIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getBoxIndicator_MedianIndicator(), this.getMedianIndicator(), null, "medianIndicator", null, 0, 1, BoxIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(frequencyIndicatorEClass, FrequencyIndicator.class, "FrequencyIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getFrequencyIndicator_UniqueValues(), ecorePackage.getEJavaObject(), "uniqueValues", null, 0, -1, FrequencyIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFrequencyIndicator_DistinctValueCount(), ecorePackage.getELongObject(), "distinctValueCount", null, 0, 1, FrequencyIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFrequencyIndicator_UniqueValueCount(), ecorePackage.getELongObject(), "uniqueValueCount", null, 0, 1, FrequencyIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFrequencyIndicator_DuplicateValueCount(), ecorePackage.getELongObject(), "duplicateValueCount", null, 0, 1, FrequencyIndicator.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getFrequencyIndicator_ValueToFreq(), this.getJavaHashMap(), "valueToFreq", null, 0, 1, FrequencyIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        op = addEOperation(frequencyIndicatorEClass, ecorePackage.getELongObject(), "getCount", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, ecorePackage.getEJavaObject(), "dataValue", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = addEOperation(frequencyIndicatorEClass, ecorePackage.getEDoubleObject(), "getFrequency", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, ecorePackage.getEJavaObject(), "dataValue", 0, 1, IS_UNIQUE, IS_ORDERED);

        addEOperation(frequencyIndicatorEClass, this.getJavaSet(), "getDistinctValues", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(blankCountIndicatorEClass, BlankCountIndicator.class, "BlankCountIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getBlankCountIndicator_BlankCount(), ecorePackage.getELongObject(), "blankCount", "0", 0, 1, BlankCountIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(indicatorParametersEClass, IndicatorParameters.class, "IndicatorParameters", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getIndicatorParameters_IndicatorValidDomain(), theDomainPackage.getDomain(), null, "indicatorValidDomain", null, 0, 1, IndicatorParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getIndicatorParameters_DataValidDomain(), theDomainPackage.getDomain(), null, "dataValidDomain", null, 0, 1, IndicatorParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getIndicatorParameters_Bins(), theDomainPackage.getDomain(), null, "bins", null, 0, 1, IndicatorParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getIndicatorParameters_TextParameter(), this.getTextParameters(), null, "textParameter", null, 0, 1, IndicatorParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getIndicatorParameters_DateParameters(), this.getDateParameters(), null, "dateParameters", null, 0, 1, IndicatorParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getIndicatorParameters_TopN(), ecorePackage.getEInt(), "topN", "10", 0, 1, IndicatorParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(medianIndicatorEClass, MedianIndicator.class, "MedianIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getMedianIndicator_Median(), ecorePackage.getEDoubleObject(), "median", null, 0, 1, MedianIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getMedianIndicator_FrequenceTable(), this.getJavaTreeMap(), "frequenceTable", null, 0, 1, MedianIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getMedianIndicator_DateMedian(), ecorePackage.getEDate(), "dateMedian", null, 0, 1, MedianIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        addEOperation(medianIndicatorEClass, ecorePackage.getEBoolean(), "computeMedian", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(valueIndicatorEClass, ValueIndicator.class, "ValueIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getValueIndicator_Value(), ecorePackage.getEString(), "value", null, 0, 1, ValueIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getValueIndicator_Datatype(), ecorePackage.getEInt(), "datatype", null, 0, 1, ValueIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(minValueIndicatorEClass, MinValueIndicator.class, "MinValueIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(maxValueIndicatorEClass, MaxValueIndicator.class, "MaxValueIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(modeIndicatorEClass, ModeIndicator.class, "ModeIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getModeIndicator_Mode(), ecorePackage.getEJavaObject(), "mode", null, 0, 1, ModeIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(nullCountIndicatorEClass, NullCountIndicator.class, "NullCountIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(distinctCountIndicatorEClass, DistinctCountIndicator.class, "DistinctCountIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDistinctCountIndicator_DistinctValueCount(), ecorePackage.getELongObject(), "distinctValueCount", null, 0, 1, DistinctCountIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        addEOperation(distinctCountIndicatorEClass, this.getJavaSet(), "getDistinctValues", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(uniqueCountIndicatorEClass, UniqueCountIndicator.class, "UniqueCountIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getUniqueCountIndicator_UniqueValueCount(), ecorePackage.getELongObject(), "uniqueValueCount", null, 0, 1, UniqueCountIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        addEOperation(uniqueCountIndicatorEClass, this.getJavaSet(), "getUniqueValues", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(duplicateCountIndicatorEClass, DuplicateCountIndicator.class, "DuplicateCountIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDuplicateCountIndicator_DuplicateValueCount(), ecorePackage.getELongObject(), "duplicateValueCount", null, 0, 1, DuplicateCountIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        addEOperation(duplicateCountIndicatorEClass, this.getJavaSet(), "getDuplicateValues", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(iqrIndicatorEClass, IQRIndicator.class, "IQRIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(textIndicatorEClass, TextIndicator.class, "TextIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTextIndicator_AverageLengthIndicator(), this.getAverageLengthIndicator(), null, "averageLengthIndicator", null, 0, 1, TextIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTextIndicator_MaxLengthIndicator(), this.getMaxLengthIndicator(), null, "maxLengthIndicator", null, 0, 1, TextIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTextIndicator_MinLengthIndicator(), this.getMinLengthIndicator(), null, "MinLengthIndicator", null, 0, 1, TextIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(minLengthIndicatorEClass, MinLengthIndicator.class, "MinLengthIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(maxLengthIndicatorEClass, MaxLengthIndicator.class, "MaxLengthIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(averageLengthIndicatorEClass, AverageLengthIndicator.class, "AverageLengthIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getAverageLengthIndicator_SumLength(), ecorePackage.getEDoubleObject(), "sumLength", "0", 0, 1, AverageLengthIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        addEOperation(averageLengthIndicatorEClass, ecorePackage.getEDouble(), "getAverageLength", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(lengthIndicatorEClass, LengthIndicator.class, "LengthIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getLengthIndicator_Length(), ecorePackage.getELongObject(), "length", null, 0, 1, LengthIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(textParametersEClass, TextParameters.class, "TextParameters", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getTextParameters_UseBlank(), ecorePackage.getEBoolean(), "useBlank", "true", 0, 1, TextParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTextParameters_MatchingAlgorithm(), this.getMatchingAlgorithm(), "matchingAlgorithm", null, 0, 1, TextParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTextParameters_IgnoreCase(), ecorePackage.getEBoolean(), "ignoreCase", null, 0, 1, TextParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTextParameters_UseNulls(), ecorePackage.getEBoolean(), "useNulls", null, 0, 1, TextParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(lowerQuartileIndicatorEClass, LowerQuartileIndicator.class, "LowerQuartileIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(upperQuartileIndicatorEClass, UpperQuartileIndicator.class, "UpperQuartileIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(countsIndicatorEClass, CountsIndicator.class, "CountsIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getCountsIndicator_BlankCountIndicator(), this.getBlankCountIndicator(), null, "blankCountIndicator", null, 0, 1, CountsIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getCountsIndicator_RowCountIndicator(), this.getRowCountIndicator(), null, "RowCountIndicator", null, 0, 1, CountsIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getCountsIndicator_NullCountIndicator(), this.getNullCountIndicator(), null, "NullCountIndicator", null, 0, 1, CountsIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getCountsIndicator_UniqueCountIndicator(), this.getUniqueCountIndicator(), null, "UniqueCountIndicator", null, 0, 1, CountsIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getCountsIndicator_DistinctCountIndicator(), this.getDistinctCountIndicator(), null, "distinctCountIndicator", null, 0, 1, CountsIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getCountsIndicator_DuplicateCountIndicator(), this.getDuplicateCountIndicator(), null, "duplicateCountIndicator", null, 0, 1, CountsIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(dateParametersEClass, DateParameters.class, "DateParameters", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getDateParameters_DateAggregationType(), this.getDateGrain(), "dateAggregationType", "year", 0, 1, DateParameters.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(patternMatchingIndicatorEClass, PatternMatchingIndicator.class, "PatternMatchingIndicator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(sqlPatternMatchingIndicatorEClass, SqlPatternMatchingIndicator.class, "SqlPatternMatchingIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(regexpMatchingIndicatorEClass, RegexpMatchingIndicator.class, "RegexpMatchingIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(matchingIndicatorEClass, MatchingIndicator.class, "MatchingIndicator", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getMatchingIndicator_MatchingValueCount(), ecorePackage.getELongObject(), "matchingValueCount", null, 0, 1, MatchingIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getMatchingIndicator_NotMatchingValueCount(), ecorePackage.getELongObject(), "notMatchingValueCount", null, 0, 1, MatchingIndicator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(lowFrequencyIndicatorEClass, LowFrequencyIndicator.class, "LowFrequencyIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(patternFreqIndicatorEClass, PatternFreqIndicator.class, "PatternFreqIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        initEClass(patternLowFreqIndicatorEClass, PatternLowFreqIndicator.class, "PatternLowFreqIndicator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        // Initialize enums and add enum literals
        initEEnum(enumStatisticsEEnum, EnumStatistics.class, "EnumStatistics");
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.MIN_VALUE);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.MAX_VALUE);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.LOWER_QUARTILE);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.UPPER_QUARTILE);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.MEAN);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.MEDIAN);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.ROW_COUNT);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.NULL_COUNT);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.DISTINCT_COUNT);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.UNIQUE_COUNT);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.DUPLICATE_COUNT);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.BOX);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.FREQ_TABLE);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.RANGE);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.IQR);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.BIN_COUNT);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.MATCHING_VALUES);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.BLANK_COUNT);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.MIN_LENGTH);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.MAX_LENGTH);
        addEEnumLiteral(enumStatisticsEEnum, EnumStatistics.AVG_LENGTH);

        initEEnum(dataminingTypeEEnum, DataminingType.class, "DataminingType");
        addEEnumLiteral(dataminingTypeEEnum, DataminingType.NOMINAL);
        addEEnumLiteral(dataminingTypeEEnum, DataminingType.INTERVAL);
        addEEnumLiteral(dataminingTypeEEnum, DataminingType.UNSTRUCTURED_TEXT);
        addEEnumLiteral(dataminingTypeEEnum, DataminingType.OTHER);

        initEEnum(dateGrainEEnum, DateGrain.class, "DateGrain");
        addEEnumLiteral(dateGrainEEnum, DateGrain.DAY);
        addEEnumLiteral(dateGrainEEnum, DateGrain.WEEK);
        addEEnumLiteral(dateGrainEEnum, DateGrain.MONTH);
        addEEnumLiteral(dateGrainEEnum, DateGrain.QUARTER);
        addEEnumLiteral(dateGrainEEnum, DateGrain.YEAR);
        addEEnumLiteral(dateGrainEEnum, DateGrain.NONE);

        initEEnum(matchingAlgorithmEEnum, MatchingAlgorithm.class, "MatchingAlgorithm");
        addEEnumLiteral(matchingAlgorithmEEnum, MatchingAlgorithm.EXACT);
        addEEnumLiteral(matchingAlgorithmEEnum, MatchingAlgorithm.METAPHONE);
        addEEnumLiteral(matchingAlgorithmEEnum, MatchingAlgorithm.DOUBLE_METAPHONE);
        addEEnumLiteral(matchingAlgorithmEEnum, MatchingAlgorithm.LEVENSHTEIN);
        addEEnumLiteral(matchingAlgorithmEEnum, MatchingAlgorithm.SOUNDEX);
        addEEnumLiteral(matchingAlgorithmEEnum, MatchingAlgorithm.REFINED_SOUNDEX);

        initEEnum(indicatorValueTypeEEnum, IndicatorValueType.class, "IndicatorValueType");
        addEEnumLiteral(indicatorValueTypeEEnum, IndicatorValueType.INTEGER_VALUE);
        addEEnumLiteral(indicatorValueTypeEEnum, IndicatorValueType.REAL_VALUE);
        addEEnumLiteral(indicatorValueTypeEEnum, IndicatorValueType.INSTANCE_VALUE);

        // Initialize data types
        initEDataType(javaSetEDataType, Set.class, "JavaSet", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.Set<Object>");
        initEDataType(javaHashMapEDataType, HashMap.class, "JavaHashMap", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.HashMap<Object, java.lang.Long>");
        initEDataType(javaTreeMapEDataType, TreeMap.class, "JavaTreeMap", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.TreeMap<Object, java.lang.Long>");
        initEDataType(objectArrayEDataType, List.class, "ObjectArray", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS, "java.util.List<Object[]>");

        // Create resource
        createResource(eNS_URI);
    }

} // IndicatorsPackageImpl
