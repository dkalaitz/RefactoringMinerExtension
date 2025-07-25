package gr.uom.java.xmi.decomposition;

import gr.uom.java.xmi.LocationInfo.CodeElementType;
import gr.uom.java.xmi.VariableDeclarationContainer;
import gr.uom.java.xmi.decomposition.replacement.Replacement;
import gr.uom.java.xmi.decomposition.replacement.Replacement.ReplacementType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConditionalExpression;

import java.util.Map;

public class TernaryOperatorExpression extends LeafExpression {

	private AbstractExpression condition;
	private AbstractExpression thenExpression;
	private AbstractCodeFragment elseExpression;

	public TernaryOperatorExpression(CompilationUnit cu, String sourceFolder, String filePath, ConditionalExpression expression, VariableDeclarationContainer container, String javaFileContent) {
		super(cu, sourceFolder, filePath, expression, CodeElementType.TERNARY_OPERATOR, container);
		this.condition = new AbstractExpression(cu, sourceFolder, filePath, expression.getExpression(), CodeElementType.TERNARY_OPERATOR_CONDITION, container, javaFileContent);
		this.thenExpression = new AbstractExpression(cu, sourceFolder, filePath, expression.getThenExpression(), CodeElementType.TERNARY_OPERATOR_THEN_EXPRESSION, container, javaFileContent);
		if(expression.getElseExpression() instanceof ConditionalExpression) {
			this.elseExpression = new TernaryOperatorExpression(cu, sourceFolder, filePath, (ConditionalExpression)expression.getElseExpression(), container, javaFileContent);
		}
		else {
			this.elseExpression = new AbstractExpression(cu, sourceFolder, filePath, expression.getElseExpression(), CodeElementType.TERNARY_OPERATOR_ELSE_EXPRESSION, container, javaFileContent);
		}
	}

	public LeafExpression asLeafExpression() {
		return new LeafExpression(getString(), getLocationInfo());
	}

	public AbstractExpression getCondition() {
		return condition;
	}

	public AbstractExpression getThenExpression() {
		return thenExpression;
	}

	public AbstractCodeFragment getElseExpression() {
		return elseExpression;
	}

	public String getExpression() {
		return getString();
	}

	public Replacement makeReplacementWithTernaryOnTheRight(String statement, Map<String, String> parameterToArgumentMap) {
		if(getElseExpression().getString().equals(statement)) {
			return new Replacement(statement, getElseExpression().getString(), ReplacementType.EXPRESSION_REPLACED_WITH_TERNARY_ELSE);
		}
		if(getThenExpression().getString().equals(statement)) {
			return new Replacement(statement, getThenExpression().getString(), ReplacementType.EXPRESSION_REPLACED_WITH_TERNARY_THEN);
		}
		String temp = new String(statement);
		for(String key : parameterToArgumentMap.keySet()) {
			if(!key.equals(parameterToArgumentMap.get(key))) {
				temp = ReplacementUtil.performReplacement(temp, parameterToArgumentMap.get(key), key);
			}
		}
		if(!temp.equals(statement)) {
			if(getElseExpression().getString().equals(temp)) {
				return new Replacement(statement, getElseExpression().getString(), ReplacementType.EXPRESSION_REPLACED_WITH_TERNARY_ELSE);
			}
			if(getThenExpression().getString().equals(temp)) {
				return new Replacement(statement, getThenExpression().getString(), ReplacementType.EXPRESSION_REPLACED_WITH_TERNARY_THEN);
			}
		}
		return null;
	}

	public Replacement makeReplacementWithTernaryOnTheLeft(String statement, Map<String, String> parameterToArgumentMap) {
		if(getElseExpression().getString().equals(statement)) {
			return new Replacement(getElseExpression().getString(), statement, ReplacementType.EXPRESSION_REPLACED_WITH_TERNARY_ELSE);
		}
		if(getThenExpression().getString().equals(statement)) {
			return new Replacement(getThenExpression().getString(), statement, ReplacementType.EXPRESSION_REPLACED_WITH_TERNARY_THEN);
		}
		String temp = new String(statement);
		for(String key : parameterToArgumentMap.keySet()) {
			if(!key.equals(parameterToArgumentMap.get(key))) {
				temp = ReplacementUtil.performReplacement(temp, parameterToArgumentMap.get(key), key);
			}
		}
		if(!temp.equals(statement)) {
			if(getElseExpression().getString().equals(temp)) {
				return new Replacement(getElseExpression().getString(), statement, ReplacementType.EXPRESSION_REPLACED_WITH_TERNARY_ELSE);
			}
			if(getThenExpression().getString().equals(temp)) {
				return new Replacement(getThenExpression().getString(), statement, ReplacementType.EXPRESSION_REPLACED_WITH_TERNARY_THEN);
			}
		}
		return null;
	}
}
