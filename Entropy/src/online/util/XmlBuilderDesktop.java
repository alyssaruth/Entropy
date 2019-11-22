package online.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import util.XmlConstants;
import util.XmlUtil;

/**
 * Methods for building XML messages that are only sent by the desktop application
 */
public class XmlBuilderDesktop implements XmlConstants
{
	public static Document factoryAchievementsUpdate(String username, String achievementName, int achievementCount)
	{
		Document document = XmlUtil.factoryNewDocument();
		Element rootElement = document.createElement(ROOT_TAG_ACHIEVEMENTS_UPDATE);
		rootElement.setAttribute("Username", username);
		rootElement.setAttribute("AchievementCount", "" + achievementCount);
		if (achievementName != null)
		{
			rootElement.setAttribute("AchievementName", achievementName);
		}
		
		document.appendChild(rootElement);
		return document;
	}
}
