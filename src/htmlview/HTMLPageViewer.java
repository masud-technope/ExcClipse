package htmlview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class HTMLPageViewer {

	String content;

	public HTMLPageViewer(String content) {
		// custom constructor
		this.content = content;
	}

	public HTMLPageViewer() {
		// default constructor
		this.content = "This is a simple blank page";
	}

	public void view() {
		// show the HTML browser
		try {
			Display display = new Display();
			Shell shell = new Shell(display);
			GridLayout gridLayout = new GridLayout();
			gridLayout.marginHeight = -1;
			gridLayout.marginHeight = -1;
			gridLayout.numColumns = 1;
			shell.setLayout(gridLayout);

			GridData data = new GridData();
			final Browser browser = new Browser(shell, SWT.TOOL);
			data = new GridData(GridData.FILL_HORIZONTAL
					| GridData.FILL_VERTICAL);
			data.horizontalSpan = 1;
			browser.setLayoutData(data);

			shell.open();
			// browser.setText("This is a simple text");
			browser.setText(this.content);
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
			display.dispose();
		} catch (SWTException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new HTMLPageViewer().view();
	}

}
