package com.goibibo.sqlshift

import com.typesafe.config.{Config, ConfigFactory}
import com.whisk.docker.impl.spotify.DockerKitSpotify
import com.whisk.docker.scalatest.DockerTestKit
import org.scalatest.time.{Second, Seconds, Span}
import org.scalatest.{FlatSpec, GivenWhenThen, Matchers}
import org.slf4j.{Logger, LoggerFactory}

import scala.util.{Failure, Success}

/**
  * Project: sqlshift
  * Author: shivamsharma
  * Date: 5/8/18.
  */
class FullDump extends FlatSpec
        with Matchers
        with GivenWhenThen
        with DockerTestKit
        with DockerKitSpotify
        with DockerMySQLService
        with DockerZookeeperService {

    private val logger: Logger = LoggerFactory.getLogger(this.getClass)
    implicit val pc: PatienceConfig = PatienceConfig(Span(20, Seconds), Span(1, Second))
    private val config: Config = ConfigFactory.load()

    val fixtures = new {

    }

    def setUpMySQL(): Unit = {
        val recordsFileName = config.getString("table.recordsFileName")
        logger.info(s"Inserting records in MySQL from file: $recordsFileName")
        isContainerReady(mySQLContainer) onComplete {
            case Success(posts) =>
                MySQLUtil.insertRecords(config, this.getClass.getClassLoader.getResourceAsStream(recordsFileName))
            case Failure(t) => logger.error("Error occurred making container ready", t)
        }
        logger.info("Insertion Done!!!")
    }


    "mongodb node" should "be ready with log line checker" in {
        Thread.sleep(10000)
        setUpMySQL()
        Thread.sleep(100000)
    }
}
