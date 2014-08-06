package com.ee.assets.transformers

import java.io.{ByteArrayInputStream, InputStream}

import com.ee.assets.deployment.{ContentInfo, Deployer}
import com.ee.log.Logger

abstract class BaseDeploy[A](d: Deployer) extends Transformer[A, Unit] {

  protected def logger: org.slf4j.Logger

  protected def getInputStream(contents: A): InputStream

  protected def encoding: Option[String]

  override def run(elements: Seq[Element[A]]): Seq[DeployedElement] = {
    elements.map {
      e =>
        def contentType = if (e.path.endsWith(".js")) "text/javascript" else "text/css"
        val is: InputStream = getInputStream(e.contents)
        d.deploy(e.path, e.lastModified.getOrElse(0), is, ContentInfo(contentType, encoding)) match {
          case Right(p) => Some(p)
          case Left(err) => {
            logger.warn(s"Error deploying: ${e.path}")
            None
          }
        }
    }.flatten
  }
}


object StringDeploy {
  def apply(d: Deployer) = new StringDeploy(d).run _
}

class StringDeploy(d: Deployer) extends BaseDeploy[String](d) {
  override protected def logger = Logger("deploy.string")

  override def getInputStream(c: String): InputStream = new ByteArrayInputStream(c.getBytes("UTF-8"))

  override protected def encoding: Option[String] = None
}

object ByteArrayDeploy {
  def apply(d: Deployer) = new ByteArrayDeploy(d).run _
}

class ByteArrayDeploy(d: Deployer) extends BaseDeploy[Array[Byte]](d) {
  override protected def logger = Logger("deploy.byte.array")

  override def getInputStream(c: Array[Byte]): InputStream = new ByteArrayInputStream(c)

  override protected def encoding: Option[String] = Some("gzip")
}

