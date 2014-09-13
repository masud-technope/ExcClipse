package ca.usask.cs.srlab.excclipse.views;

import googlesuggest.GoogleSuggestProvider;
import history.HistoryLink;
import history.RecencyScoreManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mode.SurfClipseModeManager;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.part.*;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.TextLayout;
import org.eclipse.swt.graphics.TextStyle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.*;
import org.eclipse.swt.SWT;

import utility.ContentLoader;
import ca.usask.cs.srlab.excclipse.Activator;
import ca.usask.cs.srlab.excclipse.ActiveConsoleChecker;
import ca.usask.cs.srlab.excclipse.handlers.SearchEngineManager;
import ca.usask.cs.srlab.excclipse.handlers.SearchEventManager;
import core.QueryRecommender;
import core.Result;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class SurfClipseClientView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String Client_View_ID = "ca.usask.cs.srlab.excclipse.views.SurfClipseClientView";

	public TableViewer viewer;
	// private Action action1;
	// private Action action2;
	// private Action doubleClickAction;
	// array containing Tab URL

	Display display = null;
	Shell shell = null;
	static ArrayList<String> suggestions = new ArrayList<>();
	public Label timerLabel;
	public Text input = null;
	ContentProposalAdapter adapter = null;
	static FocusListener flistener = null;
	GridLayout gridLayout = null;
	final int TEXT_MARGIN = 3;
	final int MIN = 55;
	Result currentResult = null;
	int lastSelectedIndex = -1;
	HashMap<String,Result> cache=new HashMap<>();
	Browser browser=null;

	final Display currDisplay = Display.getCurrent();
	final TextLayout textLayout = new TextLayout(currDisplay);
	Font font1 = new Font(currDisplay, "Arial", 12, SWT.BOLD);
	Font font2 = new Font(currDisplay, "Arial", 10, SWT.NORMAL);
	Font font3 = new Font(currDisplay, "Arial", 10, SWT.NORMAL);
	Color blue = currDisplay.getSystemColor(SWT.COLOR_BLUE);
	Color green = currDisplay.getSystemColor(SWT.COLOR_DARK_GREEN);
	Color gray = currDisplay.getSystemColor(SWT.COLOR_DARK_GRAY);
	TextStyle style1 = new TextStyle(font1, blue, null);
	TextStyle style2 = new TextStyle(font2, green, null);
	TextStyle style3 = new TextStyle(font3, gray, null);

	// search engines
	Button google, bing, yahoo, excclipse;
	Button associateContext;
	Button googleSuggest, excclipseSuggest;

	/*
	 * The content provider class is responsible for providing objects to the
	 * view. It can wrap existing objects in adapters or simply return objects
	 * as-is. These objects may be sensitive to the current input of the view,
	 * or ignore it and always show the same content (like Task List, for
	 * example).
	 */

	class ViewContentProvider implements IStructuredContentProvider {

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {

		}

		public void dispose() {

		}

		public Object[] getElements(Object parent) {
			return new String[] {};
		}
	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			// Object myobj=getText(obj);
			Result myresult = (Result) obj;
			switch (index) {
			case 0:
				if (myresult.title != null)
					return myresult.title + "\n" + myresult.resultURL + "\n"
							+ myresult.description;
				return "";
				/*
				 * case 1: if(myresult.resultURL!=null)return
				 * myresult.resultURL; return "";
				 */
			case 1:
				if (myresult.totalScore_content_context_popularity >= 0)
					return String
							.format("%.2f",
									myresult.totalScore_content_context_popularity * 100);
				return "";
			case 2:
				double content_relevance = myresult.content_score;
				return String.format("%.2f", content_relevance * 100);
			case 3:
				double context_relevance = myresult.context_score;
				return String.format("%.2f", context_relevance * 100);
			case 4:
				double popularity = myresult.popularity_score;
				return String.format("%.2f", popularity * 100);
			case 5:
				double confidence = myresult.search_result_confidence;
				return String.format("%.2f", confidence * 100);
			default:
				return "";
			}
		}

		public Image getColumnImage(Object obj, int index) {

			if (index > 0)
				return null;
			return getImage(obj);
		}

		public Image getImage(Object obj) {

			Image img = null;
			try {
				Result result = (Result) obj;
				if (result.resultURL.contains("stackoverflow")) {
					img = ImageDescriptor.createFromFile(
							ViewLabelProvider.class, "stackoverflow.png")
							.createImage();
				} else
					img = ImageDescriptor.createFromFile(
							ViewLabelProvider.class, "answer.png")
							.createImage();

			} catch (Exception exc) {
				// exc.printStackTrace();
				System.err.println(exc.getMessage());
			}
			return img;
		}
	}

	class MyTableSorter extends ViewerSorter {
		// table sorter class
		private static final int ASCENDING = 0;
		private static final int DESCENDING = 1;
		private int column;
		private int direction;

		public void doSort(int column) {
			if (column == this.column) {
				direction = 1 - direction;
			} else {
				this.column = column;
				direction = ASCENDING;
			}
		}

		public int compare(double d1, double d2) {
			int resp = 0;
			if (d1 > d2)
				resp = 1;
			else if (d1 < d2)
				resp = -1;
			return resp;
		}

		public int compare(Viewer viewer, Object e1, Object e2) {
			int rc = 0;
			Result result1 = (Result) e1;
			Result result2 = (Result) e2;
			switch (column) {
			case 1:
				rc = compare(result1.totalScore_content_context_popularity,
						result2.totalScore_content_context_popularity);
				break;
			case 2:
				rc = compare(result1.content_score, result2.content_score);
				break;
			case 3:
				rc = compare(result1.context_score, result2.context_score);
				break;
			case 4:
				rc = compare(result1.popularity_score, result2.popularity_score);
				break;
			case 5:
				rc = compare(result1.search_result_confidence,
						result2.search_result_confidence);
				break;
			}

			if (direction == DESCENDING)
				rc = -rc;
			return rc;
		}
	}

	protected Image get_search_image() {
		return ImageDescriptor.createFromFile(ViewLabelProvider.class,
				"searchbt16.gif").createImage();
	}

	protected void addSearchEngines(Composite parent) {
		// adding search engines
		final Composite composite2 = new Composite(parent, SWT.NONE);
		gridLayout = new GridLayout(4, false);
		GridLayout gridLayout2 = new GridLayout(4, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 10;
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing = 5;
		composite2.setLayout(gridLayout2);

		GridData gridData2 = new GridData(SWT.CENTER, SWT.FILL, true, false);
		composite2.setLayoutData(gridData2);

		// adding radio button list
		excclipse = new Button(composite2, SWT.RADIO);
		excclipse.setText("ExcClipse");
		excclipse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				if (excclipse.getSelection()) {
					associateContext.setEnabled(true);
					associateContext.setSelection(true);
				} else {
					associateContext.setSelection(false);
					associateContext.setEnabled(false);
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		google = new Button(composite2, SWT.RADIO);
		google.setText("Google");
		bing = new Button(composite2, SWT.RADIO);
		bing.setText("Bing");
		yahoo = new Button(composite2, SWT.RADIO);
		yahoo.setText("Yahoo!");
	}

	protected ArrayList<String> formattingKeywordQuery(
			ArrayList<String> rawSuggestions) {
		// code for formatting keyword query
		ArrayList<String> temp = new ArrayList<>();
		String query = new String();
		for (String item : rawSuggestions) {
			query += item.trim() + " ";
			temp.add(query);
		}
		return temp;
	}

	protected void add_related_exception_message(Composite parent,
			HashMap<String, ArrayList<Integer>> pointers) {
		// code for showing related exception message
		final Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout(5, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 10;
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing = 5;
		composite.setLayout(gridLayout);

		GridData gridData = new GridData(SWT.CENTER, SWT.FILL, true, false);
		composite.setLayoutData(gridData);

		// gridData = new GridData(SWT.DEFAULT, SWT.FILL, false, false);
		GridData gdata2 = new GridData();
		gdata2.heightHint = 25;
		gdata2.widthHint = 600;
		gdata2.horizontalAlignment = SWT.BEGINNING;
		gdata2.verticalAlignment = SWT.CENTER;
		gdata2.grabExcessHorizontalSpace = false;

		Label keywordlabel = new Label(composite, SWT.NONE);
		// final Image
		// image=ImageDescriptor.createFromFile(SurfClipseClientView.class,
		// "sclogo4.png").createImage();
		// keywordlabel.setImage(image);
		keywordlabel.setText("Keywords:");
		keywordlabel.setFont(new Font(composite.getDisplay(), "Arial", 11,
				SWT.BOLD));

		input = new Text(composite, SWT.SINGLE | SWT.BORDER);
		input.setEditable(true);
		input.setToolTipText("Press Ctrl+Space to Check Suggested Queries.");
		Font myfont = new Font(composite.getDisplay(), "Arial", 11, SWT.NORMAL);
		input.setFont(myfont);
		input.setLayoutData(gdata2);

		flistener = new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				// TODO Auto-generated method stub
				// do nothing
				// System.out.println("Focus lost");
			}

			@Override
			public void focusGained(FocusEvent e) {
				// TODO Auto-generated method stub
				// System.out.println("Focus gained");
				// suggestions collected..
				if (suggestions.size() > 0) {
					String[] proposals = suggestions
							.toArray(new String[suggestions.size()]);
					if (proposals.length > 0) {
						try{
						// ContentProposalAdapter adapter = null;
						SimpleContentProposalProvider scp = new SimpleContentProposalProvider(
								proposals);

						// setting filtering
						scp.setFiltering(true);
						String autoactive = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
						KeyStroke ks=KeyStroke.getInstance("Ctrl+Space");
						adapter = new ContentProposalAdapter(input,
								new TextContentAdapter(), scp, ks,
								autoactive.toCharArray()); // keystroke
															// is
															// ignored
						adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
						}catch(ParseException exc){
							
						}
					}
				}
			}
		};
		if (!input.isListening(SWT.FOCUSED)) {
			input.addFocusListener(flistener);
		}

		// final Button confirm; //=new Button(composite,SWT.CHECK);
		// confirm.setText("Associate context");
		// confirm.setLayoutData(gdata2);
		// Label blank=new Label(composite,SWT.NONE);

		GridData gdata3 = new GridData();
		gdata3.heightHint = 30;
		gdata3.widthHint = 90;
		gdata3.horizontalAlignment = SWT.BEGINNING;
		// gdata2.grabExcessHorizontalSpace=true;

		Button searchButton = new Button(composite, SWT.PUSH);
		searchButton.setText("Search");
		searchButton.setToolTipText("Search with ExcClipse");
		searchButton.setFont(new Font(composite.getDisplay(), "Arial", 10,
				SWT.BOLD));
		searchButton.setImage(get_search_image());
		// System.out.println("Search Icon:"+Display.getDefault().getSystemImage(SWT.ICON_SEARCH));
		searchButton.setLayoutData(gdata3);

		GridData gdata4 = new GridData();
		gdata4.heightHint = 30;
		gdata4.widthHint = 120;
		gdata4.horizontalAlignment = SWT.BEGINNING;
		// gdata2.grabExcessHorizontalSpace=true;

		Button refreshButton = new Button(composite, SWT.PUSH);
		refreshButton.setText("Get Queries");
		refreshButton.setToolTipText("Get Query Suggestions");
		refreshButton.setFont(new Font(parent.getDisplay(), "Arial", 10,
				SWT.BOLD));
		refreshButton.setImage(getRefreshImage());
		refreshButton.setLayoutData(gdata4);
		refreshButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				// clear input
				input.setText("");
				QueryRecommender recommender = new QueryRecommender();
				suggestions = recommender.recommendQueries();
				// also show the stack graph: it is shown elsewhere in
				// StackGraphManager
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		/*
		 * timerLabel=new Label(composite, SWT.NONE); timerLabel.setFont(new
		 * Font(composite.getDisplay(), "Arial",11, SWT.BOLD)); final Color
		 * myColor = new Color(composite.getDisplay(), 00, 102, 255);
		 * timerLabel.setForeground(myColor); timerLabel.addDisposeListener(new
		 * DisposeListener() { public void widgetDisposed(DisposeEvent e) {
		 * myColor.dispose(); } }); timerLabel.setText("Time:");
		 */

		// final Label progressLabel=new Label(composite.getShell(),
		// SWT.BORDER);
		// Image
		// image=ImageDescriptor.createFromFile(SurfClipseClientView.class,
		// "progress.gif").createImage();
		// progressLabel.setImage(image);

		final Composite composite2 = new Composite(parent, SWT.NONE);
		GridLayout gridLayout2 = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 5;
		gridLayout.horizontalSpacing = 5;
		composite2.setLayout(gridLayout2);

		GridData gridData2 = new GridData(SWT.CENTER, SWT.FILL, true, false);
		composite2.setLayoutData(gridData2);

		// excclipseSuggest=new Button(composite2, SWT.CHECK);
		// excclipseSuggest.setText("ExcClipse Suggest");
		// googleSuggest=new Button(composite2, SWT.RADIO);
		// googleSuggest.setText("Google Suggest");

		Label blank = new Label(composite, SWT.NONE);
		Label info = new Label(composite2, SWT.NONE);
		info.setText("Press Ctrl+Space to Check Suggested Queries.");

		associateContext = new Button(composite2, SWT.CHECK);
		// final Button confirm = new Button(composite2, SWT.CHECK);
		associateContext.setText("Associate context");
		final Button clearButton = new Button(composite2, SWT.CHECK);
		clearButton.setText("Reset search");

		// adding listener to clear button
		clearButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				suggestions.clear();
				input.setText("");
				viewer.setContentProvider(new ViewContentProvider());
				//clearing the browse panel
				browser.setText("");
				
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// searchButton.setLayoutData(gridData);
		searchButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				String searchQuery = input.getText();
				if (!searchQuery.isEmpty()) {
					boolean associate_context = false;
					// making the search
					if(excclipse.getSelection()){
						SearchEventManager manager = new SearchEventManager();
						if (associateContext.getSelection())
							associate_context = true;
						manager.fire_keyword_search(searchQuery, associate_context);
						// showing progress bar
					}else{
						int engineIndex=-1;
						if(google.getSelection())engineIndex=0;
						if(bing.getSelection())engineIndex=1;
						if(yahoo.getSelection())engineIndex=2;
						SearchEngineManager manager=new SearchEngineManager(searchQuery, engineIndex);
						manager.fireWebSearch();
					}
					// clearing the suggestions
					suggestions.clear();

					// removes the listener
					// adapter=null;
					// input.removeFocusListener(flistener);
				} else {
					showMessageBox("Please enter your query for search");
				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		// adding key listener to input
		input.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				switch (event.keyCode) {
				case SWT.CR:
					try {
						// System.out.println("Search initiated..");
						// initiating the search
						// TODO Auto-generated method stub
						String searchQuery = input.getText();
						if (!searchQuery.isEmpty()) {
							// boolean associate_context = false;
							// making the search
							/*
							 * SearchEventManager manager = new
							 * SearchEventManager(); if (confirm.getSelection())
							 * associate_context = true;
							 * manager.fire_keyword_search(searchQuery,
							 * associate_context);
							 */// showing progress bar
								// clearing the suggestions
								// suggestions.clear();
						} else {
							// showMessageBox("Please enter your query for search");
						}
					} catch (Exception exc) {
					}
					break;
				case SWT.DEL:
					// clearing the search input box
					input.setText("");
					break;
				case SWT.ESC:
					// canceling the search input
					// input.setText("");
					break;
				}
			}
		});

	}

	protected void add_result_table(Composite parent) {
		// code for adding the table viewer
		
		final Composite composite3 = new Composite(parent, SWT.NONE);
		GridLayout gridLayout2 = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.verticalSpacing = 3;
		gridLayout.horizontalSpacing = 0;
		composite3.setLayout(gridLayout2);
		 
		GridData gridData2 = new GridData(SWT.FILL, SWT.FILL, true, true);
		composite3.setLayoutData(gridData2);
		
		
		final SashForm divider=new SashForm(composite3, SWT.HORIZONTAL | SWT.BORDER);
		divider.setLayout(gridLayout2);
		divider.setLayoutData(gridData2);
		divider.addListener(SWT.Resize, new Listener(){

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				int totalWidth=divider.getClientArea().width;
				int[] weights=new int[2];
				int leftWidth=(int)(.55*totalWidth);
				weights[0]=leftWidth;
				weights[1]=totalWidth-leftWidth;
				divider.setWeights(weights);
			}
		});
		
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		viewer = new TableViewer(divider, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		// viewer.setSorter(new MyTableSorter());
		final Table table = viewer.getTable();
		table.setLayoutData(gridData);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				// TODO Auto-generated method stub
				try{
				IStructuredSelection selection=(IStructuredSelection) event.getSelection();
				if(selection==null)return;
				Result result=(Result) selection.toList().get(0);
				if(!cache.containsKey(result.resultURL)){
					result.pageContent=ContentLoader.downloadPageContent(result.resultURL);
				}else{
					Result stored=cache.get(result.resultURL);
					result.pageContent=stored.pageContent;
				}
				BrowserManager manager = new BrowserManager(
						result, browser); //show in inner browser
				manager.showSuggestion();
				}catch(Exception e){
					String query="Failed to show the relevant sections. Please make sure there exist an exception details.";
					//showMessageBox(query);
				}
			}
		});
		
		//adding second item
		//Composite composite4=new Composite(divider, SWT.NONE);
		//GridLayout glayout4=new GridLayout();
		//composite4.setLayout(glayout4);
		//GridData gdata3=new GridData(SWT.FILL, SWT.FILL, true, true);
		//composite4.setLayoutData(gdata3);
		browser=new Browser(divider, SWT.NONE);
		//browser.setLayout(glayout4);
		//browser.setLayoutData(gdata3);
		browser.setUrl("http://www.google.ca");
		
		
		/*
		 * //Tool tip for the table final ToolTip tip=new
		 * ToolTip(table.getShell(), SWT.BALLOON);
		 * //tip.setText("Result table");
		 * //tip.setMessage("This is the result table"); tip.setAutoHide(true);
		 * table.addListener(SWT.MouseHover, new Listener() { public void
		 * handleEvent(Event event) { try { TableItem item=table.getItem(new
		 * Point(event.x, event.y)); tip.setText(item.getText(0)); String
		 * description=""; DecimalFormat df=new DecimalFormat("##.00"); String
		 * content=df.format(Double.parseDouble(item.getText(3)) *100); String
		 * context=df.format(Double.parseDouble(item.getText(4))*100); String
		 * popularity=df.format(Double.parseDouble(item.getText(5))*100); String
		 * confidence=df.format(Double.parseDouble(item.getText(6))*100);
		 * description+="Content relevance: "+content+"%";
		 * description+="\nContext relevance: "+context+"%";
		 * description+="\nRelative popularity: "+popularity+"%";
		 * description+="\nResult confidence: "+confidence+"%";
		 * tip.setMessage(description+"\n\n"+item.getText(1));
		 * 
		 * tip.getDisplay().timerExec(50, new Runnable() { public void run() {
		 * tip.setVisible(true); } }); }catch(Exception exc){} } });
		 * table.addListener(SWT.MouseExit, new Listener() { public void
		 * handleEvent(Event event) { tip.getDisplay().timerExec(50, new
		 * Runnable() { public void run() { tip.setVisible(false); } }); } });
		 */

		String[] columnNames = { "Search Result", "Relevance", " ", " " };
		int[] colWidth = { 600, 100, 50, 50 };
		int[] colAlignment = { SWT.LEFT, SWT.LEFT, SWT.LEFT, SWT.LEFT };
		for (int i = 0; i < columnNames.length; i++) {
			// stored for sorting
			final int columnNum = i;

			TableColumn col = new TableColumn(table, colAlignment[i]);
			col.setText(columnNames[i]);
			col.setWidth(colWidth[i]);
			// col.setMoveable(true);
			col.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					((MyTableSorter) viewer.getSorter()).doSort(columnNum);
					viewer.refresh();
				}
			});
			// col.setImage(getDefaultImage());
		}

		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new MyTableSorter());
		// viewer.setSorter(new TableColumnSorter());
		viewer.setInput(getViewSite());
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				// TODO Auto-generated method stub

				System.out.println("Double clicked");

				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				if (selection.isEmpty())
					return;
				@SuppressWarnings("unchecked")
				List<Object> list = selection.toList();
				Object obj1 = list.get(0);
				// System.out.println("Clicked on:"+((Result)obj1).title);
				Result selected = (Result) obj1;
				System.out.println("Selected URL:" + selected.resultURL);
				try {
					// SurfClipseBrowser surfBrowser=new SurfClipseBrowser();
					// surfBrowser.show_the_result_link(selected.title,
					// selected.resultURL);
					String viewID = "ca.usask.cs.srlab.excclipse.views.SurfClipseBrowser";
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage().showView(viewID);

					IWorkbenchPage page = (IWorkbenchPage) PlatformUI
							.getWorkbench().getActiveWorkbenchWindow()
							.getActivePage();
					IViewPart vpart = page.findView(viewID);
					SurfClipseBrowser my_browser_view = (SurfClipseBrowser) vpart;
					Browser mybrowser = my_browser_view.webbrowser;
					mybrowser.setUrl(selected.resultURL);
					// showing page title
					// Label pageLabel=my_browser_view.pageLabel;
					// pageLabel.setText(selected.title);
					// adding currently displayed URL
					add_currently_displayed_url(selected.resultURL);

				} catch (Exception exc) {
					// System.err.println(exc.getMessage());
					exc.printStackTrace();
					showMessageBox("Failed to show the page.");
				}
			}
		});

		// resizing table item height
		setItemHeight(table, MIN);
		setPaintItem(table);
		addContentExtractCommands(table);
		addToolTips(table);

	}

	public void setItemHeight(Table table, final int min) {
		table.addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				TableItem item = (TableItem) event.item;
				String text = item.getText(event.index);
				Point size = event.gc.textExtent(text);
				event.width = size.x + 2 * TEXT_MARGIN;
				event.height = min;// Math.max(min, size.y + TEXT_MARGIN);
			}
		});
		table.addListener(SWT.EraseItem, new Listener() {

			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				event.detail &= ~SWT.FOREGROUND;
			}
		});
	}

	protected void setPaintItem(Table table) {
		table.addListener(SWT.PaintItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				if (event.index == 0) {
					TableItem item = (TableItem) event.item;
					// item.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_BLUE));
					String text = item.getText(event.index);
					/* center column 1 vertically */
					int yOffset = 0;
					if (event.index == 1) {
						Point size = event.gc.textExtent(text);
						yOffset = Math.max(0, (event.height - size.y) / 2);
					}
					// event.gc.drawText(text, event.x + TEXT_MARGIN, event.y
					// + yOffset, true);

					// redraw text layout
					String resultText = item.getText(0);
					int firstNL = resultText.indexOf('\n');
					int lastNL = resultText.lastIndexOf('\n');
					textLayout.setText(resultText);
					textLayout.setStyle(style1, 0, firstNL - 1);
					textLayout.setStyle(style2, firstNL + 1, lastNL - 1);
					textLayout.setStyle(style3, lastNL, resultText.length());
					textLayout.draw(event.gc, event.x, event.y);
				}

				else if (event.index == 1) {
					GC gc = event.gc;
					int index = event.index;
					TableItem item = (TableItem) event.item;
					int percent = (int) Double.parseDouble(item.getText(index));
					Color foreground = gc.getForeground();
					Color background = gc.getBackground();
					// gc.setForeground(new Color(null, 11, 59, 23));
					Color myforeground = new Color(null, 11, 97, 11);
					/*
					 * if(index==2){ myforeground=new Color(null, 0,64,255); }
					 * if(index==3){ myforeground=new Color(null, 223,64,255); }
					 * if(index==4){ myforeground=new Color(null, 17,122,141); }
					 * if(index==5){ myforeground=new Color(null, 180,180,54); }
					 */
					gc.setForeground(myforeground);
					gc.setBackground(new Color(null, 255, 255, 255));
					int col2Width = 100;
					int width = (col2Width - 1) * percent / 100;
					int height = 25;
					// gc.fillRectangle(event.x, event.y + 10, width,
					// height);
					gc.fillGradientRectangle(event.x, event.y + 15, width,
							height, false);
					Rectangle rect2 = new Rectangle(event.x, event.y + 15,
							width - 1, height - 1);
					gc.drawRectangle(rect2);
					gc.setForeground(new Color(null, 255, 255, 255));
					String text = percent + "%";
					Point size = event.gc.textExtent(text);
					int offset = Math.max(0, (height - size.y) / 2);
					gc.drawText(text, event.x + 2, event.y + 15 + offset, true);
					gc.setForeground(background);
					gc.setBackground(foreground);
				}
				if (event.index == 2) {
					// TableItem item=(TableItem)event.item;
					Image tmpImage = getSuggestionImage();
					int tmpWidth = 0;
					int tmpHeight = 0;
					int tmpX = 0;
					int tmpY = 0;

					tmpWidth = 30;// testTable.getColumn(event.index).getWidth();
					tmpHeight = ((TableItem) event.item).getBounds().height;

					tmpX = tmpImage.getBounds().width;
					tmpX = (tmpWidth / 2 - tmpX / 2);
					tmpY = tmpImage.getBounds().height;
					tmpY = (tmpHeight / 2 - tmpY / 2);
					if (tmpX <= 0)
						tmpX = event.x;
					else
						tmpX += event.x;
					if (tmpY <= 0)
						tmpY = event.y;
					else
						tmpY += event.y;
					event.gc.drawImage(tmpImage, tmpX, tmpY);

				}
				
				/*if (event.index == 3) {
					// TableItem item=(TableItem)event.item;
					Image tmpImage = getContentImage();
					int tmpWidth = 0;
					int tmpHeight = 0;
					int tmpX = 0;
					int tmpY = 0;

					tmpWidth = 30;// testTable.getColumn(event.index).getWidth();
					tmpHeight = ((TableItem) event.item).getBounds().height;

					tmpX = tmpImage.getBounds().width;
					tmpX = (tmpWidth / 2 - tmpX / 2);
					tmpY = tmpImage.getBounds().height;
					tmpY = (tmpHeight / 2 - tmpY / 2);
					if (tmpX <= 0)
						tmpX = event.x;
					else
						tmpX += event.x;
					if (tmpY <= 0)
						tmpY = event.y;
					else
						tmpY += event.y;
					event.gc.drawImage(tmpImage, tmpX, tmpY);
				}*/
			}
		});

	}

	protected void addContentExtractCommands(final Table table) {
		table.addListener(SWT.MouseDown, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// TODO Auto-generated method stub
				Point p = new Point(event.x, event.y);
				TableItem item = table.getItem(p);
				// System.out.println("selected index:"+table.getSelectionIndex());
				int currIndex = table.getSelectionIndex();
				if (item != null) {
					for (int col = 0; col < table.getColumnCount(); col++) {
						Rectangle rect = item.getBounds(col);
						if (rect.contains(p)) {
							if (col == 2) {
								try{
								if (lastSelectedIndex == currIndex) {
									// do nothing
								} else {
									currentResult = (Result) item.getData();
									currentResult.pageContent = ContentLoader
											.downloadPageContent(currentResult.resultURL);
								}
								BrowserManager manager = new BrowserManager(
										currentResult, browser); //show in inner browser
								manager.showSuggestion();
									//handle the exception
								// table.setSelection(currIndex);
								// table.setRedraw(false);
								// lastSelectedIndex=event.index;
								// item.setBackground(new
								// Color(null,169,226,243));
								}catch(Exception e){
									showMessageBox("Failed to show the relevant sections.");
								}
							} else if (col == 3) {
								if (lastSelectedIndex == currIndex) {
									// do nothing
								} else {
									currentResult = (Result) item.getData();
									currentResult.pageContent = ContentLoader
											.downloadPageContent(currentResult.resultURL);
								}
								String viewID = "ca.usask.cs.srlab.excclipse.views.SurfClipseBrowser";
								try {
									PlatformUI.getWorkbench()
											.getActiveWorkbenchWindow()
											.getActivePage().showView(viewID);
									SurfClipseBrowser browserView = (SurfClipseBrowser) PlatformUI
											.getWorkbench()
											.getActiveWorkbenchWindow()
											.getActivePage().findView(viewID);
									BrowserManager manager = new BrowserManager(
											currentResult, browserView);
									manager.showMainContent();
									// table.setSelection(currIndex);
									// table.setRedraw(false);
									// item.setBackground(new
									// Color(null,169,226,243));
								} catch (Exception e2) {
									// handle the exception
									showMessageBox("Failed to show the noise-free version of the page.");
								}

							}
						}
					}
				}
			}
		});

	}

	protected void addToolTips(final Table table)
	{
		//adding table row tool tips
		final ToolTip tip = new ToolTip(table.getShell(), SWT.ICON_INFORMATION);
		tip.setAutoHide(true);
		table.addListener(SWT.MouseHover, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
				Point p = new Point(event.x, event.y);
				TableItem item = table.getItem(p);
				for (int col = 2; col < table.getColumnCount(); col++) {
					Rectangle rect = item.getBounds(col);
					if (rect.contains(p)) {
						//System.out.println("From tool tip");
						
							if (col == 2) {
								tip.setText("Show relevant section only");
							} else if (col == 3) {
								tip.setText("Show noise-free webpage");
							}
							tip.getDisplay().timerExec(30, new Runnable() {
								public void run() {
									tip.setVisible(true);
								}
							});
						
					}
				}
				} catch (Exception exc) {
				}
			}
		});
		table.addListener(SWT.MouseExit, new Listener() {
			public void handleEvent(Event event) {
				tip.getDisplay().timerExec(50, new Runnable() {
					public void run() {
						tip.setVisible(false);
					}
				});
			}
		});
	}
	
	public void createPartControl(final Composite parent) {

		GridLayout glayout = new GridLayout();
		glayout.marginWidth = 15;
		glayout.marginHeight = 10;
		parent.setLayout(glayout);

		GridData gdata = new GridData(SWT.FILL, SWT.FILL, true, true);
		parent.setLayoutData(gdata);

		addSearchEngines(parent);
		add_related_exception_message(parent, null);
		add_result_table(parent);
		
		// adding to the Help system
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(viewer.getControl(),
						"ca.usask.ca.srlab.excclipse.viewer");
	}

	protected void add_currently_displayed_url(String currentURL) {
		// code for adding the current URL
		try {
			RecencyScoreManager recenyScoreManager = Activator.recenyScoreManager;
			ArrayList<HistoryLink> RecentFiles = RecencyScoreManager.RecentFiles;
			// removing the existing entries in top 20
			// update with new visiting time
			ArrayList<HistoryLink> tempList = new ArrayList<>();
			tempList.addAll(RecencyScoreManager.RecentFiles);

			for (HistoryLink hlink : tempList) {
				if (hlink.linkURL.equals(currentURL)) {
					RecentFiles.remove(hlink);
				}
			}

			long last_visit = System.currentTimeMillis() / 1000;

			// adding new history links
			HistoryLink mylink = new HistoryLink();
			mylink.linkURL = currentURL;
			mylink.last_visit_time = last_visit;

			RecentFiles.add(0, mylink);
			RecencyScoreManager.RecentFiles = RecentFiles;

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}

	protected Image getSuggestionImage() {
		// return suggestion image
		return ImageDescriptor.createFromFile(SurfClipseClientView.class,
				"sugg16.png").createImage();
	}

	protected Image getContentImage() {
		// return content image
		return ImageDescriptor.createFromFile(SurfClipseClientView.class,
				"doc316.png").createImage();
	}

	protected Image getRefreshImage() {
		return ImageDescriptor.createFromFile(SurfClipseClientView.class,
				"refresh.png").createImage();
	}

	protected void showMessageBox(String message) {
		// code for showing message box
		try {
			Shell shell = Display.getDefault().getShells()[0];
			MessageDialog.openInformation(shell, "Information", message);
		} catch (Exception exc) {
		}
	}

	/**
	 * The constructor.
	 */

	public SurfClipseClientView() {
	}

	public void setFocus() {
		if (SurfClipseModeManager.current_mode == 1) {
			new Thread(new ActiveConsoleChecker()).start();
		}
		viewer.getControl().setFocus();
		// setup_console_listener();
		// System.out.println("I am focused..");

	}
}