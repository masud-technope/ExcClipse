package querysuggest;

import java.util.ArrayList;
import java.util.HashSet;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

public class MethodCallVisitor extends VoidVisitorAdapter {

	public HashSet<String> MethodCalls;
	public ArrayList<String> MethodCallAll;
	public ArrayList<String> MethodScopeAll;

	public MethodCallVisitor() {
		MethodCalls = new HashSet<>();
		MethodCallAll = new ArrayList<>();
		MethodScopeAll = new ArrayList<>();
	}

	@Override
	public void visit(MethodCallExpr expr, Object arg) {
		String methodName = expr.getName();
		try {
			MethodCalls.add(methodName);
			MethodCallAll.add(methodName);
		} catch (Exception exc) {
		}
		try {
			String methodScope = expr.getScope().toString();
			if (methodScope.contains(".")) {
				String parts[] = methodScope.split("\\.");
				for (String part : parts)
					MethodScopeAll.add(part);

			} else
				MethodScopeAll.add(methodScope);
		} catch (Exception exc) {
		}
		// super.visit(expr, arg);

	}

}
