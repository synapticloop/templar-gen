package synapticloop.templar.gen.plugin;

/*
 * Copyright (c) 2017 Synapticloop.
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

public class TemplarGenPluginExtension {
	private String inDir = "src";
	private String out = ".";

	public void setInDir(String inDir) { this.inDir = inDir; }
	public String getInDir() { return this.inDir; }
	public void setOut(String out) { this.out = out; }
	public String getOut() { return this.out; }

}
