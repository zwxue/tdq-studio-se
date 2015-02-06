package org.talend.datascience.common.inference

import scala.util.parsing.combinator.RegexParsers

object TypeInferenceEngine extends RegexParsers {
  def number: Parser[Double] = """\d+(\.\d*)?""".r ^^ { _.toDouble }
  def factor: Parser[Double] = number | "(" ~> expr <~ ")"
  def term: Parser[Double] = factor ~ rep("*" ~ factor | "/" ~ factor) ^^ {
    case number ~ list => (number /: list) {
      case (x, "*" ~ y) => x * y
      case (x, "/" ~ y) => x / y
    }
  }
  def expr: Parser[Double] = term ~ rep("+" ~ log(term)("Plus term") | "-" ~ log(term)("Minus term")) ^^ {
    case number ~ list => list.foldLeft(number) { // same as before, using alternate name for /:
      case (x, "+" ~ y) => x + y
      case (x, "-" ~ y) => x - y
    }
  }

  def apply(input: String): Double = parseAll(expr, input) match {
    case Success(result, _) => result
    case failure: NoSuccess => Double.NaN
  }

  def applyWithoutCalculas(input: String): Double = parseAll(number, input) match {
    case Success(result, _) => result
    case failure: NoSuccess => Double.NaN
  }

  def getDouble(value: String, allowCalculas: Boolean): Double = {
    var dvalue = Double.NaN
    if (allowCalculas) dvalue = apply(value)
    else dvalue = applyWithoutCalculas(value)
    
    if (Double.NaN == dvalue) {
      //TODO call elastic search to guess the value, e.g. one point five will give 1.5, 二百五十 will give 250
    }
    dvalue
  }

  def isInteger(value: String): Boolean = {
    false
  }

  def isLong(value: String): Boolean = {
    false
  }

  def isDate(value: String): Boolean = {
    false
  }

  def isTime(value: String): Boolean = {
    false
  }

}