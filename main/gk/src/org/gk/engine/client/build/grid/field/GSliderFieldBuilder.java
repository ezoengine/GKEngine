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
import org.gk.engine.client.utils.IRegExpUtils;
import org.gk.ui.client.com.grid.column.gkSliderFieldColumnConfig;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SliderField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class GSliderFieldBuilder extends GridFieldBuilder {

	public GSliderFieldBuilder(String slider) {
		super(slider);
	}

	@Override
	public Object create() {
		final XGridField x = (XGridField) getField().clone();
		ColumnConfig cc = new gkSliderFieldColumnConfig(x) {

			@Override
			public void onField(Field field) {
				setAttribute(field, x);
			}

			@Override
			public Field createField() {
				// 變動量
				String inc = x.getAttribute("inc", "");
				// 最大值
				String max = x.getAttribute("max", "");
				// 最小值
				String min = x.getAttribute("min", "");
				// click調整
				String clickToChange = x.getAttribute("clickToChange", "true");

				String value = x.getValue();

				Slider slider = new Slider();
				if (inc.matches(IRegExpUtils.INTEGER)) {
					slider.setIncrement(Integer.parseInt(inc));
				}

				if (max.matches(IRegExpUtils.INTEGER)) {
					slider.setMaxValue(Integer.parseInt(max));
				}

				if (min.matches(IRegExpUtils.INTEGER)) {
					slider.setMinValue(Integer.parseInt(min));
				}

				if (value.matches(IRegExpUtils.INTEGER)) {
					slider.setValue(Integer.parseInt(value));
				}

				if (!Boolean.parseBoolean(x.getEnable())) {
					slider.disable();
					// enable="false"时slider仍然可以拖动改变值，
					// 这里通过设定Draggable辅助完成enable="false"的效果
					slider.setDraggable(false);
				}

				slider.setClickToChange(Boolean.parseBoolean(clickToChange));

				SliderField sf = new SliderField(slider) {
					@Override
					public void setFieldLabel(String fieldLabel) {
						super.setFieldLabel(fieldLabel);
						setHeader(fieldLabel);
					}
				};
				sf.setFieldLabel(x.getLabel());

				if (Boolean.parseBoolean(x.getReadOnly())) {
					sf.setReadOnly(true);
					// readOnly="true"时slider仍然可以拖动改变值，
					// 这里通过设定Draggable辅助完成readOnly="true"的效果
					slider.setDraggable(false);
				}

				slider.addListener(Events.Change, new Listener<SliderEvent>() {
					@Override
					public void handleEvent(SliderEvent be) {
						((SliderField) be.getSlider().getParent())
								.fireEvent(Events.Change);
					}
				});

				return sf;
			}
		};

		return cc;
	}
}
