/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.cwm.softwaredeployment;

import orgomg.cwm.foundation.softwaredeployment.ProviderConnection;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Td Provider Connection</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.cwm.softwaredeployment.TdProviderConnection#getLogin <em>Login</em>}</li>
 *   <li>{@link org.talend.cwm.softwaredeployment.TdProviderConnection#getPassword <em>Password</em>}</li>
 *   <li>{@link org.talend.cwm.softwaredeployment.TdProviderConnection#getConnectionString <em>Connection String</em>}</li>
 *   <li>{@link org.talend.cwm.softwaredeployment.TdProviderConnection#getDriverClassName <em>Driver Class Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.cwm.softwaredeployment.SoftwaredeploymentPackage#getTdProviderConnection()
 * @model
 * @generated
 */
public interface TdProviderConnection extends ProviderConnection {
    /**
     * Returns the value of the '<em><b>Login</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Login</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Login</em>' attribute.
     * @see #setLogin(String)
     * @see org.talend.cwm.softwaredeployment.SoftwaredeploymentPackage#getTdProviderConnection_Login()
     * @model
     * @generated
     */
    String getLogin();

    /**
     * Sets the value of the '{@link org.talend.cwm.softwaredeployment.TdProviderConnection#getLogin <em>Login</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Login</em>' attribute.
     * @see #getLogin()
     * @generated
     */
    void setLogin(String value);

    /**
     * Returns the value of the '<em><b>Password</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Password</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Password</em>' attribute.
     * @see #setPassword(String)
     * @see org.talend.cwm.softwaredeployment.SoftwaredeploymentPackage#getTdProviderConnection_Password()
     * @model
     * @generated
     */
    String getPassword();

    /**
     * Sets the value of the '{@link org.talend.cwm.softwaredeployment.TdProviderConnection#getPassword <em>Password</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Password</em>' attribute.
     * @see #getPassword()
     * @generated
     */
    void setPassword(String value);

    /**
     * Returns the value of the '<em><b>Connection String</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Connection String</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Connection String</em>' attribute.
     * @see #setConnectionString(String)
     * @see org.talend.cwm.softwaredeployment.SoftwaredeploymentPackage#getTdProviderConnection_ConnectionString()
     * @model
     * @generated
     */
    String getConnectionString();

    /**
     * Sets the value of the '{@link org.talend.cwm.softwaredeployment.TdProviderConnection#getConnectionString <em>Connection String</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Connection String</em>' attribute.
     * @see #getConnectionString()
     * @generated
     */
    void setConnectionString(String value);

    /**
     * Returns the value of the '<em><b>Driver Class Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Driver Class Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Driver Class Name</em>' attribute.
     * @see #setDriverClassName(String)
     * @see org.talend.cwm.softwaredeployment.SoftwaredeploymentPackage#getTdProviderConnection_DriverClassName()
     * @model
     * @generated
     */
    String getDriverClassName();

    /**
     * Sets the value of the '{@link org.talend.cwm.softwaredeployment.TdProviderConnection#getDriverClassName <em>Driver Class Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Driver Class Name</em>' attribute.
     * @see #getDriverClassName()
     * @generated
     */
    void setDriverClassName(String value);

} // TdProviderConnection
