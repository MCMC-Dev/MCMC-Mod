package dev.mcmc.mod.data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class ClientSession
{
	public long ticks;
	public long frames;
	public long creativeTicks;
	public boolean multiplayer;
	public boolean lan;
	public boolean hardcore;

	public Map<String, Object> getData()
	{
		Map<String, Object> data = new LinkedHashMap<>();
		data.put("ticks", ticks);
		data.put("frames", frames);

		if (creativeTicks > 0L)
		{
			data.put("creative_ticks", creativeTicks);
		}

		if (multiplayer)
		{
			data.put("multiplayer", 1);
		}

		if (lan)
		{
			data.put("lan", 1);
		}

		if (hardcore)
		{
			data.put("hardcore", 1);
		}

		return data;
	}
}