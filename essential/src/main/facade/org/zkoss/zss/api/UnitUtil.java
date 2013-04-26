package org.zkoss.zss.api;

import org.zkoss.poi.ss.usermodel.Row;
import org.zkoss.zss.api.model.NSheet;
import org.zkoss.zss.model.Book;
import org.zkoss.zss.model.Worksheet;
import org.zkoss.zss.ui.impl.Utils;

public class UnitUtil {

	
	
	
	/** convert pixel to EMU */
	public static int pxToEmu(int px) {
		//refer form ActionHandler
		return (int) Math.round(((double)px) * 72 * 20 * 635 / 96); //assume 96dpi
	}
	
	
	public static int getRowHeightInPx(NSheet sheet,int row){
		return Utils.getRowHeightInPx(sheet.getNative(), row);
	}
	
	public static int getColumnWidthInPx(NSheet sheet,int col){
		return Utils.getColumnWidthInPx(sheet.getNative(), col);
	}

	/**
	 * convert column width(char 256) to pixel
	 * @return the width in pixel
	 */
	public static int char256ToPx(int width256, int charWidthPx) {
		return Utils.fileChar256ToPx(width256,charWidthPx);
	}
	
	/**
	 * convert row height(twip) to pixel
	 * @return the height in pixel
	 */
	public static int twipToPx(int twip) {
		return Utils.twipToPx(twip);
	}
	
	public static NSheetAnchor toFilledAnchor(NSheet sheet,int row, int column, int widthPx, int heightPx){
		int lRow = 0;
		int lColumn = 0;
		int lX = 0;
		int lY = 0;
		
		Worksheet ws = sheet.getNative();
		Book book = ws.getBook();
		for(int i = column;;i++){
			if(ws.isColumnHidden(i)){
				continue;
			}
			int wPx = UnitUtil.getColumnWidthInPx(sheet,i);
			widthPx -= wPx;
			if(widthPx<=0){
				lColumn = i-1;
				lX = wPx + widthPx;//offset
				break;
			}
		}
		
		
		for(int i = row;;i++){
			Row srow = ws.getRow(i);
			if(srow!=null && srow.getZeroHeight()){
				continue;
			}
			
			int hPx = UnitUtil.getRowHeightInPx(sheet, i);
			heightPx -= hPx;
			if(heightPx<=0){
				lRow = i-1;
				lY = hPx + heightPx;
				break;
			}
		}
		return new NSheetAnchor(row,column,0,0,lRow,lColumn,lX,lY);
	}
}
