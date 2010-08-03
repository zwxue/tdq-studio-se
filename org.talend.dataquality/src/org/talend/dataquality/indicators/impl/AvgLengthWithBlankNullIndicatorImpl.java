/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.math.BigInteger;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.dataquality.indicators.AvgLengthWithBlankNullIndicator;
import org.talend.dataquality.indicators.IndicatorParameters;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.TextParameters;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Avg Length With Blank Null Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.impl.AvgLengthWithBlankNullIndicatorImpl#getSumLength <em>Sum Length</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AvgLengthWithBlankNullIndicatorImpl extends LengthIndicatorImpl implements AvgLengthWithBlankNullIndicator {

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
    protected AvgLengthWithBlankNullIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.AVG_LENGTH_WITH_BLANK_NULL_INDICATOR;
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
            eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.AVG_LENGTH_WITH_BLANK_NULL_INDICATOR__SUM_LENGTH, oldSumLength, sumLength));
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
            case IndicatorsPackage.AVG_LENGTH_WITH_BLANK_NULL_INDICATOR__SUM_LENGTH:
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
            case IndicatorsPackage.AVG_LENGTH_WITH_BLANK_NULL_INDICATOR__SUM_LENGTH:
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
            case IndicatorsPackage.AVG_LENGTH_WITH_BLANK_NULL_INDICATOR__SUM_LENGTH:
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
            case IndicatorsPackage.AVG_LENGTH_WITH_BLANK_NULL_INDICATOR__SUM_LENGTH:
                return SUM_LENGTH_EDEFAULT == null ? sumLength != null : !SUM_LENGTH_EDEFAULT.equals(sumLength);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (sumLength: ");
        result.append(sumLength);
        result.append(')');
        return result.toString();
    }

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

    @Override
    public IndicatorParameters getParameters() {
        parameters = super.getParameters();
        if (parameters == null) {
            parameters = IndicatorsFactory.eINSTANCE.createIndicatorParameters();
        }
        TextParameters textParameters = parameters.getTextParameter();
        if (textParameters == null) {
            textParameters = IndicatorsFactory.eINSTANCE.createTextParameters();
        }
        textParameters.setUseNulls(true);
        textParameters.setUseBlank(true);
        parameters.setTextParameter(textParameters);
        return parameters;
    }

    @Override
    public boolean handle(Object data) {
        boolean ok = super.handle(data);
        if (data != null && !StringUtils.isBlank(data.toString())) {
            String str = (String) data;
            sumLength += str.length();
        }
        return ok;
    }

    @Override
    public boolean reset() {
        this.sumLength = SUM_LENGTH_EDEFAULT;
        return super.reset();
    }
} // AvgLengthWithBlankNullIndicatorImpl
