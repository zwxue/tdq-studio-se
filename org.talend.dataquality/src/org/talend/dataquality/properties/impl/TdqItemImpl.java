/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.properties.impl;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.talend.core.model.properties.impl.ItemImpl;
import org.talend.dataquality.properties.PropertiesPackage;
import org.talend.dataquality.properties.TdqItem;
import orgomg.cwm.analysis.businessnomenclature.BusinessnomenclaturePackage;
import orgomg.cwm.analysis.businessnomenclature.VocabularyElement;
import orgomg.cwm.analysis.informationvisualization.InformationvisualizationPackage;
import orgomg.cwm.analysis.informationvisualization.RenderedObject;
import orgomg.cwm.analysis.transformation.DataObjectSet;
import orgomg.cwm.analysis.transformation.TransformationPackage;
import orgomg.cwm.foundation.businessinformation.BusinessinformationPackage;
import orgomg.cwm.foundation.businessinformation.Description;
import orgomg.cwm.foundation.businessinformation.Document;
import orgomg.cwm.foundation.businessinformation.ResponsibleParty;
import orgomg.cwm.foundation.expressions.ElementNode;
import orgomg.cwm.foundation.expressions.ExpressionsPackage;
import orgomg.cwm.management.warehouseoperation.ChangeRequest;
import orgomg.cwm.management.warehouseoperation.Measurement;
import orgomg.cwm.management.warehouseoperation.WarehouseoperationPackage;
import orgomg.cwm.objectmodel.core.Constraint;
import orgomg.cwm.objectmodel.core.CorePackage;
import orgomg.cwm.objectmodel.core.Dependency;
import orgomg.cwm.objectmodel.core.Element;
import orgomg.cwm.objectmodel.core.ModelElement;
import orgomg.cwm.objectmodel.core.Namespace;
import orgomg.cwm.objectmodel.core.Stereotype;
import orgomg.cwm.objectmodel.core.TaggedValue;
import orgomg.cwm.objectmodel.core.VisibilityKind;
import orgomg.cwmx.resource.dmsii.DASDLProperty;
import orgomg.cwmx.resource.dmsii.DmsiiPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Tdq Item</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getVisibility <em>Visibility</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getClientDependency <em>Client Dependency</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getSupplierDependency <em>Supplier Dependency</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getConstraint <em>Constraint</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getNamespace <em>Namespace</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getImporter <em>Importer</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getStereotype <em>Stereotype</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getTaggedValue <em>Tagged Value</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getDocument <em>Document</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getDescriptions <em>Descriptions</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getResponsibleParty <em>Responsible Party</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getElementNode <em>Element Node</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getSet <em>Set</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getRenderedObject <em>Rendered Object</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getVocabularyElement <em>Vocabulary Element</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getMeasurement <em>Measurement</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getChangeRequest <em>Change Request</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getDasdlProperty <em>Dasdl Property</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqItemImpl#getFilename <em>Filename</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TdqItemImpl extends ItemImpl implements TdqItem {
    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getVisibility() <em>Visibility</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVisibility()
     * @generated
     * @ordered
     */
    protected static final VisibilityKind VISIBILITY_EDEFAULT = VisibilityKind.VK_PUBLIC;

    /**
     * The cached value of the '{@link #getVisibility() <em>Visibility</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVisibility()
     * @generated
     * @ordered
     */
    protected VisibilityKind visibility = VISIBILITY_EDEFAULT;

    /**
     * The cached value of the '{@link #getClientDependency() <em>Client Dependency</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getClientDependency()
     * @generated
     * @ordered
     */
    protected EList<Dependency> clientDependency;

    /**
     * The cached value of the '{@link #getSupplierDependency() <em>Supplier Dependency</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSupplierDependency()
     * @generated
     * @ordered
     */
    protected EList<Dependency> supplierDependency;

    /**
     * The cached value of the '{@link #getConstraint() <em>Constraint</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConstraint()
     * @generated
     * @ordered
     */
    protected EList<Constraint> constraint;

    /**
     * The cached value of the '{@link #getImporter() <em>Importer</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getImporter()
     * @generated
     * @ordered
     */
    protected EList<orgomg.cwm.objectmodel.core.Package> importer;

    /**
     * The cached value of the '{@link #getStereotype() <em>Stereotype</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStereotype()
     * @generated
     * @ordered
     */
    protected Stereotype stereotype;

    /**
     * The cached value of the '{@link #getTaggedValue() <em>Tagged Value</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getTaggedValue()
     * @generated
     * @ordered
     */
    protected EList<TaggedValue> taggedValue;

    /**
     * The cached value of the '{@link #getDocument() <em>Document</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDocument()
     * @generated
     * @ordered
     */
    protected EList<Document> document;

    /**
     * The cached value of the '{@link #getDescriptions() <em>Descriptions</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescriptions()
     * @generated
     * @ordered
     */
    protected EList<Description> descriptions;

    /**
     * The cached value of the '{@link #getResponsibleParty() <em>Responsible Party</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getResponsibleParty()
     * @generated
     * @ordered
     */
    protected EList<ResponsibleParty> responsibleParty;

    /**
     * The cached value of the '{@link #getElementNode() <em>Element Node</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getElementNode()
     * @generated
     * @ordered
     */
    protected EList<ElementNode> elementNode;

    /**
     * The cached value of the '{@link #getSet() <em>Set</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getSet()
     * @generated
     * @ordered
     */
    protected EList<DataObjectSet> set;

    /**
     * The cached value of the '{@link #getRenderedObject() <em>Rendered Object</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRenderedObject()
     * @generated
     * @ordered
     */
    protected EList<RenderedObject> renderedObject;

    /**
     * The cached value of the '{@link #getVocabularyElement() <em>Vocabulary Element</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVocabularyElement()
     * @generated
     * @ordered
     */
    protected EList<VocabularyElement> vocabularyElement;

    /**
     * The cached value of the '{@link #getMeasurement() <em>Measurement</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMeasurement()
     * @generated
     * @ordered
     */
    protected EList<Measurement> measurement;

    /**
     * The cached value of the '{@link #getChangeRequest() <em>Change Request</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getChangeRequest()
     * @generated
     * @ordered
     */
    protected EList<ChangeRequest> changeRequest;

    /**
     * The cached value of the '{@link #getDasdlProperty() <em>Dasdl Property</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDasdlProperty()
     * @generated
     * @ordered
     */
    protected EList<DASDLProperty> dasdlProperty;

    /**
     * The default value of the '{@link #getFilename() <em>Filename</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFilename()
     * @generated
     * @ordered
     */
    protected static final String FILENAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getFilename() <em>Filename</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFilename()
     * @generated
     * @ordered
     */
    protected String filename = FILENAME_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TdqItemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return PropertiesPackage.Literals.TDQ_ITEM;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_ITEM__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public VisibilityKind getVisibility() {
        return visibility;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setVisibility(VisibilityKind newVisibility) {
        VisibilityKind oldVisibility = visibility;
        visibility = newVisibility == null ? VISIBILITY_EDEFAULT : newVisibility;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_ITEM__VISIBILITY, oldVisibility, visibility));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Dependency> getClientDependency() {
        if (clientDependency == null) {
            clientDependency = new EObjectWithInverseResolvingEList.ManyInverse<Dependency>(Dependency.class, this, PropertiesPackage.TDQ_ITEM__CLIENT_DEPENDENCY, CorePackage.DEPENDENCY__CLIENT);
        }
        return clientDependency;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Dependency> getSupplierDependency() {
        if (supplierDependency == null) {
            supplierDependency = new EObjectWithInverseResolvingEList.ManyInverse<Dependency>(Dependency.class, this, PropertiesPackage.TDQ_ITEM__SUPPLIER_DEPENDENCY, CorePackage.DEPENDENCY__SUPPLIER);
        }
        return supplierDependency;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Constraint> getConstraint() {
        if (constraint == null) {
            constraint = new EObjectWithInverseResolvingEList.ManyInverse<Constraint>(Constraint.class, this, PropertiesPackage.TDQ_ITEM__CONSTRAINT, CorePackage.CONSTRAINT__CONSTRAINED_ELEMENT);
        }
        return constraint;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Namespace getNamespace() {
        if (eContainerFeatureID != PropertiesPackage.TDQ_ITEM__NAMESPACE) return null;
        return (Namespace)eContainer();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetNamespace(Namespace newNamespace, NotificationChain msgs) {
        msgs = eBasicSetContainer((InternalEObject)newNamespace, PropertiesPackage.TDQ_ITEM__NAMESPACE, msgs);
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNamespace(Namespace newNamespace) {
        if (newNamespace != eInternalContainer() || (eContainerFeatureID != PropertiesPackage.TDQ_ITEM__NAMESPACE && newNamespace != null)) {
            if (EcoreUtil.isAncestor(this, newNamespace))
                throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
            NotificationChain msgs = null;
            if (eInternalContainer() != null)
                msgs = eBasicRemoveFromContainer(msgs);
            if (newNamespace != null)
                msgs = ((InternalEObject)newNamespace).eInverseAdd(this, CorePackage.NAMESPACE__OWNED_ELEMENT, Namespace.class, msgs);
            msgs = basicSetNamespace(newNamespace, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_ITEM__NAMESPACE, newNamespace, newNamespace));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<orgomg.cwm.objectmodel.core.Package> getImporter() {
        if (importer == null) {
            importer = new EObjectWithInverseResolvingEList.ManyInverse<orgomg.cwm.objectmodel.core.Package>(orgomg.cwm.objectmodel.core.Package.class, this, PropertiesPackage.TDQ_ITEM__IMPORTER, CorePackage.PACKAGE__IMPORTED_ELEMENT);
        }
        return importer;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Stereotype getStereotype() {
        if (stereotype != null && stereotype.eIsProxy()) {
            InternalEObject oldStereotype = (InternalEObject)stereotype;
            stereotype = (Stereotype)eResolveProxy(oldStereotype);
            if (stereotype != oldStereotype) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, PropertiesPackage.TDQ_ITEM__STEREOTYPE, oldStereotype, stereotype));
            }
        }
        return stereotype;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Stereotype basicGetStereotype() {
        return stereotype;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetStereotype(Stereotype newStereotype, NotificationChain msgs) {
        Stereotype oldStereotype = stereotype;
        stereotype = newStereotype;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_ITEM__STEREOTYPE, oldStereotype, newStereotype);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setStereotype(Stereotype newStereotype) {
        if (newStereotype != stereotype) {
            NotificationChain msgs = null;
            if (stereotype != null)
                msgs = ((InternalEObject)stereotype).eInverseRemove(this, CorePackage.STEREOTYPE__EXTENDED_ELEMENT, Stereotype.class, msgs);
            if (newStereotype != null)
                msgs = ((InternalEObject)newStereotype).eInverseAdd(this, CorePackage.STEREOTYPE__EXTENDED_ELEMENT, Stereotype.class, msgs);
            msgs = basicSetStereotype(newStereotype, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_ITEM__STEREOTYPE, newStereotype, newStereotype));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<TaggedValue> getTaggedValue() {
        if (taggedValue == null) {
            taggedValue = new EObjectContainmentWithInverseEList<TaggedValue>(TaggedValue.class, this, PropertiesPackage.TDQ_ITEM__TAGGED_VALUE, CorePackage.TAGGED_VALUE__MODEL_ELEMENT);
        }
        return taggedValue;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Document> getDocument() {
        if (document == null) {
            document = new EObjectWithInverseResolvingEList.ManyInverse<Document>(Document.class, this, PropertiesPackage.TDQ_ITEM__DOCUMENT, BusinessinformationPackage.DOCUMENT__MODEL_ELEMENT);
        }
        return document;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Description> getDescriptions() {
        if (descriptions == null) {
            descriptions = new EObjectWithInverseResolvingEList.ManyInverse<Description>(Description.class, this, PropertiesPackage.TDQ_ITEM__DESCRIPTIONS, BusinessinformationPackage.DESCRIPTION__MODEL_ELEMENT);
        }
        return descriptions;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<ResponsibleParty> getResponsibleParty() {
        if (responsibleParty == null) {
            responsibleParty = new EObjectWithInverseResolvingEList.ManyInverse<ResponsibleParty>(ResponsibleParty.class, this, PropertiesPackage.TDQ_ITEM__RESPONSIBLE_PARTY, BusinessinformationPackage.RESPONSIBLE_PARTY__MODEL_ELEMENT);
        }
        return responsibleParty;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<ElementNode> getElementNode() {
        if (elementNode == null) {
            elementNode = new EObjectWithInverseResolvingEList<ElementNode>(ElementNode.class, this, PropertiesPackage.TDQ_ITEM__ELEMENT_NODE, ExpressionsPackage.ELEMENT_NODE__MODEL_ELEMENT);
        }
        return elementNode;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<DataObjectSet> getSet() {
        if (set == null) {
            set = new EObjectWithInverseResolvingEList.ManyInverse<DataObjectSet>(DataObjectSet.class, this, PropertiesPackage.TDQ_ITEM__SET, TransformationPackage.DATA_OBJECT_SET__ELEMENT);
        }
        return set;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<RenderedObject> getRenderedObject() {
        if (renderedObject == null) {
            renderedObject = new EObjectWithInverseResolvingEList<RenderedObject>(RenderedObject.class, this, PropertiesPackage.TDQ_ITEM__RENDERED_OBJECT, InformationvisualizationPackage.RENDERED_OBJECT__MODEL_ELEMENT);
        }
        return renderedObject;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<VocabularyElement> getVocabularyElement() {
        if (vocabularyElement == null) {
            vocabularyElement = new EObjectWithInverseResolvingEList.ManyInverse<VocabularyElement>(VocabularyElement.class, this, PropertiesPackage.TDQ_ITEM__VOCABULARY_ELEMENT, BusinessnomenclaturePackage.VOCABULARY_ELEMENT__MODEL_ELEMENT);
        }
        return vocabularyElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Measurement> getMeasurement() {
        if (measurement == null) {
            measurement = new EObjectWithInverseResolvingEList<Measurement>(Measurement.class, this, PropertiesPackage.TDQ_ITEM__MEASUREMENT, WarehouseoperationPackage.MEASUREMENT__MODEL_ELEMENT);
        }
        return measurement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<ChangeRequest> getChangeRequest() {
        if (changeRequest == null) {
            changeRequest = new EObjectWithInverseResolvingEList.ManyInverse<ChangeRequest>(ChangeRequest.class, this, PropertiesPackage.TDQ_ITEM__CHANGE_REQUEST, WarehouseoperationPackage.CHANGE_REQUEST__MODEL_ELEMENT);
        }
        return changeRequest;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<DASDLProperty> getDasdlProperty() {
        if (dasdlProperty == null) {
            dasdlProperty = new EObjectWithInverseResolvingEList<DASDLProperty>(DASDLProperty.class, this, PropertiesPackage.TDQ_ITEM__DASDL_PROPERTY, DmsiiPackage.DASDL_PROPERTY__OWNER);
        }
        return dasdlProperty;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getFilename() {
        return filename;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFilename(String newFilename) {
        String oldFilename = filename;
        filename = newFilename;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_ITEM__FILENAME, oldFilename, filename));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case PropertiesPackage.TDQ_ITEM__CLIENT_DEPENDENCY:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getClientDependency()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__SUPPLIER_DEPENDENCY:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getSupplierDependency()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__CONSTRAINT:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getConstraint()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__NAMESPACE:
                if (eInternalContainer() != null)
                    msgs = eBasicRemoveFromContainer(msgs);
                return basicSetNamespace((Namespace)otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__IMPORTER:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getImporter()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__STEREOTYPE:
                if (stereotype != null)
                    msgs = ((InternalEObject)stereotype).eInverseRemove(this, CorePackage.STEREOTYPE__EXTENDED_ELEMENT, Stereotype.class, msgs);
                return basicSetStereotype((Stereotype)otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__TAGGED_VALUE:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getTaggedValue()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__DOCUMENT:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getDocument()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__DESCRIPTIONS:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getDescriptions()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__RESPONSIBLE_PARTY:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getResponsibleParty()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__ELEMENT_NODE:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getElementNode()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__SET:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getSet()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__RENDERED_OBJECT:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getRenderedObject()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__VOCABULARY_ELEMENT:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getVocabularyElement()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__MEASUREMENT:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getMeasurement()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__CHANGE_REQUEST:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getChangeRequest()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__DASDL_PROPERTY:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getDasdlProperty()).basicAdd(otherEnd, msgs);
        }
        return super.eInverseAdd(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case PropertiesPackage.TDQ_ITEM__CLIENT_DEPENDENCY:
                return ((InternalEList<?>)getClientDependency()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__SUPPLIER_DEPENDENCY:
                return ((InternalEList<?>)getSupplierDependency()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__CONSTRAINT:
                return ((InternalEList<?>)getConstraint()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__NAMESPACE:
                return basicSetNamespace(null, msgs);
            case PropertiesPackage.TDQ_ITEM__IMPORTER:
                return ((InternalEList<?>)getImporter()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__STEREOTYPE:
                return basicSetStereotype(null, msgs);
            case PropertiesPackage.TDQ_ITEM__TAGGED_VALUE:
                return ((InternalEList<?>)getTaggedValue()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__DOCUMENT:
                return ((InternalEList<?>)getDocument()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__DESCRIPTIONS:
                return ((InternalEList<?>)getDescriptions()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__RESPONSIBLE_PARTY:
                return ((InternalEList<?>)getResponsibleParty()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__ELEMENT_NODE:
                return ((InternalEList<?>)getElementNode()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__SET:
                return ((InternalEList<?>)getSet()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__RENDERED_OBJECT:
                return ((InternalEList<?>)getRenderedObject()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__VOCABULARY_ELEMENT:
                return ((InternalEList<?>)getVocabularyElement()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__MEASUREMENT:
                return ((InternalEList<?>)getMeasurement()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__CHANGE_REQUEST:
                return ((InternalEList<?>)getChangeRequest()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.TDQ_ITEM__DASDL_PROPERTY:
                return ((InternalEList<?>)getDasdlProperty()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
        switch (eContainerFeatureID) {
            case PropertiesPackage.TDQ_ITEM__NAMESPACE:
                return eInternalContainer().eInverseRemove(this, CorePackage.NAMESPACE__OWNED_ELEMENT, Namespace.class, msgs);
        }
        return super.eBasicRemoveFromContainerFeature(msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case PropertiesPackage.TDQ_ITEM__NAME:
                return getName();
            case PropertiesPackage.TDQ_ITEM__VISIBILITY:
                return getVisibility();
            case PropertiesPackage.TDQ_ITEM__CLIENT_DEPENDENCY:
                return getClientDependency();
            case PropertiesPackage.TDQ_ITEM__SUPPLIER_DEPENDENCY:
                return getSupplierDependency();
            case PropertiesPackage.TDQ_ITEM__CONSTRAINT:
                return getConstraint();
            case PropertiesPackage.TDQ_ITEM__NAMESPACE:
                return getNamespace();
            case PropertiesPackage.TDQ_ITEM__IMPORTER:
                return getImporter();
            case PropertiesPackage.TDQ_ITEM__STEREOTYPE:
                if (resolve) return getStereotype();
                return basicGetStereotype();
            case PropertiesPackage.TDQ_ITEM__TAGGED_VALUE:
                return getTaggedValue();
            case PropertiesPackage.TDQ_ITEM__DOCUMENT:
                return getDocument();
            case PropertiesPackage.TDQ_ITEM__DESCRIPTIONS:
                return getDescriptions();
            case PropertiesPackage.TDQ_ITEM__RESPONSIBLE_PARTY:
                return getResponsibleParty();
            case PropertiesPackage.TDQ_ITEM__ELEMENT_NODE:
                return getElementNode();
            case PropertiesPackage.TDQ_ITEM__SET:
                return getSet();
            case PropertiesPackage.TDQ_ITEM__RENDERED_OBJECT:
                return getRenderedObject();
            case PropertiesPackage.TDQ_ITEM__VOCABULARY_ELEMENT:
                return getVocabularyElement();
            case PropertiesPackage.TDQ_ITEM__MEASUREMENT:
                return getMeasurement();
            case PropertiesPackage.TDQ_ITEM__CHANGE_REQUEST:
                return getChangeRequest();
            case PropertiesPackage.TDQ_ITEM__DASDL_PROPERTY:
                return getDasdlProperty();
            case PropertiesPackage.TDQ_ITEM__FILENAME:
                return getFilename();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case PropertiesPackage.TDQ_ITEM__NAME:
                setName((String)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__VISIBILITY:
                setVisibility((VisibilityKind)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__CLIENT_DEPENDENCY:
                getClientDependency().clear();
                getClientDependency().addAll((Collection<? extends Dependency>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__SUPPLIER_DEPENDENCY:
                getSupplierDependency().clear();
                getSupplierDependency().addAll((Collection<? extends Dependency>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__CONSTRAINT:
                getConstraint().clear();
                getConstraint().addAll((Collection<? extends Constraint>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__NAMESPACE:
                setNamespace((Namespace)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__IMPORTER:
                getImporter().clear();
                getImporter().addAll((Collection<? extends orgomg.cwm.objectmodel.core.Package>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__STEREOTYPE:
                setStereotype((Stereotype)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__TAGGED_VALUE:
                getTaggedValue().clear();
                getTaggedValue().addAll((Collection<? extends TaggedValue>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__DOCUMENT:
                getDocument().clear();
                getDocument().addAll((Collection<? extends Document>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__DESCRIPTIONS:
                getDescriptions().clear();
                getDescriptions().addAll((Collection<? extends Description>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__RESPONSIBLE_PARTY:
                getResponsibleParty().clear();
                getResponsibleParty().addAll((Collection<? extends ResponsibleParty>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__ELEMENT_NODE:
                getElementNode().clear();
                getElementNode().addAll((Collection<? extends ElementNode>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__SET:
                getSet().clear();
                getSet().addAll((Collection<? extends DataObjectSet>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__RENDERED_OBJECT:
                getRenderedObject().clear();
                getRenderedObject().addAll((Collection<? extends RenderedObject>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__VOCABULARY_ELEMENT:
                getVocabularyElement().clear();
                getVocabularyElement().addAll((Collection<? extends VocabularyElement>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__MEASUREMENT:
                getMeasurement().clear();
                getMeasurement().addAll((Collection<? extends Measurement>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__CHANGE_REQUEST:
                getChangeRequest().clear();
                getChangeRequest().addAll((Collection<? extends ChangeRequest>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__DASDL_PROPERTY:
                getDasdlProperty().clear();
                getDasdlProperty().addAll((Collection<? extends DASDLProperty>)newValue);
                return;
            case PropertiesPackage.TDQ_ITEM__FILENAME:
                setFilename((String)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case PropertiesPackage.TDQ_ITEM__NAME:
                setName(NAME_EDEFAULT);
                return;
            case PropertiesPackage.TDQ_ITEM__VISIBILITY:
                setVisibility(VISIBILITY_EDEFAULT);
                return;
            case PropertiesPackage.TDQ_ITEM__CLIENT_DEPENDENCY:
                getClientDependency().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__SUPPLIER_DEPENDENCY:
                getSupplierDependency().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__CONSTRAINT:
                getConstraint().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__NAMESPACE:
                setNamespace((Namespace)null);
                return;
            case PropertiesPackage.TDQ_ITEM__IMPORTER:
                getImporter().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__STEREOTYPE:
                setStereotype((Stereotype)null);
                return;
            case PropertiesPackage.TDQ_ITEM__TAGGED_VALUE:
                getTaggedValue().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__DOCUMENT:
                getDocument().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__DESCRIPTIONS:
                getDescriptions().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__RESPONSIBLE_PARTY:
                getResponsibleParty().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__ELEMENT_NODE:
                getElementNode().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__SET:
                getSet().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__RENDERED_OBJECT:
                getRenderedObject().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__VOCABULARY_ELEMENT:
                getVocabularyElement().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__MEASUREMENT:
                getMeasurement().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__CHANGE_REQUEST:
                getChangeRequest().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__DASDL_PROPERTY:
                getDasdlProperty().clear();
                return;
            case PropertiesPackage.TDQ_ITEM__FILENAME:
                setFilename(FILENAME_EDEFAULT);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case PropertiesPackage.TDQ_ITEM__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case PropertiesPackage.TDQ_ITEM__VISIBILITY:
                return visibility != VISIBILITY_EDEFAULT;
            case PropertiesPackage.TDQ_ITEM__CLIENT_DEPENDENCY:
                return clientDependency != null && !clientDependency.isEmpty();
            case PropertiesPackage.TDQ_ITEM__SUPPLIER_DEPENDENCY:
                return supplierDependency != null && !supplierDependency.isEmpty();
            case PropertiesPackage.TDQ_ITEM__CONSTRAINT:
                return constraint != null && !constraint.isEmpty();
            case PropertiesPackage.TDQ_ITEM__NAMESPACE:
                return getNamespace() != null;
            case PropertiesPackage.TDQ_ITEM__IMPORTER:
                return importer != null && !importer.isEmpty();
            case PropertiesPackage.TDQ_ITEM__STEREOTYPE:
                return stereotype != null;
            case PropertiesPackage.TDQ_ITEM__TAGGED_VALUE:
                return taggedValue != null && !taggedValue.isEmpty();
            case PropertiesPackage.TDQ_ITEM__DOCUMENT:
                return document != null && !document.isEmpty();
            case PropertiesPackage.TDQ_ITEM__DESCRIPTIONS:
                return descriptions != null && !descriptions.isEmpty();
            case PropertiesPackage.TDQ_ITEM__RESPONSIBLE_PARTY:
                return responsibleParty != null && !responsibleParty.isEmpty();
            case PropertiesPackage.TDQ_ITEM__ELEMENT_NODE:
                return elementNode != null && !elementNode.isEmpty();
            case PropertiesPackage.TDQ_ITEM__SET:
                return set != null && !set.isEmpty();
            case PropertiesPackage.TDQ_ITEM__RENDERED_OBJECT:
                return renderedObject != null && !renderedObject.isEmpty();
            case PropertiesPackage.TDQ_ITEM__VOCABULARY_ELEMENT:
                return vocabularyElement != null && !vocabularyElement.isEmpty();
            case PropertiesPackage.TDQ_ITEM__MEASUREMENT:
                return measurement != null && !measurement.isEmpty();
            case PropertiesPackage.TDQ_ITEM__CHANGE_REQUEST:
                return changeRequest != null && !changeRequest.isEmpty();
            case PropertiesPackage.TDQ_ITEM__DASDL_PROPERTY:
                return dasdlProperty != null && !dasdlProperty.isEmpty();
            case PropertiesPackage.TDQ_ITEM__FILENAME:
                return FILENAME_EDEFAULT == null ? filename != null : !FILENAME_EDEFAULT.equals(filename);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
        if (baseClass == Element.class) {
            switch (derivedFeatureID) {
                default: return -1;
            }
        }
        if (baseClass == ModelElement.class) {
            switch (derivedFeatureID) {
                case PropertiesPackage.TDQ_ITEM__NAME: return CorePackage.MODEL_ELEMENT__NAME;
                case PropertiesPackage.TDQ_ITEM__VISIBILITY: return CorePackage.MODEL_ELEMENT__VISIBILITY;
                case PropertiesPackage.TDQ_ITEM__CLIENT_DEPENDENCY: return CorePackage.MODEL_ELEMENT__CLIENT_DEPENDENCY;
                case PropertiesPackage.TDQ_ITEM__SUPPLIER_DEPENDENCY: return CorePackage.MODEL_ELEMENT__SUPPLIER_DEPENDENCY;
                case PropertiesPackage.TDQ_ITEM__CONSTRAINT: return CorePackage.MODEL_ELEMENT__CONSTRAINT;
                case PropertiesPackage.TDQ_ITEM__NAMESPACE: return CorePackage.MODEL_ELEMENT__NAMESPACE;
                case PropertiesPackage.TDQ_ITEM__IMPORTER: return CorePackage.MODEL_ELEMENT__IMPORTER;
                case PropertiesPackage.TDQ_ITEM__STEREOTYPE: return CorePackage.MODEL_ELEMENT__STEREOTYPE;
                case PropertiesPackage.TDQ_ITEM__TAGGED_VALUE: return CorePackage.MODEL_ELEMENT__TAGGED_VALUE;
                case PropertiesPackage.TDQ_ITEM__DOCUMENT: return CorePackage.MODEL_ELEMENT__DOCUMENT;
                case PropertiesPackage.TDQ_ITEM__DESCRIPTIONS: return CorePackage.MODEL_ELEMENT__DESCRIPTIONS;
                case PropertiesPackage.TDQ_ITEM__RESPONSIBLE_PARTY: return CorePackage.MODEL_ELEMENT__RESPONSIBLE_PARTY;
                case PropertiesPackage.TDQ_ITEM__ELEMENT_NODE: return CorePackage.MODEL_ELEMENT__ELEMENT_NODE;
                case PropertiesPackage.TDQ_ITEM__SET: return CorePackage.MODEL_ELEMENT__SET;
                case PropertiesPackage.TDQ_ITEM__RENDERED_OBJECT: return CorePackage.MODEL_ELEMENT__RENDERED_OBJECT;
                case PropertiesPackage.TDQ_ITEM__VOCABULARY_ELEMENT: return CorePackage.MODEL_ELEMENT__VOCABULARY_ELEMENT;
                case PropertiesPackage.TDQ_ITEM__MEASUREMENT: return CorePackage.MODEL_ELEMENT__MEASUREMENT;
                case PropertiesPackage.TDQ_ITEM__CHANGE_REQUEST: return CorePackage.MODEL_ELEMENT__CHANGE_REQUEST;
                case PropertiesPackage.TDQ_ITEM__DASDL_PROPERTY: return CorePackage.MODEL_ELEMENT__DASDL_PROPERTY;
                default: return -1;
            }
        }
        return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
        if (baseClass == Element.class) {
            switch (baseFeatureID) {
                default: return -1;
            }
        }
        if (baseClass == ModelElement.class) {
            switch (baseFeatureID) {
                case CorePackage.MODEL_ELEMENT__NAME: return PropertiesPackage.TDQ_ITEM__NAME;
                case CorePackage.MODEL_ELEMENT__VISIBILITY: return PropertiesPackage.TDQ_ITEM__VISIBILITY;
                case CorePackage.MODEL_ELEMENT__CLIENT_DEPENDENCY: return PropertiesPackage.TDQ_ITEM__CLIENT_DEPENDENCY;
                case CorePackage.MODEL_ELEMENT__SUPPLIER_DEPENDENCY: return PropertiesPackage.TDQ_ITEM__SUPPLIER_DEPENDENCY;
                case CorePackage.MODEL_ELEMENT__CONSTRAINT: return PropertiesPackage.TDQ_ITEM__CONSTRAINT;
                case CorePackage.MODEL_ELEMENT__NAMESPACE: return PropertiesPackage.TDQ_ITEM__NAMESPACE;
                case CorePackage.MODEL_ELEMENT__IMPORTER: return PropertiesPackage.TDQ_ITEM__IMPORTER;
                case CorePackage.MODEL_ELEMENT__STEREOTYPE: return PropertiesPackage.TDQ_ITEM__STEREOTYPE;
                case CorePackage.MODEL_ELEMENT__TAGGED_VALUE: return PropertiesPackage.TDQ_ITEM__TAGGED_VALUE;
                case CorePackage.MODEL_ELEMENT__DOCUMENT: return PropertiesPackage.TDQ_ITEM__DOCUMENT;
                case CorePackage.MODEL_ELEMENT__DESCRIPTIONS: return PropertiesPackage.TDQ_ITEM__DESCRIPTIONS;
                case CorePackage.MODEL_ELEMENT__RESPONSIBLE_PARTY: return PropertiesPackage.TDQ_ITEM__RESPONSIBLE_PARTY;
                case CorePackage.MODEL_ELEMENT__ELEMENT_NODE: return PropertiesPackage.TDQ_ITEM__ELEMENT_NODE;
                case CorePackage.MODEL_ELEMENT__SET: return PropertiesPackage.TDQ_ITEM__SET;
                case CorePackage.MODEL_ELEMENT__RENDERED_OBJECT: return PropertiesPackage.TDQ_ITEM__RENDERED_OBJECT;
                case CorePackage.MODEL_ELEMENT__VOCABULARY_ELEMENT: return PropertiesPackage.TDQ_ITEM__VOCABULARY_ELEMENT;
                case CorePackage.MODEL_ELEMENT__MEASUREMENT: return PropertiesPackage.TDQ_ITEM__MEASUREMENT;
                case CorePackage.MODEL_ELEMENT__CHANGE_REQUEST: return PropertiesPackage.TDQ_ITEM__CHANGE_REQUEST;
                case CorePackage.MODEL_ELEMENT__DASDL_PROPERTY: return PropertiesPackage.TDQ_ITEM__DASDL_PROPERTY;
                default: return -1;
            }
        }
        return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (name: ");
        result.append(name);
        result.append(", visibility: ");
        result.append(visibility);
        result.append(", filename: ");
        result.append(filename);
        result.append(')');
        return result.toString();
    }

} //TdqItemImpl
