package gr.uom.java.xmi.decomposition;

import static gr.uom.java.xmi.Constants.JAVA;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import antlr.ast.node.LangASTNode;
import antlr.ast.node.declaration.LangSingleVariableDeclaration;
import antlr.ast.node.expression.LangAssignment;
import antlr.ast.node.expression.LangFieldAccess;
import antlr.ast.node.expression.LangSimpleName;
import antlr.ast.node.metadata.LangAnnotation;
import antlr.ast.node.unit.LangCompilationUnit;
import antlr.ast.visitor.LangVisitor;
import gr.uom.java.xmi.*;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IExtendedModifier;
import org.eclipse.jdt.core.dom.LambdaExpression;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import gr.uom.java.xmi.LocationInfo.CodeElementType;
import gr.uom.java.xmi.diff.CodeRange;

public class VariableDeclaration implements LocationInfoProvider, VariableDeclarationProvider {
	private String variableName;
	private AbstractExpression initializer;
	private UMLType type;
	private boolean varargsParameter;
	private LocationInfo locationInfo;
	private boolean isParameter;
	private boolean isAttribute;
	private boolean isEnumConstant;
	private VariableScope scope;
	private boolean isFinal;
	private List<UMLAnnotation> annotations;
	private List<UMLModifier> modifiers;
	private String actualSignature;

	public VariableDeclaration(LangCompilationUnit cu, String sourceFolder, String filePath,
							   LangSingleVariableDeclaration param) {
		this.variableName = param.getLangSimpleName().getIdentifier();

		// Extract type from parameter
		if (param.hasTypeAnnotation() && param.getTypeAnnotation() != null) {
			this.type = UMLType.extractTypeObject(param.getTypeAnnotation().getName());
		} else {
			this.type = UMLType.extractTypeObject("Object"); // Default for untyped Python parameters
		}

		this.varargsParameter = param.isVarArgs();
		this.locationInfo = new LocationInfo(cu, sourceFolder, filePath, param,
				LocationInfo.CodeElementType.SINGLE_VARIABLE_DECLARATION);

		// Extract annotations and modifiers using existing processors
		List<LangAnnotation> langAnnotations = param.getAnnotations();

		this.annotations = new ArrayList<>();
		for (LangAnnotation langAnnotation : langAnnotations) {
			UMLAnnotation umlAnnotation = new UMLAnnotation(
					langAnnotation.getRootCompilationUnit(),
					sourceFolder,
					filePath,
					langAnnotation);
			annotations.add(umlAnnotation);
		}

		modifiers = new ArrayList<>();
		// Handle varargs parameters (*args, **kwargs)
		if (param.isVarArgs()) {
			UMLModifier varargsModifier = new UMLModifier(
					param.getRootCompilationUnit(),
					sourceFolder,
					filePath,
					"varargs",
					param
			);
			modifiers.add(varargsModifier);
		}

		// Handle parameters with type annotations
		if (param.hasTypeAnnotation()) {
			UMLModifier typedModifier = new UMLModifier(
					param.getRootCompilationUnit(),
					sourceFolder,
					filePath,
					"typed",
					param
			);
			modifiers.add(typedModifier);
		}

		// No initializer for parameters
		this.initializer = null;

		// Set characteristics directly from parameter
		this.isAttribute = param.isAttribute();
		this.isParameter = param.isParameter();
		this.isEnumConstant = param.isEnumConstant();
		this.isFinal = param.isFinal();

		this.scope = new VariableScope(cu, filePath);
		StringBuilder signature = new StringBuilder();
		if (varargsParameter) {
			if (variableName.startsWith("**")) {
				signature.append(variableName); // **kwargs
			} else if (variableName.startsWith("*")) {
				signature.append(variableName); // *args
			} else {
				signature.append("*").append(variableName); // *param
			}
		} else {
			signature.append(variableName);
		}
		if (type != null && !type.toString().equals("Object")) {
			signature.append(": ").append(type.toString());
		}
		this.actualSignature = signature.toString();
	}

	public VariableDeclaration(LangCompilationUnit cu, String sourceFolder, String filePath,
							   LangAssignment assignment, VariableDeclarationContainer container,
							   String variableName) {
		this.variableName = variableName;
		this.type = UMLType.extractTypeObject("Object"); // Default type for Python attributes
		this.varargsParameter = false;

		// Determine element type based on assignment context
		LocationInfo.CodeElementType elementType;
		LangASTNode leftSide = assignment.getLeftSide();
		if (leftSide instanceof LangFieldAccess) {
			elementType = LocationInfo.CodeElementType.FIELD_DECLARATION; // self.attr = value
		} else {
			elementType = LocationInfo.CodeElementType.VARIABLE_DECLARATION_STATEMENT; // var = value
		}
		this.locationInfo = new LocationInfo(cu, sourceFolder, filePath, assignment, elementType);

		// No annotations or modifiers for simple assignments
		this.annotations = new ArrayList<>();
		this.modifiers = new ArrayList<>();

		// Extract the right-hand side of the assignment as the initializer
		this.initializer = new AbstractExpression(
				assignment.getRootCompilationUnit(),
				sourceFolder,
				filePath,
				assignment.getRightSide(),
				LocationInfo.CodeElementType.EXPRESSION,
				container
		);

		this.isAttribute = false;
		this.isParameter = false;
		this.isEnumConstant = false;
		this.isFinal = false;

		if (leftSide instanceof LangFieldAccess fieldAccess) {
			// Check if it's self.attribute
			if (fieldAccess.getExpression() instanceof LangSimpleName simpleName) {
				this.isAttribute = "self".equals(simpleName.getIdentifier());
			}
		}

		if (leftSide instanceof LangSingleVariableDeclaration singleVariableDeclaration) {
			this.isParameter = singleVariableDeclaration.isParameter();
			this.isEnumConstant = singleVariableDeclaration.isEnumConstant();
			this.isFinal = singleVariableDeclaration.isFinal();
		}

		this.scope = new VariableScope(cu, filePath);
		StringBuilder signature = new StringBuilder();
        signature.append(variableName);
        if (!type.toString().equals("Object")) {
			signature.append(": ").append(type.toString());
		}
		this.actualSignature = signature.toString();
	}

	public VariableDeclaration(LangCompilationUnit cu, String variableName, UMLType type, boolean varargsParameter,
							   LocationInfo locationInfo, List<UMLAnnotation> annotations, List<UMLModifier> modifiers) {
		this.variableName = variableName;
		this.type = type;
		this.varargsParameter = varargsParameter;
		this.locationInfo = locationInfo;
		this.annotations = annotations != null ? annotations : java.util.Collections.emptyList();
		this.modifiers = modifiers != null ? modifiers : java.util.Collections.emptyList();
		this.initializer = null;

		if (LocationInfo.CodeElementType.FIELD_DECLARATION.equals(locationInfo.getCodeElementType())) {
			this.isAttribute = true;
		}
		if (LocationInfo.CodeElementType.SINGLE_VARIABLE_DECLARATION.equals(locationInfo.getCodeElementType())) {
			this.isParameter = true;
		}

		this.scope = new VariableScope(cu, locationInfo.getFilePath());
		this.isFinal = false;

		// Generate proper signature for parameters
		StringBuilder signature = new StringBuilder();
		if (varargsParameter) {
			if (variableName.startsWith("**")) {
				signature.append(variableName); // **kwargs
			} else if (variableName.startsWith("*")) {
				signature.append(variableName); // *args
			} else {
				signature.append("*").append(variableName); // *param
			}
		} else {
			signature.append(variableName);
		}
		if (type != null && !type.toString().equals("Object")) {
			signature.append(": ").append(type.toString());
		}
		this.actualSignature = signature.toString();
	}

	public VariableDeclaration(CompilationUnit cu, String sourceFolder, String filePath, VariableDeclarationFragment fragment, VariableDeclarationContainer container, String javaFileContent) {
		this.annotations = new ArrayList<UMLAnnotation>();
		this.modifiers = new ArrayList<UMLModifier>();
		List<IExtendedModifier> extendedModifiers = null;
		if(fragment.getParent() instanceof VariableDeclarationStatement) {
			VariableDeclarationStatement parent = (VariableDeclarationStatement)fragment.getParent();
			extendedModifiers = parent.modifiers();
			int modifiers = parent.getModifiers();
			if((modifiers & Modifier.FINAL) != 0) {
				this.isFinal = true;
			}
		}
		else if(fragment.getParent() instanceof VariableDeclarationExpression) {
			VariableDeclarationExpression parent = (VariableDeclarationExpression)fragment.getParent();
			extendedModifiers = parent.modifiers();
			int modifiers = parent.getModifiers();
			if((modifiers & Modifier.FINAL) != 0) {
				this.isFinal = true;
			}
		}
		else if(fragment.getParent() instanceof FieldDeclaration) {
			FieldDeclaration parent = (FieldDeclaration)fragment.getParent();
			extendedModifiers = parent.modifiers();
			int modifiers = parent.getModifiers();
			if((modifiers & Modifier.FINAL) != 0) {
				this.isFinal = true;
			}
		}
		int startSignatureOffset = -1;
		if(extendedModifiers != null) {
			for(IExtendedModifier extendedModifier : extendedModifiers) {
				if(extendedModifier.isAnnotation()) {
					Annotation annotation = (Annotation)extendedModifier;
					this.annotations.add(new UMLAnnotation(cu, sourceFolder, filePath, annotation, javaFileContent));
				}
				else if(extendedModifier.isModifier()) {
					Modifier modifier = (Modifier)extendedModifier;
					this.modifiers.add(new UMLModifier(cu, sourceFolder, filePath, modifier));
					if(startSignatureOffset == -1) {
						startSignatureOffset = modifier.getStartPosition();
					}
				}
			}
		}
		this.locationInfo = new LocationInfo(cu, sourceFolder, filePath, fragment, extractVariableDeclarationType(fragment));
		this.variableName = fragment.getName().getIdentifier();
		this.initializer = fragment.getInitializer() != null ? new AbstractExpression(cu, sourceFolder, filePath, fragment.getInitializer(), CodeElementType.VARIABLE_DECLARATION_INITIALIZER, container, javaFileContent) : null;
		Type astType = extractType(fragment);
		if(astType != null) {
			this.type = UMLType.extractTypeObject(cu, sourceFolder, filePath, astType, fragment.getExtraDimensions(), javaFileContent);
			if(startSignatureOffset == -1) {
				startSignatureOffset = astType.getStartPosition();
			}
		}
		if(startSignatureOffset == -1) {
			startSignatureOffset = fragment.getName().getStartPosition();
		}
		ASTNode scopeNode = getScopeNode(fragment);
		int startOffset = 0;
		if(locationInfo.getCodeElementType().equals(CodeElementType.FIELD_DECLARATION)) {
			//field declarations have the entire type declaration as scope, regardless of the location they are declared
			startOffset = scopeNode.getStartPosition();
		}
		else {
			startOffset = fragment.getStartPosition();
		}
		int endOffset = scopeNode.getStartPosition() + scopeNode.getLength();
		this.scope = new VariableScope(cu, filePath, startOffset, endOffset);
		boolean anonymousClassInitializer = fragment.getInitializer() instanceof ClassInstanceCreation && ((ClassInstanceCreation)fragment.getInitializer()).getAnonymousClassDeclaration() != null;
		int endSignatureOffset = anonymousClassInitializer ?
				((ClassInstanceCreation)fragment.getInitializer()).getAnonymousClassDeclaration().getStartPosition() + 1 :
					fragment.getStartPosition() + fragment.getLength();
		this.actualSignature = javaFileContent.substring(startSignatureOffset, endSignatureOffset);
	}

	public VariableDeclaration(CompilationUnit cu, String sourceFolder, String filePath, SingleVariableDeclaration fragment, VariableDeclarationContainer container, String javaFileContent) {
		this.annotations = new ArrayList<UMLAnnotation>();
		this.modifiers = new ArrayList<UMLModifier>();
		int modifiers = fragment.getModifiers();
		if((modifiers & Modifier.FINAL) != 0) {
			this.isFinal = true;
		}
		List<IExtendedModifier> extendedModifiers = fragment.modifiers();
		for(IExtendedModifier extendedModifier : extendedModifiers) {
			if(extendedModifier.isAnnotation()) {
				Annotation annotation = (Annotation)extendedModifier;
				this.annotations.add(new UMLAnnotation(cu, sourceFolder, filePath, annotation, javaFileContent));
			}
			else if(extendedModifier.isModifier()) {
				Modifier modifier = (Modifier)extendedModifier;
				this.modifiers.add(new UMLModifier(cu, sourceFolder, filePath, modifier));
			}
		}
		this.locationInfo = new LocationInfo(cu, sourceFolder, filePath, fragment, extractVariableDeclarationType(fragment));
		this.variableName = fragment.getName().getIdentifier();
		this.initializer = fragment.getInitializer() != null ? new AbstractExpression(cu, sourceFolder, filePath, fragment.getInitializer(), CodeElementType.VARIABLE_DECLARATION_INITIALIZER, container, javaFileContent) : null;
		Type astType = extractType(fragment);
		this.type = UMLType.extractTypeObject(cu, sourceFolder, filePath, astType, fragment.getExtraDimensions(), javaFileContent);
		int startOffset = fragment.getStartPosition();
		ASTNode scopeNode = getScopeNode(fragment);
		int endOffset = scopeNode.getStartPosition() + scopeNode.getLength();
		this.scope = new VariableScope(cu, filePath, startOffset, endOffset);
	}

	public VariableDeclaration(CompilationUnit cu, String sourceFolder, String filePath, SingleVariableDeclaration fragment, VariableDeclarationContainer container, boolean varargs, String javaFileContent) {
		this(cu, sourceFolder, filePath, fragment, container, javaFileContent);
		this.varargsParameter = varargs;
		if(varargs) {
			this.type.setVarargs();
		}
	}

	public VariableDeclaration(CompilationUnit cu, String sourceFolder, String filePath, EnumConstantDeclaration fragment, String javaFileContent) {
		this.annotations = new ArrayList<UMLAnnotation>();
		this.modifiers = new ArrayList<UMLModifier>();
		int modifiers = fragment.getModifiers();
		if((modifiers & Modifier.FINAL) != 0) {
			this.isFinal = true;
		}
		this.isEnumConstant = true;
		int startSignatureOffset = -1;
		List<IExtendedModifier> extendedModifiers = fragment.modifiers();
		for(IExtendedModifier extendedModifier : extendedModifiers) {
			if(extendedModifier.isAnnotation()) {
				Annotation annotation = (Annotation)extendedModifier;
				this.annotations.add(new UMLAnnotation(cu, sourceFolder, filePath, annotation, javaFileContent));
			}
			else if(extendedModifier.isModifier()) {
				Modifier modifier = (Modifier)extendedModifier;
				this.modifiers.add(new UMLModifier(cu, sourceFolder, filePath, modifier));
				if(startSignatureOffset == -1) {
					startSignatureOffset = modifier.getStartPosition();
				}
			}
		}
		this.locationInfo = new LocationInfo(cu, sourceFolder, filePath, fragment, CodeElementType.ENUM_CONSTANT_DECLARATION);
		this.variableName = fragment.getName().getIdentifier();
		this.initializer = null;
		if(startSignatureOffset == -1) {
			startSignatureOffset = fragment.getName().getStartPosition();
		}
		if(fragment.getParent() instanceof EnumDeclaration) {
			EnumDeclaration enumDeclaration = (EnumDeclaration)fragment.getParent();
			this.type = UMLType.extractTypeObject(enumDeclaration.getName().getIdentifier());
		}
		ASTNode scopeNode = fragment.getParent();
		int startOffset = scopeNode.getStartPosition();
		int endOffset = scopeNode.getStartPosition() + scopeNode.getLength();
		this.scope = new VariableScope(cu, filePath, startOffset, endOffset);
		int endSignatureOffset = fragment.getAnonymousClassDeclaration() != null ?
				fragment.getAnonymousClassDeclaration().getStartPosition() + 1 :
				fragment.getStartPosition() + fragment.getLength();
		this.actualSignature = javaFileContent.substring(startSignatureOffset, endSignatureOffset);
	}

	public String getActualSignature() {
		return actualSignature;
	}

	public LeafExpression asLeafExpression() {
		String asString = type != null ? type.toQualifiedString() : "" + variableName;
		return new LeafExpression(asString, getLocationInfo());
	}

	public String getVariableName() {
		return variableName;
	}

	public AbstractExpression getInitializer() {
		return initializer;
	}

	public UMLType getType() {
		return type;
	}

	public VariableScope getScope() {
		return scope;
	}

	public boolean isLocalVariable() {
		return !isParameter && !isAttribute && !isEnumConstant;
	}

	public boolean isParameter() {
		return isParameter;
	}

	public void setParameter(boolean isParameter) {
		this.isParameter = isParameter;
	}

	public boolean isAttribute() {
		return isAttribute;
	}

	public void setAttribute(boolean isAttribute) {
		this.isAttribute = isAttribute;
	}

	public boolean isEnumConstant() {
		return isEnumConstant;
	}

	public boolean isVarargsParameter() {
		return varargsParameter;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public List<UMLAnnotation> getAnnotations() {
		return annotations;
	}

	public List<UMLModifier> getModifiers() {
		return modifiers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((variableName == null) ? 0 : variableName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariableDeclaration other = (VariableDeclaration) obj;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (variableName == null) {
			if (other.variableName != null)
				return false;
		} else if (!variableName.equals(other.variableName))
			return false;
		return true;
	}

	public boolean sameKind(VariableDeclaration other) {
		return this.isParameter == other.isParameter && this.isEnumConstant == other.isEnumConstant && this.isAttribute == other.isAttribute;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(variableName).append(" : ");
		if(varargsParameter) {
			String typeString = type.toString();
			int lastArrayIndex = typeString.lastIndexOf("[]");
			if (lastArrayIndex > 0) {
				sb.append(typeString.substring(0, lastArrayIndex));
			} else {
				// Fallback: if no "[]" found, just append the type as-is
				sb.append(typeString);
			}
			sb.append("...");
		}
		else {
			sb.append(type);
		}
		return sb.toString();
	}


	public String toQualifiedString() {
		StringBuilder sb = new StringBuilder();
        sb.append(variableName).append(" : ");
        if(varargsParameter) {
        	sb.append(type.toQualifiedString().substring(0, type.toQualifiedString().lastIndexOf("[]")));
        	sb.append("...");
        }
        else {
        	sb.append(type.toQualifiedString());
        }
        return sb.toString();
	}

	public LocationInfo getLocationInfo() {
		return locationInfo;
	}

	public CodeRange codeRange() {
		return locationInfo.codeRange();
	}

	private static ASTNode getScopeNode(org.eclipse.jdt.core.dom.VariableDeclaration variableDeclaration) {
		if(variableDeclaration instanceof SingleVariableDeclaration) {
			return variableDeclaration.getParent();
		}
		else if(variableDeclaration instanceof VariableDeclarationFragment) {
			return variableDeclaration.getParent().getParent();
		}
		return null;
	}

	private static CodeElementType extractVariableDeclarationType(org.eclipse.jdt.core.dom.VariableDeclaration variableDeclaration) {
		if(variableDeclaration instanceof SingleVariableDeclaration) {
			return CodeElementType.SINGLE_VARIABLE_DECLARATION;
		}
		else if(variableDeclaration instanceof VariableDeclarationFragment) {
			VariableDeclarationFragment fragment = (VariableDeclarationFragment)variableDeclaration;
			if(fragment.getParent() instanceof VariableDeclarationStatement) {
				return CodeElementType.VARIABLE_DECLARATION_STATEMENT;
			}
			else if(fragment.getParent() instanceof VariableDeclarationExpression) {
				return CodeElementType.VARIABLE_DECLARATION_EXPRESSION;
			}
			else if(fragment.getParent() instanceof FieldDeclaration) {
				return CodeElementType.FIELD_DECLARATION;
			}
			else if(fragment.getParent() instanceof LambdaExpression) {
				return CodeElementType.LAMBDA_EXPRESSION_PARAMETER;
			}
		}
		return null;
	}

	private static Type extractType(org.eclipse.jdt.core.dom.VariableDeclaration variableDeclaration) {
		Type returnedVariableType = null;
		if(variableDeclaration instanceof SingleVariableDeclaration) {
			SingleVariableDeclaration singleVariableDeclaration = (SingleVariableDeclaration)variableDeclaration;
			returnedVariableType = singleVariableDeclaration.getType();
		}
		else if(variableDeclaration instanceof VariableDeclarationFragment) {
			VariableDeclarationFragment fragment = (VariableDeclarationFragment)variableDeclaration;
			if(fragment.getParent() instanceof VariableDeclarationStatement) {
				VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement)fragment.getParent();
				returnedVariableType = variableDeclarationStatement.getType();
			}
			else if(fragment.getParent() instanceof VariableDeclarationExpression) {
				VariableDeclarationExpression variableDeclarationExpression = (VariableDeclarationExpression)fragment.getParent();
				returnedVariableType = variableDeclarationExpression.getType();
			}
			else if(fragment.getParent() instanceof FieldDeclaration) {
				FieldDeclaration fieldDeclaration = (FieldDeclaration)fragment.getParent();
				returnedVariableType = fieldDeclaration.getType();
			}
		}
		return returnedVariableType;
	}

	public boolean equalVariableDeclarationType(VariableDeclaration other) {
		return this.locationInfo.getCodeElementType().equals(other.locationInfo.getCodeElementType());
	}

	public boolean equalType(VariableDeclaration other) {
		if(this.getType() == null && other.getType() == null) {
			return true;
		}
		else if(this.getType() != null && other.getType() != null) {
			return this.getType().equals(other.getType());
		}
		return false;
	}

	public boolean equalQualifiedType(VariableDeclaration other) {
		if(this.getType() == null && other.getType() == null) {
			return true;
		}
		else if(this.getType() != null && other.getType() != null) {
			return this.getType().equalsQualified(other.getType());
		}
		return false;
	}

	public VariableDeclaration getVariableDeclaration() {
		return this;
	}

	public void addStatementInScope(AbstractCodeFragment statement) {
		if(scope.subsumes(statement.getLocationInfo())) {
			List<LeafExpression> variables = statement.getVariables();
			boolean matchFound = false;
			for(LeafExpression variable : variables) {
				if(variable.getString().equals(variableName) || (isAttribute && variable.getString().equals(JAVA.THIS_DOT + variableName))) {
					scope.addStatementUsingVariable(statement);
					matchFound = true;
					break;
				}
			}
			if(!matchFound) {
				for(LeafExpression variable : variables) {
					if(variable.getString().startsWith(variableName + ".")) {
						scope.addStatementUsingVariable(statement);
						break;
					}
				}
			}
		}
	}

	public Set<AbstractCodeFragment> getStatementsInScopeUsingVariable() {
		return scope.getStatementsInScopeUsingVariable();
	}
}
