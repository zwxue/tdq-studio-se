package org.talend.datascience.common.inference

import scala.util.parsing.combinator.RegexParsers

object TypeInferenceEngine extends RegexParsers {
  def intnumber: Parser[Int] = """^(\+|-)?\d+$""".r ^^ { _.toInt }
  //matches YYYY-MM-dd see: http://regexlib.com/REDetails.aspx?regexp_id=890
  def date: Parser[String] = """(?<Year>(19|20)[0-9][0-9])-(?<Month>0[1-9]|1[0-2])-(?<Day>0[1-9]|[12][0-9]|3[01])""".r ^^ { _.toString() }
  //matches yyyy-MM-dd HH:mm:ss see: http://regexlib.com/REDetails.aspx?regexp_id=798
  def datetime: Parser[String] = """^(19[0-9]{2}|[2-9][0-9]{3})-((0(1|3|5|7|8)|10|12)-(0[1-9]|1[0-9]|2[0-9]|3[0-1])|(0(4|6|9)|11)-(0[1-9]|1[0-9]|2[0-9]|30)|(02)-(0[1-9]|1[0-9]|2[0-9]))\x20(0[0-9]|1[0-9]|2[0-3])(:[0-5][0-9]){2}$""".r ^^ { _.toString() } //match dd/MM/YYYY see: http://regexlib.com/REDetails.aspx?regexp_id=250

  def dnumber: Parser[Double] = """^[-+]?\d*\.?\d*$""".r ^^ { _.toDouble }
  def factor: Parser[Double] = dnumber | "(" ~> expr <~ ")"
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

  def applyWithoutCalculas(input: String): Double = parseAll(dnumber, input) match {
    case Success(result, _) => result
    case failure: NoSuccess => Double.NaN
  }

  def getDouble(value: String, allowCalculas: Boolean): Double = {
    var dvalue = Double.NaN
    if (allowCalculas) dvalue = apply(value)
    else dvalue = applyWithoutCalculas(value)

    if (Double.NaN == dvalue) {
      //TODO call elastic search to guess the value, e.g. one point five will give 1.5, 二百五十(in Chinese) will give 250
    }
    dvalue
  }

  def isInteger(value: String): Boolean = {
    parseAll(intnumber, value) match {
      case Success(result, _) => true
      case failure: NoSuccess => false
    }
  }
  def isDate(value: String): Boolean = {
    var isMatch = parseAll(date, value) match {
      case Success(result, _) => true
      case failure: NoSuccess => false
    }
    if (!isMatch) {
      isMatch = parseAll(datetime, value) match {
        case Success(result, _) => return true
        case failure: NoSuccess => false
      }
    }
    isMatch

  }
  def isDateTime(value: String): Boolean = {
    parseAll(datetime, value) match {
      case Success(result, _) => true
      case failure: NoSuccess => false
    }
  }

  def isTime(value: String): Boolean = {
    false
  }

}