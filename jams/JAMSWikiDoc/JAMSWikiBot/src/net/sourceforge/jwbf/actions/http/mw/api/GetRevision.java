/*
 * Copyright 2007 Thomas Stock.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * Contributors:
 * 
 */
package net.sourceforge.jwbf.actions.http.mw.api;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;

import net.sourceforge.jwbf.actions.http.ProcessException;
import net.sourceforge.jwbf.actions.http.mw.MWAction;
import net.sourceforge.jwbf.bots.MediaWikiBot;
import net.sourceforge.jwbf.contentRep.mw.ContentAccessable;
import net.sourceforge.jwbf.contentRep.mw.SimpleArticle;

import org.apache.commons.httpclient.methods.GetMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;
import org.apache.log4j.Logger;

/**
 * @author Thomas Stock
 * @since MediaWiki 1.9.0
 *
 */
public class GetRevision extends MWAction {
	
	private final SimpleArticle sa;
	public static final int CONTENT = 1 << 1;
	public static final int TIMESTAMP = 1 << 2;
	public static final int USER = 1 << 3;
	public static final int COMMENT = 1 << 4;
	public static final int FIRST = 1 << 5;
	public static final int LAST = 1 << 6;

	private static final Logger LOG = Logger.getLogger(GetRevision.class);
	/**
	 * TODO follow redirects.
	 */
	public GetRevision(final String articlename, final int property) {
		sa = new SimpleArticle();
		sa.setLabel(articlename);
		String uS = "";
		try {
			uS = "/api.php?action=query&prop=revisions&titles="
					+ URLEncoder.encode(articlename, MediaWikiBot.CHARSET)
                                        + "&rvprop=content"
					/*+ "&rvprop=" + getDataProperties(property)*/
					+ getReversion(property)
					+ "&rvlimit=1"
					+ "&format=xml";
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
                LOG.debug(uS);
		msgs.add(new GetMethod(uS));
		
	}
	/**
	 * @param s
	 *            the returning text
	 * @return empty string
	 * 
	 */
	public String processAllReturningText(final String s) throws ProcessException {

		parse(s);
		return "";
	}
	
	private String getDataProperties(final int property) {
		String properties = "";
		
		if ((property & CONTENT) > 0) {
			properties += "content|";
		}
		if ((property & COMMENT) > 0) {
			properties += "comment|";
		}
		if ((property & TIMESTAMP) > 0) {
			properties += "timestamp|";
		}
		if ((property & USER) > 0) {
			properties += "user|";
		}
		return properties.substring(0, properties.length() - 1);
	}
	
	
	private String getReversion(final int property) {
		String properties = "&rvdir=";
		
		if ((property & FIRST) > 0) {
			properties += "newer";
		} else {
			properties += "older";
		}
		
		return properties;
	}
	
	
	private void parse(final String xml) throws ApiException{
		SAXBuilder builder = new SAXBuilder();
		Element root = null;
		try {
			Reader i = new StringReader(xml);
			Document doc = builder.build(new InputSource(i));
			
			root = doc.getRootElement();

		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		findContent(root);
	}

	public ContentAccessable getArticle() {
		
		
		return sa;
	}
	
	@SuppressWarnings("unchecked")
	private void findContent(final Element root) throws ApiException {
		
		
		
		Iterator<Element> el = root.getChildren().iterator();
		while (el.hasNext()) {
			Element element = (Element) el.next();
			if (element.getQualifiedName().equalsIgnoreCase("error")) {
				throw new ApiException(element.getAttributeValue("code"),
						element.getAttributeValue("info"));
			} else if (element.getQualifiedName().equalsIgnoreCase("rev")) {
				sa.setText(encodeUtf8(element.getText()));
				sa.setEditSummary(element.getAttributeValue("comment"));
				sa.setEditor(encodeUtf8(element.getAttributeValue("user")));
			} else {
				findContent(element);
			}
			
		}
		
		
	}

}
