package discourse

import akka.actor.Scheduler
import db.impl.OrePostgresDriver.api._
import db.impl.access.ProjectBase

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration

import play.api.Logger

/**
  * Task to periodically retry failed Discourse requests.
  */
class RecoveryTask(scheduler: Scheduler,
                   retryRate: FiniteDuration,
                   api: OreDiscourseApi,
                   projects: ProjectBase)(implicit ec: ExecutionContext) extends Runnable {

  val Logger: Logger = this.api.Logger

  /**
    * Starts the recovery task to be run at the specified interval.
    */
  def start(): Unit = {
    this.scheduler.schedule(this.retryRate, this.retryRate, this)
    Logger.info(s"Discourse recovery task started. First run in ${this.retryRate.toString}.")
  }

  override def run(): Unit = {
    Logger.info("Running Discourse recovery task...")

    this.projects.filter(_.topicId === -1).foreach { toCreate =>
      Logger.info(s"Creating ${toCreate.size} topics...")
      toCreate.foreach(this.api.createProjectTopic)
    }

    this.projects.filter(_.isTopicDirty).foreach { toUpdate =>
      Logger.info(s"Updating ${toUpdate.size} topics...")
      toUpdate.foreach(this.api.updateProjectTopic)
    }

    Logger.info("Done")
    // TODO: We need to keep deleted projects in case the topic cannot be deleted
  }

}
