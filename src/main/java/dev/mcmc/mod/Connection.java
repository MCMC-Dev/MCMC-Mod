package dev.mcmc.mod;

import com.google.gson.JsonElement;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LatvianModder
 */
public class Connection
{
	public static Connection create(String url)
	{
		Connection connection = new Connection(url);
		connection.header("Accept-Language", "en-US,en;q=0.5");
		connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36");
		return connection;
	}

	public static Connection createAPI(String path)
	{
		Connection connection = new Connection("https://mcmc.dev/api/" + path);
		connection.header("MCMC-API", MCMCMod.API_VERSION);
		connection.header("User-Agent", "MCMC-Mod");

		String t = MCMCMod.getToken();

		if (!t.isEmpty() && !t.equals("-"))
		{
			connection.header("Authorization", "Bearer " + t);
		}

		return connection;
	}

	public static class Response
	{
		public final HttpURLConnection connection;
		public final int code;
		public final List<String> body;

		private Response(@Nullable HttpURLConnection c) throws IOException
		{
			connection = c;
			code = connection == null ? 0 : connection.getResponseCode();
			body = new ArrayList<>();
		}

		public boolean isOK()
		{
			return code / 100 == 2;
		}

		public boolean hasBody()
		{
			return !body.isEmpty();
		}

		public String header(String name)
		{
			String s = connection == null ? null : connection.getHeaderField(name);
			return s == null ? "" : s;
		}

		public String getString()
		{
			return String.join("\n", body);
		}

		public <T> T getJson(Class<T> c)
		{
			return Utils.GSON.fromJson(String.join("", body), c);
		}
	}

	public final String address;
	public Proxy proxy;
	public String method;
	public byte[] data;
	public final Map<String, String> headers;

	private Connection(String u)
	{
		address = u;
		proxy = Proxy.NO_PROXY;
		method = "POST";
		data = null;
		headers = new HashMap<>();
	}

	public Connection proxy(Proxy p)
	{
		proxy = p;
		return this;
	}

	public Connection method(String m)
	{
		method = m;
		return this;
	}

	public Connection getMethod()
	{
		return method("GET");
	}

	public Connection data(byte[] d)
	{
		data = d;
		return this;
	}

	public Connection data(String string)
	{
		return data(string.getBytes(StandardCharsets.UTF_8)).header("Content-Type", "text/plain");
	}

	public Connection data(JsonElement json)
	{
		return data(Utils.GSON.toJson(json)).header("Content-Type", "application/json");
	}

	public Connection data(Map<String, Object> urlData)
	{
		StringBuilder builder = new StringBuilder();
		boolean first = true;

		for (Map.Entry<String, Object> entry : urlData.entrySet())
		{
			if (first)
			{
				first = false;
			}
			else
			{
				builder.append('&');
			}

			try
			{
				builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				builder.append('=');
				builder.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		return data(builder.toString()).header("Content-Type", "application/x-www-form-urlencoded");
	}

	public Connection header(String name, Object value)
	{
		headers.put(name, String.valueOf(value));
		return this;
	}

	public Connection contentType(String type)
	{
		return header("Content-Type", type);
	}

	public Response connect() throws IOException
	{
		URL url = new URL(address);
		MCMCMod.LOGGER.debug("Connecting to " + address);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection(proxy);
		connection.setRequestMethod(method);

		for (Map.Entry<String, String> entry : headers.entrySet())
		{
			connection.setRequestProperty(entry.getKey(), entry.getValue());
		}

		connection.setConnectTimeout(3000);
		connection.setReadTimeout(5000);
		connection.setDoInput(true);
		connection.setDoOutput(data != null);

		if (data != null && data.length > 0)
		{
			try (OutputStream out = connection.getOutputStream())
			{
				out.write(data);
			}
			catch (Exception ex)
			{
				MCMCMod.LOGGER.error("Failed to connect to " + address + ": 0/" + ex);
				connection.disconnect();
				return new Response(null);
			}
		}

		Response response = new Response(connection);

		if (connection.getErrorStream() != null)
		{
			String s;

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream())))
			{
				s = reader.readLine();
			}

			MCMCMod.LOGGER.error("Failed to connect to " + address + ": " + response.code + "/" + s);
		}
		else if (response.isOK())
		{
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))
			{
				String line;

				while ((line = reader.readLine()) != null)
				{
					response.body.add(line);
				}
			}
			catch (Exception ex)
			{
				MCMCMod.LOGGER.error("Failed to connect to " + address + ": " + response.code + "/" + ex);
			}
		}
		else
		{
			MCMCMod.LOGGER.error("Failed to connect to " + address + ": " + response.code + "/null");
		}

		connection.disconnect();
		return response;
	}

	@Nullable
	public <T> T getJson(Class<T> c)
	{
		try
		{
			Response response = connect();

			if (response.isOK() && response.code != 204 && response.hasBody())
			{
				return response.getJson(c);
			}
		}
		catch (Exception ex)
		{
		}

		return null;
	}
}