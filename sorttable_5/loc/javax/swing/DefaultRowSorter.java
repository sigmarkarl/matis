package javax.swing;

import java.util.List;

import javax.swing.RowSorter.SortKey;

public abstract class DefaultRowSorter<M, I> extends RowSorter<M> {
	private List<SortKey> sortKeys;
	
	public void setSortKeys(List<SortKey> sortKeys) { this.sortKeys = sortKeys; }
	
	public List<? extends SortKey> getSortKeys() {
        return sortKeys;
    }	
}