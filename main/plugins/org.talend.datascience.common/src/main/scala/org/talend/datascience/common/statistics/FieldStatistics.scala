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

import org.talend.datascience.common.schema.DataType

/**
 * Abstract statistics class of a given field.
 * @since 1.0
 * @author mzhao
 */
abstract class FieldStatistics extends Serializable{
   var count, distincts,uniques, duplicates,empty: Long = 0l
   var frequencyTable:Seq[(String, Long)]=Seq()
   var patternFrequencyTable:Seq[(String, Long)]=Seq()
   var typeToFrequencyTable:Map[String , Map[String, Long]]=Map()
}