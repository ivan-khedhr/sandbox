/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wicketstuff.table;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.swing.ListSelectionModel;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * Repeater component that add behavior to every row to handle clicks events and
 * manage the selection state.
 * 
 * @author Pedro Henrique Oliveira dos Santos
 * 
 */
public abstract class AjaxSelectableListView extends PageableListView
{
	// private static final Logger log =
	// LoggerFactory.getLogger(AjaxSelectableListView.class);
	private static final long serialVersionUID = 1L;
	protected ListSelectionModel listSelectionModel;

	public AjaxSelectableListView(String id, IModel model)
	{
		this(id, model, Integer.MAX_VALUE);
	}

	public AjaxSelectableListView(String id, List list)
	{
		this(id, list, Integer.MAX_VALUE);
	}

	public AjaxSelectableListView(String id, List list, int rowsPerPage)
	{
		this(id, new Model((Serializable)list), rowsPerPage);
	}

	public AjaxSelectableListView(String id, IModel model, int rowsPerPage)
	{
		this(id, model, rowsPerPage, TableUtil.createSingleSelectionModel());
	}

	public AjaxSelectableListView(String id, IModel model, int rowsPerPage,
			ListSelectionModel selectionModel)
	{
		super(id, model, rowsPerPage);
		this.listSelectionModel = selectionModel;
	}

	@Override
	protected SelectableListItem newItem(final int index)
	{
		final SelectableListItem listItem = new SelectableListItem(index, getListItemModel(
				getModel(), index), listSelectionModel)
		{
			@Override
			protected void onItemSelection(AjaxRequestTarget target, boolean shiftPressed,
					boolean ctrlPressed)
			{
				AjaxSelectableListView.this.rowClicked(this, target, shiftPressed, ctrlPressed);
			}

		};
		return listItem;
	}

	@Override
	protected void populateItem(ListItem rowItem)
	{
		populateSelectableItem((SelectableListItem)rowItem);
	}

	abstract protected void populateSelectableItem(SelectableListItem rowItem);

	private Integer lastNonShiftSelection;

	/**
	 * Method responsible to resolve items selection.
	 */
	protected void rowClicked(final SelectableListItem clickedItem,
			final AjaxRequestTarget target, boolean shiftPressed, boolean ctrlPressed)
	{
		int[] oldSelections = getSelectedRows();

		int newSelection = clickedItem.getIndex();

		if (shiftPressed
				&& getListSelectionModel().getSelectionMode() != ListSelectionModel.SINGLE_SELECTION)
		{
			int referenceSelection = lastNonShiftSelection == null ? 0 : lastNonShiftSelection;
			listSelectionModel.setSelectionInterval(Math.min(referenceSelection, newSelection),
					Math.max(referenceSelection, newSelection));
		}
		else if (ctrlPressed)
		{
			if (listSelectionModel.isSelectedIndex(newSelection))
			{
				listSelectionModel.removeSelectionInterval(newSelection, newSelection);
			}
			else
			{
				listSelectionModel.addSelectionInterval(newSelection, newSelection);
			}
		}
		else
		{
			listSelectionModel.setSelectionInterval(clickedItem.getIndex(), clickedItem
					.getIndex());
		}
		int[] newSelections = getSelectedRows();
		if (!shiftPressed)
		{
			lastNonShiftSelection = clickedItem.getIndex();
		}
		final Set toUpdate = TableUtil.getRowsToUpdate(oldSelections, newSelections);
		visitChildren(SelectableListItem.class, new IVisitor()
		{
			public Object component(Component component)
			{
				SelectableListItem listItem = (SelectableListItem)component;
				if (toUpdate.contains(listItem.getIndex()))
				{
					listItem.updateOnAjaxRequest(target);
				}
				return IVisitor.CONTINUE_TRAVERSAL;
			}
		});
		onSelection(clickedItem, target);
	}

	public SelectableListItem getMinSelection()
	{
		return (SelectableListItem)visitChildren(SelectableListItem.class, new IVisitor()
		{
			public Object component(Component component)
			{
				int rowIndex = ((SelectableListItem)component).getIndex();
				if (rowIndex == listSelectionModel.getMinSelectionIndex())
				{
					return component;
				}
				else
				{
					return IVisitor.CONTINUE_TRAVERSAL;
				}
			}
		});
	}

	/**
	 * Returns the indices of all selected rows.
	 * 
	 * @return an array of integers containing the indices of all selected rows,
	 *         or an empty array if no row is selected
	 * @see #getSelectedRow
	 */
	public int[] getSelectedRows()
	{
		return TableUtil.getSelectedRows(listSelectionModel);
	}

	/**
	 * Returns the number of selected rows.
	 * 
	 * @return the number of selected rows, 0 if no rows are selected
	 */
	public int getSelectedRowCount()
	{
		int iMin = listSelectionModel.getMinSelectionIndex();
		int iMax = listSelectionModel.getMaxSelectionIndex();
		int count = 0;

		for (int i = iMin; i <= iMax; i++)
		{
			if (listSelectionModel.isSelectedIndex(i))
			{
				count++;
			}
		}
		return count;
	}

	public ListSelectionModel getListSelectionModel()
	{
		return listSelectionModel;
	};


	public void clearSelection()
	{
		listSelectionModel.clearSelection();
	}

	protected void onSelection(SelectableListItem selectableListItem, AjaxRequestTarget target)
	{

	}

}