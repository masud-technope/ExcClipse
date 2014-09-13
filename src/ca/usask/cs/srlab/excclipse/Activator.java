package ca.usask.cs.srlab.excclipse;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import history.RecencyScoreManager;
import mode.SurfClipseModeManager;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import ca.usask.cs.srlab.excclipse.handlers.BookmarkManager;



/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "ca.usask.cs.srlab.excclipse"; //$NON-NLS-1$
	
	//Recency score manager
	public static RecencyScoreManager recenyScoreManager;

	// The shared instance
	public static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		//recenyScoreManager=new RecencyScoreManager();
		//recenyScoreManager.calculate_recency_score();
		//Display.getDefault().asyncExec(new ActiveConsoleChecker());
		new SurfClipseModeManager().check_current_mode_settings();
		if(SurfClipseModeManager.current_mode==1)
		{
		new Thread(new ActiveConsoleChecker()).start();
		}
		
		//creating bookmark file
		BookmarkManager.createBookMarkFile();
		//configure firefox
		//configureFFBrowser();
	}

	protected void configureFFBrowser()
	{
		//configure FireFox browser for IDE
		Bundle bundle=Platform.getBundle("org.mozilla.xulrunner");
		if(bundle!=null){
			URL resourceUrl = bundle.getResource("xulrunner"); //$NON-NLS-1$
		    if (resourceUrl != null) {
		        try {
		            URL fileUrl = FileLocator.toFileURL(resourceUrl);
		            File file = new File(fileUrl.toURI());
		            System.setProperty("org.eclipse.swt.browser.DefaultType",
		                    "mozilla");
		            System.setProperty(
		                    "org.eclipse.swt.browser.XULRunnerPath", file.getAbsolutePath()); //$NON-NLS-1$

		        } catch (IOException e) {
		            e.printStackTrace();
		        } catch (URISyntaxException e) {
		            e.printStackTrace();
		        }
		    }
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		//recenyScoreManager.save_recent_files();
		new SurfClipseModeManager().save_current_mode_settings();
	}
	
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
