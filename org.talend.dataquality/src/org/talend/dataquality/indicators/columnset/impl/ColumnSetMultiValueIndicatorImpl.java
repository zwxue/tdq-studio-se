/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.columnset.impl;

import java.text.MessageFormat;
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
import org.talend.dataquality.indicators.impl.CompositeIndicatorImpl;
import org.talend.utils.sql.Java2SqlType;
import orgomg.cwm.resource.relational.Column;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Column Set Multi Value Indicator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getAnalyzedColumns <em>
 * Analyzed Columns</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getListRows <em>List
 * Rows</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getNumericFunctions <em>
 * Numeric Functions</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getNominalColumns <em>
 * Nominal Columns</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getNumericColumns <em>
 * Numeric Columns</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getColumnHeaders <em>
 * Column Headers</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getDateFunctions <em>
 * Date Functions</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getDateColumns <em>Date
 * Columns</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getUniqueCount <em>
 * Unique Count</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getDistinctCount <em>
 * Distinct Count</em>}</li>
 * <li>{@link org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#getDuplicateCount <em>
 * Duplicate Count</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ColumnSetMultiValueIndicatorImpl extends CompositeIndicatorImpl implements ColumnSetMultiValueIndicator {

    private static Logger log = Logger.getLogger(ColumnSetMultiValueIndicatorImpl.class);

    /**
     * The cached value of the '{@link #getAnalyzedColumns() <em>Analyzed Columns</em>}' reference list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getAnalyzedColumns()
     * @generated
     * @ordered
     */
    protected EList<Column> analyzedColumns;

    /**
     * The default value of the '{@link #getListRows() <em>List Rows</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getListRows()
     * @generated
     * @ordered
     */
    protected static final List<Object[]> LIST_ROWS_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getListRows() <em>List Rows</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getListRows()
     * @generated
     * @ordered
     */
    protected List<Object[]> listRows = LIST_ROWS_EDEFAULT;

    /**
     * The cached value of the '{@link #getNumericFunctions() <em>Numeric Functions</em>}' attribute list. <!--
     * begin-user-doc --> <!-- end-user-doc -->
     * 
     * @see #getNumericFunctions()
     * @generated
     * @ordered
     */
    protected EList<String> numericFunctions;

    /**
     * The cached value of the '{@link #getDateFunctions() <em>Date Functions</em>}' attribute list. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getDateFunctions()
     * @generated
     * @ordered
     */
    protected EList<String> dateFunctions;

    /**
     * The cached value of the '{@link #getDateColumns() <em>Date Columns</em>}' reference list. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getDateColumns()
     * @generated
     * @ordered
     */
    protected EList<Column> dateColumns;

    /**
     * The default value of the '{@link #getUniqueCount() <em>Unique Count</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getUniqueCount()
     * @generated
     * @ordered
     */
    protected static final Long UNIQUE_COUNT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUniqueCount() <em>Unique Count</em>}' attribute. <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * 
     * @see #getUniqueCount()
     * @generated
     * @ordered
     */
    protected Long uniqueCount = UNIQUE_COUNT_EDEFAULT;

    /**
     * The default value of the '{@link #getDistinctCount() <em>Distinct Count</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getDistinctCount()
     * @generated
     * @ordered
     */
    protected static final Long DISTINCT_COUNT_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDistinctCount() <em>Distinct Count</em>}' attribute. <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * 
     * @see #getDistinctCount()
     * @generated
     * @ordered
     */
    protected Long distinctCount = DISTINCT_COUNT_EDEFAULT;

    /**
     * The default value of the '{@link #getDuplicateCount() <em>Duplicate Count</em>}' attribute. <!-- begin-user-doc
     * --> <!-- end-user-doc -->
     * 
     * @see #getDuplicateCount()
     * @generated
     * @ordered
     */
    protected static final Long DUPLICATE_COUNT_EDEFAULT = null;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected ColumnSetMultiValueIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ColumnsetPackage.Literals.COLUMN_SET_MULTI_VALUE_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList<Column> getAnalyzedColumns() {
        if (analyzedColumns == null) {
            analyzedColumns = new EObjectResolvingEList<Column>(Column.class, this,
                    ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__ANALYZED_COLUMNS);
        }
        return analyzedColumns;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public List<Object[]> getListRows() {
        return listRows;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setListRows(List<Object[]> newListRows) {
        List<Object[]> oldListRows = listRows;
        listRows = newListRows;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__LIST_ROWS,
                    oldListRows, listRows));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList<String> getNumericFunctions() {
        if (numericFunctions == null) {
            numericFunctions = new EDataTypeUniqueEList<String>(String.class, this,
                    ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__NUMERIC_FUNCTIONS);
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
        if (analyzedColumns != null) {
            for (Column column : analyzedColumns) {
                final TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(column);
                final DataminingType dmType = MetadataHelper.getDataminingType(tdColumn);
                if (DataminingType.NOMINAL.equals(dmType)) {
                    nominalColumns.add(column);
                }
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
        if (analyzedColumns != null) {
            for (Column column : analyzedColumns) {
                final TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(column);
                final DataminingType dmType = MetadataHelper.getDataminingType(tdColumn);
                if (DataminingType.INTERVAL.equals(dmType) && Java2SqlType.isNumbericInSQL(tdColumn.getJavaType())) {
                    computedColumns.add(tdColumn);
                }
            }
        }
        return computedColumns;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public EList<String> getColumnHeaders() {
        EList<String> headers = new BasicEList<String>();
        for (Column column : this.getNominalColumns()) {
            headers.add(column.getName());
        }
        for (Column column : this.getNumericColumns()) {
            // call functions for each column
            for (String f : this.getNumericFunctions()) {
                headers.add(MessageFormat.format(f, column.getName()));
            }
        }
        for (Column column : this.getDateColumns()) {
            // call functions for each column
            for (String f : this.getDateFunctions()) {
                headers.add(MessageFormat.format(f, column.getName()));
            }
        }
        headers.add(this.getCountAll());
        return headers;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public EList<String> getDateFunctions() {
        if (dateFunctions == null) {
            dateFunctions = new EDataTypeUniqueEList<String>(String.class, this,
                    ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DATE_FUNCTIONS);
        }
        return dateFunctions;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public EList<Column> getDateColumns() {
        EList<Column> dateColumns = new BasicEList<Column>();
        if (analyzedColumns != null) {
            for (Column column : analyzedColumns) {
                final TdColumn tdColumn = SwitchHelpers.COLUMN_SWITCH.doSwitch(column);
                final DataminingType dmType = MetadataHelper.getDataminingType(tdColumn);
                if (DataminingType.INTERVAL.equals(dmType) && Java2SqlType.isDateInSQL(tdColumn.getJavaType())) {
                    dateColumns.add(tdColumn);
                }
            }
        }
        return dateColumns;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Long getUniqueCount() {
        return uniqueCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setUniqueCount(Long newUniqueCount) {
        Long oldUniqueCount = uniqueCount;
        uniqueCount = newUniqueCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__UNIQUE_COUNT, oldUniqueCount, uniqueCount));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public Long getDistinctCount() {
        return distinctCount;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    public void setDistinctCount(Long newDistinctCount) {
        Long oldDistinctCount = distinctCount;
        distinctCount = newDistinctCount;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DISTINCT_COUNT, oldDistinctCount, distinctCount));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Long getDuplicateCount() {
        return (uniqueCount != null && distinctCount != null) ? distinctCount - uniqueCount : null;
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

        computeCounts(objects);
        return true;
    }

    /**
     * DOC scorreia Comment method "computeUniqueCount".
     * 
     * @param objects
     * @return
     */
    private boolean computeCounts(List<Object[]> objects) {
        boolean ok = true;
        this.setDistinctCount(Long.valueOf(objects.size()));
        Long uniq = 0L;
        Long rowcount = 0L;
        for (Object[] row : objects) {
            Object c = row[row.length - 1];
            if (c != null) {
                Long val = Long.valueOf(String.valueOf(c));
                rowcount += val;
                if (val == 1) {
                    uniq++;
                }
            } else {
                ok = false;
            }
        }
        this.setCount(rowcount);
        this.setUniqueCount(uniq);
        return ok;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
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
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__COLUMN_HEADERS:
            return getColumnHeaders();
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DATE_FUNCTIONS:
            return getDateFunctions();
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DATE_COLUMNS:
            return getDateColumns();
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__UNIQUE_COUNT:
            return getUniqueCount();
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DISTINCT_COUNT:
            return getDistinctCount();
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DUPLICATE_COUNT:
            return getDuplicateCount();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__ANALYZED_COLUMNS:
            getAnalyzedColumns().clear();
            getAnalyzedColumns().addAll((Collection<? extends Column>) newValue);
            return;
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__LIST_ROWS:
            setListRows((List<Object[]>) newValue);
            return;
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__NUMERIC_FUNCTIONS:
            getNumericFunctions().clear();
            getNumericFunctions().addAll((Collection<? extends String>) newValue);
            return;
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DATE_FUNCTIONS:
            getDateFunctions().clear();
            getDateFunctions().addAll((Collection<? extends String>) newValue);
            return;
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DATE_COLUMNS:
            getDateColumns().clear();
            getDateColumns().addAll((Collection<? extends Column>) newValue);
            return;
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__UNIQUE_COUNT:
            setUniqueCount((Long) newValue);
            return;
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DISTINCT_COUNT:
            setDistinctCount((Long) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
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
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DATE_FUNCTIONS:
            getDateFunctions().clear();
            return;
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DATE_COLUMNS:
            getDateColumns().clear();
            return;
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__UNIQUE_COUNT:
            setUniqueCount(UNIQUE_COUNT_EDEFAULT);
            return;
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DISTINCT_COUNT:
            setDistinctCount(DISTINCT_COUNT_EDEFAULT);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
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
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__COLUMN_HEADERS:
            return !getColumnHeaders().isEmpty();
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DATE_FUNCTIONS:
            return dateFunctions != null && !dateFunctions.isEmpty();
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DATE_COLUMNS:
            return dateColumns != null && !dateColumns.isEmpty();
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__UNIQUE_COUNT:
            return UNIQUE_COUNT_EDEFAULT == null ? uniqueCount != null : !UNIQUE_COUNT_EDEFAULT.equals(uniqueCount);
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DISTINCT_COUNT:
            return DISTINCT_COUNT_EDEFAULT == null ? distinctCount != null : !DISTINCT_COUNT_EDEFAULT.equals(distinctCount);
        case ColumnsetPackage.COLUMN_SET_MULTI_VALUE_INDICATOR__DUPLICATE_COUNT:
            return DUPLICATE_COUNT_EDEFAULT == null ? getDuplicateCount() != null : !DUPLICATE_COUNT_EDEFAULT
                    .equals(getDuplicateCount());
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (listRows: ");
        result.append(listRows);
        result.append(", numericFunctions: ");
        result.append(numericFunctions);
        result.append(", dateFunctions: ");
        result.append(dateFunctions);
        result.append(", uniqueCount: ");
        result.append(uniqueCount);
        result.append(", distinctCount: ");
        result.append(distinctCount);
        result.append(')');
        return result.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.columnset.ColumnSetMultiValueIndicator#getCountAll()
     * 
     * @generated NOT getCountAll()
     */
    public String getCountAll() {
        return "COUNT(*)";
    }
} // ColumnSetMultiValueIndicatorImpl
