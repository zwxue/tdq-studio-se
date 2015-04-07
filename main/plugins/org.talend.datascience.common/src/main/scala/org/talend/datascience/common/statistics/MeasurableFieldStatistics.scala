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

/**
 * @author zhao
 * @since 1.0
 * A generic statistic class for numerical and date type of data.
 */
class MeasurableFieldStatistics extends FieldStatistics {
  var max, min, mean,sum, variance, median, upperQuartile, lowerQuartile: Double = Double.NaN
  var histogram:(Array[Double],Array[Long])=(Array(),Array());
}