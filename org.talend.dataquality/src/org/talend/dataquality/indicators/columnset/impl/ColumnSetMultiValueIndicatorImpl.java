/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.columnset.impl;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.talend.cwm.helper.SwitchHelpers;
import org.talend.cwm.relational.TdColumn;
import org.talend.dataquality.helpers.MetadataHelper;
import org.talend.dataquality.indicators.DataminingType;
import org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.impl.IndicatorImpl;
import org.talend.utils.sql.Java2SqlType;
import orgomg.cwm.resource.relational.Column;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Column Set Multi Value Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getAnalyzedColumns <em>Analyzed Columns</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getListRows <em>List Rows</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getNumericFunctions <em>Numeric Functions</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getNominalColumns <em>Nominal Columns</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getNumericColumns <em>Numeric Columns</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ColumnSetMultiValueIndicatorImpl extends IndicatorImpl implements ColumnSetMultiValueIndicator {
    private static Logger log = Logger.getLogger(ColumnSetMultiValueIndicatorImpl.class);
    /**
     * The cached value of the '{@link #getAnalyzedColumns() <em>Analyzed Columns</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAnalyzedColumns()
     * @generated
     * @ordered
     */
    protected EList<Column> analyzedColumns;

    /**
     * The default value of the '{@link #getListRows() <em>List Rows</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getListRows()
     * @generated
     * @ordered
     */
    protected static final List<Object[]> LIST_ROWS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getListRows() <em>List Rows</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getListRows()
     * @generated
     * @ordered
     */
    protected List<Object[]> listRows = LIST_ROWS_EDEFAULT;

    /**
     * The cached value of the '{@link #getNumericFunctions() <em>Numeric Functions</em>}' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getNumericFunctions()
     * @generated
     * @ordered
     */
    protected EList<String> numericFunctions;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ColumnSetMultiValueIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ColumnsetPackage.Literals.COLUMN_SET_MULTI_VALUE_INDICATOR;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<Column> getAnalyzedColumns() {
        if (analyzedColumns == null) {
            analyzedColumns = new EObjectResolvingEList<Column>(Column.class, this, ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__ANALYZED_COLUMNS);
        }
        return analyzedColumns;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public List<Object[]> getListRows() {
        return listRows;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setListRows(List<Object[]> newListRows) {
        List<Object[]> oldListRows = listRows;
        listRows = newListRows;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__LIST_ROWS, oldListRows, listRows));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<String> getNumericFunctions() {
        if (numericFunctions == null) {
            numericFunctions = new EDataTypeUniqueEList<String>(String.class, this, ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__NUMERIC_FUNCTIONS);
        }
        return numericFunctions;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public EList<Column> getNominalColumns() {
        EList<Column> nominalColumns = new BasicEList<Column>();
        for (Column column : analyzedColumns) {
            final TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(column);
            final DataminingType dmType = MetadataHelper.getDataminingType(tdColumn);
            if (DataminingType.NOMINAL.equals(dmType)) {
                nominalColumns.add(column);
            } 
        }
        return nominalColumns;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public EList<Column> getNumericColumns() {
        EList<Column> computedColumns = new BasicEList<Column>();
        for (Column column : analyzedColumns) {
            final TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(column);
            final DataminingType dmType = MetadataHelper.getDataminingType(tdColumn);
            if (DataminingType.INTERVAL.equals(dmType) && Java2SqlType.isNumbericInSQL(tdColumn.getJavaType())) {
                computedColumns.add(tdColumn);
            }
        }
        return computedColumns;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#storeSqlResults(java.util.List)
     * 
     * @generated NOT
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        this.setListRows(objects);
        if (log.isDebugEnabled()) {
            StringBuilder builder = new StringBuilder();
            builder.append("Column set multivalue indicator\n");
            for (Object[] obj : objects) {
                for (int i = 0; i < obj.length; i++) {
                    builder.append(obj[i]).append("\t");
                }
                builder.append('\n');
            }
            log.debug(builder);
        }
        return true;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__ANALYZED_COLUMNS:
                return getAnalyzedColumns();
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__LIST_ROWS:
                return getListRows();
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__NUMERIC_FUNCTIONS:
                return getNumericFunctions();
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__NOMINAL_COLUMNS:
                return getNominalColumns();
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__NUMERIC_COLUMNS:
                return getNumericColumns();
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
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__ANALYZED_COLUMNS:
                getAnalyzedColumns().clear();
                getAnalyzedColumns().addAll((Collection<? extends Column>)newValue);
                return;
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__LIST_ROWS:
                setListRows((List<Object[]>)newValue);
                return;
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__NUMERIC_FUNCTIONS:
                getNumericFunctions().clear();
                getNumericFunctions().addAll((Collection<? extends String>)newValue);
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
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__ANALYZED_COLUMNS:
                getAnalyzedColumns().clear();
                return;
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__LIST_ROWS:
                setListRows(LIST_ROWS_EDEFAULT);
                return;
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__NUMERIC_FUNCTIONS:
                getNumericFunctions().clear();
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
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__ANALYZED_COLUMNS:
                return analyzedColumns != null && !analyzedColumns.isEmpty();
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__LIST_ROWS:
                return LIST_ROWS_EDEFAULT == null ? listRows != null : !LIST_ROWS_EDEFAULT.equals(listRows);
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__NUMERIC_FUNCTIONS:
                return numericFunctions != null && !numericFunctions.isEmpty();
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__NOMINAL_COLUMNS:
                return !getNominalColumns().isEmpty();
            case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__NUMERIC_COLUMNS:
                return !getNumericColumns().isEmpty();
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
        result.append(" (listRows: ");
        result.append(listRows);
        result.append(", numericFunctions: ");
        result.append(numericFunctions);
        result.append(')');
        return result.toString();
    }

} //ColumnSetMultiValueIndicatorImpl
