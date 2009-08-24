/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.properties.impl;

import java.util.Date;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.User;
import org.talend.dataquality.properties.ITDQItemState;
import org.talend.dataquality.properties.PropertiesPackage;
import orgomg.cwm.objectmodel.core.impl.ModelElementImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>ITDQ Item State</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQItemStateImpl#getPath <em>Path</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQItemStateImpl#isDeleted <em>Deleted</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQItemStateImpl#isLocked <em>Locked</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQItemStateImpl#getLocker <em>Locker</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQItemStateImpl#getLockDate <em>Lock Date</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.ITDQItemStateImpl#getCommitDate <em>Commit Date</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ITDQItemStateImpl extends ModelElementImpl implements ITDQItemState {
    /**
     * The default value of the '{@link #getPath() <em>Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPath()
     * @generated
     * @ordered
     */
    protected static final String PATH_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPath() <em>Path</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPath()
     * @generated
     * @ordered
     */
    protected String path = PATH_EDEFAULT;

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
     * The default value of the '{@link #isLocked() <em>Locked</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isLocked()
     * @generated
     * @ordered
     */
    protected static final boolean LOCKED_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isLocked() <em>Locked</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isLocked()
     * @generated
     * @ordered
     */
    protected boolean locked = LOCKED_EDEFAULT;

    /**
     * The cached value of the '{@link #getLocker() <em>Locker</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLocker()
     * @generated
     * @ordered
     */
    protected User locker;

    /**
     * The default value of the '{@link #getLockDate() <em>Lock Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLockDate()
     * @generated
     * @ordered
     */
    protected static final Date LOCK_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLockDate() <em>Lock Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLockDate()
     * @generated
     * @ordered
     */
    protected Date lockDate = LOCK_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getCommitDate() <em>Commit Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCommitDate()
     * @generated
     * @ordered
     */
    protected static final Date COMMIT_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCommitDate() <em>Commit Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCommitDate()
     * @generated
     * @ordered
     */
    protected Date commitDate = COMMIT_DATE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ITDQItemStateImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return PropertiesPackage.Literals.ITDQ_ITEM_STATE;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getPath() {
        return path;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPath(String newPath) {
        String oldPath = path;
        path = newPath;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_ITEM_STATE__PATH, oldPath, path));
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
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_ITEM_STATE__DELETED, oldDeleted, deleted));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLocked(boolean newLocked) {
        boolean oldLocked = locked;
        locked = newLocked;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_ITEM_STATE__LOCKED, oldLocked, locked));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public User getLocker() {
        if (locker != null && locker.eIsProxy()) {
            InternalEObject oldLocker = (InternalEObject)locker;
            locker = (User)eResolveProxy(oldLocker);
            if (locker != oldLocker) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, PropertiesPackage.ITDQ_ITEM_STATE__LOCKER, oldLocker, locker));
            }
        }
        return locker;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public User basicGetLocker() {
        return locker;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLocker(User newLocker) {
        User oldLocker = locker;
        locker = newLocker;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_ITEM_STATE__LOCKER, oldLocker, locker));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getLockDate() {
        return lockDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLockDate(Date newLockDate) {
        Date oldLockDate = lockDate;
        lockDate = newLockDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_ITEM_STATE__LOCK_DATE, oldLockDate, lockDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getCommitDate() {
        return commitDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCommitDate(Date newCommitDate) {
        Date oldCommitDate = commitDate;
        commitDate = newCommitDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.ITDQ_ITEM_STATE__COMMIT_DATE, oldCommitDate, commitDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case PropertiesPackage.ITDQ_ITEM_STATE__PATH:
                return getPath();
            case PropertiesPackage.ITDQ_ITEM_STATE__DELETED:
                return isDeleted() ? Boolean.TRUE : Boolean.FALSE;
            case PropertiesPackage.ITDQ_ITEM_STATE__LOCKED:
                return isLocked() ? Boolean.TRUE : Boolean.FALSE;
            case PropertiesPackage.ITDQ_ITEM_STATE__LOCKER:
                if (resolve) return getLocker();
                return basicGetLocker();
            case PropertiesPackage.ITDQ_ITEM_STATE__LOCK_DATE:
                return getLockDate();
            case PropertiesPackage.ITDQ_ITEM_STATE__COMMIT_DATE:
                return getCommitDate();
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
            case PropertiesPackage.ITDQ_ITEM_STATE__PATH:
                setPath((String)newValue);
                return;
            case PropertiesPackage.ITDQ_ITEM_STATE__DELETED:
                setDeleted(((Boolean)newValue).booleanValue());
                return;
            case PropertiesPackage.ITDQ_ITEM_STATE__LOCKED:
                setLocked(((Boolean)newValue).booleanValue());
                return;
            case PropertiesPackage.ITDQ_ITEM_STATE__LOCKER:
                setLocker((User)newValue);
                return;
            case PropertiesPackage.ITDQ_ITEM_STATE__LOCK_DATE:
                setLockDate((Date)newValue);
                return;
            case PropertiesPackage.ITDQ_ITEM_STATE__COMMIT_DATE:
                setCommitDate((Date)newValue);
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
            case PropertiesPackage.ITDQ_ITEM_STATE__PATH:
                setPath(PATH_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_ITEM_STATE__DELETED:
                setDeleted(DELETED_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_ITEM_STATE__LOCKED:
                setLocked(LOCKED_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_ITEM_STATE__LOCKER:
                setLocker((User)null);
                return;
            case PropertiesPackage.ITDQ_ITEM_STATE__LOCK_DATE:
                setLockDate(LOCK_DATE_EDEFAULT);
                return;
            case PropertiesPackage.ITDQ_ITEM_STATE__COMMIT_DATE:
                setCommitDate(COMMIT_DATE_EDEFAULT);
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
            case PropertiesPackage.ITDQ_ITEM_STATE__PATH:
                return PATH_EDEFAULT == null ? path != null : !PATH_EDEFAULT.equals(path);
            case PropertiesPackage.ITDQ_ITEM_STATE__DELETED:
                return deleted != DELETED_EDEFAULT;
            case PropertiesPackage.ITDQ_ITEM_STATE__LOCKED:
                return locked != LOCKED_EDEFAULT;
            case PropertiesPackage.ITDQ_ITEM_STATE__LOCKER:
                return locker != null;
            case PropertiesPackage.ITDQ_ITEM_STATE__LOCK_DATE:
                return LOCK_DATE_EDEFAULT == null ? lockDate != null : !LOCK_DATE_EDEFAULT.equals(lockDate);
            case PropertiesPackage.ITDQ_ITEM_STATE__COMMIT_DATE:
                return COMMIT_DATE_EDEFAULT == null ? commitDate != null : !COMMIT_DATE_EDEFAULT.equals(commitDate);
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
        if (baseClass == ItemState.class) {
            switch (derivedFeatureID) {
                case PropertiesPackage.ITDQ_ITEM_STATE__PATH: return org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__PATH;
                case PropertiesPackage.ITDQ_ITEM_STATE__DELETED: return org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__DELETED;
                case PropertiesPackage.ITDQ_ITEM_STATE__LOCKED: return org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__LOCKED;
                case PropertiesPackage.ITDQ_ITEM_STATE__LOCKER: return org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__LOCKER;
                case PropertiesPackage.ITDQ_ITEM_STATE__LOCK_DATE: return org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__LOCK_DATE;
                case PropertiesPackage.ITDQ_ITEM_STATE__COMMIT_DATE: return org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__COMMIT_DATE;
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
        if (baseClass == ItemState.class) {
            switch (baseFeatureID) {
                case org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__PATH: return PropertiesPackage.ITDQ_ITEM_STATE__PATH;
                case org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__DELETED: return PropertiesPackage.ITDQ_ITEM_STATE__DELETED;
                case org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__LOCKED: return PropertiesPackage.ITDQ_ITEM_STATE__LOCKED;
                case org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__LOCKER: return PropertiesPackage.ITDQ_ITEM_STATE__LOCKER;
                case org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__LOCK_DATE: return PropertiesPackage.ITDQ_ITEM_STATE__LOCK_DATE;
                case org.talend.core.model.properties.PropertiesPackage.ITEM_STATE__COMMIT_DATE: return PropertiesPackage.ITDQ_ITEM_STATE__COMMIT_DATE;
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
        result.append(" (path: ");
        result.append(path);
        result.append(", deleted: ");
        result.append(deleted);
        result.append(", locked: ");
        result.append(locked);
        result.append(", lockDate: ");
        result.append(lockDate);
        result.append(", commitDate: ");
        result.append(commitDate);
        result.append(')');
        return result.toString();
    }

} //ITDQItemStateImpl
