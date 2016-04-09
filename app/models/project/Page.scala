package models.project

import java.sql.Timestamp

import db.Model
import db.query.Queries
import models.project.Page._
import org.pegdown.Extensions._
import org.pegdown.PegDownProcessor

/**
  * Represents a documentation page within a project.
  *
  * @param id           Page ID
  * @param createdAt    Timestamp of creation
  * @param projectId    Project ID
  * @param name         Page name
  * @param slug         Page URL slug
  * @param _contents     Markdown contents
  * @param isDeletable  True if can be deleted by the user
  */
case class Page(override val id: Option[Int], override val createdAt: Option[Timestamp],
                projectId: Int, name: String, slug: String, private var _contents: String,
                isDeletable: Boolean) extends Model {

  def this(projectId: Int, name: String, content: String, isDeletable: Boolean = true) = {
    this(None, None, projectId, Project.sanitizeName(name), Project.slugify(name), content, isDeletable)
  }

  /**
    * Returns the Markdown contents of this Page.
    *
    * @return Markdown contents
    */
  def contents: String = this._contents

  /**
    * Sets the Markdown contents of this Page.
    *
    * @param _contents Markdown contents
    */
  def contents_=(_contents: String) = {
    Queries.now(Queries.Pages.setString(this, _.contents, _contents)).get
    this._contents = _contents
  }

  /**
    * Returns the HTML representation of this Page.
    *
    * @return HTML representation
    */
  def html: String = MD.markdownToHtml(contents)

}

object Page {

  /**
    * The name of each Project's homepage.
    */
  val HOME: String = "Home"

  /**
    * The template body for the Home page.
    */
  val HOME_MESSAGE: String = "Welcome to your new project!"

  /**
    * The Markdown processor.
    */
  val MD: PegDownProcessor = new PegDownProcessor(ALL & ~ANCHORLINKS)

  /**
    * Returns a template for new Pages.
    *
    * @param title  Page title
    * @param body   Default message
    * @return       Template
    */
  def template(title: String, body: String = ""): String = {
    "# " + title + "\n" + body
  }

}
