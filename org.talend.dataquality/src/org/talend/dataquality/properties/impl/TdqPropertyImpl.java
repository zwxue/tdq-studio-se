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
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.talend.core.model.properties.Information;
import org.talend.core.model.properties.InformationLevel;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.User;
import org.talend.dataquality.properties.PropertiesPackage;
import org.talend.dataquality.properties.TdqProperty;
import orgomg.cwm.objectmodel.core.impl.ModelElementImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Tdq Property</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqPropertyImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqPropertyImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqPropertyImpl#getPurpose <em>Purpose</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqPropertyImpl#getDescription <em>Description</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqPropertyImpl#getCreationDate <em>Creation Date</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqPropertyImpl#getModificationDate <em>Modification Date</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqPropertyImpl#getVersion <em>Version</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqPropertyImpl#getStatusCode <em>Status Code</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqPropertyImpl#getItem <em>Item</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqPropertyImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqPropertyImpl#getInformations <em>Informations</em>}</li>
 *   <li>{@link org.talend.dataquality.properties.impl.TdqPropertyImpl#getMaxInformationLevel <em>Max Information Level</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TdqPropertyImpl extends ModelElementImpl implements TdqProperty {
    /**
     * The default value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getId()
     * @generated
     * @ordered
     */
    protected static final String ID_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getId() <em>Id</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getId()
     * @generated
     * @ordered
     */
    protected String id = ID_EDEFAULT;

    /**
     * The default value of the '{@link #getLabel() <em>Label</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabel()
     * @generated
     * @ordered
     */
    protected static final String LABEL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLabel() <em>Label</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLabel()
     * @generated
     * @ordered
     */
    protected String label = LABEL_EDEFAULT;

    /**
     * The default value of the '{@link #getPurpose() <em>Purpose</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPurpose()
     * @generated
     * @ordered
     */
    protected static final String PURPOSE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getPurpose() <em>Purpose</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPurpose()
     * @generated
     * @ordered
     */
    protected String purpose = PURPOSE_EDEFAULT;

    /**
     * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected static final String DESCRIPTION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected String description = DESCRIPTION_EDEFAULT;

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
     * The default value of the '{@link #getModificationDate() <em>Modification Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getModificationDate()
     * @generated
     * @ordered
     */
    protected static final Date MODIFICATION_DATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getModificationDate() <em>Modification Date</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getModificationDate()
     * @generated
     * @ordered
     */
    protected Date modificationDate = MODIFICATION_DATE_EDEFAULT;

    /**
     * The default value of the '{@link #getVersion() <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVersion()
     * @generated
     * @ordered
     */
    protected static final String VERSION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getVersion() <em>Version</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getVersion()
     * @generated
     * @ordered
     */
    protected String version = VERSION_EDEFAULT;

    /**
     * The default value of the '{@link #getStatusCode() <em>Status Code</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStatusCode()
     * @generated
     * @ordered
     */
    protected static final String STATUS_CODE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getStatusCode() <em>Status Code</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getStatusCode()
     * @generated
     * @ordered
     */
    protected String statusCode = STATUS_CODE_EDEFAULT;

    /**
     * The cached value of the '{@link #getItem() <em>Item</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getItem()
     * @generated
     * @ordered
     */
    protected Item item;

    /**
     * The cached value of the '{@link #getAuthor() <em>Author</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAuthor()
     * @generated
     * @ordered
     */
    protected User author;

    /**
     * The cached value of the '{@link #getInformations() <em>Informations</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getInformations()
     * @generated
     * @ordered
     */
    protected EList informations;

    /**
     * The default value of the '{@link #getMaxInformationLevel() <em>Max Information Level</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaxInformationLevel()
     * @generated
     * @ordered
     */
    protected static final InformationLevel MAX_INFORMATION_LEVEL_EDEFAULT = InformationLevel.DEBUG_LITERAL;

    /**
     * The cached value of the '{@link #getMaxInformationLevel() <em>Max Information Level</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMaxInformationLevel()
     * @generated
     * @ordered
     */
    protected InformationLevel maxInformationLevel = MAX_INFORMATION_LEVEL_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected TdqPropertyImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return PropertiesPackage.Literals.TDQ_PROPERTY;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getId() {
        return id;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setId(String newId) {
        String oldId = id;
        id = newId;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_PROPERTY__ID, oldId, id));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getLabel() {
        return label;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLabel(String newLabel) {
        String oldLabel = label;
        label = newLabel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_PROPERTY__LABEL, oldLabel, label));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPurpose(String newPurpose) {
        String oldPurpose = purpose;
        purpose = newPurpose;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_PROPERTY__PURPOSE, oldPurpose, purpose));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getDescription() {
        return description;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setDescription(String newDescription) {
        String oldDescription = description;
        description = newDescription;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_PROPERTY__DESCRIPTION, oldDescription, description));
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
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_PROPERTY__CREATION_DATE, oldCreationDate, creationDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Date getModificationDate() {
        return modificationDate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setModificationDate(Date newModificationDate) {
        Date oldModificationDate = modificationDate;
        modificationDate = newModificationDate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_PROPERTY__MODIFICATION_DATE, oldModificationDate, modificationDate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getVersion() {
        return version;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setVersion(String newVersion) {
        String oldVersion = version;
        version = newVersion;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_PROPERTY__VERSION, oldVersion, version));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setStatusCode(String newStatusCode) {
        String oldStatusCode = statusCode;
        statusCode = newStatusCode;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_PROPERTY__STATUS_CODE, oldStatusCode, statusCode));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Item getItem() {
        if (item != null && item.eIsProxy()) {
            InternalEObject oldItem = (InternalEObject)item;
            item = (Item)eResolveProxy(oldItem);
            if (item != oldItem) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, PropertiesPackage.TDQ_PROPERTY__ITEM, oldItem, item));
            }
        }
        return item;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Item basicGetItem() {
        return item;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetItem(Item newItem, NotificationChain msgs) {
        Item oldItem = item;
        item = newItem;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_PROPERTY__ITEM, oldItem, newItem);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setItem(Item newItem) {
        if (newItem != item) {
            NotificationChain msgs = null;
            if (item != null)
                msgs = ((InternalEObject)item).eInverseRemove(this, org.talend.core.model.properties.PropertiesPackage.ITEM__PROPERTY, Item.class, msgs);
            if (newItem != null)
                msgs = ((InternalEObject)newItem).eInverseAdd(this, org.talend.core.model.properties.PropertiesPackage.ITEM__PROPERTY, Item.class, msgs);
            msgs = basicSetItem(newItem, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_PROPERTY__ITEM, newItem, newItem));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public User getAuthor() {
        if (author != null && author.eIsProxy()) {
            InternalEObject oldAuthor = (InternalEObject)author;
            author = (User)eResolveProxy(oldAuthor);
            if (author != oldAuthor) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, PropertiesPackage.TDQ_PROPERTY__AUTHOR, oldAuthor, author));
            }
        }
        return author;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public User basicGetAuthor() {
        return author;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAuthor(User newAuthor) {
        User oldAuthor = author;
        author = newAuthor;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_PROPERTY__AUTHOR, oldAuthor, author));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList getInformations() {
        if (informations == null) {
            informations = new EObjectContainmentEList<Information>(Information.class, this, PropertiesPackage.TDQ_PROPERTY__INFORMATIONS);
        }
        return informations;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public InformationLevel getMaxInformationLevel() {
        return maxInformationLevel;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMaxInformationLevel(InformationLevel newMaxInformationLevel) {
        InformationLevel oldMaxInformationLevel = maxInformationLevel;
        maxInformationLevel = newMaxInformationLevel == null ? MAX_INFORMATION_LEVEL_EDEFAULT : newMaxInformationLevel;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, PropertiesPackage.TDQ_PROPERTY__MAX_INFORMATION_LEVEL, oldMaxInformationLevel, maxInformationLevel));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case PropertiesPackage.TDQ_PROPERTY__ITEM:
                if (item != null)
                    msgs = ((InternalEObject)item).eInverseRemove(this, org.talend.core.model.properties.PropertiesPackage.ITEM__PROPERTY, Item.class, msgs);
                return basicSetItem((Item)otherEnd, msgs);
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
            case PropertiesPackage.TDQ_PROPERTY__ITEM:
                return basicSetItem(null, msgs);
            case PropertiesPackage.TDQ_PROPERTY__INFORMATIONS:
                return ((InternalEList<?>)getInformations()).basicRemove(otherEnd, msgs);
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
            case PropertiesPackage.TDQ_PROPERTY__ID:
                return getId();
            case PropertiesPackage.TDQ_PROPERTY__LABEL:
                return getLabel();
            case PropertiesPackage.TDQ_PROPERTY__PURPOSE:
                return getPurpose();
            case PropertiesPackage.TDQ_PROPERTY__DESCRIPTION:
                return getDescription();
            case PropertiesPackage.TDQ_PROPERTY__CREATION_DATE:
                return getCreationDate();
            case PropertiesPackage.TDQ_PROPERTY__MODIFICATION_DATE:
                return getModificationDate();
            case PropertiesPackage.TDQ_PROPERTY__VERSION:
                return getVersion();
            case PropertiesPackage.TDQ_PROPERTY__STATUS_CODE:
                return getStatusCode();
            case PropertiesPackage.TDQ_PROPERTY__ITEM:
                if (resolve) return getItem();
                return basicGetItem();
            case PropertiesPackage.TDQ_PROPERTY__AUTHOR:
                if (resolve) return getAuthor();
                return basicGetAuthor();
            case PropertiesPackage.TDQ_PROPERTY__INFORMATIONS:
                return getInformations();
            case PropertiesPackage.TDQ_PROPERTY__MAX_INFORMATION_LEVEL:
                return getMaxInformationLevel();
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
            case PropertiesPackage.TDQ_PROPERTY__ID:
                setId((String)newValue);
                return;
            case PropertiesPackage.TDQ_PROPERTY__LABEL:
                setLabel((String)newValue);
                return;
            case PropertiesPackage.TDQ_PROPERTY__PURPOSE:
                setPurpose((String)newValue);
                return;
            case PropertiesPackage.TDQ_PROPERTY__DESCRIPTION:
                setDescription((String)newValue);
                return;
            case PropertiesPackage.TDQ_PROPERTY__CREATION_DATE:
                setCreationDate((Date)newValue);
                return;
            case PropertiesPackage.TDQ_PROPERTY__MODIFICATION_DATE:
                setModificationDate((Date)newValue);
                return;
            case PropertiesPackage.TDQ_PROPERTY__VERSION:
                setVersion((String)newValue);
                return;
            case PropertiesPackage.TDQ_PROPERTY__STATUS_CODE:
                setStatusCode((String)newValue);
                return;
            case PropertiesPackage.TDQ_PROPERTY__ITEM:
                setItem((Item)newValue);
                return;
            case PropertiesPackage.TDQ_PROPERTY__AUTHOR:
                setAuthor((User)newValue);
                return;
            case PropertiesPackage.TDQ_PROPERTY__INFORMATIONS:
                getInformations().clear();
                getInformations().addAll((Collection<? extends Information>)newValue);
                return;
            case PropertiesPackage.TDQ_PROPERTY__MAX_INFORMATION_LEVEL:
                setMaxInformationLevel((InformationLevel)newValue);
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
            case PropertiesPackage.TDQ_PROPERTY__ID:
                setId(ID_EDEFAULT);
                return;
            case PropertiesPackage.TDQ_PROPERTY__LABEL:
                setLabel(LABEL_EDEFAULT);
                return;
            case PropertiesPackage.TDQ_PROPERTY__PURPOSE:
                setPurpose(PURPOSE_EDEFAULT);
                return;
            case PropertiesPackage.TDQ_PROPERTY__DESCRIPTION:
                setDescription(DESCRIPTION_EDEFAULT);
                return;
            case PropertiesPackage.TDQ_PROPERTY__CREATION_DATE:
                setCreationDate(CREATION_DATE_EDEFAULT);
                return;
            case PropertiesPackage.TDQ_PROPERTY__MODIFICATION_DATE:
                setModificationDate(MODIFICATION_DATE_EDEFAULT);
                return;
            case PropertiesPackage.TDQ_PROPERTY__VERSION:
                setVersion(VERSION_EDEFAULT);
                return;
            case PropertiesPackage.TDQ_PROPERTY__STATUS_CODE:
                setStatusCode(STATUS_CODE_EDEFAULT);
                return;
            case PropertiesPackage.TDQ_PROPERTY__ITEM:
                setItem((Item)null);
                return;
            case PropertiesPackage.TDQ_PROPERTY__AUTHOR:
                setAuthor((User)null);
                return;
            case PropertiesPackage.TDQ_PROPERTY__INFORMATIONS:
                getInformations().clear();
                return;
            case PropertiesPackage.TDQ_PROPERTY__MAX_INFORMATION_LEVEL:
                setMaxInformationLevel(MAX_INFORMATION_LEVEL_EDEFAULT);
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
            case PropertiesPackage.TDQ_PROPERTY__ID:
                return ID_EDEFAULT == null ? id != null : !ID_EDEFAULT.equals(id);
            case PropertiesPackage.TDQ_PROPERTY__LABEL:
                return LABEL_EDEFAULT == null ? label != null : !LABEL_EDEFAULT.equals(label);
            case PropertiesPackage.TDQ_PROPERTY__PURPOSE:
                return PURPOSE_EDEFAULT == null ? purpose != null : !PURPOSE_EDEFAULT.equals(purpose);
            case PropertiesPackage.TDQ_PROPERTY__DESCRIPTION:
                return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
            case PropertiesPackage.TDQ_PROPERTY__CREATION_DATE:
                return CREATION_DATE_EDEFAULT == null ? creationDate != null : !CREATION_DATE_EDEFAULT.equals(creationDate);
            case PropertiesPackage.TDQ_PROPERTY__MODIFICATION_DATE:
                return MODIFICATION_DATE_EDEFAULT == null ? modificationDate != null : !MODIFICATION_DATE_EDEFAULT.equals(modificationDate);
            case PropertiesPackage.TDQ_PROPERTY__VERSION:
                return VERSION_EDEFAULT == null ? version != null : !VERSION_EDEFAULT.equals(version);
            case PropertiesPackage.TDQ_PROPERTY__STATUS_CODE:
                return STATUS_CODE_EDEFAULT == null ? statusCode != null : !STATUS_CODE_EDEFAULT.equals(statusCode);
            case PropertiesPackage.TDQ_PROPERTY__ITEM:
                return item != null;
            case PropertiesPackage.TDQ_PROPERTY__AUTHOR:
                return author != null;
            case PropertiesPackage.TDQ_PROPERTY__INFORMATIONS:
                return informations != null && !informations.isEmpty();
            case PropertiesPackage.TDQ_PROPERTY__MAX_INFORMATION_LEVEL:
                return maxInformationLevel != MAX_INFORMATION_LEVEL_EDEFAULT;
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
        if (baseClass == Property.class) {
            switch (derivedFeatureID) {
                case PropertiesPackage.TDQ_PROPERTY__ID: return org.talend.core.model.properties.PropertiesPackage.PROPERTY__ID;
                case PropertiesPackage.TDQ_PROPERTY__LABEL: return org.talend.core.model.properties.PropertiesPackage.PROPERTY__LABEL;
                case PropertiesPackage.TDQ_PROPERTY__PURPOSE: return org.talend.core.model.properties.PropertiesPackage.PROPERTY__PURPOSE;
                case PropertiesPackage.TDQ_PROPERTY__DESCRIPTION: return org.talend.core.model.properties.PropertiesPackage.PROPERTY__DESCRIPTION;
                case PropertiesPackage.TDQ_PROPERTY__CREATION_DATE: return org.talend.core.model.properties.PropertiesPackage.PROPERTY__CREATION_DATE;
                case PropertiesPackage.TDQ_PROPERTY__MODIFICATION_DATE: return org.talend.core.model.properties.PropertiesPackage.PROPERTY__MODIFICATION_DATE;
                case PropertiesPackage.TDQ_PROPERTY__VERSION: return org.talend.core.model.properties.PropertiesPackage.PROPERTY__VERSION;
                case PropertiesPackage.TDQ_PROPERTY__STATUS_CODE: return org.talend.core.model.properties.PropertiesPackage.PROPERTY__STATUS_CODE;
                case PropertiesPackage.TDQ_PROPERTY__ITEM: return org.talend.core.model.properties.PropertiesPackage.PROPERTY__ITEM;
                case PropertiesPackage.TDQ_PROPERTY__AUTHOR: return org.talend.core.model.properties.PropertiesPackage.PROPERTY__AUTHOR;
                case PropertiesPackage.TDQ_PROPERTY__INFORMATIONS: return org.talend.core.model.properties.PropertiesPackage.PROPERTY__INFORMATIONS;
                case PropertiesPackage.TDQ_PROPERTY__MAX_INFORMATION_LEVEL: return org.talend.core.model.properties.PropertiesPackage.PROPERTY__MAX_INFORMATION_LEVEL;
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
        if (baseClass == Property.class) {
            switch (baseFeatureID) {
                case org.talend.core.model.properties.PropertiesPackage.PROPERTY__ID: return PropertiesPackage.TDQ_PROPERTY__ID;
                case org.talend.core.model.properties.PropertiesPackage.PROPERTY__LABEL: return PropertiesPackage.TDQ_PROPERTY__LABEL;
                case org.talend.core.model.properties.PropertiesPackage.PROPERTY__PURPOSE: return PropertiesPackage.TDQ_PROPERTY__PURPOSE;
                case org.talend.core.model.properties.PropertiesPackage.PROPERTY__DESCRIPTION: return PropertiesPackage.TDQ_PROPERTY__DESCRIPTION;
                case org.talend.core.model.properties.PropertiesPackage.PROPERTY__CREATION_DATE: return PropertiesPackage.TDQ_PROPERTY__CREATION_DATE;
                case org.talend.core.model.properties.PropertiesPackage.PROPERTY__MODIFICATION_DATE: return PropertiesPackage.TDQ_PROPERTY__MODIFICATION_DATE;
                case org.talend.core.model.properties.PropertiesPackage.PROPERTY__VERSION: return PropertiesPackage.TDQ_PROPERTY__VERSION;
                case org.talend.core.model.properties.PropertiesPackage.PROPERTY__STATUS_CODE: return PropertiesPackage.TDQ_PROPERTY__STATUS_CODE;
                case org.talend.core.model.properties.PropertiesPackage.PROPERTY__ITEM: return PropertiesPackage.TDQ_PROPERTY__ITEM;
                case org.talend.core.model.properties.PropertiesPackage.PROPERTY__AUTHOR: return PropertiesPackage.TDQ_PROPERTY__AUTHOR;
                case org.talend.core.model.properties.PropertiesPackage.PROPERTY__INFORMATIONS: return PropertiesPackage.TDQ_PROPERTY__INFORMATIONS;
                case org.talend.core.model.properties.PropertiesPackage.PROPERTY__MAX_INFORMATION_LEVEL: return PropertiesPackage.TDQ_PROPERTY__MAX_INFORMATION_LEVEL;
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
        result.append(", label: ");
        result.append(label);
        result.append(", purpose: ");
        result.append(purpose);
        result.append(", description: ");
        result.append(description);
        result.append(", creationDate: ");
        result.append(creationDate);
        result.append(", modificationDate: ");
        result.append(modificationDate);
        result.append(", version: ");
        result.append(version);
        result.append(", statusCode: ");
        result.append(statusCode);
        result.append(", maxInformationLevel: ");
        result.append(maxInformationLevel);
        result.append(')');
        return result.toString();
    }

} //TdqPropertyImpl
