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

	private String inputDir;
	private String outputDir;

	public Generator(String inputDir, String outputDir) {
		this.inputDir = inputDir;
		this.outputDir = outputDir;
	}

	public void generate() throws IOException {
		// walk the input dir and get all of the files
		Path start = FileSystems.getDefault().getPath(inputDir);

		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				String templarContextFile = file.toString();
				if (templarContextFile.endsWith(".templar")) {
					// look for another file without the templar
					String inputTemplarFile = templarContextFile.substring(0, templarContextFile.indexOf(".templar"));
					File inputFile = new File(inputTemplarFile);
					if(inputFile.exists()) {
						// do the processing
						TemplarContext templarContext = new TemplarContext();
						Properties properties = new Properties();
						properties.load(new FileReader(new File(templarContextFile)));

						Enumeration<Object> keys = properties.keys();
						while (keys.hasMoreElements()) {
							String key = (String) keys.nextElement();
							templarContext.add(key, properties.get(key));
						}

						FileWriter fileWriter = null;
						File newOutputDir = null;
						try {
							Parser parser = new Parser(inputFile);

							newOutputDir = new File(outputDir + "/" + inputTemplarFile.substring(0, inputTemplarFile.lastIndexOf("/")).substring(inputDir.length()));
							newOutputDir.mkdirs();
							String outputGeneratedFile = (newOutputDir + "/" + inputTemplarFile.substring(inputDir.length())).replaceAll("//", "/");
							fileWriter = new FileWriter(outputGeneratedFile);
							fileWriter.write(parser.render(templarContext));
							SimpleLogger.logInfo("Processing input file '" + inputTemplarFile + "', with context '" + templarContextFile + "', to " + outputGeneratedFile);
						} catch (ParseException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						} catch (RenderException ex) {
							// TODO Auto-generated catch block
							ex.printStackTrace();
						} finally {
							if(null != fileWriter) {
								fileWriter.close();
							}
						}
					} else {
						SimpleLogger.logWarn("Found templar file '" + templarContextFile + "', however no input file, expecting '" + inputTemplarFile + "'.");
					}
				}
				return FileVisitResult.CONTINUE;
			}
		});
	}}
