package com.github.antipovandrey.generator.config

import com.github.antipovandrey.generator.model.ModuleTemplate
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileFilter

object ConfigReader {

    private val yaml = Yaml()

    fun readConfig(projectPath: String): List<ModuleTemplate> {

        return File(projectPath)
            .resolve(Settings.templateFolderName)
            .listFiles(FileFilter { it.isDirectory })
            .map { readModule(it) }
    }

    private fun readModule(moduleDirectory: File): ModuleTemplate {
        val configFile = moduleDirectory.resolve(Settings.configFileName)

        val staticFiles = ArrayList<File>()
        val templateFiles = ArrayList<File>()

        moduleDirectory.listFiles(FileFilter { it.isDirectory })
            .forEach { directory ->
                when (directory.name) {
                    Settings.staticFilesName -> directory.walk().forEach { staticFiles.add(it) }
                    Settings.templateFilesName -> directory.walk().forEach { templateFiles.add(it) }
                }
            }

        val config = yaml.loadAs<Config>(configFile.inputStream(), Config::class.java)

        return ModuleTemplate(config.name, staticFiles, templateFiles)
    }
}