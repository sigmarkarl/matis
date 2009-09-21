package javax.swing;

import java.util.List;

import javax.swing.event.RowSorterListener;

public abstract class RowSorter<M> {
	public abstract List<? extends SortKey> getSortKeys();
	
	public static class SortKey {
        private int column;
        private SortOrder sortOrder;

        /**
         * Creates a <code>SortKey</code> for the specified column with
         * the specified sort order.
         *
         * @param column index of the column, in terms of the model
         * @param sortOrder the sorter order
         * @throws IllegalArgumentException if <code>sortOrder</code> is
         *         <code>null</code>
         */
        public SortKey(int column, SortOrder sortOrder) {
            if (sortOrder == null) {
                throw new IllegalArgumentException(
                        "sort order must be non-null");
            }
            this.column = column;
            this.sortOrder = sortOrder;
        }

        /**
         * Returns the index of the column.
         *
         * @return index of column
         */
        public final int getColumn() {
            return column;
        }

        /**
         * Returns the sort order of the column.
         *
         * @return the sort order of the column
         */
        public final SortOrder getSortOrder() {
            return sortOrder;
        }

        /**
         * Returns the hash code for this <code>SortKey</code>.
         *
         * @return hash code
         */
        public int hashCode() {
            int result = 17;
            result = 37 * result + column;
            result = 37 * result + sortOrder.hashCode();
            return result;
        }

        /**
         * Returns true if this object equals the specified object.
         * If the specified object is a <code>SortKey</code> and
         * references the same column and sort order, the two objects
         * are equal.
         *
         * @param o the object to compare to
         * @return true if <code>o</code> is equal to this <code>SortKey</code>
         */
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof SortKey) {
                return (((SortKey)o).column == column &&
                        ((SortKey)o).sortOrder == sortOrder);
            }
            return false;
        }
    }

	public void addRowSorterListener(RowSorterListener rowSorterListener) {
		
	}	
}