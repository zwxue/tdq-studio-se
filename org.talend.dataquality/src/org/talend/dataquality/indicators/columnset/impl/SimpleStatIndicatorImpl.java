/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.columnset.impl;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsFactory;
import org.talend.dataquality.indicators.columnset.ColumnsetPackage;
import org.talend.dataquality.indicators.columnset.SimpleStatIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Simple Stat Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class SimpleStatIndicatorImpl extends ColumnSetMultiValueIndicatorImpl implements SimpleStatIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected SimpleStatIndicatorImpl() {
        super();
        IndicatorsFactory factory = IndicatorsFactory.eINSTANCE;
        setRowCountIndicator(factory.createRowCountIndicator());
        setUniqueCountIndicator(factory.createUniqueCountIndicator());
        setDistinctCountIndicator(factory.createDistinctCountIndicator());
        setDuplicateCountIndicator(factory.createDuplicateCountIndicator());
        rowCountIndicator.setName("Row Count");
        uniqueCountIndicator.setName("Unique Count");
        distinctCountIndicator.setName("Distinct Count");
        duplicateCountIndicator.setName("Duplicate Count");
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ColumnsetPackage.Literals.SIMPLE_STAT_INDICATOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#setComputed(boolean)
     */
    @Override
    public void setComputed(boolean newComputed) {
        super.setComputed(newComputed);
        getRowCountIndicator().setComputed(newComputed);
        getUniqueCountIndicator().setComputed(newComputed);
        getDistinctCountIndicator().setComputed(newComputed);
        getDuplicateCountIndicator().setComputed(newComputed);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.dataquality.indicators.columnset.impl.ColumnSetMultiValueIndicatorImpl#storeSqlResults(java.util.List)
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        boolean r = super.storeSqlResults(objects);
        getRowCountIndicator().setCount(getCount());
        getUniqueCountIndicator().setUniqueValueCount(getUniqueCount());
        getDistinctCountIndicator().setDistinctValueCount(getDistinctCount());
        getDuplicateCountIndicator().setDuplicateValueCount(getDuplicateCount());
        return r;
    }

} // SimpleStatIndicatorImpl
