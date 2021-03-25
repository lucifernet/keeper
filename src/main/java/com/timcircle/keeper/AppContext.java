package com.timcircle.keeper;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;

import com.timcircle.keeper.util.JsonUtil;

public class AppContext {
	private File rootDirectory;
	private File confDirectory;
	private File jobsDirectory;
	private File extsDirectory;

	public AppContext(String rootDir) throws FileNotFoundException {
		this.rootDirectory = new File(rootDir);
		if (!this.rootDirectory.exists())
			throw new FileNotFoundException("Root directory does not exist : " + rootDir);

		this.confDirectory = new File(rootDirectory, "conf");
		if (!this.confDirectory.exists())
			throw new FileNotFoundException(
					"Configuration directory does not exist : " + confDirectory.getAbsolutePath());

		File log4jProperties = new File(this.confDirectory, "log4j.properties"); 
		if(log4jProperties.exists())
			PropertyConfigurator.configure(log4jProperties.getAbsolutePath());
		
		this.jobsDirectory = new File(rootDirectory, "jobs");
		if (!this.jobsDirectory.exists())
			throw new FileNotFoundException("Jobs directory does not exist : " + jobsDirectory.getAbsolutePath());

		this.extsDirectory = new File(rootDirectory, "ext");
	}

	public File getRootDirectory() {
		return rootDirectory;
	}

	public File getConfDirectory() {
		return confDirectory;
	}

	public File getJobsDirectory() {
		return jobsDirectory;
	}

	public JsonUtil getConfig() throws FileNotFoundException {
		File config = new File(confDirectory, "config.json");
		if (!config.exists())
			throw new FileNotFoundException("config.json does not exist.");
		return JsonUtil.toJsonUtil(config);
	}

	public List<Map<String, Object>> getExtJobs() {
		List<Map<String, Object>> jobs = new ArrayList<>();
		for (File sub : this.jobsDirectory.listFiles()) {
			if (sub.getName().endsWith(".job")) {
				try {
					Map<String, Object> job = JsonUtil.toMap(sub);
					jobs.add(job);
				} catch (FileNotFoundException e) {

				}
			}
		}
		return jobs;
	}

	public File getExtsDirectory() {
		return extsDirectory;
	}
}
