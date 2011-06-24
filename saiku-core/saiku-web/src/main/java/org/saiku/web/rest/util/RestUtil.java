package org.saiku.web.rest.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.saiku.olap.dto.resultset.AbstractBaseCell;
import org.saiku.olap.dto.resultset.CellDataSet;
import org.saiku.olap.dto.resultset.DataCell;
import org.saiku.olap.dto.resultset.MemberCell;
import org.saiku.web.rest.objects.resultset.Cell;

public class RestUtil {
	
	public static ArrayList<Cell[]> convert(ResultSet rs) {
		Integer width = 0;
        Integer height = 0;
        Cell[] header = null;
        ArrayList<Cell[]> rows = new ArrayList<Cell[]>();
        // System.out.println("DATASET");
        try {
			while (rs.next()) {
			    if (height == 0) {
			        width = rs.getMetaData().getColumnCount();
			        header = new Cell[width];
			        for (int s = 0; s < width; s++) {
			            header[s] = new Cell(rs.getMetaData().getColumnName(s + 1),Cell.Type.COLUMN_HEADER);
			        }
			        if (width > 0) {
			            rows.add(header);
			            // System.out.println(" |");
			        }
			    }
			    Cell[] row = new Cell[width];
			    for (int i = 0; i < width; i++) {
			    	String content = rs.getString(i + 1);
			        
			        if (content == null)
			            content = "";
			        row[i] = new Cell(content, Cell.Type.DATA_CELL);
			    }
			    rows.add(row);
			    height++;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rows;
	}
	public static ArrayList<Cell[]> convert(CellDataSet cellSet) {
		ArrayList<Cell[]> rows = new ArrayList<Cell[]>();
		if (cellSet == null || cellSet.getCellSetBody() == null || cellSet.getCellSetHeaders() == null) {
			return rows;
		}
		AbstractBaseCell[][] body = cellSet.getCellSetBody();
		AbstractBaseCell[][] headers = cellSet.getCellSetHeaders();
		
		
		
		for (AbstractBaseCell header[] : headers) {
			rows.add(convert(header, Cell.Type.COLUMN_HEADER));
		}
		
		for (AbstractBaseCell row[] : body) {
			rows.add(convert(row, Cell.Type.ROW_HEADER));
		}
		return rows;
		
	}
	
	public static Cell[] convert(AbstractBaseCell[] acells, Cell.Type headertype) {
		Cell[]  cells = new Cell[acells.length];
		for (int i = 0; i < acells.length; i++) {
			cells[i] = convert(acells[i], headertype);
		}
		return cells;
	}
	
	public static Cell convert(AbstractBaseCell acell, Cell.Type headertype) {
		if (acell != null) {
			if (acell instanceof DataCell) {
				DataCell dcell = (DataCell) acell;
				Properties metaprops = new Properties();
				// metaprops.put("color", "" + dcell.getColorValue());
				ArrayList<Integer> coordinates = new ArrayList<Integer>();
				for (Integer number : dcell.getCoordinates()) {
					coordinates.add(number);
				}
				metaprops.put("coordinates", coordinates);
				metaprops.put("formattedValue", "" + dcell.getFormattedValue());
				// metaprops.put("rawValue", "" + dcell.getRawValue());
				metaprops.put("rawNumber", "" + dcell.getRawNumber());
				
				Properties props = new Properties();
				props.putAll(dcell.getProperties());
				
				// TODO no properties  (NULL) for now - 
				return new Cell(dcell.getFormattedValue(), Cell.Type.DATA_CELL);
			}
			if (acell instanceof MemberCell) {
				MemberCell mcell = (MemberCell) acell;
				Properties metaprops = new Properties();
				metaprops.put("children", "" + mcell.getChildMemberCount());
				metaprops.put("uniqueName", "" + mcell.getUniqueName());
				metaprops.put("formattedValue", "" +  mcell.getFormattedValue());
				metaprops.put("rawValue", "" + mcell.getRawValue());
				
				Properties props = new Properties();
				props.putAll(mcell.getProperties());

				// TODO no properties  (NULL) for now - 
				if ("row_header_header".equals(mcell.getProperty("__headertype"))) {
					headertype = Cell.Type.ROW_HEADER_HEADER;
				}
				return new Cell("" + mcell.getFormattedValue(), headertype);
			}

		
		}
		throw new RuntimeException("Cannot convert Cell. Type Mismatch!");

	}

}
