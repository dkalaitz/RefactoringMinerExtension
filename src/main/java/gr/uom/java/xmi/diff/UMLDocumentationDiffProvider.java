package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.VariableDeclarationContainer;

import java.util.Optional;

public interface UMLDocumentationDiffProvider {
	public Optional<UMLJavadocDiff> getJavadocDiff();
	public UMLCommentListDiff getCommentListDiff();
	public VariableDeclarationContainer getContainer1();
	public VariableDeclarationContainer getContainer2();
}
