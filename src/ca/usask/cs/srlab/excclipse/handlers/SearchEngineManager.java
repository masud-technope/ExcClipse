package ca.usask.cs.srlab.excclipse.handlers;

import java.util.ArrayList;

import gaecore.BingAPI;
import gaecore.GoogleAPI;
import gaecore.YahooAPI2;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import core.Result;
import ca.usask.cs.srlab.excclipse.views.SurfClipseClientView;

public class SearchEngineManager {
	
	String searchQuery;
	int engineIndex;
	
	public SearchEngineManager(String searchQuery, int engineIndex)
	{
		this.searchQuery=searchQuery;
		this.engineIndex=engineIndex;
	}
	
	public void fireWebSearch()
	{
		//executing web search
		ArrayList<Result> results=new ArrayList<>();
		switch(engineIndex)
		{
		case 0:
			GoogleAPI gapi=new GoogleAPI();
			results=gapi.find_Google_Results(searchQuery);
			break;
		case 1:
			BingAPI bapi=new BingAPI();
			results=bapi.find_Bing_Results(searchQuery);
			break;
		case 2:
			YahooAPI2 yapi=new YahooAPI2();
			results=yapi.get_Yahoo_Results(searchQuery);
			break;
		}
		//now populate the results
		update_surfclipse_view(results.toArray(new Result[results.size()]));
		
	}
	
	protected void update_surfclipse_view(Result[] collectedResults)
	{
		//code for updating surfClipse view
		try
		{
			IWorkbenchPage page =(IWorkbenchPage)PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage();
			String viewID="ca.usask.cs.srlab.excclipse.views.SurfClipseClientView";
			IViewPart vpart=page.findView(viewID);
			SurfClipseClientView myview=(SurfClipseClientView)vpart;
			//System.out.println(myview.viewer.toString());
			ViewContentProvider viewContentProvider=new ViewContentProvider(collectedResults);
			myview.viewer.setContentProvider(viewContentProvider);
			//myview.viewer.setSorter(new TableColumnSorter());
			//myview.viewer.setInput(this.getvi);
		}catch(Exception exc){
			//System.err.println(exc.getMessage());
			//System.err.println("Failed to update Eclipse view"+exc.getMessage());
			exc.printStackTrace();
			String message="Failed to collect search results. Please try again.";
			showMessageBox(message);
			
		}
	}

	protected void showMessageBox(String message) {
		// code for showing message box
		try {
			Shell shell = Display.getDefault().getShells()[0];
			MessageDialog.openInformation(shell, "Information", message);
		} catch (Exception exc) {
		}
	}
	
	
	

}
