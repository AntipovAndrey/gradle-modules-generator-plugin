package com.github.antipovandrey.generator.config

import org.apache.velocity.VelocityContext
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import java.io.File
import java.io.FileWriter
import java.io.StringWriter
import java.io.Writer

object TemplateResolver {

    private val velocityEngine = VelocityEngine()

    init {
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file")
        velocityEngine.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH, "/")
        velocityEngine.init()
    }

    fun resolveTemplate(source: File, targetFile: File, props: Map<String, Any>) {
        resolveTemplate(source, FileWriter(targetFile), props)
    }

    fun resolveTemplate(source: File, props: Map<String, Any>): String {
        val writer = StringWriter()
        resolveTemplate(source, writer, props)
        return writer.toString()
    }

    private fun resolveTemplate(source: File, writer: Writer, props: Map<String, Any>) {
        val velocityTemplate = velocityEngine.getTemplate(source.absolutePath)
        writer.use {
            velocityTemplate.merge(VelocityContext(props), writer)
        }
    }
}