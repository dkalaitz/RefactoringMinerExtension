package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.UMLModifier;
import gr.uom.java.xmi.UMLOperation;
import gr.uom.java.xmi.Visibility;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ChangeOperationAccessModifierRefactoring implements Refactoring {
	private Visibility originalAccessModifier;
	private Visibility changedAccessModifier;
	private UMLOperation operationBefore;
	private UMLOperation operationAfter;

	public ChangeOperationAccessModifierRefactoring(Visibility originalAccessModifier, Visibility changedAccessModifier,
			UMLOperation operationBefore, UMLOperation operationAfter) {
		this.originalAccessModifier = originalAccessModifier;
		this.changedAccessModifier = changedAccessModifier;
		this.operationBefore = operationBefore;
		this.operationAfter = operationAfter;
	}

	public UMLModifier getOldModifier() {
		for(UMLModifier m : operationBefore.getModifiers()) {
			if(m.getKeyword().equals(originalAccessModifier.toString())) {
				return m;
			}
		}
		return null;
	}

	public UMLModifier getNewModifier() {
		for(UMLModifier m : operationAfter.getModifiers()) {
			if(m.getKeyword().equals(changedAccessModifier.toString())) {
				return m;
			}
		}
		return null;
	}

	public Visibility getOriginalAccessModifier() {
		return originalAccessModifier;
	}

	public Visibility getChangedAccessModifier() {
		return changedAccessModifier;
	}

	public UMLOperation getOperationBefore() {
		return operationBefore;
	}

	public UMLOperation getOperationAfter() {
		return operationAfter;
	}

	@Override
	public List<CodeRange> leftSide() {
		List<CodeRange> ranges = new ArrayList<CodeRange>();
		ranges.add(operationBefore.codeRange()
				.setDescription("original method declaration")
				.setCodeElement(operationBefore.toString()));
		return ranges;
	}

	@Override
	public List<CodeRange> rightSide() {
		List<CodeRange> ranges = new ArrayList<CodeRange>();
		ranges.add(operationAfter.codeRange()
				.setDescription("method declaration with changed access modifier")
				.setCodeElement(operationAfter.toString()));
		return ranges;
	}

	@Override
	public RefactoringType getRefactoringType() {
		return RefactoringType.CHANGE_OPERATION_ACCESS_MODIFIER;
	}

	@Override
	public String getName() {
		return getRefactoringType().getDisplayName();
	}

	@Override
	public Set<ImmutablePair<String, String>> getInvolvedClassesBeforeRefactoring() {
		Set<ImmutablePair<String, String>> pairs = new LinkedHashSet<ImmutablePair<String, String>>();
		pairs.add(new ImmutablePair<String, String>(getOperationBefore().getLocationInfo().getFilePath(), getOperationBefore().getClassName()));
		return pairs;
	}

	@Override
	public Set<ImmutablePair<String, String>> getInvolvedClassesAfterRefactoring() {
		Set<ImmutablePair<String, String>> pairs = new LinkedHashSet<ImmutablePair<String, String>>();
		pairs.add(new ImmutablePair<String, String>(getOperationAfter().getLocationInfo().getFilePath(), getOperationAfter().getClassName()));
		return pairs;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getName()).append("\t");
		sb.append(originalAccessModifier);
		sb.append(" to ");
		sb.append(changedAccessModifier);
		sb.append(" in method ");
		sb.append(operationAfter.toQualifiedString());
		sb.append(" from class ").append(operationAfter.getClassName());
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changedAccessModifier == null) ? 0 : changedAccessModifier.hashCode());
		result = prime * result + ((operationAfter == null) ? 0 : operationAfter.hashCode());
		result = prime * result + ((operationBefore == null) ? 0 : operationBefore.hashCode());
		result = prime * result + ((originalAccessModifier == null) ? 0 : originalAccessModifier.hashCode());
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
		ChangeOperationAccessModifierRefactoring other = (ChangeOperationAccessModifierRefactoring) obj;
		if (changedAccessModifier == null) {
			if (other.changedAccessModifier != null)
				return false;
		} else if (!changedAccessModifier.equals(other.changedAccessModifier))
			return false;
		if (operationAfter == null) {
			if (other.operationAfter != null)
				return false;
		} else if (!operationAfter.equals(other.operationAfter))
			return false;
		if (operationBefore == null) {
			if (other.operationBefore != null)
				return false;
		} else if (!operationBefore.equals(other.operationBefore))
			return false;
		if (originalAccessModifier == null) {
			if (other.originalAccessModifier != null)
				return false;
		} else if (!originalAccessModifier.equals(other.originalAccessModifier))
			return false;
		return true;
	}
}
