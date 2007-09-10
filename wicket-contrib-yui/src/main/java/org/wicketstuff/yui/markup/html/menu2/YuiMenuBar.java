package org.wicketstuff.yui.markup.html.menu2;

import java.util.ArrayList;
import java.util.Map;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.IBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.collections.MiniMap;
import org.apache.wicket.util.string.Strings;
import org.apache.wicket.velocity.VelocityHeaderContributor;
import org.apache.wicket.velocity.VelocityJavascriptContributor;
import org.wicketstuff.yui.YuiHeaderContributor;

public class YuiMenuBar extends Panel {
	
	public static final String MENU_BAR_ID = "menuBar";
	public static final String MENU_ITEMS_ID = "menuItems";

	private ListView list;

	public YuiMenuBar(String wicketId, final String elementId) {
		super(wicketId);
		setRenderBodyOnly(true);
		
		add(YuiHeaderContributor.forModule("menu", null, false, "2.3.0"));

		
		WebMarkupContainer mg = new WebMarkupContainer(MENU_BAR_ID) {
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);

				if (!Strings.isEmpty(elementId)) {
					tag.put("id", elementId);
				}
				tag.put("class", "yuimenubar yuimenubarnav");
			}
		};
		add(mg);
		
		list  = new ListView(MENU_ITEMS_ID, new ArrayList<YuiMenuBarItem>()) {

			@Override
			protected void populateItem(ListItem item) {
				item.setRenderBodyOnly(true);
				YuiMenuBarItem mi = (YuiMenuBarItem) item
						.getModelObject();

                if(0 == item.getIndex()) {
                    mi.addFirstOfType();
                }
				mi.setRenderBodyOnly(true);
				item.add(mi);

			}
		}.setReuseItems(true);
		mg.add( list );
		add(getMenuInit( elementId ));
	}

	
	public YuiMenuBarItem addMenu( String label ) {
		YuiMenuBarItem item = new YuiMenuBarItem( label );
		add( item );
		return item;
	}
	
	public YuiMenuBarItem addMenu( IAction action ) {
		YuiMenuBarItem item = new YuiMenuBarItem( action );
		add( item );
		return item;
	}
	
	private void add( YuiMenuBarItem menuItem ) {
		ArrayList<YuiMenuBarItem> newList = new ArrayList<YuiMenuBarItem>();
		newList.addAll( list.getList( ) );
		newList.add( menuItem );
		list.setList(newList );
	}
	
	public AbstractYuiMenuItem getItem( int idx ) {
		ListItem item = (ListItem)list.getList().get(idx );
		return item == null ? null : (YuiMenuBarItem)item.getModelObject();
	}
	
	private IBehavior getMenuInit(String elementId) {
		final Map<String, String> vars = new MiniMap(1);
		vars.put("elementId", elementId);
		return new VelocityHeaderContributor()
				.add(new VelocityJavascriptContributor(YuiMenuBar.class,
						"res/menubarinit.vm", Model.valueOf(vars),
						elementId + "Script"));
	}



}