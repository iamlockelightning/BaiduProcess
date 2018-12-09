package BaiduProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class HudongExtractor {

	public static String dataSourceDir_raw = "/home/peter/HudonBaikeDataProcess/hudongjson";
	public static String resultTargetDir = "/home/peter/HudongBaikeDataProcess/hudongResult";
//	public static String dataSourceDir_raw = "C:/Users/Locke/Desktop/test";
//	public static String resultTargetDir = "C:/Users/Locke/Desktop/test";
	
	private Set<String> KVSet;
	private Set<String> innerLinkSet;
	private Set<String> picturesSet;
	private Set<String> categorySet;
	
	public List<Integer> infoboxData;
	public List<Integer> outlineData;
	public List<Integer> innerLinkData;
	public List<Integer> picturesData;
	public List<Integer> categoryData;
	
	private static String getDefaultCharSet() {  
        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
        String enc = writer.getEncoding();
        return enc;
    }
	public static void main(String args[]) throws Exception {
//		-Dfile.encoding=UTF-8	
		System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("file.encoding=" + System.getProperty("file.encoding"));
        System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("Default Charset in Use=" + getDefaultCharSet());
        
		File sD = new File(dataSourceDir_raw);
		for (File file : sD.listFiles()) {
			System.out.println("Processing file: " + file.getName());
			HudongExtractor hE = new HudongExtractor();
			hE.processing(file.getName());
		}

//        HudongExtractor hE = new HudongExtractor();
//        hE.statistics();

		System.out.println("Done! :D");
	}
	
	public HudongExtractor() {
		KVSet = new HashSet<String>();
		innerLinkSet = new HashSet<String>();
		picturesSet = new HashSet<String>();
		categorySet = new HashSet<String>();
		
		infoboxData = new ArrayList<Integer>();
		outlineData = new ArrayList<Integer>();
		innerLinkData = new ArrayList<Integer>();
		picturesData = new ArrayList<Integer>();
		categoryData = new ArrayList<Integer>();
	}
	
	
	public void processing(String filename) throws Exception {		
		String sourceDir_raw = dataSourceDir_raw;
		String targetDir = resultTargetDir;

		File input_raw = new File(sourceDir_raw + "/" + filename);
		BufferedReader bufferedReader_raw = new BufferedReader(new FileReader(input_raw));
		File output = new File(targetDir + "/" + filename.replace(".", ".result."));
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(output));
		File outputProblem = new File(targetDir + "/" + filename.replace(".", ".problem."));
		BufferedWriter bufferedWriterProblem = new BufferedWriter(new FileWriter(outputProblem));
		File outputUrl = new File(targetDir + "/" + filename.replace(".", ".url."));
		BufferedWriter bufferedWriterUrl = new BufferedWriter(new FileWriter(outputUrl));
		
		String line = new String();
		List<String> list = new ArrayList<String>();
		int count = 0;

		while (true) {
			line = bufferedReader_raw.readLine();
//			System.out.println(line);
			if (line == null) {
				break;
			}
			if (line.equals("")) {
				count += 1;
				JSONObject temp = new JSONObject(StringUtils.join(list, ""));
				JSONObject page = new JSONObject();
				String rawHtml = temp.get("html").toString();
				Document doc = Jsoup.parse(rawHtml, "UTF-8");

				page.put("url", temp.getString("url").replaceAll("\t", ""));
				page.put("infobox", getInfobox(doc));
				page.put("outline", getOutline(doc));
				page.put("content_title", getContentTitle(doc));
				page.put("description", getDescription(doc));
				page.put("text_content", getTextContent(doc));
				page.put("inner_link", getInnerLink());
				page.put("external_link", getExternalLink(doc)); //
				page.put("pictures", getPictures(doc));
				page.put("related_term", getRelatedTerm(doc));
				page.put("wiki_hot", getWikiHot(doc));
				page.put("category", getCategory(doc));
				
				list.clear();
				
//				System.out.println(page);
//				if (count == 30) {
//					break;
//				}
				if (page.get("content_title").equals("")) {
					bufferedWriterProblem.write(temp.toString() + "\n");
					bufferedWriterUrl.write(temp.get("url").toString().replaceAll("\t", "") + "\n");
				} else {
					bufferedWriter.write(page.toString() + "\n");
				}
			} else {
				list.add(line);
			}
		}
		bufferedReader_raw.close();
		bufferedWriterProblem.close();
		bufferedWriterUrl.close();
		bufferedWriter.close();
		System.out.println("Total: " + count);
	}
	
	public void statistics() throws IOException {
		String targetDir = resultTargetDir;
		
//		File output = new File(targetDir+"/allNewBaike.result.json");
//		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(output));

		File outputCategory = new File("./allNonRepetitiveCategory.txt");
		BufferedWriter bufferedWriterCategory = new BufferedWriter(new FileWriter(outputCategory));
		File outputData = new File("./allCountData.txt");
		BufferedWriter bufferedWriterData = new BufferedWriter(new FileWriter(outputData));
		
		File sD = new File(targetDir);
		int pagesCount = 0, nonEmptyPagesCount = 0,
			pagesHaveInfobox = 0, nonRepetitiveKVCount = 0, KVCount = 0, KVwithinLinkCount = 0,
			pagesHaveOutline = 0, firstLevelCount = 0, secondLevelCount = 0,
			pagesHaveInnerLink = 0, innerLinkCount = 0,
			pagesHaveTextContent = 0,
			pagesHaveDescription = 0,
			pagesHavePictures = 0, picturesCount = 0,
			pagesHaveCategory = 0, categoryCount = 0;
		
		for (File file : sD.listFiles()) {
			System.out.println(file.getName());
			File input = new File(file.getAbsolutePath());
			BufferedReader bufferedReader = new BufferedReader(new FileReader(input));
			String line = new String();
			while (true) {
				line = bufferedReader.readLine();
//				System.out.println(line);
				if (line == null || line.equals("")) {
					break;
				}
				JSONObject page = new JSONObject(line);
//				System.out.println(page);
				pagesCount += 1;
				if (page.getString("content_title").equals("") == false) {
					nonEmptyPagesCount += 1;
					JSONObject infobox = page.getJSONObject("infobox");
					
					if (infobox.toString().equals("{}") == false) {
						pagesHaveInfobox += 1;
						for (String k : infobox.keySet()) {
							KVCount += 1;
							if (infobox.get(k).toString().indexOf("||") != -1) {
								KVwithinLinkCount += 1;
							}
							if (KVSet.contains(k) == false) {
								KVSet.add(k);
								nonRepetitiveKVCount += 1;
							}
						}
					}
					
					JSONObject outline = page.getJSONObject("outline");
					if (outline.toString().equals("{}") == false) {
						pagesHaveOutline += 1;
						for (String k : outline.keySet()) {
							firstLevelCount += 1;
							if (outline.getJSONObject(k).toString().equals("") == false) {
								secondLevelCount += outline.getJSONObject(k).length();
							}
						}
					}
					
					infoboxData.add(infobox.length());
					outlineData.add(outline.length());
					innerLinkData.add(page.getJSONArray("inner_link").length());
					picturesData.add(page.getJSONArray("pictures").length());
					categoryData.add(page.getJSONArray("category").length());
					
//					bufferedWriter.write(page.toString() + "\n");
					
					if (page.getJSONArray("inner_link").length() != 0) {
						pagesHaveInnerLink += 1;
						for (int i = 0; i < page.getJSONArray("inner_link").length(); i += 1) {
							innerLinkCount += 1;
							innerLinkSet.add(page.getJSONArray("inner_link").getString(i));
						}
					}
					if (page.getJSONArray("pictures").length() != 0) {
						pagesHavePictures += 1;
						for (int i = 0; i < page.getJSONArray("pictures").length(); i += 1) {
							picturesCount += 1;
							picturesSet.add(page.getJSONArray("pictures").getString(i));
						}
					}
					if (page.getJSONArray("category").length() != 0) {
						pagesHaveCategory += 1;
						for (int i = 0; i < page.getJSONArray("category").length(); i += 1) {
							categoryCount += 1;
							String category = page.getJSONArray("category").getString(i);
							if (category.indexOf("[[") != -1) {
								categorySet.add(category.split("\\|\\|")[0].split("\\[\\[")[1]);
							} else {
								categorySet.add(category);
							}
						}
					}
					if (page.getString("text_content").equals("") == false) {
						pagesHaveTextContent += 1;
					}
					if (page.getString("description").equals("") == false) {
						pagesHaveDescription += 1;
					}
					
				}
			}
			bufferedReader.close();
		}
//		bufferedWriter.close();

		System.out.println("-------------------------------");
		System.out.println("pagesCount: " + pagesCount);
		System.out.println("nonEmptyPagesCount: " + nonEmptyPagesCount);
		System.out.println("pagesHaveInfobox: " + pagesHaveInfobox);
		System.out.println("nonRepetitiveKVCount: " + nonRepetitiveKVCount);
		System.out.println("KVCount: " + KVCount);
		System.out.println("KVwithinLinkCount: " + KVwithinLinkCount);
		System.out.println("pagesHaveOutline: " + pagesHaveOutline);
		System.out.println("firstLevelCount: " + firstLevelCount);
		System.out.println("secondLevelCount: " + secondLevelCount);
		
		System.out.println("pagesHaveInnerLink: " + pagesHaveInnerLink);
		System.out.println("nonRepetitiveInnerLinkCount: " + innerLinkSet.size());
		System.out.println("innerLinkCount: " + innerLinkCount);
		System.out.println("pagesHaveTextContent: " + pagesHaveTextContent);
		System.out.println("pagesHaveDescription: " + pagesHaveDescription);
		System.out.println("pagesHavePictures: " + pagesHavePictures);
		System.out.println("nonRepetitivePicturesCount: " + picturesSet.size());
		System.out.println("picturesCount: " + picturesCount);
		System.out.println("pagesHaveCategory: " + pagesHaveCategory);
		System.out.println("nonRepetitiveCategoryCount: " + categorySet.size());
		System.out.println("categoryCount: " + categoryCount);
		
		bufferedWriterCategory.write(categorySet.toString() + "\n");
		bufferedWriterData.write(infoboxData.toString() + "\n");
		bufferedWriterData.write(outlineData.toString() + "\n");
		bufferedWriterData.write(innerLinkData.toString() + "\n");
		bufferedWriterData.write(picturesData.toString() + "\n");
		bufferedWriterData.write(categoryData.toString() + "\n");

		bufferedWriterCategory.close();
		bufferedWriterData.close();
		
	}
	
	private String clearString(String str) {
		str = str.replaceAll("\u00A0", "").replaceAll("\n", "").replaceAll("&nbsp;", " ").replaceAll("&nbsp", " ").trim();
		str = str.replaceAll("\\<.*?>", ""); 
		return str;
	}
	
	private String handleTextWithinHref(Element element) {
		List<String> slist = new ArrayList<String>();
		for (Node node : element.childNodes()) {
			if (node.nodeName().equals("#text")) {
				slist.add(clearString(node.toString()));
			} else if (node.nodeName().equals("a") && node.childNodeSize()!=0) {
				if (node.childNode(0).nodeName().equals("#text")) {
					if (node.attr("class").equals("innerlink")) {
						String temV = "[[" + clearString(node.childNode(0).toString()) + "||" + node.attr("href") + "]]";
						slist.add(temV);
						innerLinkSet.add(temV);
					} else if (node.attr("class").equals("link_red")) {
						slist.add(clearString(node.childNode(0).toString()));
					} else if (node.hasAttr("class")==false && node.hasAttr("title")==false) {
						String temVal = "[[" + clearString(node.childNode(0).toString()) + "||" + node.attr("href") + "]]";
						slist.add(temVal);
						innerLinkSet.add(temVal);
					}
				}
			} else if (node.nodeName().equals("span")) {
				slist.add(clearString(((Element)node).text()));
			} else if (node.nodeName().equals("b")) {
				slist.add(clearString(((Element)node).text()));				
			}
		}
		return StringUtil.join(slist, "");
	}
	
	private JSONArray getCategory(Document doc) {
		JSONArray category = new JSONArray();
		
		try {
			Elements valBlocks = doc.select("dd[class=h27]");

			if (valBlocks.size() > 0) {
				Elements cates = valBlocks.get(0).getElementsByTag("a");
				for (Element ca : cates) {
					if (ca.text().equals("")==false) {
						if (ca.attr("href").equals("/fenlei/")==true) {
							category.put(clearString(ca.text()));
						} else {
							category.put("[[" + clearString(ca.text()) + "||" + ca.attr("href") + "]]");						
						}
					}
				}
			}

			return category;
		} catch (Exception e) {
			return category;
		}
	}
	private JSONObject getWikiHot(Document doc) {
		JSONObject wiki_hot = new JSONObject();
		
		try {
			Elements valBlocks = doc.select("div[class=rightdiv cooperation cooperation_t]");

			if (valBlocks.size() > 0) {
				Elements lis = valBlocks.get(0).getElementsByTag("li");
				wiki_hot.put("编辑次数", lis.get(1).child(0).text().substring(0, lis.get(1).child(0).text().length()-1));
				wiki_hot.put("参与编辑人数", lis.get(2).child(0).text());
				wiki_hot.put("最近更新时间", lis.get(3).text().substring(7));
			}

			return wiki_hot;
		} catch (Exception e) {
			return wiki_hot;
		}
	}
	private JSONArray getRelatedTerm(Document doc) {
		JSONArray related_term = new JSONArray();
		
		try {
			Elements valBlocks = doc.select("div[id=xgct]");

			if (valBlocks.size() > 0) {
				Elements as = valBlocks.get(0).getElementsByTag("a");
				
				for (Element a : as) {
					if (a.hasAttr("title")) {
						related_term.put(clearString(a.attr("title")));
					}
				}
			}

			return related_term;
		} catch (Exception e) {
			return related_term;
		}		
	}
	private JSONArray getPictures(Document doc) {
		JSONArray pictures = new JSONArray();

		try {
			Elements valBlocks = doc.select("div[class=doc-img]");
			if (valBlocks.size() > 0) {
				Element firstPicture = valBlocks.get(0).getElementsByTag("img").get(0);
				if (firstPicture.attr("src").equals("")==false) {
					pictures.put("[[" + clearString(firstPicture.attr("alt")) + "||" + firstPicture.attr("src") + "]]");
				}
			}

			valBlocks = doc.select("div[id=content]");
			if (valBlocks.size() > 0) {
				Elements imgs = valBlocks.get(0).getElementsByTag("img");
				for (Element img : imgs) {
					if (img.attr("data-original").equals("")==false) {
						pictures.put("[[" + clearString(img.attr("alt")) + "||" + img.attr("data-original") + "]]");	
					}
				}
			}
			
			return pictures;
		} catch (Exception e) {
			return pictures;
		}
	}
	private JSONArray getExternalLink(Document doc) {
		// TODO Auto-generated method stub
		return null;
	}
	private JSONArray getInnerLink() {
		JSONArray inner_link = new JSONArray();
		
		for (String s : innerLinkSet) {
			inner_link.put(s);
		}
		innerLinkSet.clear();
		
		return inner_link;
	}
	private String getTextContent(Document doc) {
		String text_content = new String();
		
		try {
			Elements valBlocks = doc.select("div[id=content]");
			
			if (valBlocks.size() > 0) {
				List<String> plist = new ArrayList<String>();
				List<String> sslist = new ArrayList<String>();
				
				for (Node para : valBlocks.get(0).childNodes()) {
					if (para.nodeName().equals("p")) {
						if (sslist.isEmpty()==false) {
							String tem = StringUtil.join(sslist, "");
							if (tem.equals("")==false) {
								plist.add(tem);
							}
							sslist.clear();
						}
						plist.add(handleTextWithinHref((Element)para));
					} else if (para.nodeName().equals("div") && para.attr("class").contains("content_h2")) {
						if (sslist.isEmpty()==false) {
							String tem = StringUtil.join(sslist, "");
							if (tem.equals("")==false) {
								plist.add(tem);
							}
						}
						plist.add("~~~~~" + ((Element)para).text().split("/")[0]);
					} else if (para.nodeName().equals("h3")) {
						if (sslist.isEmpty()==false) {
							String tem = StringUtil.join(sslist, "");
							if (tem.equals("")==false) {
								plist.add(tem);
							}
						}
						plist.add("~~~~~" + ((Element)para).text().toString());
					} else if (para.nodeName().equals("#text")) {
						sslist.add(clearString(para.toString()));
					} else if (para.nodeName().equals("a")) {
						if (para.attr("class").equals("innerlink")) {
							String temVal = "[[" + clearString(para.childNode(0).toString()) + "||" + para.attr("href") + "]]";
							sslist.add(temVal);
							innerLinkSet.add(temVal);
						} else if (para.attr("class").equals("link_red")) {
							sslist.add(clearString(para.childNode(0).toString()));
						}
					} else if (para.nodeName().equals("b")) {
						sslist.add(((Element)para).text());
					}
				}
				text_content = StringUtil.join(plist, "*****").replaceAll("\\*\\*\\*\\*\\*[.^\\*]*", "*****");
			}
			
			return text_content;
		} catch (Exception e) {
			return text_content;
		}
	}
	private String getDescription(Document doc) {
		String description = new String();
		
		try {
			Elements valBlocks = doc.select("div[class=summary]");
			
			if (valBlocks.size() > 0) {
				Elements ps = valBlocks.get(0).getElementsByTag("p");
				List<String> slist = new ArrayList<String>();
				
				for (Element p : ps) {					
					slist.add((handleTextWithinHref(p)));
				}
				description = StringUtils.join(slist, "");
			}
			
			return description;
		} catch (Exception e) {
			return description;
		}
	}
	private String getContentTitle(Document doc) {
		String content_title = new String();
		
		try {
			Elements valBlocks = doc.select("div[class=content-h1]");
			
			if (valBlocks.size() > 0) {
				content_title = valBlocks.get(0).getElementsByTag("h1").get(0).text();
			}
			
			return content_title;
		} catch (Exception e) {
			return content_title;
		}
	}
	private JSONObject getOutline(Document doc) {
		JSONObject outline = new JSONObject();
		
		try {
			Elements valBlocks = doc.select("fieldset[id=catalog]");
			
			if (valBlocks.size() > 0) {
				Elements lis = valBlocks.get(0).getElementsByTag("li");
				String lastStr = new String();
				int cnt = 0;
				
				for (Element li : lis) {
					if (li.getElementsByTag("em").size() != 0) {
						lastStr = clearString(li.child(0).text() + "~" + li.child(1).text());
						outline.put(lastStr, new JSONObject());
						cnt = 1;
					} else {
						outline.getJSONObject(lastStr).put(cnt++ + "~" + clearString(li.child(0).text()), new JSONObject());
					}
				}
			}
			
			return outline;
		} catch (Exception e) {
			return outline;
		}
	}
	private JSONObject getInfobox(Document doc) {
		JSONObject infobox = new JSONObject();
		
		try {
			Elements valBlocks = doc.select("div[id=datamodule]");
			
			if (valBlocks.size() > 0) {
				for (Element td : valBlocks.get(0).getElementsByTag("td")) {
					
					if (td.hasAttr("class")==false) {
						Element strong = td.select("strong").get(0);
						Elements spans = td.getElementsByTag("span");
						List<String> slist = new ArrayList<String>();
						
						for (Element span : spans) {
							if (span.getElementsByTag("a").isEmpty()) {
								slist.add(clearString(span.text()));
							} else {
								slist.add(handleTextWithinHref(span));
							}
						}
						
						infobox.put(clearString(strong.text()), StringUtil.join(slist, ""));
					}
				}
			}
			
			return infobox;
		} catch (Exception e) {
			return infobox;
		}
	}
	
}
