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
package org.gk.ui.client.binding;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gk.ui.client.com.form.gkList;
import org.gk.ui.client.com.form.gkMap;

public class gkFormBinding {

	private Map<String, List> bindings = new gkMap();

	public void addFieldBinging(gkFieldBinding binding) {
		if (!bindings.containsKey(binding.getName())) {
			bindings.put(binding.getName(), new gkList());
		}
		List list = bindings.get(binding.getName());
		list.add(binding);
	}

	public void publish(Map data) {
		Iterator it = data.entrySet().iterator();
		while (it.hasNext()) {
			Entry entry = (Entry) it.next();
			Object key = entry.getKey();
			if (bindings.containsKey(key)) {
				Iterator<gkFieldBinding> it2 = bindings.get(key).iterator();
				while (it2.hasNext()) {
					gkFieldBinding binding = it2.next();
					binding.execute(entry.getValue());
				}
			}
		}
	}
}
