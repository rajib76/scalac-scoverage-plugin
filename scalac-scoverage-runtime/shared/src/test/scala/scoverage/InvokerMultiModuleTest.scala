package scoverage

import java.lang.management.ManagementFactory

import scoverage.Platform.File
import org.scalatest.{BeforeAndAfter, FunSuite}

/**
 * Verify that [[Invoker.invoked()]] can handle a multi-module project
 */
class InvokerMultiModuleTest extends FunSuite with BeforeAndAfter {

  //Getting the process id here so that it can be concatenated with the measurement file name.
  //This will ensure that no two process are going to write in the same measurement file


  val measurementDir = Array(
    new File("target/invoker-test.measurement0"),
    new File("target/invoker-test.measurement1")
  )

  before {
    deleteMeasurementFiles()
    measurementDir.foreach(_.mkdirs())
  }

  test("calling Invoker.invoked on with different directories puts measurements in different directories") {

    val testIds: Set[Int] = (1 to 10).toSet

    testIds.map { i: Int => Invoker.invoked(i, measurementDir(i % 2).toString) }

    // Verify measurements went to correct directory
    val measurementFiles0 = Invoker.findMeasurementFiles(measurementDir(0))
    val idsFromFile0 = Invoker.invoked(measurementFiles0.toIndexedSeq)

    idsFromFile0 === testIds.filter { i: Int => i % 2 == 0 }

    val measurementFiles1 = Invoker.findMeasurementFiles(measurementDir(0))
    val idsFromFile1 = Invoker.invoked(measurementFiles1.toIndexedSeq)
    idsFromFile1 === testIds.filter { i: Int => i % 2 == 1 }
  }

  after {
    deleteMeasurementFiles()
    measurementDir.foreach(_.delete)
  }

  private def deleteMeasurementFiles(): Unit = {
    measurementDir.foreach((md) => {
      if (md.isDirectory)
        md.listFiles().foreach(_.delete())
    })
  }
}
