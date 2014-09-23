package com.ee.assets.transformers

import com.ee.assets.transformers.helpers.GzipHelper
import java.io._
import org.apache.commons.io.IOUtils
import org.specs2.mutable.Specification
import scala.Some

class GzipTest extends Specification with GzipHelper {

  "GZip" should {

    "work" in {
      val elements = Seq(ContentElement("/path.js", "alert('hello');", None))

      val zipped = new Gzip().run(elements)
      println(zipped(0).contents)
      decompress(zipped(0).contents) === "alert('hello');"
    }

    "write to and read from file" in {
      val elements = Seq(ContentElement("/path.js", "alert('hello');", None))
      val zipped = new Gzip().run(elements)
      val zippedBytes = zipped(0).contents
      val output : FileOutputStream  = new FileOutputStream(new File("target-file"))
      IOUtils.write(zippedBytes, output)
      IOUtils.closeQuietly(output)
      val input = new FileInputStream(new File("target-file"))
      val readBytes : Array[Byte] = IOUtils.toByteArray(input)
      IOUtils.closeQuietly(input)
      decompress(readBytes) === "alert('hello');"
    }
  }

}

