package util

/**
  * Created by chlr on 5/27/17.
  */

abstract class AppException(message: String) extends Exception(message) {

}

object AppException {

  class EntityNotFoundException(message: String) extends AppException(message)

}

