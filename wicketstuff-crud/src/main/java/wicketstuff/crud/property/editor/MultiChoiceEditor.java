package wicketstuff.crud.property.editor;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.ListMultipleChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.IValidator;

import wicketstuff.crud.Editor;

public class MultiChoiceEditor extends Panel implements Editor
{
	private final ListMultipleChoice field;

	public MultiChoiceEditor(String id, IModel model, IModel choices, IChoiceRenderer renderer)
	{
		super(id);
		add(field = new ListMultipleChoice("choices", model, choices, renderer));
	}

	public void setRequired(boolean required)
	{
		field.setRequired(required);
	}

	public void add(IValidator validator)
	{
		field.add(validator);
	}

	public void setLabel(IModel label)
	{
		field.setLabel(label);

	}
}