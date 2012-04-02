/*
 * Copyright (C) 2000-2012  InfoChamp System Corporation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gk.engine.client.build.grid.field;

import org.gk.engine.client.build.grid.XGridField;
import org.gk.engine.client.event.IEventConstants;

import com.extjs.gxt.ui.client.widget.grid.AggregationRowConfig;
import com.extjs.gxt.ui.client.widget.grid.SummaryType;
import com.google.gwt.i18n.client.NumberFormat;

/**
 * 建立AggregationRowConfig
 * 
 * @author W11239
 * @since 2010/12/10
 */
public class GAggregationRowBuilder extends GridFieldBuilder {

	public GAggregationRowBuilder(String fieldType) {
		super(fieldType);
	}

	@Override
	public Object create() {
		XGridField x = (XGridField) getField().clone();
		String htmlId = x.getAttribute("htmlId", "");
		String html = x.getAttribute("html", "");
		String sumId = x.getAttribute("sumId", "");
		String sumType = x.getAttribute("sumType", "");
		String format = x.getAttribute("format", "");

		AggregationRowConfig config = new AggregationRowConfig();
		config.setHtml(htmlId, html);
		String[] idColon = sumId.split(IEventConstants.SPLIT_COLON);
		String[] typeColon = sumType.split(IEventConstants.SPLIT_COLON);
		String[] formatColon = format.split(IEventConstants.SPLIT_COLON);

		if (idColon.length == typeColon.length
				&& typeColon.length == formatColon.length) {
			for (int i = 0; i < idColon.length; i++) {
				config.setSummaryType(idColon[i], getSummaryType(typeColon[i]));
				config.setSummaryFormat(idColon[i],
						getSummaryFormat(formatColon[i]));
			}
		}
		return config;
	}

	/**
	 * 取得SummaryType，預設為COUNT
	 * 
	 * @param type
	 * @return SummaryType
	 */
	private SummaryType getSummaryType(String type) {
		SummaryType sType = SummaryType.COUNT;
		if (type.equalsIgnoreCase("avg")) {
			sType = SummaryType.AVG;
		} else if (type.equalsIgnoreCase("sum")) {
			sType = SummaryType.SUM;
		} else if (type.equalsIgnoreCase("max")) {
			sType = SummaryType.MAX;
		} else if (type.equalsIgnoreCase("min")) {
			sType = SummaryType.MIN;
		}
		return sType;
	}

	/**
	 * 取得NumberFormat
	 * 
	 * @param format
	 * @return NumberFormat
	 */
	private NumberFormat getSummaryFormat(String format) {
		NumberFormat nf = null;
		if (format.equalsIgnoreCase("currency")) {
			nf = NumberFormat.getCurrencyFormat();
		} else if (format.equalsIgnoreCase("scientific")) {
			nf = NumberFormat.getScientificFormat();
		} else if (format.equalsIgnoreCase("decimal")) {
			nf = NumberFormat.getDecimalFormat();
		} else if (format.equalsIgnoreCase("percent")) {
			nf = NumberFormat.getPercentFormat();
		} else {
			nf = NumberFormat.getFormat(format);
		}
		return nf;
	}
}