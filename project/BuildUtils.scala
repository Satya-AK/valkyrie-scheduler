import java.io.File
import java.nio.file.Paths

/**
  * Created by chlr on 6/19/17.
  */
object BuildUtils {

  def isParentFile(parentFile: File, file: File) = {
     val parent = Paths.get(parentFile.toString).toAbsolutePath
     file.toString.startsWith(parent.toString)
  }
}
