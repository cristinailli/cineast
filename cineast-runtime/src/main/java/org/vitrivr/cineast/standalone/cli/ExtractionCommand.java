package org.vitrivr.cineast.standalone.cli;

import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import java.io.File;
import java.io.IOException;
import org.vitrivr.cineast.core.util.json.JacksonJsonProvider;
import org.vitrivr.cineast.standalone.config.IngestConfig;
import org.vitrivr.cineast.standalone.run.ExtractionCompleteListener;
import org.vitrivr.cineast.standalone.run.ExtractionContainerProvider;
import org.vitrivr.cineast.standalone.run.ExtractionDispatcher;
import org.vitrivr.cineast.standalone.run.path.ExtractionContainerProviderFactory;

/**
 * A CLI command that can be used to start a media extraction based on an extraction definition file.
 *
 * @author Ralph Gasser
 * @version 1.0
 */
@Command(name = "extract", description = "Starts a media extracting using the specified settings.")
public class ExtractionCommand implements Runnable {

  @Option(name = {"-e", "--extraction"}, title = "Extraction config", description = "Path that points to a valid Cineast extraction config file.")
  private String extractionConfig;

  @Option(name = {"--no-finalize"}, title = "Do Not Finalize", description = "If this flag is not set, automatically rebuilds indices & optimizes all entities when writing to cottontail after the extraction. Set this flag when you want more performance with external parallelism.")
  private boolean doNotFinalize = true;


  @Override
  public void run() {
    final ExtractionDispatcher dispatcher = new ExtractionDispatcher();
    final File file = new File(this.extractionConfig);
    if (file.exists()) {
      try {
        final JacksonJsonProvider reader = new JacksonJsonProvider();
        final IngestConfig context = reader.toObject(file, IngestConfig.class);
        final ExtractionContainerProvider provider = ExtractionContainerProviderFactory.tryCreatingTreeWalkPathProvider(file, context);
        if (dispatcher.initialize(provider, context)) {
          if (!doNotFinalize) {
            dispatcher.registerListener(new ExtractionCompleteListener() {
              @Override
              public void extractionComplete() {
                OptimizeEntitiesCommand.optimizeAllCottontailEntities();
              }
            });
          }
          dispatcher.start();
          dispatcher.registerListener((ExtractionCompleteListener) provider);
        } else {
          System.err.println(String.format("Could not start handleExtraction with configuration file '%s'. Does the file exist?", file.toString()));
        }
      } catch (IOException e) {
        System.err.println(String.format("Could not start handleExtraction with configuration file '%s' due to a IO error.", file.toString()));
        e.printStackTrace();
      } catch (ClassCastException e) {
        System.err.println("Could not register completion listener for extraction.");
      }
    } else {
      System.err.println(String.format("Could not start handleExtraction with configuration file '%s'; the file does not exist!", file.toString()));
    }
  }
}
