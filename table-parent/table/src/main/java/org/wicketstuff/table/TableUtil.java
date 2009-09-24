package org.wicketstuff.table;

import java.util.HashSet;
import java.util.Set;

import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;

public class TableUtil
{

	public static ListSelectionModel createSingleSelectionModel()
	{
		ListSelectionModel selectionModel = new DefaultListSelectionModel();
		selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return selectionModel;
	}

	public static Set getRowsToUpdate(int[] oldSelection, int[] newSelection)
	{
		HashSet oldSelectionSet = new HashSet();
		for (int i = 0; i < oldSelection.length; i++)
		{
			oldSelectionSet.add(oldSelection[i]);
		}
		HashSet newSelectionSet = new HashSet();
		for (int i = 0; i < newSelection.length; i++)
		{
			newSelectionSet.add(newSelection[i]);
		}
		final Set newToUpdate = (Set)newSelectionSet.clone();
		newToUpdate.removeAll(oldSelectionSet);
		final Set oldToUpdate = (Set)oldSelectionSet.clone();
		oldToUpdate.removeAll(newSelectionSet);
		newToUpdate.addAll(oldToUpdate);
		return newToUpdate;
	}

	public static int[] getSelectedRows(ListSelectionModel listSelectionModel)
	{
		int iMin = listSelectionModel.getMinSelectionIndex();
		int iMax = listSelectionModel.getMaxSelectionIndex();

		if ((iMin == -1) || (iMax == -1))
		{
			return new int[0];
		}

		int[] rvTmp = new int[1 + (iMax - iMin)];
		int n = 0;
		for (int i = iMin; i <= iMax; i++)
		{
			if (listSelectionModel.isSelectedIndex(i))
			{
				rvTmp[n++] = i;
			}
		}
		int[] rv = new int[n];
		System.arraycopy(rvTmp, 0, rv, 0, n);
		return rv;
	}

}
