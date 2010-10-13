import java.io.File
import sbt.BasicScalaProject
import scala.collection.jcl.Conversions._
import org.apache.commons.io.FileUtils.listFiles
import org.apache.commons.io.FilenameUtils.removeExtension
import scala.xml.Utility.trim
import xml.XML

trait ScriptedTestAssertTasks extends BasicScalaProject {
  lazy val assertExpectedXmlFiles = task {
    val expectedFiles = listFiles(info.projectPath.asFile, List("expected").toArray, true).toArray.map(_.asInstanceOf[File])
    List(expectedFiles: _*).map(assertExpectedXml).foldLeft[Option[String]](None) { (acc, fileResult) => if (acc.isDefined) acc else fileResult }
  }

  private def assertExpectedXml(expectedFile: File):Option[String] = {
    val actualFile = new File(removeExtension(expectedFile.getAbsolutePath))
    if (actualFile.exists) assertExpectedXml(expectedFile, actualFile)
    else Some("Expected file " + actualFile.getAbsolutePath + " does not exist.")
  }

  private def assertExpectedXml(expectedFile: File, actualFile: File): Option[String] = {
    val actualXml = trim(XML.loadFile(actualFile))
    val expectedXml = trim(XML.loadFile(expectedFile))
    if (!actualXml.equals(expectedXml)) Some(
      "Xml file " + actualFile.getName + " does not equal expected:"
      + "\n********** Expected **********\n " + expectedXml.toString
      + "\n*********** Actual ***********\n " + actualXml.toString
    ) else None
  }
}