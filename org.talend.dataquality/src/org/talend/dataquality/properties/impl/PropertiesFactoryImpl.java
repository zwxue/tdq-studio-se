/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.properties.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.talend.dataquality.properties.*;
import org.talend.dataquality.properties.PropertiesFactory;
import org.talend.dataquality.properties.PropertiesPackage;
import org.talend.dataquality.properties.TdqItem;
import org.talend.dataquality.properties.TdqItemState;
import org.talend.dataquality.properties.TdqProperty;
import org.talend.dataquality.properties.TdqUser;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class PropertiesFactoryImpl extends EFactoryImpl implements PropertiesFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static PropertiesFactory init() {
        try {
            PropertiesFactory thePropertiesFactory = (PropertiesFactory)EPackage.Registry.INSTANCE.getEFactory("http://dataquality.properties"); 
            if (thePropertiesFactory != null) {
                return thePropertiesFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new PropertiesFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public PropertiesFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case PropertiesPackage.TDQ_PROPERTY: return createTdqProperty();
            case PropertiesPackage.TDQ_ITEM: return createTdqItem();
            case PropertiesPackage.TDQ_USER: return createTdqUser();
            case PropertiesPackage.TDQ_ITEM_STATE: return createTdqItemState();
            case PropertiesPackage.MOCK_MODEL_ELEMENT: return createMockModelElement();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TdqProperty createTdqProperty() {
        TdqPropertyImpl tdqProperty = new TdqPropertyImpl();
        return tdqProperty;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TdqItem createTdqItem() {
        TdqItemImpl tdqItem = new TdqItemImpl();
        return tdqItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TdqUser createTdqUser() {
        TdqUserImpl tdqUser = new TdqUserImpl();
        return tdqUser;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TdqItemState createTdqItemState() {
        TdqItemStateImpl tdqItemState = new TdqItemStateImpl();
        return tdqItemState;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public MockModelElement createMockModelElement() {
        MockModelElementImpl mockModelElement = new MockModelElementImpl();
        return mockModelElement;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public PropertiesPackage getPropertiesPackage() {
        return (PropertiesPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static PropertiesPackage getPackage() {
        return PropertiesPackage.eINSTANCE;
    }

} //PropertiesFactoryImpl
