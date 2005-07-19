package wicket.contrib.data.model.bind;

import java.util.Collection;
import java.util.List;

import wicket.Component;
import wicket.model.IModel;

/**
 * A column for a drop down choice (select list).
 * 
 * @author Phil Kulak
 */
public class DropDownChoiceColumn extends ValidatingColumn
{
	private Collection choices;
	
	public DropDownChoiceColumn(String displayName, String ognlPath, Collection choices)
	{
		super(displayName, ognlPath);
		this.choices = choices;
	}
	
	public Component getComponent(String id, IModel model)
	{
		return prepare(new DropDownChoicePanel(id, model, choices));
	}
}
