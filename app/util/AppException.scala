package util

/**
  * Created by chlr on 5/27/17.
  */

abstract class AppException(message: String) extends Exception(message)

object AppException {

  class EntityNotFoundException(message: String) extends AppException(message)

  class ParseException(message: String) extends AppException(message)

  class JobSetUpException(message: String) extends AppException(message)

  class JobExecutionException(val returnCode: Int, message: String) extends AppException(message)

  class AppSetupException(message: String) extends AppException(message)

  class InvalidJsonFormatException(message: String) extends AppException(message)

  class InvalidStateException(message: String) extends AppException(message)

}


