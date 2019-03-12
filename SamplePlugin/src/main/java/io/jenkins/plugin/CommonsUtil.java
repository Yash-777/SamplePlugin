package io.jenkins.plugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * As we can't extend this class in other Build classes then we need to implement their only.
 * @author yashwanth.m
 *
 */
public class CommonsUtil {
	// MS_MUTABLE_COLLECTION_PKGPROTECT: Which should be package protected - https://stackoverflow.com/a/35657830/5081877
	/*protected static final Properties props = new Properties();
	public static Properties getProps() {
		return props;
	}*/
	
	/*
	[INFO] --- findbugs-maven-plugin:3.0.5:check (findbugs) @ SamplePlugin ---
	[INFO] BugInstance size is 2
	[INFO] Error size is 0
	[INFO] Total bugs: 2
	[INFO] io.jenkins.plugin.CommonsUtil.props isn't final but should be [io.jenkins.plugin.CommonsUtil]
	       At CommonsUtil.java:[line 8] MS_SHOULD_BE_FINAL
 	       
 	       At CommonsUtil.java:[line 8] MS_MUTABLE_COLLECTION
 	       
	To see bug detail using the Findbugs GUI, use the following command "mvn findbugs:gui"
	*/
	/*static {
		ClassLoader classLoader = CommonsUtil.class.getClassLoader();
		InputStream resourceAsStream = classLoader.getResourceAsStream("config.properties");
		try {
			props.load(resourceAsStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}