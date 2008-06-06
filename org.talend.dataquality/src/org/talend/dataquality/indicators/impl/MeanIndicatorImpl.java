package org.talend.dataquality.indicators.impl;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.MeanIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Mean Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class MeanIndicatorImpl extends SumIndicatorImpl implements MeanIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected MeanIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.MEAN_INDICATOR;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public Double getMean() {
        Long c = getCount();
        // if (super.genericSum == null) {// TODO scorreia check that this work
        if (c.compareTo(0L) == 0) {
            throw new RuntimeException("Invalid mean!!");
        }
        Double sum = Double.valueOf(getSumStr());
        if (sum == null) {
            throw new RuntimeException("Invalid sum in mean computation!!");
        }
        return sum / c;
        // throw new RuntimeException("Problem when computing mean!!");
        // }
        //
        // Double mean = super.genericSum.getMean(c);
        // if (mean == null) {
        // throw new RuntimeException("Invalid mean!!");
        // }
        // return mean;
    }

    /*
     * ADDED
     * 
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IntegerSumIndicatorImpl#handle(java.lang.Object)
     */
    @Override
    public boolean handle(Object data) {
        boolean handled = super.handle(data);
        return handled;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public Double getMeanWithNulls(double valueForNull) {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#storeSqlResults(java.util.List)
     * 
     * ADDED scorreia 2008-05-02 storeSqlResults(List<Object[]> objects)
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        if (!checkResults(objects, 2)) {
            return false;
        }
        String s = String.valueOf(objects.get(0)[0]);
        String c = String.valueOf(objects.get(0)[1]);
        this.setSumStr(s);
        this.setCount(Long.valueOf(c));
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * ADDED
     * 
     * @see org.talend.dataquality.indicators.impl.SumIndicatorImpl#toString()
     */
    @Override
    public String toString() {
        return "Mean = " + getMean();
    }
} // MeanIndicatorImpl
