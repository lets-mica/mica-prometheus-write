package net.dreamlu.mica.prometheus.write;

import net.dreamlu.mica.prometheus.write.handler.PrometheusWriteHandler;
import org.tio.http.server.HttpServerStarter;

import java.io.IOException;

/**
 * 应用
 *
 * @author L.cm
 */
public class Application {

	public static void main(String[] args) throws IOException {
		PrometheusWriteHandler handler = new PrometheusWriteHandler();
		HttpServerStarter httpServerStarter = new HttpServerStarter(8080, handler);
		httpServerStarter.start();
	}

}
