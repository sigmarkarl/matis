package javax.swing;

public abstract class RowFilter<M,I> {
	public static abstract class Entry<M, I> {
        /**
         * Creates an <code>Entry</code>.
         */
        public Entry() {
        }

        /**
         * Returns the underlying model.
         *
         * @return the model containing the data that this entry represents
         */
        public abstract M getModel();

        /**
         * Returns the number of values in the entry.  For
         * example, when used with a table this corresponds to the
         * number of columns.
         *
         * @return number of values in the object being filtered
         */
        public abstract int getValueCount();

        /**
         * Returns the value at the specified index.  This may return
         * <code>null</code>.  When used with a table, index
         * corresponds to the column number in the model.
         *
         * @param index the index of the value to get
         * @return value at the specified index
         * @throws <code>IndexOutOfBoundsException</code> if index &lt; 0 or
         *         &gt;= getValueCount
         */
        public abstract Object getValue(int index);

        /**
         * Returns the string value at the specified index.  If
         * filtering is being done based on <code>String</code> values
         * this method is preferred to that of <code>getValue</code>
         * as <code>getValue(index).toString()</code> may return a
         * different result than <code>getStringValue(index)</code>.
         * <p>
         * This implementation calls <code>getValue(index).toString()</code>
         * after checking for <code>null</code>.  Subclasses that provide
         * different string conversion should override this method if
         * necessary.
         *
         * @param index the index of the value to get
         * @return {@code non-null} string at the specified index
         * @throws <code>IndexOutOfBoundsException</code> if index &lt; 0 ||
         *         &gt;= getValueCount
         */
        public String getStringValue(int index) {
            Object value = getValue(index);
            return (value == null) ? "" : value.toString();
        }

        /**
         * Returns the identifer (in the model) of the entry.
         * For a table this corresponds to the index of the row in the model,
         * expressed as an <code>Integer</code>.
         *
         * @return a model-based (not view-based) identifier for
         *         this entry
         */
        public abstract I getIdentifier();
    }
}