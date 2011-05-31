/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.talend.core.model.metadata.builder.connection.Connection;
import org.talend.core.model.metadata.builder.connection.DatabaseConnection;
import org.talend.cwm.helper.ColumnHelper;
import org.talend.cwm.helper.ConnectionHelper;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.PluginConstant;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.DefValueCountIndicator;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.utils.dates.DateUtils;
import org.talend.utils.sql.Java2SqlType;

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

    protected boolean isOracle = false;

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
        // MOD gdbu 2011-4-14 bug : 18975
        this.setDefaultValCount(IndicatorHelper.getLongFromObject(objects.get(0)[0]));
        // ~18975

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
        // MOD qiongli 2011-5-31 bug 21655,handle oracle date type.
        try {
            boolean isMatch = false;
            if (isOracle && data instanceof Date) {
                Date timeData = (Date) data;
                if (timeData.compareTo(DateFormat.getDateInstance().parse(defValue)) == 0) {
                    isMatch = true;
                }
            } else if (StringUtils.equals(str, defValue)) {
                isMatch = true;
            }
            if (isMatch) {
                mustStoreRow = true;
                this.defaultValCount++;
            }
        } catch (ParseException e) {
            e.printStackTrace();
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
        // MOD qiongli 2011-5-31 bug 21655,if it is oracle db,need to handel the default value.
        Connection connection = ConnectionHelper.getConnection(tdColumn);
        if (SwitchHelpers.DATABASECONNECTION_SWITCH.doSwitch(connection) != null) {
            String databaseType = ((DatabaseConnection) connection).getDatabaseType();
            if (databaseType != null && databaseType.contains("Oracle")) {
                isOracle = true;
            }
        }

        if (isOracle && defValue != null) {
            getDefValueForOracle(tdColumn);
        }
        return super.prepare();
    }

    /**
     * 
     * DOC qiongli:handle default value and get the right value.
     * 
     * @param tdColumn
     */
    protected void getDefValueForOracle(TdColumn tdColumn) {
        // MOD qiongli 2011-5-31 bug 21655,remove the redundant "\n" or "\'" and handle the date type for default value
        // on oracle db.
        defValue = StringUtils.removeEnd(defValue, PluginConstant.ENTER_STRING);
        try {
            if (Java2SqlType.isTextInSQL(tdColumn.getSqlDataType().getJavaDataType()) && defValue.length() > 1
                    && (defValue.startsWith(PluginConstant.SINGLE_QUOTE) && defValue.endsWith(PluginConstant.SINGLE_QUOTE))) {
                defValue = defValue.substring(1, defValue.length() - 1);
            } else if (Java2SqlType.isDateInSQL(tdColumn.getSqlDataType().getJavaDataType()) && defValue.startsWith("to_date(")) {
                String[] array = StringUtils.split(defValue, PluginConstant.SINGLE_QUOTE);
                if (array.length > 3) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat(array[3]);
                    defValue = simpleDateFormat.format(DateUtils.parse(array[3], array[1]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

} // DefValueCountIndicatorImpl

