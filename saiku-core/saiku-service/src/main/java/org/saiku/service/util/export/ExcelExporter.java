/*  
 *   Copyright 2012 OSBI Ltd
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.saiku.service.util.export;

import org.olap4j.CellSet;
import org.saiku.olap.dto.SaikuDimensionSelection;
import org.saiku.olap.dto.resultset.AbstractBaseCell;
import org.saiku.olap.dto.resultset.CellDataSet;
import org.saiku.olap.dto.resultset.DataCell;
import org.saiku.olap.util.OlapResultSetUtil;
import org.saiku.olap.util.formatter.HierarchicalCellSetFormatter;
import org.saiku.olap.util.formatter.ICellSetFormatter;

import org.saiku.service.util.export.excel.ExcelWorksheetBuilder;
import org.saiku.service.util.exception.SaikuServiceException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class ExcelExporter {

	public static byte[] exportExcel(CellSet cellSet, List<SaikuDimensionSelection> filters) {
		return exportExcel(cellSet, new HierarchicalCellSetFormatter(), filters);
	}

	public static byte[] exportExcel(CellSet cellSet,
                                     ICellSetFormatter formatter,
                                     List<SaikuDimensionSelection> filters) {
		CellDataSet table = OlapResultSetUtil.cellSet2Matrix(cellSet, formatter);
		//return getExcel(table);
		return getExcel(table, filters);
	}
	
	
	private static byte[] getExcel(CellDataSet table, List<SaikuDimensionSelection> filters) {
        // TBD Sheet name is parametric. Useful for future ideas or improvements
        ExcelWorksheetBuilder worksheetBuilder = new ExcelWorksheetBuilder(table, filters, "Sheet 1");
        return worksheetBuilder.build();
	}

	/**TO BE DELETE: sdw-saiku*/
	private static byte[] getExcel(CellDataSet table) {
		if (table != null) {

			AbstractBaseCell[][] rowData = table.getCellSetBody();
			AbstractBaseCell[][] rowHeader = table.getCellSetHeaders();

			String[][] result = new String[rowHeader.length + rowData.length][];
			for (int x = 0; x<rowHeader.length;x++) {
				List<String> cols = new ArrayList<String>();
				for(int y = 0; y < rowHeader[x].length;y++) {
					cols.add(rowHeader[x][y].getFormattedValue()); 
				}
				result[x]= cols.toArray(new String[cols.size()]);

			}
			for (int x = 0; x<rowData.length ;x++) {
				int xTarget = rowHeader.length + x;
				List<String> cols = new ArrayList<String>();
				for(int y = 0; y < rowData[x].length;y++) {
					String value = rowData[x][y].getFormattedValue();
					if (rowData[x][y] instanceof DataCell && ((DataCell) rowData[x][y]).getRawNumber() != null ) {
						value = ((DataCell) rowData[x][y]).getRawNumber().toString();
					}
					cols.add(value);
				}
				result[xTarget]= cols.toArray(new String[cols.size()]);

			}
			return export(result);
		}
		return new byte[0];
	}

	private static byte[] export(String[][] resultSet) {
		WritableWorkbook  wb = null;
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			wb = Workbook.createWorkbook(bout);
			wb.setColourRGB(Colour.BLUE, 0xf0,0xf8,0xff);
			wb.setColourRGB(Colour.PALE_BLUE, 0xf9,0xf9,0xf9);
			WritableSheet sheet = wb.createSheet("Sheet", 0); //$NON-NLS-1$

			WritableCellFormat cf;
			int[] columnwidth = new int[resultSet[0].length];
			if(resultSet.length > 0){
				for(int i =  0; i < resultSet.length; i++){
					String[] vs = resultSet[i];
					for(int j = 0; j < vs.length ; j++){
						String value = vs[j];
						if(value == null || value == "null")  //$NON-NLS-1$
							value=""; //$NON-NLS-1$

						if (columnwidth[j] < value.length()) {
							columnwidth[j] = value.length();
						}
						if(resultSet[0][j] == null || resultSet[0][j] == ""){
							cf = (i % 2 != 0 ? getEvenFormat(Alignment.LEFT) : getOddFormat(Alignment.LEFT));
						}					
						else {
							cf = (i % 2 != 0 ? getEvenFormat(Alignment.RIGHT) : getOddFormat(Alignment.RIGHT));

							try {
								Double v = Double.parseDouble(value); 
								WritableCellFormat nf = getNumberFormat();
								nf.setBackground(cf.getBackgroundColour());
								jxl.write.Number n = new jxl.write.Number(j, i , v , nf);
								sheet.addCell(n);
								continue;
							} catch (Exception e) {	}
						}
						Label label = new Label(j,i,value,cf);
						sheet.addCell(label);
					}
				}
				for(int k = 0;k<columnwidth.length;k++) {
					sheet.setColumnView(k, (int) (columnwidth[k]*1.4));
				}
				sheet.insertColumn(0);
				sheet.insertRow(0);

				wb.write();
				wb.close();
				byte[] output =bout.toByteArray();
				return output;

			}
		} catch (Throwable e) {
			throw new SaikuServiceException("Error creating excel export for query",e);
		}
		return new byte[0];
	}

	private static WritableCellFormat getOddFormat(Alignment alignment) throws WriteException {
		WritableCellFormat cs = getTextFormat(alignment);
		cs.setBackground(Colour.PALE_BLUE);
		return cs;
	}

	private static WritableCellFormat getEvenFormat(Alignment alignment) throws WriteException {
		WritableCellFormat cs = getTextFormat(alignment);
		cs.setBackground(Colour.BLUE);
		return cs;
	}

	private static WritableCellFormat getNumberFormat() throws WriteException {
		WritableCellFormat cs = new WritableCellFormat(new NumberFormat("###,###,###.0#")); //$NON-NLS-1$
		cs.setBorder(Border.ALL, BorderLineStyle.THIN);
		cs.setAlignment(Alignment.RIGHT);
		cs.setIndentation(1);
		return cs;
	}

	private static WritableCellFormat getTextFormat(Alignment alignment) throws WriteException {
		WritableCellFormat cs = new WritableCellFormat(new WritableFont(WritableFont.createFont("Verdana"),11)); //$NON-NLS-1$
		cs.setBorder(Border.ALL, BorderLineStyle.THIN);
		cs.setIndentation(1);
		cs.setAlignment(alignment);
		return cs;

	}
	
	public static boolean isDouble(String obj){
		try{
			Double.parseDouble(obj);
		}catch(NumberFormatException e){
			return false;
		}
		return true;
	}
	
}
