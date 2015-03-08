package synapticloop.templar.gen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.Properties;

import synapticloop.templar.Parser;
import synapticloop.templar.exception.ParseException;
import synapticloop.templar.exception.RenderException;
import synapticloop.templar.utils.TemplarContext;

public class Generator {

	private static final String TEMPLAR_CONTEXT_FILE_EXTENSION = ".context";
	private String inputDir;
	private String outputDir;

	public Generator(String inputDir, String outputDir) {
		this.inputDir = inputDir;
		this.outputDir = outputDir;
		if(inputDir.endsWith("/") && !outputDir.endsWith("/")) {
			this.outputDir += "/";
		}

		if(outputDir.endsWith("/") && !inputDir.endsWith("/")) {
			this.inputDir += "/";
		}
	}

	public void generate() throws IOException {
		// walk the input dir and get all of the files
		SimpleLogger.logInfo("Generating");
		SimpleLogger.logInfo("  from: " + inputDir);
		SimpleLogger.logInfo("    to: " + outputDir);


		Path start = FileSystems.getDefault().getPath(inputDir);

		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String inputTemplarContextFile = file.toString();

				if (inputTemplarContextFile.endsWith(TEMPLAR_CONTEXT_FILE_EXTENSION)) {
					// look for another file without the .context
					File inputTemplarNonContextFile = new File(inputTemplarContextFile.substring(0, inputTemplarContextFile.indexOf(TEMPLAR_CONTEXT_FILE_EXTENSION)));

					if(inputTemplarNonContextFile.exists()) {
						// do the processing
						TemplarContext templarContext = new TemplarContext();
						Properties properties = new Properties();
						properties.load(new FileReader(new File(inputTemplarContextFile)));

						Enumeration<Object> keys = properties.keys();
						while (keys.hasMoreElements()) {
							String key = (String) keys.nextElement();
							templarContext.add(key, properties.get(key));
						}

						FileWriter fileWriter = null;
						try {
							Parser parser = new Parser(inputTemplarNonContextFile);

							// now we need to figure out the output directory and the output file name
							String outputFile = inputTemplarNonContextFile.getAbsolutePath().replaceFirst(inputDir, outputDir);

							// the output directory creation
							File outputDirectory = new File(outputFile.substring(0, outputFile.lastIndexOf("/")));
							if(!outputDirectory.exists()) {
								SimpleLogger.logInfo("Creating directory '" + outputDir + "'.");
								outputDirectory.mkdirs();
							}

							fileWriter = new FileWriter(outputFile);
							fileWriter.write(parser.render(templarContext));
							SimpleLogger.logInfo("Processing input file '" + inputTemplarNonContextFile + "', with context '" + inputTemplarContextFile + "', to " + outputFile); 
						} catch (ParseException ex) {
							ex.printStackTrace();
						} catch (RenderException ex) {
							ex.printStackTrace();
						} finally {
							if(null != fileWriter) {
								fileWriter.close();
							}
						}
					} else {
						SimpleLogger.logWarn("Found templar file '" + inputTemplarContextFile + "', however no input file, expecting '" + inputTemplarNonContextFile + "'.");
					}
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}}
