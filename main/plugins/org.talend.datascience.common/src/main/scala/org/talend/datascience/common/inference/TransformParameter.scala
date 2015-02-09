package org.talend.datascience.common.inference

import org.talend.datascience.common.transform.MissingValueResolutionOption
/**
 * @author zhao
 * @parameter globalParameter the parameter applies to all data.
 * @param fieldTransformParameters ,Map(field index -> transform parameters)
 */
class TransformParameter(val globalParameter: ParameterEntity, val fieldTransformParameters: Map[Int, ParameterEntity]) extends Serializable {
  def this() = this(EmptyParameter, Map.empty)
}
class ParameterEntity {
  var numMissingValueResolution = MissingValueResolutionOption.fillZero
  var allowNumericalValueCalculas: Boolean = false
  var patternStr: String = "" 
  var allowTransform = false
}
case object EmptyParameter extends ParameterEntity {

}