/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.math.BigInteger;
import java.util.List;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.dataquality.indicators.AverageLengthIndicator;
import org.talend.dataquality.indicators.IndicatorsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Average Length Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.impl.AverageLengthIndicatorImpl#getSumLength <em>Sum Length</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AverageLengthIndicatorImpl extends LengthIndicatorImpl implements AverageLengthIndicator {

    /**
     * The default value of the '{@link #getSumLength() <em>Sum Length</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getSumLength()
     * @generated
     * @ordered
     */
    protected static final Double SUM_LENGTH_EDEFAULT = new Double(0.0);

    /**
     * The cached value of the '{@link #getSumLength() <em>Sum Length</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getSumLength()
     * @generated
     * @ordered
     */
    protected Double sumLength = SUM_LENGTH_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected AverageLengthIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.AVERAGE_LENGTH_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Double getSumLength() {
        return sumLength;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setSumLength(Double newSumLength) {
        Double oldSumLength = sumLength;
        sumLength = newSumLength;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.AVERAGE_LENGTH_INDICATOR__SUM_LENGTH, oldSumLength, sumLength));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public double getAverageLength() {
        if (getCount() == null) {
            return 0.0;
        }
        if (BigInteger.ZERO.equals(getCount())) {
            return 0.0;
        }
        Double totalLength = getSumLength();
        if (totalLength == null) {
            return 0.0;
        }
        return totalLength.doubleValue() / getCount().doubleValue();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case IndicatorsPackage.AVERAGE_LENGTH_INDICATOR__SUM_LENGTH:
                return getSumLength();
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
            case IndicatorsPackage.AVERAGE_LENGTH_INDICATOR__SUM_LENGTH:
                setSumLength((Double)newValue);
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
            case IndicatorsPackage.AVERAGE_LENGTH_INDICATOR__SUM_LENGTH:
                setSumLength(SUM_LENGTH_EDEFAULT);
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
            case IndicatorsPackage.AVERAGE_LENGTH_INDICATOR__SUM_LENGTH:
                return SUM_LENGTH_EDEFAULT == null ? sumLength != null : !SUM_LENGTH_EDEFAULT.equals(sumLength);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    @Override
    public String toString() {
        return "Average Length = " + this.getAverageLength();
    }

    /*
     * (non-Javadoc) ADDED scorreia 2008-04-08 compute sum
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#handle(java.lang.Object)
     */
    @Override
    public boolean handle(Object data) {
        boolean ok = super.handle(data);
        if (data != null) {
            String str = (String) data;
            sumLength += str.length();
        }
        // TODO scorreia handle case when data is null and should be replaced by empty string
        return ok;
    }

    // /*
    // * (non-Javadoc) ADDED scorreia 2008-04-08 get average length
    // *
    // * @see org.talend.dataquality.indicators.impl.LengthIndicatorImpl#getLength()
    // */
    // @Override
    // public int getLength() {
    // if (count == 0) {
    // return 0;
    // }
    // // else
    // return (int) (sumLength / count); // CAST in int
    // }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.LengthIndicatorImpl#storeSqlResults(java.lang.Object[]) ADDED
     * scorreia 2008-04-30 storeSqlResults(List<Object[]> objects)
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {

        if (!checkResults(objects, 2)) {
            return false;
        }

        // http://www.talendforge.org/bugs/view.php?id=4783
        // Oracle treats empty strings as null values
        Object lCount = objects.get(0)[1];
        if (lCount == null) {
            this.setCount(null);
        } else {
            String c = String.valueOf(lCount);
            this.setCount(Long.valueOf(c));
        }

        Object lSum = objects.get(0)[0];
        if (lSum == null) {
            this.setSumLength(null);
        } else {
            String s = String.valueOf(lSum);
            this.setSumLength(Double.valueOf(s));
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#getIntegerValue()
     * 
     * ADDED scorreia 2008-05-12 getIntegerValue()
     */
    @Override
    public Long getIntegerValue() {
        return this.getSumLength().longValue(); // FIXME scorreia use getRealValue method instead
    }

    @Override
    public boolean finalizeComputation() {
        return super.finalizeComputation();
    }

    @Override
    public boolean reset() {
        this.sumLength = SUM_LENGTH_EDEFAULT;
        return super.reset();
    }

    
    
} // AverageLengthIndicatorImpl
