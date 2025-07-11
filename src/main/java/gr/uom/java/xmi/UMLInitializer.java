package gr.uom.java.xmi;

import gr.uom.java.xmi.decomposition.*;
import gr.uom.java.xmi.diff.CodeRange;

import java.io.Serializable;
import java.util.*;

public class UMLInitializer implements Serializable, VariableDeclarationContainer {
	private LocationInfo locationInfo;
	private String name;
	private String className;
	private boolean isStatic;
	private Optional<UMLAnonymousClass> anonymousClassContainer;
	private OperationBody body;
	private List<UMLAnonymousClass> anonymousClassList;
	private UMLJavadoc javadoc;
	private List<UMLComment> comments;
	private Map<String, Set<VariableDeclaration>> variableDeclarationMap;
	
	public UMLInitializer(String name, LocationInfo locationInfo) {
		this.name = name;
		this.locationInfo = locationInfo;
		this.comments = new ArrayList<UMLComment>();
		this.anonymousClassList = new ArrayList<UMLAnonymousClass>();
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
	}

	public UMLJavadoc getJavadoc() {
		return javadoc;
	}

	public void setJavadoc(UMLJavadoc javadoc) {
		this.javadoc = javadoc;
	}

	public List<UMLComment> getComments() {
		return comments;
	}

	@Override
	public LocationInfo getLocationInfo() {
		return locationInfo;
	}

	@Override
	public CodeRange codeRange() {
		LocationInfo info = getLocationInfo();
		return info.codeRange();
	}

	@Override
	public List<VariableDeclaration> getParameterDeclarationList() {
		return Collections.emptyList();
	}

	@Override
	public List<UMLType> getParameterTypeList() {
		return Collections.emptyList();
	}

	@Override
	public List<String> getParameterNameList() {
		return Collections.emptyList();
	}

	@Override
	public List<UMLParameter> getParametersWithoutReturnType() {
		return Collections.emptyList();
	}

	@Override
	public int getNumberOfNonVarargsParameters() {
		return 0;
	}

	@Override
	public boolean hasVarargsParameter() {
		return false;
	}

	@Override
	public OperationBody getBody() {
		return body;
	}

	public void setBody(OperationBody body) {
		this.body = body;
	}

	public void addAnonymousClass(UMLAnonymousClass anonymous) {
		this.anonymousClassList.add(anonymous);
	}

	public boolean isDeclaredInAnonymousClass() {
		return anonymousClassContainer != null && anonymousClassContainer.isPresent();
	}

	@Override
	public boolean isGetter() {
		return false;
	}

	@Override
	public boolean isSetter() {
		return false;
	}

	@Override
	public boolean isConstructor() {
		return false;
	}

	@Override
	public AbstractCall isDelegate() {
		return null;
	}

	public Optional<UMLAnonymousClass> getAnonymousClassContainer() {
		return anonymousClassContainer;
	}

	public void setAnonymousClassContainer(UMLAnonymousClass anonymousClass) {
		this.anonymousClassContainer = Optional.of(anonymousClass);
	}

	@Override
	public List<UMLAnonymousClass> getAnonymousClassList() {
		return anonymousClassList;
	}

	@Override
	public List<LambdaExpressionObject> getAllLambdas() {
		return body.getAllLambdas();
	}

	@Override
	public List<AbstractCall> getAllOperationInvocations() {
		return body.getAllOperationInvocations();
	}

	@Override
	public List<AbstractCall> getAllCreations() {
		return body.getAllCreations();
	}

	@Override
	public List<String> getAllVariables() {
		return body.getAllVariables();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getElementType() {
		return "initializer";
	}

	@Override
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String toString() {
		return name;
	}

	@Override
	public String toQualifiedString() {
		return name;
	}

	@Override
	public Map<String, Set<VariableDeclaration>> variableDeclarationMap() {
		if(this.variableDeclarationMap == null) {
			this.variableDeclarationMap = new LinkedHashMap<String, Set<VariableDeclaration>>();
			for(VariableDeclaration declaration : getAllVariableDeclarations()) {
				if(variableDeclarationMap.containsKey(declaration.getVariableName())) {
					variableDeclarationMap.get(declaration.getVariableName()).add(declaration);
				}
				else {
					Set<VariableDeclaration> variableDeclarations = new LinkedHashSet<VariableDeclaration>();
					variableDeclarations.add(declaration);
					variableDeclarationMap.put(declaration.getVariableName(), variableDeclarations);
				}
			}
		}
		return variableDeclarationMap;
	}

	@Override
	public UMLAnonymousClass findAnonymousClass(AnonymousClassDeclarationObject anonymousClassDeclaration) {
		for(UMLAnonymousClass anonymousClass : this.getAnonymousClassList()) {
			if(anonymousClass.getLocationInfo().equals(anonymousClassDeclaration.getLocationInfo())) {
				return anonymousClass;
			}
		}
		return null;
	}

	@Override
	public boolean hasTestAnnotation() {
		return false;
	}

	@Override
	public boolean hasParameterizedTestAnnotation() {
		return false;
	}

	@Override
	public boolean hasSetUpAnnotation() {
		return false;
	}

	@Override
	public boolean hasTearDownAnnotation() {
		return false;
	}

	@Override
	public boolean isRecursive() {
		return false;
	}

	@Override
	public boolean isMain() {
		return false;
	}

	@Override
	public List<UMLAnnotation> getAnnotations() {
		return Collections.emptyList();
	}
}
