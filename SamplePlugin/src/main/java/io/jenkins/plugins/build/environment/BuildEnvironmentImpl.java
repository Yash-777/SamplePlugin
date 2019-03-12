package io.jenkins.plugins.build.environment;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.build.step.BuildStepImpl.DescriptorImpl;

public class BuildEnvironmentImpl extends BuildWrapper implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Extension // This indicates to Jenkins that this is an implementation of an extension point.
	public static final class DescriptorImpl extends BuildWrapperDescriptor {
		/**
		 * In order to load the persisted global configuration, you have to call load() in the constructor.
		 * Loads the data from the disk into this object. 
		 */
		public DescriptorImpl() {
			load();
		}
		
		@Override
		public boolean isApplicable(AbstractProject<?, ?> item) {
			// Indicates that this builder can be used with all kinds of project types. as it is returning true
			return true;
		}
		
		// This human readable name displayed in the configuration screen. if not provided uses class name.
		@Override
		public String getDisplayName() {
			return "Sample Plugin Build Environment";
		}
		
		// For Selection options no need to use doCheckGoalTypeModelItems()
		public ListBoxModel doFillGoalTypeModelItems() {
			ListBoxModel items = new ListBoxModel();
			
			// Get the data either hard coded or form the server.
			LinkedHashMap<String, String> data = BuildGoalUtil.getData();
			
			Set<Entry<String, String>> entrySet = data.entrySet();
			for (Iterator<Entry<String, String>> iterator = entrySet.iterator(); iterator.hasNext();) {
				Entry<String, String> entry = (Entry<String, String>) iterator.next();
				items.add(entry.getKey(), entry.getValue()); // (usedForDisplayName, value)
			}
			return items;
		}
		
		// To get the latest value form the user.
		public FormValidation doCheckGoalTypeModel(@QueryParameter String value) throws IOException, ServletException {
			System.out.println("User Selected Goal : "+ value);
			this.instanceData.put("goalTypeModel", value);
			return FormValidation.ok();
		}
		
		// To store the jelly field data to java.
		private Map<String, String> instanceData = new HashMap<>();
		public Map<String, String> getInstanceData() {
			return instanceData;
		}
		public void setInstanceData(Map<String, String> instanceData) {
			this.instanceData = instanceData;
		}
	}
	
	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}
	
	// Build JOB Configuration Properties: field:goalTypeModel (config.jelly)
	private final String goalTypeModel;
	// Fields in config.jelly must match the parameter names in the "DataBoundConstructor" and Initialized form Constructor.
	@DataBoundConstructor
	public BuildEnvironmentImpl(String goalTypeModel) {
		this.goalTypeModel = goalTypeModel;
	}
	// We can create only getter function as field is final.
	public String getGoalTypeModel() {
		return goalTypeModel;
	}
}