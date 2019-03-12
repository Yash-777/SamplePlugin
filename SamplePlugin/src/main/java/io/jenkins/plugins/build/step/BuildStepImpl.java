package io.jenkins.plugins.build.step;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import groovy.ui.SystemOutputInterceptor;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;

/**
 * <a href="https://rafaelrezend.github.io/post/jenkins-getting-started">reference...</a>
 * @author yashwanth.m
 *
 */
public class BuildStepImpl extends Builder implements SimpleBuildStep {

	/**
	 * Descriptor for {@link BuildStepImpl}. Used as a singleton.
	 * The class is marked as public so that it can be accessed from views.
	 * 
	 * Our plugin is a Build Step, found in the Add build step Drop-down. It's only shown in job types where it isApplicable().
	 * The label - "Sample Build Step" - is provided by the getDisplayName().
	 */
	@Extension // This indicates to Jenkins that this is an implementation of an extension point.
	public static final class DescriptorImpl<T> extends BuildStepDescriptor<Builder> {
		/**
		 * In order to load the persisted global configuration, you have to call load() in the constructor.
		 * Loads the data from the disk into this object. 
		 */
		public DescriptorImpl() {
			load();
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public boolean isApplicable(Class<? extends AbstractProject> jobType) {
			// Indicates that this builder can be used with all kinds of project types. as it is returning true
			return true;
		}
		
		@Override // This human readable name is used in the configuration screen.
		public String getDisplayName() {
			return "Sample Plugin Build Step";
		}
		
		/**
		 * If you need any Global configurations or Paths at Jenkins Level not Job level then write here.
		 * 
		 * The global configuration for this plugin has it own section in Jenkins > Manage Jenkins > Configure System.
		 * It provides a single checkbox, with title and description, along with a hidden helper,
		 * which is shown once the question mark (?) is pressed.
		 * 
		 * The configuration itself is described in 
		 *   << SamplePlugin/src/main/resources/io/jenkins/plugins/build/environment/BuildEnvironmentImpl/global.jelly
		 *   
		 * The helper is in html format: The helper is associated by the naming convention help-<variable>.html
		 * 
		 */
		
		// Global Properties: useCapitalGlobal (global.jelly)
		/**
		 * To persist global configuration information, simply store it in a field and call save().
		 *
		 * <p>
		 * If you don't want fields to be persisted, use <tt>transient</tt>.
		 */
		private boolean useCapitalGlobal;
		/**
		 * This method returns true if the global configuration says that we should use Capital Letters.
		 *
		 * The method name is bit awkward because global.jelly calls this method to determine
		 * the initial state of the CheckBox by the naming convention.
		 */
		public boolean getUseCapitalGlobal() {
			return useCapitalGlobal;
		}
		// Global properties from global.jelly are initialized from configure method.
		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			// To persist global configuration information, set that to properties and call save().
			useCapitalGlobal = formData.getBoolean("useCapitalGlobal");
			System.out.println("BuildStepDescriptor configure - useCapitalGlobal: "+ useCapitalGlobal);
			
			// ^Can also use req.bindJSON(this, formData);
			//  (easier when there are many fields; need set methods for this, like setUseFrench)
			save();
			return super.configure(req, formData);
		}
		
		/**
		 * Performs on-the-fly validation of the form field 'nameConfig' form file (config.jelly).
		 * After entering the data, when user click outside of the section this function gets executed.
		 * 
		 * Form Validation 
		 *   - https://wiki.jenkins.io/display/JENKINS/Form+Validation
		 *   - https://jenkins.io/doc/developer/security/form-validation/
		 * 
		 * <p>
		 * Jenkins has a mechanism to statically validate user inputs in the DescriptorImpl class.
		 * The validation method is bound to the variables using another specific naming convention.
		 * For instance, our example validates the name input with the DescriptorImpl.doCheckName(...) method,
		 * which shows an error message for empty field, or a warning message for a name with less than 4 characters.
		 * </p>
		 * @param  value This parameter receives the value that the user has typed.
		 * @return Indicates the outcome of the validation. This is sent to the browser.
		 *	  <p>
		 *	  Note that returning {@link FormValidation#error(String)} does not
		 *	  prevent the form from being saved. It just means that a message
		 *	  will be displayed to the user. 
		 */
		public FormValidation doCheckNameConfig(@QueryParameter String value) throws IOException, ServletException {
			if (value.length() == 0) {
				return FormValidation.error("Please set a name");
			}
			if (value.length() < 4) {
				return FormValidation.warning("Isn't the name too short?");
			}
			String upperCaseValue = value.toUpperCase();
			// ES_COMPARING_STRINGS_WITH_EQ : Comparison of String objects using == or !=.
			if (useCapitalGlobal && !(upperCaseValue.equals(value)) ) {
				// Globally selected CAPITAL, So use capitals.
				//Properties props = CommonsUtil.getProps();
				
				String message = props.getProperty("global.form.warning.msg");
				if(message == null) message = "use capital letters!";
				return FormValidation.warning(message);
			}
			return FormValidation.ok();
		}
		
		// DescriptorImpl.props is a mutable collection which should be package protected
		// MS_MUTABLE_COLLECTION_PKGPROTECT - Make props as private field.
		private static final Properties props = new Properties();
		static {
			ClassLoader classLoader = BuildStepImpl.class.getClassLoader();
			InputStream resourceAsStream = classLoader.getResourceAsStream("config.properties");
			try {
				props.load(resourceAsStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// Build JOB Configuration Properties: field:nameConfig (config.jelly)
	private final String nameConfig;
	// Fields in config.jelly must match the parameter names in the "DataBoundConstructor" and Initialized form Constructor.
	@DataBoundConstructor
	public BuildStepImpl(String nameConfig) {
		this.nameConfig = nameConfig;
	}
	// We'll use this from the <tt>config.jelly</tt>. We can create only getter function as field is final.
	// The main use is User entered some value save to java. Later you want to change the JOB_Configuration then
	// config.jelly will get the field value form this getter function and display over UI.
	public String getNameConfig() {
		return nameConfig;
	}
	
	// Overridden for better type safety. If your plugin doesn't really define
	// any property on Descriptor, you don't have to do this.
	@Override
	public DescriptorImpl<Builder> getDescriptor() {
		return (DescriptorImpl<Builder>) super.getDescriptor();
	}
	// When user enters some data in config.jelly from field, then the following action takes place.
	@Override
	public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
		// This is where you 'build' the project. Since this is a dummy, we just say 'hello world' and call that a build.

		// From the above `@Override` method of Builder we get the global properties.
		// This also shows how you can consult the global configuration of the builder
		if (getDescriptor().getUseCapitalGlobal())
			listener.getLogger().println("Globally selected CAPITAL, Name: "+nameConfig);
		else
			listener.getLogger().println("User Defauls, Name: "+nameConfig);
	}
}