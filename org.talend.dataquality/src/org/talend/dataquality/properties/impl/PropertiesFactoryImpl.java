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
import org.talend.dataquality.properties.IMockModelElement;
import org.talend.dataquality.properties.ITDQItem;
import org.talend.dataquality.properties.ITDQItemState;
import org.talend.dataquality.properties.ITDQProperty;
import org.talend.dataquality.properties.ITDQUser;
import org.talend.dataquality.properties.PropertiesFactory;
import org.talend.dataquality.properties.PropertiesPackage;

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
            case PropertiesPackage.ITDQ_PROPERTY: return createITDQProperty();
            case PropertiesPackage.ITDQ_ITEM: return createITDQItem();
            case PropertiesPackage.ITDQ_USER: return createITDQUser();
            case PropertiesPackage.ITDQ_ITEM_STATE: return createITDQItemState();
            case PropertiesPackage.IMOCK_MODEL_ELEMENT: return createIMockModelElement();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ITDQProperty createITDQProperty() {
        ITDQPropertyImpl itdqProperty = new ITDQPropertyImpl();
        return itdqProperty;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ITDQItem createITDQItem() {
        ITDQItemImpl itdqItem = new ITDQItemImpl();
        return itdqItem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ITDQUser createITDQUser() {
        ITDQUserImpl itdqUser = new ITDQUserImpl();
        return itdqUser;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ITDQItemState createITDQItemState() {
        ITDQItemStateImpl itdqItemState = new ITDQItemStateImpl();
        return itdqItemState;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public IMockModelElement createIMockModelElement() {
        IMockModelElementImpl iMockModelElement = new IMockModelElementImpl();
        return iMockModelElement;
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
