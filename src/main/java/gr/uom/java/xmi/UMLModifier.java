package gr.uom.java.xmi;

import extension.ast.node.LangASTNode;
import extension.ast.node.unit.LangCompilationUnit;
import gr.uom.java.xmi.LocationInfo.CodeElementType;
import gr.uom.java.xmi.diff.CodeRange;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Modifier;

import java.io.Serializable;

public class UMLModifier implements Serializable, LocationInfoProvider {
	private String keyword;
	private LocationInfo locationInfo;
	
	public UMLModifier(CompilationUnit cu, String sourceFolder, String filePath, Modifier modifier) {
		this.keyword = modifier.getKeyword().toString();
		this.locationInfo = new LocationInfo(cu, sourceFolder, filePath, modifier, CodeElementType.MODIFIER);
	}

	public UMLModifier(LangCompilationUnit cu, String sourceFolder, String filePath, String keyword, LangASTNode astNode) {
		this.keyword = keyword;
		this.locationInfo = new LocationInfo(cu, sourceFolder, filePath, astNode, CodeElementType.MODIFIER);
	}


	public String getKeyword() {
		return keyword;
	}

	public LocationInfo getLocationInfo() {
		return locationInfo;
	}

	public CodeRange codeRange() {
		return locationInfo.codeRange();
	}

	public String toString() {
		return keyword;
	}
}
