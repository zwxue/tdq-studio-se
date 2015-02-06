package org.talend.datascience.common.inference
import ValueSurvivorOptions._
/**
 * @author zhao
 * @parameter globalParameter the parameter applies to all data.
 * @param fieldTransformParameters ,Map(field index -> transform parameters)
 */
class TransformParameter(val globalParameter: ParameterEntity, val fieldTransformParameters: Map[Int, ParameterEntity]) extends Serializable {
  def this() = this(EmptyParameter, Map.empty)
}
class ParameterEntity {
  val globalValueSurvivorOption: ValueSurvivorOptions = SurviveTextualValue
  val globalVissingValueResolutions: Seq[MissingValueResolution] = Seq()
  val allowNumericalValueCalculas: Boolean = false
}
case object EmptyParameter extends ParameterEntity {

}