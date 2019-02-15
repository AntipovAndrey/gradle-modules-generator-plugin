package com.github.antipovandrey.generator.config

import com.github.antipovandrey.generator.model.ModuleTemplate
import com.github.antipovandrey.generator.model.Resources
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileFilter

object ModuleTemplateReader {

    private val yaml = Yaml()

    fun readModuleTemplates(projectPath: String): List<ModuleTemplate> {
        return File(projectPath)
            .resolve(Settings.templateFolderName)
            .listFiles(FileFilter { it.isDirectory })
            .map { readModule(it) }
    }

    private fun readModule(moduleDirectory: File): ModuleTemplate {
        val config = readConfig(moduleDirectory)
        val staticResources = getResources(moduleDirectory, Settings.staticFilesName)
        val templateResources = getResources(moduleDirectory, Settings.templateFilesName)
        return ModuleTemplate(config.name, staticResources, templateResources)
    }

    private fun getResources(moduleDirectory: File, resourcesName: String): Resources {
        val resourcesDirectory = moduleDirectory.resolve(resourcesName)
        return Resources(findFiles(resourcesDirectory), resourcesDirectory)
    }

    private fun readConfig(moduleDirectory: File): Config {
        val configFile = moduleDirectory.resolve(Settings.configFileName)
        return yaml.loadAs<Config>(configFile.inputStream(), Config::class.java)
    }

    private fun findFiles(directory: File): List<File> {
        return directory.walk().toList().filterNot { it == directory }
    }
}