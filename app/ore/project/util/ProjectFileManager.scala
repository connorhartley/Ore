package ore.project.util

import java.nio.file.{Files, Path}

import util.OreEnv

import scala.util.Try

/**
  * Handles file management of Projects.
  */
class ProjectFileManager(val env: OreEnv) {

  /**
    * Returns the Path to where the specified Version should be.
    *
    * @param owner    Project owner
    * @param name     Project name
    * @param version  Project version
    * @return         Path to supposed file
    */
  def uploadPath(owner: String, name: String, version: String): Path
  = projectDir(owner, name).resolve("%s-%s.zip".format(name, version))

  /**
    * Returns the specified project's plugin directory.
    *
    * @param owner  Owner name
    * @param name   Project name
    * @return       Plugin directory
    */
  def projectDir(owner: String, name: String): Path = userDir(owner).resolve(name)

  /**
    * Returns the specified user's plugin directory.
    *
    * @param owner  Owner name
    * @return       Plugin directory
    */
  def userDir(owner: String): Path = env.plugins.resolve(owner)

  /**
    * Renames this specified project in the file system.
    *
    * @param owner    Owner name
    * @param oldName  Old project name
    * @param newName  New project name
    * @return         New path
    */
  def renameProject(owner: String, oldName: String, newName: String): Try[Unit] = Try {
    val newProjectDir = projectDir(owner, newName)
    Files.move(projectDir(owner, oldName), newProjectDir)
    // Rename plugin files
    for (channelDir <- newProjectDir.toFile.listFiles()) {
      if (channelDir.isDirectory) {
        for (pluginFile <- channelDir.listFiles()) {
          val fileName = pluginFile.getName
          val versionString = fileName.substring(fileName.indexOf('-') + 1, fileName.lastIndexOf('.'))
          Files.move(pluginFile.toPath, uploadPath(owner, newName, versionString))
        }
      }
    }
  }

}
