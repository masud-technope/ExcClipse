package ca.usask.cs.srlab.excclipse.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

public class SuggestionView extends ViewPart {

	public static final String ID = "ca.usask.cs.srlab.excclipse.views.SuggestionView";
	public Browser browser;

	protected void addContentBrowser(Composite parent)
	{
		//adding the web browser
		browser=new Browser(parent, SWT.NONE);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub
		GridLayout glayout = new GridLayout();
		glayout.marginWidth = 15;
		glayout.marginHeight = 10;
		parent.setLayout(glayout);

		GridData gdata = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent.setLayoutData(gdata);
		addContentBrowser(parent);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
