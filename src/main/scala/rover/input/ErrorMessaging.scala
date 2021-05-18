package rover.input

import cats.data.{NonEmptyList, ValidatedNel}

// returned when there is an error in parsing the csv file or validating its contents
case class ParsingException(msg: String) extends Throwable(msg)

// helpers to add contextual information to error messages generated from parsing/validating `Review`s
object ErrorMessaging {
  // gathers all error messages from parsing/validating and generates a ParsingException with a message containing all
  // error messages separated by newlines (truncated at 50 so as to not be obnoxious)
  def combineErrors(errorMessages: NonEmptyList[String]): Throwable = {
    val numErrors = errorMessages.length
    val truncatedMsg =
      if (numErrors > 50) s"\n...truncated at 50. contained $numErrors errors"
      else ""
    val allErrorsMsg = errorMessages.toList.take(50).mkString("\n")
    ParsingException(s"errors during parsing:\n$allErrorsMsg$truncatedMsg")
  }

  // for any validations that are Invalid, add row number to error msg
  def addErrorRowIndices[A](list: List[ValidatedNel[String, A]]): List[ValidatedNel[String, A]] =
    list.zipWithIndex.map { case (v, idx) =>
      v.leftMap(msgs => msgs.map(msg => s"(row ${idx + 1}) $msg"))
    }

  // add offending input row to the validation error message generated when attempting to validate that row. if a
  // validation for a single row in the csv has multiple validation errors, include the input row only in the first
  // error msg, rather than duplicating it in all msgs for the row. if not invalid, do nothing
  def addErrorLineOnce[A](v: ValidatedNel[String, A], line: String): ValidatedNel[String, A] =
    v.leftMap { case NonEmptyList(head, tail) =>
      NonEmptyList(s"$head in line: $line", tail)
    }
}
