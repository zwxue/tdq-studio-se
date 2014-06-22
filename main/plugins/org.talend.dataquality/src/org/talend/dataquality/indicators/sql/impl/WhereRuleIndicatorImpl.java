/**
 * <copyright> </copyright>
 * 
 * $Id$
 */
package org.talend.dataquality.indicators.sql.impl;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.talend.dataquality.helpers.IndicatorHelper;
import org.talend.dataquality.indicators.sql.IndicatorSqlPackage;
import org.talend.dataquality.indicators.sql.WhereRuleIndicator;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Where Rule Indicator</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class WhereRuleIndicatorImpl extends UserDefIndicatorImpl implements WhereRuleIndicator {

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected WhereRuleIndicatorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return IndicatorSqlPackage.Literals.WHERE_RULE_INDICATOR;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.dataquality.indicators.impl.IndicatorImpl#storeSqlResults(java.util.List)
     * 
     * ADDED xqliu 2009-02-25 storeSqlResults(List<Object[]> objects)
     */
    @Override
    public boolean storeSqlResults(List<Object[]> objects) {
        if (!checkResults(objects, 1)) {
            return false;
        }
        // MOD gdbu 2011-4-28 bug : 18975
        // Long c = Long.valueOf(String.valueOf(objects.get(0)[0]));
        Long c = IndicatorHelper.getLongFromObject(String.valueOf(objects.get(0)[0]));
        // ~18975
        this.setUserCount(c);
        return true;
    }

} // WhereRuleIndicatorImpl
