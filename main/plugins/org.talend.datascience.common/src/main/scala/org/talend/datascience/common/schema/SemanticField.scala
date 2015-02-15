// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.datascience.common.schema

import org.talend.datascience.common.statistics.NumericalFieldStatistics
import org.talend.datascience.common.statistics.TextualFieldStatistics
import org.talend.datascience.common.statistics.FieldStatistics

/**
 * A field with senmatic information.
 * @author mzhao
 */
trait SemanticField extends Field[Any] {
  var semanticName: String
  /**
   * A sample with typical uniform distributed values.
   */
  val sampleValues: Seq[String] = Seq()
  /**
   * Numerical statistics.
   */
  var numericalStatistics = new NumericalFieldStatistics
  /**
   * Textual statistics.
   */
  var textualStatistics = new TextualFieldStatistics
  /**
   * Suggested data type with type infer details.<br>
   * (data type name, type infer details) Map[types inferred from this field, number of record which has this type]
   * @since 1.0
   */
  var suggestedType: (DataType[Any], Map[DataType[Any], Long])

}