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
		//TemplateFinder.findTemplate(page, event);
	}
	
	///////////////////////
	//
	// HANDLE POST REQUEST
	//
	///////////////////////		
	@Override
	public void handlePostRequest(Page page, HTTPEvent event) 
	{
		//TemplateFinder.findTemplate(page, event);
	}

	///////////////////////
	//
	// HANDLE 404 PAGE
	//
	///////////////////////	
	@Override
	public void handle404Page(Page page, HTTPEvent event)
	{
		//TemplateFinder.findTemplate(page, event);
	}
	
	///////////////////////
	//
	// LOGIN SUCCESSFUL
	//
	///////////////////////	
	@Override
	public String loginSuccessful(HTTPEvent event)
	{
	    /*
		try
		{
			HashMap<String, String> param = event.postParameter;
			if(param == null)
				return null;	
			if(param.size() < 3)
				return null;	
			
			String pw = getUserPassword(param.get("username"));		
			if(pw.split(":")[0] == null || pw.split(":")[1] == null)
				return null;		
					
			if(!Crypt.SHA1(pw.split(":")[1] + param.get("password")).contentEquals(pw.split(":")[0]))
				return null;
			
			String username = CoKCore.getIngameNick(param.get("username"));
			if(username == null)
				return null;	
			
			return "Username=" + username;
		}
		catch(Exception e)
		{
			return null;
		}
		*/
	    return "";
	}	
	
	///////////////////////
	//
	// LOGIN FAILED
	//
	///////////////////////	
	@Override
	public void handleWrongLogin(Page page, HTTPEvent event)
	{
	    /*
		StringObject gameList = new StringObject();		
		TemplatePage.appendGameList(gameList);		
		page.replaceText("%LISTOFGAMES%", gameList.str);
		page.replaceText("%MESSAGE%", "<font color=\"red\"><b>Login failed!</b></font><br /><br />");
	    */
	}
}
