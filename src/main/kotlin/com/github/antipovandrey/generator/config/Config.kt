package com.github.antipovandrey.generator.config

/**
 *  Representation of config.yml file inside of each module template
 */
class Config {
    lateinit var name: String
    var directories: List<String> = emptyList()
}
