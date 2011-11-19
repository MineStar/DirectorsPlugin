package de.minestar.director.web.templates;

import com.bukkit.gemo.BukkitHTTP.HTTPEvent;
import com.bukkit.gemo.BukkitHTTP.Page;

public class tplError404 extends TemplatePage
{
	public static void execTemplate(Page page, HTTPEvent event)
	{		
		String contentText 	= "ERROR 404 - Die Datei wurde nicht gefunden!";
		page.replaceText("%ERRORTEXT%", contentText);
	}
}
