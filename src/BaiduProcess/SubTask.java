package BaiduProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SubTask {
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
        
        SubTask subTask = new SubTask();
        subTask.testEXCLE();
        
        System.out.println("Done! :D");
    }
    
    public void getPersonPage() throws Exception {
    	String personListFile = "/home/peter/BaiduBaikeDataProcess/12zhengxie.txt";
    	String sourceDir = "/home/peter/BaiduBaikeDataProcess/nResult/key.nres.json";
        String resultFile = "/home/peter/BaiduBaikeDataProcess/12ZhengXiePages.json";
        String resultFileFilter = "/home/peter/BaiduBaikeDataProcess/12ZhengXiePagesFilter.json";
        
        BufferedReader bufferedReaderList = new BufferedReader(new FileReader(new File(personListFile)));
        BufferedReader bufferedReaderDB = new BufferedReader(new FileReader(new File(sourceDir)));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(resultFile)));
        BufferedWriter bufferedWriterFilter = new BufferedWriter(new FileWriter(new File(resultFileFilter)));
        
        List<String> candidateJson = new ArrayList<String>();
        List<String> candidateFilterJson = new ArrayList<String>();
        
        List<String> personList = new ArrayList<String>();
        Set<String> personSet = new HashSet<String>();
        while (true) {
        	String line = bufferedReaderList.readLine();
        	if (line == null || line == "") {
        		break;
        	}
        	if (line.contains("（") && line.contains("）")) {
        		line = line.substring(0, line.indexOf("（"));
        	}
            line = line.replaceAll("\n", "");
        	personList.add(line);
            personSet.add(line);
        }
        
        while (true) {
        	String line = bufferedReaderDB.readLine();
        	if (line == null || line == "") {
        		break;
        	}
            JSONObject page = new JSONObject(line);
        	String page_title = page.getString("content_title");
        	if (page_title.contains("（")) {
        		page_title = page_title.substring(0, page_title.indexOf("（"));
        	}
        	if (page_title.contains("(")) {
        		page_title = page_title.substring(0, page_title.indexOf("("));
        	}
        	if (personList.contains(page_title)) {
        		candidateJson.add(page.toString());
        		// TODO
                if (personSet.contains(page_title)) {
                    personSet.remove(page_title);
                }
                if (page.getString("text_content").contains("政协")) {
                	candidateFilterJson.add(page.toString());
                }
        	}
        }
        Set<String> temp = new HashSet<String>(personList);
        System.out.println("Name matched pages number:" + candidateJson.size() + "/(" + personSet.size() + "),\t" + temp.size());
        System.out.println("Filter matched pages number:" + candidateFilterJson.size() + "/(" + personSet.size() + "),\t");
        
        for (String ss : candidateJson) {
        	bufferedWriter.write(ss + "\n");
        }

        for (String ss : candidateFilterJson) {
        	bufferedWriterFilter.write(ss + "\n");        	
        }
        
        bufferedReaderList.close();
        bufferedReaderDB.close();
        bufferedWriter.close();
        bufferedWriterFilter.close();
    }
    
    public void splitJson() throws Exception {
    	String resultFile = "C:/Users/Locke/Desktop/12ZhengXiePagesFilter.json";
    	String url_name = "C:/Users/Locke/Desktop/12ZhengXiePagesFilter_url_name.json";
    	BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(resultFile)));
    	BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(url_name)));
    	while (true) {
        	String line = bufferedReader.readLine();
        	if (line == null || line == "") {
        		break;
        	}
            JSONObject page = new JSONObject(line);
        	String page_title = page.getString("content_title");
        	if (page_title.contains("（")) {
        		page_title = page_title.substring(0, page_title.indexOf("（"));
        	}
        	if (page_title.contains("(")) {
        		page_title = page_title.substring(0, page_title.indexOf("("));
        	}
        	bufferedWriter.write(page.getString("content_title") + "\t" + page.getString("url") + "\n");
        }
    	bufferedReader.close();
    	bufferedWriter.close();
    }
    
    public List<Integer> formatDate(String input) throws ParseException {
    	Pattern p = Pattern.compile("[^0-9]+");
    	Matcher m = p.matcher(input);
    	String s = m.replaceAll(" ").trim();
        String[] sArray = s.split(" ");
        List<Integer> iArray = new ArrayList<Integer>();
        for (String ss : sArray) {
        	if (iArray.size()==3) {
        		break;
        	}
        	if (ss.equals("")==false) {
        		iArray.add(Integer.parseInt(ss));
        	}
        }
        SimpleDateFormat sdf;
        String birthday;
        if (iArray.size() == 1) {
        	sdf = new SimpleDateFormat("yyyy");
        	birthday = iArray.get(0).toString();
        } else if (iArray.size() == 2) {
        	sdf = new SimpleDateFormat("yyyy-MM");
        	birthday = iArray.get(0).toString() + "-" + iArray.get(1).toString();
        } else if (iArray.size() == 3) {
        	sdf = new SimpleDateFormat("yyyy-MM-dd");
        	birthday = iArray.get(0).toString() + "-" + iArray.get(1).toString() + "-" + iArray.get(2).toString();
        } else {
        	return null;
        }
        Calendar born = Calendar.getInstance();
        born.setTime(sdf.parse(birthday));
    	Calendar now = Calendar.getInstance();
    	now.setTime(new Date());
    	int age = 0;
    	if (born.after(now)) {
    		throw new IllegalArgumentException("Can't be born in the future");
    	}
    	age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
    	if (now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR)) {
    		age -= 1;
    	}
    	iArray.add(age);
        return iArray;
    }
    
    public String clearLinks(String input) {
    	if (input.indexOf("[[") == -1) {
    		return input;
    	} else {
    		return clearLinks(input.substring(0, input.indexOf("[[")) + input.substring(input.indexOf("[[")+2, input.indexOf("||")) + input.substring(input.indexOf("]]")+2));
    	}
    }
    
    public void testEXCLE() throws Exception {
    	String resultFile = "C:/Users/Locke/Desktop/12ZhengXiePagesFilter.json";
    	String[] sheet_title = {"姓名", "url", "年龄", "性别", "民族", "出生日期", "出生地", "职业", "职务", "党派", "代表团", "学历", "学位", "毕业院校", "专业", "获得荣誉", "成就", "配偶", "照片", "界别", "政协委员", "人大代表", "description"};
    	List<String> sheet_title_List = Arrays.asList(sheet_title);
    	
    	JSONObject persons = new JSONObject();
    	ArrayList<String> personAttr = new ArrayList<String>();
    	XSSFWorkbook rwb = new XSSFWorkbook(new FileInputStream("C:/Users/Locke/Desktop/已分类-政协名单.xlsx"));
        Sheet readsheet = rwb.getSheetAt(0);
        int skip = 0;
        for (int i = 0; i < readsheet.getPhysicalNumberOfRows(); i += 1) {
        	if (skip >= 1) {
        		if (skip == 1) {
        			for (int j = 0; j < readsheet.getRow(i).getPhysicalNumberOfCells(); j += 1) {
        				personAttr.add(readsheet.getRow(i).getCell(j).toString());
            		}
//        			System.out.println(personAttr);
        			skip += 1;
        		} else {
        			JSONObject person = new JSONObject();
        			for (int j = 0; j < readsheet.getRow(i).getPhysicalNumberOfCells(); j += 1) {
            			person.put(personAttr.get(j), readsheet.getRow(i).getCell(j).toString());
            		}
//        			System.out.println(person);
        			persons.put(person.getString("姓名"), person);
        		}
        	} else {
        		skip += 1;
        	}
        }
        rwb.close();
//        System.out.println(persons);
        
        XSSFWorkbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("从infobox抽取");
        short line_num = 0;
        Row title_row = sheet.createRow(line_num);
        for (int i = 0; i < sheet_title.length; i += 1) {
        	title_row.createCell(i).setCellValue(sheet_title[i]);
        }

    	BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(resultFile)));
        while (true) {
        	String line = bufferedReader.readLine();
        	if (line == null || line == "") {
        		break;
        	}
            JSONObject page = new JSONObject(line);
            JSONObject infobox = page.getJSONObject("infobox");
            
            Row row = sheet.createRow(++line_num);
            for (int i = 0; i < sheet_title.length; i += 1) {
            	if (sheet_title[i].equals("姓名")) {
            		row.createCell(i).setCellValue(page.getString("content_title"));
            	} else if (sheet_title[i].equals("url")) {
            		row.createCell(i).setCellValue(clearLinks(page.getString("url")));
            	} else if (sheet_title[i].equals("description")) {
            		row.createCell(i).setCellValue(clearLinks(page.getString("description")));
            	} else if (infobox.has(sheet_title[i])) {
            		if (sheet_title[i].equals("出生日期")) {
            			List<Integer> iArray = formatDate(clearLinks(infobox.getString(sheet_title[i])));
                		if (iArray != null) {
    	                    if (iArray.size() == 2) {
    	                    	row.createCell(i).setCellValue(iArray.get(0) + "年");
    	                    	row.createCell(i-3).setCellValue(iArray.get(1));
    	                    } else if (iArray.size() == 3) {
    	                    	row.createCell(i).setCellValue(iArray.get(0) + "年" + iArray.get(1) + "月");
    	                 		row.createCell(i-3).setCellValue(iArray.get(2));
    	                    } else if (iArray.size() == 4) {
    	                    	row.createCell(i).setCellValue(iArray.get(0) + "年" + iArray.get(1) + "月" + iArray.get(2) + "日");
    	                        row.createCell(i-3).setCellValue(iArray.get(3));
    	                    }
                		}
            		} else if (sheet_title[i].equals("民族")) {
            			row.createCell(i).setCellValue(clearLinks(infobox.getString(sheet_title[i])) + ((clearLinks(infobox.getString(sheet_title[i])).indexOf("族")==-1)?"族":""));
            		} else {
            			row.createCell(i).setCellValue(clearLinks(infobox.getString(sheet_title[i])));
            		}
            	} else {
            	}
            	if ((i+1) == sheet_title.length) {
            		if (persons.has(row.getCell(0).toString())) {
            			JSONObject temp_person = persons.getJSONObject(row.getCell(0).toString());
            			for (String key : temp_person.keySet()) {
            				if (key.equals("")==false && temp_person.getString(key).equals("")==false) {
//            					System.out.println(temp_person);
//            					System.out.println(key);
//            					System.out.println(sheet_title_List.indexOf(key));
//            					System.out.println(temp_person.getString(key));
            					Cell cell = row.getCell((short)sheet_title_List.indexOf(key));
            					if (cell == null) {
            						row.createCell((short)sheet_title_List.indexOf(key)).setCellValue(temp_person.getString(key));
            					} else {
            						cell.setCellValue(temp_person.getString(key));
            					}
            				}
            			}
            		}
            	}
            }
        }
        
        bufferedReader.close();
        FileOutputStream fileOut = new FileOutputStream("C:/Users/Locke/Desktop/workbook.xlsx");
        wb.write(fileOut);
        wb.close();
        fileOut.close();
    }
    
}
