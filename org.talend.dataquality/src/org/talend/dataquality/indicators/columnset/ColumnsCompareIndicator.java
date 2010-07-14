/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.talend.dataquality.indicators.columnset;

import org.eclipse.emf.common.util.EList;
import org.talend.dataquality.indicators.MatchingIndicator;
import orgomg.cwm.resource.relational.Column;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Columns Compare Indicator</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.talend.dataquality.indicators.columnset.ColumnsCompareIndicator#getColumnSetA <em>Column Set A</em>}</li>
 *   <li>{@link org.talend.dataquality.indicators.columnset.ColumnsCompareIndicator#getColumnSetB <em>Column Set B</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.talend.dataquality.indicators.columnset.ColumnsetPackage#getColumnsCompareIndicator()
 * @model abstract="true"
 * @generated
 */
public interface ColumnsCompareIndicator extends MatchingIndicator {
    /**
	 * Returns the value of the '<em><b>Column Set A</b></em>' reference list.
	 * The list contents are of type {@link orgomg.cwm.resource.relational.Column}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Column Set A</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Column Set A</em>' reference list.
	 * @see org.talend.dataquality.indicators.columnset.ColumnsetPackage#getColumnsCompareIndicator_ColumnSetA()
	 * @model
	 * @generated
	 */
    EList<Column> getColumnSetA();

    /**
	 * Returns the value of the '<em><b>Column Set B</b></em>' reference list.
	 * The list contents are of type {@link orgomg.cwm.resource.relational.Column}.
	 * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Column Set B</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
	 * @return the value of the '<em>Column Set B</em>' reference list.
	 * @see org.talend.dataquality.indicators.columnset.ColumnsetPackage#getColumnsCompareIndicator_ColumnSetB()
	 * @model
	 * @generated
	 */
    EList<Column> getColumnSetB();

} // ColumnsCompareIndicator
