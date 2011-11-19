package de.minestar.director.web.templates;

import com.bukkit.gemo.BukkitHTTP.HTTPEvent;
import com.bukkit.gemo.BukkitHTTP.Page;

public class tplIndex extends TemplatePage
{
	public static void execTemplate(Page page, HTTPEvent event)
	{		
		String contentText 	= "THIS IS A PLACEHOLDER FOR A SIMPLE TEXT";
		page.replaceText("%TEXT%", contentText);
	}
}
