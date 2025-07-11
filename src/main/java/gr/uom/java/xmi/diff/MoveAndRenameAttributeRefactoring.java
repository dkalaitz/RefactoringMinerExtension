package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLAttribute;
import gr.uom.java.xmi.decomposition.AbstractCodeMapping;
import org.refactoringminer.api.RefactoringType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MoveAndRenameAttributeRefactoring extends MoveAttributeRefactoring implements ReferenceBasedRefactoring {
	private Set<CandidateAttributeRefactoring> attributeRenames;
	
	public MoveAndRenameAttributeRefactoring(UMLAttribute originalAttribute, UMLAttribute movedAttribute,
			Set<CandidateAttributeRefactoring> attributeRenames) {
		super(originalAttribute, movedAttribute);
		this.attributeRenames = attributeRenames;
	}

	public Set<AbstractCodeMapping> getReferences() {
		Set<AbstractCodeMapping> references = new LinkedHashSet<AbstractCodeMapping>();
		for(CandidateAttributeRefactoring candidate : attributeRenames) {
			references.addAll(candidate.getReferences());
		}
		return references;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append("\t");
		sb.append(originalAttribute.toQualifiedString());
		sb.append(" renamed to ");
		sb.append(movedAttribute.toQualifiedString());
		sb.append(" and moved from class ");
		sb.append(getSourceClassName());
		sb.append(" to class ");
		sb.append(getTargetClassName());
		return sb.toString();
	}

	public String getName() {
		return this.getRefactoringType().getDisplayName();
	}

	public RefactoringType getRefactoringType() {
		return RefactoringType.MOVE_RENAME_ATTRIBUTE;
	}

	@Override
	public List<CodeRange> rightSide() {
		List<CodeRange> ranges = new ArrayList<CodeRange>();
		ranges.add(movedAttribute.codeRange()
				.setDescription("moved and renamed attribute declaration")
				.setCodeElement(movedAttribute.toString()));
		return ranges;
	}
}
