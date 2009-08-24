/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.indicators.IndicatorsPackage;
import org.talend.dataquality.indicators.UpperQuartileIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Upper Quartile Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class UpperQuartileIndicatorImpl extends MaxValueIndicatorImpl implements UpperQuartileIndicator {

    private static Logger log = Logger.getLogger(UpperQuartileIndicatorImpl.class);

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected UpperQuartileIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorsPackage.Literals.UPPER_QUARTILE_INDICATOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#storeSqlResults(java.util.List)
     * 
     * ADDED scorreia 2008-12-01 storeSqlResults(List<Object[]> objects)
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        if (!checkResults(objects, 1)) {
            return false;
        }

        // get the correct type of result from the analyzed element
        int javaType = this.getColumnType();
        this.setDatatype(javaType);

        if (objects.size() == 1) { // case when 1 row is returned
            String med = String.valueOf(objects.get(0)[0]);
            if (med == null) {
                log.error("Upper quartile is null!!");
                return false;
            }
            this.setValue(String.valueOf(MedianIndicatorImpl.getRealValue(javaType, med)));
            return true;
        } else if (objects.size() == 2) { // case when 2 rows are returned
            Double r1 = MedianIndicatorImpl.getRealValue(javaType, String.valueOf(objects.get(0)[0]));
            Double r2 = MedianIndicatorImpl.getRealValue(javaType, String.valueOf(objects.get(1)[0]));
            if (r1 == null || r2 == null) {
                log.error("Cannot compute the upper quartile: At least one of the rows is null: " + r1 + " | " + r2);
                return false;
            }
            this.setValue(String.valueOf((r1 + r2) / 2));
            return true;
        }
        return false;
    }

} // UpperQuartileIndicatorImpl
