package de.minestar.director.web;

import com.bukkit.gemo.BukkitHTTP.HTTPEvent;
import com.bukkit.gemo.BukkitHTTP.Page;
import com.bukkit.gemo.BukkitHTTP.HTTPPlugin;

public class DirectorHTTP extends HTTPPlugin
{
	///////////////////////
	//
	// CONSTRUCTOR
	//
	///////////////////////	
	public DirectorHTTP(String rootAlias, String pluginName, String root, boolean useAuth) 
	{
		super(rootAlias, pluginName, root, useAuth);		
	}

	///////////////////////
	//
	// HANDLE GET REQUEST
	//
	///////////////////////		
	@Override
	public void handleGetRequest(Page page, HTTPEvent event)
	{
		TemplateFinder.findTemplate(page, event);
	}
	
	///////////////////////
	//
	// HANDLE POST REQUEST
	//
	///////////////////////		
	@Override
	public void handlePostRequest(Page page, HTTPEvent event) 
	{
		TemplateFinder.findTemplate(page, event);
	}

	///////////////////////
	//
	// HANDLE 404 PAGE
	//
	///////////////////////	
	@Override
	public void handle404Page(Page page, HTTPEvent event)
	{
		TemplateFinder.findTemplate(page, event);
	}
}
