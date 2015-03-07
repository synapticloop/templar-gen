package synapticloop.templar.gen;

import java.io.File;
import java.net.URL;

public class Main {
	private static void usage(String message) {
		if(null != message) {
			System.out.println("Error occurred, message was:");
			System.out.println("\t" + message);
		}

		System.out.println("Usage:");
		System.out.println("\tjava " + Main.class.getSimpleName() + " <input_directory> <output_directory>");
		System.exit(-1);
	}

	private static void usage() {
		usage(null);
	}

	public static void main(String[] args) {
		if(args.length != 2) {
			usage();
		} else {
			// kick of the generation
			String inputDir = args[0];
			checkDirectory(inputDir, false);
			String outputDir = args[1];
			checkDirectory(outputDir, true);
			
			Generator generator = new Generator();
		}
	}
	
	private static void checkDirectory(String directory, boolean create) {
		String temp = "";
		if(directory.startsWith("/")) {
			// looking from the root of the file system
			temp = directory;
		} else {
			// else from current directory... (probably not correct)

			URL location = Main.class.getProtectionDomain().getCodeSource().getLocation();
			String rootDir = location.getFile();

			if(rootDir.endsWith(".jar")) {
				// we are running this from the jar file
				rootDir = rootDir.substring(0, rootDir.lastIndexOf("/") + 1);
			}
			
			temp = rootDir + directory;
		}

		// now check to see whether the directory exists
		File file = new File(temp);
		if(file.exists() && !file.isDirectory()) {
			usage("Directory '" + temp + " exists and is not a directory.  Please remove the file.");
		}

		if(!file.exists() && create) {
			file.mkdirs();
		}
	}
}
