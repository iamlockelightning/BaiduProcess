# BaiduProcess

这是一个百度百科页面抽取程序，可以从HTML源码中抽取出页面：标题、摘要、信息框、正文、目录结构、标签等信息。

## 项目结构

```shell
BaiduProcess
├── build.xml	// 可以使用ant命令编译执行
├── lib			// 项目中使用到的工具包
│   ├── commons-collections4-4.1.jar
│   ├── commons-lang3-3.4.jar
│   ├── curvesapi-1.04.jar
│   ├── json-20160810.jar
│   ├── jsoup-1.9.2.jar
│   ├── jython-standalone-2.7.0.jar
│   ├── poi-3.15.jar
│   ├── poi-examples-3.15.jar
│   ├── poi-excelant-3.15.jar
│   ├── poi-ooxml-3.15.jar
│   ├── poi-ooxml-schemas-3.15.jar
│   ├── poi-scratchpad-3.15.jar
│   └── xmlbeans-2.6.0.jar
├── other
│   └── Dereplication.py
└── src
    └── BaiduProcess
        ├── BDKG.java
        ├── BaiduExtractor.java		// 百度百科页面抽取
        ├── Dereplication.java
        ├── HudongExtractor.java
        ├── MultiThread.java
        └── SubTask.java
```

项目src目录下大部分代码都没有用，只需修改使用**BaiduExtractor.java**即。

## 项目使用步骤

1. 修改BaiduExtractor.java的输入输出文件位置：（例如）

   ```java
   public static String input_filename = "/home/xlore/BaiduBaike20180705/data_final_force_all.txt";
   public static String output_dir = "/home/xlore/BaiduBaike20180705/1_extraction/";
   ```

   输入文件：一行对应一个百度词条的JSON结构体，JSON结构体内"html"对应的数值为词条HTML源码，"url"为词条在百科中的实际URL。当前抽取程序仅需要JSON结构体中存有这两个键值对。

   输出目录：会分多个文件输出，文件名会指明其抽取的内容类型。每一行对应一个词条的抽取内容，格式一般为"title_h1 (\t\t) title_h2 (\t\t) url (\t\t) 内容"

2. 编译执行：

   ```shell
   ➜  BaiduProcess git:(master) ✗ant build
   ➜  BaiduProcess git:(master) ✗ant BaiduExtractor
   ```

   也可使用eclipse导入项目后编译执行

## BaiduExtractor.java说明

BaiduExtractor.java的主要思路，是使用Jsoup工具包对HTML解析提取出其DOM结构，然后根据写定的模版匹配不同的内容，进行抽取。

下面是BaiduExtractor.java主要的函数功能说明，可以根据不同的抽取需要修改代码。

```java
public static void main(String args[]);	// 主函数，程序入口
```

```java
public BaiduExtractor();	// 构造函数，定义了使用到的不同模块的模版
```

```java
public void new3Table(String input_filename, String output_dir);    // 生成同义词表、多义词表、mention表；经过main调用
```

```java
public void newProcessingDataFinalForce(String input_filename, String output_dir);    // 现在使用的抽取函数，里面包含了不同内容抽取的调用代码；经过main调用
```

**newProcessingDataFinalForce()**里除了决定抽取的内容，还可以修改输入文件格式。下面的两个函数就对应了不同的输入格式。

```java
public void newProcessing(String input_filename, String output_dir);	// 和newProcessingDataFinalForce类似；经过main调用
```

```java
public void processing(String filename);	// 和newProcessingDataFinalForce类似，经过main调用
```

下面就是各个模块的抽取代码实现

```java
public JSONObject getForce(Document doc);	// 抽取消歧页面的内容
public String getSynonym(Document doc);	// 抽取同义词
public String getPolyseme(Document doc);	// 抽取页面可能有的多义词（标题下方）
public JSONObject getTitle(Document doc, String temp_title);	// 抽取标题（一级、二级）
public String getDescription(Document doc);	// 抽取摘要（infobox以上的文本）
public String getContent(Document doc);	// 抽取正文（infobox以下的文本）
public JSONArray getLinks();	// 抽取文本内的所有锚文本
public JSONArray getReferences(Document doc);	// 抽取文末的外部引用链接
public JSONArray getImages(Document doc);	// 抽取所有图片的url
public JSONArray getTags(Document doc);	// 抽取文本的标签
public JSONObject getInfobox(Document doc);	// 抽取信息框，属性、属性值
public String getOutline(Document doc);	// 抽取页面目录大纲
public JSONObject getStatistics(Document doc);	// 抽取页面的统计信息（编辑次数、创建时间等）
```

