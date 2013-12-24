/* XUtils.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mar 20, 2008 12:40:01 PM     2008, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zss.ngapi.impl.imexp;

import java.awt.font.*;
import java.text.AttributedString;

import org.zkoss.poi.ss.usermodel.*;


/**
 * copied from ZSS project to make ng-model run.
 * We should remove it after integrating with ZSS.
 *
 */
public class XUtils {

	
	public static int getWidthAny(Sheet zkSheet,int col, int charWidth){
		int w = zkSheet.getColumnWidth(col);
		if (w == zkSheet.getDefaultColumnWidth() * 256) { //default column width
			return XUtils.defaultColumnWidthToPx(w / 256, charWidth);
		}
		return fileChar256ToPx(w, charWidth);
	}
	
	public static int getHeightAny(Sheet sheet, int row){
		return getRowHeightInPx(sheet, sheet.getRow(row));
	}
	
	public static int getRowHeightInPx(Sheet sheet, Row row) {
		final int defaultHeight = sheet.getDefaultRowHeight();
		int h = row == null ? defaultHeight : row.getHeight();
		if (h == 0xFF) {
			h = defaultHeight;
		}
		return twipToPx(h);
	}
	
	//calculate the default char width in pixel per the given Font
	public static int calcDefaultCharWidth(java.awt.Font font) {
        /**
         * Excel measures columns in units of 1/256th of a character width
         * but the docs say nothing about what particular character is used.
         * '0' looks to be a good choice. ref. http://support.microsoft.com/kb/214123
         */
        final String defaultString = "0";

        final FontRenderContext frc = new FontRenderContext(null, true, true);
        final AttributedString str = new AttributedString(defaultString);
        copyAttributes(font, str, 0, defaultString.length());
        final TextLayout layout = new TextLayout(str.getIterator(), frc);
        //TODO, don't know how to calculate the charter width per the Font
        final double w = layout.getAdvance();
        final int charWidth = (int) Math.floor(w + 0.5) +  1;
        return charWidth;
	}
	
    private static void copyAttributes(java.awt.Font font, AttributedString str, int startIdx, int endIdx) {
        str.addAttribute(TextAttribute.FAMILY, font.getFontName(), startIdx, endIdx);
        str.addAttribute(TextAttribute.SIZE, new Float(font.getSize2D()), startIdx, endIdx);
        if (font.isBold()) str.addAttribute(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD, startIdx, endIdx);
        if (font.isItalic()) str.addAttribute(TextAttribute.POSTURE, TextAttribute.POSTURE_OBLIQUE, startIdx, endIdx);
    }

	/** convert pixel to point */
	public static int pxToPoint(int px) {
		return px * 72 / 96; //assume 96dpi
	}
	
	/** convert point to pixel */
	public static int pointToPx(int point) {
		return point * 96 / 72; //assume 96dpi
	}
	
	/** convert pixel to twip (1/20 point) */
	public static int pxToTwip(int px) {
		return px * 72 * 20 / 96; //assume 96dpi
	}

	/** convert twip (1/20 point) to pixel */
	public static int twipToPx(int twip) {
		return twip * 96 / 72 / 20; //assume 96dpi
	}
	
	/** convert point to twip (1/20 point) */
	public static int pointToTwip(int px) {
		return px * 20; 
	}

	/** convert twip (1/20 point) to point */
	public static int twipToPoint(int twip) {
		return twip / 20; 
	}

	/** convert file 1/256 character width to pixel */
	public static int fileChar256ToPx(int char256, int charWidth) {
		final double w = (double) char256;
		return (int) Math.floor(w * charWidth / 256 + 0.5);
	}
	
	/** convert pixel to file 1/256 character width */
	public static int pxToFileChar256(int px, int charWidth) {
		final double w = (double) px;
		return (int) Math.floor(w * 256 / charWidth + 0.5);
	}
	
	/** convert 1/256 character width to pixel */
	public static int screenChar256ToPx(int char256, int charWidth) {
		final double w = (double) char256;
		return (char256 < 256) ?
			(int) Math.floor(w * (charWidth + 5) / 256 + 0.5):
			(int) Math.floor(w * charWidth  / 256 + 0.5) + 5;
	}
	
	/** Convert pixel to 1/256 character width */
	public static int pxToScreenChar256(int px, int charWidth) {
		return px < (charWidth + 5) ? 
				px * 256 / (charWidth + 5):
				(px - 5) * 256 / charWidth;
	}
	
	/** convert character width to pixel */
	public static int screenChar1ToPx(double w, int charWidth) {
		return w < 1 ?
			(int) Math.floor(w * (charWidth + 5) + 0.5):
			(int) Math.floor(w * charWidth + 0.5) + 5;
	}
	
	/** Convert pixel to character width (for application) */
	public static double pxToScreenChar1(int px, int charWidth) {
		final double w = (double) px;
		return px < (charWidth + 5) ?
			roundTo100th(w / (charWidth + 5)):
			roundTo100th((w - 5) / charWidth);
	}
	
	
	public static double pxToCTChar(int px, int charWidth) {
		return (double) pxToFileChar256(px, charWidth) / 256;
	}
	
	private static double roundTo100th(double w) {
		return Math.floor(w * 100 + 0.5) / 100;
	}
	
	public static int getDefaultColumnWidthInPx(Sheet sheet, int charWidth) {
		int columnWidth = sheet != null ? sheet.getDefaultColumnWidth() : -1;
		return columnWidth <= 0 ? 64 : XUtils.defaultColumnWidthToPx(columnWidth, charWidth);
	}
	
	/** 
	 * Convert default columns width (in character) to pixel.
	 * 5 pixels are margin padding.(There are 4 pixels of margin padding (two on each side), plus 1 pixel padding for the gridlines.)
	 * Description of how column widths are determined in Excel (http://support.microsoft.com/kb/214123)
	 * @param columnWidth number of character
	 * @param charWidth Using the Calibri font, the maximum digit width of 11 point font size is 7 pixels (at 96 dpi).
	 * @return width in pixel  Rounds this number up to the nearest multiple of 8 pixels.
	 */ 
	public static int defaultColumnWidthToPx(int columnWidth, int charWidth) {
		final int w = columnWidth * charWidth + 5;
		final int diff = w % 8;
		return w + (diff > 0 ? (8 - diff) : 0);
	}
	
	/**
	 * Convert default column width in pixel to number of character by reverse defaultColumnWidthToPx() and ignore the mod(%) operation.
	 * @param px default column width in pixel
	 * @param charWidth  one character width in pixel of normal style's font 
	 * @return default column width in character
	 */
	public static int pxToDefaultColumnWidth(int px, int charWidth) {
		return (px - 5) / charWidth;
	}
}
