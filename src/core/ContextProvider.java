package core;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;

public class ContextProvider {

	public String strace = new String();
	public String ccode = new String();
	boolean fileOpened=false;

	public String getCurrentStackTrace() {
		strace = extract_stacktrace_from_console();
		return strace;
	}

	public String getCurrentCodeContext() {
		ccode = extract_source_code_context(strace);
		return ccode;
	}

	public String extract_stacktrace_from_console() {
		// code for extracting stack from console
		String extractedContent = new String();
		String stackPattern = "^.+Exception(:)?[^\\n]+\\n(\\t+\\Qat \\E.+\\s+)+$";
		Pattern p = Pattern.compile(stackPattern);
		// console content
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole consoles[] = conMan.getConsoles();
		System.out.println("No of consoles:" + consoles.length);
		// get the default console opened
		TextConsole myConsole = (TextConsole) consoles[0];
		// storing the stack trace information.
		String consoleContent = myConsole.getDocument().get();
		// matching
		Matcher matcher = p.matcher(consoleContent);
		while (matcher.find()) {
			extractedContent = consoleContent.substring(matcher.start(),
					matcher.end());
			break;
		}
		// returning
		return extractedContent;
	}

	public String extract_source_code_context(String extractedStack) {
		// code for extracting source code context
		String sourceContext = new String();
		String javaFileName=new String();
		int lineNumber=0;
		try{
		String[] stackElements = extractedStack.split("\n");
		// current source code line
		String firstTraceLine = new String();
		for (String elem : stackElements) {
			if (elem.contains(".java:")) {
				firstTraceLine = elem;
				System.out.println("Trace of interest:" + elem);
				break;
			}
		}
		int open_brac = firstTraceLine.lastIndexOf('(');
		int close_brac = firstTraceLine.lastIndexOf(')');
		String fileNameLine = firstTraceLine.substring(open_brac + 1,
				close_brac);
		String parts[] = fileNameLine.split(":");
		javaFileName = parts[0].trim();
		lineNumber = Integer.parseInt(parts[1].trim());
		System.out.println(javaFileName + " " + lineNumber);
		}catch(Exception  exc2){
			//handle it
		}
		// now extract the context of the source line
		try {
			sourceContext = this.extract_the_context(javaFileName, lineNumber);
		} catch (Exception exc) {
		}
		return sourceContext;
	}

	protected boolean open_target_project_file(final String fileName,
			final int lineNumber) {
		// code for opening target project file
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		try {
			root.accept(new IResourceVisitor() {
				@SuppressWarnings("unchecked")
				@Override
				public boolean visit(IResource resource) throws CoreException {
					// TODO Auto-generated method stub
					if (resource.getType() == IResource.FILE) {
						if (resource.getName().endsWith(fileName)) {
							// System.out.println(resource.getLocation().toOSString());
							IFile fileToBeOpened = (IFile) resource;
							IWorkbenchPage page = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow().getActivePage();
							HashMap map = new HashMap();
							map.put(IMarker.LINE_NUMBER,
									new Integer(lineNumber));
							// map.put(IWorkbenchPage.EDITOR_ID_ATTR,
							// "org.eclipse.ui.DefaultTextEditor");
							IMarker marker = fileToBeOpened
									.createMarker(IMarker.TEXT);
							marker.setAttributes(map);
							IDE.openEditor(page, fileToBeOpened);
							fileOpened=true;
							return false;
						}
					}
					return true;
				}
			});
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		//return if the file is opened
		return fileOpened;
	}

	private String extract_the_context(String fileName, int lineNumber) {

		// opening the target file
		boolean succeeded=open_target_project_file(fileName, lineNumber);
		// collect contextual codes
		String context = new String();

		int block_depth = 3;
		// code for returning the context
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			ITextEditor editor = (ITextEditor) page.getActiveEditor();
			IDocument doc = editor.getDocumentProvider().getDocument(
					editor.getEditorInput());
			String fileContent = doc.get();
			
			//return active file content if cannot open the file.
			if(succeeded)return fileContent;
			
			String[] lines = fileContent.split("\n");
			int start_line = lineNumber > block_depth ? lineNumber
					- block_depth : 0;
			int end_line = lineNumber < (lines.length - block_depth) ? (lines.length - block_depth)
					: lines.length;
			for (int i = start_line; i < end_line; i++) {
				context += lines[i] + "\n";
			}
		} catch (Exception exc) {
			// System.err.println(exc.getMessage());
		}
		return context;
	}

}
