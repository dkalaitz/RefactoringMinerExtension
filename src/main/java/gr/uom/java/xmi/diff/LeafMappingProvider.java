package gr.uom.java.xmi.diff;

import gr.uom.java.xmi.decomposition.LeafMapping;

import java.util.List;

public interface LeafMappingProvider {
	List<LeafMapping> getSubExpressionMappings();
	void addSubExpressionMapping(LeafMapping newLeafMapping);
}
