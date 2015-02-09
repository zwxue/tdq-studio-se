package org.talend.datascience.common.statistics

class NumericalFieldStatistics extends FieldStatistics {
  var max, min, mean, median, upperQuartile, lowerQuartile: Double = Double.NaN
  var count, distincts, duplicates: Long = 0l
}