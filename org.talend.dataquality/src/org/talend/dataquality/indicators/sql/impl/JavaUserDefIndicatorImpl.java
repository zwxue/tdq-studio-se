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

    @Override
    public boolean finalizeComputation() {
        return javaUDI.finalizeComputation();
    }

    @Override
    public boolean handle(Object data) {
        return javaUDI.handle(data);
    }

    @Override
    public boolean prepare() {
        return javaUDI.prepare();
    }

    @Override
    public boolean reset() {
        return javaUDI.reset();
    }

    @Override
    public Long getCount(Object dataValue) {

        return javaUDI.getCount(dataValue);
    }

    @Override
    public int getDatatype() {

        return javaUDI.getDatatype();
    }

    @Override
    public Long getDistinctValueCount() {

        return javaUDI.getDistinctValueCount();
    }

    @Override
    public Set<Object> getDistinctValues() {

        return javaUDI.getDistinctValues();
    }

    @Override
    public Long getDuplicateValueCount() {

        return javaUDI.getDuplicateValueCount();
    }

    @Override
    public Double getFrequency(Object dataValue) {

        return javaUDI.getFrequency(dataValue);
    }

    @Override
    public Long getIntegerValue() {

        return javaUDI.getIntegerValue();
    }

    @Override
    public Long getMatchingValueCount() {

        return javaUDI.getMatchingValueCount();
    }

    @Override
    public Long getNotMatchingValueCount() {

        return javaUDI.getNotMatchingValueCount();
    }

    @Override
    public Double getRealValue() {

        return javaUDI.getRealValue();
    }

    @Override
    public Long getUniqueValueCount() {

        return javaUDI.getUniqueValueCount();
    }

    @Override
    public EList<Object> getUniqueValues() {

        return javaUDI.getUniqueValues();
    }

    @Override
    public Long getUserCount() {

        return javaUDI.getUserCount();
    }

    @Override
    public String getValue() {

        return javaUDI.getValue();
    }

    @Override
    public HashMap<Object, Long> getValueToFreq() {

        return javaUDI.getValueToFreq();
    }

    @Override
    public IndicatorValueType getValueType() {

        return javaUDI.getValueType();
    }

    @Override
    public void setDatatype(int newDatatype) {

        javaUDI.setDatatype(newDatatype);
    }

    @Override
    public void setDistinctValueCount(Long newDistinctValueCount) {

        javaUDI.setDistinctValueCount(newDistinctValueCount);
    }

    @Override
    public void setMatchingValueCount(Long newMatchingValueCount) {

        javaUDI.setMatchingValueCount(newMatchingValueCount);
    }

    @Override
    public void setNotMatchingValueCount(Long newNotMatchingValueCount) {

        javaUDI.setNotMatchingValueCount(newNotMatchingValueCount);
    }

    @Override
    public void setUniqueValueCount(Long newUniqueValueCount) {

        javaUDI.setUniqueValueCount(newUniqueValueCount);
    }

    @Override
    public void setUserCount(Long newUserCount) {

        javaUDI.setUserCount(newUserCount);
    }

    @Override
    public void setValue(String newValue) {

        javaUDI.setValue(newValue);
    }

    @Override
    public void setValueToFreq(HashMap<Object, Long> newValueToFreq) {

        javaUDI.setValueToFreq(newValueToFreq);
    }

    @Override
    public boolean storeSqlResults(List<Object[]> objects) {

        return javaUDI.storeSqlResults(objects);
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

} // JavaUserDefIndicatorImpl
