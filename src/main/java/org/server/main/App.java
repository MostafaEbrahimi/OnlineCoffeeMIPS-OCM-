package org.server.main;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.server.service.Execute;

/**
 * Created by Mostafa on 6/27/2016.
 */
public class App extends Application<Config> {
    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void run(Config config, Environment env) {
        env.jersey().register(MultiPartFeature.class);
//        env.jersey().register(MultiPartConfigProvider.class);
        Execute execute=new Execute();
        env.jersey().register(execute);
    }
}
