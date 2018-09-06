package com.jtestim.tomcatssl;

import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.core.env.Environment;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;

/**
 * Spring boot application
 */
@SpringBootApplication
public class Application implements EmbeddedServletContainerCustomizer {

	@Autowired
	private Environment env;

	@Autowired
	private AppConfig appConfig;

	private final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}





	@Override
	public void customize(
			ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer) {

		if (configurableEmbeddedServletContainer instanceof TomcatEmbeddedServletContainerFactory) {
			TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) configurableEmbeddedServletContainer;
			String port = appConfig.getPort();
			String keyStore = appConfig.getKeyStore();
			String keyStorePass = appConfig.getKeyStorePassword();
			String keyStoreType = appConfig.getKeyStoreType();
			File keystoreFile = new File(keyStore);
			String absoluteKeystoreFile = keystoreFile.getAbsolutePath();

			log.info("Absolute KeystoreFile path in use : {}", absoluteKeystoreFile);
			tomcat.addConnectorCustomizers(
					(connector) -> {
						connector.setPort(Integer.parseInt(port));
						connector.setSecure(true);
						connector.setScheme("https");

						Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
						proto.setSSLEnabled(true);
						proto.setKeystoreFile(absoluteKeystoreFile);
						proto.setKeystorePass(keyStorePass);
						proto.setKeystoreType(keyStoreType);

						HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);

					}
			);
		}
	}
}
