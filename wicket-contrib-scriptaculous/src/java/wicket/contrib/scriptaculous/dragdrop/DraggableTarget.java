package wicket.contrib.scriptaculous.dragdrop;

import java.util.HashMap;
import java.util.Map;

import wicket.MarkupContainer;
import wicket.Page;
import wicket.PageParameters;
import wicket.contrib.scriptaculous.Indicator;
import wicket.contrib.scriptaculous.JavascriptBuilder;
import wicket.contrib.scriptaculous.ScriptaculousAjaxBehavior;
import wicket.contrib.scriptaculous.JavascriptBuilder.JavascriptFunction;
import wicket.markup.MarkupStream;
import wicket.markup.html.WebMarkupContainer;

public class DraggableTarget<T> extends WebMarkupContainer<T>
{
	private static final long serialVersionUID = 1L;
	private String draggableClass;
	private String indicatorId;
	private final Class<? extends Page> pageContribution;

	public DraggableTarget(MarkupContainer parent, String id, Class<? extends Page> pageContribution)
	{
		super(parent, id);
		this.pageContribution = pageContribution;

		setOutputMarkupId(true);
		add(ScriptaculousAjaxBehavior.newJavascriptBindingBehavior());
	}

	public void accepts(DraggableImage image)
	{
		this.draggableClass = image.getStyleClass();
	}

	public void setIndicator(Indicator indicator)
	{
		this.indicatorId = indicator.getId();
	}

	protected void onRender(MarkupStream markupStream)
	{
		super.onRender(markupStream);


		final CharSequence url = urlFor(null, pageContribution, new PageParameters());

		renderInitialAjaxRequest(url);

		final Map<String,Object> updaterOptions = new HashMap<String,Object>() {{
			if (null != indicatorId)
			{
				put("onLoading", new JavascriptFunction("function(request){ Element.show('indicator')}"));
				put("onComplete", new JavascriptFunction("function(request){Element.hide('indicator')}"));
			}
			put("parameters", "");
			put("evalScripts", Boolean.TRUE);
			put("asynchronous", Boolean.TRUE);

		}};

		final JavascriptBuilder builder = new JavascriptBuilder();
		Map<String,Object> dropOptions = new HashMap<String,Object>() {{
			put("accept", draggableClass);
			put("onDrop", new JavascriptFunction("function(element) { new Ajax.Updater('" + getId() + "', '"+ url+ "' " + builder.formatAsJavascriptHash(updaterOptions) + ") }"));
			put("hoverclass", getId() + "-active");
		}};
		builder.addLine("Droppables.add('" + getId() + "', ");
		builder.addOptions(dropOptions);
		builder.addLine(");");


		getResponse().write(builder.buildScriptTagString());
	}

	private void renderInitialAjaxRequest(CharSequence url)
	{
		Map<String,Object> options = new HashMap<String,Object>() {{
			put("evalScripts", Boolean.TRUE);
			put("asynchronous", Boolean.TRUE);
		}};
		JavascriptBuilder builder = new JavascriptBuilder();
		builder.addLine("new Ajax.Updater('" + getId() + "', ");
		builder.addLine("  '" + url+ "', ");
		builder.addOptions(options);
		getResponse().write(builder.buildScriptTagString());
	}
}
