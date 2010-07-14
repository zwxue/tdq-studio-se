/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.indicators.DefValueCountIndicator;
import org.talend.dataquality.indicators.IndicatorsPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Def Value Count Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.impl.DefValueCountIndicatorImpl#getDefaultValCount <em>Default Val Count</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DefValueCountIndicatorImpl extends IndicatorImpl implements DefValueCountIndicator {

    /**
     * The default value of the '{@link #getDefaultValCount() <em>Default Val Count</em>}' attribute. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getDefaultValCount()
     * @generated
     * @ordered
     */
    protected static final Long DEFAULT_VAL_COUNT_EDEFAULT = new Long(0L);

    /**
	 * The cached value of the '{@link #getDefaultValCount() <em>Default Val Count</em>}' attribute.
	 * <!-- begin-user-doc
     * --> <!-- end-user-doc -->
	 * @see #getDefaultValCount()
	 * @generated
	 * @ordered
	 */
    protected Long defaultValCount = DEFAULT_VAL_COUNT_EDEFAULT;

    protected String defValue = null;

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
    protected DefValueCountIndicatorImpl() {
		super();
	}

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    protected EClass eStaticClass() {
		return IndicatorsPackage.Literals.DEF_VALUE_COUNT_INDICATOR;
	}

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
    public Long getDefaultValCount() {
		return defaultValCount;
	}

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
    public void setDefaultValCount(Long newDefaultValCount) {
		Long oldDefaultValCount = defaultValCount;
		defaultValCount = newDefaultValCount;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, IndicatorsPackage.DEF_VALUE_COUNT_INDICATOR__DEFAULT_VAL_COUNT, oldDefaultValCount, defaultValCount));
	}

    /**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * @generated
	 */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case IndicatorsPackage.DEF_VALUE_COUNT_INDICATOR__DEFAULT_VAL_COUNT:
				return getDefaultValCount();
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
			case IndicatorsPackage.DEF_VALUE_COUNT_INDICATOR__DEFAULT_VAL_COUNT:
				setDefaultValCount((Long)newValue);
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
			case IndicatorsPackage.DEF_VALUE_COUNT_INDICATOR__DEFAULT_VAL_COUNT:
				setDefaultValCount(DEFAULT_VAL_COUNT_EDEFAULT);
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
			case IndicatorsPackage.DEF_VALUE_COUNT_INDICATOR__DEFAULT_VAL_COUNT:
				return DEFAULT_VAL_COUNT_EDEFAULT == null ? defaultValCount != null : !DEFAULT_VAL_COUNT_EDEFAULT.equals(defaultValCount);
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
		result.append(" (defaultValCount: ");
		result.append(defaultValCount);
		result.append(')');
		return result.toString();
	}

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#storeSqlResults(java.util.List)
     * 
     * ADDED scorreia 2009-01-28 storeSqlResults(List<Object[]> objects)
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        if (!super.checkResults(objects, 1)) {
            return false;
        }
        Long c = Long.valueOf(String.valueOf(objects.get(0)[0]));
        this.setDefaultValCount(c);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#getIntegerValue()
     * 
     * ADDED scorreia 2009-04-28 getIntegerValue()
     */
    @Override
    public Long getIntegerValue() {
        return this.getDefaultValCount();
    }

    @Override
    public boolean reset() {
        this.defaultValCount = DEFAULT_VAL_COUNT_EDEFAULT;
        return super.reset();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#handle(java.lang.Object)
     * 
     * ADD yyi 2009-09-02 prepare()
     */
    @Override
    public boolean handle(Object data) {
        mustStoreRow = false;
        boolean ok = super.handle(data);
        if (data == null) {
            return ok;
        }

        String str = data.toString();
        if (StringUtils.equals(str, this.defValue)) {
            mustStoreRow = true;
            this.defaultValCount++;
        }

        return ok;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#prepare()
     * 
     * ADD yyi 2009-09-02 prepare()
     */
    @Override
    public boolean prepare() {
        TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(this.getAnalyzedElement());
        this.defValue = ColumnHelper.getDefaultValue(tdColumn);
        return super.prepare();
    }

} // DefValueCountIndicatorImpl
