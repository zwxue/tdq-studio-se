/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.cwm.softwaredeployment.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.talend.cwm.softwaredeployment.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SoftwaredeploymentFactoryImpl extends EFactoryImpl implements SoftwaredeploymentFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static SoftwaredeploymentFactory init() {
        try {
            SoftwaredeploymentFactory theSoftwaredeploymentFactory = (SoftwaredeploymentFactory)EPackage.Registry.INSTANCE.getEFactory("http:///org.talend/cwm/foundation.softwaredeployment"); 
            if (theSoftwaredeploymentFactory != null) {
                return theSoftwaredeploymentFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new SoftwaredeploymentFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SoftwaredeploymentFactoryImpl() {
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
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION: return createTdProviderConnection();
            case SoftwaredeploymentPackage.TD_DATA_MANAGER: return createTdDataManager();
            case SoftwaredeploymentPackage.TD_DATA_PROVIDER: return createTdDataProvider();
            case SoftwaredeploymentPackage.TD_SOFTWARE_SYSTEM: return createTdSoftwareSystem();
            case SoftwaredeploymentPackage.TD_MACHINE: return createTdMachine();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TdProviderConnection createTdProviderConnection() {
        TdProviderConnectionImpl tdProviderConnection = new TdProviderConnectionImpl();
        return tdProviderConnection;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TdDataManager createTdDataManager() {
        TdDataManagerImpl tdDataManager = new TdDataManagerImpl();
        return tdDataManager;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TdDataProvider createTdDataProvider() {
        TdDataProviderImpl tdDataProvider = new TdDataProviderImpl();
        return tdDataProvider;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TdSoftwareSystem createTdSoftwareSystem() {
        TdSoftwareSystemImpl tdSoftwareSystem = new TdSoftwareSystemImpl();
        return tdSoftwareSystem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public TdMachine createTdMachine() {
        TdMachineImpl tdMachine = new TdMachineImpl();
        return tdMachine;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SoftwaredeploymentPackage getSoftwaredeploymentPackage() {
        return (SoftwaredeploymentPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static SoftwaredeploymentPackage getPackage() {
        return SoftwaredeploymentPackage.eINSTANCE;
    }

} //SoftwaredeploymentFactoryImpl
