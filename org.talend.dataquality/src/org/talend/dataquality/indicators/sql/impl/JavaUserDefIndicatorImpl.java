/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.sql.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.talend.dataquality.analysis.ExecutionLanguage;
import org.talend.dataquality.indicators.IndicatorValueType;
import org.talend.dataquality.indicators.sql.IndicatorSqlPackage;
import org.talend.dataquality.indicators.sql.JavaUserDefIndicator;
import org.talend.dataquality.indicators.sql.UserDefIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Java User Def Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class JavaUserDefIndicatorImpl extends UserDefIndicatorImpl implements JavaUserDefIndicator {

    private UserDefIndicator javaUDI = null;

    private ExecutionLanguage executionLanguage = ExecutionLanguage.JAVA;

    private boolean isJavaEngine() {
        if (executionLanguage == ExecutionLanguage.JAVA) {
            return true;
        }
        return false;
    }

    @Override
    public boolean finalizeComputation() {
        if (isJavaEngine()) {
            return javaUDI.finalizeComputation();
        }
        return super.finalizeComputation();

    }

    @Override
    public boolean handle(Object data) {
        if (isJavaEngine()) {
            return javaUDI.handle(data);
        }
        return super.handle(data);
    }

    @Override
    public boolean prepare() {
        if (isJavaEngine()) {
            // copy parameters of the judi as set by the user in the UI
            javaUDI.setParameters(EcoreUtil.copy(getParameters()));
            return javaUDI.prepare();
        }
        return super.prepare();
    }

    @Override
    public boolean reset() {
        if (isJavaEngine()) {
            return javaUDI.reset();
        }
        return super.reset();
    }

    @Override
    public Long getCount(Object dataValue) {
        if (isJavaEngine()) {
            return javaUDI.getCount(dataValue);
        }
        return super.getCount(dataValue);
    }

    @Override
    public int getDatatype() {
        if (isJavaEngine()) {
            return javaUDI.getDatatype();
        }
        return super.getDatatype();

    }

    @Override
    public Long getDistinctValueCount() {
        if (isJavaEngine()) {
            return javaUDI.getDistinctValueCount();
        }
        return super.getDistinctValueCount();
    }

    @Override
    public Set<Object> getDistinctValues() {
        if (isJavaEngine()) {
            return javaUDI.getDistinctValues();
        }
        return super.getDistinctValues();
    }

    @Override
    public Long getDuplicateValueCount() {
        if (isJavaEngine()) {
            return javaUDI.getDuplicateValueCount();
        }
        return super.getDuplicateValueCount();
    }

    @Override
    public Double getFrequency(Object dataValue) {
        if (isJavaEngine()) {
            return javaUDI.getFrequency(dataValue);
        }
        return super.getFrequency(dataValue);
    }

    @Override
    public Long getIntegerValue() {
        if (isJavaEngine()) {
            return javaUDI.getIntegerValue();
        }
        return super.getIntegerValue();
    }

    @Override
    public Long getMatchingValueCount() {
        if (isJavaEngine()) {
            return javaUDI.getMatchingValueCount();
        }
        return super.getMatchingValueCount();
    }

    @Override
    public Long getNotMatchingValueCount() {
        if (isJavaEngine()) {
            return javaUDI.getNotMatchingValueCount();
        }
        return super.getNotMatchingValueCount();
    }

    @Override
    public Double getRealValue() {
        if (isJavaEngine()) {
            return javaUDI.getRealValue();
        }
        return super.getRealValue();
    }

    @Override
    public Long getUniqueValueCount() {
        if (isJavaEngine()) {
            return javaUDI.getUniqueValueCount();
        }
        return super.getUniqueValueCount();
    }

    @Override
    public EList<Object> getUniqueValues() {
        if (isJavaEngine()) {
            return javaUDI.getUniqueValues();
        }
        return super.getUniqueValues();
    }

    @Override
    public Long getUserCount() {
        if (isJavaEngine()) {
            return javaUDI.getUserCount();
        }
        return super.getUserCount();
    }

    @Override
    public String getValue() {
        if (isJavaEngine()) {
            return javaUDI.getValue();
        }
        return super.getValue();
    }

    @Override
    public HashMap<Object, Long> getValueToFreq() {
        if (isJavaEngine()) {
            return javaUDI.getValueToFreq();
        }
        return super.getValueToFreq();
    }

    @Override
    public IndicatorValueType getValueType() {
        if (isJavaEngine()) {
            return javaUDI.getValueType();
        }
        return super.getValueType();
    }

    @Override
    public void setDatatype(int newDatatype) {
        if (isJavaEngine()) {
            javaUDI.setDatatype(newDatatype);
        } else {
            super.setDatatype(newDatatype);
        }
    }

    @Override
    public void setDistinctValueCount(Long newDistinctValueCount) {
        if (isJavaEngine()) {
            javaUDI.setDistinctValueCount(newDistinctValueCount);
        } else {
            super.setDistinctValueCount(newDistinctValueCount);
        }

    }

    @Override
    public void setMatchingValueCount(Long newMatchingValueCount) {
        if (isJavaEngine()) {
            javaUDI.setMatchingValueCount(newMatchingValueCount);
        } else {
            super.setMatchingValueCount(newMatchingValueCount);
        }
    }

    @Override
    public void setNotMatchingValueCount(Long newNotMatchingValueCount) {
        if (isJavaEngine()) {
            javaUDI.setNotMatchingValueCount(newNotMatchingValueCount);
        } else {
            super.setNotMatchingValueCount(newNotMatchingValueCount);
        }
    }

    @Override
    public void setUniqueValueCount(Long newUniqueValueCount) {
        if (isJavaEngine()) {
            javaUDI.setUniqueValueCount(newUniqueValueCount);
        } else {
            super.setUniqueValueCount(newUniqueValueCount);
        }
    }

    @Override
    public void setUserCount(Long newUserCount) {
        if (isJavaEngine()) {
            javaUDI.setUserCount(newUserCount);
        } else {
            super.setUserCount(newUserCount);
        }

    }

    @Override
    public void setValue(String newValue) {
        if (isJavaEngine()) {
            javaUDI.setValue(newValue);
        } else {
            super.setValue(newValue);
        }

    }

    @Override
    public void setValueToFreq(HashMap<Object, Long> newValueToFreq) {
        if (isJavaEngine()) {
            javaUDI.setValueToFreq(newValueToFreq);
        } else {
            super.setValueToFreq(newValueToFreq);
        }
    }

    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        if (isJavaEngine()) {
            return javaUDI.storeSqlResults(objects);
        }
        return super.storeSqlResults(objects);
    }

    @Override
    public boolean isComputed() {
        return super.isComputed();
    }

    @Override
    public void setComputed(boolean newComputed) {
        super.setComputed(newComputed);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected JavaUserDefIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorSqlPackage.Literals.JAVA_USER_DEF_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void setJavaUserDefObject(UserDefIndicator javaUDIObj) {
        this.javaUDI = javaUDIObj;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public void setExecuteEngine(ExecutionLanguage executionLanguage) {
        this.executionLanguage = executionLanguage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#getCount()
     */
    @Override
    public Long getCount() {
        if (isJavaEngine()) {
            return javaUDI.getCount();
        }
        return super.getCount();
    }

} // JavaUserDefIndicatorImpl
