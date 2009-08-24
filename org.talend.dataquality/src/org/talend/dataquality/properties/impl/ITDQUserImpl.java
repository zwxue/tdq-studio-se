/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.properties.impl;

import java.util.Collection;
import java.util.Date;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.talend.core.model.properties.DashboardConnection;
import org.talend.core.model.properties.User;
import org.talend.core.model.properties.UserModuleAuthorization;
import org.talend.core.model.properties.UserProjectAuthorization;
import org.talend.core.model.properties.UserRole;
import org.talend.dataquality.properties.ITDQUser;
import org.talend.dataquality.properties.PropertiesPackage;
import orgomg.cwm.objectmodel.core.impl.ModelElementImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>ITDQ User</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getLogin <em>Login</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getPassword <em>Password</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getFirstName <em>First Name</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getLastName <em>Last Name</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getCreationDate <em>Creation Date</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getDeleteDate <em>Delete Date</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#isDeleted <em>Deleted</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#isAllowedToModifyComponents <em>Allowed To Modify Components</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getComment <em>Comment</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getRole <em>Role</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getProjectAuthorization <em>Project Authorization</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getModuleAuthorization <em>Module Authorization</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getPreferredDashboardConnection <em>Preferred Dashboard Connection</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getLastAdminConnectionDate <em>Last Admin Connection Date</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getLastStudioConnectionDate <em>Last Studio Connection Date</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getFirstAdminConnectionDate <em>First Admin Connection Date</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getFirstStudioConnectionDate <em>First Studio Connection Date</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getAdminConnexionNumber <em>Admin Connexion Number</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getStudioConnexionNumber <em>Studio Connexion Number</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQUserImpl#getAuthenticationInfo <em>Authentication Info</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ITDQUserImpl extends ModelElementImpl implements ITDQUser {
    /**
     * The default value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getId()
     * @generated
     * @ordered
     */
    protected static final int ID_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getId()
     * @generated
     * @ordered
     */
    protected int id = ID_EDEFAULT;

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
    protected static final byte[] PASSWORD_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPassword() <em>Password</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPassword()
     * @generated
     * @ordered
     */
    protected byte[] password = PASSWORD_EDEFAULT;

    /**
     * The default value of the '{@link #getFirstName() <em>First Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFirstName()
     * @generated
     * @ordered
     */
    protected static final String FIRST_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getFirstName() <em>First Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFirstName()
     * @generated
     * @ordered
     */
    protected String firstName = FIRST_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getLastName() <em>Last Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLastName()
     * @generated
     * @ordered
     */
    protected static final String LAST_NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLastName() <em>Last Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLastName()
     * @generated
     * @ordered
     */
    protected String lastName = LAST_NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCreationDate()
     * @generated
     * @ordered
     */
    protected static final Date CREATION_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCreationDate() <em>Creation Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCreationDate()
     * @generated
     * @ordered
     */
    protected Date creationDate = CREATION_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getDeleteDate() <em>Delete Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDeleteDate()
     * @generated
     * @ordered
     */
    protected static final Date DELETE_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDeleteDate() <em>Delete Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDeleteDate()
     * @generated
     * @ordered
     */
    protected Date deleteDate = DELETE_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #isDeleted() <em>Deleted</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isDeleted()
     * @generated
     * @ordered
     */
    protected static final boolean DELETED_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isDeleted() <em>Deleted</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isDeleted()
     * @generated
     * @ordered
     */
    protected boolean deleted = DELETED_EDEFAULT;

    /**
     * The default value of the '{@link #isAllowedToModifyComponents() <em>Allowed To Modify Components</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAllowedToModifyComponents()
     * @generated
     * @ordered
     */
    protected static final boolean ALLOWED_TO_MODIFY_COMPONENTS_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isAllowedToModifyComponents() <em>Allowed To Modify Components</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAllowedToModifyComponents()
     * @generated
     * @ordered
     */
    protected boolean allowedToModifyComponents = ALLOWED_TO_MODIFY_COMPONENTS_EDEFAULT;

    /**
     * The default value of the '{@link #getComment() <em>Comment</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getComment()
     * @generated
     * @ordered
     */
    protected static final String COMMENT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getComment() <em>Comment</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getComment()
     * @generated
     * @ordered
     */
    protected String comment = COMMENT_EDEFAULT;

    /**
     * The cached value of the '{@link #getRole() <em>Role</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRole()
     * @generated
     * @ordered
     */
    protected UserRole role;

    /**
     * The cached value of the '{@link #getProjectAuthorization() <em>Project Authorization</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getProjectAuthorization()
     * @generated
     * @ordered
     */
    protected EList projectAuthorization;

    /**
     * The cached value of the '{@link #getModuleAuthorization() <em>Module Authorization</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getModuleAuthorization()
     * @generated
     * @ordered
     */
    protected EList moduleAuthorization;

    /**
     * The cached value of the '{@link #getPreferredDashboardConnection() <em>Preferred Dashboard Connection</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPreferredDashboardConnection()
     * @generated
     * @ordered
     */
    protected DashboardConnection preferredDashboardConnection;

    /**
     * The default value of the '{@link #getLastAdminConnectionDate() <em>Last Admin Connection Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLastAdminConnectionDate()
     * @generated
     * @ordered
     */
    protected static final Date LAST_ADMIN_CONNECTION_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLastAdminConnectionDate() <em>Last Admin Connection Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLastAdminConnectionDate()
     * @generated
     * @ordered
     */
    protected Date lastAdminConnectionDate = LAST_ADMIN_CONNECTION_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getLastStudioConnectionDate() <em>Last Studio Connection Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLastStudioConnectionDate()
     * @generated
     * @ordered
     */
    protected static final Date LAST_STUDIO_CONNECTION_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLastStudioConnectionDate() <em>Last Studio Connection Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLastStudioConnectionDate()
     * @generated
     * @ordered
     */
    protected Date lastStudioConnectionDate = LAST_STUDIO_CONNECTION_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getFirstAdminConnectionDate() <em>First Admin Connection Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFirstAdminConnectionDate()
     * @generated
     * @ordered
     */
    protected static final Date FIRST_ADMIN_CONNECTION_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getFirstAdminConnectionDate() <em>First Admin Connection Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFirstAdminConnectionDate()
     * @generated
     * @ordered
     */
    protected Date firstAdminConnectionDate = FIRST_ADMIN_CONNECTION_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getFirstStudioConnectionDate() <em>First Studio Connection Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFirstStudioConnectionDate()
     * @generated
     * @ordered
     */
    protected static final Date FIRST_STUDIO_CONNECTION_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getFirstStudioConnectionDate() <em>First Studio Connection Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFirstStudioConnectionDate()
     * @generated
     * @ordered
     */
    protected Date firstStudioConnectionDate = FIRST_STUDIO_CONNECTION_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getAdminConnexionNumber() <em>Admin Connexion Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAdminConnexionNumber()
     * @generated
     * @ordered
     */
    protected static final int ADMIN_CONNEXION_NUMBER_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getAdminConnexionNumber() <em>Admin Connexion Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAdminConnexionNumber()
     * @generated
     * @ordered
     */
    protected int adminConnexionNumber = ADMIN_CONNEXION_NUMBER_EDEFAULT;

    /**
     * The default value of the '{@link #getStudioConnexionNumber() <em>Studio Connexion Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStudioConnexionNumber()
     * @generated
     * @ordered
     */
    protected static final int STUDIO_CONNEXION_NUMBER_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getStudioConnexionNumber() <em>Studio Connexion Number</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStudioConnexionNumber()
     * @generated
     * @ordered
     */
    protected int studioConnexionNumber = STUDIO_CONNEXION_NUMBER_EDEFAULT;

    /**
     * The default value of the '{@link #getAuthenticationInfo() <em>Authentication Info</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAuthenticationInfo()
     * @generated
     * @ordered
     */
    protected static final String AUTHENTICATION_INFO_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getAuthenticationInfo() <em>Authentication Info</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAuthenticationInfo()
     * @generated
     * @ordered
     */
    protected String authenticationInfo = AUTHENTICATION_INFO_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ITDQUserImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return PropertiesPackage.Literals.ITDQ_USER;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getId() {
        return id;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setId(int newId) {
        int oldId = id;
        id = newId;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__ID, oldId, id));
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
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__LOGIN, oldLogin, login));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public byte[] getPassword() {
        return password;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPassword(byte[] newPassword) {
        byte[] oldPassword = password;
        password = newPassword;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__PASSWORD, oldPassword, password));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFirstName(String newFirstName) {
        String oldFirstName = firstName;
        firstName = newFirstName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__FIRST_NAME, oldFirstName, firstName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLastName(String newLastName) {
        String oldLastName = lastName;
        lastName = newLastName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__LAST_NAME, oldLastName, lastName));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCreationDate(Date newCreationDate) {
        Date oldCreationDate = creationDate;
        creationDate = newCreationDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__CREATION_DATE, oldCreationDate, creationDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getDeleteDate() {
        return deleteDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDeleteDate(Date newDeleteDate) {
        Date oldDeleteDate = deleteDate;
        deleteDate = newDeleteDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__DELETE_DATE, oldDeleteDate, deleteDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isDeleted() {
        return deleted;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDeleted(boolean newDeleted) {
        boolean oldDeleted = deleted;
        deleted = newDeleted;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__DELETED, oldDeleted, deleted));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isAllowedToModifyComponents() {
        return allowedToModifyComponents;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAllowedToModifyComponents(boolean newAllowedToModifyComponents) {
        boolean oldAllowedToModifyComponents = allowedToModifyComponents;
        allowedToModifyComponents = newAllowedToModifyComponents;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__ALLOWED_TO_MODIFY_COMPONENTS, oldAllowedToModifyComponents, allowedToModifyComponents));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getComment() {
        return comment;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setComment(String newComment) {
        String oldComment = comment;
        comment = newComment;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__COMMENT, oldComment, comment));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public UserRole getRole() {
        if (role != null && role.eIsProxy()) {
            InternalEObject oldRole = (InternalEObject)role;
            role = (UserRole)eResolveProxy(oldRole);
            if (role != oldRole) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, PropertiesPackage.ITDQ_USER__ROLE, oldRole, role));
            }
        }
        return role;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public UserRole basicGetRole() {
        return role;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRole(UserRole newRole) {
        UserRole oldRole = role;
        role = newRole;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__ROLE, oldRole, role));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getProjectAuthorization() {
        if (projectAuthorization == null) {
            projectAuthorization = new EObjectWithInverseResolvingEList<UserProjectAuthorization>(UserProjectAuthorization.class, this, PropertiesPackage.ITDQ_USER__PROJECT_AUTHORIZATION, org.talend.core.model.properties.PropertiesPackage.USER_PROJECT_AUTHORIZATION__USER);
        }
        return projectAuthorization;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getModuleAuthorization() {
        if (moduleAuthorization == null) {
            moduleAuthorization = new EObjectWithInverseResolvingEList<UserModuleAuthorization>(UserModuleAuthorization.class, this, PropertiesPackage.ITDQ_USER__MODULE_AUTHORIZATION, org.talend.core.model.properties.PropertiesPackage.USER_MODULE_AUTHORIZATION__USER);
        }
        return moduleAuthorization;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DashboardConnection getPreferredDashboardConnection() {
        if (preferredDashboardConnection != null && preferredDashboardConnection.eIsProxy()) {
            InternalEObject oldPreferredDashboardConnection = (InternalEObject)preferredDashboardConnection;
            preferredDashboardConnection = (DashboardConnection)eResolveProxy(oldPreferredDashboardConnection);
            if (preferredDashboardConnection != oldPreferredDashboardConnection) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, PropertiesPackage.ITDQ_USER__PREFERRED_DASHBOARD_CONNECTION, oldPreferredDashboardConnection, preferredDashboardConnection));
            }
        }
        return preferredDashboardConnection;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DashboardConnection basicGetPreferredDashboardConnection() {
        return preferredDashboardConnection;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPreferredDashboardConnection(DashboardConnection newPreferredDashboardConnection) {
        DashboardConnection oldPreferredDashboardConnection = preferredDashboardConnection;
        preferredDashboardConnection = newPreferredDashboardConnection;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__PREFERRED_DASHBOARD_CONNECTION, oldPreferredDashboardConnection, preferredDashboardConnection));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getLastAdminConnectionDate() {
        return lastAdminConnectionDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLastAdminConnectionDate(Date newLastAdminConnectionDate) {
        Date oldLastAdminConnectionDate = lastAdminConnectionDate;
        lastAdminConnectionDate = newLastAdminConnectionDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__LAST_ADMIN_CONNECTION_DATE, oldLastAdminConnectionDate, lastAdminConnectionDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getLastStudioConnectionDate() {
        return lastStudioConnectionDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLastStudioConnectionDate(Date newLastStudioConnectionDate) {
        Date oldLastStudioConnectionDate = lastStudioConnectionDate;
        lastStudioConnectionDate = newLastStudioConnectionDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__LAST_STUDIO_CONNECTION_DATE, oldLastStudioConnectionDate, lastStudioConnectionDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getFirstAdminConnectionDate() {
        return firstAdminConnectionDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFirstAdminConnectionDate(Date newFirstAdminConnectionDate) {
        Date oldFirstAdminConnectionDate = firstAdminConnectionDate;
        firstAdminConnectionDate = newFirstAdminConnectionDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__FIRST_ADMIN_CONNECTION_DATE, oldFirstAdminConnectionDate, firstAdminConnectionDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getFirstStudioConnectionDate() {
        return firstStudioConnectionDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFirstStudioConnectionDate(Date newFirstStudioConnectionDate) {
        Date oldFirstStudioConnectionDate = firstStudioConnectionDate;
        firstStudioConnectionDate = newFirstStudioConnectionDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__FIRST_STUDIO_CONNECTION_DATE, oldFirstStudioConnectionDate, firstStudioConnectionDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getAdminConnexionNumber() {
        return adminConnexionNumber;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAdminConnexionNumber(int newAdminConnexionNumber) {
        int oldAdminConnexionNumber = adminConnexionNumber;
        adminConnexionNumber = newAdminConnexionNumber;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__ADMIN_CONNEXION_NUMBER, oldAdminConnexionNumber, adminConnexionNumber));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getStudioConnexionNumber() {
        return studioConnexionNumber;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setStudioConnexionNumber(int newStudioConnexionNumber) {
        int oldStudioConnexionNumber = studioConnexionNumber;
        studioConnexionNumber = newStudioConnexionNumber;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__STUDIO_CONNEXION_NUMBER, oldStudioConnexionNumber, studioConnexionNumber));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getAuthenticationInfo() {
        return authenticationInfo;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAuthenticationInfo(String newAuthenticationInfo) {
        String oldAuthenticationInfo = authenticationInfo;
        authenticationInfo = newAuthenticationInfo;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_USER__AUTHENTICATION_INFO, oldAuthenticationInfo, authenticationInfo));
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
            case PropertiesPackage.ITDQ_USER__PROJECT_AUTHORIZATION:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getProjectAuthorization()).basicAdd(otherEnd, msgs);
            case PropertiesPackage.ITDQ_USER__MODULE_AUTHORIZATION:
                return ((InternalEList<InternalEObject>)(InternalEList<?>)getModuleAuthorization()).basicAdd(otherEnd, msgs);
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
            case PropertiesPackage.ITDQ_USER__PROJECT_AUTHORIZATION:
                return ((InternalEList<?>)getProjectAuthorization()).basicRemove(otherEnd, msgs);
            case PropertiesPackage.ITDQ_USER__MODULE_AUTHORIZATION:
                return ((InternalEList<?>)getModuleAuthorization()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case PropertiesPackage.ITDQ_USER__ID:
                return new Integer(getId());
            case PropertiesPackage.ITDQ_USER__LOGIN:
                return getLogin();
            case PropertiesPackage.ITDQ_USER__PASSWORD:
                return getPassword();
            case PropertiesPackage.ITDQ_USER__FIRST_NAME:
                return getFirstName();
            case PropertiesPackage.ITDQ_USER__LAST_NAME:
                return getLastName();
            case PropertiesPackage.ITDQ_USER__CREATION_DATE:
                return getCreationDate();
            case PropertiesPackage.ITDQ_USER__DELETE_DATE:
                return getDeleteDate();
            case PropertiesPackage.ITDQ_USER__DELETED:
                return isDeleted() ? Boolean.TRUE : Boolean.FALSE;
            case PropertiesPackage.ITDQ_USER__ALLOWED_TO_MODIFY_COMPONENTS:
                return isAllowedToModifyComponents() ? Boolean.TRUE : Boolean.FALSE;
            case PropertiesPackage.ITDQ_USER__COMMENT:
                return getComment();
            case PropertiesPackage.ITDQ_USER__ROLE:
                if (resolve) return getRole();
                return basicGetRole();
            case PropertiesPackage.ITDQ_USER__PROJECT_AUTHORIZATION:
                return getProjectAuthorization();
            case PropertiesPackage.ITDQ_USER__MODULE_AUTHORIZATION:
                return getModuleAuthorization();
            case PropertiesPackage.ITDQ_USER__PREFERRED_DASHBOARD_CONNECTION:
                if (resolve) return getPreferredDashboardConnection();
                return basicGetPreferredDashboardConnection();
            case PropertiesPackage.ITDQ_USER__LAST_ADMIN_CONNECTION_DATE:
                return getLastAdminConnectionDate();
            case PropertiesPackage.ITDQ_USER__LAST_STUDIO_CONNECTION_DATE:
                return getLastStudioConnectionDate();
            case PropertiesPackage.ITDQ_USER__FIRST_ADMIN_CONNECTION_DATE:
                return getFirstAdminConnectionDate();
            case PropertiesPackage.ITDQ_USER__FIRST_STUDIO_CONNECTION_DATE:
                return getFirstStudioConnectionDate();
            case PropertiesPackage.ITDQ_USER__ADMIN_CONNEXION_NUMBER:
                return new Integer(getAdminConnexionNumber());
            case PropertiesPackage.ITDQ_USER__STUDIO_CONNEXION_NUMBER:
                return new Integer(getStudioConnexionNumber());
            case PropertiesPackage.ITDQ_USER__AUTHENTICATION_INFO:
                return getAuthenticationInfo();
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
            case PropertiesPackage.ITDQ_USER__ID:
                setId(((Integer)newValue).intValue());
                return;
            case PropertiesPackage.ITDQ_USER__LOGIN:
                setLogin((String)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__PASSWORD:
                setPassword((byte[])newValue);
                return;
            case PropertiesPackage.ITDQ_USER__FIRST_NAME:
                setFirstName((String)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__LAST_NAME:
                setLastName((String)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__CREATION_DATE:
                setCreationDate((Date)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__DELETE_DATE:
                setDeleteDate((Date)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__DELETED:
                setDeleted(((Boolean)newValue).booleanValue());
                return;
            case PropertiesPackage.ITDQ_USER__ALLOWED_TO_MODIFY_COMPONENTS:
                setAllowedToModifyComponents(((Boolean)newValue).booleanValue());
                return;
            case PropertiesPackage.ITDQ_USER__COMMENT:
                setComment((String)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__ROLE:
                setRole((UserRole)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__PROJECT_AUTHORIZATION:
                getProjectAuthorization().clear();
                getProjectAuthorization().addAll((Collection<? extends UserProjectAuthorization>)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__MODULE_AUTHORIZATION:
                getModuleAuthorization().clear();
                getModuleAuthorization().addAll((Collection<? extends UserModuleAuthorization>)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__PREFERRED_DASHBOARD_CONNECTION:
                setPreferredDashboardConnection((DashboardConnection)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__LAST_ADMIN_CONNECTION_DATE:
                setLastAdminConnectionDate((Date)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__LAST_STUDIO_CONNECTION_DATE:
                setLastStudioConnectionDate((Date)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__FIRST_ADMIN_CONNECTION_DATE:
                setFirstAdminConnectionDate((Date)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__FIRST_STUDIO_CONNECTION_DATE:
                setFirstStudioConnectionDate((Date)newValue);
                return;
            case PropertiesPackage.ITDQ_USER__ADMIN_CONNEXION_NUMBER:
                setAdminConnexionNumber(((Integer)newValue).intValue());
                return;
            case PropertiesPackage.ITDQ_USER__STUDIO_CONNEXION_NUMBER:
                setStudioConnexionNumber(((Integer)newValue).intValue());
                return;
            case PropertiesPackage.ITDQ_USER__AUTHENTICATION_INFO:
                setAuthenticationInfo((String)newValue);
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
            case PropertiesPackage.ITDQ_USER__ID:
                setId(ID_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__LOGIN:
                setLogin(LOGIN_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__PASSWORD:
                setPassword(PASSWORD_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__FIRST_NAME:
                setFirstName(FIRST_NAME_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__LAST_NAME:
                setLastName(LAST_NAME_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__CREATION_DATE:
                setCreationDate(CREATION_DATE_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__DELETE_DATE:
                setDeleteDate(DELETE_DATE_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__DELETED:
                setDeleted(DELETED_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__ALLOWED_TO_MODIFY_COMPONENTS:
                setAllowedToModifyComponents(ALLOWED_TO_MODIFY_COMPONENTS_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__COMMENT:
                setComment(COMMENT_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__ROLE:
                setRole((UserRole)null);
                return;
            case PropertiesPackage.ITDQ_USER__PROJECT_AUTHORIZATION:
                getProjectAuthorization().clear();
                return;
            case PropertiesPackage.ITDQ_USER__MODULE_AUTHORIZATION:
                getModuleAuthorization().clear();
                return;
            case PropertiesPackage.ITDQ_USER__PREFERRED_DASHBOARD_CONNECTION:
                setPreferredDashboardConnection((DashboardConnection)null);
                return;
            case PropertiesPackage.ITDQ_USER__LAST_ADMIN_CONNECTION_DATE:
                setLastAdminConnectionDate(LAST_ADMIN_CONNECTION_DATE_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__LAST_STUDIO_CONNECTION_DATE:
                setLastStudioConnectionDate(LAST_STUDIO_CONNECTION_DATE_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__FIRST_ADMIN_CONNECTION_DATE:
                setFirstAdminConnectionDate(FIRST_ADMIN_CONNECTION_DATE_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__FIRST_STUDIO_CONNECTION_DATE:
                setFirstStudioConnectionDate(FIRST_STUDIO_CONNECTION_DATE_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__ADMIN_CONNEXION_NUMBER:
                setAdminConnexionNumber(ADMIN_CONNEXION_NUMBER_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__STUDIO_CONNEXION_NUMBER:
                setStudioConnexionNumber(STUDIO_CONNEXION_NUMBER_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_USER__AUTHENTICATION_INFO:
                setAuthenticationInfo(AUTHENTICATION_INFO_EDEFAULT);
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
            case PropertiesPackage.ITDQ_USER__ID:
                return id != ID_EDEFAULT;
            case PropertiesPackage.ITDQ_USER__LOGIN:
                return LOGIN_EDEFAULT == null ? login != null : !LOGIN_EDEFAULT.equals(login);
            case PropertiesPackage.ITDQ_USER__PASSWORD:
                return PASSWORD_EDEFAULT == null ? password != null : !PASSWORD_EDEFAULT.equals(password);
            case PropertiesPackage.ITDQ_USER__FIRST_NAME:
                return FIRST_NAME_EDEFAULT == null ? firstName != null : !FIRST_NAME_EDEFAULT.equals(firstName);
            case PropertiesPackage.ITDQ_USER__LAST_NAME:
                return LAST_NAME_EDEFAULT == null ? lastName != null : !LAST_NAME_EDEFAULT.equals(lastName);
            case PropertiesPackage.ITDQ_USER__CREATION_DATE:
                return CREATION_DATE_EDEFAULT == null ? creationDate != null : !CREATION_DATE_EDEFAULT.equals(creationDate);
            case PropertiesPackage.ITDQ_USER__DELETE_DATE:
                return DELETE_DATE_EDEFAULT == null ? deleteDate != null : !DELETE_DATE_EDEFAULT.equals(deleteDate);
            case PropertiesPackage.ITDQ_USER__DELETED:
                return deleted != DELETED_EDEFAULT;
            case PropertiesPackage.ITDQ_USER__ALLOWED_TO_MODIFY_COMPONENTS:
                return allowedToModifyComponents != ALLOWED_TO_MODIFY_COMPONENTS_EDEFAULT;
            case PropertiesPackage.ITDQ_USER__COMMENT:
                return COMMENT_EDEFAULT == null ? comment != null : !COMMENT_EDEFAULT.equals(comment);
            case PropertiesPackage.ITDQ_USER__ROLE:
                return role != null;
            case PropertiesPackage.ITDQ_USER__PROJECT_AUTHORIZATION:
                return projectAuthorization != null && !projectAuthorization.isEmpty();
            case PropertiesPackage.ITDQ_USER__MODULE_AUTHORIZATION:
                return moduleAuthorization != null && !moduleAuthorization.isEmpty();
            case PropertiesPackage.ITDQ_USER__PREFERRED_DASHBOARD_CONNECTION:
                return preferredDashboardConnection != null;
            case PropertiesPackage.ITDQ_USER__LAST_ADMIN_CONNECTION_DATE:
                return LAST_ADMIN_CONNECTION_DATE_EDEFAULT == null ? lastAdminConnectionDate != null : !LAST_ADMIN_CONNECTION_DATE_EDEFAULT.equals(lastAdminConnectionDate);
            case PropertiesPackage.ITDQ_USER__LAST_STUDIO_CONNECTION_DATE:
                return LAST_STUDIO_CONNECTION_DATE_EDEFAULT == null ? lastStudioConnectionDate != null : !LAST_STUDIO_CONNECTION_DATE_EDEFAULT.equals(lastStudioConnectionDate);
            case PropertiesPackage.ITDQ_USER__FIRST_ADMIN_CONNECTION_DATE:
                return FIRST_ADMIN_CONNECTION_DATE_EDEFAULT == null ? firstAdminConnectionDate != null : !FIRST_ADMIN_CONNECTION_DATE_EDEFAULT.equals(firstAdminConnectionDate);
            case PropertiesPackage.ITDQ_USER__FIRST_STUDIO_CONNECTION_DATE:
                return FIRST_STUDIO_CONNECTION_DATE_EDEFAULT == null ? firstStudioConnectionDate != null : !FIRST_STUDIO_CONNECTION_DATE_EDEFAULT.equals(firstStudioConnectionDate);
            case PropertiesPackage.ITDQ_USER__ADMIN_CONNEXION_NUMBER:
                return adminConnexionNumber != ADMIN_CONNEXION_NUMBER_EDEFAULT;
            case PropertiesPackage.ITDQ_USER__STUDIO_CONNEXION_NUMBER:
                return studioConnexionNumber != STUDIO_CONNEXION_NUMBER_EDEFAULT;
            case PropertiesPackage.ITDQ_USER__AUTHENTICATION_INFO:
                return AUTHENTICATION_INFO_EDEFAULT == null ? authenticationInfo != null : !AUTHENTICATION_INFO_EDEFAULT.equals(authenticationInfo);
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
        if (baseClass == User.class) {
            switch (derivedFeatureID) {
                case PropertiesPackage.ITDQ_USER__ID: return org.talend.core.model.properties.PropertiesPackage.USER__ID;
                case PropertiesPackage.ITDQ_USER__LOGIN: return org.talend.core.model.properties.PropertiesPackage.USER__LOGIN;
                case PropertiesPackage.ITDQ_USER__PASSWORD: return org.talend.core.model.properties.PropertiesPackage.USER__PASSWORD;
                case PropertiesPackage.ITDQ_USER__FIRST_NAME: return org.talend.core.model.properties.PropertiesPackage.USER__FIRST_NAME;
                case PropertiesPackage.ITDQ_USER__LAST_NAME: return org.talend.core.model.properties.PropertiesPackage.USER__LAST_NAME;
                case PropertiesPackage.ITDQ_USER__CREATION_DATE: return org.talend.core.model.properties.PropertiesPackage.USER__CREATION_DATE;
                case PropertiesPackage.ITDQ_USER__DELETE_DATE: return org.talend.core.model.properties.PropertiesPackage.USER__DELETE_DATE;
                case PropertiesPackage.ITDQ_USER__DELETED: return org.talend.core.model.properties.PropertiesPackage.USER__DELETED;
                case PropertiesPackage.ITDQ_USER__ALLOWED_TO_MODIFY_COMPONENTS: return org.talend.core.model.properties.PropertiesPackage.USER__ALLOWED_TO_MODIFY_COMPONENTS;
                case PropertiesPackage.ITDQ_USER__COMMENT: return org.talend.core.model.properties.PropertiesPackage.USER__COMMENT;
                case PropertiesPackage.ITDQ_USER__ROLE: return org.talend.core.model.properties.PropertiesPackage.USER__ROLE;
                case PropertiesPackage.ITDQ_USER__PROJECT_AUTHORIZATION: return org.talend.core.model.properties.PropertiesPackage.USER__PROJECT_AUTHORIZATION;
                case PropertiesPackage.ITDQ_USER__MODULE_AUTHORIZATION: return org.talend.core.model.properties.PropertiesPackage.USER__MODULE_AUTHORIZATION;
                case PropertiesPackage.ITDQ_USER__PREFERRED_DASHBOARD_CONNECTION: return org.talend.core.model.properties.PropertiesPackage.USER__PREFERRED_DASHBOARD_CONNECTION;
                case PropertiesPackage.ITDQ_USER__LAST_ADMIN_CONNECTION_DATE: return org.talend.core.model.properties.PropertiesPackage.USER__LAST_ADMIN_CONNECTION_DATE;
                case PropertiesPackage.ITDQ_USER__LAST_STUDIO_CONNECTION_DATE: return org.talend.core.model.properties.PropertiesPackage.USER__LAST_STUDIO_CONNECTION_DATE;
                case PropertiesPackage.ITDQ_USER__FIRST_ADMIN_CONNECTION_DATE: return org.talend.core.model.properties.PropertiesPackage.USER__FIRST_ADMIN_CONNECTION_DATE;
                case PropertiesPackage.ITDQ_USER__FIRST_STUDIO_CONNECTION_DATE: return org.talend.core.model.properties.PropertiesPackage.USER__FIRST_STUDIO_CONNECTION_DATE;
                case PropertiesPackage.ITDQ_USER__ADMIN_CONNEXION_NUMBER: return org.talend.core.model.properties.PropertiesPackage.USER__ADMIN_CONNEXION_NUMBER;
                case PropertiesPackage.ITDQ_USER__STUDIO_CONNEXION_NUMBER: return org.talend.core.model.properties.PropertiesPackage.USER__STUDIO_CONNEXION_NUMBER;
                case PropertiesPackage.ITDQ_USER__AUTHENTICATION_INFO: return org.talend.core.model.properties.PropertiesPackage.USER__AUTHENTICATION_INFO;
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
        if (baseClass == User.class) {
            switch (baseFeatureID) {
                case org.talend.core.model.properties.PropertiesPackage.USER__ID: return PropertiesPackage.ITDQ_USER__ID;
                case org.talend.core.model.properties.PropertiesPackage.USER__LOGIN: return PropertiesPackage.ITDQ_USER__LOGIN;
                case org.talend.core.model.properties.PropertiesPackage.USER__PASSWORD: return PropertiesPackage.ITDQ_USER__PASSWORD;
                case org.talend.core.model.properties.PropertiesPackage.USER__FIRST_NAME: return PropertiesPackage.ITDQ_USER__FIRST_NAME;
                case org.talend.core.model.properties.PropertiesPackage.USER__LAST_NAME: return PropertiesPackage.ITDQ_USER__LAST_NAME;
                case org.talend.core.model.properties.PropertiesPackage.USER__CREATION_DATE: return PropertiesPackage.ITDQ_USER__CREATION_DATE;
                case org.talend.core.model.properties.PropertiesPackage.USER__DELETE_DATE: return PropertiesPackage.ITDQ_USER__DELETE_DATE;
                case org.talend.core.model.properties.PropertiesPackage.USER__DELETED: return PropertiesPackage.ITDQ_USER__DELETED;
                case org.talend.core.model.properties.PropertiesPackage.USER__ALLOWED_TO_MODIFY_COMPONENTS: return PropertiesPackage.ITDQ_USER__ALLOWED_TO_MODIFY_COMPONENTS;
                case org.talend.core.model.properties.PropertiesPackage.USER__COMMENT: return PropertiesPackage.ITDQ_USER__COMMENT;
                case org.talend.core.model.properties.PropertiesPackage.USER__ROLE: return PropertiesPackage.ITDQ_USER__ROLE;
                case org.talend.core.model.properties.PropertiesPackage.USER__PROJECT_AUTHORIZATION: return PropertiesPackage.ITDQ_USER__PROJECT_AUTHORIZATION;
                case org.talend.core.model.properties.PropertiesPackage.USER__MODULE_AUTHORIZATION: return PropertiesPackage.ITDQ_USER__MODULE_AUTHORIZATION;
                case org.talend.core.model.properties.PropertiesPackage.USER__PREFERRED_DASHBOARD_CONNECTION: return PropertiesPackage.ITDQ_USER__PREFERRED_DASHBOARD_CONNECTION;
                case org.talend.core.model.properties.PropertiesPackage.USER__LAST_ADMIN_CONNECTION_DATE: return PropertiesPackage.ITDQ_USER__LAST_ADMIN_CONNECTION_DATE;
                case org.talend.core.model.properties.PropertiesPackage.USER__LAST_STUDIO_CONNECTION_DATE: return PropertiesPackage.ITDQ_USER__LAST_STUDIO_CONNECTION_DATE;
                case org.talend.core.model.properties.PropertiesPackage.USER__FIRST_ADMIN_CONNECTION_DATE: return PropertiesPackage.ITDQ_USER__FIRST_ADMIN_CONNECTION_DATE;
                case org.talend.core.model.properties.PropertiesPackage.USER__FIRST_STUDIO_CONNECTION_DATE: return PropertiesPackage.ITDQ_USER__FIRST_STUDIO_CONNECTION_DATE;
                case org.talend.core.model.properties.PropertiesPackage.USER__ADMIN_CONNEXION_NUMBER: return PropertiesPackage.ITDQ_USER__ADMIN_CONNEXION_NUMBER;
                case org.talend.core.model.properties.PropertiesPackage.USER__STUDIO_CONNEXION_NUMBER: return PropertiesPackage.ITDQ_USER__STUDIO_CONNEXION_NUMBER;
                case org.talend.core.model.properties.PropertiesPackage.USER__AUTHENTICATION_INFO: return PropertiesPackage.ITDQ_USER__AUTHENTICATION_INFO;
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
        result.append(" (id: ");
        result.append(id);
        result.append(", login: ");
        result.append(login);
        result.append(", password: ");
        result.append(password);
        result.append(", firstName: ");
        result.append(firstName);
        result.append(", lastName: ");
        result.append(lastName);
        result.append(", creationDate: ");
        result.append(creationDate);
        result.append(", deleteDate: ");
        result.append(deleteDate);
        result.append(", deleted: ");
        result.append(deleted);
        result.append(", allowedToModifyComponents: ");
        result.append(allowedToModifyComponents);
        result.append(", Comment: ");
        result.append(comment);
        result.append(", lastAdminConnectionDate: ");
        result.append(lastAdminConnectionDate);
        result.append(", lastStudioConnectionDate: ");
        result.append(lastStudioConnectionDate);
        result.append(", firstAdminConnectionDate: ");
        result.append(firstAdminConnectionDate);
        result.append(", firstStudioConnectionDate: ");
        result.append(firstStudioConnectionDate);
        result.append(", adminConnexionNumber: ");
        result.append(adminConnexionNumber);
        result.append(", studioConnexionNumber: ");
        result.append(studioConnexionNumber);
        result.append(", authenticationInfo: ");
        result.append(authenticationInfo);
        result.append(')');
        return result.toString();
    }

} //ITDQUserImpl
