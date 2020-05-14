package dev.mcmc.mod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author LatvianModder
 */
public class Utils
{
	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
}