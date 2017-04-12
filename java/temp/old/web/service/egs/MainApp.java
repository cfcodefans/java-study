package cf.study.web.service.egs;

import com.sun.grizzly.http.SelectorThread;
import com.sun.jersey.api.container.grizzly.GrizzlyWebContainerFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainApp {
    public static void main(String[] args) throws IOException {
        // Port can be pulled from an environmental variable
        // Ex: System.getenv("SERVICE_PORT")
        final String baseUri = "http://localhost:9998";
        final Map<String, String> initParams = new HashMap<String, String>();
        // Classes under resources and its subpackages will be included when routing requests
        initParams.put("com.sun.jersey.property.packages", "com.mycompany.app.resources");
        SelectorThread threadSelector = GrizzlyWebContainerFactory.create(baseUri, initParams);
        System.in.read();
        threadSelector.stopEndpoint();
        System.exit(0);
    }
}