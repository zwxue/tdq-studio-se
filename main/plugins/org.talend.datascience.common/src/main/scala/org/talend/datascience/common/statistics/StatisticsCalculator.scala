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
package org.talend.datascience.common.statistics

import org.talend.datascience.common.data.ElasticData
import org.talend.datascience.common.schema.SemanticField
import org.talend.datascience.common.schema.SemanticSchema

/**
 * @author zhao
 * Calculate the field statistics.
 */
trait StatisticsCalculator {

  /**
   * Given the field data and the data types from semantic field, compute the the distinct values and store it in the semantic field.
   */
  def computeCardinalities(field: SemanticField, fieldData: ElasticData[Any])

  /**
   * Given the field data and the data types from semantic field , compute the quantile values.
   */
  def computeQuantiles(field: SemanticField, fieldData: ElasticData[Any])

  /**
   * Given the field data and data types  from semantic field, compute the frequency table.
   * @author zhao
   * @param k: the count of frequency items to be returned.
   */
  def computeFrequencyTable(field: SemanticField, fieldData: ElasticData[Any], k: Int)

  /**
   * Given the field data and data types  from semantic field, compute the histogram
   * @author zhao
   * @param bins: number of buckets of this data split.
   */
  def computeHistogram(field: SemanticField, fieldData: ElasticData[Any], bins: Int)

  /**
   * Given the field data and data types  from semantic field, compute the histogram
   * @author zhao
   * @param buckets: buckets specified for this data split.
   */
  def computeHistogram(field: SemanticField, fieldData: ElasticData[Any], buckets: Array[Double])

  /**
   * compute all the statistics given the aggregated by field and schema.
   * @author zhao
   * @param ed elastic data set
   * @param schema data schema
   * @param topKfreqTable top k parameters for frequency tables
   * @param binsOrBuckets customized bins set for histogram.
   * @param aggregatedBy field index used as aggregated key.
   */
  def computeAggregatedStatistics(ed: ElasticData[Any], schema: SemanticSchema[Any], topKfreqTable: Int, binsOrBuckets: String, aggregatedBy: Int): Array[_ <: (String, Iterable[SemanticField])]
  /**
   * Compute all statistics given the dataset , this method will do statistics with one pass only
   * @param ed elastic data set
   * @param schema data schema
   * @param topKfreqTable top k parameters for frequency tables
   * @param binsOrBuckets customized bins set for histogram.
   */
  def computeStatistics(ed: ElasticData[Any], schema: SemanticSchema[Any], topKfreqTable: Int, binsOrBuckets: String): Iterable[SemanticField]

}
object StatisticsCalculator {
  def createStasCalculator(calculatorClassName: String): StatisticsCalculator = {
    val aclass = Class.forName(calculatorClassName)
    aclass.newInstance().asInstanceOf[StatisticsCalculator]
  }
}