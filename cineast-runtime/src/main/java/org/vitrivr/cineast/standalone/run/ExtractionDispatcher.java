package org.vitrivr.cineast.standalone.run;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.extraction.ExtractionContextProvider;
import org.vitrivr.cineast.standalone.config.Config;
import org.vitrivr.cineast.core.data.MediaType;
import org.vitrivr.cineast.standalone.run.filehandler.AudioExtractionFileHandler;
import org.vitrivr.cineast.standalone.run.filehandler.GenericExtractionItemHandler;
import org.vitrivr.cineast.standalone.run.filehandler.ImageExtractionFileHandler;
import org.vitrivr.cineast.standalone.run.filehandler.ImageSequenceExtractionFileHandler;
import org.vitrivr.cineast.standalone.run.filehandler.Model3DExtractionFileHandler;
import org.vitrivr.cineast.standalone.run.filehandler.VideoExtractionFileHandler;

import java.io.File;
import java.io.IOException;

/**
 * @author rgasser
 * @version 1.0
 * @created 13.01.17
 */
public class ExtractionDispatcher {

  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * ExtractionContextProvider used to setup the extraction.
   */
  private ExtractionContextProvider context;

  /**
   * List of files due for extraction.
   */
  private ExtractionContainerProvider pathProvider;

  /**
   * Reference to the thread that is being used to run the ExtractionFileHandler.
   */
  private Thread fileHandlerThread;

  private ExtractionItemProcessor handler;

  private volatile boolean threadRunning = false;

  public boolean initialize(ExtractionContainerProvider pathProvider,
      ExtractionContextProvider context) throws IOException {
    File outputLocation = Config.sharedConfig().getExtractor().getOutputLocation();
    if (outputLocation == null) {
      LOGGER.error("invalid output location specified in config");
      return false;
    }
    outputLocation.mkdirs();
    if (!outputLocation.canWrite()) {
      LOGGER.error("cannot write to specified output location: '{}'",
          outputLocation.getAbsolutePath());
      return false;
    }

    this.pathProvider = pathProvider;
    this.context = context;

    if (this.fileHandlerThread == null) {
      MediaType sourceType = this.context.sourceType();
      if (sourceType == null) {
        this.handler = new GenericExtractionItemHandler(this.pathProvider, this.context);
        this.fileHandlerThread = new Thread((GenericExtractionItemHandler) handler);
      } else {
        switch (sourceType) {
          case IMAGE:
            handler = new ImageExtractionFileHandler(this.pathProvider, this.context);
            this.fileHandlerThread = new Thread((ImageExtractionFileHandler) handler);
            break;
          case VIDEO:
            this.handler = new VideoExtractionFileHandler(this.pathProvider,
                this.context);
            this.fileHandlerThread = new Thread((VideoExtractionFileHandler) handler);
            break;
          case AUDIO:
            this.handler = new AudioExtractionFileHandler(this.pathProvider,
                this.context);
            this.fileHandlerThread = new Thread((AudioExtractionFileHandler) handler);
            break;
          case MODEL3D:
            this.handler = new Model3DExtractionFileHandler(this.pathProvider,
                this.context);
            this.fileHandlerThread = new Thread((Model3DExtractionFileHandler) handler);
            break;
          case IMAGE_SEQUENCE:
            this.handler = new ImageSequenceExtractionFileHandler(this.pathProvider, this.context);
            this.fileHandlerThread = new Thread((ImageSequenceExtractionFileHandler) handler);
            break;
          default:
            break;
        }
      }
    } else {
      LOGGER.warn("You cannot initialize the current instance of ExtractionDispatcher again!");
    }

    return this.pathProvider.isOpen();
  }

  /**
   * Starts extraction by dispatching a new ExtractionFileHandler thread.
   *
   * @throws IOException If an error occurs during pre-processing of the files.
   */
  public synchronized void start() throws IOException {
    if (fileHandlerThread != null && !threadRunning) {
      this.fileHandlerThread.setName("extraction-file-handler-thread");
      this.fileHandlerThread.start();
      threadRunning = true;
    }else{
      LOGGER.warn("You cannot start the current instance of ExtractionDispatcher again!");
    }
  }

  public void registerListener(ExtractionCompleteListener listener) {
    if (this.fileHandlerThread == null) {
      LOGGER.error("Could not register listener, no thread available");
      throw new RuntimeException();
    }
    LOGGER.debug("Registering Listener {}", listener.getClass().getSimpleName());
    handler.addExtractionCompleteListener(listener);
  }
}