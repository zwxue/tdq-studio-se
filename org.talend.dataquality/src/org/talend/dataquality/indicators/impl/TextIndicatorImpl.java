/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.Indicator;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MaxLengthIndicator;
import org.talend.dataquality.indicators.MinLengthIndicator;
import org.talend.dataquality.indicators.TextIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Text Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.impl.TextIndicatorImpl#getAverageLengthIndicator <em>Average Length Indicator</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.impl.TextIndicatorImpl#getMaxLengthIndicator <em>Max Length Indicator</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.impl.TextIndicatorImpl#getMinLengthIndicator <em>Min Length Indicator</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TextIndicatorImpl extends CompositeIndicatorImpl implements TextIndicator {

    /**
     * The cached value of the '{@link #getAverageLengthIndicator() <em>Average Length Indicator</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getAverageLengthIndicator()
     * @generated
     * @ordered
     */
    protected AverageLengthIndicator averageLengthIndicator;

    /**
     * The cached value of the '{@link #getMaxLengthIndicator() <em>Max Length Indicator</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getMaxLengthIndicator()
     * @generated
     * @ordered
     */
    protected MaxLengthIndicator maxLengthIndicator;

    /**
     * The cached value of the '{@link #getMinLengthIndicator() <em>Min Length Indicator</em>}' containment reference.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getMinLengthIndicator()
     * @generated
     * @ordered
     */
    protected MinLengthIndicator minLengthIndicator;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected TextIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.TEXT_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public AverageLengthIndicator getAverageLengthIndicator() {
        return averageLengthIndicator;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetAverageLengthIndicator(AverageLengthIndicator newAverageLengthIndicator,
            NotificationChain msgs) {
        AverageLengthIndicator oldAverageLengthIndicator = averageLengthIndicator;
        averageLengthIndicator = newAverageLengthIndicator;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, IndicatorsPackage.TEXT_INDICATOR__AVERAGE_LENGTH_INDICATOR, oldAverageLengthIndicator, newAverageLengthIndicator);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setAverageLengthIndicator(AverageLengthIndicator newAverageLengthIndicator) {
        if (newAverageLengthIndicator != averageLengthIndicator) {
            NotificationChain msgs = null;
            if (averageLengthIndicator != null)
                msgs = ((InternalEObject)averageLengthIndicator).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - IndicatorsPackage.TEXT_INDICATOR__AVERAGE_LENGTH_INDICATOR, null, msgs);
            if (newAverageLengthIndicator != null)
                msgs = ((InternalEObject)newAverageLengthIndicator).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - IndicatorsPackage.TEXT_INDICATOR__AVERAGE_LENGTH_INDICATOR, null, msgs);
            msgs = basicSetAverageLengthIndicator(newAverageLengthIndicator, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.TEXT_INDICATOR__AVERAGE_LENGTH_INDICATOR, newAverageLengthIndicator, newAverageLengthIndicator));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public MaxLengthIndicator getMaxLengthIndicator() {
        return maxLengthIndicator;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetMaxLengthIndicator(MaxLengthIndicator newMaxLengthIndicator, NotificationChain msgs) {
        MaxLengthIndicator oldMaxLengthIndicator = maxLengthIndicator;
        maxLengthIndicator = newMaxLengthIndicator;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, IndicatorsPackage.TEXT_INDICATOR__MAX_LENGTH_INDICATOR, oldMaxLengthIndicator, newMaxLengthIndicator);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setMaxLengthIndicator(MaxLengthIndicator newMaxLengthIndicator) {
        if (newMaxLengthIndicator != maxLengthIndicator) {
            NotificationChain msgs = null;
            if (maxLengthIndicator != null)
                msgs = ((InternalEObject)maxLengthIndicator).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - IndicatorsPackage.TEXT_INDICATOR__MAX_LENGTH_INDICATOR, null, msgs);
            if (newMaxLengthIndicator != null)
                msgs = ((InternalEObject)newMaxLengthIndicator).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - IndicatorsPackage.TEXT_INDICATOR__MAX_LENGTH_INDICATOR, null, msgs);
            msgs = basicSetMaxLengthIndicator(newMaxLengthIndicator, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.TEXT_INDICATOR__MAX_LENGTH_INDICATOR, newMaxLengthIndicator, newMaxLengthIndicator));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public MinLengthIndicator getMinLengthIndicator() {
        return minLengthIndicator;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public NotificationChain basicSetMinLengthIndicator(MinLengthIndicator newMinLengthIndicator, NotificationChain msgs) {
        MinLengthIndicator oldMinLengthIndicator = minLengthIndicator;
        minLengthIndicator = newMinLengthIndicator;
        if (eNotificationRequired()) {
            ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, IndicatorsPackage.TEXT_INDICATOR__MIN_LENGTH_INDICATOR, oldMinLengthIndicator, newMinLengthIndicator);
            if (msgs == null) msgs = notification; else msgs.add(notification);
        }
        return msgs;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setMinLengthIndicator(MinLengthIndicator newMinLengthIndicator) {
        if (newMinLengthIndicator != minLengthIndicator) {
            NotificationChain msgs = null;
            if (minLengthIndicator != null)
                msgs = ((InternalEObject)minLengthIndicator).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - IndicatorsPackage.TEXT_INDICATOR__MIN_LENGTH_INDICATOR, null, msgs);
            if (newMinLengthIndicator != null)
                msgs = ((InternalEObject)newMinLengthIndicator).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - IndicatorsPackage.TEXT_INDICATOR__MIN_LENGTH_INDICATOR, null, msgs);
            msgs = basicSetMinLengthIndicator(newMinLengthIndicator, msgs);
            if (msgs != null) msgs.dispatch();
        }
        else if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.TEXT_INDICATOR__MIN_LENGTH_INDICATOR, newMinLengthIndicator, newMinLengthIndicator));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
            case IndicatorsPackage.TEXT_INDICATOR__AVERAGE_LENGTH_INDICATOR:
                return basicSetAverageLengthIndicator(null, msgs);
            case IndicatorsPackage.TEXT_INDICATOR__MAX_LENGTH_INDICATOR:
                return basicSetMaxLengthIndicator(null, msgs);
            case IndicatorsPackage.TEXT_INDICATOR__MIN_LENGTH_INDICATOR:
                return basicSetMinLengthIndicator(null, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case IndicatorsPackage.TEXT_INDICATOR__AVERAGE_LENGTH_INDICATOR:
                return getAverageLengthIndicator();
            case IndicatorsPackage.TEXT_INDICATOR__MAX_LENGTH_INDICATOR:
                return getMaxLengthIndicator();
            case IndicatorsPackage.TEXT_INDICATOR__MIN_LENGTH_INDICATOR:
                return getMinLengthIndicator();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case IndicatorsPackage.TEXT_INDICATOR__AVERAGE_LENGTH_INDICATOR:
                setAverageLengthIndicator((AverageLengthIndicator)newValue);
                return;
            case IndicatorsPackage.TEXT_INDICATOR__MAX_LENGTH_INDICATOR:
                setMaxLengthIndicator((MaxLengthIndicator)newValue);
                return;
            case IndicatorsPackage.TEXT_INDICATOR__MIN_LENGTH_INDICATOR:
                setMinLengthIndicator((MinLengthIndicator)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case IndicatorsPackage.TEXT_INDICATOR__AVERAGE_LENGTH_INDICATOR:
                setAverageLengthIndicator((AverageLengthIndicator)null);
                return;
            case IndicatorsPackage.TEXT_INDICATOR__MAX_LENGTH_INDICATOR:
                setMaxLengthIndicator((MaxLengthIndicator)null);
                return;
            case IndicatorsPackage.TEXT_INDICATOR__MIN_LENGTH_INDICATOR:
                setMinLengthIndicator((MinLengthIndicator)null);
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case IndicatorsPackage.TEXT_INDICATOR__AVERAGE_LENGTH_INDICATOR:
                return averageLengthIndicator != null;
            case IndicatorsPackage.TEXT_INDICATOR__MAX_LENGTH_INDICATOR:
                return maxLengthIndicator != null;
            case IndicatorsPackage.TEXT_INDICATOR__MIN_LENGTH_INDICATOR:
                return minLengthIndicator != null;
        }
        return super.eIsSet(featureID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.CompositeIndicatorImpl#getChildIndicators()
     * 
     * ADDED scorreia 2008-05-22 getChildIndicators()
     */
    @Override
    public EList<Indicator> getChildIndicators() {
        EList<Indicator> children = new BasicEList<Indicator>();
        addChildToList(this.getAverageLengthIndicator(), children);
        addChildToList(this.getMaxLengthIndicator(), children);
        addChildToList(this.getMinLengthIndicator(), children);
        return children;
    }
} // TextIndicatorImpl
