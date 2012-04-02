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
package org.gk.engine.client.build;

import java.util.List;

import org.gk.engine.client.build.form.FieldBuilder;
import org.gk.engine.client.build.form.FormBuilder;
import org.gk.engine.client.build.form.FormRowBuilder;
import org.gk.engine.client.build.form.HeaderBuilder;
import org.gk.engine.client.build.form.field.AdaptFieldBuilder;
import org.gk.engine.client.build.form.field.ButtonBuilder;
import org.gk.engine.client.build.form.field.CheckBoxBuilder;
import org.gk.engine.client.build.form.field.ComboBoxBuilder;
import org.gk.engine.client.build.form.field.DateFieldBuilder;
import org.gk.engine.client.build.form.field.DateRangeFieldBuilder;
import org.gk.engine.client.build.form.field.FileUploadFieldBuilder;
import org.gk.engine.client.build.form.field.FormFieldBuilder;
import org.gk.engine.client.build.form.field.ImageFieldBuilder;
import org.gk.engine.client.build.form.field.LabelFieldBuilder;
import org.gk.engine.client.build.form.field.NumFieldBuilder;
import org.gk.engine.client.build.form.field.RadioBuilder;
import org.gk.engine.client.build.form.field.SliderFieldBuilder;
import org.gk.engine.client.build.form.field.SpinnerFieldBuilder;
import org.gk.engine.client.build.form.field.TextAreaBuilder;
import org.gk.engine.client.build.form.field.TimeFieldBuilder;
import org.gk.engine.client.build.form.field.TriggerFieldBuilder;
import org.gk.engine.client.build.form.field.TxtFieldBuilder;
import org.gk.engine.client.build.form.field.YMFieldBuilder;
import org.gk.engine.client.build.frame.FrameBuilder;
import org.gk.engine.client.build.grid.GridBuilder;
import org.gk.engine.client.build.grid.field.GAdaptFieldBuilder;
import org.gk.engine.client.build.grid.field.GAggregationRowBuilder;
import org.gk.engine.client.build.grid.field.GButtonBuilder;
import org.gk.engine.client.build.grid.field.GCheckBoxBuilder;
import org.gk.engine.client.build.grid.field.GComboBoxBuilder;
import org.gk.engine.client.build.grid.field.GDateFiledBuilder;
import org.gk.engine.client.build.grid.field.GDateRangeFieldBuilder;
import org.gk.engine.client.build.grid.field.GHeaderGroupBuilder;
import org.gk.engine.client.build.grid.field.GICBuilder;
import org.gk.engine.client.build.grid.field.GImageFieldBuilder;
import org.gk.engine.client.build.grid.field.GLabelFieldBuilder;
import org.gk.engine.client.build.grid.field.GNumFieldBuilder;
import org.gk.engine.client.build.grid.field.GRadioBuilder;
import org.gk.engine.client.build.grid.field.GSliderFieldBuilder;
import org.gk.engine.client.build.grid.field.GSpinnerFieldBuilder;
import org.gk.engine.client.build.grid.field.GTextAreaBuilder;
import org.gk.engine.client.build.grid.field.GTimeFieldBuilder;
import org.gk.engine.client.build.grid.field.GTriggerFieldBuilder;
import org.gk.engine.client.build.grid.field.GTxtFieldBuilder;
import org.gk.engine.client.build.grid.field.GYMFiledBuilder;
import org.gk.engine.client.build.grid.field.GridFieldBuilder;
import org.gk.engine.client.build.gul.GULBuilder;
import org.gk.engine.client.build.js.JavaScriptBuilder;
import org.gk.engine.client.build.layout.BorderLayoutDataBuilder;
import org.gk.engine.client.build.layout.BoxLayoutDataBuilder;
import org.gk.engine.client.build.layout.FormDataBuilder;
import org.gk.engine.client.build.layout.RowDataBuilder;
import org.gk.engine.client.build.layout.TableDataBuilder;
import org.gk.engine.client.build.menu.MenuBarBuilder;
import org.gk.engine.client.build.menu.MenuBarItemBuilder;
import org.gk.engine.client.build.menu.MenuBuilder;
import org.gk.engine.client.build.menu.MenuItemBuilder;
import org.gk.engine.client.build.page.PageBuilder;
import org.gk.engine.client.build.panel.ContentPanelBuilder;
import org.gk.engine.client.build.panel.FieldSetBuilder;
import org.gk.engine.client.build.panel.HtmlPanelBuilder;
import org.gk.engine.client.build.panel.PanelBuilder;
import org.gk.engine.client.build.panel.WindowBuilder;
import org.gk.engine.client.build.portal.PortalBuilder;
import org.gk.engine.client.build.portal.PortletBuilder;
import org.gk.engine.client.build.tab.TabBuilder;
import org.gk.engine.client.build.tab.TabPanelBuilder;
import org.gk.engine.client.build.toolbar.ToolBarBuilder;
import org.gk.engine.client.build.tree.TreeBuilder;
import org.gk.engine.client.build.tree.TreeDirBuilder;
import org.gk.ui.client.com.form.gkList;

/**
 * 建構器工廠
 * 
 * @author i23250
 * @since 2010/10/18
 */
public class BuilderFactory {

	/**
	 * 建立容器建構器
	 * 
	 * @return List
	 */
	public static List createBuilders() {
		List<Builder> builders = new gkList();
		builders.add(new PageBuilder("page"));
		builders.add(new TabBuilder("tab"));
		builders.add(new GridBuilder("grid"));
		builders.add(new FormBuilder("form"));
		builders.add(new FormRowBuilder("formRow"));
		builders.add(new HeaderBuilder("header"));
		builders.add(new FieldBuilder("field"));
		builders.add(new ToolBarBuilder("toolbar"));
		builders.add(new TabPanelBuilder("tabPanel"));
		builders.add(new PortletBuilder("portlet"));
		builders.add(new PortalBuilder("portal"));
		builders.add(new FrameBuilder("frame"));
		builders.add(new ContentPanelBuilder("contentPanel"));
		builders.add(new WindowBuilder("window"));
		builders.add(new PanelBuilder("panel"));
		builders.add(new HtmlPanelBuilder("hp,htmlPanel"));
		builders.add(new JavaScriptBuilder("js"));
		builders.add(new GULBuilder("gul"));
		builders.add(new MenuBuilder("menu"));
		builders.add(new MenuItemBuilder("menuItem"));
		builders.add(new MenuBarBuilder("menuBar"));
		builders.add(new MenuBarItemBuilder("menuBarItem"));
		builders.add(new FieldSetBuilder("fieldSet"));
		builders.add(new TreeBuilder("tree"));
		builders.add(new TreeDirBuilder("dir"));
		// builders.add(new TreeGridBuilder("treeGrid")); 廢棄不用
		return builders;
	}

	/**
	 * 建立Layout建構器
	 * 
	 * @return List
	 */
	public static List createLayoutBuilders() {
		List<Builder> builders = new gkList();
		builders.add(new RowDataBuilder("row"));
		builders.add(new BorderLayoutDataBuilder("border"));
		builders.add(new TableDataBuilder("table"));
		builders.add(new FormDataBuilder("formd"));
		builders.add(new BoxLayoutDataBuilder("box"));
		return builders;
	}

	/**
	 * 建立表單欄位建構器
	 * 
	 * @return List
	 */
	public static List createFormFieldBuilders() {
		List<FormFieldBuilder> builders = new gkList();
		builders.add(new LabelFieldBuilder("label"));
		builders.add(new TxtFieldBuilder("txt"));
		builders.add(new ButtonBuilder("btn"));
		builders.add(new NumFieldBuilder("num"));
		builders.add(new AdaptFieldBuilder("adapt"));
		builders.add(new CheckBoxBuilder("check,checkBox"));
		builders.add(new RadioBuilder("radio,radioBox"));
		builders.add(new TextAreaBuilder("textArea"));
		builders.add(new DateFieldBuilder("date"));
		builders.add(new TimeFieldBuilder("time"));
		builders.add(new YMFieldBuilder("ym"));
		builders.add(new DateRangeFieldBuilder("dateRange"));
		builders.add(new ComboBoxBuilder("combo,comboBox"));
		builders.add(new SliderFieldBuilder("slider"));
		builders.add(new SpinnerFieldBuilder("spin"));
		builders.add(new ImageFieldBuilder("img"));
		builders.add(new TriggerFieldBuilder("trigger"));
		// builders.add(new ListFieldBuilder("list")); 廢棄不用
		// 以下builder為form有，grid沒有
		// builders.add(new HtmlEditorBuilder("editor")); 廢棄不用
		// builders.add(new TwinTriggerFieldBuilder("twintrigger")); 廢棄不用
		builders.add(new FileUploadFieldBuilder("file"));
		// builders.add(new TagFieldBuilder("tag")); 廢棄不用
		return builders;
	}

	/**
	 * 建立清單欄位建構器
	 * 
	 * @return List
	 */
	public static List createGridFieldBuilders() {
		List<GridFieldBuilder> builders = new gkList();
		builders.add(new GLabelFieldBuilder("label"));
		builders.add(new GTxtFieldBuilder("txt"));
		builders.add(new GButtonBuilder("btn"));
		builders.add(new GNumFieldBuilder("num"));
		builders.add(new GAdaptFieldBuilder("adapt"));
		builders.add(new GCheckBoxBuilder("checkBox"));
		builders.add(new GRadioBuilder("radioBox"));
		builders.add(new GTextAreaBuilder("textarea"));
		builders.add(new GDateFiledBuilder("date"));
		builders.add(new GTimeFieldBuilder("time"));
		builders.add(new GYMFiledBuilder("ym"));
		builders.add(new GDateRangeFieldBuilder("dateRange"));
		builders.add(new GComboBoxBuilder("combo,comboBox"));
		builders.add(new GSliderFieldBuilder("slider"));
		builders.add(new GSpinnerFieldBuilder("spin"));
		builders.add(new GImageFieldBuilder("img"));
		builders.add(new GTriggerFieldBuilder("trigger"));
		// builders.add(new GListFieldBuilder("list")); 廢棄不用
		// 以下builder為grid有，form沒有
		builders.add(new GICBuilder("ic"));
		builders.add(new GHeaderGroupBuilder("header"));
		builders.add(new GAggregationRowBuilder("aggRow"));
		return builders;
	}
}
