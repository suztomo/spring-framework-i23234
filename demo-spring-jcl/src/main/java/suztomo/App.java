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
    Log log = LogFactoryService.getLog(App.class);
    log.info("Hello, spring-jcl log!");

    // slf4j dependencies are added. Now logs disappear.
    Logger logger = LoggerFactory.getLogger(App.class);
    logger.info("Hello, slf4j log!");
  }
}
