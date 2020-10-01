package com.archesky.ssl.library.configuration

import org.apache.catalina.Context
import org.apache.catalina.connector.Connector
import org.apache.tomcat.util.descriptor.web.SecurityCollection
import org.apache.tomcat.util.descriptor.web.SecurityConstraint
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory.DEFAULT_PROTOCOL
import org.springframework.boot.web.servlet.server.ServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.lang.Integer.parseInt

@Configuration
open class SSLConfig(private val environment: Environment) {
    @Bean
    open fun servletContainer(): ServletWebServerFactory {
        val tomcat: TomcatServletWebServerFactory = object : TomcatServletWebServerFactory() {
            override fun postProcessContext(context: Context) {
                val securityConstraint = SecurityConstraint()
                securityConstraint.userConstraint = "CONFIDENTIAL"
                val collection = SecurityCollection()
                collection.addPattern("/*")
                securityConstraint.addCollection(collection)
                context.addConstraint(securityConstraint)
            }
        }
        tomcat.addAdditionalTomcatConnectors(httpConnector)
        return tomcat
    }

    private val httpConnector: Connector
        get() {
            val connector = Connector(DEFAULT_PROTOCOL)
            connector.scheme = "http"
            connector.port = parseInt(environment.getProperty("server.http.port", "8080"))
            connector.secure = false
            connector.redirectPort = parseInt(environment.getProperty("server.port", "8443"))
            return connector
        }
}
