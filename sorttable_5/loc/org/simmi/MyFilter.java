package org.simmi;

import javax.swing.table.TableModel;

import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.decorator.PipelineEvent;
import org.jdesktop.swingx.decorator.PipelineListener;

public class MyFilter extends FilterPipeline {
	String			filterText;
	int				fInd = 1;
	Filter			filt;
	static PatternFilter	pfilt;
	
	public MyFilter( TableModel model ) {
		super( new Filter[] { pfilt } );
		filt = pfilt;
		pfilt = null;
		
		init();
	}
	
	public MyFilter( Filter[] filters ) {
		super( filters );
		filt = filters[0];
		
		init();
	}
	
	public void init() {}
	
	public void addFilterListener( final Runnable r ) {
		this.addPipelineListener( new PipelineListener() {
			public void contentsChanged(PipelineEvent arg0) {
				r.run();
			}
		});
	}
	
	public void updateFilter() {
		if( filt instanceof PatternFilter ) {
			PatternFilter pfilt = (PatternFilter)filt;
			pfilt.setPattern( filterText, 0 );
			pfilt.setColumnIndex( 0 );
			
			this.filterChanged( filt );
		} else {
			this.filterChanged( filt );
		}
	}
}
