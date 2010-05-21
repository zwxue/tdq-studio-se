/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.columnset;

import org.eclipse.emf.common.util.EList;
import org.talend.dataquality.indicators.RegexpMatchingIndicator;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>All Match Indicator</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.talend.dataquality.indicators.columnset.ColumnsetPackage#getAllMatchIndicator()
 * @model
 * @generated
 */
public interface AllMatchIndicator extends ColumnSetMultiValueIndicator, RegexpMatchingIndicator {

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model kind="operation" many="false"
     * @generated
     */
    EList<RegexpMatchingIndicator> getCompositeRegexMatchingIndicators();
} // AllMatchIndicator
