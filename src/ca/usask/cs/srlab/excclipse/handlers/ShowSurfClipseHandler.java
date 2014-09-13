package ca.usask.cs.srlab.excclipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

public class ShowSurfClipseHandler extends AbstractHandler{

	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// code for event handling on the menu item
		try {
			//code for showing SurfClipse View
			
			
			String SCviewID="ca.usask.cs.srlab.excclipse.views.SurfClipseClientView";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SCviewID);
			
			String SCBviewID="ca.usask.cs.srlab.excclipse.views.SurfClipseBrowser";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SCBviewID);
			
			String SEviewID="ca.usask.cs.srlab.excclipse.views.SurfExampleClientView";
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(SEviewID);
			
			System.out.println("Surfclipse windows shown successfully");
			
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}
	
}
