package coincalculator;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterRegistration;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import io.dropwizard.core.Application;
import io.dropwizard.core.setup.Bootstrap;
import io.dropwizard.core.setup.Environment;

import java.util.EnumSet;


public class CoinCalcApp extends Application<AppConfiguration> {
    public static void main(String[] args) throws Exception {
        new CoinCalcApp().run(args);
    }
    @Override
    public void initialize(Bootstrap<AppConfiguration> bootstrap) {
    }
    @Override
    public void run(AppConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(new HelloResource());
        environment.jersey().register(new CalculatorResource());
        System.out.println("Dropwizard application started!");

        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
    }
}
