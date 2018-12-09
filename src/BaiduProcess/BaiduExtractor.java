package BaiduProcess;

import java.util.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.python.antlr.PythonParser.and_expr_return;
import org.python.antlr.PythonParser.else_clause_return;
import org.apache.commons.lang3.StringUtils;
import org.json.*;

public class BaiduExtractor {
    public Map<String, Map<String, String>> infoboxTemplate;
    public Map<String, Map<String, String>> outlineTemplate;
    public Map<String, Map<String, String>> titleTemplate;
    public Map<String, Map<String, String>> descriptionTemplate;
    public Map<String, Map<String, String>> imagesTemplate;
    public Map<String, Map<String, String>> referencesTemplate;
    public Map<String, Map<String, String>> tagsTemplate;

    public Set<String> imagesSet;
    public Set<String> referencesSet;
    public Set<String> tagsSet;
    public Set<String> linksSet;
    public Set<String> kvSet;
    
    public static String sourceDir;
    public static String resultDir;
    
    public static String input_filename = "/home/xlore/BaiduBaike20180705/data_final_force_all.txt";
    public static String output_dir = "/home/xlore/BaiduBaike20180705/1_extraction/";
//    public static String input_filename = "/Users/locke/Desktop/a.txt";
//    public static String output_dir = "/Users/locke/Desktop/";
    
    
//    public static String sourceDir = "/home/peter/BaiduBaikeDataProcess/extraData/"; // extraData
//    public static String sourceDir = "/mnt/server66/lockeData/";
//    public static String resultDir = "/home/peter/BaiduBaikeDataProcess/result/";
//    public static String sourceDir = "C:/Users/Locke/Desktop/test/";
//    public static String resultDir = "C:/Users/Locke/Desktop/result/";
    
    private static String getDefaultCharSet() {
        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());  
        String enc = writer.getEncoding();  
        return enc;
    }

    public static void main(String args[]) throws Exception {
        System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("file.encoding=" + System.getProperty("file.encoding"));
        System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("Default Charset in Use=" + getDefaultCharSet()); // -Dfile.encoding=UTF-8

//      sourceDir = System.getProperty("inputDir"); // -DinputDir="/mnt/server66/lockeData/"
//		resultDir = System.getProperty("outputDir"); // -DoutputDir="/home/peter/BaiduBaikeDataProcess/result/"
		
		BaiduExtractor bE = new BaiduExtractor();
//		bE.newProcessing(input_filename, output_dir);
		bE.newProcessingDataFinalForce(input_filename, output_dir);
// 		bE.new3Table(input_filename, output_dir);
		
		// 20180709以前能跑的版本
//		File input = new File(sourceDir);
//		if (input.exists()==false) {
//			System.out.println("FAULT: inputDir has no source files.");
//			return;
//		}
//		File output = new File(resultDir);
//		if (output.exists()==false) {
//			System.out.println("WARNING: outputDir doesn't exist. Having been created now.");
//			output.mkdir();
//		}
//		for (File file : input.listFiles()) {
//		    BaiduExtractor bE = new BaiduExtractor();
//		    bE.processing(file.getName());
//		}
//		        
//		if (args.length > 0) {
//		    for (String filename : args) {
//		        BaiduExtractor bE = new BaiduExtractor();
//		        bE.processing(filename);
//		    }
//		}
        System.out.println("Done! :D");
    }

    public BaiduExtractor() {
        infoboxTemplate = new HashMap<String, Map<String, String>>();
        outlineTemplate = new HashMap<String, Map<String, String>>();
        titleTemplate = new HashMap<String, Map<String, String>>();
        descriptionTemplate = new HashMap<String, Map<String, String>>();
        imagesTemplate = new HashMap<String, Map<String, String>>();
        referencesTemplate = new HashMap<String, Map<String, String>>();
        tagsTemplate = new HashMap<String, Map<String, String>>();

        imagesSet = new HashSet<String>();
        referencesSet = new HashSet<String>();
        tagsSet = new HashSet<String>();
        linksSet = new HashSet<String>();
        kvSet = new HashSet<String>();

        Map<String, String> ibTmpl_1 = new HashMap<String, String>();
        ibTmpl_1.put("keyType", "span");
        ibTmpl_1.put("keyClass", "biTitle");
        ibTmpl_1.put("valType", "div");
        ibTmpl_1.put("valClass", "biContent");
        infoboxTemplate.put("baseInfoWrap", ibTmpl_1);
        Map<String, String> ibTmpl_2 = new HashMap<String, String>();
        ibTmpl_2.put("keyType", "dt");
        ibTmpl_2.put("keyClass", "basicInfo-item name");
        ibTmpl_2.put("valType", "dd");
        ibTmpl_2.put("valClass", "basicInfo-item value");
        infoboxTemplate.put("basic-info cmn-clearfix", ibTmpl_2);
        Map<String, String> ibTmpl_3 = new HashMap<String, String>();
        ibTmpl_3.put("keyType", "dt");
        ibTmpl_3.put("keyClass", "basicInfo-item name");
        ibTmpl_3.put("valType", "dd");
        ibTmpl_3.put("valClass", "basicInfo-item value");
        infoboxTemplate.put("basic-info", ibTmpl_3);
        //
        Map<String, String> olTmpl_1 = new HashMap<String, String>();
        olTmpl_1.put("valType", "li");
        olTmpl_1.put("valClass", "level");
        outlineTemplate.put("lemma-catalog", olTmpl_1);
        Map<String, String> olTmpl_2 = new HashMap<String, String>();
        olTmpl_2.put("valType", "a");
        olTmpl_2.put("valClass", "true");
        outlineTemplate.put("z-catalog nslog-area log-set-param", olTmpl_2);
        //
        Map<String, String> tiTmpl_1 = new HashMap<String, String>();
        tiTmpl_1.put("valType", "span");
        tiTmpl_1.put("valClass", "lemmaTitleH1");
        titleTemplate.put("lemmaTitleH1_span", tiTmpl_1);
        Map<String, String> tiTmpl_2 = new HashMap<String, String>();
        tiTmpl_2.put("valType", "dd");
        tiTmpl_2.put("valClass", "lemmaWgt-lemmaTitle-title");
        titleTemplate.put("lemmaWgt-lemmaTitle-title", tiTmpl_2);
        Map<String, String> tiTmpl_3 = new HashMap<String, String>();
        tiTmpl_3.put("valType", "div");
        tiTmpl_3.put("valClass", "lemmaTitleBox clearfix");
        titleTemplate.put("lemmaTitleBox clearfix", tiTmpl_3);
        Map<String, String> tiTmpl_4 = new HashMap<String, String>();
        tiTmpl_4.put("valType", "div");
        tiTmpl_4.put("valClass", "lemmaTitleH1");
        titleTemplate.put("lemmaTitleH1_div", tiTmpl_4);
        //
        Map<String, String> deTmpl_1 = new HashMap<String, String>();
        deTmpl_1.put("valType", "div");
        deTmpl_1.put("valClass", "card-summary-content");
        descriptionTemplate.put("card-summary-content", deTmpl_1);
        Map<String, String> deTmpl_2 = new HashMap<String, String>();
        deTmpl_2.put("valType", "div");
        deTmpl_2.put("valClass", "lemma-summary");
        descriptionTemplate.put("lemma-summary", deTmpl_2);
        //
        Map<String, String> tgTmpl_1 = new HashMap<String, String>();
        tgTmpl_1.put("valType", "span");
        tgTmpl_1.put("valClass", "taglist");
        tagsTemplate.put("taglist_span", tgTmpl_1);
        Map<String, String> tgTmpl_2 = new HashMap<String, String>();
        tgTmpl_2.put("valType", "sapn");
        tgTmpl_2.put("valClass", "taglist");
        tagsTemplate.put("taglist_sapn", tgTmpl_2);
        //
        Map<String, String> reTmpl_1 = new HashMap<String, String>();
        reTmpl_1.put("valType", "li");
        reTmpl_1.put("valClass", "reference-item");
        referencesTemplate.put("reference-item", reTmpl_1);
        Map<String, String> reTmpl_2 = new HashMap<String, String>();
        reTmpl_2.put("valType", "p");
        reTmpl_2.put("valClass", "refUrl");
        referencesTemplate.put("refUrl", reTmpl_2);
        //
        Map<String, String> imTmpl_1 = new HashMap<String, String>();
        imTmpl_1.put("valType", "div");
        imTmpl_1.put("valClass", "main-content");
        imagesTemplate.put("main-content", imTmpl_1);
        Map<String, String> imTmpl_2 = new HashMap<String, String>();
        imTmpl_2.put("valType", "div");
        imTmpl_2.put("valClass", "u-page");
        imagesTemplate.put("u-page", imTmpl_2);
    }


 // 20180709以后[新]的版本
    public void new3Table(String input_filename, String output_dir) throws Exception {
    	System.out.println("Processing file: " + input_filename);
    	BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(output_dir + "bd_lite_entity_newid.txt")));
    	HashMap<String, String> url_newid_dict = new HashMap<String, String>();
    	String line = new String();
        while (true) {
            line = bufferedReader.readLine();
//            System.out.println(line);
            if (line == null) {
                break;
            }
            String[] ws = line.split("\t\t");
            if (url_newid_dict.containsKey(ws[2])) {
            	System.out.println("How to do...." + ws[2] + "\t" + url_newid_dict.get(ws[2]) + "\t" + ws[3] + "\n");
            } else {
            	url_newid_dict.put(ws[2], ws[3]);
            }
        }
        System.out.println("url_newid_dict.size(): " + url_newid_dict.size());
        bufferedReader.close();
        
    	BufferedReader bufferedReaderRaw = new BufferedReader(new FileReader(new File(input_filename)));
    	BufferedWriter bufferedWriter_syn = new BufferedWriter(new FileWriter(new File(output_dir + "bd_synonym_not_ready.txt")));
    	BufferedWriter bufferedWriter_pol = new BufferedWriter(new FileWriter(new File(output_dir + "bd_polyseme_not_ready.txt")));
    	
    	int count = 0;
    	line = new String();
        while (true) {
            line = bufferedReaderRaw.readLine();
//            System.out.println(line);
            if (line == null) {
                break;
            }
            
            JSONObject temp = new JSONObject(line);
            String rawHtml = temp.get("html").toString();
            JSONObject page = new JSONObject();
            Document doc = Jsoup.parse(rawHtml, "UTF-8");
            JSONObject titles = getTitle(doc, "");
			page.put("title", titles);
            page.put("url", temp.getString("url").replaceAll("\t", ""));
			page.put("synonym", getSynonym(doc));
			page.put("polyseme", getPolyseme(doc));

			count += 1;
//			System.out.println(page);
			
			if (count % 500000 == 0) {
				System.out.println("___" + count);
			}
			
			if (titles.has("h1")) {
				bufferedWriter_syn.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + url_newid_dict.get(page.getString("url")) + "\t\t" + page.getString("synonym") + "\n");
				bufferedWriter_pol.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + url_newid_dict.get(page.getString("url")) + "\t\t" + page.getString("polyseme") + "\n");
			} else {
//				System.out.println(page);
			}
        }
        bufferedReaderRaw.close();
        bufferedWriter_syn.close();
        bufferedWriter_pol.close();
    }
    
 // 20180709以后[新]的版本
    public void newProcessingDataFinalForce(String input_filename, String output_dir) throws Exception {
    	System.out.println("Processing file: " + input_filename);
        BufferedReader bufferedReaderRaw = new BufferedReader(new FileReader(new File(input_filename)));

        BufferedWriter bufferedWriter_force = new BufferedWriter(new FileWriter(new File(output_dir + "bd_force.txt")));
        HashSet<String> exist_set = new HashSet<String>(); 
        int count = 0;
        String line = new String();
        while (true) {
            line = bufferedReaderRaw.readLine();
//            System.out.println(line);
            if (line == null) {
                break;
            }
            
            JSONObject temp = new JSONObject(line);
            String rawHtml = temp.get("html").toString();
//            String rawHtml = line;
            
            JSONObject page = new JSONObject();
//          long s1 = System.nanoTime();
//            Document doc = Jsoup.parse(rawHtml, "GB18030");
            Document doc = Jsoup.parse(rawHtml, "UTF-8");
//          long s2 = System.nanoTime();
            page.put("url", temp.getString("url").replaceAll("\t", "").trim());
            page.put("re_url", temp.getString("ChongUrl").replaceAll("\t", "").trim());
			JSONObject force = getForce(doc);
			
//          long s3 = System.nanoTime();
//          System.out.println("Jsoup.parse: "+ (s2-s1)/1000000000.0 +"s");
//          System.out.println("Process: "+ (s3-s2)/1000000000.0 +" s");
			
//			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
//			String aString = bufferedReader.readLine();
	        
			count += 1;
// 			System.out.println(page);
			
			if (count % 500000 == 0) {
				System.out.println("___" + count);
			}
			
			if (force.length() != 0) {
				if (exist_set.contains(page.getString("url")) == false && force.getString("title").equals("") == false && force.getJSONArray("array").length() > 0) {
					exist_set.add(page.getString("url"));
					bufferedWriter_force.write(page.getString("url") + "\t\t" + force.getString("title") + "\t\t" + StringUtils.join(force.getJSONArray("array"), "::;")  + "\n");
				}
			} else {
				System.out.println(page);
			}
        }
        bufferedReaderRaw.close();
        bufferedWriter_force.close();
        System.out.println("__Total: " + count);
    }
    // 20180709以后[新]的版本
    public void newProcessing(String input_filename, String output_dir) throws Exception {
    	System.out.println("Processing file: " + input_filename);
        BufferedReader bufferedReaderRaw = new BufferedReader(new FileReader(new File(input_filename)));
        
        BufferedWriter bufferedWriter_h1h2url = new BufferedWriter(new FileWriter(new File(output_dir + "bd_h1h2url.txt")));
        BufferedWriter bufferedWriter_abs = new BufferedWriter(new FileWriter(new File(output_dir + "bd_abstract.txt")));
        BufferedWriter bufferedWriter_art = new BufferedWriter(new FileWriter(new File(output_dir + "bd_article.txt")));
        BufferedWriter bufferedWriter_cat = new BufferedWriter(new FileWriter(new File(output_dir + "bd_category.txt")));
        BufferedWriter bufferedWriter_inf = new BufferedWriter(new FileWriter(new File(output_dir + "bd_infobox.txt")));
        BufferedWriter bufferedWriter_out = new BufferedWriter(new FileWriter(new File(output_dir + "bd_outline.txt")));
        
        BufferedWriter bufferedWriter_stat = new BufferedWriter(new FileWriter(new File(output_dir + "bd_stat.txt")));
        
        int count = 0;
        String line = new String();
        while (true) {
            line = bufferedReaderRaw.readLine();
//            System.out.println(line);
            if (line == null) {
                break;
            }
            
            JSONObject temp = new JSONObject(line);
            String rawHtml = temp.get("html").toString();
//            String rawHtml = line;
            
            JSONObject page = new JSONObject();
//          long s1 = System.nanoTime();
//            Document doc = Jsoup.parse(rawHtml, "GB18030");
            Document doc = Jsoup.parse(rawHtml, "UTF-8");
//          long s2 = System.nanoTime();
            page.put("url", temp.getString("url").replaceAll("\t", ""));
            page.put("re_url", temp.getString("ChongUrl").replaceAll("\t", ""));
//            page.put("url", "test");
//            page.put("re_url", "test");
            
			page.put("infobox", getInfobox(doc));
			page.put("outline", getOutline(doc));
			JSONObject titles = getTitle(doc, "");
			page.put("title", titles);
//			page.put("synonym", getSynonym(doc));
//			page.put("polyseme", getPolyseme(doc));
			page.put("description", getDescription(doc));
			page.put("content", getContent(doc));
			page.put("tags", getTags(doc));
			
			
            page.put("statistics", getStatistics(doc));
//          long s3 = System.nanoTime();
//          System.out.println("Jsoup.parse: "+ (s2-s1)/1000000000.0 +"s");
//          System.out.println("Process: "+ (s3-s2)/1000000000.0 +" s");
			
			count += 1;
// 			System.out.println(page);
			
			if (count % 500000 == 0) {
				System.out.println("___" + count);
			}
			
			if (titles.has("h1")) {

				
				bufferedWriter_h1h2url.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + page.getString("re_url") + "\n");
				
				if (!page.getString("description").equals("")) {
					bufferedWriter_abs.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + "AbstractHere::;" + page.getString("description") + "\n");
				} else {
					bufferedWriter_abs.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + "\n");
				}
				
				if (!page.getString("content").equals("")) {
					bufferedWriter_art.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + "ArticleHere::;" + page.getString("content") + "\n");
				} else {
					bufferedWriter_art.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + "\n");
				}
				
				if (page.getJSONArray("tags").length() != 0) {
					bufferedWriter_cat.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + StringUtils.join(page.getJSONArray("tags"), "::;") + "\n");
				} else {
					bufferedWriter_cat.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + "\n");
				}
				
				if (page.getJSONObject("infobox").length() != 0) {
					bufferedWriter_inf.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + page.getJSONObject("infobox").toString() + "\n");
				} else {
					bufferedWriter_inf.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + "\n");
				}
				
				if (!page.getString("outline").equals("")) {
					bufferedWriter_out.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + page.getString("outline") + "\n");
				} else {
					bufferedWriter_out.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + "\n");
				}
				
				
				if (!page.getJSONObject("statistics").toString().equals("")) {
					bufferedWriter_stat.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + page.getJSONObject("statistics").toString() + "\n");
				} else {
					bufferedWriter_stat.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + "\n");
				}
			} else {
				System.out.println(page);
			}
//	        bufferedWriter_syn.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + page.getString("synonym") + "\n");
//	        bufferedWriter_pol.write(titles.getString("h1") + "\t\t" + titles.getString("h2") + "\t\t" + page.getString("url") + "\t\t" + page.getString("polyseme") + "\n");

// 			break;

//			if (rawHtml.equals("<html><head></head><body></body></html>\n") || rawHtml.equals("") || page.getJSONObject("title").length()==0) {
////				bufferedWriterProblem.write(temp.toString() + "\n");
////				bufferedWriterUrl.write(page.getString("url") + "\n");
//			} else {
//				bufferedWriter.write(page.toString() + "\n");
//			}
        }
        bufferedReaderRaw.close();
        
        bufferedWriter_h1h2url.close();
        bufferedWriter_abs.close();
        bufferedWriter_art.close();
        bufferedWriter_cat.close();
        bufferedWriter_inf.close();
        bufferedWriter_out.close();
        bufferedWriter_stat.close();
        
        System.out.println("__Total: " + count);
    }
    
    // 20180709以前能跑的版本
    public void processing(String filename) throws Exception {
    	System.out.println("Processing file: " + filename);
        BufferedReader bufferedReaderRaw = new BufferedReader(new FileReader(new File(sourceDir + filename)));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(resultDir + filename.replace(".", ".result."))));
//        BufferedWriter bufferedWriterProblem = new BufferedWriter(new FileWriter(new File(resultDir + "/" + filename.replace(".", ".problem."))));
//        BufferedWriter bufferedWriterUrl = new BufferedWriter(new FileWriter(new File(resultDir + "/" + filename.replace(".", ".url."))));
        int count = 0;        
        String line = new String();
        List<String> list = new ArrayList<String>();
        while (true) {
            line = bufferedReaderRaw.readLine();
//            System.out.println(line);
            if (line == null) {
                break;
            }
            if (line.equals("")) {
                count += 1;
                JSONObject temp = new JSONObject(StringUtils.join(list, ""));
                JSONObject page = new JSONObject();
                String rawHtml = temp.get("html").toString();
//                long s1 = System.nanoTime();
                Document doc = Jsoup.parse(rawHtml, "UTF-8");
//                long s2 = System.nanoTime();
                page.put("url", temp.getString("url").replaceAll("\t", ""));   
                page.put("infobox", getInfobox(doc));
                page.put("outline", getOutline(doc));
                page.put("title", getTitle(doc, temp.getString("content_title")));
                page.put("synonym", getSynonym(doc));
                page.put("polyseme", getPolyseme(doc));
                page.put("description", getDescription(doc));
                page.put("content", getContent(doc));
                page.put("images", getImages(doc));
                page.put("tags", getTags(doc));
                page.put("links", getLinks());
                page.put("references", getReferences(doc));
                page.put("statistics", getStatistics(doc));
//                long s3 = System.nanoTime();
//                System.out.println("Jsoup.parse: "+ (s2-s1)/1000000000.0 +"s");
//                System.out.println("Process: "+ (s3-s2)/1000000000.0 +" s");
                list.clear();
//                System.out.println(page.toString());
                
                if (rawHtml.equals("<html><head></head><body></body></html>\n") || rawHtml.equals("") || page.getJSONObject("title").length()==0) {
//                    bufferedWriterProblem.write(temp.toString() + "\n");
//                    bufferedWriterUrl.write(page.getString("url") + "\n");
                } else {
                    bufferedWriter.write(page.toString() + "\n");
                }
            } else {
                list.add(line);
            }
        }
        bufferedReaderRaw.close();
        bufferedWriter.close();
//        bufferedWriterProblem.close();
//        bufferedWriterUrl.close();
        System.out.println("__Total: " + count);
    }

    public void testProcessing(String filename) throws Exception {
    	System.out.println("Processing file: " + filename);
        BufferedReader bufferedReaderRaw = new BufferedReader(new FileReader(new File(sourceDir + filename)));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(resultDir + filename.replace(".", ".result."))));
//        BufferedWriter bufferedWriterProblem = new BufferedWriter(new FileWriter(new File(resultDir + "/" + filename.replace(".", ".problem."))));
//        BufferedWriter bufferedWriterUrl = new BufferedWriter(new FileWriter(new File(resultDir + "/" + filename.replace(".", ".url."))));
        String line = new String();
        List<String> list = new ArrayList<String>();
        while (true) {
            line = bufferedReaderRaw.readLine();
//            System.out.println(line);
            if (line == null) {
                JSONObject page = new JSONObject();
                String rawHtml = StringUtils.join(list, "");
//                long s1 = System.nanoTime();
                Document doc = Jsoup.parse(rawHtml, "UTF-8");
//                long s2 = System.nanoTime();  
                page.put("infobox", getInfobox(doc));
                page.put("outline", getOutline(doc));
                page.put("title", getTitle(doc, ""));
                page.put("synonym", getSynonym(doc));
                page.put("polyseme", getPolyseme(doc));
                page.put("description", getDescription(doc));
                page.put("content", getContent(doc));
                page.put("images", getImages(doc));
                page.put("tags", getTags(doc));
                page.put("links", getLinks());
                page.put("references", getReferences(doc));
                page.put("statistics", getStatistics(doc));
//                long s3 = System.nanoTime();
//                System.out.println("Jsoup.parse: "+ (s2-s1)/1000000000.0 +"s");
//                System.out.println("Process: "+ (s3-s2)/1000000000.0 +" s");
                list.clear();
                System.out.println(page.toString());
                
                if (rawHtml.equals("<html><head></head><body></body></html>\n") || rawHtml.equals("") || page.getJSONObject("title").length()==0) {
//                    bufferedWriterProblem.write(temp.toString() + "\n");
//                    bufferedWriterUrl.write(page.getString("url") + "\n");
                } else {
                    bufferedWriter.write(page.toString() + "\n");
                }
                break;
            } else {
                list.add(line);
            }
        }
        bufferedReaderRaw.close();
        bufferedWriter.close();
//        bufferedWriterProblem.close();
//        bufferedWriterUrl.close();
    }
    
    public String clearString(String str) {
        str = str.replaceAll("\u00A0", "").replaceAll("\n", "").replaceAll("&nbsp;", "").replaceAll("&nbsp", "").trim();
        str = str.replaceAll("\\<.*?>", ""); 
        return str.trim();
    }

 // 20180709以后[新]的版本
    public JSONObject getForce(Document doc) {
    	JSONObject force = new JSONObject();
    	try {
	    	Elements valBlocks = doc.select("div[class*=main-content]");
	    	if (valBlocks.size() == 1) {
		    	String h1 = valBlocks.get(0).getElementsByTag("h1").text().trim();
		    	Elements paras = valBlocks.get(0).getElementsByClass("para");
		    	JSONArray array = new JSONArray();
		    	for (Element p : paras) {
		    		if (p.getElementsByTag("a").size() == 0 || p.getElementsByTag("a").get(0).hasAttr("href") == false) {
		    			continue;
		    		}
		    		array.put(p.getElementsByTag("a").get(0).attr("href").trim() + ":::" + p.text().trim());
		    	}
		    	if (h1.equals("") == false) {
		    		force.put("title", h1);
		    		force.put("array", array);
		    	}
	    	}
    	} catch (Exception e) {
    		System.out.println("err" + doc);
            return force;
        }
    	return force;
	}
    
    public String getSynonym(Document doc) {
        JSONObject synonym = new JSONObject();
        String synonym_str = ""; 
        try {
            Elements spansV = doc.select("span[class=view-tip-panel]");
            if (spansV.size() == 1) {
//                System.out.println("__ _" + spansV);
                if (spansV.size() != 0) {
                    ArrayList<String> aList = new ArrayList<String>();
                    for (Node node : spansV.get(0).childNodes()) {
                        if (node.nodeName().equals("#text")) {
                            aList.add(node.toString());
                        } else if (node.nodeName().equals("a")) {
                            continue;
                        } else if (node.nodeName().equals("span")) {
                            aList.add(((Element)node).text());
                        }
                    }
                    String ss[] = StringUtil.join(aList, "").split("一般指");
                    synonym.put("from", clearString(ss[0])); // ref
                    synonym.put("to", clearString(ss[1])); // main
                    
                    synonym_str = clearString(ss[0]);
                }
            }
//            System.out.println(synonym);
            return synonym_str;
        } catch (Exception e) {
            return synonym_str;
        }
    }
    
    public String getPolyseme(Document doc) {
    	JSONArray polyseme = new JSONArray();
    	ArrayList<String> polyseme_str = new ArrayList<String>();
    	
        try {
            Elements divsV = doc.select("div[class=polysemeBody]");
            if (divsV.size() == 1) {
//                System.out.println("__ _" + divsV);
                for (Element li : divsV.get(0).getElementsByTag("li")) {
                	polyseme.put(clearString(li.text().substring(1)));                    
                }
            } else {
            	divsV = doc.select("div[class*=polysemant-list]");
            	if (divsV.size() == 1) {
//                  System.out.println("__ _" + divsV);
                    for (Element li : divsV.get(0).getElementsByTag("li")) {
                    	polyseme.put(clearString(li.text().substring(1)));
                    	polyseme_str.add(clearString(li.text().substring(1)));
                    }
            	}
            }
//            System.out.println(polyseme);
            return StringUtils.join(polyseme_str, "::;");
        } catch (Exception e) {
            return StringUtils.join(polyseme_str, "::;");
        }
    }
    
    public JSONObject getTitle(Document doc, String temp_title) {
    	JSONObject title = new JSONObject();
        try {
            for (String key : titleTemplate.keySet()) {
                Elements valBlocks = doc.select(titleTemplate.get(key).get("valType").toString() + "[class*=" + titleTemplate.get(key).get("valClass").toString() + "]");
//                System.out.println("__ _" + valBlocks);
                if (valBlocks.size() >= 1) {
                    if (key.equals("lemmaTitleH1_span") || key.equals("lemmaTitleH1_div")) {
                    	String temp_str = valBlocks.text();
                    	if (temp_str.equals("")==false) {
//                    		System.out.println("__ _>>>" + valBlocks.get(0).getElementsByTag("span").size() + "+ +++");
                    		if (valBlocks.get(0).getElementsByTag("span").size()>=1 && temp_str.indexOf("（")!=-1) {
	                            title.put("h1", temp_str.substring(0, temp_str.indexOf("（")));
	                            title.put("h2", temp_str.substring(temp_str.indexOf("（")));
                    		} else {
                    			title.put("h1", temp_str);
                    			title.put("h2", "");
                    		}
                    	}
                    } else { // lemmaWgt-lemmaTitle-title // lemmaTitleBox // clearfix // dt_title
                    	title.put("h1", valBlocks.get(0).getElementsByTag("h1").text());
                    	title.put("h2", valBlocks.get(0).getElementsByTag("h2").text());
                    }
                    if (title.getString("h1").equals("")) {
                    	title.put("h1", temp_title);
                    }
                    break;
                }
            }
            return title;
        } catch (Exception e) {
            return title;
        }
    }

    public String getDescription(Document doc) {
        String description = new String();
        try {
            for (String key : descriptionTemplate.keySet()) {
                Elements valBlocks = doc.select(descriptionTemplate.get(key).get("valType").toString() + "[class*="
                        + descriptionTemplate.get(key).get("valClass").toString() + "]");
                if (valBlocks.size() >= 1) {
                    List<String> desList = new ArrayList<String>();
                    List<String> list = new ArrayList<String>();
                    for (Node ele : valBlocks.get(0).childNodes()) {
//                        System.out.println("__ _" + ele.nodeName());
                        boolean last = false;
                        if (ele.nodeName().equals("div")) {
                            for (Element para : valBlocks.get(0).children()) {
                                for (Node node : para.childNodes()) {
//                                    System.out.println("__ _" + node.nodeName());
                                    if (node.nodeName().equals("a")) {
                                        if (last == false && clearString(node.childNodes().get(0).toString()).equals("")==false && node.attr("href").equals("")==false) {
                                            list.add("[[");
                                            list.add(clearString(node.childNodes().get(0).toString()));
                                            list.add("|");
                                            list.add(node.attr("href"));
                                            list.add("]]");
                                            linksSet.add("[["+clearString(node.childNodes().get(0).toString())+"|"+node.attr("href")+"]]");
                                        }
                                    } else if (node.nodeName().equals("sup")) {
                                        last = true;
                                    } else if (node.nodeName().equals("b")) {
	                                    if (node.childNodeSize() > 0) {
                                    		list.add(node.childNode(0).toString());
                                    	}
                                        last = false;
                                    } else if (node.nodeName().equals("#text")) {
                                        list.add(node.toString());
                                        last = false;
                                    }
                                }
                                desList.add(clearString(StringUtils.join(list, "")));
                                list.clear();
                            }
                            break;
                        } else if (ele.nodeName().equals("a")) {
                            if (last == false && clearString(ele.childNodes().get(0).toString()).equals("")==false && ele.attr("href").equals("")==false) {
                                list.add("[[");
                                list.add(clearString(ele.childNodes().get(0).toString()));
                                list.add("|");
                                list.add(ele.attr("href"));
                                list.add("]]");
                                linksSet.add("[["+clearString(ele.childNodes().get(0).toString())+"|"+ele.attr("href")+"]]");
                            }
                        } else if (ele.nodeName().equals("sup")) {
                            last = true;
                        } else if (ele.nodeName().equals("b")) {
                            list.add(ele.childNode(0).toString());
                            last = false;
                        } else if (ele.nodeName().equals("#text")) {
                            list.add(ele.toString());
                            last = false;
                        }
                        desList.add(clearString(StringUtils.join(list, "")));
                        list.clear();
                    }
                    ArrayList<String> non_null_desList = new ArrayList<String>();
                    for (String s : desList) {
                    	if (!s.equals("")) {
                    		non_null_desList.add(s);
                    	}
                    }
                    description = StringUtils.join(non_null_desList, "::;");
                    break;
                }
            }
            return description;
        } catch (Exception e) {
            return description;
        }
    }

    public String getContent(Document doc) {
        List<String> text_contentList = new ArrayList<String>();
        try {
            Elements valBlocks = doc.select("div[class*=lemma-main-content]");
//            System.out.println("__ _" + valBlocks);
            if (valBlocks.size() > 0) {
//                System.out.println("__ _" + valBlocks.get(0));
                for (Element element : valBlocks.get(0).children()) {
//                    System.out.println("__ _" + element.tagName());
                    if (element.tagName().equals("div") && element.attr("class").equals("para")) {
                        List<String> list = new ArrayList<String>();
                        boolean last = false;
                        for (Node node : element.childNodes()) {
//                            System.out.println("__ _" + node.nodeName());
                            if (node.nodeName().equals("a") && node.hasAttr("class") == false) {
                                if (last == false && node.childNodes().get(0).toString().equals("")==false && node.attr("href").equals("")==false) {
                                    list.add("[[");
                                    list.add(node.childNodes().get(0).toString());
                                    list.add("|");
                                    list.add(node.attr("href"));
                                    list.add("]]");
                                    linksSet.add("[["+clearString(node.childNodes().get(0).toString())+"|"+node.attr("href")+"]]");
                                }
                            } else if (node.nodeName().equals("sup")) {
                                last = true;
                            } else if (node.nodeName().equals("b")) {
                                list.add(node.childNode(0).toString());
                                last = false;
                            } else if (node.nodeName().equals("#text")) {
                                list.add(node.toString());
                                last = false;
                            }
                        }
                        String para = StringUtils.join(list, "").replaceAll("\n", "").trim();
                        if (para.equals("") == false) {
                            text_contentList.add(para);
                        }
                    } else if (element.tagName().equals("h2") && element.attr("class").equals("headline-1")) {
//                        System.out.println("__ _" + element.text());
                        for (Element node : element.children()) {
                            if (node.tagName().equals("span") && node.attr("class").equals("headline-content")) {
                                text_contentList.add("== " + node.text().trim() + " ==");
                            }
                        }
                    } else if (element.tagName().equals("h3") && element.attr("class").equals("headline-2")) {
                      for (Element node : element.children()) {
                          if (node.tagName().equals("span") && node.attr("class").equals("headline-content")) {
                              text_contentList.add("=== " + node.text().trim() + " ===");
                          }
                      }
                  }
                }
            } else {
                valBlocks = doc.select("div[class*=main_tab main_tab-defaultTab]");
//                System.out.println("__ _" + valBlocks);
                if (valBlocks.size() == 0) {
                    valBlocks = doc.select("div[class=main-content]");
                }
                if (valBlocks.size() > 0) {
                    for (Element element : valBlocks.get(0).children()) {
//                        System.out.println("__ _" + element.tagName());
                        if (element.tagName().equals("div") && element.attr("class").equals("para")) {
                            List<String> list = new ArrayList<String>();
                            boolean last = false;
                            for (Node node : element.childNodes()) {
//                                System.out.println("__ _" + node.nodeName());
                                if (node.nodeName().equals("a") && node.hasAttr("class") == false) {
                                    if (last == false && clearString(node.childNodes().get(0).toString()).equals("")==false && node.attr("href").equals("")==false) {
                                        list.add("[[");
                                        list.add(clearString(node.childNodes().get(0).toString()));
                                        list.add("|");
                                        list.add(node.attr("href"));
                                        list.add("]]");
                                        linksSet.add("[["+clearString(node.childNodes().get(0).toString())+"|"+node.attr("href")+"]]");
                                    }
                                } else if (node.nodeName().equals("sup")) {
                                    last = true;
                                } else if (node.nodeName().equals("b")) {
                                    list.add(node.childNode(0).toString());
                                    last = false;
                                } else if (node.nodeName().equals("#text")) {
                                    list.add(node.toString());
                                    last = false;
                                }
                            }
                            
                            
                            String para = StringUtils.join(list, "").replaceAll("\n", "").trim();
                            if (para.equals("") == false) {
                                text_contentList.add(para);
                            }
                        } else if (element.tagName().equals("div")
                                && element.attr("class").equals("para-title level-2")) {
                            for (Node node : element.getElementsByTag("h2").get(0).childNodes()) {
                                if (node.nodeName().equals("#text")) {
                                	text_contentList.add("== " + node.toString().trim() + " ==");
                                }
                            }
                        } else if (element.tagName().equals("div")
                                && element.attr("class").equals("para-title level-3")) {
                            for (Node node : element.getElementsByTag("h3").get(0).childNodes()) {
                                if (node.nodeName().equals("#text")) {
                                	text_contentList.add("=== " + node.toString().trim() + " ===");
                                }
                            }
                        }
                    }
                }
            }
            
            ArrayList<String> non_null_desList = new ArrayList<String>();
            for (String s : text_contentList) {
            	if (!s.equals("")) {
            		non_null_desList.add(s);
            	}
            }
            return StringUtils.join(non_null_desList, "::;");
        } catch (Exception e) {
            return StringUtils.join(text_contentList, "::;");
        }
    }

    public JSONArray getLinks() {
        JSONArray inner_link = new JSONArray();
        linksSet.remove("[[|]]");
        for (String key : linksSet) {
            inner_link.put(key);
        }
        linksSet.clear();
//      System.out.println(inner_link);
        return inner_link;
    }

    public JSONArray getReferences(Document doc) {
        JSONArray references = new JSONArray();
        try {
            for (String key : referencesTemplate.keySet()) {
                Elements valBlocks = doc.select(referencesTemplate.get(key).get("valType").toString() + "[class=" + referencesTemplate.get(key).get("valClass").toString() + "]");
                if (valBlocks.size() > 0) {
                    // System.out.println(valBlocks);
                    for (Element li : valBlocks) {
                        for (Node node : li.childNodes()) {
                            if (node.nodeName().equals("a") && node.attr("rel").equals("nofollow")) {
                            	references.put("[[" + clearString(node.childNode(0).toString()) + "|" + node.attr("href") + "]]");
                            }
                        }
                    }
                    break;
                }
            }
            return references;
        } catch (Exception e) {
            return references;
        }
    }
    
    public JSONArray getImages(Document doc) {
        JSONArray images = new JSONArray();
        try {
            Elements summaryPicBlock = doc.select("div[class=summary-pic]");
            if (summaryPicBlock.size() > 0) {
            	images.put("[[" + clearString(summaryPicBlock.get(0).text()) + "|"
                        + summaryPicBlock.get(0).select("img").get(0).attr("src") + "]]");
            }
            for (String key : imagesTemplate.keySet()) {
                Elements valBlocks = doc.select(imagesTemplate.get(key).get("valType").toString() + "[class=" + imagesTemplate.get(key).get("valClass").toString() + "]");
                if (valBlocks.size() > 0) {
                    Elements imageBox = valBlocks.get(0).select("img[class]");
                    // System.out.println(imageBox);
                    for (Element element : imageBox) {
                    	images.put("[[" + clearString(element.attr("alt")) + "|" + element.attr("src") + "]]");
                    }
                    break;
                }
            }
            return images;
        } catch (Exception e) {
            return images;
        }
    }
    
    public JSONArray getTags(Document doc) {
        JSONArray tags = new JSONArray();
        try {
            for (String key : tagsTemplate.keySet()) {
                Elements valBlocks = doc.select(tagsTemplate.get(key).get("valType").toString() + "[class="
                        + tagsTemplate.get(key).get("valClass").toString() + "]");
                if (valBlocks.size() > 0) {
                    for (Element span : valBlocks) {
                        for (Node node : span.childNodes()) {
                            if (node.nodeName().equals("#text")) {
                                tags.put(clearString(node.toString()));
                            } else if (node.nodeName().equals("a")) {
                                tags.put("[[" + clearString(span.child(0).text()) + "|" + node.attr("href") + "]]");
                            }
                        }
                    }
                    break;
                }
            }
            return tags;
        } catch (Exception e) {
            return tags;
        }
    }

    public JSONObject getInfobox(Document doc) {
        JSONObject infobox = new JSONObject();
        try {
            for (String key : infoboxTemplate.keySet()) {
                Elements keyBlocks = doc.select(infoboxTemplate.get(key).get("keyType").toString() + "[class*="
                        + infoboxTemplate.get(key).get("keyClass").toString() + "]");
                Elements valBlocks = doc.select(infoboxTemplate.get(key).get("valType").toString() + "[class*="
                        + infoboxTemplate.get(key).get("valClass").toString() + "]");

                if (keyBlocks.size() == valBlocks.size() && keyBlocks.size() != 0) {
                    for (int i = 0; i < keyBlocks.size(); i += 1) {
                        List<String> list = new ArrayList<String>();

                        for (Node node : valBlocks.get(i).childNodes()) {
                            if (node.nodeName().equals("a") && clearString(node.childNodes().get(0).toString()).equals("")==false && node.attr("href").equals("")==false) {
                                list.add("[[");
                                list.add(clearString(node.childNodes().get(0).toString()));
                                list.add("|");
                                list.add(node.attr("href"));
                                list.add("]]");
                                linksSet.add("[["+clearString(node.childNodes().get(0).toString())+"|"+node.attr("href")+"]]");
                            } else if (node.nodeName().equals("br")) {
                            	list.add("::;");
                            } else if (node.nodeName().equals("sup")){
                            	continue;
                            } else if (node.nodeName().equals("a") && node.hasAttr("href") == false) {
                            	continue;
                            } else {
                                list.add(node.toString());
                                list.add("");
                            }
                        }
                        infobox.put(clearString(keyBlocks.get(i).text()), clearString(StringUtils.join(list, "")));
                    }
                    break;
                }
            }
            return infobox;
        } catch (Exception e) {
            return infobox;
        }
    }

    public String getOutline(Document doc) {
        JSONObject outline = new JSONObject();
        ArrayList<String> outline_str = new ArrayList<String>();
        try {
            for (String key : outlineTemplate.keySet()) {
                Elements valBlocks = doc.select(outlineTemplate.get(key).get("valType").toString() + "["
                        + (key == "lemma-catalog" ? "class" : "catalog") + "^="
                        + outlineTemplate.get(key).get("valClass").toString() + "]");
                String last = new String();
                if (valBlocks.size() != 0) {
                    for (Element element : valBlocks) {
                        String order = (key == "lemma-catalog"
                                ? element.children().get(1).children().attr("href").substring(1)
                                : element.attr("href").substring(1));
                        String val = (key == "lemma-catalog" ? element.children().get(1).text() : element.text());
                        val = clearString(val);

                        if (order.indexOf('_') == -1) {
                            outline.put(order + "~" + val, new JSONObject());
                            last = order + "~" + val;
                            
                            outline_str.add(order.replace("#", "").replace("_", ".") + "#" + val);
                        } else {
                            outline.getJSONObject(last).put(order + "~" + val, new JSONObject());
                            
                            outline_str.add(order.replace("#", "").replace("_", ".") + "#" + val);
                        }
                    }
                    break;
                }
            }
            return StringUtils.join(outline_str, "::;");
        } catch (Exception e) {
            return StringUtils.join(outline_str, "::;");
        }
    }

    public JSONObject getStatistics(Document doc) {
    	JSONObject statistics = new JSONObject();
    	try {
            Elements lisV = doc.select("[class*=side-box lemma-statistics]");
            if (lisV.size()==1) {
            	Elements lis = lisV.get(0).select("li");
        		if (lis.size()==4) {
//        			System.out.println(lis.get(0).text());
//        			System.out.println(lis.get(0).text().replaceAll("[^(0-9)]", ""));
        			
        			statistics.put("pv", lis.get(0).text().replaceAll("[^(0-9)]", ""));
        			statistics.put("edit_times", lis.get(1).text().replaceAll("[^(0-9)]", ""));
        			statistics.put("last_modified", lis.get(2).text().substring(lis.get(2).text().indexOf("：")+1));
        			statistics.put("creator", lis.get(3).text().substring(lis.get(3).text().indexOf("：")+1));
        		}
            } else {
            	Elements divsV = doc.select("[class*=side-box side-box-extend]");
            	if (divsV.size()==1) {
            		Elements divs = divsV.get(0).select("[class=side-list-item]");
            		if (divs.size()==4) {
//            			System.out.println(divs.get(0).text());
//            			System.out.println(divs.get(0).text().replaceAll("[^(0-9)]", ""));
            			
            			statistics.put("pv", divs.get(0).text().replaceAll("[^(0-9)]", ""));
            			statistics.put("edit_times", divs.get(1).text().replaceAll("[^(0-9)]", ""));
            			statistics.put("last_modified", divs.get(2).text().substring(divs.get(2).text().indexOf("：")+1));
            			statistics.put("creator", divs.get(3).text().substring(divs.get(3).text().indexOf("：")+1));
            		}
            	}
            }
            return statistics;
        } catch (Exception e) {
            return statistics;
        }
	}
}
