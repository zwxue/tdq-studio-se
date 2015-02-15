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
package org.talend.datascience.common.inference

import org.talend.datascience.common.transform.MissingValueResolutionOption
/**
 * A kind of bean class contains parameters for data transformation.
 * @author mzhao
 * @since 1.0
 * @param globalParameter the parameter applies to all data.
 * @param fieldTransformParameters The map which Map(field index -> transform parameters) for each specific field.
 */
class TransformParameter(val globalParameter: ParameterEntity, val fieldTransformParameters: Map[Int, ParameterEntity]) extends Serializable {
  def this() = this(EmptyParameter, Map.empty)
}
/**
 * A parameter entity which includes information such as missing value resolution, allow calculation for numerical values and pattern of the field etc.
 * 
 */
class ParameterEntity extends Serializable {
  var numMissingValueResolution = MissingValueResolutionOption.fillZero
  var allowNumericalValueCalculas: Boolean = false
  var patternStr: String = ""
}
case object EmptyParameter extends ParameterEntity {
}