/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.cwm.xml.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.talend.cwm.constants.ConstantsPackage;
import org.talend.cwm.constants.impl.ConstantsPackageImpl;
import org.talend.cwm.relational.impl.RelationalPackageImpl;
import org.talend.cwm.softwaredeployment.impl.SoftwaredeploymentPackageImpl;
import org.talend.cwm.xml.TdXMLContent;
import org.talend.cwm.xml.TdXMLDocument;
import org.talend.cwm.xml.TdXMLElement;
import org.talend.cwm.xml.XmlFactory;
import org.talend.cwm.xml.XmlPackage;
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
public class XmlPackageImpl extends EPackageImpl implements XmlPackage {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass tdXMLElementEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass tdXMLContentEClass = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private EClass tdXMLDocumentEClass = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with {@link org.eclipse.emf.ecore.EPackage.Registry
     * EPackage.Registry} by the package package URI value.
     * <p>
     * Note: the correct way to create the package is via the static factory method {@link #init init()}, which also
     * performs initialization of the package, or returns the registered package, if one already exists. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.talend.cwm.xml.XmlPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private XmlPackageImpl() {
        super(eNS_URI, XmlFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>This method is used to initialize {@link XmlPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static XmlPackage init() {
        if (isInited) return (XmlPackage)EPackage.Registry.INSTANCE.getEPackage(XmlPackage.eNS_URI);

        // Obtain or create and register package
        XmlPackageImpl theXmlPackage = (XmlPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof XmlPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new XmlPackageImpl());

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
        orgomg.cwm.resource.xml.XmlPackage.eINSTANCE.eClass();
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
        RelationalPackageImpl theRelationalPackage_1 = (RelationalPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(org.talend.cwm.relational.RelationalPackage.eNS_URI) instanceof RelationalPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(org.talend.cwm.relational.RelationalPackage.eNS_URI) : org.talend.cwm.relational.RelationalPackage.eINSTANCE);
        SoftwaredeploymentPackageImpl theSoftwaredeploymentPackage_1 = (SoftwaredeploymentPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(org.talend.cwm.softwaredeployment.SoftwaredeploymentPackage.eNS_URI) instanceof SoftwaredeploymentPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(org.talend.cwm.softwaredeployment.SoftwaredeploymentPackage.eNS_URI) : org.talend.cwm.softwaredeployment.SoftwaredeploymentPackage.eINSTANCE);
        ConstantsPackageImpl theConstantsPackage = (ConstantsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(ConstantsPackage.eNS_URI) instanceof ConstantsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(ConstantsPackage.eNS_URI) : ConstantsPackage.eINSTANCE);

        // Create package meta-data objects
        theXmlPackage.createPackageContents();
        theRelationalPackage_1.createPackageContents();
        theSoftwaredeploymentPackage_1.createPackageContents();
        theConstantsPackage.createPackageContents();

        // Initialize created meta-data
        theXmlPackage.initializePackageContents();
        theRelationalPackage_1.initializePackageContents();
        theSoftwaredeploymentPackage_1.initializePackageContents();
        theConstantsPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theXmlPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(XmlPackage.eNS_URI, theXmlPackage);
        return theXmlPackage;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getTdXMLElement() {
        return tdXMLElementEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getTdXMLElement_XsdElementDeclaration() {
        return (EReference)tdXMLElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getTdXMLElement_OwnedDocument() {
        return (EReference)tdXMLElementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTdXMLElement_JavaType() {
        return (EAttribute)tdXMLElementEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getTdXMLElement_XmlContent() {
        return (EReference)tdXMLElementEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getTdXMLContent() {
        return tdXMLContentEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EReference getTdXMLContent_XmlElements() {
        return (EReference)tdXMLContentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EClass getTdXMLDocument() {
        return tdXMLDocumentEClass;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getTdXMLDocument_XsdFilePath() {
        return (EAttribute)tdXMLDocumentEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public XmlFactory getXmlFactory() {
        return (XmlFactory)getEFactoryInstance();
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
        tdXMLElementEClass = createEClass(TD_XML_ELEMENT);
        createEReference(tdXMLElementEClass, TD_XML_ELEMENT__XSD_ELEMENT_DECLARATION);
        createEReference(tdXMLElementEClass, TD_XML_ELEMENT__OWNED_DOCUMENT);
        createEAttribute(tdXMLElementEClass, TD_XML_ELEMENT__JAVA_TYPE);
        createEReference(tdXMLElementEClass, TD_XML_ELEMENT__XML_CONTENT);

        tdXMLContentEClass = createEClass(TD_XML_CONTENT);
        createEReference(tdXMLContentEClass, TD_XML_CONTENT__XML_ELEMENTS);

        tdXMLDocumentEClass = createEClass(TD_XML_DOCUMENT);
        createEAttribute(tdXMLDocumentEClass, TD_XML_DOCUMENT__XSD_FILE_PATH);
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
        orgomg.cwm.resource.xml.XmlPackage theXmlPackage_1 = (orgomg.cwm.resource.xml.XmlPackage)EPackage.Registry.INSTANCE.getEPackage(orgomg.cwm.resource.xml.XmlPackage.eNS_URI);
        CorePackage theCorePackage = (CorePackage)EPackage.Registry.INSTANCE.getEPackage(CorePackage.eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        tdXMLElementEClass.getESuperTypes().add(theXmlPackage_1.getElement());
        tdXMLContentEClass.getESuperTypes().add(theXmlPackage_1.getContent());
        tdXMLDocumentEClass.getESuperTypes().add(theXmlPackage_1.getDocument());

        // Initialize classes and features; add operations and parameters
        initEClass(tdXMLElementEClass, TdXMLElement.class, "TdXMLElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTdXMLElement_XsdElementDeclaration(), ecorePackage.getEObject(), null, "xsdElementDeclaration", null, 0, 1, TdXMLElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTdXMLElement_OwnedDocument(), this.getTdXMLDocument(), null, "ownedDocument", null, 0, 1, TdXMLElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getTdXMLElement_JavaType(), theCorePackage.getString(), "javaType", null, 0, 1, TdXMLElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getTdXMLElement_XmlContent(), this.getTdXMLContent(), null, "xmlContent", null, 0, 1, TdXMLElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(tdXMLContentEClass, TdXMLContent.class, "TdXMLContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getTdXMLContent_XmlElements(), this.getTdXMLElement(), null, "xmlElements", null, 0, -1, TdXMLContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(tdXMLDocumentEClass, TdXMLDocument.class, "TdXMLDocument", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getTdXMLDocument_XsdFilePath(), theCorePackage.getString(), "xsdFilePath", "", 0, 1, TdXMLDocument.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Create resource
        createResource(eNS_URI);
    }

} // XmlPackageImpl
