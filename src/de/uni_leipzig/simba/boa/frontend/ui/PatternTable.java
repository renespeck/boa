package de.uni_leipzig.simba.boa.frontend.ui;

import java.text.DecimalFormat;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Table;

import de.uni_leipzig.simba.boa.frontend.BoaFrontendApplication;

@SuppressWarnings("serial")
public class PatternTable extends Table {
	
	public static final Object[] NATURAL_COL_ORDER = new Object[] {
		 "id",	"confidence",	"globalConfidence"	,"support",	"typicity",	"specificity",	"naturalLanguageRepresentation",	"numberOfOccurrences"};

	public static final String[] COL_HEADERS_ENGLISH = new String[] {
		 "id",	"CONF",			"GCONF"				,"SUPP",		"TYP",		"SPEC",			"naturalLanguageRepresentation",	"OCC" };
	
	public PatternTable(BoaFrontendApplication app, Container dataSource) {
		
//		setPageLength(Math.max(15, dataSource.size()));
		setSizeFull();
		setContainerDataSource(dataSource);
		
		setVisibleColumns(PatternTable.NATURAL_COL_ORDER);
		setColumnHeaders(PatternTable.COL_HEADERS_ENGLISH);
		
		setSortContainerPropertyId(NATURAL_COL_ORDER[1]);
		setSortAscending(false);
		sort();
		
		setColumnWidth(NATURAL_COL_ORDER[0],50);
		setColumnWidth(NATURAL_COL_ORDER[1],50);
		setColumnWidth(NATURAL_COL_ORDER[2],50);
		setColumnWidth(NATURAL_COL_ORDER[3],50);
		setColumnWidth(NATURAL_COL_ORDER[4],50);
		setColumnWidth(NATURAL_COL_ORDER[5],50);
		setColumnWidth(NATURAL_COL_ORDER[7],50);
		
		setColumnCollapsingAllowed(true);
		setColumnReorderingAllowed(true);

		/*
		 * Make table selectable, react immediatedly to user events, and pass
		 * events to the controller (our main application)
		 */
		setSelectable(true);
		setImmediate(true);
		addListener((ItemClickListener) app);
		/* We don't want to allow users to de-select a row */
		setNullSelectionAllowed(false);
	}
	@Override
    protected String formatPropertyValue(Object rowId, Object colId, Property property) {
        
		try {
			
			// Format by property type
	        if (property.getType() == Double.class) {
	        	
	        	DecimalFormat df = new DecimalFormat("#.###");
	            return df.format((Double)property.getValue());
	        }

	        return super.formatPropertyValue(rowId, colId, property);
		}
		catch (IllegalArgumentException iae) {
			
			return "-1";
		}
    }
}