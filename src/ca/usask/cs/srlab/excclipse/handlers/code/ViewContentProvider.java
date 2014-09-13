package ca.usask.cs.srlab.excclipse.handlers.code;

import java.util.ArrayList;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import core.code.Result;

public class ViewContentProvider implements IStructuredContentProvider {

	ArrayList<Result> collectedResults;
	
	public ViewContentProvider()
	{
		//default constructor
		this.collectedResults=new ArrayList<>();
	}
	
	public ViewContentProvider(ArrayList<Result> results)
	{
		//initialization
		this.collectedResults=results;
	}
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getElements(Object inputElement) {
		// TODO Auto-generated method stub
		//Result[] results=FragmentProvider.provideDummyResults();
		Result[] results=new Result[this.collectedResults.size()];
		return this.collectedResults.toArray(results);
	}

}
