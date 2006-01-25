package wicket.contrib.dojo.examples.rssreader;

import java.util.StringTokenizer;

import wicket.AttributeModifier;
import wicket.contrib.dojo.DojoAjaxHandler;
import wicket.markup.ComponentTag;
import wicket.markup.html.WebMarkupContainer;
import wicket.markup.html.basic.Label;
import wicket.markup.html.basic.MultiLineLabel;
import wicket.markup.html.link.ExternalLink;
import wicket.markup.html.panel.Panel;
import wicket.model.CompoundPropertyModel;
import wicket.model.IModel;
import wicket.model.Model;
import wicket.util.string.Strings;

public class DescriptionPanel extends Panel{

	private String HTMLID;
	private String description;
	private MultiLineLabel desc;
	private Label date;
	private FullStoryLink link;
	private Label title;
	
	public DescriptionPanel(String id, IModel model) {
		super(id, model);
		String componentpath = removeColon(getPath());

		this.HTMLID = "p_" + getId() + "_" + componentpath;
		add(new AttributeModifier("id",true,new Model(this.HTMLID)));
		update(model);
		
	}

	public void update(IModel model)
	{
		
		if(desc == null)
		{
			String description = ((DescriptionModel)getModelObject()).getDescription();		
			desc = new MultiLineLabel("desc", new Model(description));
			desc.setEscapeModelStrings(false);
			add(desc);
			
			title = new Label("desctitle","");
			add(title);
			date = new Label("descdate","");
			add(date);
			
			link = new FullStoryLink("fulllink", new Model("#"));
			
			add(link);
		} else {
			setModel(model);
			String description = ((DescriptionModel)getModelObject()).getDescription();
			String title = ((DescriptionModel)getModelObject()).getTitle();
			String dateString = ((DescriptionModel)getModelObject()).getDateString();
			String linkString = ((DescriptionModel)getModelObject()).getLink();
			
			
			
			this.desc.setModelObject(description);
			this.desc.modelChanged();
			
			this.date.setModelObject(dateString);
			this.date.modelChanged();
			
			this.title.setModelObject(title);
			this.title.modelChanged();
			
			this.link.setModel(new Model(linkString));
			
			this.link.modelChanged();
			
			
		}
	}
	
	public String removeColon(String s) {
		  StringTokenizer st = new StringTokenizer(s,":",false);
		  String t="";
		  while (st.hasMoreElements()) t += st.nextElement();
		  return t;
	  }
	public String getHTMLID()
	{
		return this.HTMLID;
	}
	
	private class FullStoryLink extends WebMarkupContainer
	{

		public FullStoryLink(String id, IModel model) {
			super(id);
			setModel(model);
		}
		protected void onComponentTag(ComponentTag tag)
		{
			if (getModelObject() != null)
			{
				tag.put("href", Strings.replaceAll((String)getModelObject(),"&", "&amp;"));
				tag.put("target", "_new");
			}
		}


		
	}
}
