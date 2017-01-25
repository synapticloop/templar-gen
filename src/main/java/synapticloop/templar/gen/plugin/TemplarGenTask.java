package synapticloop.templar.gen.plugin;

import java.io.IOException;

/*
 * Copyright (c) 2016 Synapticloop.
 * 
 * All rights reserved.
 * 
 * This code may contain contributions from other parties which, where 
 * applicable, will be listed in the default build file for the project 
 * ~and/or~ in a file named CONTRIBUTORS.txt in the root of the project.
 * 
 * This source code and any derived binaries are covered by the terms and 
 * conditions of the Licence agreement ("the Licence").  You may not use this 
 * source code or any derived binaries except in compliance with the Licence.  
 * A copy of the Licence is available in the file named LICENSE.txt shipped with 
 * this source code or binaries.
 */

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import synapticloop.templar.gen.Generator;

public class TemplarGenTask extends DefaultTask {
	/**
	 * Instantiate the task, setting the group and description
	 */
	public TemplarGenTask() {
		super.setGroup("Generation");
		super.setDescription("Generates static pages.");
	}

	/**
	 * Generate the README file from the documentr.json input file
	 * 
	 * @throws IOException If there was an error generating the static files
	 */
	@TaskAction
	public void generate() throws IOException {
		TemplarGenPluginExtension extension = getProject().getExtensions().findByType(TemplarGenPluginExtension.class);

		if (extension == null) {
			extension = new TemplarGenPluginExtension();
		}

		Generator generator = new Generator(extension.getIn(), extension.getOut());
		generator.generate();
	}
}
