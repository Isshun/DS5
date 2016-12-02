package org.smallbox.faraway;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.smallbox.faraway.config.EntityFilteringApplication;
import org.smallbox.faraway.util.Log;

import java.net.URI;

/**
 * Created by Alex on 01/12/2016.
 */
public class DebugServer {

    private static final URI BASE_URI = URI.create("http://127.0.0.1:8008/");

    public DebugServer() throws Exception {
        try {
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(BASE_URI, new EntityFilteringApplication(), false);
            server.getServerConfiguration().addHttpHandler(new StaticHttpHandler("src/main/webapp/www/"), "/www/");
            Runtime.getRuntime().addShutdownHook(new Thread(server::shutdownNow));
            server.start();

//            // Get the input stream
//            InputStream streamToUploadFrom = new FileInputStream(new File("C:/Home/test.png"));
//
//            // Create some custom options
//            GridFSUploadOptions options = new GridFSUploadOptions()
//                    .chunkSizeBytes(1024)
//                    .metadata(new Document("contentType", "image/png"));
//
//            ObjectId fileId = gridFSBucket.uploadFromStream(UUID.nameUUIDFromBytes("res-1".getBytes()).toString(), streamToUploadFrom, options);

//            URI baseUri = UriBuilder.fromUri("http://localhost/").port(8008).build();
//            ResourceConfig config = new EntityFilteringApplication();
//            Server server = JettyHttpContainerFactory.createServer(baseUri, config);
//            server.start();
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                try {
//                    server.stop();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }));

            Thread.currentThread().join();
        } catch (Exception ex) {
            Log.error(ex.getMessage(), ex);
        }
    }

    public static void start() {
        try {
            new DebugServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
