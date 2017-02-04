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
import java.util.ArrayList;
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

	private ArrayList<String> nonGeneratedFiles = new ArrayList<String>();
	private int numFilesGenerated = 0;

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

		final Properties defaultProperties = new Properties();
		// now load up the default context
		File initialTemplarContextFile = new File(start.toAbsolutePath() + "/.context");
		if(initialTemplarContextFile.exists() && initialTemplarContextFile.canRead() && initialTemplarContextFile.isFile()) {
			defaultProperties.load(new FileReader(initialTemplarContextFile));
			SimpleLogger.logInfo("Loaded default context items '" + inputDir + "/.context'");
		} else {
			SimpleLogger.logInfo("No default context file found '" + inputDir + "/.context'");
		}

		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String inputTemplarContextFile = file.toString();
				String fileName = file.getFileName().toString();

				if (inputTemplarContextFile.endsWith(TEMPLAR_CONTEXT_FILE_EXTENSION) && 
						!fileName.equals(TEMPLAR_CONTEXT_FILE_EXTENSION)) {
					// look for another file without the .context
					String inputTemplarFileName = inputTemplarContextFile.substring(0, inputTemplarContextFile.indexOf(TEMPLAR_CONTEXT_FILE_EXTENSION));
					File inputTemplarFile = new File(inputTemplarFileName);

					if(inputTemplarFile.exists()) {
						// do the processing
						TemplarContext templarContext = new TemplarContext();
						Properties properties = new Properties();
						properties.load(new FileReader(new File(inputTemplarContextFile)));
						properties.putAll(defaultProperties);

						Enumeration<Object> keys = properties.keys();
						while (keys.hasMoreElements()) {
							String key = (String) keys.nextElement();
							templarContext.add(key, properties.get(key));
						}

						FileWriter fileWriter = null;
						try {
							Parser parser = new Parser(inputTemplarFile);

							// now we need to figure out the output directory and the output file name
							String outputFile = inputTemplarFile.getAbsolutePath().replaceFirst(inputDir, outputDir);

							// the output directory creation
							File outputDirectory = new File(outputFile.substring(0, outputFile.lastIndexOf("/")));
							if(!outputDirectory.exists()) {
								SimpleLogger.logInfo("Creating directory '" + outputDir + "'.");
								outputDirectory.mkdirs();
							}

							fileWriter = new FileWriter(outputFile);
							fileWriter.write(parser.render(templarContext));
							SimpleLogger.logInfo("Processing input file '" + inputTemplarFile);
							SimpleLogger.logInfo("         with context '" + inputTemplarContextFile);
							SimpleLogger.logInfo("            output to '" + outputFile);
							numFilesGenerated++;
						} catch (ParseException pex) {
							SimpleLogger.logFatal("Could not parse file '" + inputTemplarFileName + "'.", pex);
							nonGeneratedFiles.add(inputTemplarFileName);
						} catch (RenderException rex) {
							SimpleLogger.logFatal("Could not parse file '" + inputTemplarFileName + "'.", rex);
							nonGeneratedFiles.add(inputTemplarFileName);
						} finally {
							if(null != fileWriter) {
								fileWriter.close();
							}
						}
					} else {
						SimpleLogger.logWarn("Found templar file '" + inputTemplarContextFile + "', however no input file, expecting '" + inputTemplarFile + "'.");
					}
				}
				return FileVisitResult.CONTINUE;
			}
		});

		SimpleLogger.logInfo(":---------------------------------------------------:");
		SimpleLogger.logInfo("Generated " + numFilesGenerated + " files.");

		for (String nonGeneratedFile : nonGeneratedFiles) {
			SimpleLogger.logFatal("Failed to generate the following file '" + nonGeneratedFile + "'.");
		}

		int numNonGeneratedFiles = nonGeneratedFiles.size();
		if(numNonGeneratedFiles > 0) {
			SimpleLogger.logFatal(numNonGeneratedFiles + " files failed to generate, please see the output for more details.");
		}
	}
}
