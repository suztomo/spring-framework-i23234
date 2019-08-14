package suztomo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class App {

  public static void main(String[] args) {
    /*
    spring-jcl has a couple of log4j and slf4j dependencies, but those are not the actual
    dependencies you should have to enable logging with a logging framework.
    Those are dependencies that we use to build the specific bridge implementations, but using a
    logging framework requires more than that.
     */

    // A couple of log4j and slf4j dependencies:
    // https://github.com/spring-projects/spring-framework/blob/master/spring-jcl/spring-jcl.gradle
    Log log = LogFactoryService.getLog(App.class);
    log.info("Hello, spring-jcl log!");

    // slf4j dependencies are added. Now logs disappear.
    Logger logger = LoggerFactory.getLogger(App.class);
    logger.info("Hello, slf4j log!");



  }
}
