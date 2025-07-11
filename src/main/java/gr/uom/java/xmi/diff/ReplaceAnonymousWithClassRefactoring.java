package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLAnonymousClass;
import gr.uom.java.xmi.UMLClass;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ReplaceAnonymousWithClassRefactoring implements Refactoring {
	private UMLAnonymousClass anonymousClass;
	private UMLClass addedClass;
	private UMLAnonymousToClassDiff diff;
	
	public ReplaceAnonymousWithClassRefactoring(UMLAnonymousClass anonymousClass, UMLClass addedClass, UMLAnonymousToClassDiff diff) {
		this.anonymousClass = anonymousClass;
		this.addedClass = addedClass;
		this.diff = diff;
	}

	public UMLAnonymousClass getAnonymousClass() {
		return anonymousClass;
	}

	public UMLClass getAddedClass() {
		return addedClass;
	}

	public UMLAnonymousToClassDiff getDiff() {
		return diff;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append("\t");
		sb.append(anonymousClass.getCodePath());
		sb.append(" with ");
		sb.append(addedClass);
		return sb.toString();
	}

	public String getName() {
		return this.getRefactoringType().getDisplayName();
	}

	public RefactoringType getRefactoringType() {
		return RefactoringType.REPLACE_ANONYMOUS_WITH_CLASS;
	}

	public Set<ImmutablePair<String, String>> getInvolvedClassesBeforeRefactoring() {
		Set<ImmutablePair<String, String>> pairs = new LinkedHashSet<ImmutablePair<String, String>>();
		pairs.add(new ImmutablePair<String, String>(getAnonymousClass().getLocationInfo().getFilePath(), getAnonymousClass().getName()));
		return pairs;
	}

	public Set<ImmutablePair<String, String>> getInvolvedClassesAfterRefactoring() {
		Set<ImmutablePair<String, String>> pairs = new LinkedHashSet<ImmutablePair<String, String>>();
		pairs.add(new ImmutablePair<String, String>(getAddedClass().getLocationInfo().getFilePath(), getAddedClass().getName()));
		return pairs;
	}

	@Override
	public List<CodeRange> leftSide() {
		List<CodeRange> ranges = new ArrayList<CodeRange>();
		ranges.add(anonymousClass.codeRange()
				.setDescription("anonymous class declaration")
				.setCodeElement(anonymousClass.getName()));
		return ranges;
	}

	@Override
	public List<CodeRange> rightSide() {
		List<CodeRange> ranges = new ArrayList<CodeRange>();
		ranges.add(addedClass.codeRange()
				.setDescription("added type declaration")
				.setCodeElement(addedClass.getName()));
		return ranges;
	}
}
