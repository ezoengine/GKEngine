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
package org.gk.engine.client.build.form.field;

import java.util.Map;

import org.gk.engine.client.utils.IRegExpUtils;
import org.gk.ui.client.binding.gkFieldBinding;
import org.gk.ui.client.binding.gkNumberFieldBinding;
import org.gk.ui.client.com.panel.gkFormPanelIC;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SliderEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Slider;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.PropertyEditor;
import com.extjs.gxt.ui.client.widget.form.SliderField;

public class SliderFieldBuilder extends FormFieldBuilder {

	public SliderFieldBuilder(String fieldType) {
		super(fieldType);
	}

	@Override
	public Component create() {
		Field field = createField();
		initField(field);
		return field;
	}

	@Override
	public Component create(gkFormPanelIC form) {
		SliderField field = createField();
		gkFieldBinding fb = new gkNumberFieldBinding(field, getField()
				.getName(), (Map) form.getInfo());
		form.addFieldBinding(fb);
		initField(field);
		return field;
	}

	private SliderField createField() {
		// 變動量
		String inc = getField().getAttribute("inc", "");
		// 最大值
		String max = getField().getAttribute("max", "");
		// 最小值
		String min = getField().getAttribute("min", "");
		// click調整
		String clickToChange = getField().getAttribute("clickToChange", "true");

		final Slider slider = new Slider();

		if (inc.matches(IRegExpUtils.INTEGER)) {
			slider.setIncrement(Integer.parseInt(inc));
		}

		if (max.matches(IRegExpUtils.INTEGER)) {
			slider.setMaxValue(Integer.parseInt(max));
		}

		if (min.matches(IRegExpUtils.INTEGER)) {
			slider.setMinValue(Integer.parseInt(min));
		}

		slider.setClickToChange(Boolean.parseBoolean(clickToChange));

		// 監聽Disable事件，觸發時將draggable設為false
		slider.addListener(Events.Disable, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				slider.setDraggable(false);
			}
		});

		SliderField sf = new SliderField(slider);

		slider.addListener(Events.Change, new Listener<SliderEvent>() {
			@Override
			public void handleEvent(SliderEvent be) {
				((SliderField) be.getSlider().getParent())
						.fireEvent(Events.Change);
			}
		});

		PropertyEditor<Integer> pe = new PropertyEditor<Integer>() {

			@Override
			public String getStringValue(Integer value) {
				return Integer.toString(value);
			}

			@Override
			public Integer convertStringValue(String value) {
				return Integer.parseInt(value);
			}
		};

		sf.setPropertyEditor(pe);
		return sf;
	}

	private void initField(Field field) {
		field.setFieldLabel(getField().getLabel());

		String value = getField().getValue();
		if (value.matches(IRegExpUtils.INTEGER)) {
			field.setValue(Integer.parseInt(value));
			field.fireEvent(Events.Change);
		}
	}
}
