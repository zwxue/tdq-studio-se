/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.cwm.softwaredeployment.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.talend.cwm.softwaredeployment.SoftwaredeploymentPackage;
import org.talend.cwm.softwaredeployment.TdProviderConnection;

import orgomg.cwm.foundation.softwaredeployment.impl.ProviderConnectionImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Td Provider Connection</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.cwm.softwaredeployment.impl.TdProviderConnectionImpl#getLogin <em>Login</em>}</li>
 *   <li>{@link org.talend.cwm.softwaredeployment.impl.TdProviderConnectionImpl#getPassword <em>Password</em>}</li>
 *   <li>{@link org.talend.cwm.softwaredeployment.impl.TdProviderConnectionImpl#getConnectionString <em>Connection String</em>}</li>
 *   <li>{@link org.talend.cwm.softwaredeployment.impl.TdProviderConnectionImpl#getDriverClassName <em>Driver Class Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TdProviderConnectionImpl extends ProviderConnectionImpl implements TdProviderConnection {
    /**
     * The default value of the '{@link #getLogin() <em>Login</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLogin()
     * @generated
     * @ordered
     */
    protected static final String LOGIN_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLogin() <em>Login</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLogin()
     * @generated
     * @ordered
     */
    protected String login = LOGIN_EDEFAULT;

    /**
     * The default value of the '{@link #getPassword() <em>Password</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPassword()
     * @generated
     * @ordered
     */
    protected static final String PASSWORD_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPassword() <em>Password</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPassword()
     * @generated
     * @ordered
     */
    protected String password = PASSWORD_EDEFAULT;

    /**
     * The default value of the '{@link #getConnectionString() <em>Connection String</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConnectionString()
     * @generated
     * @ordered
     */
    protected static final String CONNECTION_STRING_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getConnectionString() <em>Connection String</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getConnectionString()
     * @generated
     * @ordered
     */
    protected String connectionString = CONNECTION_STRING_EDEFAULT;

    /**
     * The default value of the '{@link #getDriverClassName() <em>Driver Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDriverClassName()
     * @generated
     * @ordered
     */
    protected static final String DRIVER_CLASS_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDriverClassName() <em>Driver Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDriverClassName()
     * @generated
     * @ordered
     */
    protected String driverClassName = DRIVER_CLASS_NAME_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TdProviderConnectionImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return SoftwaredeploymentPackage.Literals.TD_PROVIDER_CONNECTION;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getLogin() {
        return login;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLogin(String newLogin) {
        String oldLogin = login;
        login = newLogin;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__LOGIN, oldLogin, login));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getPassword() {
        return password;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPassword(String newPassword) {
        String oldPassword = password;
        password = newPassword;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__PASSWORD, oldPassword, password));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getConnectionString() {
        return connectionString;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setConnectionString(String newConnectionString) {
        String oldConnectionString = connectionString;
        connectionString = newConnectionString;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__CONNECTION_STRING, oldConnectionString, connectionString));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDriverClassName() {
        return driverClassName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDriverClassName(String newDriverClassName) {
        String oldDriverClassName = driverClassName;
        driverClassName = newDriverClassName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__DRIVER_CLASS_NAME, oldDriverClassName, driverClassName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__LOGIN:
                return getLogin();
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__PASSWORD:
                return getPassword();
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__CONNECTION_STRING:
                return getConnectionString();
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__DRIVER_CLASS_NAME:
                return getDriverClassName();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__LOGIN:
                setLogin((String)newValue);
                return;
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__PASSWORD:
                setPassword((String)newValue);
                return;
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__CONNECTION_STRING:
                setConnectionString((String)newValue);
                return;
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__DRIVER_CLASS_NAME:
                setDriverClassName((String)newValue);
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
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__LOGIN:
                setLogin(LOGIN_EDEFAULT);
                return;
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__PASSWORD:
                setPassword(PASSWORD_EDEFAULT);
                return;
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__CONNECTION_STRING:
                setConnectionString(CONNECTION_STRING_EDEFAULT);
                return;
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__DRIVER_CLASS_NAME:
                setDriverClassName(DRIVER_CLASS_NAME_EDEFAULT);
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
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__LOGIN:
                return LOGIN_EDEFAULT == null ? login != null : !LOGIN_EDEFAULT.equals(login);
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__PASSWORD:
                return PASSWORD_EDEFAULT == null ? password != null : !PASSWORD_EDEFAULT.equals(password);
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__CONNECTION_STRING:
                return CONNECTION_STRING_EDEFAULT == null ? connectionString != null : !CONNECTION_STRING_EDEFAULT.equals(connectionString);
            case SoftwaredeploymentPackage.TD_PROVIDER_CONNECTION__DRIVER_CLASS_NAME:
                return DRIVER_CLASS_NAME_EDEFAULT == null ? driverClassName != null : !DRIVER_CLASS_NAME_EDEFAULT.equals(driverClassName);
        }
        return super.eIsSet(featureID);
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
        result.append(" (login: ");
        result.append(login);
        result.append(", password: ");
        result.append(password);
        result.append(", connectionString: ");
        result.append(connectionString);
        result.append(", driverClassName: ");
        result.append(driverClassName);
        result.append(')');
        return result.toString();
    }

} //TdProviderConnectionImpl
