package util;

import org.w3c.dom.Document;

import java.util.prefs.Preferences;

public class RegistryUtil
{
	public static void clearNode(Preferences node)
	{
		try
		{
			node.clear();
		}
		catch (Throwable t)
		{
			Debug.stackTrace(t);
		}
	}
	
	public static Document getAttributeXml(Preferences node, String key)
	{
		String attribute = node.get(key, null);
		if (attribute == null) {
			return null;
		}
		
		return XmlUtil.getDocumentFromXmlString(attribute);
	}
	
	public static void setAttributeXml(Preferences node, String key, Document xmlDoc)
	{
		String xmlStr = XmlUtil.getStringFromDocument(xmlDoc);
		node.put(key, xmlStr);
	}
}
