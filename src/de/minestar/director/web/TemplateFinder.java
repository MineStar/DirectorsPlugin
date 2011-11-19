package de.minestar.director.web;

import com.bukkit.gemo.BukkitHTTP.HTTPEvent;
import com.bukkit.gemo.BukkitHTTP.Page;

import de.minestar.director.web.templates.*;

public class TemplateFinder
{
	public static Page findTemplate(Page page, HTTPEvent event)
	{
		if(event.fileName.equalsIgnoreCase("ERROR404.html"))
		{
			tplError404.execTemplate(page, event);
		}
		else if(event.fileName.equalsIgnoreCase("index.html"))
		{
			tplIndex.execTemplate(page, event);			
		}		
		
		return page;
	}	
}
