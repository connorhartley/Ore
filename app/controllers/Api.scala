package controllers

import models.Project
import play.api.libs.json._
import play.api.mvc._

class Api extends Controller {

  implicit val projectWrites = new Writes[Project] {
    def writes(project: Project) = Json.obj(
      "id" -> project.id,
      "name" -> project.name,
      "description" -> project.description,
      "owner" -> project.owner.name,
      "views" -> project.views,
      "downloads" -> project.downloads,
      "starred" -> project.starred
    )
  }

  def listProjects = Action {
    val json = Json.toJson(Project.projects)
    Ok(json)
  }

}
